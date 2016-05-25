package daksh.userevents.storage.admins.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import daksh.userevents.storage.admins.constants.AdminDataConstants;
import daksh.userevents.storage.common.model.Model;

/**
 * Created by daksh on 23-May-16.
 */

@XmlRootElement
@Entity(value = AdminDataConstants.COLLECTION_NAME, noClassnameStored = true)
public class Admin extends Model {

    @XmlElement
    private String username;

    private String password;

    @XmlElement(name = AdminDataConstants.AUTHORIZATION_TOKEN)
    @Property(AdminDataConstants.AUTHORIZATION_TOKEN)
    private String authorizationToken;

    public Admin() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }
}
