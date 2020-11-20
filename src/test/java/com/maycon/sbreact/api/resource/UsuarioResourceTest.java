package com.maycon.sbreact.api.resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maycon.sbreact.api.dto.UsuarioDTO;
import com.maycon.sbreact.exceptions.AutenticacaoException;
import com.maycon.sbreact.exceptions.RegraNegocioException;
import com.maycon.sbreact.model.entity.Usuario;
import com.maycon.sbreact.service.LancamentoService;
import com.maycon.sbreact.service.UsuarioService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioResource.class)
@AutoConfigureMockMvc
public class UsuarioResourceTest {

	static final String API = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;

	@Autowired
	MockMvc mvc;

	@MockBean
	UsuarioService service;

	@MockBean
	LancamentoService lancamentoService;

	@Test
	@DisplayName("Deve autenticar um usuário.")
	public void autenticaUsuario() throws Exception {
		String email = "usuario@email.com";
		String senha = "123";

		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();

		Mockito.when(service.autenticar(email, senha)).thenReturn(usuario);

		String json = new ObjectMapper().writeValueAsString(dto);

		// execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar")).accept(JSON)
				.contentType(JSON).content(json);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
				.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
	}

	@Test
	@DisplayName("Deve retornar bad request ao obter erro de autenticacao.")
	public void badRequestAutenticaUsuario() throws Exception {
		String email = "usuario@email.com";
		String senha = "123";

		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();

		Mockito.when(service.autenticar(email, senha)).thenThrow(AutenticacaoException.class);

		String json = new ObjectMapper().writeValueAsString(dto);

		// execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar")).accept(JSON)
				.contentType(JSON).content(json);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

	@Test
	@DisplayName("Deve criar um novo usuário.")
	public void criaNovoUsuario() throws Exception {
		String email = "usuario@email.com";
		String senha = "123";

		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();

		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);

		String json = new ObjectMapper().writeValueAsString(dto);

		// execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API).accept(JSON).contentType(JSON)
				.content(json);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
				.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
	}
	
	@Test
	@DisplayName("Deve retornar bad request ao tentar criar um novo usuário inválido.")
	public void badRequestCriaNovoUsuario() throws Exception {
		String email = "usuario@email.com";
		String senha = "123";

		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();

		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);

		String json = new ObjectMapper().writeValueAsString(dto);

		// execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API).accept(JSON).contentType(JSON)
				.content(json);

		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
		
	}

}
