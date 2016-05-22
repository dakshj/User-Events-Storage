package daksh.userevents.storage.accounts.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import daksh.userevents.storage.accounts.constants.DataConstants;

/**
 * Created by daksh on 22-May-16.
 */

@XmlRootElement
@Entity(DataConstants.COLLECTION_NAME)
public class Account {

    @Id
    @XmlElement
    private ObjectId id;

    @XmlElement
    private String name;

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

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
