package com.mackenzie.lunchexpress.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitarServicoRequest {

    private String descricao;

    private boolean avulso = false;

    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser positiva")
    private Integer quantidade;

    @NotBlank(message = "Unidade de medida é obrigatória")
    private String unidadeMedida;

    @NotNull(message = "Data de entrega desejada é obrigatória")
    private LocalDateTime dataEntregaDesejada;

}
