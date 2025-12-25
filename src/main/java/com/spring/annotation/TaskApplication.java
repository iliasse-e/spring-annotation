package com.spring.annotation;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@SpringBootApplication
public class TaskApplication {

  public static void main(String[] args) throws InterruptedException {
    try (AnnotationConfigApplicationContext appCtx =
                  new AnnotationConfigApplicationContext(TaskApplication.class)) {
      appCtx.getBean(Runnable.class).run();
    }
  }

}
