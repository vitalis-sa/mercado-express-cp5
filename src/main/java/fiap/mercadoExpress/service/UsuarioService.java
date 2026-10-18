package fiap.mercadoExpress.service;

import fiap.mercadoExpress.mapper.PagamentoMapper;
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
    private final PagamentoMapper mapper;

    public Usuario cadastrar(String nome, String email, String senha) {
        repository.findByEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        });
        return repository.save(mapper.toUsuario(nome, email, encoder.encode(senha)));
    }
}
