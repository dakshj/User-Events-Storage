package daksh.userevents.storage.common.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Created by daksh on 27-May-16.
 */

@Provider
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

    private final ObjectMapper MAPPER;

    public ObjectMapperContextResolver() {
        MAPPER = new ObjectMapper();
        MAPPER.findAndRegisterModules();
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return MAPPER;
    }
}
