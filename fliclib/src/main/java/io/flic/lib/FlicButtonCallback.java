package io.flic.lib;

/**
 * FlicButtonCallback
 *
 * Callbacks for button updates. You should extend this class and implement the callbacks you want.
 */
public class FlicButtonCallback {
	/**
	 * Called when the Bluetooth connection has just been started.
	 * It's not ready for use yet however - {@link FlicButtonCallback#onConnectionCompleted(FlicButton)} will be called when ready.
	 *
	 * @param button The button
	 */
	public void onConnectionStarted(FlicButton button) {}

	/**
	 * Called if there was a problem establishing a Bluetooth connection to the button. Happens very rarely.
	 *
	 * @param button The button
	 * @param status A Bluetooth GATT status.
	 */
	public void onConnectionFailed(FlicButton button, int status) {}

	/**
	 * Called when the Bluetooth connection was disconnected, for example if the button becomes out of range or the user manually disconnecting this button in the Flic Application.
	 *
	 * @param button The button
	 */
	public void onDisconnect(FlicButton button) {}

	/**
	 * Called when the connection to the button has been established and is ready to use.
	 *
	 * @param button The button
	 */
	public void onConnectionCompleted(FlicButton button) {}

	/**
	 * Called as a result of {@link FlicButton#readRemoteRSSI()}.
	 *
	 * @param button The button
	 * @param rssi RSSI value for the remote device
	 * @param status 0 if the RSSI was read successfully
	 */
	public void onReadRemoteRSSI(FlicButton button, int rssi, int status) {}

	/**
	 * Called when the button was pressed or released.
	 *
	 * @param button The button
	 * @param wasQueued If the event was locally queued in the button because it was disconnected. After the connection is completed, the event will be sent with this parameter set to true.
	 * @param timeDiff If the event was queued, the timeDiff will be the number of seconds since the event happened.
	 * @param isUp True if up, else false
	 * @param isDown True if down, else false
	 */
	public void onButtonUpOrDown(FlicButton button, boolean wasQueued, int timeDiff, boolean isUp, boolean isDown) {}

	/**
	 * Used for the scenario where you want to listen on button click and hold.
	 *
	 * @param button The button
	 * @param wasQueued If the event was locally queued in the button because it was disconnected. After the connection is completed, the event will be sent with this parameter set to true.
	 * @param timeDiff If the event was queued, the timeDiff will be the number of seconds since the event happened.
	 * @param isClick True if click, else false
	 * @param isHold True if hold, else false
	 */
	public void onButtonClickOrHold(FlicButton button, boolean wasQueued, int timeDiff, boolean isClick, boolean isHold) {}

	/**
	 * Used for the scenario where you want to listen on single click and double click.
	 * Single clicks might be delayed for up to 0.5 seconds because we can't be sure if it was rather a double click or not until then.
	 *
	 * @param button The button
	 * @param wasQueued If the event was locally queued in the button because it was disconnected. After the connection is completed, the event will be sent with this parameter set to true.
	 * @param timeDiff If the event was queued, the timeDiff will be the number of seconds since the event happened.
	 * @param isSingleClick True if single click, else false
	 * @param isDoubleClick True if double click, else false
	 */
	public void onButtonSingleOrDoubleClick(FlicButton button, boolean wasQueued, int timeDiff, boolean isSingleClick, boolean isDoubleClick) {}

	/**
	 * Used for the scenario where you want to listen on single click, double click and hold.
	 * Single clicks might be delayed for up to 0.5 seconds because we can't be sure if it was rather a double click or not until then.
	 *
	 * @param button The button
	 * @param wasQueued If the event was locally queued in the button because it was disconnected. After the connection is completed, the event will be sent with this parameter set to true.
	 * @param timeDiff If the event was queued, the timeDiff will be the number of seconds since the event happened.
	 * @param isSingleClick True if single click, else false
	 * @param isDoubleClick True if double click, else false
	 * @param isHold True if hold, else false
	 */
	public void onButtonSingleOrDoubleClickOrHold(FlicButton button, boolean wasQueued, int timeDiff, boolean isSingleClick, boolean isDoubleClick, boolean isHold) {}

	/**
	 * Called when the button was removed in the Flic App, or when the user disconnected this app from the button in the Flic App (if so the button can be grabbed again as usual).
	 * This object cannot be used any more once this method has returned.
	 *
	 * @param button The button
	 */
	public void onButtonRemoved(FlicButton button) {}
}
