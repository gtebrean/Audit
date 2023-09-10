package com.audit.entities;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class VehicleWithId implements Vehicle {

  private String id;
  private String displayName;
  private List<String> services;

  public VehicleWithId(String id, String displayName, List<String> services) {
    this.id = id;
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
  //    final VehicleWithId other = (VehicleWithId) obj;
  //    return id.equals(other.id);
  //  }

  @Override
  public String toString() {
    return "id: " + id + " Name: " + displayName + " Services: " + services;
  }
}
