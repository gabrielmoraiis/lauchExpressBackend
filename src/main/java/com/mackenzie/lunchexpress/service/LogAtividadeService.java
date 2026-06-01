package com.mackenzie.lunchexpress.service;

import com.mackenzie.lunchexpress.dto.response.LogResponse;
import com.mackenzie.lunchexpress.entity.LogAtividade;
import com.mackenzie.lunchexpress.entity.Usuario;
import com.mackenzie.lunchexpress.enums.PerfilUsuario;
import com.mackenzie.lunchexpress.repository.LogAtividadeRepository;
import com.mackenzie.lunchexpress.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogAtividadeService {

    private final LogAtividadeRepository logRepository;
    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void registrar(String evento, String descricao, Long solicitacaoId, Long escolaId, Long fornecedorId) {
        LogAtividade log = new LogAtividade();
        log.setEvento(evento);
        log.setDescricao(descricao);
        log.setSolicitacaoId(solicitacaoId);
        log.setEscolaId(escolaId);
        log.setFornecedorId(fornecedorId);
        logRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<LogResponse> listarParaUsuario(String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.obterEmailDoToken(token);
        Usuario usuario = usuarioService.obterPorEmail(email);

        List<LogAtividade> logs = usuario.getPerfil() == PerfilUsuario.CLIENTE
                ? logRepository.findByEscolaIdOrderByDataCriacaoDesc(usuario.getId())
                : logRepository.findByFornecedorIdOrderByDataCriacaoDesc(usuario.getId());

        return logs.stream().map(LogResponse::fromEntity).collect(Collectors.toList());
    }
}
