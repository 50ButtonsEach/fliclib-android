package io.flic.lib;

/**
 * Used in {@link FlicButton#setFlicButtonCallbackFlags(int)} to tell which events you want to listen for.
 * If you are concerned about low latencies and performance, only register the flags you actually use.
 * These can be bitwise-or'ed together.
 */
@SuppressWarnings("unused")
public interface FlicButtonCallbackFlags {
  int NONE = 0;

  int UP_OR_DOWN = 1;
  int CLICK_OR_HOLD = 2;
  int CLICK_OR_DOUBLE_CLICK = 4;
  int CLICK_OR_DOUBLE_CLICK_OR_HOLD = 8;

  int ALL = UP_OR_DOWN | CLICK_OR_HOLD | CLICK_OR_DOUBLE_CLICK | CLICK_OR_DOUBLE_CLICK_OR_HOLD;
}
