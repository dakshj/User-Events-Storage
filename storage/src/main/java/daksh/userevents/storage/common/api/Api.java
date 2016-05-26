package daksh.userevents.storage.common.api;

import com.mongodb.WriteResult;

import org.bson.types.ObjectId;

import java.net.URI;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import daksh.userevents.storage.admins.constants.AdminNetworkConstants;
import daksh.userevents.storage.apps.constants.AppNetworkConstants;
import daksh.userevents.storage.common.db.Dao;
import daksh.userevents.storage.common.model.Model;
import daksh.userevents.storage.common.util.TextUtils;

/**
 * Created by daksh on 26-May-16.
 */
public abstract class Api<T extends Model> {

    protected Response create(T object, Dao<T> dao, String baseUrl, boolean entityRequired) {
        return create(object, dao, baseUrl, false, null, null, false, entityRequired);
    }

    protected Response create(T object, Dao<T> dao, String baseUrl,
                              boolean validateField, String field, String value,
                              boolean regenerateToken, boolean entityRequired) {
        if (object == null || TextUtils.isEmpty(object.getName())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (validateField) {
            if (dao.exists(field, value)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(value + " is already in use").build();
            }
        }

        ObjectId objectId = dao.create(object);

        if (objectId == null) {
            return Response.serverError().entity("Failed to create").build();
        }

        ResponseBuilder responseBuilder = Response.created(URI.create(baseUrl + "/" + objectId));

        if (regenerateToken) {
            String token = dao.regenerateToken(objectId);

            if (TextUtils.isEmpty(token)) {
                return Response.serverError().entity("Failed to generate token").build();
            }

            responseBuilder.entity(token);
        } else if (entityRequired) {
            responseBuilder.entity(objectId);
        }

        return responseBuilder.build();
    }

    protected Response getAll(Dao<T> dao) {
        List<T> allObjects = dao.getAll();

        if (allObjects == null) {
            return Response.serverError().entity("Failed to fetch").build();
        }

        return Response.ok(allObjects).build();
    }

    protected Response get(String objectIdString, Dao<T> dao) {
        if (TextUtils.isEmpty(objectIdString) || !ObjectId.isValid(objectIdString)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        T object = dao.get(new ObjectId(objectIdString));

        if (object == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(object).build();
    }

    protected Response updateField(ObjectId objectId, Dao<T> dao, String field, String value) {
        if (objectId == null || TextUtils.isEmpty(value)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        boolean success = dao.updateField(objectId, field, value);

        if (!success) {
            return Response.serverError().entity("Failed to update").build();
        }

        return Response.ok().build();
    }

    protected Response updateProperties(T object, Dao<T> dao) {
        if (object == null || object.getId() == null ||
                object.getProperties() == null || object.getProperties().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        boolean success = dao.updateProperties(object.getId(), object.getProperties());

        if (!success) {
            return Response.serverError().entity("Failed to update properties").build();
        }

        return Response.ok().build();
    }

    protected Response regenerateToken(T object, Dao<T> dao) {
        if (object == null || object.getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String token = dao.regenerateToken(object.getId());

        if (TextUtils.isEmpty(token)) {
            return Response.serverError().entity("Failed to generate token").build();
        }

        return Response.ok(token).build();
    }

    protected Response delete(T object, Dao<T> dao) {
        if (object == null || object.getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        WriteResult writeResult = dao.delete(object.getId());

        if (writeResult.getN() == 0) {
            return Response.status(Response.Status.NOT_FOUND).entity("Failed to delete").build();
        }

        return Response.noContent().build();
    }

    protected ObjectId extractAppId(ContainerRequestContext requestContext) {
        return new ObjectId((String) requestContext.getProperty(AppNetworkConstants.APP_ID));
    }

    protected static ObjectId extractAdminId(ContainerRequestContext requestContext) {
        return new ObjectId((String) requestContext.getProperty(AdminNetworkConstants.ADMIN_ID));
    }
}
