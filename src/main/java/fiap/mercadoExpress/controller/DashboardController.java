package fiap.mercadoExpress.controller;

import fiap.mercadoExpress.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("d", service.gerar());
        return "dashboard/dashboard";
    }
}
