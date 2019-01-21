package es.fragonib.vpn.pinsafe.resolver;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SmsHelper {

    private static final String LOG_TAG = SmsHelper.class.getSimpleName();

    public static void deleteSMS(Context context, String message, String number) {
        try {
            Log.i(LOG_TAG, "Deleting SMS from inbox");
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = context.getContentResolver().query(uriSms,
                    new String[] { "_id", "thread_id", "address", "person", "date", "body" },
                    null, null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);

                    boolean isTargetSMS = message.equals(body) && address.equals(number);
                    if (isTargetSMS) {
                        Log.i(LOG_TAG, "Deleting SMS with id: " + id);
                        context.getContentResolver().delete(
                                Uri.parse("content://sms/" + id), null, null);
                    }
                } while (c.moveToNext());

                c.close();
            }
        }
        catch (Exception e) {
            Log.i(LOG_TAG, "Could not delete SMS from inbox: " + e.getMessage());
        }
    }

}
