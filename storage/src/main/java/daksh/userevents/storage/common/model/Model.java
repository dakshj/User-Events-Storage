package daksh.userevents.storage.common.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

import daksh.userevents.storage.common.api.ObjectIdJsonSerializer;

/**
 * Created by daksh on 24-May-16.
 */
public class Model implements Serializable {

    @Id
    @XmlElement
    @JsonSerialize(using = ObjectIdJsonSerializer.class)
    private ObjectId id;

    @XmlElement
    private String name;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
