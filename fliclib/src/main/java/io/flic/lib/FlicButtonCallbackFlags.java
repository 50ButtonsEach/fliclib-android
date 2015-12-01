package io.flic.lib;

/**
 * Used in {@link FlicButton#setFlicButtonCallbackFlags(int)} to tell which events you want to listen for.
 * If you are concerned about low latencies and performance, only register the flags you actually use.
 * These can be bitwise-or'ed together.
 */
public class FlicButtonCallbackFlags {
	public static final int NONE = 0;
	public static final int UP_OR_DOWN = 1;
	public static final int CLICK_OR_HOLD = 2;
	public static final int CLICK_OR_DOUBLE_CLICK = 4;
	public static final int CLICK_OR_DOUBLE_CLICK_OR_HOLD = 8;
	public static final int ALL = 15;
}
