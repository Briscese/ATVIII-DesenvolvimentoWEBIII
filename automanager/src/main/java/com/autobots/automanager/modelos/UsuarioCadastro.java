package com.autobots.automanager.modelos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.repositorios.TelefoneRepositorio;
import com.autobots.automanager.repositorios.DocumentoRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

import java.util.Set;

@Component
public class UsuarioCadastro {

    @Autowired
    private TelefoneRepositorio telefoneRepositorio;

    @Autowired
    private DocumentoRepositorio documentoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Transactional
    public Usuario cadastrarNovoUsuario(Usuario usuario) {
        validarUsuario(usuario);

        // Salvar o novo usuário
        return usuarioRepositorio.save(usuario);
    }

    private void validarUsuario(Usuario usuario) {
        // Exemplo de validação de telefones
        validarTelefones(usuario.getTelefones());

        // Exemplo de validação de documentos
        validarDocumentos(usuario.getDocumentos());

    }

    private void validarTelefones(Set<Telefone> telefones) {
        for (Telefone telefone : telefones) {
            // Verificar se o telefone já existe no banco de dados
            if (telefoneRepositorio.existsByNumero(telefone.getNumero())) {
                throw new RuntimeException("Este telefone já está associado a outro usuário.");
            }
        }
    }

    private void validarDocumentos(Set<Documento> documentos) {
        for (Documento documento : documentos) {
            // Verificar se o documento já existe no banco de dados
            if (documentoRepositorio.existsByNumero(documento.getNumero())) {
                throw new RuntimeException("Este documento já está associado a outro usuário.");
            }
        }
    }

}
