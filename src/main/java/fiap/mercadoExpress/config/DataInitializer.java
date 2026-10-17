package fiap.mercadoExpress.config;

import fiap.mercadoExpress.model.Produto;
import fiap.mercadoExpress.model.Usuario;
import fiap.mercadoExpress.repository.ProdutoRepository;
import fiap.mercadoExpress.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Dados iniciais: usuários de teste (ADMIN e USER) e alguns produtos. */
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final PasswordEncoder encoder;

    @Bean
    CommandLineRunner seed() {
        return args -> {
            if (!usuarioRepository.existsByEmail("admin@mercado.com")) {
                usuarioRepository.save(new Usuario(null, "Administrador", "admin@mercado.com",
                        encoder.encode("admin123"), "ADMIN"));
            }
            if (!usuarioRepository.existsByEmail("user@mercado.com")) {
                usuarioRepository.save(new Usuario(null, "Cliente Teste", "user@mercado.com",
                        encoder.encode("user123"), "USER"));
            }
            if (produtoRepository.count() == 0) {
                produtoRepository.save(new Produto(null, "Banana Prata", "Alimento", "Hortifruti", "Médio", 7.50, 4.00, 100));
                produtoRepository.save(new Produto(null, "Detergente Neutro", "Limpeza", "Limpeza", "Pequeno", 3.20, 1.80, 80));
                produtoRepository.save(new Produto(null, "Meia Esportiva", "Outro", "Vestuário", "Único", 19.90, 9.00, 40));
                produtoRepository.save(new Produto(null, "Suco de Laranja 1L", "Bebida", "Bebidas", "Grande", 8.90, 5.00, 60));
            }
        };
    }
}
