package es.fragonib.vpn.pinsafe.resolver.infrastructure;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import es.fragonib.vpn.pinsafe.resolver.OnPreferenceChangeListener;
import es.fragonib.vpn.pinsafe.resolver.domain.PinSafePreference;


/**
 * Helper repository for app preferences
 */
public class PreferenceHelper {

    private static String PREFERENCE_PINSAFE_PREFIX = "preference.pinsafe.";

    private static SharedPreferences preferences;

    /**
     * Initialize preference manager for given context. Should be invoked before any other method.
     *
     * @param context Context which preferences should be managed.
     */
    public static void init(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Should be invoked always after {@link PreferenceHelper#init(Context)}
     *
     * @param listener Listener interested on preferences change
     */
    public static void register(OnPreferenceChangeListener listener) {
        preferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.startsWith(PREFERENCE_PINSAFE_PREFIX))
                listener.onPreferenceChange(retrieve());
        });
    }

    /**
     * Should be invoked after always after {@link PreferenceHelper#init(Context)}
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
     * Should be invoked after always after {@link PreferenceHelper#init(Context)}
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