package daksh.userevents.storage.common.db;

import com.sun.istack.internal.Nullable;

import org.bson.types.ObjectId;

import daksh.userevents.storage.common.util.LruCache;

/**
 * Created by daksh on 24-May-16.
 */
public abstract class CollectionSpecificDao<T> extends Dao<T> {

    private static LruCache<ObjectId, CollectionSpecificDao> cache;

    public CollectionSpecificDao(ObjectId parentId, Class<T> clazz) {
        super(parentId, clazz);
    }

    @Nullable
    protected static CollectionSpecificDao getFromMap(ObjectId adminId) {
        if (cache == null) {
            cache = new LruCache<>();
        }

        return cache.get(adminId);
    }

    protected static void putIntoMap(ObjectId objectId, CollectionSpecificDao collectionSpecificDao) {
        if (cache == null) {
            cache = new LruCache<>();
        }

        cache.put(objectId, collectionSpecificDao);
    }
}
