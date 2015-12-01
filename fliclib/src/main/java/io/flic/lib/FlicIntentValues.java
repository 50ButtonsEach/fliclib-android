package io.flic.lib;

/**
 * Represents possible values of {@link FlicIntentExtras#VALUE}.
 */
class FlicIntentValues {
	/**
	 * Indicates that the button was released.
	 * Possible value for {@link FlicIntentExtras#VALUE} when {@link FlicIntentExtras#TYPE} is {@link FlicIntentTypes#UP_OR_DOWN}.
	 */
	public static final String UP = "UP";

	/**
	 * Indicates that the button was pressed.
	 * Possible value for {@link FlicIntentExtras#VALUE} when {@link FlicIntentExtras#TYPE} is {@link FlicIntentTypes#UP_OR_DOWN}.
	 */
	public static final String DOWN = "DOWN";

	/**
	 * Indicates that the button was clicked.
	 * Possible value for {@link FlicIntentExtras#VALUE} when {@link FlicIntentExtras#TYPE} is {@link FlicIntentTypes#CLICK_OR_HOLD}.
	 */
	public static final String CLICK = "CLICK";

	/**
	 * Indicates that the button was hold for half a second.
	 * Possible value for {@link FlicIntentExtras#VALUE} when {@link FlicIntentExtras#TYPE} is {@link FlicIntentTypes#CLICK_OR_HOLD} or {@link FlicIntentTypes#SINGLE_OR_DOUBLE_CLICK_OR_HOLD}.
	 */
	public static final String HOLD = "HOLD";

	/**
	 * Indicates that the button was single clicked.
	 * Possible value for {@link FlicIntentExtras#VALUE} when {@link FlicIntentExtras#TYPE} is {@link FlicIntentTypes#SINGLE_OR_DOUBLE_CLICK} or {@link FlicIntentTypes#SINGLE_OR_DOUBLE_CLICK_OR_HOLD}.
	 */
	public static final String SINGLE_CLICK = "SINGLE_CLICK";

	/**
	 * Indicates that the button was double clicked.
	 * Possible value for {@link FlicIntentExtras#VALUE} when {@link FlicIntentExtras#TYPE} is {@link FlicIntentTypes#SINGLE_OR_DOUBLE_CLICK} or {@link FlicIntentTypes#SINGLE_OR_DOUBLE_CLICK_OR_HOLD}.
	 */
	public static final String DOUBLE_CLICK = "DOUBLE_CLICK";
}
