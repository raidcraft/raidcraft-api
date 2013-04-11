package de.raidcraft.util;

/**
 * @author Philip
 */
public class StringEncodingUtil {

    public static String encode(String message) {

        message = SignUtil.encodeColor(message);

        // encode special chars
        for(SPECIAL_CHARACTER specialCharacter : SPECIAL_CHARACTER.values()) {
            message = specialCharacter.encode(message);
        }

        return message;
    }

    public static String decode(String message) {

        message = SignUtil.parseColor(message);

        // decode special chars
        for(SPECIAL_CHARACTER specialCharacter : SPECIAL_CHARACTER.values()) {
            message = specialCharacter.decode(message);
        }

        return message;
    }

    public enum SPECIAL_CHARACTER {

        AE_S("@ae@", "ä"),
        OE_S("@oe@", "ö"),
        UE_S("@ue@", "ü"),
        AE_B("@AE@", "Ä"),
        OE_B("@OE@", "Ö"),
        UE_B("@UE@", "Ü"),
        SS("@SS@", "ß");

        private String encoded;
        private String decoded;

        private SPECIAL_CHARACTER(String encoded, String decoded) {

            this.encoded = encoded;
            this.decoded = decoded;
        }

        public String encode(String message) {

            return message.replace(decoded, encoded);
        }

        public String decode(String message) {

            return message.replace(encoded, decoded);
        }
    }
}
