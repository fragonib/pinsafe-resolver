package es.fragonib.vpn.pinsafe.resolver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import es.fragonib.vpn.pinsafe.resolver.domain.PinSafeMessage;
import es.fragonib.vpn.pinsafe.resolver.domain.PinSafePreference;
import es.fragonib.vpn.pinsafe.resolver.infrastructure.NotificationHelper;
import es.fragonib.vpn.pinsafe.resolver.infrastructure.PreferenceHelper;
import es.fragonib.vpn.pinsafe.resolver.infrastructure.SmsHelper;


/**
 * A broadcast receiver who listens for incoming SMS and decodes PINsafe
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = SmsBroadcastReceiver.class.getSimpleName();

    private final OnPinsafeReceivedListener onPinsafeReceivedListener;
    private final NotificationHelper notificationHelper;
    private final SmsHelper smsHelper;

    public SmsBroadcastReceiver(OnPinsafeReceivedListener onPinsafeReceivedListener) {
        super();
        this.onPinsafeReceivedListener = onPinsafeReceivedListener;
        this.notificationHelper = new NotificationHelper();
        this.smsHelper = new SmsHelper();
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {

            PinSafeMessage message = messageFromIntent(intent);
            Log.d(LOG_TAG, "SMS received from\n" + message);

            if (message.isPinSafeMessage()) {
                Log.d(LOG_TAG, "PINsafe SMS detected");
                PinSafePreference pinSafePreference = PreferenceHelper.retrieve();
                if (pinSafePreference != null) {
                    String otc = message.resolveOtc(pinSafePreference.getCode());
                    Log.d(LOG_TAG, "Decoded otc: " + otc);
                    onPinsafeReceivedListener.onPinsafeReceived(otc);
                    sendOtcNotification(context, otc);
                }
            }

        }
    }

    public PinSafeMessage messageFromIntent(Intent smsIntent) {
        String smsSender = "";
        String smsBody = "";
        SmsMessage[] messagesFromIntent = Telephony.Sms.Intents.getMessagesFromIntent(smsIntent);
        for (SmsMessage smsMessage : messagesFromIntent) {
            smsBody = smsMessage.getMessageBody();
            smsSender = smsMessage.getOriginatingAddress();
        }
        return PinSafeMessage.of(smsSender, smsBody);
    }

    private void sendOtcNotification(Context context, String otc) {
        String notificationText = otc;
        notificationHelper.notify(context, notificationText);
        Toast.makeText(context, notificationText, Toast.LENGTH_LONG).show();
    }

}
