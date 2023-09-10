package com.audit;

import com.audit.exception.AccessAuditException;
import com.audit.exception.AuditException;
import com.audit.exception.DifferentObjectsAuditException;
import com.audit.exception.InvalidFieldsAuditException;
import com.audit.item.AuditKey;
import com.audit.item.NestedItem;
import com.audit.type.ChangeType;
import com.audit.type.ListUpdate;
import com.audit.type.PropertyUpdate;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class DiffTool {

  public List<ChangeType> diff(Object previous, Object current) throws AuditException {
    if (previous == null || current == null) {
      return null;
    }

    Class<?> typeClass = previous.getClass();
    if (typeClass != current.getClass()) {
      throw new DifferentObjectsAuditException();
    }

    List<ChangeType> changes = new ArrayList<>();

    Field[] fields = typeClass.getDeclaredFields();

    for (Field field : fields) {
      processField(field, previous, current, changes);
    }
    return changes;
  }

  private void processField(Field field, Object previous, Object current, List<ChangeType> changes)
      throws AuditException {
    try {
      field.setAccessible(true);
      String filedName = field.getName();

      Class<?> fieldType = field.getType();

      if (fieldType.isAnnotationPresent(NestedItem.class)) {
        processNestedField(field, previous, current).stream()
            .forEach(
                c -> {
                  c.buildPath(filedName);
                  changes.add(c);
                });
      }

      // Check if the field's type is a ParameterizedType: List<Object> by verifying if it is List
      // assignable, avoiding the classic `field.getGenericType() instanceof ParameterizedType`
      if (List.class.isAssignableFrom(field.getType())) {
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        Class<?> elementType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        changes.addAll(
            Optional.ofNullable(processListItems(field, elementType, previous, current))
                .orElse(new ArrayList<>()));
      }

      // Check if the field's type is not a nested item
      if (!fieldType.isAnnotationPresent(NestedItem.class)
          && !fieldType.isArray()
          && !Collection.class.isAssignableFrom(fieldType)) {
        ChangeType change = processSimpleField(field, previous, current);
        if (change != null) {
          changes.add(change);
        }
      }

    } catch (IllegalAccessException e) {
      throw new AccessAuditException(field.getName(), e.getMessage());
    }
  }

  private List<ChangeType> processListItems(
      Field field, Class<?> elementType, Object previous, Object current)
      throws AuditException, IllegalAccessException {
    List<ChangeType> changes = new ArrayList<>();

    List previousList = extractList(field, previous);
    List currentList = extractList(field, current);

    List removed = new ArrayList();
    List added;

    if (elementType.isAnnotationPresent(NestedItem.class)) {
      List matched = new ArrayList();

      for (Object obj : previousList) {
        Field id = getIdField(obj);
        id.setAccessible(true);
        Object value = id.get(obj);
        Object match =
            currentList.stream()
                .filter(o -> assertIds(o, id.getName(), value))
                .findFirst()
                .orElse(null);

        if (match != null) {
          changes = diff(obj, match);
          changes.stream()
              .forEachOrdered(
                  changeType -> changeType.buildPath(field.getName() + "[" + value + "]"));

          matched.add(match);

        } else {
          removed.add(obj);
        }
      }

      added = generateListDifference(currentList, matched);

    } else {
      added = generateListDifference(currentList, previousList);
      removed = generateListDifference(previousList, currentList);
    }

    if (!(added.isEmpty() && removed.isEmpty())) {
      changes.add(new ListUpdate(field.getName(), added, removed));
    }

    return changes;
  }

  private List<ChangeType> processNestedField(Field nestedField, Object previous, Object current)
      throws AuditException, IllegalAccessException {
    List<ChangeType> changes = new ArrayList<>();

    Object nestedPrevious = getObjectFromField(previous, nestedField);
    Object nestedCurrent = getObjectFromField(current, nestedField);

    Field[] fields = nestedField.getType().getDeclaredFields();

    for (Field field : fields) {
      processField(field, nestedPrevious, nestedCurrent, changes);
    }

    return changes;
  }

  private PropertyUpdate processSimpleField(Field field, Object previous, Object current)
      throws IllegalAccessException {
    Object valuePrevious = getObjectFromField(previous, field);
    Object valueCurrent = getObjectFromField(current, field);
    if (valuePrevious == null && valueCurrent == null) {
      return null;
    }
    if (valuePrevious == null || valueCurrent == null) {
      return new PropertyUpdate(
          field.getName(),
          valuePrevious == null ? "null" : valuePrevious.toString(),
          valueCurrent == null ? "null" : valueCurrent.toString());
    }
    if (!valuePrevious.equals(valueCurrent)) {
      return new PropertyUpdate(field.getName(), valuePrevious.toString(), valueCurrent.toString());
    }
    return null;
  }

  private Object getObjectFromField(Object obj, Field field) throws IllegalAccessException {
    return obj == null ? null : field.get(obj);
  }

  private Field getIdField(Object obj) throws InvalidFieldsAuditException {
    Field[] fields = obj.getClass().getDeclaredFields();
    return Arrays.stream(fields)
        .filter(f -> f.getName().equals("id") || f.isAnnotationPresent(AuditKey.class))
        .findFirst()
        .orElseThrow(() -> new InvalidFieldsAuditException());
  }

  private boolean assertIds(Object object, String fieldName, Object value) {
    try {
      Field classField = object.getClass().getDeclaredField(fieldName);
      classField.setAccessible(true);
      return classField.get(object).equals(value);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  private List generateListDifference(List l1, List l2) {
    return l1.stream().filter(e -> !l2.contains(e)).toList();
  }

  private List extractList(Field field, Object obj) throws IllegalAccessException {
    return obj == null
        ? new ArrayList()
        : Optional.ofNullable((List) field.get(obj)).orElse(new ArrayList());
  }
}
