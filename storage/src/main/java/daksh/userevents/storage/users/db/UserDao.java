package daksh.userevents.storage.users.db;

import org.bson.types.ObjectId;

import daksh.userevents.storage.common.db.Dao;
import daksh.userevents.storage.common.util.LruCache;
import daksh.userevents.storage.users.constants.UserDataConstants;
import daksh.userevents.storage.users.model.User;

/**
 * Created by daksh on 22-May-16.
 */
public class UserDao extends Dao<User> {

    private static LruCache<ObjectId, UserDao> cache;

    public static UserDao getInstance(ObjectId appId) {
        if (cache == null) {
            cache = new LruCache<>();
        }

        UserDao userDao = cache.get(appId);

        if (userDao != null) {
            return userDao;
        }

        userDao = new UserDao(appId);
        cache.put(appId, userDao);
        return userDao;
    }

    private UserDao(ObjectId appId) {
        super(appId, User.class);
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
    }

    @Override
    public String getModelsPackage() {
        return UserDataConstants.MODELS_PACKAGE;
    }

    @Override
    public String getDbName() {
        return UserDataConstants.DB_NAME;
    }
}
