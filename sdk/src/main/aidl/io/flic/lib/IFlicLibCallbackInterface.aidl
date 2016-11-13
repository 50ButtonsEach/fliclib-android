// Internal class used for communication with the Flic Application

package io.flic.lib;

interface IFlicLibCallbackInterface {
    void onConnect(String mac);
    void onReady(String mac);
    void onDisconnect(String mac, int flicError);
    void onConnectionFailed(String mac, int status);

    void onReadRemoteRSSI(String mac, int rssi, int status);
    void onButtonUpOrDown(String mac, boolean wasQueued, int timeDiff, int action);
    void onButtonClickOrHold(String mac, boolean wasQueued, int timeDiff, int action);
    void onButtonSingleOrDoubleClick(String mac, boolean wasQueued, int timeDiff, int action);
    void onButtonSingleOrDoubleClickOrHold(String mac, boolean wasQueued, int timeDiff, int action);

    void onButtonRemoved(String mac);
}
