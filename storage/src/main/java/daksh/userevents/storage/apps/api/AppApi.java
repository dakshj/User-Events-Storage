package daksh.userevents.storage.apps.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import daksh.userevents.storage.admins.api.AdminSecured;
import daksh.userevents.storage.apps.constants.AppNetworkConstants;
import daksh.userevents.storage.apps.db.AppDao;
import daksh.userevents.storage.apps.model.App;
import daksh.userevents.storage.common.api.Api;
import daksh.userevents.storage.common.constants.Constants;

/**
 * Created by daksh on 23-May-16.
 */

@Path(AppNetworkConstants.BASE_URL)
public class AppApi extends Api<App> {

    @AdminSecured
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response create(App app, @Context ContainerRequestContext requestContext) {
        return super.create(app,
                AppDao.getInstance(extractAdminId(requestContext)),
                AppNetworkConstants.BASE_URL,
                true, AppNetworkConstants.NAME, app.getName(), true, true);
    }

    @AdminSecured
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context ContainerRequestContext requestContext) {
        return super.getAll(AppDao.getInstance(extractAdminId(requestContext)));
    }

    @AdminSecured
    @Path(AppNetworkConstants.GET_APP)
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam(AppNetworkConstants.APP_ID) String appIdString,
                        @Context ContainerRequestContext requestContext) {
        return super.get(appIdString, AppDao.getInstance(extractAdminId(requestContext)));
    }

    @AdminSecured
    @Path(Constants.UPDATE_PROPERTIES)
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateProperties(App app, @Context ContainerRequestContext requestContext) {
        return super.updateProperties(app, AppDao.getInstance(extractAdminId(requestContext)));
    }

    @AdminSecured
    @Path(Constants.REGENERATE_TOKEN)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response regenerateToken(App app, @Context ContainerRequestContext requestContext) {
        return super.regenerateToken(app, AppDao.getInstance(extractAdminId(requestContext)));
    }

    @AdminSecured
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response delete(App app, @Context ContainerRequestContext requestContext) {
        return super.delete(app, AppDao.getInstance(extractAdminId(requestContext)));
    }
}
