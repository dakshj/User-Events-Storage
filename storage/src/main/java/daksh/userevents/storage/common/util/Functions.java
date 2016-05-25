package daksh.userevents.storage.common.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by daksh on 25-May-16.
 */
public class Functions {

    public static String getRandomString() {
        Random random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
}
