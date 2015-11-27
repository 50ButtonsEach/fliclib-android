package io.flic.lib;

import android.content.Context;

/**
 * FlicManagerInitializedCallback.
 */
public interface FlicManagerInitializedCallback {
	/**
	 * Called once when the manager has been initialized.
	 * This is called on the UI thread shortly after {@link FlicManager#getInstance(Context, FlicManagerInitializedCallback)}
	 * or {@link FlicManager#getInstance(Context, FlicManagerInitializedCallback, FlicManagerUninitializedCallback)} was called.
	 *
	 * @param manager The manager
	 */
	void onInitialized(FlicManager manager);
}
