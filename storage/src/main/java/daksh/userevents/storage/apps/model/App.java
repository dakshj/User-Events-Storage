package daksh.userevents.storage.apps.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import daksh.userevents.storage.apps.constants.AppDataConstants;
import daksh.userevents.storage.common.api.ObjectIdJsonSerializer;

/**
 * Created by daksh on 23-May-16.
 */

@XmlRootElement
@Entity
public class App implements Serializable {

    @Id
    @XmlElement
    @JsonSerialize(using = ObjectIdJsonSerializer.class)
    private ObjectId id;

    @XmlElement
    private String name;

    @XmlElement(name = AppDataConstants.APP_TOKEN)
    @Property(AppDataConstants.APP_TOKEN)
    private String appToken;

    public App() {
    }

    public App(String name) {
        this.name = name;
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

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    @Override
    public String toString() {
        return "App{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", appToken='" + appToken + '\'' +
                '}';
    }
}
