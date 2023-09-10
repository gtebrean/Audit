package com.audit.exception;

public class AccessAuditException extends AuditException {
  public AccessAuditException(String name, String message) {
    super(String.format("Exception in accessing field %s: %s", name, message));
  }
}
