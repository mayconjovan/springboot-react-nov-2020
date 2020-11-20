package com.maycon.sbreact.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.maycon.sbreact.model.entity.Lancamento;
import com.maycon.sbreact.model.entity.enums.StatusLancamento;
import com.maycon.sbreact.model.entity.enums.TipoLancamento;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository repository;

	@Autowired
	TestEntityManager entityManager;

	@Test
	@DisplayName("Deve salvar um lancamento")
	public void salvarLancamento() {
		Lancamento lancamento = criarLancamento();

		lancamento = repository.save(lancamento);

		assertThat(lancamento.getId()).isNotNull();
	}

	@Test
	@DisplayName("Deve deletar um lançamento.")
	public void deletarLancamento() {
		Lancamento lancamento = criaPersisteLancamento();

		lancamento = entityManager.find(Lancamento.class, lancamento.getId());

		repository.delete(lancamento);

		Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());
		assertThat(lancamentoInexistente).isNull();

	}

	@Test
	@DisplayName("Deve atualizar um lancamento.")
	public void atualizaLancamento() {
		Lancamento lancamento = criaPersisteLancamento();
		lancamento.setAno(2018);
		lancamento.setDescricao("Teste atualizar");
		lancamento.setStatus(StatusLancamento.CANCENLADO);

		repository.save(lancamento);

		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

		assertThat(lancamentoAtualizado.getAno()).isEqualTo(2018);
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste atualizar");
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCENLADO);

	}

	@Test
	@DisplayName("Deve buscar um lancamento por id.")
	public void buscaLancamentoPorId() {
		Lancamento lancamento = criaPersisteLancamento();

		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
		assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}

	public static Lancamento criarLancamento() {
		return Lancamento.builder().ano(2019).mes(1).descricao("lancamento qualquer").valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.RECEITA).status(StatusLancamento.PENDENTE).dataCadastro(LocalDate.now()).build();
	}

	private Lancamento criaPersisteLancamento() {
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		return lancamento;
	}

}
