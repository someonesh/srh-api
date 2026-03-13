package backend_java.backend_java.controller;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend_java.backend_java.model.Funcionario;
import backend_java.backend_java.service.FuncionarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {
    
    @Autowired
    private FuncionarioService funcionarioService;
    
    @GetMapping
    public ResponseEntity<List<Funcionario>> listarTodos() {
        return ResponseEntity.ok(funcionarioService.listarTodos());
    }
    
    @GetMapping("/ativos")
    public ResponseEntity<List<Funcionario>> listarAtivos() {
        return ResponseEntity.ok(funcionarioService.listarAtivos());
    }
    
    @GetMapping("/inativos")
    public ResponseEntity<List<Funcionario>> listarInativos() {
        return ResponseEntity.ok(funcionarioService.listarInativos());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(funcionarioService.buscarPorId(id));
    }
    
    // ENDPOINT DE CPF REMOVIDO - Não usamos mais CPF
    
    @GetMapping("/departamento/{departamento}")
    public ResponseEntity<List<Funcionario>> buscarPorDepartamento(@PathVariable String departamento) {
        return ResponseEntity.ok(funcionarioService.buscarPorDepartamento(departamento));
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Funcionario>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(funcionarioService.buscarPorNome(nome));
    }
    
    @PostMapping
    public ResponseEntity<Funcionario> criar(@Valid @RequestBody Funcionario funcionario) {
        return new ResponseEntity<>(funcionarioService.salvar(funcionario), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Funcionario> atualizar(
            @PathVariable Long id, 
            @Valid @RequestBody Funcionario funcionario) {
        return ResponseEntity.ok(funcionarioService.atualizar(id, funcionario));
    }
    
    @PatchMapping("/{id}/demitir")
    public ResponseEntity<Map<String, String>> demitir(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate dataDemissao) {
        funcionarioService.demitir(id, dataDemissao);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Funcionário demitido com sucesso");
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<Map<String, String>> reativar(@PathVariable Long id) {
        funcionarioService.reativar(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Funcionário reativado com sucesso");
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        funcionarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/estatisticas/departamento/{departamento}")
    public ResponseEntity<Map<String, Object>> estatisticasDepartamento(@PathVariable String departamento) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("departamento", departamento);
        stats.put("totalFuncionarios", funcionarioService.contarPorDepartamento(departamento));
        stats.put("mediaSalarial", funcionarioService.calcularMediaSalarialPorDepartamento(departamento));
        return ResponseEntity.ok(stats);
    }
}