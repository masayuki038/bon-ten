package net.wrap_trap.bonten;

public class BontenException extends RuntimeException {

  private static final long serialVersionUID = 7288186307319016028L;

  public BontenException(Throwable cause) {
    super(cause);
  }

  public BontenException(String cause) {
    super(cause);
  }
  
  public BontenException(String message, Throwable cause) {
    super(message, cause);
  }

  public BontenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
