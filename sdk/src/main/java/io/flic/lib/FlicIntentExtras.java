package io.flic.lib;

/**
 * Intent extras of flic event intents.
 */
interface FlicIntentExtras {
  /**
   * Contains the button identifier (Bluetooth Address).
   */
  String BUTTON_ID = "buttonId";

  /**
   * Contains the type of this event.
   * See {@link FlicIntentTypes} for possible values.
   */
  String TYPE = "type";

  /**
   * Contains the value of this event, unless {@link FlicIntentExtras#TYPE} is {@link FlicIntentTypes#REMOVED}.
   * See {@link FlicIntentValues} for possible values.
   */
  String VALUE = "value";

  /**
   * Contains a boolean whether the event was queued or not on the Flic button.
   * The event will be queued on the button if it is not connected when it is pressed.
   * This will not be present if {@link FlicIntentExtras#TYPE} is {@link FlicIntentTypes#REMOVED}.
   */
  String WAS_QUEUED = "wasQueued";

  /**
   * If the event was queued (see {@link FlicIntentExtras#WAS_QUEUED}, contains the number of seconds since the event happened.
   * This will not be present if {@link FlicIntentExtras#TYPE} is {@link FlicIntentTypes#REMOVED}.
   */
  String TIME_DIFF = "timeDiff";
}
