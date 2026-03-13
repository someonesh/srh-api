package backend_java.backend_java.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend_java.backend_java.model.Comissao;
import backend_java.backend_java.model.FolhaPagamento;
import backend_java.backend_java.model.Funcionario;
import backend_java.backend_java.repository.ComissaoRepository;
import backend_java.backend_java.repository.FolhaPagamentoRepository;
import backend_java.backend_java.repository.FuncionarioRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class FolhaPagamentoService {
    
    @Autowired
    private FolhaPagamentoRepository folhaPagamentoRepository;
    
    @Autowired
    private FuncionarioRepository funcionarioRepository;
    
    @Autowired
    private ComissaoRepository comissaoRepository; // 🔥 NOVO: para buscar comissões
    
    @Autowired
    private VendedorService vendedorService;
    
    // ========== NOVO MÉTODO AUXILIAR ==========
    
    /**
     * Calcula o total de comissões de um funcionário em um determinado mês/ano
     */
    private Double calcularTotalComissoes(Long funcionarioId, Integer mes, Integer ano) {
        List<Comissao> comissoes = comissaoRepository.findByFuncionarioIdAndMesAndAno(funcionarioId, mes, ano);
        
        Double total = 0.0;
        for (Comissao c : comissoes) {
            if (c.getValorComissao() != null) {
                total += c.getValorComissao();
            }
        }
        
        return total;
    }
    
    // ========== MÉTODOS PRINCIPAIS ATUALIZADOS ==========
    
    /**
     * Processar folha de pagamento de todos os funcionários ativos
     * AGORA COM COMISSÕES REAIS DO BANCO!
     */
    @Transactional
    public List<FolhaPagamento> processarFolhaMensal(Integer mes, Integer ano) {
        List<Funcionario> funcionarios = funcionarioRepository.findByAtivoTrue();
        List<FolhaPagamento> folhasProcessadas = new ArrayList<>();
        
        System.out.println("\n📊 PROCESSANDO FOLHA DE " + mes + "/" + ano);
        System.out.println("======================================");
        
        for (Funcionario func : funcionarios) {
            // Verificar se já existe folha para este período
            if (!folhaPagamentoRepository.existsByFuncionarioAndMesAndAno(func, mes, ano)) {
                
                // Criar folha base
                FolhaPagamento folha = new FolhaPagamento(func, mes, ano);
                
                // 🔥 CALCULAR COMISSÕES REAIS DO BANCO (importadas da API)
                Double totalComissoes = 0.0;
                
                if (func.isVendedor()) {
                    totalComissoes = calcularTotalComissoes(func.getId(), mes, ano);
                    System.out.println("👤 " + func.getNomeCompleto());
                    System.out.println("   Comissões do mês: " + totalComissoes);
                }
                
                // Aplicar comissões na folha
                if (totalComissoes > 0) {
                    folha.setComissao(totalComissoes);
                    folha.setSalarioBruto(func.getSalarioBase() + totalComissoes);
                    folha.setInss(folha.getSalarioBruto() * 0.05);
                    folha.setSalarioLiquido(folha.getSalarioBruto() - folha.getInss());
                }
                
                System.out.println("   Salário Base: " + func.getSalarioBase());
                System.out.println("   Salário Bruto: " + folha.getSalarioBruto());
                System.out.println("   INSS (5%): " + folha.getInss());
                System.out.println("   Salário Líquido: " + folha.getSalarioLiquido());
                System.out.println("--------------------------------------");
                
                folhasProcessadas.add(folhaPagamentoRepository.save(folha));
            } else {
                System.out.println("⏭️ " + func.getNomeCompleto() + " já processado");
            }
        }
        
        System.out.println("======================================");
        System.out.println("✅ Total processado: " + folhasProcessadas.size() + " funcionários\n");
        
        return folhasProcessadas;
    }
    
    /**
     * Processar folha de um funcionário específico
     * AGORA COM COMISSÕES REAIS DO BANCO!
     */
    @Transactional
    public FolhaPagamento processarFolhaFuncionario(Long funcionarioId, Integer mes, Integer ano) {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
            .orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado"));
        
        // Verificar se já existe
        if (folhaPagamentoRepository.existsByFuncionarioAndMesAndAno(funcionario, mes, ano)) {
            throw new RuntimeException("Folha já processada para este período");
        }
        
        // Criar folha base
        FolhaPagamento folha = new FolhaPagamento(funcionario, mes, ano);
        
        // 🔥 CALCULAR COMISSÕES REAIS DO BANCO
        Double totalComissoes = 0.0;
        
        if (funcionario.isVendedor()) {
            totalComissoes = calcularTotalComissoes(funcionarioId, mes, ano);
            System.out.println("👤 Processando folha individual para " + funcionario.getNomeCompleto());
            System.out.println("   Comissões encontradas: " + totalComissoes);
        }
        
        // Aplicar comissões na folha
        if (totalComissoes > 0) {
            folha.setComissao(totalComissoes);
            folha.setSalarioBruto(funcionario.getSalarioBase() + totalComissoes);
            folha.setInss(folha.getSalarioBruto() * 0.05);
            folha.setSalarioLiquido(folha.getSalarioBruto() - folha.getInss());
        }
        
        return folhaPagamentoRepository.save(folha);
    }
    
    // ========== MÉTODOS EXISTENTES (MANTIDOS) ==========
    
    /**
     * Adicionar comissão a uma folha existente
     */
    @Transactional
    public FolhaPagamento adicionarComissao(Long folhaId, Double valorComissao) {
        FolhaPagamento folha = folhaPagamentoRepository.findById(folhaId)
            .orElseThrow(() -> new EntityNotFoundException("Folha não encontrada"));
        
        folha.setComissao(valorComissao);
        folha.setSalarioBruto(folha.getSalarioBase() + valorComissao);
        folha.setInss(folha.getSalarioBruto() * 0.05);
        folha.setSalarioLiquido(folha.getSalarioBruto() - folha.getInss());
        
        return folhaPagamentoRepository.save(folha);
    }
    
    /**
     * Listar folhas por período
     */
    public List<FolhaPagamento> listarPorPeriodo(Integer mes, Integer ano) {
        return folhaPagamentoRepository.findByMesAndAno(mes, ano);
    }
    
    /**
     * Listar folhas de um funcionário
     */
    public List<FolhaPagamento> listarPorFuncionario(Long funcionarioId) {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
            .orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado"));
        return folhaPagamentoRepository.findByFuncionarioOrderByAnoDescMesDesc(funcionario);
    }
    
    /**
     * Buscar folha por ID
     */
    public FolhaPagamento buscarPorId(Long id) {
        return folhaPagamentoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Folha não encontrada"));
    }
    
    /**
     * Marcar como pago
     */
    @Transactional
    public FolhaPagamento marcarComoPago(Long id) {
        FolhaPagamento folha = buscarPorId(id);
        folha.setStatus(FolhaPagamento.StatusPagamento.PAGO);
        folha.setDataPagamento(LocalDate.now());
        return folhaPagamentoRepository.save(folha);
    }

    /**
     * Cancelar folha
     */
    @Transactional
    public FolhaPagamento cancelarFolha(Long id) {
        FolhaPagamento folha = buscarPorId(id);
        folha.setStatus(FolhaPagamento.StatusPagamento.CANCELADO);
        return folhaPagamentoRepository.save(folha);
    }

    /**
     * Gerar estatísticas da folha
     */
    public Map<String, Object> gerarEstatisticas(Integer mes, Integer ano) {
        List<FolhaPagamento> folhas = listarPorPeriodo(mes, ano);
        
        long totalProcessadas = folhas.size();
        long pagas = folhas.stream()
            .filter(f -> f.getStatus() == FolhaPagamento.StatusPagamento.PAGO)
            .count();
        long pendentes = folhas.stream()
            .filter(f -> f.getStatus() == FolhaPagamento.StatusPagamento.ATIVO || 
                         f.getStatus() == FolhaPagamento.StatusPagamento.PROCESSADO)
            .count();
        
        double totalSalarios = folhas.stream()
            .mapToDouble(FolhaPagamento::getSalarioLiquido)
            .sum();
        double totalPago = folhas.stream()
            .filter(f -> f.getStatus() == FolhaPagamento.StatusPagamento.PAGO)
            .mapToDouble(FolhaPagamento::getSalarioLiquido)
            .sum();
        
        double totalComissoes = folhas.stream()
            .mapToDouble(f -> f.getComissao() != null ? f.getComissao() : 0.0)
            .sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("periodo", String.format("%02d/%d", mes, ano));
        stats.put("totalFuncionarios", totalProcessadas);
        stats.put("folhasPagas", pagas);
        stats.put("folhasPendentes", pendentes);
        stats.put("totalSalarios", String.format("MT %.2f", totalSalarios));
        stats.put("totalPago", String.format("MT %.2f", totalPago));
        stats.put("totalComissoes", String.format("MT %.2f", totalComissoes));
        
        return stats;
    }

    /**
     * Buscar folhas pendentes
     */
    public List<FolhaPagamento> buscarPendentes(Integer mes, Integer ano) {
        return folhaPagamentoRepository.findByMesAndAno(mes, ano)
            .stream()
            .filter(f -> f.getStatus() != FolhaPagamento.StatusPagamento.PAGO)
            .toList();
    }
    
    /**
     * Gerar relatório da folha
     */
    public Map<String, Object> gerarRelatorio(Integer mes, Integer ano) {
        List<FolhaPagamento> folhas = listarPorPeriodo(mes, ano);
        
        double totalSalariosBase = 0;
        double totalComissoes = 0;
        double totalSalariosBrutos = 0;
        double totalINSS = 0;
        double totalSalariosLiquidos = 0;
        
        for (FolhaPagamento f : folhas) {
            totalSalariosBase += f.getSalarioBase();
            totalComissoes += f.getComissao() != null ? f.getComissao() : 0;
            totalSalariosBrutos += f.getSalarioBruto() != null ? f.getSalarioBruto() : f.getSalarioBase();
            totalINSS += f.getInss() != null ? f.getInss() : 0;
            totalSalariosLiquidos += f.getSalarioLiquido() != null ? f.getSalarioLiquido() : f.getSalarioBase();
        }
        
        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("periodo", String.format("%02d/%d", mes, ano));
        relatorio.put("totalFuncionarios", folhas.size());
        relatorio.put("totalSalariosBase", String.format("MT %.2f", totalSalariosBase));
        relatorio.put("totalComissoes", String.format("MT %.2f", totalComissoes));
        relatorio.put("totalSalariosBrutos", String.format("MT %.2f", totalSalariosBrutos));
        relatorio.put("totalINSS", String.format("MT %.2f", totalINSS));
        relatorio.put("totalSalariosLiquidos", String.format("MT %.2f", totalSalariosLiquidos));
        relatorio.put("folhas", folhas);
        
        return relatorio;
    }
}