package com.spring.annotation;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WriterService implements Runnable {

  @Autowired(required = false)
  private Supplier<String> supplier;

  @Override
  public void run() {
    if (supplier == null) {
      System.out.println("Aucun fournisseur de données disponible.");
    } else {
      System.out.println(supplier.get());
    }
  }

}
