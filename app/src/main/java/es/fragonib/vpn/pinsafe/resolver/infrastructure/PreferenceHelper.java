package es.fragonib.vpn.pinsafe.resolver.infrastructure;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import es.fragonib.vpn.pinsafe.resolver.domain.PinSafePreference;


/**
 * Helper repository for app preferences
 */
public class PreferenceHelper {

    private static String PREFERENCE_PINSAFE_PREFIX = "preference.pinsafe.";

    private static SharedPreferences preferences;

    public static void init(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     *
     * @param preference Preference to save (non <code>null</code>)
     */
    public static void save(PinSafePreference preference) {
        preferences.edit()
                .putString(PREFERENCE_PINSAFE_PREFIX + "sender", preference.getSender())
                .putString(PREFERENCE_PINSAFE_PREFIX + "code", preference.getCode())
                .apply();
    }

    /**
     *
     * @return <code>null</code> if preference is not present
     */
    public static PinSafePreference retrieve() {
        String sender = preferences.getString(PREFERENCE_PINSAFE_PREFIX + "sender", null);
        String code = preferences.getString(PREFERENCE_PINSAFE_PREFIX + "code", null);
        if (sender == null || code == null)
            return null;
        return PinSafePreference.of(sender, code);
    }
 
}