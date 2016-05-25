package daksh.userevents.storage.apps.db;

import com.mongodb.WriteResult;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Key;

import java.util.List;

import daksh.userevents.storage.admins.db.AdminDao;
import daksh.userevents.storage.apps.constants.AppDataConstants;
import daksh.userevents.storage.apps.model.App;
import daksh.userevents.storage.common.db.CollectionSpecificDao;
import daksh.userevents.storage.common.util.Functions;
import daksh.userevents.storage.events.db.EventDao;

/**
 * Created by daksh on 23-May-16.
 */
public class AppDao extends CollectionSpecificDao<App> {

    public static AppDao getInstance(ObjectId adminId) {
        AppDao appDa = (AppDao) getFromMap(adminId);

        if (appDa == null) {
            appDa = new AppDao(adminId);
            putIntoMap(adminId, appDa);
        }

        return appDa;
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

    public String regenerateAppToken(ObjectId appId) {
        AdminDao.getInstance().removeAppTokenFromCache(appId);

        final String token = Functions.getRandomString();
        updateField(appId, AppDataConstants.APP_TOKEN, token);
        return token;
    }

    @Override
    public WriteResult delete(ObjectId objectId) {
        WriteResult writeResult = super.delete(objectId);
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
        //TODO delete all users for this app as well
        //UserDao.getInstance(appId).deleteAll();
    }
}
