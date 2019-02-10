package es.fragonib.vpn.pinsafe.resolver.domain;

import android.text.TextUtils;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;


@Value
@AllArgsConstructor(staticName = "of")
public class PinSafePreference {

    @NonNull private String sender;
    @NonNull private String code;

    public boolean isValid() {
        return !TextUtils.isEmpty(sender) && sender.length() > 0 &&
                !TextUtils.isEmpty(code) && code.length() == 4 ;
    }

}
