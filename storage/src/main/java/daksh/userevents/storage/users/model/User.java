package daksh.userevents.storage.users.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import daksh.userevents.storage.common.model.Model;
import daksh.userevents.storage.users.constants.UserDataConstants;

/**
 * Created by daksh on 22-May-16.
 */

@XmlRootElement
@Entity(noClassnameStored = true)
public class User extends Model {

    @XmlElement(name = UserDataConstants.DEFAULT_PROPERTIES)
    @Property(UserDataConstants.DEFAULT_PROPERTIES)
    private Map<String, String> defaultProperties;

    @XmlElement(name = UserDataConstants.USER_PROPERTIES)
    @Property(UserDataConstants.USER_PROPERTIES)
    private Map<String, String> userProperties;

    public User() {
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
}
