package fiap.mercadoExpress.controller;

import fiap.mercadoExpress.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    @GetMapping({"/", "/index", "/index.html"})
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/cadastro")
    public String cadastroForm() {
        return "auth/cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@RequestParam String nome,
                            @RequestParam String email,
                            @RequestParam String senha,
                            Model model, RedirectAttributes ra) {
        if (nome.isBlank() || email.isBlank() || senha.length() < 6) {
            model.addAttribute("erro", "Preencha todos os campos (senha com no mínimo 6 caracteres).");
            return "auth/cadastro";
        }
        try {
            usuarioService.cadastrar(nome.trim(), email.trim().toLowerCase(), senha);
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "auth/cadastro";
        }
        ra.addFlashAttribute("cadastrado", true);
        return "redirect:/login?cadastrado=true";
    }
}
