package com.mackenzie.lunchexpress.repository;

import com.mackenzie.lunchexpress.entity.Solicitacao;
import com.mackenzie.lunchexpress.enums.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    @Query("SELECT s FROM Solicitacao s JOIN FETCH s.cliente WHERE s.cliente.id = :clienteId ORDER BY s.dataCriacao DESC")
    List<Solicitacao> findByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT s FROM Solicitacao s JOIN FETCH s.cliente WHERE s.prestador.id = :prestadorId ORDER BY s.dataCriacao DESC")
    List<Solicitacao> findByPrestadorId(@Param("prestadorId") Long prestadorId);

    List<Solicitacao> findByStatus(StatusSolicitacao status);

    @Query("SELECT s FROM Solicitacao s JOIN FETCH s.cliente WHERE s.status = :status ORDER BY s.dataCriacao DESC")
    List<Solicitacao> findByStatusWithCliente(@Param("status") StatusSolicitacao status);

    @Query("SELECT s FROM Solicitacao s JOIN FETCH s.cliente WHERE s.status IN :statuses ORDER BY s.dataCriacao DESC")
    List<Solicitacao> findByStatusInWithCliente(@Param("statuses") List<StatusSolicitacao> statuses);
}
