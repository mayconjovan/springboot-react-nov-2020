package com.maycon.sbreact.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.maycon.sbreact.model.entity.Lancamento;
import com.maycon.sbreact.model.entity.enums.StatusLancamento;
import com.maycon.sbreact.model.entity.enums.TipoLancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
	
	@Query(value =
	"SELECT SUM(l.valor) FROM Lancamento l JOIN l.usuario u WHERE u.id = :idUsuario"
	+ " AND l.tipo = :tipo and l.status = :status GROUP BY u ")
	BigDecimal obtserSaldoPorTipoLancamentoEusuarioEStatus(
			@Param("idUsuario") Long idUsuario, 
			@Param("tipo") TipoLancamento tipo,
			@Param("status") StatusLancamento status);
}
