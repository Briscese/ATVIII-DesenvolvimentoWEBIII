package com.autobots.automanager.controles;

import com.autobots.automanager.modelos.AdicionarLinkServicos;
import com.autobots.automanager.modelos.ServicosCadastro;
import com.autobots.automanager.repositorios.ServicosRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Servico;

import org.springframework.hateoas.CollectionModel;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/servicos")
public class ServicosControle {

	@Autowired
	private ServicosRepositorio repositorioServico;

	@Autowired
	private AdicionarLinkServicos adicionadorLinkServicos;

	@PostMapping
	public ResponseEntity<EntityModel<Servico>> cadastrarServico(@RequestBody ServicosCadastro servicoCadastro) {
		try {
			// Converter o DTO para a entidade
			Servico novoServico = servicoCadastro.toServico();

			// Lógica para cadastrar o novo serviço
			Servico servicoCadastrado = repositorioServico.save(novoServico);

			// Criar link HATEOAS para o novo serviço
			Link selfLink = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder.methodOn(ServicosControle.class).obterServicoPorId(servicoCadastrado.getId()))
					.withSelfRel();
			EntityModel<Servico> resource = EntityModel.of(servicoCadastrado, selfLink);

			return new ResponseEntity<>(resource, HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<EntityModel<Servico>> obterServicoPorId(@PathVariable Long id) {
		Servico servico = repositorioServico.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));

		// Adicionar links HATEOAS
		adicionadorLinkServicos.adicionarLink(servico);

		// Criar link HATEOAS para o serviço
		Link selfLink = Link.of(String.format("/servicos/%d", servico.getId())).withSelfRel();
		EntityModel<Servico> resource = EntityModel.of(servico, selfLink);

		return new ResponseEntity<>(resource, HttpStatus.OK);
	}

	// GET para obter todos os serviços
	@GetMapping
	public CollectionModel<EntityModel<Servico>> obterServicos() {
		List<Servico> servicos = repositorioServico.findAll();

		List<EntityModel<Servico>> servicosDTO = servicos.stream().map(servico -> {
			adicionadorLinkServicos.adicionarLink(servico);
			Link selfLink = Link.of(String.format("/servicos/%d", servico.getId())).withSelfRel();
			return EntityModel.of(servico, selfLink);
		}).collect(Collectors.toList());

		Link linkSelf = Link.of("/servicos").withSelfRel();
		Link linkPost = Link.of("/servicos").withRel("post");

		return CollectionModel.of(servicosDTO, linkSelf, linkPost);
	}

	// PUT para atualizar um serviço
	@PutMapping("/{id}")
	public ResponseEntity<EntityModel<Servico>> atualizarServico(@PathVariable Long id,
			@RequestBody ServicosCadastro servicoCadastro) {
		Servico servico = repositorioServico.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));

		try {
			// Atualizar os dados do serviço com base no DTO
			servico.setNome(servicoCadastro.getNome());
			servico.setValor(servicoCadastro.getValor());
			servico.setDescricao(servicoCadastro.getDescricao());

			// Lógica para salvar o serviço atualizado
			Servico servicoAtualizado = repositorioServico.save(servico);

			// Adicionar links HATEOAS
			adicionadorLinkServicos.adicionarLink(servicoAtualizado);

			// Criar link HATEOAS para o serviço atualizado
			Link selfLink = Link.of(String.format("/servicos/%d", servicoAtualizado.getId())).withSelfRel();
			EntityModel<Servico> resource = EntityModel.of(servicoAtualizado, selfLink);

			return new ResponseEntity<>(resource, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	// DELETE para excluir um serviço
	@DeleteMapping("/{id}")
	public ResponseEntity<?> excluirServico(@PathVariable Long id) {
		try {
			// Lógica para excluir o serviço
			repositorioServico.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}
