package com.autobots.automanager.modelos;

import com.autobots.automanager.controles.MercadoriaControle;
import com.autobots.automanager.entidades.Mercadoria;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdicionarLinkMercadoria implements AdicionadorLink<Mercadoria> {

    @Override
    public void adicionarLink(List<Mercadoria> lista) {
        for (Mercadoria mercadoria : lista) {
            Long id = mercadoria.getId();
            Link linkProprio = WebMvcLinkBuilder
                    .linkTo(WebMvcLinkBuilder
                            .methodOn(MercadoriaControle.class)
                            .obterMercadoria(id))
                    .withSelfRel();
            mercadoria.add(linkProprio);
        }
    }

    @Override
    public void adicionarLink(Mercadoria objeto) {
        Long id = objeto.getId();
        Link linkProprio = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(MercadoriaControle.class)
                        .obterMercadoria(id))
                .withRel("mercadorias");
        objeto.add(linkProprio);
    }
}
