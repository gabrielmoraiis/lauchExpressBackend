package com.mackenzie.lunchexpress.service;

import com.mackenzie.lunchexpress.dto.request.PublicarServicoRequest;
import com.mackenzie.lunchexpress.dto.response.ServicoResponse;
import com.mackenzie.lunchexpress.entity.Servico;
import com.mackenzie.lunchexpress.entity.Usuario;
import com.mackenzie.lunchexpress.exception.BadRequestException;
import com.mackenzie.lunchexpress.exception.ResourceNotFoundException;
import com.mackenzie.lunchexpress.repository.ServicoRepository;
import com.mackenzie.lunchexpress.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ServicoResponse publicarServico(PublicarServicoRequest request, String tokenHeader) {
        String email = extrairEmail(tokenHeader);
        Usuario prestador = usuarioService.obterPorEmail(email);

        Servico servico = Servico.builder()
                .prestador(prestador)
                .descricao(request.getDescricao())
                .categoria(request.getCategoria().toLowerCase().trim())
                .preco(request.getPreco())
                .quantidadeDisponivel(request.getQuantidadeDisponivel())
                .unidadeMedida(request.getUnidadeMedida())
                .build();

        return ServicoResponse.fromEntity(servicoRepository.save(servico));
    }

    public List<ServicoResponse> listarMeusServicos(String tokenHeader) {
        String email = extrairEmail(tokenHeader);
        Usuario prestador = usuarioService.obterPorEmail(email);

        return servicoRepository.findByPrestadorIdWithPrestador(prestador.getId())
                .stream()
                .map(ServicoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ServicoResponse> listarPorCategoria(String categoria) {
        return servicoRepository.findDisponivelPorCategoria(categoria.toLowerCase().trim())
                .stream()
                .map(ServicoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ServicoResponse> listarTodos() {
        return servicoRepository.findAll()
                .stream()
                .map(ServicoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ServicoResponse atualizar(Long id, PublicarServicoRequest request, String tokenHeader) {
        String email = extrairEmail(tokenHeader);
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado: " + id));

        if (!servico.getPrestador().getEmail().equals(email)) {
            throw new BadRequestException("Você não tem permissão para editar este serviço");
        }

        servico.setDescricao(request.getDescricao());
        servico.setCategoria(request.getCategoria().toLowerCase().trim());
        servico.setPreco(request.getPreco());
        servico.setQuantidadeDisponivel(request.getQuantidadeDisponivel());
        servico.setUnidadeMedida(request.getUnidadeMedida());

        return ServicoResponse.fromEntity(servicoRepository.save(servico));
    }

    @Transactional
    public void deletar(Long id, String tokenHeader) {
        String email = extrairEmail(tokenHeader);
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado: " + id));

        if (!servico.getPrestador().getEmail().equals(email)) {
            throw new BadRequestException("Você não tem permissão para deletar este serviço");
        }

        servicoRepository.delete(servico);
    }

    @Transactional
    public ServicoResponse alterarDisponibilidade(Long id, Boolean disponivel, String tokenHeader) {
        String email = extrairEmail(tokenHeader);
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado: " + id));

        if (!servico.getPrestador().getEmail().equals(email)) {
            throw new BadRequestException("Você não tem permissão para alterar este serviço");
        }

        servico.setDisponivel(disponivel);
        return ServicoResponse.fromEntity(servicoRepository.save(servico));
    }

    public void verificarEstoqueParaAceite(Long prestadorId, String categoria, int quantidadeSolicitada) {
        Optional<Servico> opt = servicoRepository
                .findFirstByPrestadorIdAndCategoriaAndDisponivelTrue(prestadorId, categoria);
        if (opt.isEmpty()) {
            throw new BadRequestException(
                    "Você não possui um serviço ativo na categoria '" + categoria + "' para aceitar este pedido");
        }
        Servico s = opt.get();
        if (s.getQuantidadeDisponivel() < quantidadeSolicitada) {
            throw new BadRequestException(
                    "Estoque insuficiente: você tem " + s.getQuantidadeDisponivel()
                    + " " + s.getUnidadeMedida() + " disponível(is), mas o pedido requer " + quantidadeSolicitada);
        }
    }

    private String extrairEmail(String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        return jwtTokenProvider.obterEmailDoToken(token);
    }
}
