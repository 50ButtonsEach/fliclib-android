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
	public static final int BUTTON_DISCONNECTED = 0;
	public static final int BUTTON_CONNECTION_STARTED = 1;
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
	 * If you are concerned about low latencies and performance, only register the flags you actually use.
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
	 * To receive button clicks in the case the app is not currently running, you can set up a
	 * broadcast receiver with intent filter io.flic.FLICLIB_EVENT.
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
	 * This means that this app is the only one receiving button events.
	 * Please release this exclusivity with {@link #releaseExclusivity} when you're done.
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
	 * Get the MAC address of this button.
	 *
	 * @return The MAC address
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
