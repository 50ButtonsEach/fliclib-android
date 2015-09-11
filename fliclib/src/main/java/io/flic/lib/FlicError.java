package io.flic.lib;

/**
 * Disconnect errors
 */
public class FlicError {
	public static final int NO_ERROR = 0;
	public static final int CONNECTION_FAILED = 1;
	public static final int UNKNOWN_DATA_RECEIVED = 2;
	public static final int VERIFICATION_TIME_OUT = 3;
	public static final int BACKEND_UNREACHABLE = 4;
	public static final int NO_INTERNET_CONNECTION = 5;
	public static final int CREDENTIALS_NOT_MATCHING = 6;
	public static final int BUTTON_IS_PRIVATE = 7; // button has other h3 than we have
	public static final int CRYPTOGRAPHIC_FAILURE = 8;
	public static final int BUTTON_DISCONNECTED_DURING_VERIFICATION = 9;
	public static final int REBONDING = 10;
	public static final int INVALID_BUTTON = 11;
	public static final int UNKNOWN = 12;
}
