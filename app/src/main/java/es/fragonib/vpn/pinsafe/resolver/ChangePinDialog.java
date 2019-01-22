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
        builder.setMessage(R.string.update_your_pin)
                .setPositiveButton(R.string.update, (dialog, id) -> updatePinAction())
                .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());

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
        Toast.makeText(context, R.string.pin_updated_message, Toast.LENGTH_SHORT).show();
    }

    private void infoInvalidPin(Context context) {
        Toast.makeText(context, R.string.error_invalid_pin, Toast.LENGTH_SHORT).show();
    }

    /**
     * Validates:
     *
     * <li>Has read SMS permissions</li>
     * <li>Pin safe is valid</li>
     *
     * @param newPin
     *
     * @return boolean validation value
     */
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