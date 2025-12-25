package com.spring.annotation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.spring.annotation.access.SecurityAccessConfiguration;
import com.spring.annotation.security.SecurityConfiguration;

@ComponentScan // Cette annotation permet de supprimer les fonction de fabriques passées en commentaire
@Import({SecurityConfiguration.class, SecurityAccessConfiguration.class}) // On importe les class de configuration
public class TaskApplication {

  // @Bean
  // public Supplier<String> dataSupplier() {
  //   return new HardcodedSupplier();
  // }

  // @Bean
  // public Runnable task() {
  //   return new WriterService();
  // }

  public static void main(String[] args) throws InterruptedException {
    try (AnnotationConfigApplicationContext appCtx =
                  new AnnotationConfigApplicationContext(TaskApplication.class)) {
      appCtx.getBean(Runnable.class).run();
    }
  }

}
