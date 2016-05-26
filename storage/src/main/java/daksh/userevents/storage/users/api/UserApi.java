package daksh.userevents.storage.users.api;

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
import daksh.userevents.storage.common.api.Api;
import daksh.userevents.storage.users.constants.UserNetworkConstants;
import daksh.userevents.storage.users.db.UserDao;
import daksh.userevents.storage.users.model.User;

/**
 * Created by daksh on 22-May-16.
 */

@Path(UserNetworkConstants.BASE_URL)
public class UserApi extends Api<User> {

    @AppSecured
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response create(User user, @Context ContainerRequestContext requestContext) {
        return super.create(user,
                UserDao.getInstance(extractAppId(requestContext)),
                UserNetworkConstants.BASE_URL, true);
    }

    @AppSecured
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context ContainerRequestContext requestContext) {
        return super.getAll(UserDao.getInstance(extractAppId(requestContext)));
    }

    @AppSecured
    @Path(UserNetworkConstants.GET)
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam(UserNetworkConstants.USER_ID) String userIdString,
                        @Context ContainerRequestContext requestContext) {
        return super.get(userIdString, UserDao.getInstance(extractAppId(requestContext)));
    }
}
