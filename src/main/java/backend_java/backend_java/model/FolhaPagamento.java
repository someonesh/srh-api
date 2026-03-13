package backend_java.backend_java.model;


import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "folhas_pagamento")
public class FolhaPagamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;
    
    @Column(nullable = false)
    private Integer mes;
    
    @Column(nullable = false)
    private Integer ano;
    
    // Proventos
    @Column(name = "salario_base", nullable = false)
    private Double salarioBase;
    
    @Column(name = "comissao")
    private Double comissao; // Comissão do mês
    
    @Column(name = "salario_bruto")
    private Double salarioBruto; // Salário base + comissão
    
    // Descontos
    private Double inss; // 5% do salário bruto
    
    @Column(name = "salario_liquido")
    private Double salarioLiquido;
    
    @Enumerated(EnumType.STRING)
    private StatusPagamento status = StatusPagamento.ATIVO;
    
    @Column(name = "data_calculo")
    private LocalDate dataCalculo = LocalDate.now();
    
    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;
    
    public enum StatusPagamento {
        ATIVO, PROCESSADO, PAGO, CANCELADO
    }
    
    // Construtor vazio
    public FolhaPagamento() {}
    
    // Construtor para criar folha
    public FolhaPagamento(Funcionario funcionario, Integer mes, Integer ano) {
        this.funcionario = funcionario;
        this.mes = mes;
        this.ano = ano;
        this.salarioBase = funcionario.getSalarioBase();
        this.comissao = 0.0;
        this.salarioBruto = this.salarioBase;
        this.inss = this.salarioBruto * 0.05; // 5% de INSS
        this.salarioLiquido = this.salarioBruto - this.inss;
    }
    
    // Método para calcular comissão
    public void calcularComissao(Double valorComissao) {
        this.comissao = valorComissao != null ? valorComissao : 0.0;
        this.salarioBruto = this.salarioBase + this.comissao;
        this.inss = this.salarioBruto * 0.05;
        this.salarioLiquido = this.salarioBruto - this.inss;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }
    
    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }
    
    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }
    
    public Double getSalarioBase() { return salarioBase; }
    public void setSalarioBase(Double salarioBase) { this.salarioBase = salarioBase; }
    
    public Double getComissao() { return comissao; }
    public void setComissao(Double comissao) { this.comissao = comissao; }
    
    public Double getSalarioBruto() { return salarioBruto; }
    public void setSalarioBruto(Double salarioBruto) { this.salarioBruto = salarioBruto; }
    
    public Double getInss() { return inss; }
    public void setInss(Double inss) { this.inss = inss; }
    
    public Double getSalarioLiquido() { return salarioLiquido; }
    public void setSalarioLiquido(Double salarioLiquido) { this.salarioLiquido = salarioLiquido; }
    
    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }
    
    public LocalDate getDataCalculo() { return dataCalculo; }
    public void setDataCalculo(LocalDate dataCalculo) { this.dataCalculo = dataCalculo; }
    
    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }
    
    public String getPeriodo() {
        return String.format("%02d/%d", mes, ano);
    }
}