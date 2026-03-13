package backend_java.backend_java.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "comissoes")
public class Comissao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;
    
    @Column(nullable = false)
    private Double valorVenda;
    
    @Column(nullable = false)
    private Double percentualComissao;
    
    @Column(nullable = false)
    private Double valorComissao;
    
    private Boolean metaAtingida;
    
    private Integer mes;
    
    private Integer ano;
    
    private LocalDate dataVenda;
    
    private LocalDateTime dataRegistro;
    
    private String observacao;
    
    // Construtores
    public Comissao() {
        this.dataRegistro = LocalDateTime.now();
    }
    
    public Comissao(Funcionario funcionario, Double valorVenda, Double percentualComissao, 
                    Double valorComissao, Integer mes, Integer ano) {
        this.funcionario = funcionario;
        this.valorVenda = valorVenda;
        this.percentualComissao = percentualComissao;
        this.valorComissao = valorComissao;
        this.mes = mes;
        this.ano = ano;
        this.dataVenda = LocalDate.now();
        this.dataRegistro = LocalDateTime.now();
        this.metaAtingida = valorVenda >= (funcionario.getMetaMensal() != null ? 
                                           funcionario.getMetaMensal() : 50000);
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Funcionario getFuncionario() {
        return funcionario;
    }
    
    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
    
    public Double getValorVenda() {
        return valorVenda;
    }
    
    public void setValorVenda(Double valorVenda) {
        this.valorVenda = valorVenda;
    }
    
    public Double getPercentualComissao() {
        return percentualComissao;
    }
    
    public void setPercentualComissao(Double percentualComissao) {
        this.percentualComissao = percentualComissao;
    }
    
    public Double getValorComissao() {
        return valorComissao;
    }
    
    public void setValorComissao(Double valorComissao) {
        this.valorComissao = valorComissao;
    }
    
    public Boolean getMetaAtingida() {
        return metaAtingida;
    }
    
    public void setMetaAtingida(Boolean metaAtingida) {
        this.metaAtingida = metaAtingida;
    }
    
    public Integer getMes() {
        return mes;
    }
    
    public void setMes(Integer mes) {
        this.mes = mes;
    }
    
    public Integer getAno() {
        return ano;
    }
    
    public void setAno(Integer ano) {
        this.ano = ano;
    }
    
    public LocalDate getDataVenda() {
        return dataVenda;
    }
    
    public void setDataVenda(LocalDate dataVenda) {
        this.dataVenda = dataVenda;
    }
    
    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }
    
    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }
    
    public String getObservacao() {
        return observacao;
    }
    
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
