package it.gioxi.statemachine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class StateMachineDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(StateMachineDemoApplication.class, args);
	}

}
