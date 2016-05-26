package daksh.userevents.storage.users.model;

import org.mongodb.morphia.annotations.Entity;

import javax.xml.bind.annotation.XmlRootElement;

import daksh.userevents.storage.common.model.Model;

/**
 * Created by daksh on 22-May-16.
 */

@XmlRootElement
@Entity(noClassnameStored = true)
public class User extends Model {
}
