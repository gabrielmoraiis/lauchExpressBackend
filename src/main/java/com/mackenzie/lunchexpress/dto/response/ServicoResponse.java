package com.mackenzie.lunchexpress.dto.response;

import com.mackenzie.lunchexpress.entity.Servico;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoResponse {
    private Long id;
    private Long prestadorId;
    private String nomePrestador;
    private String descricao;
    private String categoria;
    private Double preco;
    private Integer quantidadeDisponivel;
    private String unidadeMedida;
    private Boolean disponivel;
    private LocalDateTime dataCriacao;

    public static ServicoResponse fromEntity(Servico s) {
        return ServicoResponse.builder()
                .id(s.getId())
                .prestadorId(s.getPrestador().getId())
                .nomePrestador(s.getPrestador().getNome())
                .descricao(s.getDescricao())
                .categoria(s.getCategoria())
                .preco(s.getPreco())
                .quantidadeDisponivel(s.getQuantidadeDisponivel())
                .unidadeMedida(s.getUnidadeMedida())
                .disponivel(s.getDisponivel())
                .dataCriacao(s.getDataCriacao())
                .build();
    }
}
