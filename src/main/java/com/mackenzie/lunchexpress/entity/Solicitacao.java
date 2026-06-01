package com.mackenzie.lunchexpress.entity;

import com.mackenzie.lunchexpress.enums.StatusSolicitacao;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id")
    private Usuario prestador;

    private String descricao;

    @Column(nullable = false)
    @Builder.Default
    private boolean avulso = false;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "unidade_medida", nullable = false)
    private String unidadeMedida;

    @Column(name = "data_entrega_desejada")
    private LocalDateTime dataEntregaDesejada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusSolicitacao status = StatusSolicitacao.PENDENTE;

    @Version
    private Long version;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    @Builder.Default
    private LocalDateTime dataAtualizacao = LocalDateTime.now();

    @Column(name = "data_aceite")
    private LocalDateTime dataAceite;

    @Column(name = "data_entregue")
    private LocalDateTime dataEntregue;

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
}
