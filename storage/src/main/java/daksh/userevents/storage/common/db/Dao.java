package daksh.userevents.storage.common.db;

import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

import org.bson.types.ObjectId;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.util.List;

/**
 * Created by daksh on 25-May-16.
 */
public abstract class Dao<T> {

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

    protected UpdateResults removeField(ObjectId objectId, String field) {
        Query<T> query = datastore.createQuery(parentId.toString(), clazz)
                .field(Mapper.ID_KEY).equal(objectId);

        UpdateOperations<T> ops = datastore.createUpdateOperations(clazz).unset(field);

        return datastore.update(query, ops);
    }

    protected WriteResult delete(ObjectId objectId) {
        return datastore.delete(parentId.toString(), clazz, objectId);
    }

    protected void deleteAll() {
        datastore.getMongo().getDatabase(getDbName())
                .getCollection(parentId.toString()).drop();
    }

    protected AdvancedDatastore getDatastore() {
        return datastore;
    }

    protected ObjectId getParentId() {
        return parentId;
    }

    protected abstract String getModelsPackage();

    protected abstract String getDbName();
}
