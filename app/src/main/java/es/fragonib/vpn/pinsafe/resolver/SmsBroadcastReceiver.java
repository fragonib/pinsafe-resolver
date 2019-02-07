package es.fragonib.vpn.pinsafe.resolver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import es.fragonib.vpn.pinsafe.resolver.domain.Message;
import es.fragonib.vpn.pinsafe.resolver.domain.OtcResolver;
import es.fragonib.vpn.pinsafe.resolver.infrastructure.NotificationHelper;
import es.fragonib.vpn.pinsafe.resolver.infrastructure.PreferenceHelper;
import es.fragonib.vpn.pinsafe.resolver.infrastructure.SmsHelper;


/**
 * A broadcast receiver who listens for incoming SMS and decodes PINsafe
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = SmsBroadcastReceiver.class.getSimpleName();

    private final OtcResolver otcResolver;
    private final NotificationHelper notificationHelper;
    private final SmsHelper smsHelper;

    public SmsBroadcastReceiver() {
        super();
        otcResolver = new OtcResolver();
        notificationHelper = new NotificationHelper();
        smsHelper = new SmsHelper();
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {

            Message message = from(intent);
            Log.d(LOG_TAG, "SMS received from\n" + message);

            if (message.isPinSafeMessage()) {
                Log.d(LOG_TAG, "PINsafe SMS detected");
                String pinSafe = PreferenceHelper.retrieve(PreferenceHelper.PREF_SAVED_PIN);
                if (!TextUtils.isEmpty(pinSafe)) {
                    String codingTableText = message.getBody();
                    String otc = otcResolver.resolveOtc(codingTableText, pinSafe);
                    Log.d(LOG_TAG, "Decoded otc: " + otc);
                    sendOtcNotification(context, otc);
                    delayedDeleteSMS(context, message);
                }
            }

        }
    }

    private void sendOtcNotification(Context context, String otc) {
        String notification = otc;
        notificationHelper.notify(context, notification);
        Toast.makeText(context, notification, Toast.LENGTH_LONG).show();
    }

    private void delayedDeleteSMS(Context context, Message message) {
        new Timer().schedule(new TimerTask() {
                @Override public void run() {
                    smsHelper.deleteSMS(context, message);
                }
            }, 1000L);
    }



    public Message from(Intent smsIntent) {
        String smsSender = "";
        String smsBody = "";
        SmsMessage[] messagesFromIntent = Telephony.Sms.Intents.getMessagesFromIntent(smsIntent);
        for (SmsMessage smsMessage : messagesFromIntent) {
            smsBody = smsMessage.getMessageBody();
            smsSender = smsMessage.getOriginatingAddress();
        }
        return Message.of(smsSender, smsBody);
    }

}
