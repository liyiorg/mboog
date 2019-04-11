package mboog.support.util;

import org.apache.commons.codec.digest.DigestUtils;

public abstract class IdUtil {

    public String sha1Id(String... keys){
        return DigestUtils.shaHex(String.join(",", keys));
    }
}
