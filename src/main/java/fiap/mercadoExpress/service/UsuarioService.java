package fiap.mercadoExpress.service;

import fiap.mercadoExpress.model.Usuario;
import fiap.mercadoExpress.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public Usuario cadastrar(String nome, String email, String senha) {
        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }
        return repository.save(new Usuario(null, nome, email, encoder.encode(senha), "USER"));
    }
}
