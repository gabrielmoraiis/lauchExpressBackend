package com.mackenzie.lunchexpress.dto.response;

import com.mackenzie.lunchexpress.entity.LogAtividade;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LogResponse {

    private Long id;
    private String evento;
    private String descricao;
    private Long solicitacaoId;
    private LocalDateTime dataCriacao;

    public static LogResponse fromEntity(LogAtividade l) {
        LogResponse r = new LogResponse();
        r.id             = l.getId();
        r.evento         = l.getEvento();
        r.descricao      = l.getDescricao();
        r.solicitacaoId  = l.getSolicitacaoId();
        r.dataCriacao    = l.getDataCriacao();
        return r;
    }
}
