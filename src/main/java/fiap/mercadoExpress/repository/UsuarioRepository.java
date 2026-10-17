package fiap.mercadoExpress.repository;

import fiap.mercadoExpress.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNome(String nome);
    boolean existsByEmail(String email);
}
