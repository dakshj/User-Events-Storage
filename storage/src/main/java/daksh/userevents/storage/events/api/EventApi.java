package daksh.userevents.storage.events.api;

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
import daksh.userevents.storage.events.constants.EventNetworkConstants;
import daksh.userevents.storage.events.db.EventDao;
import daksh.userevents.storage.events.model.Event;

/**
 * Created by daksh on 22-May-16.
 */

@Path(EventNetworkConstants.BASE_URL)
public class EventApi extends Api<Event> {

    @AppSecured
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response create(Event event, @Context ContainerRequestContext requestContext) {
        return super.create(event,
                EventDao.getInstance(extractAppId(requestContext)),
                EventNetworkConstants.BASE_URL, false);
    }

    @AppSecured
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context ContainerRequestContext requestContext) {
        return super.getAll(EventDao.getInstance(extractAppId(requestContext)));
    }

    @AppSecured
    @Path(EventNetworkConstants.GET)
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam(EventNetworkConstants.EVENT_ID) String eventIdString,
                        @Context ContainerRequestContext requestContext) {
        return super.get(eventIdString, EventDao.getInstance(extractAppId(requestContext)));
    }
}
