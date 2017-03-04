package au.com.greater.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * This is the main entry point for the application.
 *
 * @author Justin Lewis Salmon
 */
@SpringBootApplication
@EnableScheduling
public class Main {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class);
  }
}
