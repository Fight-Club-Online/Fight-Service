package Fight_club.Fight_Services;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class FightServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(FightServicesApplication.class, args);
	}

}
