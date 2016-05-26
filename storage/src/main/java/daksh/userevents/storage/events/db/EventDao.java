package daksh.userevents.storage.events.db;

import org.bson.types.ObjectId;

import daksh.userevents.storage.common.db.Dao;
import daksh.userevents.storage.common.util.LruCache;
import daksh.userevents.storage.events.constants.EventDataConstants;
import daksh.userevents.storage.events.model.Event;

/**
 * Created by daksh on 22-May-16.
 */
public class EventDao extends Dao<Event> {

    private static LruCache<ObjectId, EventDao> cache;

    public static EventDao getInstance(ObjectId appId) {
        if (cache == null) {
            cache = new LruCache<>();
        }

        EventDao eventDao = cache.get(appId);

        if (eventDao != null) {
            return eventDao;
        }

        eventDao = new EventDao(appId);
        cache.put(appId, eventDao);
        return eventDao;
    }

    private EventDao(ObjectId appId) {
        super(appId, Event.class);
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
    }

    @Override
    public String getModelsPackage() {
        return EventDataConstants.MODELS_PACKAGE;
    }

    @Override
    public String getDbName() {
        return EventDataConstants.DB_NAME;
    }
}
