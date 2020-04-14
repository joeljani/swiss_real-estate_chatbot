package ch.propbuddy.propbuddy;

import ch.propbuddy.propbuddy.scraper.HomeGateScraper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class PropbuddyApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(PropbuddyApplication.class, args);
	}

}
