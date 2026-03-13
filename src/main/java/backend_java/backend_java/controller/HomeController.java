package backend_java.backend_java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import backend_java.backend_java.service.FuncionarioService;

@Controller
public class HomeController {
    
    @Autowired
    private FuncionarioService funcionarioService;
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalFuncionarios", funcionarioService.listarTodos().size());
        model.addAttribute("funcionariosAtivos", funcionarioService.listarAtivos().size());
        model.addAttribute("funcionariosInativos", funcionarioService.listarInativos().size());
        return "home";
    }
}