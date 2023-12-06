package com.autobots.automanager.controles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.modelos.AdicionadorLinkUsuario;
import com.autobots.automanager.modelos.UsuarioAtualizador;
import com.autobots.automanager.modelos.UsuarioCadastro;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/usuarios")
public class UsuarioControle {

	@Autowired
	private UsuarioCadastro usuarioCadastro;

	@Autowired
	private UsuarioRepositorio usuarioRepositorio;

	@Autowired
	private UsuarioAtualizador usuarioAtualizacao;
	
	@Autowired
    private AdicionadorLinkUsuario adicionadorLinkUsuario; // Adicione esta linha
	
	


	@PostMapping
    public ResponseEntity<EntityModel<Usuario>> cadastrarUsuario(@RequestBody Usuario usuario) {
        try {
            // Lógica para cadastrar o novo usuário
            Usuario novoUsuario = usuarioCadastro.cadastrarNovoUsuario(usuario);

            // Adicionar links HATEOAS
            adicionadorLinkUsuario.adicionarLink(novoUsuario);

            // Criar link HATEOAS para o novo usuário
            Link selfLink = Link.of(String.format("/usuarios/%d", novoUsuario.getId())).withSelfRel();
            EntityModel<Usuario> resource = EntityModel.of(novoUsuario, selfLink);

            return new ResponseEntity<>(resource, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

	@PutMapping("/{id}")
	public ResponseEntity<EntityModel<Usuario>> atualizarUsuario(@PathVariable Long id,
			@RequestBody Usuario usuarioAtualizado) {
		log.info("ID recebido no controlador: {}", id);
		try {

			// Lógica para atualizar o usuário
			Usuario usuarioAtualizadoSalvo = usuarioAtualizacao.atualizarUsuario(id, usuarioAtualizado);
			System.out.println("o ID é:" + id);

			// Criar link HATEOAS para o usuário atualizado
			Link selfLink = Link.of(String.format("/usuarios/%d", usuarioAtualizadoSalvo.getId())).withSelfRel();
			EntityModel<Usuario> resource = EntityModel.of(usuarioAtualizadoSalvo, selfLink);

			return new ResponseEntity<>(resource, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obterUsuarioPorId(@PathVariable Long id) {
        try {
            // Lógica para obter o usuário pelo ID
            Usuario usuario = usuarioRepositorio.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            // Adicionar links HATEOAS
            adicionadorLinkUsuario.adicionarLink(usuario);

            // Criar link HATEOAS para o usuário
            Link selfLink = Link.of(String.format("/usuarios/%d", usuario.getId())).withSelfRel();
            EntityModel<Usuario> resource = EntityModel.of(usuario, selfLink);

            return new ResponseEntity<>(resource, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> excluirUsuario(@PathVariable Long id) {
		try {
			// Lógica para excluir o usuário
			usuarioRepositorio.deleteById(id);

			// Retornar uma resposta vazia com status 204 (No Content) indicando sucesso
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			// Se ocorrer um erro ao excluir, retornar status 400 (Bad Request)
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			// Se ocorrer outro erro, retornar status 500 (Internal Server Error)
			return ResponseEntity.status(500).build();
		}
	}

}
