package com.maycon.sbreact.model.repository;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.maycon.sbreact.model.entity.Usuario;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository repository;
	
	
	@Test
	@DisplayName("Deve retornar verdadeiro quando houver a existencia de um email informado.")
	public void existsEmail() {
		// cenário
		Usuario usuario = Usuario.builder().nome("usuario").email("usuario@email.com").build();
		repository.save(usuario);
		
		// ação/execução
		boolean result = repository.existsByEmail("usuario@email.com");
		
		// verificação		
		Assertions.assertThat(result).isTrue();
		
	}
	
	@Test
	@DisplayName("Deve retornar falso quando não houver usuario cadastrado com o email informado.")
	public void notExistsEmail() {
		// cenario
		repository.deleteAll();
		
		// ação
		boolean result = repository.existsByEmail("usuario@email.com");
		//verificação
		
		Assertions.assertThat(result).isFalse();
		
	}
}
