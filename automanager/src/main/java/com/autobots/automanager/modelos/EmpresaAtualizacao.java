package com.autobots.automanager.modelos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.TelefoneRepositorio;

import java.util.HashSet;
import java.util.Set;

@Component
public class EmpresaAtualizacao {

	@Autowired
	private RepositorioEmpresa repositorioEmpresa;

	@Autowired
	private TelefoneRepositorio telefoneRepositorio;

	@Transactional
	public Empresa atualizarEmpresa(Empresa empresaExistente, Empresa empresaAtualizada) {
		validarEmpresa(empresaAtualizada);

		try {
			// Atualizar os campos da empresa existente com os dados da empresa atualizada
			empresaExistente.setRazaoSocial(empresaAtualizada.getRazaoSocial());
			empresaExistente.setNomeFantasia(empresaAtualizada.getNomeFantasia());
			empresaExistente.setCadastro(empresaAtualizada.getCadastro());
			empresaExistente.setEndereco(empresaAtualizada.getEndereco());

			// Atualizar ou adicionar telefones
			Set<Telefone> telefonesAtualizados = new HashSet<>();
			for (Telefone telefone : empresaAtualizada.getTelefones()) {
				// Verificar se o telefone já existe no banco de dados
				if (!telefoneRepositorio.existsByNumero(telefone.getNumero())) {
					// Se não existe, salvar antes de associar à empresa
					telefone = telefoneRepositorio.save(telefone);
				}
				telefonesAtualizados.add(telefone);
			}
			// Limpar os telefones existentes e adicionar os telefones atualizados
			Set<Telefone> telefonesExistente = empresaExistente.getTelefones();
			telefonesExistente.clear();
			telefonesExistente.addAll(telefonesAtualizados);

			// Salvar a empresa atualizada
			empresaExistente = repositorioEmpresa.save(empresaExistente);

			// Retornar a empresa atualizada
			return empresaExistente;
		} catch (Exception e) {
			// Lançar uma exceção com mensagem informativa em caso de erro na atualização
			throw new RuntimeException("Erro ao atualizar a empresa. Detalhes: " + e.getMessage());
		}
	}

	private void validarEmpresa(Empresa empresa) {
		if (empresa.getRazaoSocial() == null || empresa.getRazaoSocial().trim().isEmpty()) {
			throw new RuntimeException("A razão social da empresa não pode estar vazia.");
		}
	}
}
