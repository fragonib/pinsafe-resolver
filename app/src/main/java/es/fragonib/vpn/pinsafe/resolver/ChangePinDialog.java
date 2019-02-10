package es.fragonib.vpn.pinsafe.resolver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import es.fragonib.vpn.pinsafe.resolver.domain.PinSafePreference;
import es.fragonib.vpn.pinsafe.resolver.infrastructure.PreferenceHelper;


public class ChangePinDialog extends DialogFragment {

    private OnDialogCloseListener listener;
    private View dialogView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {
            listener = (OnDialogCloseListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDialogCloseListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.pinsafe_update_title)
                .setPositiveButton(R.string.pinsafe_update_button, (dialog, id) -> updatePinPreferences())
                .setNegativeButton(R.string.pinsafe_cancel_button, (dialog, id) -> dialog.cancel());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.pinsafe_dialog, null);
        builder.setView(dialogView);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void updatePinPreferences() {
        Context parentActivity = this.getActivity();
        PinSafePreference pinSafePreference = retrieveUserPinSafePreference();
        if (!pinSafePreference.isValid()) {
            infoInvalidPinSafePreferences(parentActivity);
            return;
        }
        savePinSafePreferences(pinSafePreference);
        resetPinSafeDialog();
        infoPinSafePreferencesUpdated(parentActivity);
        if (listener != null)
            listener.onDialogClose();
    }

    private PinSafePreference retrieveUserPinSafePreference() {
        EditText pinSafeSMSSenderEditText = dialogView.findViewById(R.id.pinSafeSmsSenderEditText);
        String newPinSafeSender = String.valueOf(pinSafeSMSSenderEditText.getText());
        EditText pinSafePinCodeEditText = dialogView.findViewById(R.id.pinSafePinCodeEditText);
        String newPinSafePinCode = String.valueOf(pinSafePinCodeEditText.getText());
        return PinSafePreference.of(newPinSafeSender, newPinSafePinCode);
    }

    private void savePinSafePreferences(PinSafePreference preference) {
        PreferenceHelper.save(preference);
    }

    private void infoPinSafePreferencesUpdated(Context context) {
        Toast.makeText(context, R.string.pinsafe_updated_message, Toast.LENGTH_SHORT).show();
    }

    private void infoInvalidPinSafePreferences(Context context) {
        Toast.makeText(context, R.string.pinsafe_invalid_message, Toast.LENGTH_SHORT).show();
    }

    private void resetPinSafeDialog() {
        EditText pinSafeSMSSenderEditText = dialogView.findViewById(R.id.pinSafeSmsSenderEditText);
        String senderValue = (PreferenceHelper.retrieve() == null) ? "" :
                PreferenceHelper.retrieve().getSender();
        pinSafeSMSSenderEditText.setText(senderValue);

        EditText pinSafePinCodeEditText = dialogView.findViewById(R.id.pinSafePinCodeEditText);
        pinSafePinCodeEditText.setText("");
    }

}