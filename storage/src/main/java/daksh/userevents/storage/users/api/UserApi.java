package daksh.userevents.storage.users.api;

import org.bson.types.ObjectId;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import daksh.userevents.storage.apps.api.AppSecured;
import daksh.userevents.storage.apps.constants.AppNetworkConstants;
import daksh.userevents.storage.users.constants.UserNetworkConstants;
import daksh.userevents.storage.users.db.UserDao;
import daksh.userevents.storage.users.model.User;

/**
 * Created by daksh on 22-May-16.
 */

@Path(UserNetworkConstants.BASE_URL)
public class UserApi {

    @AppSecured
    @Path(UserNetworkConstants.CREATE_USER)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createUser(User user, @Context ContainerRequestContext requestContext) {
        if (user == null ||
                user.getName() == null || user.getName().isEmpty() ||
                user.getDefaultProperties() == null || user.getDefaultProperties().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ObjectId appId = extractAppId(requestContext);

        ObjectId userId = UserDao.getInstance(appId).create(user);

        return Response.created(
                URI.create(UserNetworkConstants.BASE_URL + "/" + userId)
        ).build();
    }

    @AppSecured
    @Path(UserNetworkConstants.GET_USER)
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam(UserNetworkConstants.USER_ID) String userId,
                            @Context ContainerRequestContext requestContext) {
        if (userId == null || userId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ObjectId appId = extractAppId(requestContext);

        User user = UserDao.getInstance(appId).get(new ObjectId(userId));

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(user).build();
    }

    @AppSecured
    @Path(UserNetworkConstants.GET_ALL_USERS)
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@Context ContainerRequestContext requestContext) {
        ObjectId appId = extractAppId(requestContext);

        List<User> allUsers = UserDao.getInstance(appId).getAll();

        if (allUsers == null) {
            return Response.serverError().entity("Failed to fetch Apps").build();
        }

        return Response.ok(allUsers).build();
    }

    private static ObjectId extractAppId(ContainerRequestContext requestContext) {
        return new ObjectId((String) requestContext.getProperty(AppNetworkConstants.APP_ID));
    }
}
