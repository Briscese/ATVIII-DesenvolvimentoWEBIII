package com.autobots.automanager.repositorios;

import com.autobots.automanager.entidades.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicosRepositorio extends JpaRepository<Servico, Long> {
   
}
