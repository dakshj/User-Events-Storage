package daksh.userevents.storage.admins.api;

import com.mongodb.WriteResult;

import org.bson.types.ObjectId;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import daksh.userevents.storage.admins.constants.AdminNetworkConstants;
import daksh.userevents.storage.admins.db.AdminDao;
import daksh.userevents.storage.admins.model.Admin;
import daksh.userevents.storage.apps.api.AppApi;
import daksh.userevents.storage.common.api.AuthenticationFilter;
import daksh.userevents.storage.common.util.TextUtils;

/**
 * Created by daksh on 22-May-16.
 */

@Path(AdminNetworkConstants.BASE_URL)
public class AdminApi {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response create(Admin admin) {
        if (admin == null ||
                TextUtils.isEmpty(admin.getUsername()) ||
                TextUtils.isEmpty(admin.getPassword()) ||
                TextUtils.isEmpty(admin.getUsername())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (AdminDao.getInstance().usernameExists(admin.getUsername())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Username is already in use")
                    .location(AdminNetworkConstants.getLoginURI()).build();
        }

        ObjectId adminId = AdminDao.getInstance().create(admin);

        if (adminId == null) {
            return Response.serverError().entity("Failed to create Admin").build();
        }

        String authorizationToken = AdminDao.getInstance().regenerateAuthorizationToken(adminId);

        if (authorizationToken == null || authorizationToken.isEmpty()) {
            return Response.serverError().entity("Failed to generate Authorization Token").build();
        }

        return Response.created(null).entity(authorizationToken).build();
    }

    @Path(AdminNetworkConstants.AUTHENTICATE)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response authenticate(Admin admin) {
        if (admin == null ||
                TextUtils.isEmpty(admin.getUsername()) ||
                TextUtils.isEmpty(admin.getPassword())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ObjectId adminId = AdminDao.getInstance().authenticate(admin);

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

    @AdminSecured
    @Path(AdminNetworkConstants.RENAME)
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response rename(Admin admin, @Context ContainerRequestContext requestContext) {
        if (admin == null ||
                TextUtils.isEmpty(admin.getName())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        admin.setId(AppApi.extractAdminId(requestContext));

        if (!AdminDao.getInstance().rename(admin)) {
            return Response.serverError().entity("Failed to rename Admin").build();
        }

        return Response.ok().build();
    }

    @Path(AdminNetworkConstants.LOG_OUT)
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response logOut(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response delete(Admin admin) {
        if (admin == null ||
                TextUtils.isEmpty(admin.getUsername()) ||
                TextUtils.isEmpty(admin.getPassword())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ObjectId adminId = AdminDao.getInstance().authenticate(admin);

        if (adminId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Username or password is wrong")
                    .location(AdminNetworkConstants.getLoginURI()).build();
        }

        WriteResult writeResult = AdminDao.getInstance().delete(adminId);

        if (writeResult.getN() == 0) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Failed to delete admin").build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
