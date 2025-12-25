package com.spring.annotation;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan // Cette annotation permet de supprimer les fonction de fabriques passées en commentaire
@SpringBootApplication
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
