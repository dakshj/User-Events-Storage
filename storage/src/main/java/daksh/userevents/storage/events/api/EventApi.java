package daksh.userevents.storage.events.api;

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
import daksh.userevents.storage.events.constants.EventNetworkConstants;
import daksh.userevents.storage.events.db.EventDao;
import daksh.userevents.storage.events.model.Event;

/**
 * Created by daksh on 22-May-16.
 */

@Path(EventNetworkConstants.BASE_URL)
public class EventApi {

    @AppSecured
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createEvent(Event event, @Context ContainerRequestContext requestContext) {
        if (event == null ||
                event.getName() == null || event.getName().isEmpty() ||
                event.getDefaultProperties() == null || event.getDefaultProperties().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ObjectId appId = extractAppId(requestContext);

        ObjectId eventId = EventDao.getInstance(appId).create(event);

        return Response.created(
                URI.create(EventNetworkConstants.BASE_URL + "/" + eventId)
        ).build();
    }

    @AppSecured
    @Path(EventNetworkConstants.GET_EVENT)
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEvent(@PathParam(EventNetworkConstants.EVENT_ID) String eventId,
                             @Context ContainerRequestContext requestContext) {
        if (eventId == null || eventId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ObjectId appId = extractAppId(requestContext);

        Event event = EventDao.getInstance(appId).get(new ObjectId(eventId));

        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(event).build();
    }

    @AppSecured
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEvents(@Context ContainerRequestContext requestContext) {
        ObjectId appId = extractAppId(requestContext);

        List<Event> allEvents = EventDao.getInstance(appId).getAll();

        if (allEvents == null) {
            return Response.serverError().entity("Failed to fetch Apps").build();
        }

        return Response.ok(allEvents).build();
    }

    private static ObjectId extractAppId(ContainerRequestContext requestContext) {
        return new ObjectId((String) requestContext.getProperty(AppNetworkConstants.APP_ID));
    }
}
