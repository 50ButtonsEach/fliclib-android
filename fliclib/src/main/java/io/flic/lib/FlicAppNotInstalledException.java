package io.flic.lib;

/**
 * Exception thrown when the Flic App was not installed
 */
public class FlicAppNotInstalledException extends RuntimeException {
	FlicAppNotInstalledException(String s) {
		super(s);
	}
}
