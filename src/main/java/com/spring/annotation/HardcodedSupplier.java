package com.spring.annotation;

import java.util.function.Supplier;

import org.springframework.stereotype.Component;

@Component
public class HardcodedSupplier implements Supplier<String> {
  @Override
  public String get() {
    return "Hello world";
  }
}