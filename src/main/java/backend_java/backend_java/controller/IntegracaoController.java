package backend_java.backend_java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend_java.backend_java.service.IntegracaoService;

@RestController
@RequestMapping("/api/integracao")
public class IntegracaoController {

    @Autowired
    private IntegracaoService integracaoService;

    // ✅ STATUS - Verifica se as APIs estão online
    @GetMapping("/status")
    public String status() {
        return integracaoService.testarConexaoSimples();
    }

    // ✅ TESTAR VENDAS - Mostra os dados da API de vendas
    @GetMapping("/testar/vendas")
    public String testarVendas() {
        return integracaoService.testarAPIVendas();
    }

    // ✅ TESTAR COMISSÕES - Mostra os dados da API de comissões
    @GetMapping("/testar/comissoes")
    public String testarComissoes() {
        return integracaoService.testarAPIComissoes();
    }

    // ✅ IMPORTAR - Busca vendas e cria comissões no banco
    @PostMapping("/importar")
    public String importarVendas() {
        return integracaoService.importarVendas(); // 🔥 NOME CORRETO (sem "ComLimpeza")
    }
}