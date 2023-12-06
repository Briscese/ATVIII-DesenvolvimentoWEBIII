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
import com.autobots.automanager.modelos.AdicionadorLinkEmpresas;
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
    
    @Autowired
    private AdicionadorLinkEmpresas adicionadorLinkEmpresas;



    
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<EmpresaDTO>> obterEmpresa(@PathVariable Long id) {
        try {
            Empresa empresa = repositorioEmpresa.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));

            // Adicionar links HATEOAS
            adicionadorLinkEmpresas.adicionarLink(empresa);

            // Criar link HATEOAS para a empresa
            Link selfLink = Link.of(String.format("/empresas/%d", empresa.getId())).withSelfRel();
            EntityModel<EmpresaDTO> resource = EntityModel.of(converterParaDTO(empresa), selfLink);

            return new ResponseEntity<>(resource, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping
    public CollectionModel<EntityModel<EmpresaDTO>> obterEmpresas() {
        List<Empresa> empresas = repositorioEmpresa.findAll();
        List<EntityModel<EmpresaDTO>> empresasDTO = empresas.stream()
                .map(empresa -> {
                    EmpresaDTO empresaDTO = converterParaDTO(empresa);
                    Link selfLink = Link.of(String.format("/empresas/%d", empresa.getId())).withSelfRel();
                    return EntityModel.of(empresaDTO, selfLink);
                })
                .collect(Collectors.toList());

        Link linkSelf = Link.of("/empresas").withSelfRel();
        Link linkPost = Link.of("/empresas").withRel("post");

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
    public ResponseEntity<EntityModel<Empresa>> cadastrarEmpresa(@RequestBody EmpresaDTO empresaDTO) {
        try {
            // Converter o DTO para a entidade
            Empresa novaEmpresa = empresaDTO.toEmpresa();

            // Lógica para cadastrar a nova empresa
            Empresa empresaCadastrada = empresaCadastro.cadastrarNovaEmpresa(novaEmpresa, null); // você precisa fornecer a lista de IDs de usuários

            // Criar link HATEOAS para a nova empresa
            Link selfLink = Link.of(String.format("/empresas/%d", empresaCadastrada.getId())).withSelfRel();
            EntityModel<Empresa> resource = EntityModel.of(empresaCadastrada, selfLink);

            return new ResponseEntity<>(resource, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    private Empresa converterDTOparaEntidade(EmpresaDTO empresaDTO) {
        return empresaDTO.toEmpresa();
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
