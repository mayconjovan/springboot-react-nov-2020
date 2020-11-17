package com.maycon.sbreact.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maycon.sbreact.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
	
}
