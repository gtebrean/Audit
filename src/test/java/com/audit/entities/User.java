package com.audit.entities;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class User {

  private String firstName;
  private int age;
  private BigDecimal walletBalance;
  private Subscription subscription;
  private List<Vehicle> vehicles;

  public User(
      String firstName,
      int age,
      BigDecimal walletAmount,
      Subscription subscription,
      List<Vehicle> vehicles) {
    this.firstName = firstName;
    this.age = age;
    this.walletBalance = walletAmount;
    this.subscription = subscription;
    this.vehicles = vehicles;
  }
}
