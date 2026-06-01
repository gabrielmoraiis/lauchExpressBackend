package com.mackenzie.lunchexpress.repository;

import com.mackenzie.lunchexpress.entity.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findByPrestadorId(Long prestadorId);

    List<Servico> findByCategoriaAndDisponivelTrue(String categoria);

    @Query("SELECT s FROM Servico s JOIN FETCH s.prestador WHERE s.categoria = :categoria AND s.disponivel = true")
    List<Servico> findDisponivelPorCategoria(@Param("categoria") String categoria);

    @Query("SELECT s FROM Servico s JOIN FETCH s.prestador WHERE s.prestador.id = :prestadorId")
    List<Servico> findByPrestadorIdWithPrestador(@Param("prestadorId") Long prestadorId);

    Optional<Servico> findFirstByPrestadorIdAndCategoriaAndDisponivelTrue(Long prestadorId, String categoria);
}
