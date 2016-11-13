package io.flic.lib;

/**
 * Exception thrown when app credentials were not provided.
 */
public class AppCredentialsNotProvidedException extends RuntimeException {

  private static final long serialVersionUID = -9167684352894197754L;

  AppCredentialsNotProvidedException(String s) {
    super(s);
  }
}
