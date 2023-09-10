package com.audit.entities;

import com.audit.item.AuditKey;

import java.util.List;

public class VehicleWithAdnotation implements Vehicle {

  @AuditKey private String primary;
  private String displayName;
  private List<String> services;

  public VehicleWithAdnotation(String id, String displayName, List<String> services) {
    this.primary = id;
    this.displayName = displayName;
    this.services = services;
  }

  //  @Override
  //  public boolean equals(Object obj) {
  //    if (obj == this) {
  //      return true;
  //    }
  //    if (obj.getClass() != this.getClass()) {
  //      return false;
  //    }
  //    final VehicleWithAdnotation other = (VehicleWithAdnotation) obj;
  //    return primary.equals(other.primary);
  //  }

  @Override
  public String toString() {
    return "id: " + primary + " Name: " + displayName + " Services: " + services;
  }
}
