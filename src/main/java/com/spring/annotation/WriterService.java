package com.spring.annotation;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//@Component
@Service // Service est plus précis que Component (mais il est à titre descriptif seulement)
public class WriterService implements Runnable {

  @Autowired(required = false) // Cette annotation permet de supprimer la méthode de fabrique passée en commentaire
  private Supplier<String> supplier;

  // public WriterService(Supplier<String> supplier) {
  //   this.supplier = supplier;
  // }

  @Override
  public void run() {
    if (supplier == null) {
      System.out.println("Aucun fournisseur de données disponible.");
    } else {
      System.out.println(supplier.get());
    }
  }

}
