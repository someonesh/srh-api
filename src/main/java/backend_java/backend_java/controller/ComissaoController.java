package backend_java.backend_java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import backend_java.backend_java.dto.ComissaoResponseDTO;
import backend_java.backend_java.dto.RegistroVendaDTO;
import backend_java.backend_java.model.Comissao;
import backend_java.backend_java.model.Funcionario;
import backend_java.backend_java.repository.ComissaoRepository;
import backend_java.backend_java.repository.FuncionarioRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comissoes")
@CrossOrigin(origins = "http://localhost:8080") // Permitir requisições do frontend
public class ComissaoController {
    
    @Autowired
    private ComissaoRepository comissaoRepository;
    
    @Autowired
    private FuncionarioRepository funcionarioRepository;
    
    // Endpoint para registrar uma venda e calcular comissão
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarComissao(@RequestBody RegistroVendaDTO registroDTO) {
        try {
            // Buscar funcionário
            Optional<Funcionario> funcionarioOpt = funcionarioRepository.findById(registroDTO.getFuncionarioId());
            
            if (funcionarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("erro", "Funcionário não encontrado"));
            }
            
            Funcionario funcionario = funcionarioOpt.get();
            
            // Verificar se é vendedor
            if (!"Vendas".equalsIgnoreCase(funcionario.getDepartamento())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("erro", "Funcionário não é um vendedor"));
            }
            
            // Obter valores para cálculo
            Double valorVenda = registroDTO.getValorVenda();
            Double percentual = funcionario.getPercentualComissao() != null ? 
                                funcionario.getPercentualComissao() : 5.0;
            Double meta = funcionario.getMetaMensal() != null ? 
                          funcionario.getMetaMensal() : 50000.0;
            
            // Calcular comissão
            Double valorComissao = valorVenda * (percentual / 100);
            Boolean metaAtingida = valorVenda >= meta;
            
            // Criar e salvar comissão
            Comissao comissao = new Comissao();
            comissao.setFuncionario(funcionario);
            comissao.setValorVenda(valorVenda);
            comissao.setPercentualComissao(percentual);
            comissao.setValorComissao(valorComissao);
            comissao.setMetaAtingida(metaAtingida);
            comissao.setMes(registroDTO.getMes());
            comissao.setAno(registroDTO.getAno());
            comissao.setObservacao(registroDTO.getObservacao());
            
            Comissao saved = comissaoRepository.save(comissao);
            
            // Preparar resposta
            ComissaoResponseDTO response = new ComissaoResponseDTO();
            response.setComissaoId(saved.getId());
            response.setFuncionarioId(funcionario.getId());
            response.setFuncionarioNome(funcionario.getNomeCompleto());
            response.setValorVenda(valorVenda);
            response.setPercentualComissao(percentual);
            response.setValorComissao(valorComissao);
            response.setMetaMensal(meta);
            response.setMetaAtingida(metaAtingida);
            response.setMes(registroDTO.getMes());
            response.setAno(registroDTO.getAno());
            response.setMensagem("Comissão registrada com sucesso");
            
            // Calcular bônus se solicitado
            if (registroDTO.getCalcularDetalhado() != null && 
                registroDTO.getCalcularDetalhado() && metaAtingida) {
                response.setBonus(valorComissao * 0.1); // 10% de bônus
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao registrar comissão: " + e.getMessage()));
        }
    }
    
    // Endpoint para calcular comissão sem registrar (pré-visualização)
    @PostMapping("/calcular")
    public ResponseEntity<?> calcularComissao(@RequestBody RegistroVendaDTO registroDTO) {
        try {
            Optional<Funcionario> funcionarioOpt = funcionarioRepository.findById(registroDTO.getFuncionarioId());
            
            if (funcionarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("erro", "Funcionário não encontrado"));
            }
            
            Funcionario funcionario = funcionarioOpt.get();
            
            Double valorVenda = registroDTO.getValorVenda();
            Double percentual = funcionario.getPercentualComissao() != null ? 
                                funcionario.getPercentualComissao() : 5.0;
            Double meta = funcionario.getMetaMensal() != null ? 
                          funcionario.getMetaMensal() : 50000.0;
            
            Double valorComissao = valorVenda * (percentual / 100);
            Boolean metaAtingida = valorVenda >= meta;
            
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("funcionarioId", funcionario.getId());
            resultado.put("funcionarioNome", funcionario.getNomeCompleto());
            resultado.put("valorVenda", valorVenda);
            resultado.put("percentual", percentual);
            resultado.put("valorComissao", valorComissao);
            resultado.put("meta", meta);
            resultado.put("metaAtingida", metaAtingida);
            
            if (registroDTO.getCalcularDetalhado() != null && 
                registroDTO.getCalcularDetalhado() && metaAtingida) {
                resultado.put("bonus", valorComissao * 0.1);
            }
            
            return ResponseEntity.ok(resultado);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", e.getMessage()));
        }
    }
    
    // Endpoint para listar comissões de um vendedor
    @GetMapping("/vendedor/{funcionarioId}")
    public ResponseEntity<?> listarComissoesPorVendedor(
            @PathVariable Long funcionarioId,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano) {
        
        try {
            List<Comissao> comissoes;
            
            if (mes != null && ano != null) {
                comissoes = comissaoRepository.findByFuncionarioIdAndMesAndAno(funcionarioId, mes, ano);
            } else {
                comissoes = comissaoRepository.findByFuncionarioId(funcionarioId);
            }
            
            List<Map<String, Object>> resultado = comissoes.stream().map(c -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", c.getId());
                map.put("valorVenda", c.getValorVenda());
                map.put("valorComissao", c.getValorComissao());
                map.put("percentual", c.getPercentualComissao());
                map.put("metaAtingida", c.getMetaAtingida());
                map.put("mes", c.getMes());
                map.put("ano", c.getAno());
                map.put("dataRegistro", c.getDataRegistro());
                return map;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(resultado);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", e.getMessage()));
        }
    }
    
    // Endpoint para atualizar percentual de comissão do vendedor
    @PatchMapping("/vendedor/{funcionarioId}/percentual")
    public ResponseEntity<?> atualizarPercentualComissao(
            @PathVariable Long funcionarioId,
            @RequestParam Double percentual) {
        
        try {
            Optional<Funcionario> funcionarioOpt = funcionarioRepository.findById(funcionarioId);
            
            if (funcionarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("erro", "Funcionário não encontrado"));
            }
            
            Funcionario funcionario = funcionarioOpt.get();
            funcionario.setPercentualComissao(percentual);
            funcionarioRepository.save(funcionario);
            
            return ResponseEntity.ok(Map.of(
                "mensagem", "Percentual de comissão atualizado com sucesso",
                "novoPercentual", percentual
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", e.getMessage()));
        }
    }
    
    // Endpoint para atualizar meta do vendedor
    @PatchMapping("/vendedor/{funcionarioId}/meta")
    public ResponseEntity<?> atualizarMeta(
            @PathVariable Long funcionarioId,
            @RequestParam Double meta) {
        
        try {
            Optional<Funcionario> funcionarioOpt = funcionarioRepository.findById(funcionarioId);
            
            if (funcionarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("erro", "Funcionário não encontrado"));
            }
            
            Funcionario funcionario = funcionarioOpt.get();
            funcionario.setMetaMensal(meta);
            funcionarioRepository.save(funcionario);
            
            return ResponseEntity.ok(Map.of(
                "mensagem", "Meta atualizada com sucesso",
                "novaMeta", meta
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", e.getMessage()));
        }
    }
    
    // Endpoint para estatísticas de comissões
    @GetMapping("/estatisticas")
    public ResponseEntity<?> getEstatisticas(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer ano) {
        
        try {
            List<Comissao> comissoes;
            
            if (mes != null && ano != null) {
                comissoes = comissaoRepository.findByMesAndAno(mes, ano);
            } else {
                comissoes = comissaoRepository.findAll();
            }
            
            Double totalComissoes = comissoes.stream()
                    .mapToDouble(Comissao::getValorComissao)
                    .sum();
            
            Double mediaComissao = comissoes.stream()
                    .mapToDouble(Comissao::getValorComissao)
                    .average()
                    .orElse(0.0);
            
            long totalComMetaAtingida = comissoes.stream()
                    .filter(c -> c.getMetaAtingida() != null && c.getMetaAtingida())
                    .count();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalRegistros", comissoes.size());
            stats.put("totalComissoes", totalComissoes);
            stats.put("mediaComissao", mediaComissao);
            stats.put("totalComMetaAtingida", totalComMetaAtingida);
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", e.getMessage()));
        }
    }
}
