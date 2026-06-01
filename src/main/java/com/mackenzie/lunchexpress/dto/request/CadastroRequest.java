package com.mackenzie.lunchexpress.dto.request;

import com.mackenzie.lunchexpress.enums.PerfilUsuario;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CadastroRequest {
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter mínimo 6 caracteres")
    private String senha;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "Cidade é obrigatória")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    private String estado;

    @NotNull(message = "Perfil é obrigatório")
    private PerfilUsuario perfil;

    private String cep;
    private String logradouro;
    private String numero;
    private String bairro;
}
