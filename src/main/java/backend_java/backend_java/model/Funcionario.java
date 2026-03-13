package backend_java.backend_java.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "funcionarios")
@Inheritance(strategy = InheritanceType.JOINED)
public class Funcionario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome completo é obrigatório")
    @Column(name = "nome_completo", nullable = false, length = 150)
    private String nomeCompleto;
    
    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Column(unique = true, length = 100)
    private String email;
    
    @NotBlank(message = "Telefone é obrigatório")
    @Column(length = 20)
    private String telefone;
    
    @NotNull(message = "Salário base é obrigatório")
    @Positive(message = "Salário deve ser positivo")
    @Column(name = "salario_base", nullable = false)
    private Double salarioBase;
    
    @NotBlank(message = "Cargo é obrigatório")
    @Column(length = 50)
    private String cargo;
    
    @NotBlank(message = "Departamento é obrigatório")
    @Column(length = 50)
    private String departamento;
    
    @NotNull(message = "Data de contratação é obrigatória")
    @PastOrPresent(message = "Data de contratação inválida")
    @Column(name = "data_contratacao", nullable = false)
    private LocalDate dataContratacao;
    
    @Column(name = "data_demissao")
    private LocalDate dataDemissao;
    
    @Column(length = 200)
    private String endereco;
    
    @Column(name = "tipo_contrato", length = 30)
    private String tipoContrato;
    
    private Boolean ativo = true;
    
    // ========== NOVOS CAMPOS PARA VENDEDORES ==========
    
    @Column(name = "percentual_comissao")
    private Double percentualComissao = 5.0; // Valor padrão 5%
    
    @Column(name = "meta_mensal")
    private Double metaMensal = 50000.0; // Meta padrão 50.000 MZN
    
    @Column(name = "codigo_vendedor", unique = true)
    private String codigoVendedor;
    
    @Column(name = "total_vendas_mes")
    private Double totalVendasMes = 0.0;
    
    // ==================================================
    
    // Construtor vazio
    public Funcionario() {}
    
    // Método para verificar se é vendedor (baseado no departamento)
    public boolean isVendedor() {
        return "Vendas".equalsIgnoreCase(this.departamento);
    }
    
    // Método para calcular comissão baseado em um valor de venda
    public Double calcularComissao(Double valorVenda) {
        if (!isVendedor() || valorVenda == null || valorVenda <= 0) {
            return 0.0;
        }
        Double percentual = this.percentualComissao != null ? this.percentualComissao : 5.0;
        return valorVenda * (percentual / 100);
    }
    
    // Método para verificar se atingiu a meta
    public boolean atingiuMeta(Double valorVenda) {
        if (!isVendedor() || valorVenda == null) {
            return false;
        }
        Double meta = this.metaMensal != null ? this.metaMensal : 50000.0;
        return valorVenda >= meta;
    }
    
    // Método para adicionar venda ao total do mês
    public void adicionarVenda(Double valorVenda) {
        if (isVendedor() && valorVenda != null && valorVenda > 0) {
            this.totalVendasMes = (this.totalVendasMes != null ? this.totalVendasMes : 0) + valorVenda;
        }
    }
    
    // Getters e Setters dos novos campos
    public Double getPercentualComissao() {
        return percentualComissao;
    }
    
    public void setPercentualComissao(Double percentualComissao) {
        this.percentualComissao = percentualComissao;
    }
    
    public Double getMetaMensal() {
        return metaMensal;
    }
    
    public void setMetaMensal(Double metaMensal) {
        this.metaMensal = metaMensal;
    }
    
    public String getCodigoVendedor() {
        return codigoVendedor;
    }
    
    public void setCodigoVendedor(String codigoVendedor) {
        this.codigoVendedor = codigoVendedor;
    }
    
    public Double getTotalVendasMes() {
        return totalVendasMes;
    }
    
    public void setTotalVendasMes(Double totalVendasMes) {
        this.totalVendasMes = totalVendasMes;
    }
    
    // Getters e Setters existentes
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    
    public Double getSalarioBase() { return salarioBase; }
    public void setSalarioBase(Double salarioBase) { this.salarioBase = salarioBase; }
    
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    
    public LocalDate getDataContratacao() { return dataContratacao; }
    public void setDataContratacao(LocalDate dataContratacao) { this.dataContratacao = dataContratacao; }
    
    public LocalDate getDataDemissao() { return dataDemissao; }
    public void setDataDemissao(LocalDate dataDemissao) { 
        this.dataDemissao = dataDemissao; 
        if (dataDemissao != null) {
            this.ativo = false;
        }
    }
    
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    
    public String getTipoContrato() { return tipoContrato; }
    public void setTipoContrato(String tipoContrato) { this.tipoContrato = tipoContrato; }
    
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { 
        this.ativo = ativo;
        if (ativo && this.dataDemissao != null) {
            this.dataDemissao = null; // Se reativar, limpa data de demissão
        }
    }

    @Override
    public String toString() {
        return "Funcionario{" +
                "id=" + id +
                ", nomeCompleto='" + nomeCompleto + '\'' +
                ", cargo='" + cargo + '\'' +
                ", departamento='" + departamento + '\'' +
                ", percentualComissao=" + (isVendedor() ? percentualComissao : "N/A") +
                ", metaMensal=" + (isVendedor() ? metaMensal : "N/A") +
                ", ativo=" + ativo +
                '}';
    }
}