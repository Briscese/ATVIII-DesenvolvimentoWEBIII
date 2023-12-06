package com.autobots.automanager.modelos;

import com.autobots.automanager.controles.ServicosControle;
import com.autobots.automanager.entidades.Servico;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdicionarLinkServicos implements AdicionadorLink<Servico> {

    @Override
    public void adicionarLink(List<Servico> lista) {
        for (Servico servico : lista) {
            Long id = servico.getId();
            Link linkProprio = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder
                            .methodOn(ServicosControle.class)
                            .obterServicoPorId(id))
                    .withSelfRel();
            servico.add(linkProprio);
        }
    }

    @Override
    public void adicionarLink(Servico objeto) {
        Long id = objeto.getId();
        Link linkProprio = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(ServicosControle.class)
                        .obterServicoPorId(id))
                .withRel("servicos");
        objeto.add(linkProprio);
    }
}
