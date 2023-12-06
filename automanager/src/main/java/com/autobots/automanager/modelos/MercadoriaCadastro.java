package com.autobots.automanager.modelos;

import com.autobots.automanager.entidades.Mercadoria;

import java.util.Date;

import lombok.Data;

@Data
public class MercadoriaCadastro {
    private Date validade;
    private Date fabricao;
    private String nome;
    private long quantidade;
    private double valor;
    private String descricao;

    public Mercadoria toMercadoria() {
        Mercadoria mercadoria = new Mercadoria();
        mercadoria.setValidade(this.validade);
        mercadoria.setFabricao(this.fabricao);
        mercadoria.setNome(this.nome);
        mercadoria.setQuantidade(this.quantidade);
        mercadoria.setValor(this.valor);
        mercadoria.setDescricao(this.descricao);
        return mercadoria;
    }
}
