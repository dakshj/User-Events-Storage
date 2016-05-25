package daksh.userevents.storage.admins.constants;

import java.net.URI;

/**
 * Created by daksh on 22-May-16.
 */
public final class AdminNetworkConstants {

    public static final String BASE_URL = "/admins";

    public static final String USERNAME = "username";
    public static final String NAME = "name";

    public static final String AUTHENTICATE = "/login";

    public static final String LOG_OUT = "/logout";

    public static final String ADMIN_ID = "admin_id";

    public static final String RENAME = "/rename";

    public static URI getLoginURI() {
        return URI.create(AdminNetworkConstants.BASE_URL
                + AdminNetworkConstants.AUTHENTICATE);
    }
}
