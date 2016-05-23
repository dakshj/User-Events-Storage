package daksh.userevents.storage.common.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.bson.types.ObjectId;

import java.io.IOException;

/**
 * Created by daksh on 23-May-16.
 */
public class ObjectIdJsonSerializer extends JsonSerializer<ObjectId> {
    @Override
    public void serialize(ObjectId o, JsonGenerator j, SerializerProvider s) throws IOException {
        if (o == null) {
            j.writeNull();
        } else {
            j.writeString(o.toString());
        }
    }
}
