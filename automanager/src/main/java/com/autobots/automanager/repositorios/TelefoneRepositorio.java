package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Usuario;

@Repository
public interface TelefoneRepositorio extends JpaRepository<Telefone, Long> {
	boolean existsByNumeroAndUsuarioNot(String numero, Usuario usuario);
	

	boolean existsByNumero(String numero);


	boolean existsByNumeroAndUsuarioIdNot(String numero, Long usuarioId);
}