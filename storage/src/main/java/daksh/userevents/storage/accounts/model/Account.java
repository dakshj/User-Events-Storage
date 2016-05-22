package daksh.userevents.storage.accounts.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import daksh.userevents.storage.accounts.constants.DataConstants;
import daksh.userevents.storage.common.ObjectIdJsonSerializer;

/**
 * Created by daksh on 22-May-16.
 */

@XmlRootElement
@Entity(DataConstants.COLLECTION_NAME)
public class Account {

    @Id
    @XmlElement
    @JsonSerialize(using = ObjectIdJsonSerializer.class)
    private ObjectId id;

    @XmlElement
    private String name;

    @XmlElement(name = DataConstants.AUTHORIZATION_TOKEN)
    @Property(DataConstants.AUTHORIZATION_TOKEN)
    private String authorizationToken;

    public Account() {
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

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", authorizationToken='" + authorizationToken + '\'' +
                '}';
    }
}
