package backend_java.backend_java.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend_java.backend_java.model.FolhaPagamento;
import backend_java.backend_java.service.FolhaPagamentoService;

@RestController
@RequestMapping("/api/folha")
public class FolhaPagamentoController {
    
    @Autowired
    private FolhaPagamentoService folhaPagamentoService;
    
    // ==============================================
    // PROCESSAMENTO DA FOLHA
    // ==============================================
    
    /**
     * Processar folha de pagamento de todos os funcionários ativos
     */
    @PostMapping("/processar")
    public ResponseEntity<?> processarFolhaMensal(
            @RequestParam Integer mes,
            @RequestParam Integer ano) {
        
        try {
            List<FolhaPagamento> folhas = folhaPagamentoService.processarFolhaMensal(mes, ano);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Folha processada com sucesso!");
            response.put("periodo", String.format("%02d/%d", mes, ano));
            response.put("totalFuncionarios", folhas.size());
            response.put("folhas", folhas);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Processar folha de um funcionário específico
     */
    @PostMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<?> processarFolhaFuncionario(
            @PathVariable Long funcionarioId,
            @RequestParam Integer mes,
            @RequestParam Integer ano) {
        
        try {
            FolhaPagamento folha = folhaPagamentoService.processarFolhaFuncionario(funcionarioId, mes, ano);
            return ResponseEntity.ok(folha);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==============================================
    // CONSULTAS
    // ==============================================
    
    /**
     * Listar todas as folhas de um período
     */
    @GetMapping("/periodo")
    public ResponseEntity<?> listarPorPeriodo(
            @RequestParam Integer mes,
            @RequestParam Integer ano) {
        
        try {
            List<FolhaPagamento> folhas = folhaPagamentoService.listarPorPeriodo(mes, ano);
            return ResponseEntity.ok(folhas);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Listar folhas de um funcionário
     */
    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<?> listarPorFuncionario(@PathVariable Long funcionarioId) {
        try {
            List<FolhaPagamento> folhas = folhaPagamentoService.listarPorFuncionario(funcionarioId);
            return ResponseEntity.ok(folhas);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Buscar folha por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            FolhaPagamento folha = folhaPagamentoService.buscarPorId(id);
            return ResponseEntity.ok(folha);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==============================================
    // OPERAÇÕES NA FOLHA
    // ==============================================
    
    /**
     * Adicionar comissão a uma folha existente
     */
    @PatchMapping("/{id}/adicionar-comissao")
    public ResponseEntity<?> adicionarComissao(
            @PathVariable Long id,
            @RequestParam Double valor) {
        
        try {
            FolhaPagamento folha = folhaPagamentoService.adicionarComissao(id, valor);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Comissão adicionada com sucesso!");
            response.put("folhaId", folha.getId());
            response.put("funcionario", folha.getFuncionario().getNomeCompleto());
            response.put("periodo", folha.getPeriodo());
            response.put("salarioBase", folha.getSalarioBase());
            response.put("comissao", folha.getComissao());
            response.put("salarioBruto", folha.getSalarioBruto());
            response.put("inss", folha.getInss());
            response.put("salarioLiquido", folha.getSalarioLiquido());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Marcar folha como paga
     */
    @PatchMapping("/{id}/marcar-pago")
    public ResponseEntity<?> marcarComoPago(@PathVariable Long id) {
        try {
            FolhaPagamento folha = folhaPagamentoService.marcarComoPago(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Folha marcada como PAGA!");
            response.put("folhaId", folha.getId());
            response.put("status", folha.getStatus());
            response.put("dataPagamento", folha.getDataPagamento());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Cancelar folha
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarFolha(@PathVariable Long id) {
        try {
            FolhaPagamento folha = folhaPagamentoService.cancelarFolha(id);
            
            return ResponseEntity.ok(Map.of(
                "message", "Folha cancelada!",
                "folhaId", folha.getId(),
                "status", folha.getStatus()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==============================================
    // RELATÓRIOS
    // ==============================================
    
    /**
     * Gerar relatório completo da folha
     */
    @GetMapping("/relatorio")
    public ResponseEntity<?> gerarRelatorio(
            @RequestParam Integer mes,
            @RequestParam Integer ano) {
        
        try {
            Map<String, Object> relatorio = folhaPagamentoService.gerarRelatorio(mes, ano);
            return ResponseEntity.ok(relatorio);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Estatísticas da folha
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<?> estatisticas(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano) {
        
        try {
            if (mes == null) mes = LocalDate.now().getMonthValue();
            if (ano == null) ano = LocalDate.now().getYear();
            
            Map<String, Object> stats = folhaPagamentoService.gerarEstatisticas(mes, ano);
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==============================================
    // ENDPOINTS PARA INTEGRAÇÃO COM SISTEMA FINANCEIRO
    // ==============================================
    
    /**
     * API para sistema financeiro consultar folhas a pagar
     */
    @GetMapping("/financeiro/pendentes")
    public ResponseEntity<?> folhasPendentes(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano) {
        
        try {
            if (mes == null) mes = LocalDate.now().getMonthValue();
            if (ano == null) ano = LocalDate.now().getYear();
            
            List<FolhaPagamento> pendentes = folhaPagamentoService.buscarPendentes(mes, ano);
            
            double totalPagar = pendentes.stream()
                .mapToDouble(FolhaPagamento::getSalarioLiquido)
                .sum();
            
            Map<String, Object> response = new HashMap<>();
            response.put("periodo", String.format("%02d/%d", mes, ano));
            response.put("totalFuncionarios", pendentes.size());
            response.put("totalPagar", String.format("MT %.2f", totalPagar));
            response.put("folhas", pendentes);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Confirmar pagamento (sistema financeiro confirma que pagou)
     */
    @PatchMapping("/financeiro/confirmar-pagamento/{id}")
    public ResponseEntity<?> confirmarPagamento(@PathVariable Long id) {
        try {
            FolhaPagamento folha = folhaPagamentoService.marcarComoPago(id);
            
            return ResponseEntity.ok(Map.of(
                "message", "Pagamento confirmado pelo sistema financeiro",
                "folhaId", folha.getId(),
                "funcionario", folha.getFuncionario().getNomeCompleto(),
                "valorPago", String.format("MT %.2f", folha.getSalarioLiquido()),
                "dataPagamento", folha.getDataPagamento()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
