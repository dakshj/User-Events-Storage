package daksh.userevents.storage.apps.db;

import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import daksh.userevents.storage.apps.constants.AppDataConstants;
import daksh.userevents.storage.apps.constants.AppNetworkConstants;
import daksh.userevents.storage.apps.model.App;
import daksh.userevents.storage.common.constants.CommonDataConstants;

/**
 * Created by daksh on 23-May-16.
 */
public class AppDao {

    private static Map<ObjectId, AppDao> appDaoMap;

    public static AppDao getInstance(ObjectId adminId) {
        if (appDaoMap == null) {
            appDaoMap = new LinkedHashMap<ObjectId, AppDao>(
                    CommonDataConstants.LRU_MAX * 4 / 3, 0.75f, true
            ) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<ObjectId, AppDao> eldest) {
                    return size() > CommonDataConstants.LRU_MAX;
                }
            };
        }

        if (appDaoMap.containsKey(adminId)) {
            AppDao appDao = appDaoMap.get(adminId);
            if (appDao != null) {
                return appDao;
            }
        }

        final AppDao appDao = new AppDao(adminId);
        appDaoMap.put(adminId, appDao);

        return appDao;
    }

    private final Datastore datastore;
    private final ObjectId adminId;

    private AppDao(ObjectId adminId) {
        //TODO need to make this server independent

        this.adminId = adminId;
        final MongoClient mongoClient = new MongoClient("localhost");
        final Morphia morphia = new Morphia();
        morphia.mapPackage(AppDataConstants.MODELS_PACKAGE, true);

        datastore = morphia.createDatastore(mongoClient, AppDataConstants.DB_NAME);
        datastore.ensureIndexes();
    }

    public ObjectId getAdminId() {
        return adminId;
    }

    public ObjectId createApp(String appName) {
        App app = new App();
        app.setName(appName);
        app.setAdminId(adminId);

        return (ObjectId) datastore.save(app).getId();
    }

    public boolean appNameExists(String appname) {
        return datastore.find(App.class, AppNetworkConstants.APP_NAME, appname)
                .limit(1).countAll() > 0;
    }

    public App getApp(ObjectId appId) {
        return datastore.get(App.class, appId);
    }

    public List<App> getAllApps() {
        return datastore.find(App.class).asList();
    }

    private UpdateResults updateField(ObjectId appId, String field, String value) {
        Query<App> query = datastore.createQuery(App.class)
                .field(Mapper.ID_KEY).equal(appId);

        UpdateOperations<App> ops = datastore.createUpdateOperations(App.class)
                .set(field, value);

        return datastore.update(query, ops);
    }

    public String regenerateAppToken(ObjectId appId) {
        Random random = new SecureRandom();
        String token = new BigInteger(130, random).toString(32);
        updateField(appId, AppDataConstants.APP_TOKEN, token);
        return token;
    }

    public WriteResult deleteApp(ObjectId appId) {
        WriteResult writeResult = datastore.delete(App.class, appId);

        //TODO delete all events and users beloing to those deleted apps

        return writeResult;
    }
}
