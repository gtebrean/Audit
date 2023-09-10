package com.audit.exception;

public class InvalidFieldsAuditException extends AuditException {

  public InvalidFieldsAuditException() {
    super(
        "Unable to determine changes: Required audit information is missing for processed fields");
  }
}
