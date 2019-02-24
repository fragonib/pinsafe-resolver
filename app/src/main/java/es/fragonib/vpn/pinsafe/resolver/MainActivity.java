package es.fragonib.vpn.pinsafe.resolver;

import android.app.DialogFragment;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.common.base.Optional;

import es.fragonib.vpn.pinsafe.resolver.domain.PinSafeMessage;
import es.fragonib.vpn.pinsafe.resolver.domain.PinSafePreference;
import es.fragonib.vpn.pinsafe.resolver.infrastructure.PermissionHelper;
import es.fragonib.vpn.pinsafe.resolver.infrastructure.PreferenceHelper;
import es.fragonib.vpn.pinsafe.resolver.infrastructure.SmsHelper;


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = SmsHelper.class.getSimpleName();


    // --------------------------------------- Construction

    private final PermissionHelper permissionHelper;
    private final SmsHelper smsHelper;

    public MainActivity() {
        super();
        permissionHelper = new PermissionHelper();
        smsHelper = new SmsHelper();
    }


    // --------------------------------------- Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUIView();
        initPreferencesStore();
        checkAndRequestForSmsPermissions();
        registerSmsBroadcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "Resumed activity");
        PinSafePreference pinSafePreference = PreferenceHelper.retrieve();
        if (pinSafePreference != null) {
            resolveOldestPinSafeMessage(pinSafePreference);
        } else {
            Log.d(LOG_TAG, "There is no PINsafe preferences stored yet");
        }
    }


    // --------------------------------------- Private

    private void initUIView() {
        setContentView(R.layout.activity_main);
        findViewById(R.id.pinSafeDialogButton).setOnClickListener(v -> {
            if (checkAndRequestForSmsPermissions())
                showChangePinSafeDialog();
        });
    }

    private void initPreferencesStore() {
        PreferenceHelper.init(this);
        PreferenceHelper.register((pinSafePreference) -> {
            Log.d(LOG_TAG, "On preference change");
            resolveOldestPinSafeMessage(pinSafePreference);
        });
    }

    private boolean checkAndRequestForSmsPermissions() {
        if (!permissionHelper.hasReadSmsPermission(this)) {
            permissionHelper.showRequestPermissionsInfoAlertDialog(this);
            return false;
        }
        return true;
    }

    private void registerSmsBroadcastReceiver() {
        SmsBroadcastReceiver smsBroadcastReceiver = new SmsBroadcastReceiver(this::renderNewOtc);
        IntentFilter smsBroadcastFilter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        this.registerReceiver(smsBroadcastReceiver, smsBroadcastFilter);
    }

    private void showChangePinSafeDialog() {
        DialogFragment changePinDialog = new ChangePinDialog();
        changePinDialog.show(getFragmentManager(), "dialog");
    }

    private void resolveOldestPinSafeMessage(PinSafePreference pinSafePreference) {
        Optional<PinSafeMessage> oldestPinSafeMessage = findOldestPinSafeMessage(pinSafePreference);
        if (oldestPinSafeMessage.isPresent()) {
            Log.d(LOG_TAG, "PINsafe SMS detected");
            String newOtc = oldestPinSafeMessage.get().resolveOtc(pinSafePreference.getCode());
            Log.d(LOG_TAG, "Decoded otc: " + newOtc);
            renderNewOtc(newOtc);
        }
    }

    private Optional<PinSafeMessage> findOldestPinSafeMessage(PinSafePreference pinSafePreference) {
        return smsHelper.findOlderSms(this,
                pinSafePreference.getSender(), PinSafeMessage::isPinSafeMessage);
    }

    private void renderNewOtc(String newOtc) {
        TextView otcText = findViewById(R.id.otcText);
        otcText.setText(newOtc);
    }

}