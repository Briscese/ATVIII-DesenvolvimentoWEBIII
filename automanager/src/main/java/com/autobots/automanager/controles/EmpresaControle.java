package com.autobots.automanager.controles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.modelos.EmpresaAtualizacao;
import com.autobots.automanager.modelos.EmpresaCadastro;
import com.autobots.automanager.modelos.EmpresaDTO;
import com.autobots.automanager.modelos.EmpresaSelecionador;
import com.autobots.automanager.modelos.EnderecoDTO;
import com.autobots.automanager.modelos.TelefoneDTO;
import com.autobots.automanager.repositorios.RepositorioEmpresa;

@RestController
@RequestMapping("/empresas")
public class EmpresaControle {

    @Autowired
    private EmpresaCadastro empresaCadastro;
    
    @Autowired
    private EmpresaAtualizacao empresaAtualizacao;
    
    @Autowired
    private RepositorioEmpresa repositorioEmpresa;
    
    @Autowired
    private EmpresaSelecionador empresaSelecionador;

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<EmpresaDTO>> obterEmpresa(@PathVariable Long id) {
        List<Empresa> empresas = repositorioEmpresa.findAll();
        Optional<Empresa> empresaOptional = empresaSelecionador.selecionar(empresas, id);

        if (empresaOptional.isPresent()) {
            Empresa empresaSelecionada = empresaOptional.get();
            EmpresaDTO empresaDTO = converterParaDTO(empresaSelecionada);

            // Criar links HATEOAS
            EntityModel<EmpresaDTO> resource = EntityModel.of(empresaDTO);

            // Link para obter a própria empresa
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).obterEmpresa(id)).withSelfRel();
            resource.add(selfLink);

            // Link para UPDATE
            Link updateLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).atualizarEmpresa(id, empresaDTO)).withRel("update");
            resource.add(updateLink);

            // Link para DELETE
            Link deleteLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).excluirEmpresa(id)).withRel("delete");
            resource.add(deleteLink);

            return new ResponseEntity<>(resource, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public CollectionModel<EntityModel<EmpresaDTO>> obterEmpresas() {
        List<Empresa> empresas = repositorioEmpresa.findAll();
        List<EntityModel<EmpresaDTO>> empresasDTO = empresas.stream()
                .map(empresa -> {
                    EmpresaDTO empresaDTO = converterParaDTO(empresa);
                    Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).obterEmpresa(empresa.getId())).withSelfRel();
                    return EntityModel.of(empresaDTO, selfLink);
                })
                .collect(Collectors.toList());

        Link linkSelf = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).obterEmpresas()).withSelfRel();
        Link linkPost = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).cadastrarEmpresa(null)).withRel("post");

        return CollectionModel.of(empresasDTO, linkSelf, linkPost);
    }
    
    private EmpresaDTO converterParaDTO(Empresa empresa) {
        EmpresaDTO empresaDTO = new EmpresaDTO();
        empresaDTO.setId(empresa.getId());
        empresaDTO.setRazaoSocial(empresa.getRazaoSocial());
        empresaDTO.setNomeFantasia(empresa.getNomeFantasia());
        empresaDTO.setCadastro(empresa.getCadastro());
        empresaDTO.setEndereco(converterEnderecoParaDTO(empresa.getEndereco()));
        empresaDTO.setTelefones(new HashSet<>(converterTelefonesParaDTO(empresa.getTelefones())));
        return empresaDTO;
    }

    
    private EnderecoDTO converterEnderecoParaDTO(Endereco endereco) {
		EnderecoDTO enderecoDTO = new EnderecoDTO();
		enderecoDTO.setId(endereco.getId());
		enderecoDTO.setEstado(endereco.getEstado());
		enderecoDTO.setCidade(endereco.getCidade());
		enderecoDTO.setBairro(endereco.getBairro());
		enderecoDTO.setRua(endereco.getRua());
		enderecoDTO.setNumero(endereco.getNumero());
		enderecoDTO.setCodigoPostal(endereco.getCodigoPostal());
		enderecoDTO.setInformacoesAdicionais(endereco.getInformacoesAdicionais());
		return enderecoDTO;
	}

    private List<TelefoneDTO> converterTelefonesParaDTO(Set<Telefone> telefones) {
        List<TelefoneDTO> telefonesDTO = new ArrayList<>();
        for (Telefone telefone : telefones) {
            TelefoneDTO telefoneDTO = new TelefoneDTO();
            telefoneDTO.setId(telefone.getId());
            telefoneDTO.setDdd(telefone.getDdd());
            telefoneDTO.setNumero(telefone.getNumero());
            telefonesDTO.add(telefoneDTO);
        }
        return telefonesDTO;
    }


    @PostMapping
    public ResponseEntity<?> cadastrarEmpresa(@RequestBody Empresa empresa) {
        try {
            Empresa novaEmpresa = empresaCadastro.cadastrarNovaEmpresa(empresa);

            // Criar link HATEOAS para a nova empresa
            // (Aqui você pode adicionar links conforme necessário)

            return new ResponseEntity<>(novaEmpresa, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<EmpresaDTO>> atualizarEmpresa(@PathVariable Long id, @RequestBody EmpresaDTO empresaDTO) {
        // Lógica para atualizar a empresa
        Empresa empresa = repositorioEmpresa.getById(id);
        if (empresa != null) {
            try {
                Empresa empresaAtualizada = empresaAtualizacao.atualizarEmpresa(empresa, empresaDTO.toEmpresa());

                // Criar link HATEOAS para a empresa atualizada
                Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).obterEmpresa(id)).withSelfRel();

                EntityModel<EmpresaDTO> resource = EntityModel.of(empresaDTO, selfLink);
                return new ResponseEntity<>(resource, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirEmpresa(@PathVariable Long id) {
        Optional<Empresa> empresaOptional = repositorioEmpresa.findById(id);

        if (empresaOptional.isPresent()) {
            Empresa empresa = empresaOptional.get();
            repositorioEmpresa.delete(empresa);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
