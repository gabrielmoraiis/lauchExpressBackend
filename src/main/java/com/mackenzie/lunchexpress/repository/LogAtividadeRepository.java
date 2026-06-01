package com.mackenzie.lunchexpress.repository;

import com.mackenzie.lunchexpress.entity.LogAtividade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LogAtividadeRepository extends JpaRepository<LogAtividade, Long> {

    List<LogAtividade> findByEscolaIdOrderByDataCriacaoDesc(Long escolaId);

    List<LogAtividade> findByFornecedorIdOrderByDataCriacaoDesc(Long fornecedorId);
}
