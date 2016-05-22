package daksh.userevents.storage.events.api;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import daksh.userevents.storage.accounts.constants.NetworkConstants;
import daksh.userevents.storage.accounts.db.DataSource;
import daksh.userevents.storage.common.Secured;

/**
 * Created by daksh on 22-May-16.
 */

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {
            requestContext.setProperty(NetworkConstants.ACCOUNT_ID,
                    getAccountIdFromAuthorizationToken(token));
        } catch (NotAuthorizedException e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private String getAccountIdFromAuthorizationToken(String token) throws NotAuthorizedException {
        return DataSource.getInstance().getAccountIdFromAuthorizationToken(token);
    }
}
