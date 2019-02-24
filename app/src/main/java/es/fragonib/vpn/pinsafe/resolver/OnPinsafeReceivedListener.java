package es.fragonib.vpn.pinsafe.resolver;


/**
 * Listener interested on pinsafe messages
 */
public interface OnPinsafeReceivedListener {

    void onPinsafeReceived(String decodedOtc);

}
