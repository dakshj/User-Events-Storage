package daksh.userevents.storage.devices.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import daksh.userevents.storage.common.api.ObjectIdJsonSerializer;
import daksh.userevents.storage.common.model.Model;
import daksh.userevents.storage.devices.constants.DeviceNetworkConstants;
import daksh.userevents.storage.users.constants.UserNetworkConstants;

/**
 * Created by daksh on 22-May-16.
 */

@XmlRootElement
@Entity(noClassnameStored = true)
public class Device extends Model {

    @XmlElement(name = DeviceNetworkConstants.DEVICE_SYSTEM_ID)
    @Property(DeviceNetworkConstants.DEVICE_SYSTEM_ID)
    private String deviceSystemId;

    @XmlElement(name = UserNetworkConstants.USER_ID)
    @Property(UserNetworkConstants.USER_ID)
    @JsonSerialize(using = ObjectIdJsonSerializer.class)
    private ObjectId userId;

    @XmlElement(name = DeviceNetworkConstants.PUSH_MESSAGING_ID)
    @Property(DeviceNetworkConstants.PUSH_MESSAGING_ID)
    private String pushMessagingId;

    public String getDeviceSystemId() {
        return deviceSystemId;
    }

    public void setDeviceSystemId(String deviceSystemId) {
        this.deviceSystemId = deviceSystemId;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getPushMessagingId() {
        return pushMessagingId;
    }

    public void setPushMessagingId(String pushMessagingId) {
        this.pushMessagingId = pushMessagingId;
    }
}
