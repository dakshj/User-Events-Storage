package daksh.userevents.storage.admins.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import daksh.userevents.storage.admins.constants.AdminDataConstants;
import daksh.userevents.storage.common.api.ObjectIdJsonSerializer;

/**
 * Created by daksh on 23-May-16.
 */

@XmlRootElement
@Entity(AdminDataConstants.COLLECTION_NAME)
public class Admin implements Serializable{

    @Id
    @XmlElement
    @JsonSerialize(using = ObjectIdJsonSerializer.class)
    private ObjectId id;

    @XmlElement
    private String username;

    @XmlElement(name = AdminDataConstants.PASSWORD_HASHED)
    @Property(AdminDataConstants.PASSWORD_HASHED)
    private String passwordHashed;

    @XmlElement
    private String name;

    @XmlElement(name = AdminDataConstants.AUTHORIZATION_TOKEN)
    @Property(AdminDataConstants.AUTHORIZATION_TOKEN)
    private String authorizationToken;

    public Admin() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHashed() {
        return passwordHashed;
    }

    public void setPasswordHashed(String passwordHashed) {
        this.passwordHashed = passwordHashed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", passwordHashed='" + passwordHashed + '\'' +
                ", name='" + name + '\'' +
                ", authorizationToken='" + authorizationToken + '\'' +
                '}';
    }
}
