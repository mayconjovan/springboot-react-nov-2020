package com.maycon.sbreact.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.maycon.sbreact.exceptions.AutenticacaoException;
import com.maycon.sbreact.exceptions.RegraNegocioException;
import com.maycon.sbreact.model.entity.Usuario;
import com.maycon.sbreact.model.repository.UsuarioRepository;
import com.maycon.sbreact.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	UsuarioService service;

	@MockBean
	UsuarioRepository repository;

	@BeforeEach
	public void setUp() {
		service = new UsuarioServiceImpl(repository);
	}

	@Test
	@DisplayName("Deve autenticar um usuario com sucesso!")
	public void autenticaUsuario() {
		Assertions.assertDoesNotThrow(() -> {
			// cenario
			String email = "email@email.com";
			String senha = "senha";

			Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
			Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

			// acao
			Usuario result = service.autenticar(email, senha);

			// verificacao
			Assertions.assertNotNull(result);
		});
	}

	@Test
	@DisplayName("Deve lançar erro quando não encontrar usuario cadastrado com email informado.")
	public void throwsNotFoundUser() {
		Assertions.assertThrows(AutenticacaoException.class, () -> {
			Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

			service.autenticar("email.email.com", "senha");
		});
	};

	@Test
	@DisplayName("Deve lançar erro quando a senha não conferir")
	public void passwordWrongValidation() {
		Assertions.assertThrows(AutenticacaoException.class, () -> {
			String senha = "senha";
			Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
			Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

			// acao
			service.autenticar("email@email.com", "123");
		});
	}

	@Test
	@DisplayName("Deve validar email.")
	public void validarEmail() {
		Assertions.assertDoesNotThrow(() -> {
			// cenário
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
			// ação
			service.validarEmail("email@email.com");
		});
	}

	@Test
	@DisplayName("Deve lançar erro quando existir email cadastrado.")
	public void lancaErroValidarEmail() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {

			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

			service.validarEmail("email@email.com");
		});

	}

}
