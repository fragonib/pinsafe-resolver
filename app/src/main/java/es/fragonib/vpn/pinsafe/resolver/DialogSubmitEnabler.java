package es.fragonib.vpn.pinsafe.resolver;

import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;

import java.util.function.Predicate;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class DialogSubmitEnabler implements TextWatcher {

    private AlertDialog dialog;
    private Predicate<AlertDialog> validator;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Empty
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Empty
    }

    @Override
    public void afterTextChanged(Editable s) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(validator.test(dialog));
    }

}
