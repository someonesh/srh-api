package backend_java.backend_java.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import backend_java.backend_java.model.Comissao;

public interface ComissaoRepository extends JpaRepository<Comissao, Long> {
    
    List<Comissao> findByFuncionarioId(Long funcionarioId);
    
    List<Comissao> findByFuncionarioIdAndMesAndAno(Long funcionarioId, Integer mes, Integer ano);
    
    List<Comissao> findByMesAndAno(Integer mes, Integer ano);
    
    
    @Query("SELECT SUM(c.valorComissao) FROM Comissao c WHERE c.funcionario.id = :funcionarioId AND c.mes = :mes AND c.ano = :ano")
    Double sumComissoesByFuncionarioAndPeriodo(@Param("funcionarioId") Long funcionarioId, 
                                               @Param("mes") Integer mes, 
                                               @Param("ano") Integer ano);
    
    @Query("SELECT c FROM Comissao c WHERE c.funcionario.departamento = 'Vendas' ORDER BY c.dataRegistro DESC")
    List<Comissao> findUltimasComissoesVendedores();
}
