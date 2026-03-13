package backend_java.backend_java.repository;


import backend_java.backend_java.model.Ferias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface FeriasRepository extends JpaRepository<Ferias, Long> {
    
    // Buscar por funcionário
    List<Ferias> findByFuncionarioId(Long funcionarioId);
    
    // Buscar por status
    List<Ferias> findByStatus(String status);
    
    // Buscar por período
    @Query("SELECT f FROM Ferias f WHERE f.dataInicio BETWEEN :inicio AND :fim OR f.dataFim BETWEEN :inicio AND :fim")
    List<Ferias> findByPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
    
    // Buscar férias aprovadas de um funcionário
    @Query("SELECT f FROM Ferias f WHERE f.funcionario.id = :funcionarioId AND f.status = 'aprovadas'")
    List<Ferias> findFeriasAprovadasByFuncionario(@Param("funcionarioId") Long funcionarioId);
    
    // Buscar próximas férias (a partir de hoje)
    @Query("SELECT f FROM Ferias f WHERE f.dataInicio >= CURRENT_DATE AND f.status = 'aprovadas' ORDER BY f.dataInicio ASC")
    List<Ferias> findProximasFerias();
    
    // Buscar férias de um mês específico
    @Query("SELECT f FROM Ferias f WHERE MONTH(f.dataInicio) = :mes AND YEAR(f.dataInicio) = :ano AND f.status = 'aprovadas'")
    List<Ferias> findByMesAndAno(@Param("mes") int mes, @Param("ano") int ano);
    
    // Contar por status
    @Query("SELECT COUNT(f) FROM Ferias f WHERE f.status = :status")
    long countByStatus(@Param("status") String status);
    
    // Verificar se já existe solicitação pendente para o funcionário no período
    @Query("SELECT COUNT(f) > 0 FROM Ferias f WHERE f.funcionario.id = :funcionarioId AND f.status = 'pendentes'")
    boolean existsSolicitacaoPendente(@Param("funcionarioId") Long funcionarioId);
    
    // Buscar férias pendentes para aprovação
    @Query("SELECT f FROM Ferias f WHERE f.status = 'pendentes' ORDER BY f.dataSolicitacao ASC")
    List<Ferias> findPendentesOrderByDataSolicitacao();
}
