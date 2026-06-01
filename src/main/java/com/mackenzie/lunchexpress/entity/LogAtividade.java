package com.mackenzie.lunchexpress.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_atividades")
@Getter
@Setter
@NoArgsConstructor
public class LogAtividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String evento;

    @Column(nullable = false, length = 500)
    private String descricao;

    private Long solicitacaoId;
    private Long escolaId;
    private Long fornecedorId;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();
}
