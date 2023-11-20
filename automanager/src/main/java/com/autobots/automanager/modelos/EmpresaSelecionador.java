package com.autobots.automanager.modelos;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Empresa;

@Component
public class EmpresaSelecionador {

	public Optional<Empresa> selecionar(List<Empresa> empresas, Long id) {
		return empresas.stream().filter(empresa -> empresa.getId().equals(id)).findFirst();
	}

}