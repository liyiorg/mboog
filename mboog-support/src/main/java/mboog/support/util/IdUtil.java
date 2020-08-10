package mboog.support.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author LiYi
 */
public abstract class IdUtil {

    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final String SHA = "SHA";

    protected static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return out;
    }

    public static String sha1Id(String... keys) {
        try {
            byte[] bytes = String.join(",", keys).getBytes(StandardCharsets.UTF_8);
            byte[] digest = MessageDigest.getInstance(SHA).digest(bytes);
            return new String(encodeHex(digest));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
