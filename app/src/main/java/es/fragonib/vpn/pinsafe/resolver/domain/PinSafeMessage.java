package es.fragonib.vpn.pinsafe.resolver.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;


@Value
@AllArgsConstructor(staticName = "of")
public class PinSafeMessage {

    private static final String BODY_HEADER = "PINsafe Security String";
    private static final Pattern PIN_SAFE_CODE_TABLE_ENTRY_PATTERN =
            Pattern.compile("(?<number>\\d)______(?<decoding>\\w)");

    @NonNull private String sender;
    @NonNull private String body;

    public boolean isPinSafeMessage() {
        String expectedSmsBodyHeader = BODY_HEADER.toLowerCase();
        boolean matchBodyHeader = this.body.toLowerCase().startsWith(expectedSmsBodyHeader);
        return matchBodyHeader;
    }

    public String resolveOtc(String pinSafe) {
        Map<String, String> codingTable = buildCodingTable(body);
        return resolvePin(codingTable, pinSafe);
    }

    private Map<String, String> buildCodingTable(String codingTableText) {
        Map<String, String> codeTable = new HashMap<>();
        Matcher m = PIN_SAFE_CODE_TABLE_ENTRY_PATTERN.matcher(codingTableText);
        while (m.find()) {
            String coding = m.group(1);
            String decoding = m.group(2);
            codeTable.put(coding, decoding);
        }
        return codeTable;
    }

    private String resolvePin(final Map<String, String> codingTable, String pinSafe) {
        StringBuilder sb = new StringBuilder();
        for (char aChar : pinSafe.toCharArray()) {
            String key = String.valueOf(aChar);
            String decodePart = codingTable.get(key);
            sb.append(decodePart);
        }
        return sb.toString();
    }

}