package daksh.userevents.storage.devices.api;

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
import daksh.userevents.storage.devices.constants.DeviceNetworkConstants;
import daksh.userevents.storage.devices.db.DeviceDao;
import daksh.userevents.storage.devices.model.Device;

/**
 * Created by daksh on 22-May-16.
 */

@Path(DeviceNetworkConstants.BASE_URL)
public class DeviceApi extends Api<Device> {

    @AppSecured
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response create(Device device, @Context ContainerRequestContext requestContext) {
        //TODO don't create the device again if the deviceSystemId is found in the app's devices collection

        return super.create(device,
                DeviceDao.getInstance(extractAppId(requestContext)),
                DeviceNetworkConstants.BASE_URL, true);
    }

    @AppSecured
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context ContainerRequestContext requestContext) {
        return super.getAll(DeviceDao.getInstance(extractAppId(requestContext)));
    }

    @AppSecured
    @Path(DeviceNetworkConstants.GET)
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam(DeviceNetworkConstants.DEVICE_ID) String deviceIdString,
                        @Context ContainerRequestContext requestContext) {
        return super.get(deviceIdString, DeviceDao.getInstance(extractAppId(requestContext)));
    }
}
