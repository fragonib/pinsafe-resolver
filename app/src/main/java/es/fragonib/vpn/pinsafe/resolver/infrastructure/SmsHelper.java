package es.fragonib.vpn.pinsafe.resolver.infrastructure;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony.Sms;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import es.fragonib.vpn.pinsafe.resolver.domain.PinSafeMessage;


public class SmsHelper {

    private static final String LOG_TAG = SmsHelper.class.getSimpleName();

    public Optional<PinSafeMessage> findOlderSms(
            Context context, String sender, Predicate<PinSafeMessage> messagePredicate) {

        Log.i(LOG_TAG, String.format("Last SMS with address [%s] from inbox", sender));

        Uri contentUri = Sms.CONTENT_URI;
        String[] projection = { Sms.ADDRESS, Sms.BODY, Sms.DATE };
        String selection = Sms.TYPE + " = ? and " + Sms.ADDRESS + " = ?";
        String[] selectionArgs = { String.valueOf(Sms.MESSAGE_TYPE_INBOX), sender };
        String sortOrder = Sms.DATE + " DESC";

        Optional<PinSafeMessage> lastSms = Optional.absent();

        try (Cursor c = context.getContentResolver().query(
                contentUri,
                projection,
                selection, selectionArgs,
                sortOrder)) {

            if (c != null && c.moveToFirst()) {
                do {
                    String body = c.getString(1);
                    PinSafeMessage message = PinSafeMessage.of(sender, body);
                    boolean isTargetSMS = messagePredicate.apply(message);
                    if (isTargetSMS) {
                        lastSms = Optional.of(message);
                        break;
                    }

                } while (c.moveToNext());
            }

        }
        catch (Exception e) {
            Log.i(LOG_TAG, "Could not find SMS from inbox: " + e.getMessage());
        }

        return lastSms;

    }

    public void deleteSMS(Context context, PinSafeMessage message) {
        try {
            Log.i(LOG_TAG, "Deleting SMS from inbox");
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = context.getContentResolver().query(uriSms,
                    new String[]{"_id", "thread_id", "address", "person", "date", "body"},
                    null, null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);

                    boolean isTargetSMS = message.getBody().equals(address) &&
                            message.getBody().equals(body);
                    if (isTargetSMS) {
                        Log.i(LOG_TAG, "Deleting SMS with id: " + id);
                        context.getContentResolver().delete(
                                Uri.parse("content://sms/" + id), null, null);
                    }
                } while (c.moveToNext());

                c.close();
            }
        } catch (Exception e) {
            Log.i(LOG_TAG, "Could not delete SMS from inbox: " + e.getMessage());
        }
    }

}
