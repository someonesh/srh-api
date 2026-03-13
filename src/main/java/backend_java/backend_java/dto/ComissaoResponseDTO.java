package backend_java.backend_java.dto;


public class ComissaoResponseDTO {
    private Long comissaoId;
    private Long funcionarioId;
    private String funcionarioNome;
    private Double valorVenda;
    private Double percentualComissao;
    private Double valorComissao;
    private Double metaMensal;
    private Boolean metaAtingida;
    private Double bonus;
    private Integer mes;
    private Integer ano;
    private String mensagem;
    
    // Construtor padrão
    public ComissaoResponseDTO() {}
    
    // Getters e Setters
    public Long getComissaoId() {
        return comissaoId;
    }
    
    public void setComissaoId(Long comissaoId) {
        this.comissaoId = comissaoId;
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
    
    public Double getMetaMensal() {
        return metaMensal;
    }
    
    public void setMetaMensal(Double metaMensal) {
        this.metaMensal = metaMensal;
    }
    
    public Boolean getMetaAtingida() {
        return metaAtingida;
    }
    
    public void setMetaAtingida(Boolean metaAtingida) {
        this.metaAtingida = metaAtingida;
    }
    
    public Double getBonus() {
        return bonus;
    }
    
    public void setBonus(Double bonus) {
        this.bonus = bonus;
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
    
    public String getMensagem() {
        return mensagem;
    }
    
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}