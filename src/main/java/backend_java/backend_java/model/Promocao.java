package backend_java.backend_java.model;


import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "promocoes")
public class Promocao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;
    
    private LocalDate data;
    private String cargoAnterior;
    private String cargoNovo;
    private Double salarioAnterior;
    private Double salarioNovo;
    private String motivo;
    
    // Construtor vazio
    public Promocao() {}
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }
    
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    
    public String getCargoAnterior() { return cargoAnterior; }
    public void setCargoAnterior(String cargoAnterior) { this.cargoAnterior = cargoAnterior; }
    
    public String getCargoNovo() { return cargoNovo; }
    public void setCargoNovo(String cargoNovo) { this.cargoNovo = cargoNovo; }
    
    public Double getSalarioAnterior() { return salarioAnterior; }
    public void setSalarioAnterior(Double salarioAnterior) { this.salarioAnterior = salarioAnterior; }
    
    public Double getSalarioNovo() { return salarioNovo; }
    public void setSalarioNovo(Double salarioNovo) { this.salarioNovo = salarioNovo; }
    
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
