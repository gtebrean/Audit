package com.audit.exception;

public class DifferentObjectsAuditException extends AuditException {

  public DifferentObjectsAuditException() {
    super("Object types are different!");
  }
}
