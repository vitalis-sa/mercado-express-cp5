package fiap.mercadoExpress;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class MercadoExpressApplication {

	public static void main(String[] args) {
		System.setProperty("javax.net.ssl.trustStoreType", "Windows-ROOT");
		SpringApplication.run(MercadoExpressApplication.class, args);
	}

}
