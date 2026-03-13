package backend_java.backend_java.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import backend_java.backend_java.model.Vendedor;

@Repository
public interface VendedorRepository extends JpaRepository<Vendedor, Long> {
    
    /**
     * Busca vendedor por código
     */
    Optional<Vendedor> findByCodigo(String codigo);
    
    /**
     * Busca vendedor ativo por código
     */
    Optional<Vendedor> findByCodigoAndAtivoTrue(String codigo);
    
    /**
     * Lista todos os vendedores ativos
     */
    List<Vendedor> findByAtivoTrue();
    
    /**
     * Lista todos os vendedores inativos
     */
    List<Vendedor> findByAtivoFalse();
    
    /**
     * Busca vendedores por nome (busca parcial)
     */
    List<Vendedor> findByNomeCompletoContainingIgnoreCase(String nome);
    
    /**
     * Verifica se código já existe
     */
    boolean existsByCodigo(String codigo);
    
    /**
     * Busca vendedores com comissão acima de um percentual
     * ✅ AGORA USA O CAMPO CORRETO 'comissao'
     */
    List<Vendedor> findByComissaoGreaterThan(Double percentual);
    
    /**
     * Conta vendedores ativos
     */
    long countByAtivoTrue();
    
    /**
     * Atualiza o status de um vendedor
     */
    @Modifying
    @Transactional
    @Query("UPDATE Vendedor v SET v.ativo = :ativo WHERE v.id = :id")
    int updateStatus(@Param("id") Long id, @Param("ativo") Boolean ativo);
    
    /**
     * Busca o próximo ID disponível (para gerar código)
     */
    @Query("SELECT MAX(v.id) FROM Vendedor v")
    Long findMaxId();
}