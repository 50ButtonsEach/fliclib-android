package io.flic.lib;

/**
 * Exception thrown when app credentials were not provided.
 */
public class AppCredentialsNotProvidedException extends RuntimeException {
	AppCredentialsNotProvidedException(String s) {
		super(s);
	}
}
