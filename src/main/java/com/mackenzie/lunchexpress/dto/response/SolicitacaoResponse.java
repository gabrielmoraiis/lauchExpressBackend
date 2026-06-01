package com.mackenzie.lunchexpress.dto.response;

import com.mackenzie.lunchexpress.entity.Solicitacao;
import com.mackenzie.lunchexpress.enums.StatusSolicitacao;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SolicitacaoResponse {

    private Long id;
    private boolean avulso;
    private Long clienteId;
    private String nomeCliente;
    private Long prestadorId;
    private String nomePrestador;
    private String descricao;
    private String categoria;
    private Integer quantidade;
    private String unidadeMedida;
    private LocalDateTime dataEntregaDesejada;
    private StatusSolicitacao status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAceite;
    private LocalDateTime dataEntregue;
    private String enderecoCliente;

    public static SolicitacaoResponse fromEntity(Solicitacao s) {
        SolicitacaoResponse r = new SolicitacaoResponse();
        r.id                  = s.getId();
        r.avulso              = s.isAvulso();
        r.clienteId           = s.getCliente().getId();
        r.nomeCliente         = s.getCliente().getNome();
        r.prestadorId         = s.getPrestador() != null ? s.getPrestador().getId()   : null;
        r.nomePrestador       = s.getPrestador() != null ? s.getPrestador().getNome() : null;
        r.descricao           = s.getDescricao();
        r.categoria           = s.getCategoria();
        r.quantidade          = s.getQuantidade();
        r.unidadeMedida       = s.getUnidadeMedida();
        r.dataEntregaDesejada = s.getDataEntregaDesejada();
        r.status              = s.getStatus();
        r.dataCriacao         = s.getDataCriacao();
        r.dataAceite          = s.getDataAceite();
        r.dataEntregue        = s.getDataEntregue();
        r.enderecoCliente     = s.getCliente().enderecoCompleto();
        return r;
    }
}
