package daksh.userevents.storage.events.db;

import com.mongodb.MongoClient;

import org.bson.types.ObjectId;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Morphia;

import java.util.LinkedHashMap;
import java.util.Map;

import daksh.userevents.storage.common.constants.CommonDataConstants;
import daksh.userevents.storage.events.constants.EventDataConstants;
import daksh.userevents.storage.events.model.Event;

/**
 * Created by daksh on 22-May-16.
 */
public class EventDao {

    private static Map<ObjectId, EventDao> eventDaoMap;

    public static EventDao getInstance(ObjectId appId) {
        if (eventDaoMap == null) {
            eventDaoMap = new LinkedHashMap<ObjectId, EventDao>(
                    CommonDataConstants.LRU_MAX * 4 / 3, 0.75f, true
            ) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<ObjectId, EventDao> eldest) {
                    return size() > CommonDataConstants.LRU_MAX;
                }
            };
        }

        if (eventDaoMap.containsKey(appId)) {
            EventDao eventDao = eventDaoMap.get(appId);
            if (eventDao != null) {
                return eventDao;
            }
        }

        final EventDao eventDao = new EventDao(appId);
        eventDaoMap.put(appId, eventDao);

        return eventDao;
    }

    private final AdvancedDatastore datastore;
    private final ObjectId appId;

    private EventDao(ObjectId appId) {
        //TODO need to make this server independent

        this.appId = appId;
        final MongoClient mongoClient = new MongoClient("localhost");
        final Morphia morphia = new Morphia();
        morphia.mapPackage(EventDataConstants.MODELS_PACKAGE, true);

        datastore = (AdvancedDatastore) morphia.createDatastore(mongoClient, appId.toString());
        datastore.ensureIndexes();
    }

    public ObjectId createEvent(Event event) {
        return (ObjectId) datastore.save(appId.toString(), event).getId();
    }

    public Event getEvent(ObjectId eventId) {
        return datastore.get(appId.toString(), Event.class, eventId);
    }
}
