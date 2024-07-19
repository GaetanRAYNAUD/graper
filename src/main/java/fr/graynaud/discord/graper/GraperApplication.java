package fr.graynaud.discord.graper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GraperApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraperApplication.class, args);
	}

}
