package es.fragonib.vpn.pinsafe.resolver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A broadcast receiver who listens for incoming SMS and decodes PINSafe
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = SmsBroadcastReceiver.class.getSimpleName();

    public static final String PIN_SAFE_MSG_SENDER = "INDITEX";
    private static final String PIN_SAFE_MSG_TITLE = "PINSafe";
    private static final Pattern PIN_SAFE_CODE_TABLE_ENTRY_PATTERN =
            Pattern.compile("(?<number>\\d)______(?<decoding>\\w)");

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {

            String smsSender = "";
            String smsBody = "";
            SmsMessage[] messagesFromIntent = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            for (SmsMessage smsMessage : messagesFromIntent) {
                smsBody = smsMessage.getMessageBody();
                smsSender = smsMessage.getOriginatingAddress();
            }
            Log.d(LOG_TAG, "SMS received from [" + smsSender + "] with text \n" + smsBody);

            if (isPinSafeMessage(smsSender, smsBody)) {
                Log.d(LOG_TAG, "PINSafe SMS detected");
                String password = calculatePassword(smsBody);
                Log.d(LOG_TAG, "Decoded password: " + password);
                if (password != null) {
                    sendPasswordNotification(context, password);
                    final String finalSmsBody = smsBody;
                    final String finalSmsSender = smsSender;
                    executeDelayed(new TimerTask() {
                            @Override public void run() {
                                SmsHelper.deleteSMS(context, finalSmsBody, finalSmsSender);
                            }
                        }, 1000);
                }
            }

        }
    }

    private void executeDelayed(TimerTask task, long delay) {
        new Timer().schedule(task, delay);
    }

    private boolean isPinSafeMessage(String smsSender, String smsBody) {
        boolean isSender = smsSender.equalsIgnoreCase(PIN_SAFE_MSG_SENDER);
        boolean isMessage = smsBody.toLowerCase().contains(PIN_SAFE_MSG_TITLE.toLowerCase());
        return isSender && isMessage;
    }

    private String calculatePassword(String message) {
        String pin = retrievePINPref();
        if (pin == null)
            return null;
        Map<String, String> codingTable = buildCodingTable(message);
        return resolvePin(codingTable, pin);
    }

    private String retrievePINPref() {
        return PreferenceHelper.retrieve(PreferenceHelper.PREF_SAVED_PIN);
    }

    private Map<String, String> buildCodingTable(String message) {
        Map<String, String> codeTable = new HashMap<>();
        Matcher m = PIN_SAFE_CODE_TABLE_ENTRY_PATTERN.matcher(message);
        while (m.find()) {
            String coding = m.group(1);
            String decoding = m.group(2);
            codeTable.put(coding, decoding);
        }
        return codeTable;
    }

    private String resolvePin(final Map<String, String> codingTable, String pin) {
        StringBuilder sb = new StringBuilder();
        for (char aChar : pin.toCharArray()) {
            String key = String.valueOf(aChar);
            String decodePart = codingTable.get(key);
            sb.append(decodePart);
        }
        return sb.toString();
    }

    public void sendPasswordNotification(Context context, String password) {
        String msg = password;
        NotificationHelper notificationHelper = new NotificationHelper();
        notificationHelper.notify(context, msg);
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

}
