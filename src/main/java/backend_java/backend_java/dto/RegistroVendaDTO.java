package backend_java.backend_java.dto;

public class RegistroVendaDTO {
    private Long funcionarioId;
    private Double valorVenda;
    private Integer mes;
    private Integer ano;
    private String observacao;
    private Boolean calcularDetalhado;
    
    // Getters e Setters
    public Long getFuncionarioId() {
        return funcionarioId;
    }
    
    public void setFuncionarioId(Long funcionarioId) {
        this.funcionarioId = funcionarioId;
    }
    
    public Double getValorVenda() {
        return valorVenda;
    }
    
    public void setValorVenda(Double valorVenda) {
        this.valorVenda = valorVenda;
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
    
    public String getObservacao() {
        return observacao;
    }
    
    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
    
    public Boolean getCalcularDetalhado() {
        return calcularDetalhado;
    }
    
    public void setCalcularDetalhado(Boolean calcularDetalhado) {
        this.calcularDetalhado = calcularDetalhado;
    }
}
