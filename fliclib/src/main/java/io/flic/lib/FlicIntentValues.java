package io.flic.lib;

/**
 * Represents possible values of {@link FlicIntentExtras#VALUE}.
 */
interface FlicIntentValues {
  /**
   * Indicates that the button was released.
   * Possible value for {@link FlicIntentExtras#VALUE} when {@link FlicIntentExtras#TYPE} is {@link FlicIntentTypes#UP_OR_DOWN}.
   */
  String UP = "UP";

  /**
   * Indicates that the button was pressed.
   * Possible value for {@link FlicIntentExtras#VALUE} when {@link FlicIntentExtras#TYPE} is {@link FlicIntentTypes#UP_OR_DOWN}.
   */
  String DOWN = "DOWN";

  /**
   * Indicates that the button was clicked.
   * Possible value for {@link FlicIntentExtras#VALUE} when {@link FlicIntentExtras#TYPE} is {@link FlicIntentTypes#CLICK_OR_HOLD}.
   */
  String CLICK = "CLICK";

  /**
   * Indicates that the button was hold for half a second.
   * Possible value for {@link FlicIntentExtras#VALUE} when {@link FlicIntentExtras#TYPE} is {@link FlicIntentTypes#CLICK_OR_HOLD} or {@link FlicIntentTypes#SINGLE_OR_DOUBLE_CLICK_OR_HOLD}.
   */
  String HOLD = "HOLD";

  /**
   * Indicates that the button was single clicked.
   * Possible value for {@link FlicIntentExtras#VALUE} when {@link FlicIntentExtras#TYPE} is {@link FlicIntentTypes#SINGLE_OR_DOUBLE_CLICK} or {@link FlicIntentTypes#SINGLE_OR_DOUBLE_CLICK_OR_HOLD}.
   */
  String SINGLE_CLICK = "SINGLE_CLICK";

  /**
   * Indicates that the button was double clicked.
   * Possible value for {@link FlicIntentExtras#VALUE} when {@link FlicIntentExtras#TYPE} is {@link FlicIntentTypes#SINGLE_OR_DOUBLE_CLICK} or {@link FlicIntentTypes#SINGLE_OR_DOUBLE_CLICK_OR_HOLD}.
   */
  String DOUBLE_CLICK = "DOUBLE_CLICK";
}
