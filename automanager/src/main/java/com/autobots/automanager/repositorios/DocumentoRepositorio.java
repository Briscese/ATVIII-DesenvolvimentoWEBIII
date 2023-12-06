package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Usuario;

public interface DocumentoRepositorio extends JpaRepository<Documento, Long> {

	@Query("SELECT d FROM Documento d WHERE d.numero = :numero")
	Documento findDocumentoByNumero(@Param("numero") String numero);
	

	boolean existsByNumero(String numero);

	boolean existsByNumeroAndUsuarioNot(String numero, Usuario usuarioExistente);


	boolean existsByNumeroAndUsuarioIdNot(String numero, Long usuarioId);
}
