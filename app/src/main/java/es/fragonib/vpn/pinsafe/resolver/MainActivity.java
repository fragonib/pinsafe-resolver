package es.fragonib.vpn.pinsafe.resolver;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    // --------------------------------------- Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!hasReadSmsPermission())
            showRequestPermissionsInfoAlertDialog();

        PreferenceHelper.init(this);
    }


    // ---------------------------------------- Actions

    public void updatePinAction(View v) {
        EditText pinEditText = findViewById(R.id.pin_number);
        String newPin = String.valueOf(pinEditText.getText());
        if (!hasValidPreConditions(newPin)) return;
        PreferenceHelper.save(PreferenceHelper.PREF_SAVED_PIN, newPin);
        pinEditText.setText("");
        Toast.makeText(getApplicationContext(), R.string.pin_updated_message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Validates if the app has readSmsPermissions and the pin safe is valid
     *
     * @param newPin
     *
     * @return boolean validation value
     */
    private boolean hasValidPreConditions(String newPin) {
        if (!hasReadSmsPermission()) {
            requestReadSmsPermission();
            return false;
        }

        if (TextUtils.isEmpty(newPin) || newPin.length() != 4) {
            Toast.makeText(getApplicationContext(), R.string.error_invalid_pin, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    // --------------------------------------- Permissions

    /**
     * Runtime SMS permissions
     */
    private boolean hasReadSmsPermission() {
        int readSMSPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS);
        int receiveSMSPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS);
        return readSMSPermission == PackageManager.PERMISSION_GRANTED &&
                receiveSMSPermission == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Optional informative alert dialog to explain the user why the app needs the Read/Send SMS permission
     */
    private void showRequestPermissionsInfoAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_alert_dialog_title);
        builder.setMessage(R.string.permission_dialog_message);
        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestReadSmsPermission();
            }
        });
        builder.show();
    }

    private void requestReadSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_SMS)) {
            Log.d(MainActivity.class.getSimpleName(),
                    "shouldShowRequestPermissionRationale(), no permission requested");
            return;
        }
        int SMS_PERMISSION_CODE = 0;
        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[] { Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS },
                SMS_PERMISSION_CODE);
    }

}