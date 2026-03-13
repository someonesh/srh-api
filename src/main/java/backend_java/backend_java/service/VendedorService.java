package backend_java.backend_java.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend_java.backend_java.model.Vendedor;
import backend_java.backend_java.repository.VendedorRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class VendedorService {
    
    @Autowired
    private VendedorRepository vendedorRepository;
    
    // ==============================================
    // OPERAÇÕES DE CRUD
    // ==============================================
    
    /**
     * Salva um novo vendedor
     */
    @Transactional
    public Vendedor salvar(Vendedor vendedor) {
        // Garantir valores padrão
        vendedor.setAtivo(true);
        vendedor.setDepartamento("Vendas");
        
        // Garantir que a comissão tem um valor
        if (vendedor.getComissao() == null) {
            vendedor.setComissao(5.0);
        }
        
        // Garantir que a meta mensal tem um valor
        if (vendedor.getMetaMensal() == null) {
            vendedor.setMetaMensal(50000.0);
        }
        
        // Código temporário
        vendedor.setCodigo("TEMP");
        
        // Salva para gerar ID
        Vendedor saved = vendedorRepository.save(vendedor);
        
        // Gera código definitivo baseado no ID
        saved.setCodigo(String.format("VEND-%d", saved.getId()));
        
        return vendedorRepository.save(saved);
    }
    
    /**
     * Atualiza dados de um vendedor existente
     */
    @Transactional
    public Vendedor atualizar(Long id, Vendedor vendedorAtualizado) {
        Vendedor vendedor = buscarPorId(id);
        
        // Atualizar campos básicos
        if (vendedorAtualizado.getNomeCompleto() != null && !vendedorAtualizado.getNomeCompleto().isEmpty())
            vendedor.setNomeCompleto(vendedorAtualizado.getNomeCompleto());
        
        if (vendedorAtualizado.getEmail() != null && !vendedorAtualizado.getEmail().isEmpty())
            vendedor.setEmail(vendedorAtualizado.getEmail());
        
        if (vendedorAtualizado.getTelefone() != null && !vendedorAtualizado.getTelefone().isEmpty())
            vendedor.setTelefone(vendedorAtualizado.getTelefone());
        
        // Atualizar comissão
        if (vendedorAtualizado.getComissao() != null)
            vendedor.setComissao(vendedorAtualizado.getComissao());
        
        // Atualizar meta mensal
        if (vendedorAtualizado.getMetaMensal() != null)
            vendedor.setMetaMensal(vendedorAtualizado.getMetaMensal());
        
        // Atualizar outros campos se fornecidos
        if (vendedorAtualizado.getDataNascimento() != null)
            vendedor.setDataNascimento(vendedorAtualizado.getDataNascimento());
        
        if (vendedorAtualizado.getEndereco() != null && !vendedorAtualizado.getEndereco().isEmpty())
            vendedor.setEndereco(vendedorAtualizado.getEndereco());
        
        if (vendedorAtualizado.getCargo() != null && !vendedorAtualizado.getCargo().isEmpty())
            vendedor.setCargo(vendedorAtualizado.getCargo());
        
        if (vendedorAtualizado.getSalarioBase() != null)
            vendedor.setSalarioBase(vendedorAtualizado.getSalarioBase());
        
        if (vendedorAtualizado.getTipoContrato() != null && !vendedorAtualizado.getTipoContrato().isEmpty())
            vendedor.setTipoContrato(vendedorAtualizado.getTipoContrato());
        
        // Código NÃO é atualizado - permanece o mesmo
        
        return vendedorRepository.save(vendedor);
    }
    
    /**
     * Deletar vendedor (cuidado!)
     */
    @Transactional
    public void deletar(Long id) {
        if (!vendedorRepository.existsById(id)) {
            throw new EntityNotFoundException("Vendedor não encontrado com ID: " + id);
        }
        vendedorRepository.deleteById(id);
    }
    
    // ==============================================
    // CONSULTAS
    // ==============================================
    
    /**
     * Lista todos os vendedores (ativos e inativos)
     */
    public List<Vendedor> listarTodos() {
        return vendedorRepository.findAll();
    }
    
    /**
     * Lista apenas vendedores ativos
     */
    public List<Vendedor> listarVendedoresAtivos() {
        return vendedorRepository.findByAtivoTrue();
    }
    
    /**
     * Lista apenas vendedores inativos
     */
    public List<Vendedor> listarVendedoresInativos() {
        return vendedorRepository.findByAtivoFalse();
    }
    
    /**
     * Busca vendedor por ID
     */
    public Vendedor buscarPorId(Long id) {
        return vendedorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Vendedor não encontrado com ID: " + id));
    }
    
    /**
     * Busca vendedor por código
     */
    public Optional<Vendedor> buscarPorCodigo(String codigo) {
        return vendedorRepository.findByCodigo(codigo);
    }
    
    /**
     * Busca vendedor ativo por código
     */
    public Optional<Vendedor> buscarPorCodigoAtivo(String codigo) {
        return vendedorRepository.findByCodigoAndAtivoTrue(codigo);
    }
    
    /**
     * Busca vendedores por nome (busca parcial)
     */
    public List<Vendedor> buscarPorNome(String nome) {
        return vendedorRepository.findByNomeCompletoContainingIgnoreCase(nome);
    }
    
    // ==============================================
    // STATUS
    // ==============================================
    
    /**
     * Ativar vendedor
     */
    @Transactional
    public void ativar(Long id) {
        Vendedor vendedor = buscarPorId(id);
        if (vendedor.getAtivo()) {
            throw new RuntimeException("Vendedor já está ativo");
        }
        vendedor.setAtivo(true);
        vendedorRepository.save(vendedor);
    }
    
    /**
     * Inativar vendedor
     */
    @Transactional
    public void inativar(Long id) {
        Vendedor vendedor = buscarPorId(id);
        if (!vendedor.getAtivo()) {
            throw new RuntimeException("Vendedor já está inativo");
        }
        vendedor.setAtivo(false);
        vendedorRepository.save(vendedor);
    }
    
    // ==============================================
    // CÁLCULOS DE COMISSÃO
    // ==============================================
    
    /**
     * Calcula comissão baseada no percentual do vendedor
     */
    public Double calcularComissao(Long id, Double totalVendas) {
        Vendedor vendedor = buscarPorId(id);
        
        if (!vendedor.getAtivo()) {
            throw new RuntimeException("Vendedor inativo não pode receber comissão");
        }
        
        double percentual = vendedor.getComissao() != null ? vendedor.getComissao() : 5.0;
        return totalVendas * (percentual / 100);
    }
    
    /**
     * Calcula comissão por código do vendedor (para sistema de vendas)
     */
    public Double calcularComissaoPorCodigo(String codigo, Double totalVendas) {
        Vendedor vendedor = buscarPorCodigoAtivo(codigo)
            .orElseThrow(() -> new RuntimeException("Vendedor não encontrado ou inativo: " + codigo));
        
        return calcularComissao(vendedor.getId(), totalVendas);
    }
    
    /**
     * Calcula comissão com detalhes (inclui meta e bônus)
     */
    public Map<String, Object> calcularComissaoDetalhada(Long id, Double totalVendas) {
        Vendedor vendedor = buscarPorId(id);
        
        if (!vendedor.getAtivo()) {
            throw new RuntimeException("Vendedor inativo não pode receber comissão");
        }
        
        double percentual = vendedor.getComissao() != null ? vendedor.getComissao() : 5.0;
        double comissao = totalVendas * (percentual / 100);
        double meta = vendedor.getMetaMensal() != null ? vendedor.getMetaMensal() : 50000.0;
        boolean atingiuMeta = totalVendas >= meta;
        double bonus = atingiuMeta ? comissao * 0.10 : 0.0;
        double comissaoTotal = comissao + bonus;
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("vendedorId", vendedor.getId());
        resultado.put("vendedorNome", vendedor.getNomeCompleto());
        resultado.put("vendedorCodigo", vendedor.getCodigo());
        resultado.put("totalVendas", totalVendas);
        resultado.put("percentualComissao", percentual);
        resultado.put("comissaoBase", comissao);
        resultado.put("metaMensal", meta);
        resultado.put("atingiuMeta", atingiuMeta);
        resultado.put("bonus", bonus);
        resultado.put("comissaoTotal", comissaoTotal);
        
        return resultado;
    }
    
    /**
     * Verifica se o vendedor atingiu a meta
     */
    public boolean atingiuMeta(Long id, Double totalVendas) {
        Vendedor vendedor = buscarPorId(id);
        if (!vendedor.getAtivo()) return false;
        
        double meta = vendedor.getMetaMensal() != null ? vendedor.getMetaMensal() : 50000.0;
        return totalVendas >= meta;
    }
    
    /**
     * Calcula bônus por meta (10% do salário base)
     */
    public Double calcularBonusPorMeta(Long id) {
        Vendedor vendedor = buscarPorId(id);
        return vendedor.getSalarioBase() * 0.10;
    }
    
    // ==============================================
    // RELATÓRIOS
    // ==============================================
    
    /**
     * Gera relatório básico de vendedores ativos
     */
    public Object[][] gerarRelatorioVendedores() {
        List<Vendedor> vendedores = listarVendedoresAtivos();
        if (vendedores.isEmpty()) {
            return new Object[0][5];
        }
        
        Object[][] relatorio = new Object[vendedores.size()][5];
        
        for (int i = 0; i < vendedores.size(); i++) {
            Vendedor v = vendedores.get(i);
            relatorio[i][0] = v.getCodigo();
            relatorio[i][1] = v.getNomeCompleto();
            relatorio[i][2] = String.format("MT %.2f", v.getSalarioBase());
            
            Double comissao = v.getComissao() != null ? v.getComissao() : 5.0;
            relatorio[i][3] = String.format("%.1f%%", comissao);
            
            relatorio[i][4] = String.format("MT %.2f", v.getMetaMensal() != null ? v.getMetaMensal() : 50000.0);
        }
        return relatorio;
    }
    
    /**
     * Gera relatório detalhado com estatísticas
     */
    public Map<String, Object> gerarEstatisticasVendedores() {
        List<Vendedor> ativos = listarVendedoresAtivos();
        List<Vendedor> inativos = listarVendedoresInativos();
        
        double mediaComissao = ativos.stream()
            .mapToDouble(v -> v.getComissao() != null ? v.getComissao() : 5.0)
            .average()
            .orElse(0.0);
        
        double mediaMeta = ativos.stream()
            .mapToDouble(v -> v.getMetaMensal() != null ? v.getMetaMensal() : 50000.0)
            .average()
            .orElse(0.0);
        
        double totalFolha = ativos.stream()
            .mapToDouble(Vendedor::getSalarioBase)
            .sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalVendedores", ativos.size() + inativos.size());
        stats.put("totalAtivos", ativos.size());
        stats.put("totalInativos", inativos.size());
        stats.put("mediaComissao", String.format("%.1f%%", mediaComissao));
        stats.put("mediaMeta", String.format("MT %.2f", mediaMeta));
        stats.put("totalFolhaMensal", String.format("MT %.2f", totalFolha));
        
        return stats;
    }
}