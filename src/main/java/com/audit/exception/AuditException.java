package com.audit.exception;

public abstract class AuditException extends Exception {
  public AuditException(String message) {
    super(message);
  }
}
