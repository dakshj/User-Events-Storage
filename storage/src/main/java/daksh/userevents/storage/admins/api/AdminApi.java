package daksh.userevents.storage.admins.api;

import com.mongodb.WriteResult;

import org.bson.types.ObjectId;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import daksh.userevents.storage.admins.constants.AdminNetworkConstants;
import daksh.userevents.storage.admins.db.AdminDao;
import daksh.userevents.storage.common.api.AuthenticationFilter;

/**
 * Created by daksh on 22-May-16.
 */

@Path(AdminNetworkConstants.BASE_URL)
public class AdminApi {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createAdmin(@FormParam(AdminNetworkConstants.USERNAME) String username,
                                @FormParam(AdminNetworkConstants.PASSWORD) String password,
                                @FormParam(AdminNetworkConstants.NAME) String name) {
        if (username == null || username.isEmpty() ||
                password == null || password.isEmpty() ||
                name == null || name.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (AdminDao.getInstance().usernameExists(username)) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Username is already in use")
                    .location(AdminNetworkConstants.getLoginURI()).build();
        }

        ObjectId adminId = AdminDao.getInstance().createAdmin(username, password, name);

        if (adminId == null) {
            return Response.serverError().entity("Failed to create Admin").build();
        }

        String authorizationToken = AdminDao.getInstance().regenerateAuthorizationToken(adminId);

        if (authorizationToken == null || authorizationToken.isEmpty()) {
            return Response.serverError().entity("Failed to generate Authorization Token").build();
        }

        return Response.created(null).entity(authorizationToken).build();
    }

    @Path(AdminNetworkConstants.AUTHENTICATE_ADMIN)
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response authenticateAdmin(@FormParam(AdminNetworkConstants.USERNAME) String username,
                                      @FormParam(AdminNetworkConstants.PASSWORD) String password) {
        if (username == null || username.isEmpty() ||
                password == null || password.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ObjectId adminId = AdminDao.getInstance().authenticateAdmin(username, password);

        if (adminId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Username or password is wrong")
                    .location(AdminNetworkConstants.getLoginURI()).build();
        }

        String authorizationToken = AdminDao.getInstance().regenerateAuthorizationToken(adminId);

        if (authorizationToken == null || authorizationToken.isEmpty()) {
            return Response.serverError().entity("Failed to generate Authorization Token").build();
        }

        return Response.ok(authorizationToken).build();
    }

    @Path(AdminNetworkConstants.LOG_OUT_ADMIN)
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response logOutAdmin(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        try {
            String token = AuthenticationFilter.getToken(authorizationHeader);
            ObjectId adminId = new ObjectId(AdminDao.getInstance()
                    .getAdminIdFromAuthorizationToken(token));
            AdminDao.getInstance().logOutAdmin(adminId);
        } catch (NotAuthorizedException ignored) {
        }

        return Response.seeOther(AdminNetworkConstants.getLoginURI()).build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteAdmin(@FormParam(AdminNetworkConstants.USERNAME) String username,
                                @FormParam(AdminNetworkConstants.PASSWORD) String password) {
        if (username == null || username.isEmpty() ||
                password == null || password.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ObjectId adminId = AdminDao.getInstance().authenticateAdmin(username, password);

        if (adminId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Username or password is wrong")
                    .location(AdminNetworkConstants.getLoginURI()).build();
        }

        WriteResult writeResult = AdminDao.getInstance().deleteAdmin(adminId);

        if (writeResult.getN() == 0) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Failed to delete admin").build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
