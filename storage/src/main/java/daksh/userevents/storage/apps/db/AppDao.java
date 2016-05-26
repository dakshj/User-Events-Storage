package daksh.userevents.storage.apps.db;

import com.mongodb.WriteResult;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Key;

import java.util.List;

import daksh.userevents.storage.admins.db.AdminDao;
import daksh.userevents.storage.apps.constants.AppDataConstants;
import daksh.userevents.storage.apps.model.App;
import daksh.userevents.storage.common.db.Dao;
import daksh.userevents.storage.common.util.Functions;
import daksh.userevents.storage.common.util.LruCache;
import daksh.userevents.storage.events.db.EventDao;
import daksh.userevents.storage.users.db.UserDao;

/**
 * Created by daksh on 23-May-16.
 */
public class AppDao extends Dao<App> {

    private static LruCache<ObjectId, AppDao> cache;

    public static AppDao getInstance(ObjectId adminId) {
        if (cache == null) {
            cache = new LruCache<>();
        }

        AppDao appDao = cache.get(adminId);

        if (appDao != null) {
            return appDao;
        }

        appDao = new AppDao(adminId);
        cache.put(adminId, appDao);
        return appDao;
    }

    private AppDao(ObjectId adminId) {
        super(adminId, App.class);
    }

    @Override
    public String getModelsPackage() {
        return AppDataConstants.MODELS_PACKAGE;
    }

    @Override
    public String getDbName() {
        return AppDataConstants.DB_NAME;
    }

    @Override
    public String regenerateToken(ObjectId appId) {
        AdminDao.getInstance().removeAppTokenFromCache(appId);

        final String token = Functions.getRandomString();
        updateField(appId, AppDataConstants.APP_TOKEN, token);
        return token;
    }

    @Override
    public WriteResult delete(ObjectId objectId) {
        WriteResult writeResult = super.deleteActually(objectId);
        deleteAllEventsUsers(objectId);
        return writeResult;
    }

    @Override
    public void deleteAll() {
        List<Key<App>> keys = getAllKeys();

        super.deleteAll();

        for (Key<App> key : keys) {
            deleteAllEventsUsers((ObjectId) key.getId());
        }
    }

    private void deleteAllEventsUsers(ObjectId appId) {
        EventDao.getInstance(appId).deleteAll();
        UserDao.getInstance(appId).deleteAll();
    }
}
