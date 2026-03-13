package backend_java.backend_java.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend_java.backend_java.model.FolhaPagamento;
import backend_java.backend_java.model.Funcionario;

@Repository
public interface FolhaPagamentoRepository extends JpaRepository<FolhaPagamento, Long> {
    
    // Buscar por funcionário
    List<FolhaPagamento> findByFuncionario(Funcionario funcionario);
    
    // Buscar por funcionário e período
    Optional<FolhaPagamento> findByFuncionarioAndMesAndAno(Funcionario funcionario, Integer mes, Integer ano);
    
    // Buscar por período
    List<FolhaPagamento> findByMesAndAno(Integer mes, Integer ano);
    
    // Buscar por status
    List<FolhaPagamento> findByStatus(FolhaPagamento.StatusPagamento status);
    
    // Buscar folhas de um funcionário ordenadas por data
    List<FolhaPagamento> findByFuncionarioOrderByAnoDescMesDesc(Funcionario funcionario);
    
    // Calcular total de salários de um período
    @Query("SELECT SUM(f.salarioLiquido) FROM FolhaPagamento f WHERE f.mes = :mes AND f.ano = :ano")
    Double totalSalariosPeriodo(@Param("mes") Integer mes, @Param("ano") Integer ano);
    
    // Verificar se já existe folha para o funcionário no período
    boolean existsByFuncionarioAndMesAndAno(Funcionario funcionario, Integer mes, Integer ano);
}