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

import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@Component
public class UsuarioAtualizador {

    @Autowired
    private TelefoneRepositorio telefoneRepositorio;

    @Autowired
    private DocumentoRepositorio documentoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Transactional
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        log.info("ID recebido no serviço: {}", id);

        // Verificar se o usuário com o ID fornecido existe no banco de dados
        Usuario usuarioExistente = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Atualizar os dados do usuário existente com os dados do usuário atualizado
        usuarioExistente.setNome(usuarioAtualizado.getNome());

        // Atualizar telefones existentes com base nos telefones do usuário atualizado
        atualizarTelefones(usuarioAtualizado.getTelefones(), usuarioExistente);

        // Atualizar documentos existentes com base nos documentos do usuário atualizado
        atualizarDocumentos(usuarioAtualizado.getDocumentos(), usuarioExistente);

        // Salvar as atualizações no banco de dados
        return usuarioRepositorio.save(usuarioExistente);
    }

    private void atualizarTelefones(Set<Telefone> telefonesAtualizados, Usuario usuario) {
        if (telefonesAtualizados != null) {
            // Limpar telefones existentes para garantir que apenas os telefones do usuário atualizado estejam associados
            usuario.getTelefones().clear();

            for (Telefone telefoneAtualizado : telefonesAtualizados) {
                if (telefoneAtualizado != null) {
                    // Adicionar telefone atualizado à lista de telefones do usuário
                    usuario.getTelefones().add(telefoneAtualizado);
                }
            }
        }
    }

    private void atualizarDocumentos(Set<Documento> documentosAtualizados, Usuario usuario) {
        if (documentosAtualizados != null) {
            // Limpar documentos existentes para garantir que apenas os documentos do usuário atualizado estejam associados
            usuario.getDocumentos().clear();

            for (Documento documentoAtualizado : documentosAtualizados) {
                // Adicionar documento atualizado à lista de documentos do usuário
                usuario.getDocumentos().add(documentoAtualizado);
            }
        }
    }
}
