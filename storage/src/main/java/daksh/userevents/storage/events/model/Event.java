package daksh.userevents.storage.events.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import daksh.userevents.storage.common.api.ObjectIdJsonSerializer;
import daksh.userevents.storage.events.constants.EventDataConstants;
import daksh.userevents.storage.users.constants.UserNetworkConstants;

/**
 * Created by daksh on 22-May-16.
 */

@XmlRootElement
@Entity
public class Event implements Serializable {

    @Id
    @XmlElement
    @JsonSerialize(using = ObjectIdJsonSerializer.class)
    private ObjectId id;

    @XmlElement
    private String name;

    @XmlElement(name = UserNetworkConstants.USER_ID)
    @Property(UserNetworkConstants.USER_ID)
    @JsonSerialize(using = ObjectIdJsonSerializer.class)
    private ObjectId userId;

    @XmlElement(name = EventDataConstants.DEFAULT_PROPERTIES)
    @Property(EventDataConstants.DEFAULT_PROPERTIES)
    private Map<String, String> defaultProperties;

    @XmlElement(name = EventDataConstants.USER_PROPERTIES)
    @Property(EventDataConstants.USER_PROPERTIES)
    private Map<String, String> userProperties;

    public Event() {
    }

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

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public Map<String, String> getDefaultProperties() {
        return defaultProperties;
    }

    public void setDefaultProperties(Map<String, String> defaultProperties) {
        this.defaultProperties = defaultProperties;
    }

    public Map<String, String> getUserProperties() {
        return userProperties;
    }

    public void setUserProperties(Map<String, String> userProperties) {
        this.userProperties = userProperties;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userId=" + userId +
                ", defaultProperties=" + defaultProperties +
                ", userProperties=" + userProperties +
                '}';
    }
}
