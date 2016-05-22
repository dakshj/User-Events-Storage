package daksh.userevents.storage.events.api;

import org.mongodb.morphia.Key;

import java.net.URI;

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

import daksh.userevents.storage.common.Secured;
import daksh.userevents.storage.events.constants.NetworkConstants;
import daksh.userevents.storage.events.db.DataSource;
import daksh.userevents.storage.events.model.Event;

import static daksh.userevents.storage.accounts.constants.NetworkConstants.ACCOUNT_ID;

/**
 * Created by daksh on 22-May-16.
 */

@Path(NetworkConstants.BASE_URL)
public class EventsApi {

    @Secured
    @Path(NetworkConstants.CREATE_EVENT)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createEvent(Event event,
                                @Context ContainerRequestContext requestContext) {
        if (event == null ||
                event.getName() == null || event.getName().isEmpty() ||
                event.getDefaultProperties() == null || event.getDefaultProperties().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Key<Event> key = DataSource
                .getInstance((String) requestContext.getProperty(ACCOUNT_ID))
                .createEvent(event);

        return Response.created(
                URI.create(NetworkConstants.BASE_URL + "/" + key.getId().toString())
        ).build();
    }

    @Secured
    @Path(NetworkConstants.GET_EVENT)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEvent(@PathParam(NetworkConstants.EVENT_ID) String eventId,
                             @Context ContainerRequestContext requestContext) {
        if (eventId == null || eventId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Event event = DataSource
                .getInstance((String) requestContext.getProperty(ACCOUNT_ID))
                .getEvent(eventId);

        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(event).build();
    }
}
