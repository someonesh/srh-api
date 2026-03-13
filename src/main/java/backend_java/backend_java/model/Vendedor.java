package backend_java.backend_java.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "vendedores")
@PrimaryKeyJoinColumn(name = "funcionario_id")
public class Vendedor extends Funcionario {
    
    @Column(unique = true, length = 20)
    private String codigo;
    
    // ✅ ÚNICA COLUNA - Percentual de comissão
    @Column(name = "comissao")
    private Double comissao;  // valor padrão 5%
    
    @Column(name = "meta_mensal")
    private Double metaMensal = 50000.0;
    
    public Vendedor() {
        super();
        this.setDepartamento("Vendas");
    }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public Double getComissao() { return comissao; }
    public void setComissao(Double comissao) { this.comissao = comissao; }
    
    public Double getMetaMensal() { return metaMensal; }
    public void setMetaMensal(Double metaMensal) { this.metaMensal = metaMensal; }
    
    /**
     * Calcula a comissão baseada no percentual
     */
    public Double calcularComissao(Double totalVendas) {
        if (comissao == null) return 0.0;
        return totalVendas * (comissao / 100);
    }
    
    


    @Override
    public String toString() {
        return "Vendedor{" +
                "id=" + getId() +
                ", codigo='" + codigo + '\'' +
                ", nome='" + getNomeCompleto() + '\'' +
                ", comissao=" + comissao + "%" +
                ", metaMensal=" + metaMensal +
                ", ativo=" + getAtivo() +
                '}';
    }
}