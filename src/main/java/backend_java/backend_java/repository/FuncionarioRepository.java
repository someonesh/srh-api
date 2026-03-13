package backend_java.backend_java.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend_java.backend_java.model.Funcionario;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    
    // ==============================================
    // BUSCAS POR CAMPOS ÚNICOS
    // ==============================================
    
    // ❌ REMOVIDO: Optional<Funcionario> findByCpf(String cpf);
    
    /**
     * Busca funcionário por Email
     */
    Optional<Funcionario> findByEmail(String email);
    
    // ==============================================
    // BUSCAS POR STATUS
    // ==============================================
    
    List<Funcionario> findByAtivoTrue();
    List<Funcionario> findByAtivoFalse();
    
    // ==============================================
    // BUSCAS POR DEPARTAMENTO E CARGO
    // ==============================================
    
    List<Funcionario> findByDepartamento(String departamento);
    List<Funcionario> findByDepartamentoAndAtivoTrue(String departamento);
    List<Funcionario> findByCargo(String cargo);
    List<Funcionario> findByDepartamentoAndCargo(String departamento, String cargo);
    
    // ==============================================
    // BUSCAS POR NOME
    // ==============================================
    
    List<Funcionario> findByNomeCompletoContainingIgnoreCase(String nomeCompleto);
    List<Funcionario> findByNomeCompletoContainingIgnoreCaseAndDepartamento(
        String nomeCompleto, String departamento);
    
    // ==============================================
    // BUSCAS POR SALÁRIO
    // ==============================================
    
    List<Funcionario> findBySalarioBaseGreaterThan(Double valor);
    List<Funcionario> findBySalarioBaseLessThan(Double valor);
    
    @Query("SELECT f FROM Funcionario f WHERE f.salarioBase BETWEEN :min AND :max")
    List<Funcionario> buscarPorFaixaSalarial(@Param("min") Double min, @Param("max") Double max);
    
    // ==============================================
    // BUSCAS POR TIPO DE CONTRATO
    // ==============================================
    
    List<Funcionario> findByTipoContrato(String tipoContrato);
    List<Funcionario> findByTipoContratoAndAtivoTrue(String tipoContrato);
    
    // ==============================================
    // BUSCAS POR DATA
    // ==============================================
    
    List<Funcionario> findByDataContratacaoAfter(LocalDate data);
    List<Funcionario> findByDataContratacaoBefore(LocalDate data);
    List<Funcionario> findByDataContratacaoBetween(LocalDate inicio, LocalDate fim);
    
    @Query("SELECT f FROM Funcionario f WHERE MONTH(f.dataNascimento) = :mes")
    List<Funcionario> findByMesNascimento(@Param("mes") int mes);
    
    @Query("SELECT f FROM Funcionario f WHERE DAY(f.dataNascimento) = DAY(CURRENT_DATE) AND MONTH(f.dataNascimento) = MONTH(CURRENT_DATE)")
    List<Funcionario> findAniversariantesDoDia();
    
    // ==============================================
    // BUSCAS ESPECÍFICAS PARA VENDEDORES
    // ==============================================
    
    default List<Funcionario> findAllVendedoresAtivos() {
        return findByDepartamentoAndAtivoTrue("Vendas");
    }
    
    // ==============================================
    // CONSULTAS PERSONALIZADAS COM @QUERY
    // ==============================================
    
    @Query("SELECT f FROM Funcionario f WHERE f.departamento = :departamento AND f.salarioBase > (SELECT AVG(f2.salarioBase) FROM Funcionario f2 WHERE f2.departamento = :departamento)")
    List<Funcionario> findAcimaMediaPorDepartamento(@Param("departamento") String departamento);
    
    @Query("SELECT DISTINCT f.departamento FROM Funcionario f ORDER BY f.departamento")
    List<String> findAllDepartamentos();
    
    @Query("SELECT DISTINCT f.cargo FROM Funcionario f ORDER BY f.cargo")
    List<String> findAllCargos();
    
    @Query("SELECT f.departamento, COUNT(f), AVG(f.salarioBase), MIN(f.salarioBase), MAX(f.salarioBase) FROM Funcionario f WHERE f.ativo = true GROUP BY f.departamento")
    List<Object[]> getEstatisticasPorDepartamento();
    
    // ==============================================
    // VERIFICAÇÕES (EXISTS)
    // ==============================================
    
    // ❌ REMOVIDO: boolean existsByCpf(String cpf);
    
    /**
     * Verifica se Email já existe
     */
    boolean existsByEmail(String email);
    
    // ❌ REMOVIDO: @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Funcionario f WHERE f.cpf = :cpf AND f.id != :id")
    // ❌ REMOVIDO: boolean existsByCpfAndIdNot(@Param("cpf") String cpf, @Param("id") Long id);
    
    /**
     * Verifica se existe funcionário com Email diferente do ID fornecido
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Funcionario f WHERE f.email = :email AND f.id != :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);
    
    // ==============================================
    // CONTAGENS (COUNT)
    // ==============================================
    
    long countByDepartamento(String departamento);
    long countByDepartamentoAndAtivoTrue(String departamento);
    long countByAtivoTrue();
    long countByAtivoFalse();
    long countByCargo(String cargo);
    long countByTipoContrato(String tipoContrato);
}