package daksh.userevents.storage.common.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import daksh.userevents.storage.common.api.ObjectIdJsonSerializer;
import daksh.userevents.storage.common.constants.Constants;

/**
 * Created by daksh on 24-May-16.
 */
public class Model implements Serializable {

    public Model() {
    }

    @Id
    @XmlElement
    @JsonSerialize(using = ObjectIdJsonSerializer.class)
    private ObjectId id;

    @XmlElement
    private String name;

    @XmlElement(name = Constants.DATE_CREATED)
    @Property(Constants.DATE_CREATED)
    private Date dateCreated;

    @XmlElement(name = Constants.DATE_UPDATED)
    @Property(Constants.DATE_UPDATED)
    private Date dateUpdated;

    @XmlElement(name = Constants.PROPERTIES)
    @Property(Constants.PROPERTIES)
    private Map<String, Object> properties;

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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
