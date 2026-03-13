package backend_java.backend_java.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FeriasResponseDTO {
    private Long id;
    private Long funcionarioId;
    private String funcionarioNome;
    private String funcionarioCargo;
    private String funcionarioDepartamento;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String status;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataAprovacao;
    private Integer dias;
    private String observacao;
    
    // Construtor vazio
    public FeriasResponseDTO() {}
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getFuncionarioId() {
        return funcionarioId;
    }
    
    public void setFuncionarioId(Long funcionarioId) {
        this.funcionarioId = funcionarioId;
    }
    
    public String getFuncionarioNome() {
        return funcionarioNome;
    }
    
    public void setFuncionarioNome(String funcionarioNome) {
        this.funcionarioNome = funcionarioNome;
    }
    
    public String getFuncionarioCargo() {
        return funcionarioCargo;
    }
    
    public void setFuncionarioCargo(String funcionarioCargo) {
        this.funcionarioCargo = funcionarioCargo;
    }
    
    public String getFuncionarioDepartamento() {
        return funcionarioDepartamento;
    }
    
    public void setFuncionarioDepartamento(String funcionarioDepartamento) {
        this.funcionarioDepartamento = funcionarioDepartamento;
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
    
    public Integer getDias() {
        return dias;
    }
    
    public void setDias(Integer dias) {
        this.dias = dias;
    }
    
    public String getObservacao() {
        return observacao;
    }
    
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}