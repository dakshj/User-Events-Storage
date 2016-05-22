package daksh.userevents.storage.events.db;

import com.mongodb.MongoClient;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;

import daksh.userevents.storage.events.model.Event;

/**
 * Created by daksh on 22-May-16.
 */
public class DataSource {

    private static DataSource dataSource;

    public static DataSource getInstance(String accountId) {
        if (dataSource == null) {
            dataSource = new DataSource(accountId);
        }

        return dataSource;
    }

    private final Datastore datastore;

    private DataSource(String accountId) {
        MongoClient mongoClient = new MongoClient("localhost");
        Morphia morphia = new Morphia();
        morphia.mapPackage("daksh.userevents.storage.events.model", true);

        datastore = morphia.createDatastore(mongoClient, accountId);
        datastore.ensureIndexes();
    }

    public Key<Event> createEvent(Event event) {
        return datastore.save(event);
    }

    public Event getEvent(String eventId) {
        return datastore.get(Event.class, new ObjectId(eventId));
    }
}
