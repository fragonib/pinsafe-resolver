package es.fragonib.vpn.pinsafe.resolver;

import es.fragonib.vpn.pinsafe.resolver.domain.PinSafePreference;


/**
 * Listener interested on preferences change
 */
public interface OnPreferenceChangeListener {

    void onPreferenceChange(PinSafePreference pinSafePreference);

}
