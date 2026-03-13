package backend_java.backend_java.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend_java.backend_java.model.Funcionario;
import backend_java.backend_java.model.Vendedor;
import backend_java.backend_java.repository.FuncionarioRepository;
import backend_java.backend_java.repository.VendedorRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class FuncionarioService {
    
    @Autowired
    private FuncionarioRepository funcionarioRepository;
    
    @Autowired
    private VendedorRepository vendedorRepository;
    
    // ==============================================
    // OPERAÇÕES DE CRUD
    // ==============================================
    
    @Transactional
    public Funcionario salvar(Funcionario funcionario) {
        // Validar Email único
        if (funcionarioRepository.existsByEmail(funcionario.getEmail())) {
            throw new RuntimeException("Email já cadastrado: " + funcionario.getEmail());
        }
        
        // Validar salário mínimo (15.000 MT)
        if (funcionario.getSalarioBase() < 15000) {
            throw new RuntimeException("Salário base não pode ser inferior ao mínimo legal (15.000 MT)");
        }
        
        // Configurar valores padrão
        funcionario.setAtivo(true);
        if (funcionario.getDataContratacao() == null) {
            funcionario.setDataContratacao(LocalDate.now());
        }
        if (funcionario.getTipoContrato() == null) {
            funcionario.setTipoContrato("CLT");
        }
        
        // 🔥 VERIFICAR SE É VENDEDOR (departamento = "Vendas")
        boolean isVendedor = "Vendas".equalsIgnoreCase(funcionario.getDepartamento());
        
        if (isVendedor) {
            // É um vendedor - precisamos criar como Vendedor, não como Funcionario simples
            
            // Criar objeto Vendedor com os dados do funcionario
            Vendedor vendedor = new Vendedor();
            vendedor.setNomeCompleto(funcionario.getNomeCompleto());
            vendedor.setDataNascimento(funcionario.getDataNascimento());
            vendedor.setEmail(funcionario.getEmail());
            vendedor.setTelefone(funcionario.getTelefone());
            vendedor.setSalarioBase(funcionario.getSalarioBase());
            vendedor.setCargo(funcionario.getCargo());
            vendedor.setDepartamento("Vendas");
            vendedor.setDataContratacao(funcionario.getDataContratacao());
            vendedor.setEndereco(funcionario.getEndereco());
            vendedor.setTipoContrato(funcionario.getTipoContrato());
            vendedor.setAtivo(true);
            
            // Valores padrão para vendedor
            vendedor.setComissao(5.0); // 5% padrão
            vendedor.setMetaMensal(50000.0); // Meta padrão
            vendedor.setCodigo("TEMP"); // Código temporário
            
        // Salvar primeiro para gerar ID
        Vendedor saved = vendedorRepository.save(vendedor);
        
        // 🔥 AGORA VEM A MUDANÇA: GERAR CÓDIGO SEQUENCIAL
        // Em vez de usar o ID, vamos contar quantos vendedores existem
        
        long totalVendedores = vendedorRepository.count(); // Conta todos os vendedores
        int proximoNumero = (int) totalVendedores; // Pega o total como próximo número
        
        // Gerar código: VEND-1, VEND-2, VEND-3...
        String codigo = String.format("VEND-%d", proximoNumero);
        
        // Verificar se esse código já existe (segurança)
        while (vendedorRepository.existsByCodigo(codigo)) {
            proximoNumero++;
            codigo = String.format("VEND-%d", proximoNumero);
        }
        
        saved.setCodigo(codigo);
        
        return vendedorRepository.save(saved);
    
            
        } else {
            // É um funcionário normal
            return funcionarioRepository.save(funcionario);
        }
    }
    
    @Transactional
    public Funcionario atualizar(Long id, Funcionario funcionarioAtualizado) {
        Funcionario funcionario = buscarPorId(id);
        
        // Validar Email se foi alterado
        if (!funcionario.getEmail().equals(funcionarioAtualizado.getEmail()) &&
            funcionarioRepository.existsByEmail(funcionarioAtualizado.getEmail())) {
            throw new RuntimeException("Email já está em uso por outro funcionário");
        }
        
        // Guardar estado anterior para verificar mudança de departamento
        boolean eraVendedor = funcionario instanceof Vendedor || "Vendas".equalsIgnoreCase(funcionario.getDepartamento());
        boolean vaiSerVendedor = "Vendas".equalsIgnoreCase(funcionarioAtualizado.getDepartamento());
        
        // Atualizar campos básicos
        funcionario.setNomeCompleto(funcionarioAtualizado.getNomeCompleto());
        funcionario.setTelefone(funcionarioAtualizado.getTelefone());
        funcionario.setEndereco(funcionarioAtualizado.getEndereco());
        funcionario.setCargo(funcionarioAtualizado.getCargo());
        funcionario.setDepartamento(funcionarioAtualizado.getDepartamento());
        funcionario.setSalarioBase(funcionarioAtualizado.getSalarioBase());
        funcionario.setTipoContrato(funcionarioAtualizado.getTipoContrato());
        
        if (funcionarioAtualizado.getDataNascimento() != null) {
            funcionario.setDataNascimento(funcionarioAtualizado.getDataNascimento());
        }
        
        // 🔥 SE MUDOU O DEPARTAMENTO, PRECISAMOS ATUALIZAR A TABELA VENDEDORES
        if (!eraVendedor && vaiSerVendedor) {
            // Funcionário normal virou vendedor - criar registro na tabela vendedores
            Vendedor vendedor = new Vendedor();
            vendedor.setId(funcionario.getId()); // Mesmo ID
            vendedor.setNomeCompleto(funcionario.getNomeCompleto());
            vendedor.setDataNascimento(funcionario.getDataNascimento());
            vendedor.setEmail(funcionario.getEmail());
            vendedor.setTelefone(funcionario.getTelefone());
            vendedor.setSalarioBase(funcionario.getSalarioBase());
            vendedor.setCargo(funcionario.getCargo());
            vendedor.setDepartamento("Vendas");
            vendedor.setDataContratacao(funcionario.getDataContratacao());
            vendedor.setEndereco(funcionario.getEndereco());
            vendedor.setTipoContrato(funcionario.getTipoContrato());
            vendedor.setAtivo(funcionario.getAtivo());
            
            // Valores padrão para vendedor
            vendedor.setComissao(5.0);
            vendedor.setMetaMensal(50000.0);
            vendedor.setCodigo(String.format("VEND-%d", funcionario.getId()));
            
            vendedorRepository.save(vendedor);
            
        } else if (eraVendedor && !vaiSerVendedor) {
            // Vendedor virou funcionário normal - remover da tabela vendedores
            vendedorRepository.deleteById(id);
        }
        
        return funcionarioRepository.save(funcionario);
    }
    
    @Transactional
    public void deletar(Long id) {
        if (!funcionarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Funcionário não encontrado com ID: " + id);
        }
        
        // 🔥 Se for vendedor, deletar também da tabela vendedores
        Funcionario funcionario = buscarPorId(id);
        if (funcionario instanceof Vendedor || "Vendas".equalsIgnoreCase(funcionario.getDepartamento())) {
            vendedorRepository.deleteById(id);
        }
        
        funcionarioRepository.deleteById(id);
    }
    
    // ==============================================
    // CONSULTAS
    // ==============================================
    
    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }
    
    public List<Funcionario> listarAtivos() {
        return funcionarioRepository.findByAtivoTrue();
    }
    
    public List<Funcionario> listarInativos() {
        return funcionarioRepository.findByAtivoFalse();
    }
    
    public Funcionario buscarPorId(Long id) {
        return funcionarioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado com ID: " + id));
    }
    
    public List<Funcionario> buscarPorDepartamento(String departamento) {
        return funcionarioRepository.findByDepartamento(departamento);
    }
    
    public List<Funcionario> buscarPorNome(String nome) {
        return funcionarioRepository.findByNomeCompletoContainingIgnoreCase(nome);
    }
    
    // ==============================================
    // OPERAÇÕES DE STATUS
    // ==============================================
    
    @Transactional
    public void demitir(Long id, LocalDate dataDemissao) {
        Funcionario funcionario = buscarPorId(id);
        
        if (!funcionario.getAtivo()) {
            throw new RuntimeException("Funcionário já está inativo");
        }
        
        funcionario.setAtivo(false);
        funcionario.setDataDemissao(dataDemissao != null ? dataDemissao : LocalDate.now());
        
        // 🔥 Se for vendedor, atualizar também na tabela vendedores
        if (funcionario instanceof Vendedor) {
            Vendedor vendedor = (Vendedor) funcionario;
            vendedor.setAtivo(false);
            vendedor.setDataDemissao(dataDemissao != null ? dataDemissao : LocalDate.now());
            vendedorRepository.save(vendedor);
        } else {
            funcionarioRepository.save(funcionario);
        }
    }
    
    @Transactional
    public void reativar(Long id) {
        Funcionario funcionario = buscarPorId(id);
        
        if (funcionario.getAtivo()) {
            throw new RuntimeException("Funcionário já está ativo");
        }
        
        funcionario.setAtivo(true);
        funcionario.setDataDemissao(null);
        
        // 🔥 Se for vendedor, atualizar também na tabela vendedores
        if (funcionario instanceof Vendedor) {
            Vendedor vendedor = (Vendedor) funcionario;
            vendedor.setAtivo(true);
            vendedor.setDataDemissao(null);
            vendedorRepository.save(vendedor);
        } else {
            funcionarioRepository.save(funcionario);
        }
    }
    
    // ==============================================
    // ESTATÍSTICAS E CÁLCULOS
    // ==============================================
    
    public Double calcularMediaSalarialPorDepartamento(String departamento) {
        return funcionarioRepository.findByDepartamento(departamento)
            .stream()
            .filter(Funcionario::getAtivo)
            .mapToDouble(Funcionario::getSalarioBase)
            .average()
            .orElse(0.0);
    }
    
    public long contarPorDepartamento(String departamento) {
        return funcionarioRepository.countByDepartamento(departamento);
    }
    
    public Double calcularFolhaMensal() {
        return listarAtivos()
            .stream()
            .mapToDouble(Funcionario::getSalarioBase)
            .sum();
    }
    
    public Double projetarDecimoTerceiro() {
        return calcularFolhaMensal() / 2;
    }
    
    // ==============================================
    // MÉTODOS ESPECÍFICOS PARA VENDEDORES
    // ==============================================
    
    public List<Funcionario> listarVendedores() {
        return funcionarioRepository.findByDepartamentoAndAtivoTrue("Vendas");
    }
    
    public Double calcularComissao(Long id, Double totalVendas, Double percentual) {
        Funcionario vendedor = buscarPorId(id);
        
        if (!vendedor.isVendedor()) {
            throw new RuntimeException("Funcionário não é vendedor");
        }
        
        return totalVendas * (percentual / 100);
    }
    
    public boolean isVendedor(Long id) {
        Funcionario funcionario = buscarPorId(id);
        return funcionario.isVendedor();
    }
    
    // ==============================================
    // RELATÓRIOS
    // ==============================================
    
    public Object[][] gerarRelatorioPorDepartamento() {
        List<String> departamentos = funcionarioRepository.findAllDepartamentos();
        Object[][] relatorio = new Object[departamentos.size()][4];
        
        for (int i = 0; i < departamentos.size(); i++) {
            String depto = departamentos.get(i);
            List<Funcionario> funcionarios = buscarPorDepartamento(depto);
            long ativos = funcionarios.stream().filter(Funcionario::getAtivo).count();
            double mediaSalarial = calcularMediaSalarialPorDepartamento(depto);
            
            relatorio[i][0] = depto;
            relatorio[i][1] = funcionarios.size();
            relatorio[i][2] = ativos;
            relatorio[i][3] = mediaSalarial;
        }
        
        return relatorio;
    }
    
    public List<Funcionario> buscarAniversariantes() {
        int mesAtual = LocalDate.now().getMonthValue();
        
        return funcionarioRepository.findAll()
            .stream()
            .filter(f -> f.getDataNascimento() != null && 
                         f.getDataNascimento().getMonthValue() == mesAtual)
            .toList();
    }
}