package com.audit.entities;

import com.audit.item.NestedItem;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@NestedItem
@Getter
@Setter
public class Payment {

  private BigDecimal value;

  public Payment(BigDecimal value) {
    this.value = value;
  }
}
