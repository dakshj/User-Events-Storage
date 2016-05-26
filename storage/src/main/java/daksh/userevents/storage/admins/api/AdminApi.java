package daksh.userevents.storage.admins.api;

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
import daksh.userevents.storage.common.api.Api;
import daksh.userevents.storage.common.api.AuthenticationFilter;
import daksh.userevents.storage.common.constants.Constants;
import daksh.userevents.storage.common.util.TextUtils;

/**
 * Created by daksh on 22-May-16.
 */

@Path(AdminNetworkConstants.BASE_URL)
public class AdminApi extends Api<Admin> {

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

        return super.create(admin, AdminDao.getInstance(), AdminNetworkConstants.BASE_URL,
                true, AdminNetworkConstants.USERNAME, admin.getUsername(),
                true, true);
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

        ObjectId adminId = AdminDao.getInstance()
                .authenticate(admin.getUsername(), admin.getPassword());

        if (adminId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Username or password is wrong")
                    .location(AdminNetworkConstants.getLoginURI()).build();
        }

        String token = AdminDao.getInstance().regenerateToken(adminId);

        if (TextUtils.isEmpty(token)) {
            return Response.serverError().entity("Failed to generate token").build();
        }

        return Response.ok(token).build();
    }

    @AdminSecured
    @Path(AdminNetworkConstants.RENAME)
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response rename(Admin admin, @Context ContainerRequestContext requestContext) {
        return super.updateField(extractAdminId(requestContext), AdminDao.getInstance(),
                AdminNetworkConstants.NAME, admin.getName());
    }

    @AdminSecured
    @Path(Constants.UPDATE_PROPERTIES)
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateProperties(Admin admin, @Context ContainerRequestContext requestContext) {
        if (admin != null) {
            admin.setId(extractAdminId(requestContext));
            return super.updateProperties(admin, AdminDao.getInstance());
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
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

        ObjectId adminId = AdminDao.getInstance()
                .authenticate(admin.getUsername(), admin.getPassword());

        if (adminId == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Username or password is wrong")
                    .location(AdminNetworkConstants.getLoginURI()).build();
        }

        return super.delete(admin, AdminDao.getInstance());
    }
}
