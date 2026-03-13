package backend_java.backend_java.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend_java.backend_java.dto.FeriasRequestDTO;
import backend_java.backend_java.dto.FeriasResponseDTO;
import backend_java.backend_java.model.Ferias;
import backend_java.backend_java.model.Funcionario;
import backend_java.backend_java.repository.FeriasRepository;
import backend_java.backend_java.repository.FuncionarioRepository;

@RestController
@RequestMapping("/api/ferias")
public class FeriasController {
    
    @Autowired
    private FeriasRepository feriasRepository;
    
    @Autowired
    private FuncionarioRepository funcionarioRepository;
    
    // ==============================================
    // ENDPOINTS DE CONSULTA
    // ==============================================
    
    @GetMapping
    public ResponseEntity<List<FeriasResponseDTO>> listarTodas() {
        List<Ferias> ferias = feriasRepository.findAll();
        return ResponseEntity.ok(converterParaDTO(ferias));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Ferias> ferias = feriasRepository.findById(id);
        if (ferias.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Solicitação de férias não encontrada"));
        }
        return ResponseEntity.ok(converterParaDTO(ferias.get()));
    }
    
    @GetMapping("/pendentes")
    public ResponseEntity<List<FeriasResponseDTO>> listarPendentes() {
        List<Ferias> ferias = feriasRepository.findByStatus("pendentes");
        return ResponseEntity.ok(converterParaDTO(ferias));
    }
    
    @GetMapping("/aprovadas")
    public ResponseEntity<List<FeriasResponseDTO>> listarAprovadas() {
        List<Ferias> ferias = feriasRepository.findByStatus("aprovadas");
        return ResponseEntity.ok(converterParaDTO(ferias));
    }
    
    @GetMapping("/rejeitadas")
    public ResponseEntity<List<FeriasResponseDTO>> listarRejeitadas() {
        List<Ferias> ferias = feriasRepository.findByStatus("rejeitadas");
        return ResponseEntity.ok(converterParaDTO(ferias));
    }
    
    @GetMapping("/proximas")
    public ResponseEntity<List<FeriasResponseDTO>> listarProximas() {
        List<Ferias> ferias = feriasRepository.findProximasFerias();
        return ResponseEntity.ok(converterParaDTO(ferias));
    }
    
    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<?> listarPorFuncionario(@PathVariable Long funcionarioId) {
        if (!funcionarioRepository.existsById(funcionarioId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Funcionário não encontrado"));
        }
        
        List<Ferias> ferias = feriasRepository.findByFuncionarioId(funcionarioId);
        return ResponseEntity.ok(converterParaDTO(ferias));
    }
    
    @GetMapping("/periodo")
    public ResponseEntity<List<FeriasResponseDTO>> listarPorPeriodo(
            @RequestParam String inicio,
            @RequestParam String fim) {
        LocalDate dataInicio = LocalDate.parse(inicio);
        LocalDate dataFim = LocalDate.parse(fim);
        List<Ferias> ferias = feriasRepository.findByPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(converterParaDTO(ferias));
    }
    
    @GetMapping("/mes/{mes}/{ano}")
    public ResponseEntity<List<FeriasResponseDTO>> listarPorMes(
            @PathVariable int mes,
            @PathVariable int ano) {
        List<Ferias> ferias = feriasRepository.findByMesAndAno(mes, ano);
        return ResponseEntity.ok(converterParaDTO(ferias));
    }
    
    // ==============================================
    // ENDPOINTS DE AÇÃO
    // ==============================================
    
    @PostMapping("/solicitar")
    public ResponseEntity<?> solicitarFerias(@RequestBody FeriasRequestDTO request) {
        try {
            // Validar funcionário
            Optional<Funcionario> funcionarioOpt = funcionarioRepository.findById(request.getFuncionarioId());
            
            if (funcionarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("erro", "Funcionário não encontrado"));
            }
            
            Funcionario funcionario = funcionarioOpt.get();
            
            // Validar se funcionário está ativo
            if (!funcionario.getAtivo()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("erro", "Funcionário inativo não pode solicitar férias"));
            }
            
            // Validar datas
            if (request.getDataInicio() == null || request.getDataFim() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("erro", "Datas de início e fim são obrigatórias"));
            }
            
            if (request.getDataFim().isBefore(request.getDataInicio())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("erro", "Data fim deve ser posterior à data início"));
            }
            
            // Validar período mínimo de férias (pelo menos 10 dias)
            long dias = ChronoUnit.DAYS.between(request.getDataInicio(), request.getDataFim()) + 1;
            if (dias < 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("erro", "Período de férias deve ser de pelo menos 1 dia"));
            }
            
            // Verificar se já existe solicitação pendente
            if (feriasRepository.existsSolicitacaoPendente(request.getFuncionarioId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("erro", "Funcionário já possui uma solicitação de férias pendente"));
            }
            
            // Criar nova solicitação
            Ferias ferias = new Ferias();
            ferias.setFuncionario(funcionario);
            ferias.setDataInicio(request.getDataInicio());
            ferias.setDataFim(request.getDataFim());
            ferias.setObservacao(request.getObservacao());
            ferias.setStatus("pendentes");
            ferias.setDataSolicitacao(LocalDateTime.now());
            
            Ferias saved = feriasRepository.save(ferias);
            
            Map<String, Object> resposta = new HashMap<>();
            resposta.put("mensagem", "Férias solicitadas com sucesso");
            resposta.put("id", saved.getId());
            resposta.put("ferias", converterParaDTO(saved));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao solicitar férias: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<?> aprovarFerias(@PathVariable Long id) {
        try {
            Optional<Ferias> feriasOpt = feriasRepository.findById(id);
            
            if (feriasOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("erro", "Solicitação não encontrada"));
            }
            
            Ferias ferias = feriasOpt.get();
            
            if (!"pendentes".equals(ferias.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("erro", "Apenas solicitações pendentes podem ser aprovadas"));
            }
            
            ferias.setStatus("aprovadas");
            ferias.setDataAprovacao(LocalDateTime.now());
            
            Ferias updated = feriasRepository.save(ferias);
            
            Map<String, Object> resposta = new HashMap<>();
            resposta.put("mensagem", "Férias aprovadas com sucesso");
            resposta.put("id", updated.getId());
            resposta.put("ferias", converterParaDTO(updated));
            
            return ResponseEntity.ok(resposta);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao aprovar férias: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/rejeitar")
    public ResponseEntity<?> rejeitarFerias(@PathVariable Long id) {
        try {
            Optional<Ferias> feriasOpt = feriasRepository.findById(id);
            
            if (feriasOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("erro", "Solicitação não encontrada"));
            }
            
            Ferias ferias = feriasOpt.get();
            
            if (!"pendentes".equals(ferias.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("erro", "Apenas solicitações pendentes podem ser rejeitadas"));
            }
            
            ferias.setStatus("rejeitadas");
            
            Ferias updated = feriasRepository.save(ferias);
            
            Map<String, Object> resposta = new HashMap<>();
            resposta.put("mensagem", "Férias rejeitadas com sucesso");
            resposta.put("id", updated.getId());
            resposta.put("ferias", converterParaDTO(updated));
            
            return ResponseEntity.ok(resposta);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao rejeitar férias: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarSolicitacao(@PathVariable Long id) {
        try {
            Optional<Ferias> feriasOpt = feriasRepository.findById(id);
            
            if (feriasOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("erro", "Solicitação não encontrada"));
            }
            
            Ferias ferias = feriasOpt.get();
            
            if (!"pendentes".equals(ferias.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("erro", "Apenas solicitações pendentes podem ser canceladas"));
            }
            
            feriasRepository.delete(ferias);
            
            return ResponseEntity.ok(Map.of("mensagem", "Solicitação cancelada com sucesso"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao cancelar solicitação: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarFerias(@PathVariable Long id) {
        try {
            if (!feriasRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("erro", "Solicitação não encontrada"));
            }
            
            feriasRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("mensagem", "Registro de férias deletado com sucesso"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao deletar: " + e.getMessage()));
        }
    }
    
    // ==============================================
    // ENDPOINTS DE ESTATÍSTICAS
    // ==============================================
    
    @GetMapping("/estatisticas")
    public ResponseEntity<?> getEstatisticas() {
        try {
            List<Ferias> todas = feriasRepository.findAll();
            List<Ferias> pendentes = feriasRepository.findByStatus("pendentes");
            List<Ferias> aprovadas = feriasRepository.findByStatus("aprovadas");
            List<Ferias> rejeitadas = feriasRepository.findByStatus("rejeitadas");
            List<Ferias> proximas = feriasRepository.findProximasFerias();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", todas.size());
            stats.put("pendentes", pendentes.size());
            stats.put("aprovadas", aprovadas.size());
            stats.put("rejeitadas", rejeitadas.size());
            stats.put("proximas", proximas.size());
            
            // Calcular totais de dias
            long totalDiasAprovados = aprovadas.stream()
                    .mapToLong(f -> (long) f.getDias())
                    .sum();
            
            stats.put("totalDiasAprovados", totalDiasAprovados);
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro ao calcular estatísticas: " + e.getMessage()));
        }
    }
    
    // ==============================================
    // MÉTODOS AUXILIARES
    // ==============================================
    
    private List<FeriasResponseDTO> converterParaDTO(List<Ferias> ferias) {
        return ferias.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    private FeriasResponseDTO converterParaDTO(Ferias ferias) {
        FeriasResponseDTO dto = new FeriasResponseDTO();
        dto.setId(ferias.getId());
        dto.setFuncionarioId(ferias.getFuncionario().getId());
        dto.setFuncionarioNome(ferias.getFuncionario().getNomeCompleto());
        dto.setFuncionarioCargo(ferias.getFuncionario().getCargo());
        dto.setFuncionarioDepartamento(ferias.getFuncionario().getDepartamento());
        dto.setDataInicio(ferias.getDataInicio());
        dto.setDataFim(ferias.getDataFim());
        dto.setStatus(ferias.getStatus());
        dto.setDataSolicitacao(ferias.getDataSolicitacao());
        dto.setDataAprovacao(ferias.getDataAprovacao());
        dto.setObservacao(ferias.getObservacao());
        dto.setDias(ferias.getDias());
        
        return dto;
    }
}
