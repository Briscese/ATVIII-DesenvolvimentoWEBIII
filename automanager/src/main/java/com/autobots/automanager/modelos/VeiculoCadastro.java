package com.autobots.automanager.modelos;

import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.enumeracoes.TipoVeiculo;
import lombok.Data;

@Data
public class VeiculoCadastro {
    private TipoVeiculo tipo;
    private String modelo;
    private String placa;
    private Usuario proprietario;

    public Veiculo toVeiculo() {
        Veiculo veiculo = new Veiculo();
        veiculo.setTipo(this.tipo);
        veiculo.setModelo(this.modelo);
        veiculo.setPlaca(this.placa);
        veiculo.setProprietario(this.proprietario);
        return veiculo;
    }
}
