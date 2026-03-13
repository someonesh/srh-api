package backend_java.backend_java;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BackendJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendJavaApplication.class, args);
        
        System.out.println("\n✅ API SRH rodando com sucesso!");
        System.out.println("📌 Acesse: http://localhost:8081");
        System.out.println("📌 API: http://localhost:8081/api/funcionarios\n");
    }

    @Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}
}