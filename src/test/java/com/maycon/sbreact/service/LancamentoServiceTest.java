package com.maycon.sbreact.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.maycon.sbreact.exceptions.RegraNegocioException;
import com.maycon.sbreact.model.entity.Lancamento;
import com.maycon.sbreact.model.entity.Usuario;
import com.maycon.sbreact.model.entity.enums.StatusLancamento;
import com.maycon.sbreact.model.repository.LancamentoRepository;
import com.maycon.sbreact.model.repository.LancamentoRepositoryTest;
import com.maycon.sbreact.service.impl.LancamentoServiceImpl;

import net.bytebuddy.description.type.TypeDefinition.Sort;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;

	@MockBean
	LancamentoRepository repository;

	@Test
	@DisplayName("Deve salvar um lancamento.")
	public void salvarLancamento() {
		Assertions.assertDoesNotThrow(() -> {
			// cenario
			Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
			Mockito.doNothing().when(service).validar(lancamentoASalvar);

			Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
			lancamentoSalvo.setId(1l);
			lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
			Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

			// execucao
			Lancamento lancamento = service.salvar(lancamentoASalvar);

			// verificacao
			assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
			assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);

		});
	}

	@Test
	@DisplayName("Não deve salvar um lancamento quando houver erro de validação.")
	public void naoSalvarLancamento() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

		catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);

	}

	@Test
	@DisplayName("Deve atualizar um lancamento.")
	public void atualizarLancamento() {
		Assertions.assertDoesNotThrow(() -> {
			// cenario

			Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
			lancamentoSalvo.setId(1l);
			lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
			Mockito.doNothing().when(service).validar(lancamentoSalvo);

			Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

			// execucao
			Lancamento lancamento = service.atualizar(lancamentoSalvo);

			// verificacao
			Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
		});
	}

	@Test
	@DisplayName("Não deve atualizar um lancamento quando houver erro de validação.")
	public void naoAtualizarLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		catchThrowableOfType(() -> service.atualizar(lancamento), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamento);

	}

	@Test
	@DisplayName("Deve deletar um lançamento")
	public void deletaLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		service.deletarLancamento(lancamento);

		Mockito.verify(repository).delete(lancamento);

	}

	@Test
	@DisplayName("Deve lançar erro ao tentar deletar lancamento sem id.")
	public void erroDeletaLancamento() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		catchThrowableOfType(() -> service.deletarLancamento(lancamento), NullPointerException.class);

		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}

	@Test
	@DisplayName("Deve filtrar lançamentos.")
	public void filtrarLancamentos() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

		List<Lancamento> resultado = service.buscar(lancamento);

		assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);

	}

	@Test
	@DisplayName("Deve atualizar o status de um lancamento.")
	public void atualizarStatus() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);

		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

		service.atualizarStatus(lancamento, novoStatus);

		assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);

	}
	
	@Test
	@DisplayName("Deve obter um lancamento por id.")
	public void obterLancamentoById() {
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		Optional<Lancamento> result = service.obterPorId(id);
		assertThat(result.isPresent()).isTrue();
		
	}
	
	@Test
	@DisplayName("Deve retornar vazio quando um lancamento nao existe.")
	public void lancamentoNaoExiste() {
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		Optional<Lancamento> result = service.obterPorId(id);
		assertThat(result.isPresent()).isFalse();
		
	}
	
	@Test
	@DisplayName("")
	public void throwErroWhenValidLanc() {
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");
		
		lancamento.setDescricao("");
		
		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição válida.");
				
		lancamento.setDescricao("Salário");
		

		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");
		
		lancamento.setMes(0);
		
		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");
		
		lancamento.setMes(13);
		
		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");
		
		lancamento.setMes(1);
		
		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");
		
		lancamento.setAno(202);
		
		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");
		
		lancamento.setAno(2020);
		
		
		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário.");
		
		lancamento.setUsuario(new Usuario());		
		
		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário.");
		
		lancamento.setUsuario(new Usuario());
		lancamento.getUsuario().setId(1l);
		
		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido.");
		
		lancamento.setValor(BigDecimal.ZERO);
		
		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor válido.");
		
		lancamento.setValor(BigDecimal.valueOf(1));
		
		erro = catchThrowable(() -> service.validar(lancamento));
		assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lançamento.");
		
		
	}


}
