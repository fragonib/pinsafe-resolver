package es.fragonib.vpn.pinsafe.resolver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import es.fragonib.vpn.pinsafe.resolver.domain.PinSafePreference;
import es.fragonib.vpn.pinsafe.resolver.infrastructure.PreferenceHelper;


public class ChangePinDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.pinsafe_update_title)
                .setPositiveButton(R.string.pinsafe_update_button, (dialog, id) -> updatePinSafePreferences(dialog))
                .setNegativeButton(R.string.pinsafe_cancel_button, (dialog, id) -> dialog.cancel());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.pinsafe_dialog, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(this::resetPinSafeDialog);

        // Add valid listeners to text dialog
        EditText pinSafeSMSSenderEditText = dialogView.findViewById(R.id.pinSafeSmsSenderEditText);
        pinSafeSMSSenderEditText.addTextChangedListener(
                new DialogSubmitEnabler(alertDialog, this::enableSubmitIfValidPreferences));

        EditText pinSafePinCodeEditText = dialogView.findViewById(R.id.pinSafePinCodeEditText);
        pinSafePinCodeEditText.addTextChangedListener(
                new DialogSubmitEnabler(alertDialog, this::enableSubmitIfValidPreferences));

        return alertDialog;
    }

    private void resetPinSafeDialog(DialogInterface dialog) {
        AlertDialog alertDialog = (AlertDialog) dialog;
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        EditText pinSafeSMSSenderEditText = alertDialog.findViewById(R.id.pinSafeSmsSenderEditText);
        PinSafePreference pinSafePreference = PreferenceHelper.retrieve();
        String senderValue = (pinSafePreference == null) ? "" : pinSafePreference.getSender();
        pinSafeSMSSenderEditText.setText(senderValue);

        EditText pinSafePinCodeEditText = alertDialog.findViewById(R.id.pinSafePinCodeEditText);
        pinSafePinCodeEditText.setText("");

        if (pinSafePreference != null)
            pinSafePinCodeEditText.requestFocus();
    }

    public boolean enableSubmitIfValidPreferences(AlertDialog alertDialog) {
        PinSafePreference pinSafePreference = retrieveUserPinSafePreference(alertDialog);
        return pinSafePreference.isValid();
    }

    private PinSafePreference retrieveUserPinSafePreference(AlertDialog alertDialog) {
        EditText pinSafeSMSSenderEditText = alertDialog.findViewById(R.id.pinSafeSmsSenderEditText);
        String newPinSafeSender = String.valueOf(pinSafeSMSSenderEditText.getText());
        EditText pinSafePinCodeEditText = alertDialog.findViewById(R.id.pinSafePinCodeEditText);
        String newPinSafePinCode = String.valueOf(pinSafePinCodeEditText.getText());
        return PinSafePreference.of(newPinSafeSender, newPinSafePinCode);
    }

    private void updatePinSafePreferences(DialogInterface dialog) {
        AlertDialog alertDialog = (AlertDialog) dialog;
        PinSafePreference pinSafePreference = retrieveUserPinSafePreference(alertDialog);
        storePinSafePreferences(pinSafePreference);
        alertPinSafePreferencesUpdated(this.getActivity());
    }

    private void storePinSafePreferences(PinSafePreference preference) {
        PreferenceHelper.save(preference);
    }

    private void alertPinSafePreferencesUpdated(Context context) {
        Toast.makeText(context, R.string.pinsafe_updated_message, Toast.LENGTH_SHORT).show();
    }

}