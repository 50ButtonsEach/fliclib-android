package io.flic.lib;

/**
 * Represents possible values for the intent extra {@link FlicIntentExtras#TYPE}.
 */
class FlicIntentTypes {
	/**
	 * Indicates that the button was pressed or released.
	 * See {@link FlicIntentValues#UP} and {@link FlicIntentValues#DOWN}.
	 */
	public static final String UP_OR_DOWN = "UP_OR_DOWN";

	/**
	 * Indicates that the button was clicked or held.
	 * See {@link FlicIntentValues#CLICK} and {@link FlicIntentValues#HOLD}.
	 */
	public static final String CLICK_OR_HOLD = "CLICK_OR_HOLD";

	/**
	 * Indicates that the button was single- or double clicked.
	 * See {@link FlicIntentValues#SINGLE_CLICK} and {@link FlicIntentValues#DOUBLE_CLICK}.
	 */
	public static final String SINGLE_OR_DOUBLE_CLICK = "SINGLE_OR_DOUBLE_CLICK";

	/**
	 * Indicates that the button was single- or double clicked or held.
	 * See {@link FlicIntentValues#SINGLE_CLICK}, {@link FlicIntentValues#DOUBLE_CLICK} and {@link FlicIntentValues#HOLD}.
	 */
	public static final String SINGLE_OR_DOUBLE_CLICK_OR_HOLD = "SINGLE_OR_DOUBLE_CLICK_OR_HOLD";

	/**
	 * Indicates that this app for this button was released by the user in the Flic app.
	 *
	 * This broadcast receiver will no longer receive intents for this button, until {@link FlicButton#registerListenForBroadcast(int)} is called again.
	 */
	public static final String REMOVED = "REMOVED";
}
