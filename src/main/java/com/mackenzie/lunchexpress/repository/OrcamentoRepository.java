package com.mackenzie.lunchexpress.repository;

import com.mackenzie.lunchexpress.entity.Orcamento;
import com.mackenzie.lunchexpress.enums.StatusOrcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

    @Query("SELECT o FROM Orcamento o JOIN FETCH o.prestador WHERE o.solicitacao.id = :solicitacaoId ORDER BY o.dataCriacao ASC")
    List<Orcamento> findBySolicitacaoId(@Param("solicitacaoId") Long solicitacaoId);

    boolean existsBySolicitacaoIdAndPrestadorId(Long solicitacaoId, Long prestadorId);

    @Query("SELECT o FROM Orcamento o WHERE o.solicitacao.id = :solicitacaoId AND o.status = :status")
    List<Orcamento> findBySolicitacaoIdAndStatus(
            @Param("solicitacaoId") Long solicitacaoId,
            @Param("status") StatusOrcamento status);
}
