package com.autobots.automanager.modelos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.TelefoneRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

import java.sql.Date;
import java.time.LocalDate; // Importe LocalDate do pacote java.time
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class EmpresaCadastro {

	@Autowired
	private RepositorioEmpresa repositorioEmpresa;

	@Autowired
	private TelefoneRepositorio telefoneRepositorio;

	@Autowired
	private UsuarioRepositorio usuarioRepositorio;

	@Transactional
	public Empresa cadastrarNovaEmpresa(Empresa empresa, List<Long> idsUsuarios) {
		validarEmpresa(empresa);

		// Validar e salvar telefones antes de associá-los à empresa
		Set<Telefone> telefonesSalvos = new HashSet<>();
		for (Telefone telefone : empresa.getTelefones()) {
			// Verificar se o telefone já existe no banco de dados
			if (!telefoneRepositorio.existsByNumero(telefone.getNumero())) {
				// Se não existe, salvar antes de associar à empresa
				telefone = telefoneRepositorio.save(telefone);
			}
			telefonesSalvos.add(telefone);
		}

		// Associar telefones salvos à empresa
		empresa.setTelefones(telefonesSalvos);

		// Definir a data de cadastro usando LocalDate
		empresa.setCadastro(Date.valueOf(LocalDate.now())); // Converte LocalDate para Date

		// Salvar a empresa
		empresa = repositorioEmpresa.save(empresa);

		// Associar usuários à empresa
		if (idsUsuarios != null) {
		    for (Long idUsuario : idsUsuarios) {
		        Usuario usuario = usuarioRepositorio.findById(idUsuario)
		                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

		        // Associar o usuário à empresa
		        usuario.setEmpresa(empresa);
		        
		        // Adicionar o usuário à coleção de usuários da empresa
		        empresa.getUsuarios().add(usuario);
		    }
		}

		// Retornar a empresa salva
		return empresa;
	}

	private void validarEmpresa(Empresa empresa) {
		// Verificar se a empresa já existe pelo nome
		Empresa empresaExistente = repositorioEmpresa.findByRazaoSocial(empresa.getRazaoSocial());
		if (empresaExistente != null && !empresaExistente.getId().equals(empresa.getId())) {
			throw new RuntimeException("Já existe uma empresa com a mesma razão social.");
		}

		validarTelefones(empresa.getTelefones());
		validarEndereco(empresa.getEndereco());
	}

	private void validarTelefones(Set<Telefone> telefones) {
		for (Telefone telefone : telefones) {
			// Verificar se o telefone já existe no banco de dados
			if (telefoneRepositorio.existsByNumero(telefone.getNumero())) {
				throw new RuntimeException("Este telefone já está associado a outra empresa.");
			}
		}
	}

	private void validarEndereco(com.autobots.automanager.entidades.Endereco endereco) {
		if (endereco == null) {
			throw new RuntimeException("A empresa deve ter um endereço.");
		}
	}
}
