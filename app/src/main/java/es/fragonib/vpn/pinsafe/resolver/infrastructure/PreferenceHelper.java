package es.fragonib.vpn.pinsafe.resolver.infrastructure;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Helper repository for app preferences
 */
public class PreferenceHelper {

    public static String PREF_SAVED_PIN = "pinSafe";

    private static SharedPreferences preferences;

    public static void init(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
 
    public static void save(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public static String retrieve(String key) {
        return preferences.getString(key, null);
    }
 
}