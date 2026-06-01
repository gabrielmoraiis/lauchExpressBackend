package com.mackenzie.lunchexpress.service;

import com.mackenzie.lunchexpress.dto.request.AceitarSolicitacaoRequest;
import com.mackenzie.lunchexpress.dto.request.SolicitarServicoRequest;
import com.mackenzie.lunchexpress.dto.response.SolicitacaoResponse;
import com.mackenzie.lunchexpress.dto.response.ServicoResponse;
import com.mackenzie.lunchexpress.entity.Solicitacao;
import com.mackenzie.lunchexpress.entity.Usuario;
import com.mackenzie.lunchexpress.enums.StatusSolicitacao;
import com.mackenzie.lunchexpress.exception.BadRequestException;
import com.mackenzie.lunchexpress.exception.ResourceNotFoundException;
import com.mackenzie.lunchexpress.repository.SolicitacaoRepository;
import com.mackenzie.lunchexpress.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final ServicoService servicoService;
    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;
    private final LogAtividadeService logService;

    @Transactional
    public SolicitacaoResponse criarSolicitacao(SolicitarServicoRequest request, String tokenHeader) {
        String email = extrairEmail(tokenHeader);
        Usuario cliente = usuarioService.obterPorEmail(email);

        Solicitacao solicitacao = Solicitacao.builder()
                .cliente(cliente)
                .descricao(request.getDescricao())
                .avulso(request.isAvulso())
                .categoria(request.getCategoria().toLowerCase().trim())
                .quantidade(request.getQuantidade())
                .unidadeMedida(request.getUnidadeMedida())
                .dataEntregaDesejada(request.getDataEntregaDesejada())
                .status(StatusSolicitacao.PENDENTE)
                .build();

        SolicitacaoResponse resp = SolicitacaoResponse.fromEntity(solicitacaoRepository.save(solicitacao));
        logService.registrar("SOLICITACAO_CRIADA",
                "Escola " + cliente.getNome() + " criou solicitação #" + resp.getId()
                + " — " + solicitacao.getCategoria() + " (" + solicitacao.getQuantidade() + " " + solicitacao.getUnidadeMedida() + ")",
                resp.getId(), cliente.getId(), null);
        return resp;
    }

    public List<ServicoResponse> obterMatchingPrestadores(Long solicitacaoId) {
        Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada: " + solicitacaoId));

        return servicoService.listarPorCategoria(solicitacao.getCategoria());
    }

    @Transactional
    public SolicitacaoResponse aceitarSolicitacao(Long id, AceitarSolicitacaoRequest request, String tokenHeader) {
        String email = extrairEmail(tokenHeader);
        Usuario prestador = usuarioService.obterPorEmail(email);

        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada: " + id));

        if (solicitacao.isAvulso()) {
            throw new BadRequestException("Solicitações avulsas requerem envio de proposta — use o fluxo de orçamento");
        }

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new BadRequestException("Esta solicitação já foi aceita ou não está disponível");
        }

        try {
            if (Boolean.TRUE.equals(request.getAceitar())) {
                servicoService.verificarEstoqueParaAceite(
                        prestador.getId(), solicitacao.getCategoria(), solicitacao.getQuantidade());
                solicitacao.setPrestador(prestador);
                solicitacao.setStatus(StatusSolicitacao.ACEITA);
                solicitacao.setDataAceite(LocalDateTime.now());
            } else {
                solicitacao.setStatus(StatusSolicitacao.RECUSADA);
            }

            SolicitacaoResponse resp = SolicitacaoResponse.fromEntity(solicitacaoRepository.save(solicitacao));
            Long escolaId = solicitacao.getCliente().getId();
            if (Boolean.TRUE.equals(request.getAceitar())) {
                logService.registrar("SOLICITACAO_ACEITA",
                        "Fornecedor " + prestador.getNome() + " aceitou a solicitação #" + id,
                        id, escolaId, prestador.getId());
            } else {
                logService.registrar("SOLICITACAO_RECUSADA",
                        "Fornecedor " + prestador.getNome() + " recusou a solicitação #" + id,
                        id, escolaId, prestador.getId());
            }
            return resp;

        } catch (OptimisticLockingFailureException e) {
            throw new BadRequestException("Esta solicitação acabou de ser aceita por outro prestador. Por favor, escolha outra.");
        }
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoResponse> listarSolicitacoesCliente(String tokenHeader) {
        String email = extrairEmail(tokenHeader);
        Usuario cliente = usuarioService.obterPorEmail(email);

        return solicitacaoRepository.findByClienteId(cliente.getId())
                .stream()
                .map(SolicitacaoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoResponse> listarSolicitacoesPrestador(String tokenHeader) {
        String email = extrairEmail(tokenHeader);
        Usuario prestador = usuarioService.obterPorEmail(email);

        return solicitacaoRepository.findByPrestadorId(prestador.getId())
                .stream()
                .map(SolicitacaoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoResponse> listarPendentes() {
        return solicitacaoRepository.findByStatusInWithCliente(
                Arrays.asList(StatusSolicitacao.PENDENTE, StatusSolicitacao.EM_ORCAMENTO))
                .stream()
                .map(SolicitacaoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SolicitacaoResponse obterDetalhes(Long id) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada: " + id));
        return SolicitacaoResponse.fromEntity(solicitacao);
    }

    @Transactional
    public SolicitacaoResponse cancelar(Long id, String tokenHeader) {
        String email = extrairEmail(tokenHeader);
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada: " + id));

        if (!solicitacao.getCliente().getEmail().equals(email)) {
            throw new BadRequestException("Você não tem permissão para cancelar esta solicitação");
        }
        if (solicitacao.getStatus() == StatusSolicitacao.ENTREGUE_ESCOLA) {
            throw new BadRequestException("Não é possível cancelar uma solicitação já entregue");
        }

        solicitacao.setStatus(StatusSolicitacao.CANCELADA);
        SolicitacaoResponse resp = SolicitacaoResponse.fromEntity(solicitacaoRepository.save(solicitacao));
        logService.registrar("SOLICITACAO_CANCELADA",
                "Escola cancelou a solicitação #" + id,
                id, solicitacao.getCliente().getId(), null);
        return resp;
    }

    @Transactional
    public SolicitacaoResponse avancarRastreio(Long id, String tokenHeader) {
        String email = extrairEmail(tokenHeader);
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada: " + id));

        if (solicitacao.getPrestador() == null || !solicitacao.getPrestador().getEmail().equals(email)) {
            throw new BadRequestException("Você não tem permissão para atualizar o rastreio desta solicitação");
        }

        StatusSolicitacao proximoStatus = switch (solicitacao.getStatus()) {
            case ACEITA -> StatusSolicitacao.INSUMOS_PREPARADOS;
            case INSUMOS_PREPARADOS -> StatusSolicitacao.EM_ROTA_ENTREGA;
            case EM_ROTA_ENTREGA -> {
                solicitacao.setDataEntregue(LocalDateTime.now());
                yield StatusSolicitacao.ENTREGUE_ESCOLA;
            }
            default -> throw new BadRequestException("Status atual não permite avançar rastreio: " + solicitacao.getStatus());
        };

        solicitacao.setStatus(proximoStatus);
        SolicitacaoResponse resp = SolicitacaoResponse.fromEntity(solicitacaoRepository.save(solicitacao));
        logService.registrar("RASTREIO_ATUALIZADO",
                "Rastreio da solicitação #" + id + " atualizado para: " + proximoStatus.name(),
                id, solicitacao.getCliente().getId(), solicitacao.getPrestador().getId());
        return resp;
    }

    private double calcularDistanciaKm(double lat1, double lng1, double lat2, double lng2) {
        final int RAIO_TERRA = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RAIO_TERRA * c;
    }

    private String extrairEmail(String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        return jwtTokenProvider.obterEmailDoToken(token);
    }
}
