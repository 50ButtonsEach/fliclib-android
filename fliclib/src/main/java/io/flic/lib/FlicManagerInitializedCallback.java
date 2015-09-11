package io.flic.lib;

import android.content.Context;

/**
 * FlicManagerInitializedCallback.
 */
public class FlicManagerInitializedCallback {
	/**
	 * Called once when the manager has been initialized.
	 * This is called on the UI thread shortly after {@link FlicManager#getInstance(Context, FlicManagerInitializedCallback)}
	 * or {@link FlicManager#getInstance(Context, FlicManagerInitializedCallback, FlicManagerUninitializedCallback)} was called.
	 *
	 * @param manager The manager
	 */
	public void onInitialized(FlicManager manager) {

	}
}
