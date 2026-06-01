package com.mackenzie.lunchexpress.dto.response;

import com.mackenzie.lunchexpress.enums.PerfilUsuario;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {
    private String token;
    @Builder.Default
    private String tipo = "Bearer";
    private Long usuarioId;
    private String email;
    private String nome;
    private PerfilUsuario perfil;
}
