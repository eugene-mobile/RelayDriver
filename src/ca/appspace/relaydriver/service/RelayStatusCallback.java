package ca.appspace.relaydriver.service;

/**
 * Created by esukharev on 09/10/2014.
 */
public interface RelayStatusCallback {
    public void onRelayStatusChanged(int relayNum, boolean newValue);
}
