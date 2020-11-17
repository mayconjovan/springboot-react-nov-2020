package com.maycon.sbreact.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.maycon.sbreact.exceptions.RegraNegocioException;
import com.maycon.sbreact.model.entity.Usuario;
import com.maycon.sbreact.model.repository.UsuarioRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {
	
	@Autowired
	UsuarioService service;
	
	@Autowired
	UsuarioRepository repository;
	
	@Test
	@DisplayName("Deve validar email.")
	public void validarEmail() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			repository.deleteAll();		
			
			// ação
			service.validarEmail("email@email.com");
		});		
	}
	
	@Test
	@DisplayName("Deve lançar erro quando existir email cadastrado.")
	public void lancaErroValidarEmail() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			Usuario usuario = Usuario.builder().nome("usuario").email("email@email.com").build();
			
			repository.save(usuario);
			
			service.validarEmail("email@email.com");
		});
		
	}
	

}
