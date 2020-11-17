package com.maycon.sbreact.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maycon.sbreact.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

	boolean existsByEmail(String email);
	
	Optional<Usuario> findByEmail(String email);
	
}
