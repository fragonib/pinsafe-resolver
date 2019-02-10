package es.fragonib.vpn.pinsafe.resolver;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.common.base.Optional;

import es.fragonib.vpn.pinsafe.resolver.domain.PinSafeMessage;
import es.fragonib.vpn.pinsafe.resolver.domain.PinSafePreference;
import es.fragonib.vpn.pinsafe.resolver.infrastructure.PermissionHelper;
import es.fragonib.vpn.pinsafe.resolver.infrastructure.PreferenceHelper;
import es.fragonib.vpn.pinsafe.resolver.infrastructure.SmsHelper;


public class MainActivity extends AppCompatActivity implements OnDialogCloseListener {

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

        // Init view
        setContentView(R.layout.activity_main);
        findViewById(R.id.pinSafeDialogButton).setOnClickListener(v -> {
                    if (checkAndRequestForSmsPermissions())
                        showChangePinSafeDialog();
                });

        // Init preferences store
        initPreferencesStore();

        // Check for permissions
        checkAndRequestForSmsPermissions();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "Resumed activity");
        resolveOldestPinSafeMessage();
    }

    @Override
    public void onDialogClose() {
        Log.d(LOG_TAG, "On dialog close");
        resolveOldestPinSafeMessage();
    }


    // --------------------------------------- Private

    private void resolveOldestPinSafeMessage() {
        PinSafePreference pinSafePreference = PreferenceHelper.retrieve();
        if (pinSafePreference != null) {
            Optional<PinSafeMessage> oldestPinSafeMessage = findOldestPinSafeMessage(pinSafePreference);
            if (oldestPinSafeMessage.isPresent()) {
                Log.d(LOG_TAG, "PINsafe SMS detected");
                String newOtc = oldestPinSafeMessage.get().resolveOtc(pinSafePreference.getCode());
                Log.d(LOG_TAG, "Decoded otc: " + newOtc);
                renderNewOtc(newOtc);
            }
        }
        else {
            Log.d(LOG_TAG, "There is no PINsafe preferences stored yet");
        }
    }

    private Optional<PinSafeMessage> findOldestPinSafeMessage(PinSafePreference pinSafePreference) {
        Optional<PinSafeMessage> olderSms = smsHelper.findOlderSms(this,
                pinSafePreference.getSender(), PinSafeMessage::isPinSafeMessage);
        return olderSms;
    }

    private void renderNewOtc(String newOtc) {
        TextView otcText = findViewById(R.id.otcText);
        otcText.setText(newOtc);
    }

    private void initPreferencesStore() {
        PreferenceHelper.init(this);
    }

    private boolean checkAndRequestForSmsPermissions() {
        if (!permissionHelper.hasReadSmsPermission(this)) {
            permissionHelper.showRequestPermissionsInfoAlertDialog(this);
            return false;
        }
        return true;
    }

    void showChangePinSafeDialog() {
        DialogFragment changePinDialog = new ChangePinDialog();
        changePinDialog.show(getFragmentManager(), "dialog");
    }

}