package es.fragonib.vpn.pinsafe.resolver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import es.fragonib.vpn.pinsafe.resolver.infrastructure.PreferenceHelper;


public class ChangePinDialog extends DialogFragment {

    private View dialogView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.pinsafe_update_title)
                .setPositiveButton(R.string.pinsafe_update_button, (dialog, id) -> updatePinAction())
                .setNegativeButton(R.string.pinsafe_cancel_button, (dialog, id) -> dialog.cancel());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.pinsafe_dialog, null);
        builder.setView(dialogView);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void updatePinAction() {
        Context parentActivity = this.getActivity();
        String newPin = getPinEditorValue();
        if (!isPinValid(newPin)) {
            infoInvalidPin(parentActivity);
            return;
        }
        saveNewPin(newPin);
        clearPinEditor();
        infoUpdated(parentActivity);
    }

    private void infoUpdated(Context context) {
        Toast.makeText(context, R.string.pinsafe_updated_message, Toast.LENGTH_SHORT).show();
    }

    private void infoInvalidPin(Context context) {
        Toast.makeText(context, R.string.pinsafe_invalid_message, Toast.LENGTH_SHORT).show();
    }

    private boolean isPinValid(String newPin) {
        return !TextUtils.isEmpty(newPin) && newPin.length() == 4;
    }

    private String getPinEditorValue() {
        EditText pinEditText = dialogView.findViewById(R.id.pinSafeEdit);
        return String.valueOf(pinEditText.getText());
    }

    private void clearPinEditor() {
        EditText pinEditText = dialogView.findViewById(R.id.pinSafeEdit);
        pinEditText.setText("");
    }

    private void saveNewPin(String newPin) {
        PreferenceHelper.save(PreferenceHelper.PREF_SAVED_PIN, newPin);
    }

}