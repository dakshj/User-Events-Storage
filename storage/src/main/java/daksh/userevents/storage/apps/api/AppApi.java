package daksh.userevents.storage.apps.api;

import com.mongodb.WriteResult;

import org.bson.types.ObjectId;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import daksh.userevents.storage.admins.api.AdminSecured;
import daksh.userevents.storage.admins.constants.AdminNetworkConstants;
import daksh.userevents.storage.apps.constants.AppNetworkConstants;
import daksh.userevents.storage.apps.db.AppDao;
import daksh.userevents.storage.apps.model.App;
import daksh.userevents.storage.common.util.TextUtils;

/**
 * Created by daksh on 23-May-16.
 */

@Path(AppNetworkConstants.BASE_URL)
public class AppApi {

    @AdminSecured
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response create(App app, @Context ContainerRequestContext requestContext) {
        if (app == null || TextUtils.isEmpty(app.getName())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ObjectId adminId = extractAdminId(requestContext);

        final AppDao appDao = AppDao.getInstance(adminId);

        if (appDao.exists(AppNetworkConstants.NAME, app.getName())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("App Name is already in use").build();
        }

        ObjectId appId = appDao.create(app);

        if (appId == null) {
            return Response.serverError()
                    .entity("Failed to create app").build();
        }

        String appToken = appDao.regenerateAppToken(appId);

        if (appToken == null || appToken.isEmpty()) {
            return Response.serverError().entity("Failed to generate App Token").build();
        }

        return Response.created(
                URI.create(AppNetworkConstants.BASE_URL + "/" + appId)
        ).entity(appToken).build();
    }

    @AdminSecured
    @Path(AppNetworkConstants.GET_APP)
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam(AppNetworkConstants.APP_ID) String appId,
                        @Context ContainerRequestContext requestContext) {
        if (appId == null || appId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ObjectId adminId = extractAdminId(requestContext);

        App app = AppDao.getInstance(adminId).get(new ObjectId(appId));

        if (app == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(app).build();
    }

    @AdminSecured
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context ContainerRequestContext requestContext) {
        ObjectId adminId = extractAdminId(requestContext);

        List<App> allApps = AppDao.getInstance(adminId).getAll();

        if (allApps == null) {
            return Response.serverError().entity("Failed to fetch Apps").build();
        }

        return Response.ok(allApps).build();
    }

    @AdminSecured
    @Path(AppNetworkConstants.REGENERATE_APP_TOKEN)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response regenerateAppToken(App app, @Context ContainerRequestContext requestContext) {
        if (app == null || TextUtils.isEmpty(app.getId().toString())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ObjectId adminId = extractAdminId(requestContext);

        final AppDao appDao = AppDao.getInstance(adminId);

        String appToken = appDao.regenerateAppToken(app.getId());

        if (appToken == null || appToken.isEmpty()) {
            return Response.serverError().entity("Failed to generate App Token").build();
        }

        return Response.ok(appToken).build();
    }

    @AdminSecured
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response delete(App app, @Context ContainerRequestContext requestContext) {
        if (app == null || TextUtils.isEmpty(app.getId().toString())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ObjectId adminId = extractAdminId(requestContext);

        WriteResult writeResult = AppDao.getInstance(adminId).delete(app.getId());

        if (writeResult.getN() == 0) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Failed to delete app").build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    public static ObjectId extractAdminId(ContainerRequestContext requestContext) {
        return new ObjectId((String) requestContext.getProperty(AdminNetworkConstants.ADMIN_ID));
    }
}
