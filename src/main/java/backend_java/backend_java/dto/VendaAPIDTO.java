package backend_java.backend_java.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public class VendaAPIDTO {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("vendedor_id")
    private Long vendedorId;
    
    @JsonProperty("vendedor_codigo")
    private String vendedorCodigo;
    
    @JsonProperty("total_venda")
    private Double totalVenda;
    
    @JsonProperty("comissao_valor")
    private Double comissaoValor;
    
    @JsonProperty("data_venda")
    private String dataVenda;
    
    @JsonProperty("comissao_percentual")
    private Double comissaoPercentual;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVendedorId() { return vendedorId; }
    public void setVendedorId(Long vendedorId) { this.vendedorId = vendedorId; }

    public String getVendedorCodigo() { return vendedorCodigo; }
    public void setVendedorCodigo(String vendedorCodigo) { this.vendedorCodigo = vendedorCodigo; }

    public Double getTotalVenda() { return totalVenda; }
    public void setTotalVenda(Double totalVenda) { this.totalVenda = totalVenda; }

    public Double getComissaoValor() { return comissaoValor; }
    public void setComissaoValor(Double comissaoValor) { this.comissaoValor = comissaoValor; }

    public String getDataVenda() { return dataVenda; }
    public void setDataVenda(String dataVenda) { this.dataVenda = dataVenda; }

    public Double getComissaoPercentual() { return comissaoPercentual; }
    public void setComissaoPercentual(Double comissaoPercentual) { this.comissaoPercentual = comissaoPercentual; }
}
