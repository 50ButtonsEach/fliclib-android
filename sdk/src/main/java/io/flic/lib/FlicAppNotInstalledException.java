package io.flic.lib;

/**
 * Exception thrown when the Flic App was not installed
 */
public class FlicAppNotInstalledException extends RuntimeException {
  private static final long serialVersionUID = 4452641139128143951L;

  FlicAppNotInstalledException(String s) {
    super(s);
  }
}
