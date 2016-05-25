package daksh.userevents.storage.events.db;

import org.bson.types.ObjectId;

import daksh.userevents.storage.common.db.CollectionSpecificDao;
import daksh.userevents.storage.events.constants.EventDataConstants;
import daksh.userevents.storage.events.model.Event;

/**
 * Created by daksh on 22-May-16.
 */
public class EventDao extends CollectionSpecificDao<Event> {

    public static EventDao getInstance(ObjectId appId) {
        EventDao eventDao = (EventDao) getFromMap(appId);

        if (eventDao == null) {
            eventDao = new EventDao(appId);
            putIntoMap(appId, eventDao);
        }

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
