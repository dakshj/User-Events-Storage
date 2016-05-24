package daksh.userevents.storage.apps.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import daksh.userevents.storage.apps.constants.AppDataConstants;
import daksh.userevents.storage.common.model.Model;

/**
 * Created by daksh on 23-May-16.
 */

@XmlRootElement
@Entity(noClassnameStored = true)
public class App extends Model {

    @XmlElement(name = AppDataConstants.APP_TOKEN)
    @Property(AppDataConstants.APP_TOKEN)
    private String appToken;

    public App() {
    }

    public App(String name) {
        setName(name);
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }
}
