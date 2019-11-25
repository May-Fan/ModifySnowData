import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PreDestroy;

@EnableScheduling
@SpringBootApplication
@ComponentScan("cn.skio.crms")
public class ModifierApplication {


  private static ConfigurableApplicationContext context;

  public static void main(String[] args) {
    ModifierApplication.context =
            SpringApplication.run(ModifierApplication.class, args);
  }

  @PreDestroy
  public void close() {
    ModifierApplication.context.close();}


}
