package mboog.support.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author LiYi
 */
public abstract class IdUtil {

    public String sha1Id(String... keys){
        return DigestUtils.shaHex(String.join(",", keys));
    }
}
