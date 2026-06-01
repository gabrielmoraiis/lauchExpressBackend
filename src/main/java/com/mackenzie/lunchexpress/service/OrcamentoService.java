package com.mackenzie.lunchexpress.service;

import com.mackenzie.lunchexpress.dto.request.OrcamentoRequest;
import com.mackenzie.lunchexpress.dto.response.OrcamentoResponse;
import com.mackenzie.lunchexpress.entity.Orcamento;
import com.mackenzie.lunchexpress.entity.Solicitacao;
import com.mackenzie.lunchexpress.entity.Usuario;
import com.mackenzie.lunchexpress.enums.StatusOrcamento;
import com.mackenzie.lunchexpress.enums.StatusSolicitacao;
import com.mackenzie.lunchexpress.exception.BadRequestException;
import com.mackenzie.lunchexpress.exception.ResourceNotFoundException;
import com.mackenzie.lunchexpress.repository.OrcamentoRepository;
import com.mackenzie.lunchexpress.repository.SolicitacaoRepository;
import com.mackenzie.lunchexpress.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrcamentoService {

    private final OrcamentoRepository orcamentoRepository;
    private final SolicitacaoRepository solicitacaoRepository;
    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;
    private final LogAtividadeService logService;

    @Transactional
    public OrcamentoResponse enviarOrcamento(Long solicitacaoId, OrcamentoRequest request, String tokenHeader) {
        String email = extrairEmail(tokenHeader);
        Usuario prestador = usuarioService.obterPorEmail(email);

        Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada: " + solicitacaoId));

        if (!solicitacao.isAvulso()) {
            throw new BadRequestException("Esta solicitação usa o catálogo de fornecedores e não aceita orçamentos avulsos");
        }

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE
                && solicitacao.getStatus() != StatusSolicitacao.EM_ORCAMENTO) {
            throw new BadRequestException("Esta solicitação não está aceitando propostas");
        }

        if (orcamentoRepository.existsBySolicitacaoIdAndPrestadorId(solicitacaoId, prestador.getId())) {
            throw new BadRequestException("Você já enviou uma proposta para esta solicitação");
        }

        Orcamento orcamento = new Orcamento();
        orcamento.setSolicitacao(solicitacao);
        orcamento.setPrestador(prestador);
        orcamento.setValorTotal(request.getValorTotal());
        orcamento.setObservacoes(request.getObservacoes());
        orcamento.setStatus(StatusOrcamento.PENDENTE);

        if (solicitacao.getStatus() == StatusSolicitacao.PENDENTE) {
            solicitacao.setStatus(StatusSolicitacao.EM_ORCAMENTO);
            solicitacaoRepository.save(solicitacao);
        }

        OrcamentoResponse resp = OrcamentoResponse.fromEntity(orcamentoRepository.save(orcamento));
        logService.registrar("ORCAMENTO_ENVIADO",
                "Fornecedor " + prestador.getNome() + " enviou proposta de R$ "
                + request.getValorTotal() + " para a solicitação #" + solicitacaoId,
                solicitacaoId, solicitacao.getCliente().getId(), prestador.getId());
        return resp;
    }

    @Transactional(readOnly = true)
    public List<OrcamentoResponse> listarPorSolicitacao(Long solicitacaoId) {
        return orcamentoRepository.findBySolicitacaoId(solicitacaoId)
                .stream()
                .map(OrcamentoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrcamentoResponse aprovarOrcamento(Long orcamentoId, String tokenHeader) {
        String email = extrairEmail(tokenHeader);

        Orcamento orcamento = orcamentoRepository.findById(orcamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Orçamento não encontrado: " + orcamentoId));

        Solicitacao solicitacao = orcamento.getSolicitacao();

        if (!solicitacao.getCliente().getEmail().equals(email)) {
            throw new BadRequestException("Você não tem permissão para aprovar este orçamento");
        }

        if (solicitacao.getStatus() != StatusSolicitacao.EM_ORCAMENTO) {
            throw new BadRequestException("Esta solicitação não está aguardando aprovação de orçamento");
        }

        if (orcamento.getStatus() != StatusOrcamento.PENDENTE) {
            throw new BadRequestException("Esta proposta já foi processada");
        }

        orcamento.setStatus(StatusOrcamento.APROVADO);
        orcamentoRepository.save(orcamento);

        List<Orcamento> pendentes = orcamentoRepository
                .findBySolicitacaoIdAndStatus(solicitacao.getId(), StatusOrcamento.PENDENTE);
        for (Orcamento outro : pendentes) {
            outro.setStatus(StatusOrcamento.REJEITADO);
            orcamentoRepository.save(outro);
        }

        solicitacao.setStatus(StatusSolicitacao.ACEITA);
        solicitacao.setPrestador(orcamento.getPrestador());
        solicitacao.setDataAceite(LocalDateTime.now());
        solicitacaoRepository.save(solicitacao);

        logService.registrar("ORCAMENTO_APROVADO",
                "Escola aprovou a proposta de " + orcamento.getPrestador().getNome()
                + " (R$ " + orcamento.getValorTotal() + ") para a solicitação #" + solicitacao.getId(),
                solicitacao.getId(), solicitacao.getCliente().getId(), orcamento.getPrestador().getId());

        return OrcamentoResponse.fromEntity(orcamento);
    }

    private String extrairEmail(String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        return jwtTokenProvider.obterEmailDoToken(token);
    }
}
