package org.mytest.catchme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CatchMeApplication {
	private static final Logger logger = LoggerFactory.getLogger(CatchMeApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CatchMeApplication.class, args);
		logger.info("🟢 CatchMe Steganography backend started!");
	}
}
