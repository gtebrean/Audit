package com.audit.entities;

import com.audit.item.NestedItem;

import lombok.Getter;
import lombok.Setter;

@NestedItem
@Setter
@Getter
public class Subscription {
  private String status;
  private Payment payment;

  public Subscription(String status, Payment payment) {
    this.status = status;
    this.payment = payment;
  }
}
