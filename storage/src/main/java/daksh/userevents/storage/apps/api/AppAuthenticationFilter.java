package daksh.userevents.storage.apps.api;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import daksh.userevents.storage.admins.db.AdminDao;
import daksh.userevents.storage.apps.constants.AppNetworkConstants;

/**
 * Created by daksh on 22-May-16.
 */

@AppSecured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AppAuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {
            requestContext.setProperty(AppNetworkConstants.APP_ID,
                    getAppIdFromAuthorizationToken(token));
        } catch (NotAuthorizedException e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage()).build());
        }
    }

    private String getAppIdFromAuthorizationToken(String token) throws NotAuthorizedException {
        return AdminDao.getInstance().getAppIdFromAuthorizationToken(token).toString();
    }
}
