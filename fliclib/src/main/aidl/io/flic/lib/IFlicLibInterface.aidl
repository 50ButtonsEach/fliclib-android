// Internal class used for communication with the Flic Application

package io.flic.lib;

import io.flic.lib.IFlicLibCallbackInterface;

interface IFlicLibInterface {
    long registerCallback(IFlicLibCallbackInterface cb);
    void unregisterCallback(long id);

    void listenForConnectionCallbacks(long id, String mac);
    void unListenForConnectionCallbacks(long id, String mac);
    void setButtonCallbacks(long id, String mac, int flags);
    int getConnectionStatus(long id, String mac);
    String getColor(long id, String mac);
    boolean readRemoteRSSI(long id, String mac);
    void setActiveMode(long id, String mac, boolean activeMode);
}
