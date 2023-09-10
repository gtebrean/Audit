package com.audit.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class PropertyUpdate implements ChangeType {
  public PropertyUpdate(String property, String previous, String current) {
    this.property = property;
    this.previous = previous;
    this.current = current;
  }

  private String property;
  private String previous;
  private String current;

  public void buildPath(String filedName) {
    property = filedName + "." + property;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != this.getClass()) {
      return false;
    }
    final PropertyUpdate other = (PropertyUpdate) obj;
    return property.equals(other.property)
        && previous.equals(other.previous)
        && current.equals(other.current);
  }
}
