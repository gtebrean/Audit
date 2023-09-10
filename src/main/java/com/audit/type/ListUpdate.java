package com.audit.type;

import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ListUpdate implements ChangeType {
  private String property;
  private List<Object> added;
  private List<String> removed;

  public ListUpdate(String property, List<Object> added, List<String> removed) {
    this.property = property;
    this.added = added;
    this.removed = removed;
  }

  @Override
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
    final ListUpdate other = (ListUpdate) obj;
    return property.equals(other.property)
        && Objects.equals(added, other.added)
        && Objects.equals(removed, other.removed);
  }
}
