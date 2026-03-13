// backend_java/backend_java/service/IntegracaoService.java
package backend_java.backend_java.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class IntegracaoService {

    @Autowired
    private RestTemplate restTemplate;

    // 🔥 CRIANDO INSTÂNCIA DIRETAMENTE (SEM @Autowired)
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private backend_java.backend_java.repository.FuncionarioRepository funcionarioRepository;

    @Autowired
    private backend_java.backend_java.repository.ComissaoRepository comissaoRepository;

    private final String API_VENDAS = "https://alphasolutions-0kn3.onrender.com/api_vendas.php?token=chave";
    private final String API_COMISSOES = "https://alphasolutions-0kn3.onrender.com/api_comissoes_vendedores.php?token=chave";

    // ========== MÉTODOS DE TESTE ==========

    /**
     * Testa a API de vendas e retorna os dados formatados
     */
    public String testarAPIVendas() {
        try {
            String json = restTemplate.getForObject(API_VENDAS, String.class);
            return "✅ API VENDAS respondeu:\n" + formatarJSON(json);
        } catch (Exception e) {
            return "❌ Erro ao acessar API VENDAS: " + e.getMessage();
        }
    }

    /**
     * Testa a API de comissões e retorna os dados formatados
     */
    public String testarAPIComissoes() {
        try {
            String json = restTemplate.getForObject(API_COMISSOES, String.class);
            return "✅ API COMISSÕES respondeu:\n" + formatarJSON(json);
        } catch (Exception e) {
            return "❌ Erro ao acessar API COMISSÕES: " + e.getMessage();
        }
    }

    /**
     * Versão simplificada que retorna apenas status online/offline
     */
    public String testarConexaoSimples() {
        StringBuilder sb = new StringBuilder();

        try {
            restTemplate.getForObject(API_VENDAS, String.class);
            sb.append("✅ API VENDAS: OK\n");
        } catch (Exception e) {
            sb.append("❌ API VENDAS: ").append(e.getMessage()).append("\n");
        }

        try {
            restTemplate.getForObject(API_COMISSOES, String.class);
            sb.append("✅ API COMISSÕES: OK");
        } catch (Exception e) {
            sb.append("❌ API COMISSÕES: ").append(e.getMessage());
        }

        return sb.toString();
    }

    // ========== MÉTODO PRINCIPAL DE IMPORTAÇÃO ==========

    /**
     * Importa as vendas da API e registra comissões no banco de dados
     */
    @Transactional
    public String importarVendas() {
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("🚀 INICIANDO IMPORTAÇÃO DE VENDAS\n");
        relatorio.append("================================\n");

        try {
            // 1. Buscar dados da API
            String json = restTemplate.getForObject(API_VENDAS, String.class);
            relatorio.append("📦 Vendas recebidas: ").append(json.length()).append(" caracteres\n");

            // 2. Converter JSON para árvore de nós
            JsonNode root = objectMapper.readTree(json);

            if (!root.isArray()) {
                return "❌ Resposta não é um array JSON";
            }

            int importadas = 0;
            int ignoradas = 0;

            // 3. Percorrer cada venda
            for (JsonNode venda : root) {
                Long id = venda.has("id") ? venda.get("id").asLong() : null;
                Long vendedorId = venda.has("vendedor_id") && !venda.get("vendedor_id").isNull()
                        ? venda.get("vendedor_id").asLong() : null;

                relatorio.append("\n📦 Processando venda #").append(id);

                // 3.1 Verificar se tem vendedor
                if (vendedorId == null) {
                    relatorio.append(" → ⏭️ Ignorada (sem vendedor)\n");
                    ignoradas++;
                    continue;
                }

                // 3.2 Verificar se vendedor existe no banco
                var funcionarioOpt = funcionarioRepository.findById(vendedorId);
                if (funcionarioOpt.isEmpty()) {
                    relatorio.append(" → ⏭️ Vendedor ID ").append(vendedorId).append(" não encontrado no SRH\n");
                    ignoradas++;
                    continue;
                }

                var funcionario = funcionarioOpt.get();

                // 3.3 Verificar se é do departamento Vendas
                if (!"Vendas".equalsIgnoreCase(funcionario.getDepartamento())) {
                    relatorio.append(" → ⏭️ Funcionário não é vendedor\n");
                    ignoradas++;
                    continue;
                }

                // 3.4 Extrair dados da venda
                Double totalVenda = venda.get("total_venda").asDouble();
                Double comissaoValor = venda.get("comissao_valor").asDouble();
                Double percentual = venda.has("comissao_percentual") ? venda.get("comissao_percentual").asDouble() : 5.0;
                String dataStr = venda.get("data_venda").asText();

                // Converter data
                LocalDate dataVenda = LocalDate.parse(dataStr.substring(0, 10), DateTimeFormatter.ISO_DATE);
                int mes = dataVenda.getMonthValue();
                int ano = dataVenda.getYear();

                // 3.5 Verificar duplicata (pela observação)
                String idExterno = "API_ID:" + id;
                boolean jaExiste = comissaoRepository.findAll().stream()
                        .anyMatch(c -> c.getObservacao() != null && c.getObservacao().contains(idExterno));

                if (jaExiste) {
                    relatorio.append(" → ⏭️ Venda já importada anteriormente\n");
                    ignoradas++;
                    continue;
                }

                // 3.6 Criar e salvar comissão
                backend_java.backend_java.model.Comissao comissao = new backend_java.backend_java.model.Comissao();
                comissao.setFuncionario(funcionario);
                comissao.setValorVenda(totalVenda);
                comissao.setPercentualComissao(percentual);
                comissao.setValorComissao(comissaoValor);
                comissao.setMes(mes);
                comissao.setAno(ano);
                comissao.setDataVenda(dataVenda);
                comissao.setDataRegistro(LocalDateTime.now());
                comissao.setObservacao("Importado via API - ID:" + id);

                // Verificar meta
                Double meta = funcionario.getMetaMensal() != null ? funcionario.getMetaMensal() : 50000.0;
                comissao.setMetaAtingida(totalVenda >= meta);

                // Salvar no banco
                comissaoRepository.save(comissao);
                importadas++;

                relatorio.append(" → ✅ Comissão de ").append(comissaoValor)
                        .append(" MTn para ").append(funcionario.getNomeCompleto()).append("\n");
            }

            // 4. Resumo final
            relatorio.append("\n================================\n");
            relatorio.append("📊 RESUMO DA IMPORTAÇÃO:\n");
            relatorio.append("   Total de vendas: ").append(root.size()).append("\n");
            relatorio.append("   Comissões importadas: ").append(importadas).append("\n");
            relatorio.append("   Vendas ignoradas: ").append(ignoradas).append("\n");
            relatorio.append("================================\n");

        } catch (Exception e) {
            relatorio.append("❌ ERRO GERAL: ").append(e.getMessage());
            e.printStackTrace();
        }

        return relatorio.toString();
    }

    // ========== MÉTODO AUXILIAR ==========

    /**
     * Formata JSON para exibição mais legível
     */
    private String formatarJSON(String json) {
        try {
            Object obj = objectMapper.readValue(json, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return json;
        }
    }
}