package com.audit.entities;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class WrongVehicle implements Vehicle {
  private String primary;
  private String displayName;
  private List<String> services;

  public WrongVehicle(String primary, String displayName, List<String> services) {
    this.primary = primary;
    this.displayName = displayName;
    this.services = services;
  }
}
