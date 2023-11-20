package com.autobots.automanager.modelos;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.entidades.Telefone;

public class EmpresaDTO {

    private Long id;
    private String razaoSocial;
    private String nomeFantasia;
    private Date cadastro;
    private EnderecoDTO endereco;
    private Set<TelefoneDTO> telefones;
    
    public Empresa toEmpresa() {
        Empresa empresa = new Empresa();
        empresa.setId(this.id);
        empresa.setRazaoSocial(this.razaoSocial);
        empresa.setNomeFantasia(this.nomeFantasia);
        empresa.setCadastro(this.cadastro);
        empresa.setEndereco(converterEnderecoDTOParaEndereco(this.endereco));
        empresa.setTelefones(converterTelefonesDTOParaTelefones(this.telefones));
        return empresa;
    }
    
    private Endereco converterEnderecoDTOParaEndereco(EnderecoDTO enderecoDTO) {
        Endereco endereco = new Endereco();
        endereco.setId(enderecoDTO.getId());
        endereco.setEstado(enderecoDTO.getEstado());
        endereco.setCidade(enderecoDTO.getCidade());
        endereco.setBairro(enderecoDTO.getBairro());
        endereco.setRua(enderecoDTO.getRua());
        endereco.setNumero(enderecoDTO.getNumero());
        endereco.setCodigoPostal(enderecoDTO.getCodigoPostal());
        endereco.setInformacoesAdicionais(enderecoDTO.getInformacoesAdicionais());
        return endereco;
    }
    
    private Set<Telefone> converterTelefonesDTOParaTelefones(Set<TelefoneDTO> telefonesDTO) {
        Set<Telefone> telefones = new HashSet<>();
        for (TelefoneDTO telefoneDTO : telefonesDTO) {
            Telefone telefone = new Telefone();
            telefone.setId(telefoneDTO.getId());
            telefone.setDdd(telefoneDTO.getDdd());
            telefone.setNumero(telefoneDTO.getNumero());
            telefones.add(telefone);
        }
        return telefones;
    }


    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRazaoSocial() {
		return razaoSocial;
	}
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}
	public String getNomeFantasia() {
		return nomeFantasia;
	}
	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}
	public Date getCadastro() {
		return cadastro;
	}
	public void setCadastro(Date cadastro) {
		this.cadastro = cadastro;
	}
	public EnderecoDTO getEndereco() {
		return endereco;
	}
	public void setEndereco(EnderecoDTO endereco) {
		this.endereco = endereco;
	}
	public Set<TelefoneDTO> getTelefones() {
		return telefones;
	}
	public void setTelefones(Set<TelefoneDTO> telefones) {
		this.telefones = telefones;
	}

}