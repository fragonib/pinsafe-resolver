package es.fragonib.vpn.pinsafe.resolver.domain;

import lombok.AllArgsConstructor;
import lombok.Value;


@Value
@AllArgsConstructor(staticName = "of")
public class Message {

    public static final String SENDER = "INDITEX";
    public static final String BODY_HEADER = "PINsafe Security String";

    private String sender;
    private String body;

    public boolean isPinSafeMessage() {
        boolean matchSender = sender.equalsIgnoreCase(SENDER);
        boolean matchBody = body.toLowerCase().contains(BODY_HEADER.toLowerCase());
        return matchSender && matchBody;
    }

}