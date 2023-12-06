package com.autobots.automanager.modelos;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.EmpresaControle;
import com.autobots.automanager.entidades.Empresa;

import java.util.List;

@Component
public class AdicionadorLinkEmpresas implements AdicionadorLink<Empresa> {

    @Override
    public void adicionarLink(List<Empresa> lista) {
        for (Empresa empresa : lista) {
            Long id = empresa.getId();
            Link linkProprio = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder
                            .methodOn(EmpresaControle.class)
                            .obterEmpresa(id))  // Corrigir aqui para chamar o método correto
                    .withSelfRel();
            empresa.add(linkProprio);
        }
    }

    @Override
    public void adicionarLink(Empresa objeto) {
        Long id = objeto.getId();
        Link linkProprio = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(EmpresaControle.class)
                        .obterEmpresa(id))  // Corrigir aqui para chamar o método correto
                .withRel("empresas");
        objeto.add(linkProprio);
    }
}
