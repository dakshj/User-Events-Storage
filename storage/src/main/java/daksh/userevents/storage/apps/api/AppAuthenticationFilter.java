package daksh.userevents.storage.apps.api;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import daksh.userevents.storage.admins.db.AdminDao;
import daksh.userevents.storage.apps.constants.AppNetworkConstants;
import daksh.userevents.storage.common.api.AuthenticationFilter;

/**
 * Created by daksh on 22-May-16.
 */

@AppSecured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AppAuthenticationFilter extends AuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        handleAuthentication(requestContext, AppNetworkConstants.APP_ID, false);
    }

    @Override
    public String getIdFromToken(String token) throws NotAuthorizedException {
        return AdminDao.getInstance().getAppIdFromAuthorizationToken(token).toString();
    }
}
