package es.fragonib.vpn.pinsafe.resolver.infrastructure;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import es.fragonib.vpn.pinsafe.resolver.MainActivity;
import es.fragonib.vpn.pinsafe.resolver.R;


public class PermissionHelper {

    /**
     * Runtime SMS permissions
     */
    public boolean hasReadSmsPermission(Context context) {
        int readSMSPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS);
        int receiveSMSPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS);
        return readSMSPermission == PackageManager.PERMISSION_GRANTED &&
                receiveSMSPermission == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Optional informative alert dialog to explain the user why the app needs the Read/Send SMS permission
     */
    public void showRequestPermissionsInfoAlertDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.permission_alert_dialog_title);
        builder.setMessage(R.string.permission_dialog_message);
        builder.setPositiveButton(R.string.action_ok, (dialog, which) -> {
            dialog.dismiss();
            requestReadSmsPermission(activity);
        });
        builder.show();
    }

    public void requestReadSmsPermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_SMS)) {
            Log.d(MainActivity.class.getSimpleName(),
                    "shouldShowRequestPermissionRationale(), no permission requested");
            return;
        }
        int SMS_PERMISSION_CODE = 0;
        ActivityCompat.requestPermissions(
                activity,
                new String[] { Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS },
                SMS_PERMISSION_CODE);
    }

}
