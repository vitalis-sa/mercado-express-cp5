package fiap.mercadoExpress.config;

import fiap.mercadoExpress.service.UsuarioDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain seguranca(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .userDetailsService(usuarioDetailsService)
            .authorizeHttpRequests(auth -> auth
                // rotas públicas
                .requestMatchers("/", "/index", "/login", "/cadastro", "/css/**", "/error").permitAll()
                // somente admin: CRUD de escrita e dashboard gerencial
                .requestMatchers("/produtos/novo", "/produtos/salvar",
                        "/produtos/editar/**", "/produtos/deletar/**", "/dashboard").hasRole("ADMIN")
                // restante precisa estar logado (inclui /pagamentos e /sending-email via Basic Auth)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/produtos", true)
                .failureUrl("/login?erro=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
