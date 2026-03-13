package backend_java.backend_java.dto;


import java.time.LocalDate;

public class FeriasRequestDTO {
    private Long funcionarioId;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String observacao;
    
    // Construtor vazio
    public FeriasRequestDTO() {}
    
    // Getters e Setters
    public Long getFuncionarioId() {
        return funcionarioId;
    }
    
    public void setFuncionarioId(Long funcionarioId) {
        this.funcionarioId = funcionarioId;
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
    
    public String getObservacao() {
        return observacao;
    }
    
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
