package backend_java.backend_java.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ferias")
public class Ferias {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;
    
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;
    
    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;
    
    @Column(nullable = false)
    private String status; // "pendentes", "aprovadas", "rejeitadas"
    
    @Column(name = "data_solicitacao")
    private LocalDateTime dataSolicitacao;
    
    @Column(name = "data_aprovacao")
    private LocalDateTime dataAprovacao;
    
    @Column(length = 500)
    private String observacao;
    
    // Construtor vazio
    public Ferias() {
        this.dataSolicitacao = LocalDateTime.now();
        this.status = "pendentes";
    }
    
    // Construtor com parâmetros
    public Ferias(Funcionario funcionario, LocalDate dataInicio, LocalDate dataFim) {
        this.funcionario = funcionario;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.dataSolicitacao = LocalDateTime.now();
        this.status = "pendentes";
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
    
    public LocalDate getDataInicio() {
        return dataInicio;
    }
    
    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }
    
    public LocalDate getDataFim() {
        return dataFim;
    }
    
    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }
    
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }
    
    public LocalDateTime getDataAprovacao() {
        return dataAprovacao;
    }
    
    public void setDataAprovacao(LocalDateTime dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }
    
    public String getObservacao() {
        return observacao;
    }
    
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
    
    // Método para calcular dias de férias
    public int getDias() {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
    }
    
    @Override
    public String toString() {
        return "Ferias{" +
                "id=" + id +
                ", funcionario=" + funcionario.getNomeCompleto() +
                ", periodo=" + dataInicio + " a " + dataFim +
                ", status='" + status + '\'' +
                '}';
    }
}
