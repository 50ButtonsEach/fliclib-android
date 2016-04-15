package io.flic.lib;

import android.app.Activity;
import android.os.RemoteException;

import java.io.InvalidObjectException;
import java.util.ArrayList;

/**
 * FlicButton.
 *
 * This class represents a Flic button.
 *
 * Instances of this class cannot be created directly but are returned from the manager.
 * See {@link FlicManager#initiateGrabButton(Activity)}, {@link FlicManager#getButtonByDeviceId(String)} and {@link FlicManager#getKnownButtons()}.
 */
public final class FlicButton {
	/**
	 * The button is disconnected and there is no pending connection to it.
	 */
	public static final int BUTTON_DISCONNECTED = 0;

	/**
	 * The button is currently not connected but there is a pending connection to it.
	 * When it is in range and is pressed, it will connect.
	 */
	public static final int BUTTON_CONNECTION_STARTED = 1;

	/**
	 * The button is currently connected and button events will arrive immediately when pressed.
	 */
	public static final int BUTTON_CONNECTION_COMPLETED = 2;

	boolean forgotten = false;
	FlicManager manager;
	String mac;
	final ArrayList<FlicButtonCallback> callbacks = new ArrayList<>();
	int callbackFlags = FlicButtonCallbackFlags.ALL;

	FlicButton(FlicManager manager, String mac) {
		this.manager = manager;
		this.mac = mac;
	}

	private void checkNotForgotten() {
		if (forgotten) {
			throw new RuntimeException("This button has been forgotten: " + mac);
		}
	}

	/**
	 * Add callbacks for this button.
	 * The callbacks remain valid during the lifetime of the application process or until you call {@link FlicManager#forgetButton(FlicButton)} for this button.
	 * Callbacks are not called during the manager is not in the initialized state.
	 * In order to receive callbacks, {@link #setFlicButtonCallbackFlags(int)} must be called first.
	 *
	 * @param callback The callback object
	 */
	public void addFlicButtonCallback(FlicButtonCallback callback) {
		checkNotForgotten();
		synchronized (callbacks) {
			callbacks.add(callback);
		}
	}

	/**
	 * Removes a callback for this button.
	 *
	 * @param callback The callback object
	 */
	public void removeFlicButtonCallback(FlicButtonCallback callback) {
		checkNotForgotten();
		synchronized (callbacks) {
			callbacks.remove(callback);
		}
	}

	/**
	 * Remove all callbacks for this button.
	 */
	public void removeAllFlicButtonCallbacks() {
		checkNotForgotten();
		synchronized (callbacks) {
			callbacks.clear();
		}
	}

	/**
	 * Set which button events to listen for.
	 *
	 * In order to get callbacks added by {@link #addFlicButtonCallback(FlicButtonCallback)},
	 * this method must be called first.
	 *
	 * If you are concerned about low latencies and performance, only register the flags you actually use.
	 *
	 * The settings are active as long as the process is running and must be set again when the app process restarts.
	 *
	 * @param flicButtonCallbackFlags A bitwise-or'ed value of {@link FlicButtonCallbackFlags}
	 * @return true on success, false if manager was in uninitialized state
	 */
	public boolean setFlicButtonCallbackFlags(int flicButtonCallbackFlags) {
		checkNotForgotten();
		synchronized (manager.mIntfLock) {
			if (manager.mIntf != null) {
				try {
					manager.mIntf.setButtonCallbacks(manager.mIntfId, mac, flicButtonCallbackFlags);
					callbackFlags = flicButtonCallbackFlags;
					return true;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * To receive button clicks that also works in the case the app is not currently running, you
	 * can set up a broadcast receiver extending {@link FlicBroadcastReceiver}.
	 * Declare it in your manifest and set android:exported to true and add an intent-filter
	 * with the name io.flic.FLICLIB_EVENT.
	 *
	 * To unregister all events, use {@link FlicBroadcastReceiverFlags#NONE}.
	 *
	 * The settings will remain even if the app process restarts.
	 *
	 * @param flicBroadcastReceiverFlags A bitwise-or'ed value of {@link FlicBroadcastReceiverFlags}
	 * @return true on success, false if manager was in uninitialized state
	 */
	public boolean registerListenForBroadcast(int flicBroadcastReceiverFlags) {
		checkNotForgotten();
		synchronized (manager.mIntfLock) {
			if (manager.mIntf != null) {
				try {
					manager.mIntf.registerListenForBroadcast(manager.mIntfId, mac, flicBroadcastReceiverFlags);
					return true;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}


	/**
	 * Grab a button exclusivity.
	 *
	 * This means that this app's callbacks registered by addFlicButtonCallback are the only
	 * ones receiving button events on this Android device. No actions will be triggered in
	 * neither the Flic app nor any other app.
	 * Please release this exclusivity with {@link #releaseExclusivity} when you're done.
	 *
	 * A possible use-case for this is if your app is a wake-up alarm, and you want to use the Flic button
	 * to turn off the alarm. Then light up the screen, bring your activity to the foreground and grab
	 * button exclusivity. Then when the user presses the button, it will only turn off the alarm
	 * and not trigger other functionality the user might have assigned the button. Another use-case
	 * is games where button presses should only control the game and do nothing else.
	 *
	 * @return true on success, false if manager was in uninitialized state
	 */
	public boolean grabExclusivity() {
		checkNotForgotten();
		synchronized (manager.mIntfLock) {
			if (manager.mIntf != null) {
				try {
					manager.mIntf.grabExclusivity(manager.mIntfId, mac);
					return true;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * Release a button exclusivity.
	 *
	 * Call this after {@link #grabExclusivity} when you're done with the button.
	 *
	 * @return true on success, false if manager was in uninitialized state
	 */
	public boolean releaseExclusivity() {
		checkNotForgotten();
		synchronized (manager.mIntfLock) {
			if (manager.mIntf != null) {
				try {
					manager.mIntf.releaseExclusivity(manager.mIntfId, mac);
					return true;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * Get the current callback flags.
	 *
	 * @return Callback flags
	 */
	public int getCallbackFlags() {
		return callbackFlags;
	}

	/**
	 * Get the Bluetooth device address of this button.
	 *
	 * Can be used to uniquely identify a button.
	 *
	 * @return The address
	 */
	public String getButtonId() {
		return mac;
	}

	/**
	 * Get the connection status.
	 *
	 * @return One of {@link FlicButton#BUTTON_DISCONNECTED}, {@link FlicButton#BUTTON_CONNECTION_STARTED} and {@link FlicButton#BUTTON_CONNECTION_COMPLETED}
	 */
	public int getConnectionStatus() {
		checkNotForgotten();
		synchronized (manager.mIntfLock) {
			if (manager.mIntf != null) {
				try {
					return manager.mIntf.getConnectionStatus(manager.mIntfId, mac);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		return BUTTON_DISCONNECTED;
	}

	/**
	 * Get the name of this button, as assigned in the Flic app.
	 * Available in Flic app version >= 2.0
	 *
	 * @return The name. If Flic app version is < 2.0 or the user has not entered a name, null is returned.
	 */
	public String getName() {
		checkNotForgotten();
		synchronized (manager.mIntfLock) {
			if (manager.mIntf != null) {
				try {
					return manager.mIntf.getName(manager.mIntfId, mac);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}


	/**
	 * Get the color of this button.
	 *
	 * @return The color as a string. Currently one of "black", "white", "turquoise", "yellow", "green" or "unknown".
	 */
	public String getColor() {
		checkNotForgotten();
		synchronized (manager.mIntfLock) {
			if (manager.mIntf != null) {
				try {
					return manager.mIntf.getColor(manager.mIntfId, mac);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * Read the current RSSI value of the button.
	 * {@link FlicButtonCallback#onReadRemoteRSSI(FlicButton, int, int)} will be called when a result is available.
	 *
	 * @return True if the read was successfully initiated
	 */
	public boolean readRemoteRSSI() {
		checkNotForgotten();
		synchronized (manager.mIntfLock) {
			if (manager.mIntf != null) {
				try {
					return manager.mIntf.readRemoteRSSI(manager.mIntfId, mac);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 * Set the button to active mode.
	 *
	 * This decreases the delay, but will consume more battery power.
	 * Good for games where it is important with low delays.
	 * Please unset active mode as soon as you don't need it anymore.
	 *
	 * @param activeMode True if to use active mode. False to use passive mode.
	 * @return True if successful. False if the manager was in uninitialized state.
	 */
	public boolean setActiveMode(boolean activeMode) {
		checkNotForgotten();
		synchronized (manager.mIntfLock) {
			if (manager.mIntf != null) {
				try {
					manager.mIntf.setActiveMode(manager.mIntfId, mac, activeMode);
					return true;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "FlicButton " + this.mac + (forgotten ? " (forgotten)" : "");
	}
}
