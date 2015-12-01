package io.flic.lib;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Flic Manager.
 *
 * This is a singleton class used to communicate with the Flic Application.
 * Use {@link FlicManager#getInstance(Context, FlicManagerInitializedCallback, FlicManagerUninitializedCallback)} or {@link FlicManager#getInstance(Context, FlicManagerInitializedCallback)} to retrieve a manager.
 */
public final class FlicManager {
	private static final String TAG = "FlicManager";
	/**
	 * Request code used in the grab button flow.
	 */
	public static final int GRAB_BUTTON_REQUEST_CODE = 0xce8b;

	private static FlicManager instance = new FlicManager();
	private Context mContext;
	private DB mDb;
	private ServiceConnection mServiceConnection;
	private FlicManagerCallback mFlicManagerCallback;
	private List<FlicManagerInitializedCallback> mInitializedCallbacks = new ArrayList<>();
	private List<FlicManagerUninitializedCallback> mUninitializedCallbacks = new ArrayList<>();
	private final HashMap<String, FlicButton> mKnownButtons = new HashMap<>();
	private SecureRandom mSecRand = new SecureRandom();
	private byte[] mLastPrivateCurve25519Key;

	private String mAppId;
	private String mAppSecret;
	private String mAppName;

	final Object mIntfLock = new Object();
	boolean isInitializing;
	IFlicLibInterface mIntf;
	long mIntfId;

	private static class FlicManagerCallback {
		public void onInitialized() {

		}
		public void onUninitialized() {

		}
	}

	private FlicManager() {}

	/**
	 * Set the App credentials.
	 *
	 * The credentials can only be set once.
	 * Setting them again is a no-op.
	 *
	 * @param appId App ID
	 * @param appSecret App Secret
	 * @param appName App name that will be shown to the user in the Flic App
	 */
	public static void setAppCredentials(String appId, String appSecret, String appName) {
		if (instance.mAppId == null) {
			instance.mAppId = appId;
		}
		if (instance.mAppSecret == null) {
			instance.mAppSecret = appSecret;
		}
		if (instance.mAppName == null) {
			instance.mAppName = appName;
		}
	}

	static boolean hasSetAppCredentials() {
		return instance.mAppId != null && instance.mAppSecret != null && instance.mAppName != null;
	}

	/**
	 * Get the singleton instance of the manager.
	 *
	 * @param context An application/service/activity context. {@link Context#getApplicationContext()} on this object will be the context to use.
	 * @param initializedCallback A callback that will be called with the manager as parameter.
	 *
	 * @throws IllegalArgumentException If context or initializedCallback is null.
	 * @throws AppCredentialsNotProvidedException If App credentials were not provided. See {@link #setAppCredentials(String, String, String)}.
	 * @throws FlicAppNotInstalledException If the Flic Application is not installed.
	 */
	public static void getInstance(Context context, FlicManagerInitializedCallback initializedCallback) {
		getInstance(context, initializedCallback, null);
	}

	/**
	 * Get the singleton instance of the manager.
	 *
	 * @param context An application/service/activity context. {@link Context#getApplicationContext()} on this object will be the context to use.
	 * @param initializedCallback A callback that will be called with the manager as parameter.
	 * @param uninitializedCallback If non-null, a callback that will be called if the Flic Application exits, or when {@link FlicManager#destroyInstance()} is called.
	 *
	 * @throws IllegalArgumentException If context or initializedCallback is null.
	 * @throws AppCredentialsNotProvidedException If App credentials were not provided. See {@link #setAppCredentials(String, String, String)}.
	 * @throws FlicAppNotInstalledException If the Flic Application is not installed.
	 */
	public static void getInstance(Context context, FlicManagerInitializedCallback initializedCallback, FlicManagerUninitializedCallback uninitializedCallback) {
		if (context == null) {
			throw new IllegalArgumentException("context is null");
		}
		if (initializedCallback == null) {
			throw new IllegalArgumentException("initializedCallback is null");
		}
		instance.getInstanceInternal(context, initializedCallback, uninitializedCallback);
	}
	private void getInstanceInternal(Context context, FlicManagerInitializedCallback initializedCallback, FlicManagerUninitializedCallback uninitializedCallback) {
		if (!hasSetAppCredentials()) {
			throw new AppCredentialsNotProvidedException("App credentials were not provided");
		}

		synchronized (mIntfLock) {
			if (uninitializedCallback != null) {
				mUninitializedCallbacks.add(uninitializedCallback);
			}
			if (mIntf == null || isInitializing) {
				mInitializedCallbacks.add(initializedCallback);
			}
			if (mIntf == null && !isInitializing) {
				isInitializing = true;
				init(context, new FlicManagerCallback() {
					@Override
					public void onInitialized() {
						synchronized (mIntfLock) {
							isInitializing = false;
							for (FlicManagerInitializedCallback cb : mInitializedCallbacks) {
								cb.onInitialized(FlicManager.this);
							}
							mInitializedCallbacks.clear();
						}
					}

					@Override
					public void onUninitialized() {
						synchronized (mIntfLock) {
							for (FlicManagerUninitializedCallback cb : mUninitializedCallbacks) {
								cb.onUninitialized(FlicManager.this);
							}
							mUninitializedCallbacks.clear();
						}
					}
				});
			} else if (mIntf != null && !isInitializing) {
				initializedCallback.onInitialized(this);
			}
		}
	}

	FlicButton synchronizedGetButton(String mac) {
		synchronized (mKnownButtons) {
			return mKnownButtons.get(mac);
		}
	}

	IFlicLibCallbackInterface mCallbackIntf = new IFlicLibCallbackInterface.Stub() {
		@Override
		public void onConnect(String mac) {
			mac = mac.toLowerCase();
			FlicButton button = synchronizedGetButton(mac);
			if (button != null) {
				synchronized (button.callbacks) {
					for (FlicButtonCallback cb : (ArrayList<FlicButtonCallback>)button.callbacks.clone()) {
						cb.onConnectionStarted(button);
					}
				}
			}
		}

		@Override
		public void onReady(String mac) {
			mac = mac.toLowerCase();
			FlicButton button = synchronizedGetButton(mac);
			if (button != null) {
				synchronized (button.callbacks) {
					for (FlicButtonCallback cb : (ArrayList<FlicButtonCallback>)button.callbacks.clone()) {
						cb.onConnectionCompleted(button);
					}
				}
			}
		}

		@Override
		public void onDisconnect(String mac, int flicError) {
			mac = mac.toLowerCase();
			FlicButton button = synchronizedGetButton(mac);
			if (button != null) {
				synchronized (button.callbacks) {
					for (FlicButtonCallback cb : (ArrayList<FlicButtonCallback>)button.callbacks.clone()) {
						cb.onDisconnect(button);
					}
				}
			}
		}

		@Override
		public void onConnectionFailed(String mac, int status) {
			mac = mac.toLowerCase();
			FlicButton button = synchronizedGetButton(mac);
			if (button != null) {
				synchronized (button.callbacks) {
					for (FlicButtonCallback cb : (ArrayList<FlicButtonCallback>)button.callbacks.clone()) {
						cb.onConnectionFailed(button, status);
					}
				}
			}
		}

		@Override
		public void onReadRemoteRSSI(String mac, int rssi, int status) {
			mac = mac.toLowerCase();
			FlicButton button = synchronizedGetButton(mac);
			if (button != null) {
				synchronized (button.callbacks) {
					for (FlicButtonCallback cb : (ArrayList<FlicButtonCallback>)button.callbacks.clone()) {
						cb.onReadRemoteRSSI(button, rssi, status);
					}
				}
			}
		}

		@Override
		public void onButtonUpOrDown(String mac, boolean wasQueued, int timeDiff, int action) {
			Log.d(TAG, "onButtonUpOrDown");
			mac = mac.toLowerCase();
			FlicButton button = synchronizedGetButton(mac);
			if (button != null) {
				synchronized (button.callbacks) {
					for (FlicButtonCallback cb : (ArrayList<FlicButtonCallback>)button.callbacks.clone()) {
						cb.onButtonUpOrDown(button, wasQueued, timeDiff, action == 0, action == 1);
					}
				}
			}
		}

		@Override
		public void onButtonClickOrHold(String mac, boolean wasQueued, int timeDiff, int action) {
			mac = mac.toLowerCase();
			FlicButton button = synchronizedGetButton(mac);
			if (button != null) {
				synchronized (button.callbacks) {
					for (FlicButtonCallback cb : (ArrayList<FlicButtonCallback>)button.callbacks.clone()) {
						cb.onButtonClickOrHold(button, wasQueued, timeDiff, action == 0, action == 1);
					}
				}
			}
		}

		@Override
		public void onButtonSingleOrDoubleClick(String mac, boolean wasQueued, int timeDiff, int action) {
			mac = mac.toLowerCase();
			FlicButton button = synchronizedGetButton(mac);
			if (button != null) {
				synchronized (button.callbacks) {
					for (FlicButtonCallback cb : (ArrayList<FlicButtonCallback>)button.callbacks.clone()) {
						cb.onButtonSingleOrDoubleClick(button, wasQueued, timeDiff, action == 0, action == 1);
					}
				}
			}
		}

		@Override
		public void onButtonSingleOrDoubleClickOrHold(String mac, boolean wasQueued, int timeDiff, int action) {
			mac = mac.toLowerCase();
			FlicButton button = synchronizedGetButton(mac);
			if (button != null) {
				synchronized (button.callbacks) {
					for (FlicButtonCallback cb : (ArrayList<FlicButtonCallback>)button.callbacks.clone()) {
						cb.onButtonSingleOrDoubleClickOrHold(button, wasQueued, timeDiff, action == 0, action == 1, action == 2);
					}
				}
			}
		}

		@Override
		public void onButtonRemoved(String mac) {
			mac = mac.toLowerCase();
			FlicButton button = synchronizedGetButton(mac);
			if (button != null) {
				synchronized (button.callbacks) {
					for (FlicButtonCallback cb : (ArrayList<FlicButtonCallback>)button.callbacks.clone()) {
						cb.onButtonRemoved(button);
					}
				}
				forgetButton(button);
			}
		}
	};

	/**
	 * Is initialized.
	 *
	 * @return true if the manager has been initialized.
	 */
	public boolean isInitialized() {
		return mIntf != null;
	}

	private void init(Context context, FlicManagerCallback flicManagerCallback) {
		context = context.getApplicationContext();
		mContext = context;
		mFlicManagerCallback = flicManagerCallback;

		mDb = new DB(context.getApplicationContext());
		Intent intent = new Intent();
		intent.setClassName("io.flic.app", "io.flic.app.FlicService");
		mServiceConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				synchronized (mIntfLock) {
					IFlicLibInterface intf = IFlicLibInterface.Stub.asInterface(service);
					try {
						mIntfId = intf.registerCallback(mCallbackIntf, mAppId, mAppSecret, mAppName);
						mIntf = intf;
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				synchronized (mKnownButtons) {
					try {
						List<String> verifiedButtons = mDb.getButtons();
						List<String> addresses = mIntf.listButtons(mIntfId);
						for (String address : addresses) {
							if (verifiedButtons.contains(address)) {
								mKnownButtons.put(address, new FlicButton(FlicManager.this, address));
							}
						}
						for (FlicButton button : mKnownButtons.values()) {
							mIntf.listenForConnectionCallbacks(mIntfId, button.mac);
							mIntf.setButtonCallbacks(mIntfId, button.mac, button.callbackFlags);
						}
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				if (mFlicManagerCallback != null) {
					mFlicManagerCallback.onInitialized();
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				synchronized (mIntfLock) {
					mIntf = null;
				}
				if (mFlicManagerCallback != null) {
					mFlicManagerCallback.onUninitialized();
				}
			}
		};
		if (!context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)) {
			isInitializing = false;
			mInitializedCallbacks.clear();
			mUninitializedCallbacks.clear();
			throw new FlicAppNotInstalledException("Flic Application is not installed");
		}
	}

	/**
	 * Clean resources and unregister all callbacks. Use {@link FlicManager#getInstance} to re-initialize.
	 */
	public static void destroyInstance() {
		instance.destroyInstanceInternal();
	}
	private void destroyInstanceInternal() {
		synchronized (mIntfLock) {
			if (mIntf != null) {
				try {
					mIntf.unregisterCallback(mIntfId);
					mIntf = null;
					mIntfId = 0;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		mContext.unbindService(mServiceConnection);
	}

	protected void finalize() {
		destroyInstanceInternal();
	}

	/**
	 * Get a button by its Bluetooth device address.
	 *
	 * @param deviceId The Bluetooth device address case insensitive.
	 * @return A button object or null if it has not been grabbed before.
	 */
	public FlicButton getButtonByDeviceId(String deviceId) {
		synchronized (mKnownButtons) {
			FlicButton button = mKnownButtons.get(deviceId.toLowerCase());
			return button;
		}
	}

	/**
	 * Get a copy of the internal list of buttons.
	 *
	 * This is a list of all previously grabbed buttons that have not been removed.
	 *
	 * @return The list
	 */
	public List<FlicButton> getKnownButtons() {
		synchronized (mKnownButtons) {
			return new ArrayList<>(mKnownButtons.values());
		}
	}

	/**
	 * Initiate a grab button sequence.
	 *
	 * Call this method to ask the Flic app to retrieve a button object.
	 * This calls startActivityForResult with the request code {@link FlicManager#GRAB_BUTTON_REQUEST_CODE}.
	 * The result retrieved in onActivityResult for currentActivity must be sent to the completeGrabButton method.
	 *
	 * @param currentActivity The current activity initiating the button grabbing.
	 */
	public void initiateGrabButton(Activity currentActivity) {
		Intent intent = new Intent("io.flic.app.GrabButton");
		intent.setPackage("io.flic.app");
		byte[] secretKey = new byte[32];
		byte[] publicKey = new byte[32];
		mSecRand.nextBytes(secretKey);
		Curve25519.keygen(publicKey, secretKey);
		mLastPrivateCurve25519Key = secretKey;
		intent.putExtra("token", publicKey);
		intent.putExtra("intfId", mIntfId);
		intent.putExtra("appId", mAppId);
		intent.putExtra("appSecret", mAppSecret);
		currentActivity.startActivityForResult(intent, GRAB_BUTTON_REQUEST_CODE);
	}

	/**
	 * Completes a grab button sequence.
	 * Call this from {@link Activity#onActivityResult(int, int, Intent)}.
	 *
	 * @param data An intent received by onActivityResult
	 * @return The grabbed FlicButton, null on failure, if requestCode wasn't {@link FlicManager#GRAB_BUTTON_REQUEST_CODE} or if resultCode was not {@link Activity#RESULT_OK}.
	 */
	public FlicButton completeGrabButton(int requestCode, int resultCode, Intent data) {
		if (requestCode != GRAB_BUTTON_REQUEST_CODE || resultCode != Activity.RESULT_OK) {
			return null;
		}
		if (data == null) {
			Log.e(TAG, "completeGrabButton: intent data is null");
			return null;
		}

		String mac = data.getStringExtra("mac").toLowerCase();
		String buttonUuid = data.getStringExtra("button_uuid");
		byte[] proof = data.getByteArrayExtra("proof");

		if (proof == null) {
			Log.e(TAG, "completeGrabButton: Invalid proof");
			return null;
		}

		byte[] buttonPublicCurve25519Key = data.getByteArrayExtra("pubkey");
		if (buttonPublicCurve25519Key == null || buttonPublicCurve25519Key.length != 32) {
			Log.e(TAG, "completeGrabButton: Invalid pubkey " + (buttonPublicCurve25519Key == null ? "null" : "length " + buttonPublicCurve25519Key.length));
			return null;
		}

		byte[] signature = data.getByteArrayExtra("pubkey_signature");
		if (signature == null) {
			Log.e(TAG, "completeGrabButton: null signature");
			return null;
		}
		boolean signatureOk = false;
		try {
			KeyFactory keyPair = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyPair.generatePublic(new RSAPublicKeySpec(new BigInteger("" +
					"26646109746820229206157738327971569752090211692348400628335016561872593810714980851565627258969379628284446" +
					"95485621727101111383914892371847123298307135674746058587731715965000418491422033377759596510596132771659601" +
					"93128877599848411256939953432876711220309718095564170603905551586617637901664463975584697344343488170915406" +
					"71614521772978783834371305059063805184552675999014093903953357902272537363249004733827686143866515001845855" +
					"26969464300271560446891592452975408396387087701542295019503281458064645269615535802577242268132904180219203" +
					"8883202203423153080919807757020166001248942233209849142898672366749341514289888739"), new BigInteger("65537")));

			Signature rsaSigner = Signature.getInstance("SHA256withRSA");
			rsaSigner.initVerify(publicKey);
			rsaSigner.update(buttonPublicCurve25519Key);
			rsaSigner.update(mac.toLowerCase().getBytes());
			rsaSigner.update(Utils.hexToBytes(buttonUuid));
			signatureOk = rsaSigner.verify(signature);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}

		if (!signatureOk) {
			Log.e(TAG, "Couldn't validate signature");
			return null;
		}

		byte[] sharedSecret = new byte[32];
		Curve25519.curve(sharedSecret, mLastPrivateCurve25519Key, buttonPublicCurve25519Key);
		mLastPrivateCurve25519Key = null;

		MessageDigest md = Utils.createSHA256();
		md.update(sharedSecret);
		byte[] proofExpected = Arrays.copyOfRange(md.digest(), 1, 14);
		if (!Arrays.equals(proof, proofExpected)) {
			Log.e(TAG, "completeGrabButton: proofs are not equal");
			return null;
		}

		synchronized (mKnownButtons) {
			FlicButton flicButton = mKnownButtons.get(mac);
			if (flicButton == null) {
				flicButton = new FlicButton(this, mac);
				mKnownButtons.put(mac, flicButton);
				mDb.addButton(mac);
			}

			synchronized (mIntfLock) {
				if (mIntf != null) {
					try {
						mIntf.listenForConnectionCallbacks(mIntfId, mac);
						mIntf.setButtonCallbacks(mIntfId, mac, flicButton.callbackFlags);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
			return flicButton;
		}

	}

	/**
	 * Forget button.
	 * Disposes a button object and removes it from the internal list of known buttons.
	 * You will no longer get callbacks for this button.
	 *
	 * @param button A button
	 */
	public void forgetButton(FlicButton button) {
		synchronized (mKnownButtons) {
			if (mKnownButtons.containsKey(button.mac)) {
				mKnownButtons.remove(button.mac);
				button.setFlicButtonCallbackFlags(0);
				button.removeAllFlicButtonCallbacks();
				mDb.removeButton(button.mac);
				button.forgotten = true;
				synchronized (mIntfLock) {
					if (mIntf != null) {
						try {
							mIntf.removeButton(mIntfId, button.mac);
							mIntf.unListenForConnectionCallbacks(mIntfId, button.mac);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	boolean validateIntent(Intent intent) {
		synchronized (mIntfLock) {
			try {
				return mIntf.validateIntent(mIntfId, intent.getExtras());
			} catch(RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
