package com.spring.annotation;

import java.util.function.Supplier;

import org.springframework.stereotype.Repository;

//@Component
@Repository // Repository est plus précis que Component (mais il est à titre descriptif seulement)
public class HardcodedSupplier implements Supplier<String> {
  @Override
  public String get() {
    return "Hello world";
  }
}