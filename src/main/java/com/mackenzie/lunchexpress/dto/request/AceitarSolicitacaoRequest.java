package com.mackenzie.lunchexpress.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AceitarSolicitacaoRequest {

    @NotNull(message = "Campo aceitar é obrigatório")
    private Boolean aceitar;

    private String motivo; // Opcional — usado em caso de recusa
}
