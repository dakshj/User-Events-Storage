package daksh.userevents.storage.common.db;

import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.sun.istack.internal.Nullable;

import org.bson.types.ObjectId;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.util.List;

import daksh.userevents.storage.common.util.LruCache;

/**
 * Created by daksh on 24-May-16.
 */
public abstract class Dao<T> {

    private static LruCache<ObjectId, Dao> cache;

    @Nullable
    protected static Dao getFromMap(ObjectId adminId) {
        if (cache == null) {
            cache = new LruCache<>();
        }

        return cache.get(adminId);
    }

    protected static void putIntoMap(ObjectId objectId, Dao dao) {
        if (cache == null) {
            cache = new LruCache<>();
        }

        cache.put(objectId, dao);
    }

    private final AdvancedDatastore datastore;
    private final ObjectId parentId;
    private final Class<T> clazz;

    protected Dao(ObjectId parentId, Class<T> clazz) {
        //TODO need to make this server independent

        this.parentId = parentId;
        this.clazz = clazz;
        final MongoClient mongoClient = new MongoClient("localhost");
        final Morphia morphia = new Morphia();
        morphia.mapPackage(getModelsPackage(), true);

        datastore = (AdvancedDatastore) morphia.createDatastore(mongoClient, getDbName());
        datastore.ensureIndexes();
    }

    public boolean exists(String field, String value) {
        return datastore.createQuery(parentId.toString(), clazz)
                .field(field).equal(value)
                .limit(1).countAll() > 0;
    }

    public ObjectId create(T model) {
        return (ObjectId) datastore.save(parentId.toString(), model).getId();
    }

    public T get(ObjectId objectId) {
        return datastore.get(parentId.toString(), clazz, objectId);
    }

    public List<T> getAll() {
        return datastore.find(parentId.toString(), clazz).asList();
    }

    protected List<Key<T>> getAllKeys() {
        return datastore.find(parentId.toString(), clazz).asKeyList();
    }

    protected UpdateResults updateField(ObjectId objectId, String field, String value) {
        Query<T> query = datastore.createQuery(parentId.toString(), clazz)
                .field(Mapper.ID_KEY).equal(objectId);

        UpdateOperations<T> ops = datastore.createUpdateOperations(clazz).set(field, value);

        return datastore.update(query, ops);
    }

    public WriteResult delete(ObjectId objectId) {
        WriteResult writeResult = datastore.delete(parentId.toString(), clazz, objectId);

        return writeResult;
    }

    public void deleteAll() {
        datastore.getMongo().getDatabase(getDbName())
                .getCollection(parentId.toString()).drop();
    }

    protected AdvancedDatastore getDatastore() {
        return datastore;
    }

    protected ObjectId getParentId() {
        return parentId;
    }

    public abstract String getModelsPackage();

    public abstract String getDbName();
}
