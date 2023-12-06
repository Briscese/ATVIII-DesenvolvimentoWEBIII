package com.autobots.automanager.controles;

import com.autobots.automanager.modelos.AdicionarLinkVeiculos;
import com.autobots.automanager.modelos.VeiculoCadastro;
import com.autobots.automanager.repositorios.VeiculoRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.autobots.automanager.entidades.Veiculo;

import org.springframework.hateoas.CollectionModel;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/veiculos")
public class VeiculoControle {

	@Autowired
	private VeiculoRepositorio repositorioVeiculo;

	@Autowired
	private AdicionarLinkVeiculos adicionadorLinkVeiculos;

	@PostMapping
	public ResponseEntity<EntityModel<Veiculo>> cadastrarVeiculo(@RequestBody VeiculoCadastro veiculoCadastro) {
		try {
			// Converter o DTO para a entidade
			Veiculo novoVeiculo = veiculoCadastro.toVeiculo();

			// Lógica para cadastrar o novo veículo
			Veiculo veiculoCadastrado = repositorioVeiculo.save(novoVeiculo);

			// Adicionar links HATEOAS
			adicionadorLinkVeiculos.adicionarLink(veiculoCadastrado);

			// Criar link HATEOAS para o novo veículo
			Link selfLink = Link.of(String.format("/veiculos/%d", veiculoCadastrado.getId())).withSelfRel();

			// Criar instância de EntityModel com os links
			EntityModel<Veiculo> resource = EntityModel.of(veiculoCadastrado).add(selfLink);

			return new ResponseEntity<>(resource, HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<EntityModel<Veiculo>> obterVeiculoPorId(@PathVariable Long id) {
		// Lógica para obter o veículo por ID
		Veiculo veiculo = repositorioVeiculo.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

		// Adicionar links HATEOAS
		adicionadorLinkVeiculos.adicionarLink(veiculo);

		// Criar link HATEOAS para o veículo
		Link selfLink = Link.of(String.format("/veiculos/%d", veiculo.getId())).withSelfRel();
		EntityModel<Veiculo> resource = EntityModel.of(veiculo, selfLink);

		return new ResponseEntity<>(resource, HttpStatus.OK);
	}

	@GetMapping
	public CollectionModel<EntityModel<Veiculo>> obterVeiculos() {
		List<Veiculo> veiculos = repositorioVeiculo.findAll();
		List<EntityModel<Veiculo>> veiculosDTO = veiculos.stream().map(veiculo -> {
			Link selfLink = Link.of(String.format("/veiculos/%d", veiculo.getId())).withSelfRel();
			return EntityModel.of(veiculo, selfLink);
		}).collect(Collectors.toList());

		Link linkSelf = Link.of("/veiculos").withSelfRel();
		Link linkPost = Link.of("/veiculos").withRel("post");

		return CollectionModel.of(veiculosDTO, linkSelf, linkPost);
	}

	@PutMapping("/{id}")
	public ResponseEntity<EntityModel<Veiculo>> atualizarVeiculo(@PathVariable Long id,
			@RequestBody VeiculoCadastro veiculoCadastro) {
		// Lógica para atualizar o veículo
		Veiculo veiculo = repositorioVeiculo.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

		// Atualizar os atributos do veículo com base no DTO
		veiculo.setTipo(veiculoCadastro.getTipo());
		veiculo.setModelo(veiculoCadastro.getModelo());
		veiculo.setPlaca(veiculoCadastro.getPlaca());
		veiculo.setProprietario(veiculoCadastro.getProprietario());

		// Salvar as alterações
		Veiculo veiculoAtualizado = repositorioVeiculo.save(veiculo);

		// Adicionar links HATEOAS
		adicionadorLinkVeiculos.adicionarLink(veiculoAtualizado);

		// Criar link HATEOAS para o veículo atualizado
		Link selfLink = Link.of(String.format("/veiculos/%d", veiculoAtualizado.getId())).withSelfRel();

		// Criar instância de EntityModel com os links
		EntityModel<Veiculo> resource = EntityModel.of(veiculoAtualizado).add(selfLink);

		return new ResponseEntity<>(resource, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> excluirVeiculo(@PathVariable Long id) {
		try {
			// Lógica para excluir o veículo
			repositorioVeiculo.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}
