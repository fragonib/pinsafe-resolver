package es.fragonib.vpn.pinsafe.resolver.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OtcResolver {

    private static final Pattern PIN_SAFE_CODE_TABLE_ENTRY_PATTERN =
            Pattern.compile("(?<number>\\d)______(?<decoding>\\w)");

    public String resolveOtc(String pinSafeCodingTable, String pinSafe) {
        Map<String, String> codingTable = buildCodingTable(pinSafeCodingTable);
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
