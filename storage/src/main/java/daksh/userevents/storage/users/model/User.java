package daksh.userevents.storage.users.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import daksh.userevents.storage.apps.constants.AppNetworkConstants;
import daksh.userevents.storage.common.api.ObjectIdJsonSerializer;
import daksh.userevents.storage.users.constants.UserDataConstants;

/**
 * Created by daksh on 22-May-16.
 */

@XmlRootElement
@Entity(UserDataConstants.COLLECTION_NAME)
public class User implements Serializable {

    @Id
    @XmlElement
    @JsonSerialize(using = ObjectIdJsonSerializer.class)
    private ObjectId id;

    @XmlElement
    private String name;

    @XmlElement(name = AppNetworkConstants.APP_ID)
    @Property(AppNetworkConstants.APP_ID)
    @JsonSerialize(using = ObjectIdJsonSerializer.class)
    private ObjectId appId;

    @XmlElement(name = UserDataConstants.DEFAULT_PROPERTIES)
    @Property(UserDataConstants.DEFAULT_PROPERTIES)
    private Map<String, String> defaultProperties;

    @XmlElement(name = UserDataConstants.USER_PROPERTIES)
    @Property(UserDataConstants.USER_PROPERTIES)
    private Map<String, String> userProperties;

    public User() {
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

    public ObjectId getAppId() {
        return appId;
    }

    public void setAppId(ObjectId appId) {
        this.appId = appId;
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
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", appId=" + appId +
                ", defaultProperties=" + defaultProperties +
                ", userProperties=" + userProperties +
                '}';
    }
}
