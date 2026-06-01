package com.mackenzie.lunchexpress.entity;

import com.mackenzie.lunchexpress.enums.PerfilUsuario;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String telefone;

    private String cep;
    private String logradouro;
    private String numero;
    private String bairro;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilUsuario perfil;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();

    public String enderecoCompleto() {
        StringBuilder sb = new StringBuilder();
        if (logradouro != null && !logradouro.isBlank()) sb.append(logradouro);
        if (numero     != null && !numero.isBlank())     sb.append(", ").append(numero);
        if (bairro     != null && !bairro.isBlank())     sb.append(", ").append(bairro);
        if (cidade     != null && !cidade.isBlank())     sb.append(", ").append(cidade);
        if (estado     != null && !estado.isBlank())     sb.append(" - ").append(estado);
        if (cep        != null && !cep.isBlank())        sb.append(", CEP ").append(cep);
        return sb.toString();
    }
}
