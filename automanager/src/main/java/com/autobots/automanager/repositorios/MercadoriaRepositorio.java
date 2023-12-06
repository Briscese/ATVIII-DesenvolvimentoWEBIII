package com.autobots.automanager.repositorios;

import com.autobots.automanager.entidades.Mercadoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MercadoriaRepositorio extends JpaRepository<Mercadoria, Long> {
   
}
