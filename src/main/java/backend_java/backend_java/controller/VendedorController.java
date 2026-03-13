package backend_java.backend_java.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend_java.backend_java.model.Vendedor;
import backend_java.backend_java.service.VendedorService;

@RestController
@RequestMapping("/api/vendedores")
public class VendedorController {
    
    @Autowired
    private VendedorService vendedorService;
    
    /**
     * Endpoint para consulta de vendedores - SEM TOKEN
     * 
     * @param codigo Código do vendedor (opcional)
     * @return Lista de vendedores ou vendedor específico
     */
    @GetMapping
    public ResponseEntity<?> consultarVendedores(
            @RequestParam(required = false) String codigo) {
        
        // Se código foi fornecido, buscar vendedor específico
        if (codigo != null && !codigo.isEmpty()) {
            Optional<Vendedor> vendedorOpt = vendedorService.buscarPorCodigoAtivo(codigo);
            
            if (vendedorOpt.isPresent()) {
                return ResponseEntity.ok(vendedorOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Vendedor não encontrado ou inativo"));
            }
        }
        
        // Retornar lista de todos ativos
        List<Vendedor> vendedores = vendedorService.listarVendedoresAtivos();
        return ResponseEntity.ok(vendedores);
    }
    
    /**
     * Lista todos os vendedores (incluindo inativos)
     */
    @GetMapping("/todos")
    public ResponseEntity<List<Vendedor>> listarTodos() {
        return ResponseEntity.ok(vendedorService.listarTodos());
    }
    
    /**
     * Busca vendedor por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vendedor> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vendedorService.buscarPorId(id));
    }
    
    /**
     * Criar novo vendedor
     */
    @PostMapping
    public ResponseEntity<Vendedor> criar(@RequestBody Vendedor vendedor) {
        return new ResponseEntity<>(vendedorService.salvar(vendedor), HttpStatus.CREATED);
    }
    
    /**
     * Atualizar vendedor
     */
    @PutMapping("/{id}")
    public ResponseEntity<Vendedor> atualizar(
            @PathVariable Long id, 
            @RequestBody Vendedor vendedor) {
        return ResponseEntity.ok(vendedorService.atualizar(id, vendedor));
    }
    
    /**
     * Ativar vendedor
     */
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Map<String, String>> ativar(@PathVariable Long id) {
        vendedorService.ativar(id);
        return ResponseEntity.ok(Map.of("message", "Vendedor ativado com sucesso"));
    }
    
    /**
     * Inativar vendedor
     */
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Map<String, String>> inativar(@PathVariable Long id) {
        vendedorService.inativar(id);
        return ResponseEntity.ok(Map.of("message", "Vendedor inativado com sucesso"));
    }

}