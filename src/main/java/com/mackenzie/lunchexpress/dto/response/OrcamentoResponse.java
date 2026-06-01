package com.mackenzie.lunchexpress.dto.response;

import com.mackenzie.lunchexpress.entity.Orcamento;
import com.mackenzie.lunchexpress.enums.StatusOrcamento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrcamentoResponse {

    private Long id;
    private Long solicitacaoId;
    private Long prestadorId;
    private String nomePrestador;
    private BigDecimal valorTotal;
    private String observacoes;
    private StatusOrcamento status;
    private LocalDateTime dataCriacao;

    public static OrcamentoResponse fromEntity(Orcamento o) {
        OrcamentoResponse r = new OrcamentoResponse();
        r.id             = o.getId();
        r.solicitacaoId  = o.getSolicitacao().getId();
        r.prestadorId    = o.getPrestador().getId();
        r.nomePrestador  = o.getPrestador().getNome();
        r.valorTotal     = o.getValorTotal();
        r.observacoes    = o.getObservacoes();
        r.status         = o.getStatus();
        r.dataCriacao    = o.getDataCriacao();
        return r;
    }
}
