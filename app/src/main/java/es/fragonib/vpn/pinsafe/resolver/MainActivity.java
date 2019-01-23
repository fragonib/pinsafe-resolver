package es.fragonib.vpn.pinsafe.resolver;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.google.common.base.Optional;

import es.fragonib.vpn.pinsafe.resolver.domain.OtcResolver;
import es.fragonib.vpn.pinsafe.resolver.domain.Message;
import es.fragonib.vpn.pinsafe.resolver.infrastructure.PermissionHelper;
import es.fragonib.vpn.pinsafe.resolver.infrastructure.PreferenceHelper;
import es.fragonib.vpn.pinsafe.resolver.infrastructure.SmsHelper;


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = SmsHelper.class.getSimpleName();

    private final PermissionHelper permissionHelper;
    private final SmsHelper smsHelper;
    private final OtcResolver otcResolver;

    public MainActivity() {
        super();
        permissionHelper = new PermissionHelper();
        smsHelper = new SmsHelper();
        otcResolver = new OtcResolver();
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
        Optional<Message> olderPinSafeMessage = smsHelper.findOlderSms(this,
                Message.SENDER, Message::isPinSafeMessage);
        if (olderPinSafeMessage.isPresent()) {
            Log.d(LOG_TAG, "PINsafe SMS detected");
            String pinSafe = PreferenceHelper.retrieve(PreferenceHelper.PREF_SAVED_PIN);
            if (!TextUtils.isEmpty(pinSafe)) {
                String otc = otcResolver.resolveOtc(olderPinSafeMessage.get(), pinSafe);
                Log.d(LOG_TAG, "Decoded otc: " + otc);
                renderNewOtc(otc);
            }
        }
    }


    // --------------------------------------- Private

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

    private void renderNewOtc(String newOtc) {
        TextView otcText = findViewById(R.id.otcText);
        otcText.setText(newOtc);
    }

}