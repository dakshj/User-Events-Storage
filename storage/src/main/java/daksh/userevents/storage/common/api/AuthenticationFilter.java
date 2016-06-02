package daksh.userevents.storage.common.api;

import java.util.List;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import daksh.userevents.storage.admins.constants.AdminNetworkConstants;

/**
 * Created by daksh on 23-May-16.
 */

public abstract class AuthenticationFilter implements ContainerRequestFilter {

    protected void handleAuthentication(ContainerRequestContext requestContext,
                                        String key, boolean redirectToLoginOnFailure) {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        try {
            String token = getToken(authorizationHeader);
            requestContext.setProperty(key, getIdFromToken(token));
        } catch (NotAuthorizedException e) {
            requestContext.abortWith(getNotAuthorizedExceptionResponse(e, redirectToLoginOnFailure));
        }
    }

    public static String getToken(String authorizationHeader) throws NotAuthorizedException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        String token = authorizationHeader.substring("Bearer".length()).trim();

        if (token.isEmpty()) {
            throw new NotAuthorizedException("Authorization Token is invalid");
        }

        return token;
    }

    private static String getChallengeString(NotAuthorizedException e) {
        List<Object> challenges = e.getChallenges();

        String message = null;

        if (challenges != null && challenges.size() > 0) {
            message = challenges.get(0).toString();
        }

        return message;
    }

    private static Response getNotAuthorizedExceptionResponse(NotAuthorizedException e,
                                                              boolean redirectToLoginOnFailure) {
        Response.ResponseBuilder responseBuilder = Response.status(Response.Status.UNAUTHORIZED);

        String message = getChallengeString(e);

        if (message != null && !message.isEmpty()) {
            responseBuilder.entity(message);
        }

        if (redirectToLoginOnFailure) {
            responseBuilder.location(AdminNetworkConstants.getLoginURI());
        }

        return responseBuilder.build();
    }

    public abstract String getIdFromToken(String token) throws NotAuthorizedException;
}
