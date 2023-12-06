package com.autobots.automanager.controles;

import com.autobots.automanager.modelos.AdicionarLinkMercadoria;
import com.autobots.automanager.modelos.MercadoriaCadastro;
import com.autobots.automanager.repositorios.MercadoriaRepositorio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Mercadoria;

import org.springframework.hateoas.CollectionModel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mercadorias")
public class MercadoriaControle {

    @Autowired
    private MercadoriaRepositorio repositorioMercadoria;

    @Autowired
    private AdicionarLinkMercadoria adicionadorLinkMercadorias;

    @PostMapping
    public ResponseEntity<EntityModel<Mercadoria>> cadastrarMercadoria(@RequestBody MercadoriaCadastro mercadoriaCadastro) {
        try {
            // Converter o DTO para a entidade
            Mercadoria novaMercadoria = mercadoriaCadastro.toMercadoria();

            // Lógica para cadastrar a nova mercadoria
            Mercadoria mercadoriaCadastrada = repositorioMercadoria.save(novaMercadoria);

            // Criar link HATEOAS para a nova mercadoria
            adicionadorLinkMercadorias.adicionarLink(mercadoriaCadastrada);

            // Criar instância de EntityModel com os links
            EntityModel<Mercadoria> resource = EntityModel.of(mercadoriaCadastrada);

            return new ResponseEntity<>(resource, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Mercadoria>> obterMercadoria(@PathVariable Long id) {
        Optional<Mercadoria> optionalMercadoria = repositorioMercadoria.findById(id);
        if (optionalMercadoria.isPresent()) {
            Mercadoria mercadoria = optionalMercadoria.get();

            // Adicionar links HATEOAS
            adicionadorLinkMercadorias.adicionarLink(mercadoria);

            // Criar link HATEOAS para a mercadoria
            Link selfLink = Link.of(String.format("/mercadorias/%d", mercadoria.getId())).withSelfRel();
            EntityModel<Mercadoria> resource = EntityModel.of(mercadoria, selfLink);

            return new ResponseEntity<>(resource, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public CollectionModel<EntityModel<Mercadoria>> obterMercadorias() {
        List<Mercadoria> mercadorias = repositorioMercadoria.findAll();
        List<EntityModel<Mercadoria>> mercadoriasDTO = mercadorias.stream()
                .map(mercadoria -> {
                    adicionadorLinkMercadorias.adicionarLink(mercadoria);
                    return EntityModel.of(mercadoria);
                })
                .collect(Collectors.toList());

        Link linkSelf = Link.of("/mercadorias").withSelfRel();
        Link linkPost = Link.of("/mercadorias").withRel("post");

        return CollectionModel.of(mercadoriasDTO, linkSelf, linkPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Mercadoria>> atualizarMercadoria(@PathVariable Long id, @RequestBody MercadoriaCadastro mercadoriaCadastro) {
        try {
            // Verificar se a mercadoria existe
            Optional<Mercadoria> optionalMercadoria = repositorioMercadoria.findById(id);
            if (optionalMercadoria.isPresent()) {
                Mercadoria mercadoriaExistente = optionalMercadoria.get();

                // Atualizar os dados da mercadoria
                mercadoriaExistente.setNome(mercadoriaCadastro.getNome());
                mercadoriaExistente.setQuantidade(mercadoriaCadastro.getQuantidade());
                mercadoriaExistente.setValor(mercadoriaCadastro.getValor());
                mercadoriaExistente.setDescricao(mercadoriaCadastro.getDescricao());

                // Salvar a mercadoria atualizada
                Mercadoria mercadoriaAtualizada = repositorioMercadoria.save(mercadoriaExistente);

                // Adicionar links HATEOAS
                adicionadorLinkMercadorias.adicionarLink(mercadoriaAtualizada);

                // Criar link HATEOAS para a mercadoria atualizada
                Link selfLink = Link.of(String.format("/mercadorias/%d", mercadoriaAtualizada.getId())).withSelfRel();
                EntityModel<Mercadoria> resource = EntityModel.of(mercadoriaAtualizada, selfLink);

                return new ResponseEntity<>(resource, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirMercadoria(@PathVariable Long id) {
        // Verificar se a mercadoria existe
        if (repositorioMercadoria.existsById(id)) {
            // Excluir a mercadoria
            repositorioMercadoria.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
