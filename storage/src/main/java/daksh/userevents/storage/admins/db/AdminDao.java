package daksh.userevents.storage.admins.db;

import com.mongodb.WriteResult;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateResults;

import javax.ws.rs.NotAuthorizedException;

import daksh.userevents.storage.admins.constants.AdminDataConstants;
import daksh.userevents.storage.admins.constants.AdminNetworkConstants;
import daksh.userevents.storage.admins.model.Admin;
import daksh.userevents.storage.apps.db.AppDao;
import daksh.userevents.storage.apps.model.App;
import daksh.userevents.storage.common.db.Dao;
import daksh.userevents.storage.common.security.PasswordAuthentication;
import daksh.userevents.storage.common.util.Functions;
import daksh.userevents.storage.common.util.LruCache;

/**
 * Created by daksh on 22-May-16.
 */
public class AdminDao extends Dao<Admin> {

    private static AdminDao adminDao;

    public static AdminDao getInstance() {
        if (adminDao == null) {
            adminDao = new AdminDao();
        }

        return adminDao;
    }

    private AdminDao() {
        super(new ObjectId(AdminDataConstants.COLLECTION_NAME), Admin.class);
    }

    @Override
    public ObjectId create(Admin admin) {
        admin.setPassword(new PasswordAuthentication().hash(admin.getPassword().toCharArray()));
        return super.create(admin);
    }

    public ObjectId authenticate(Admin admin) {
        Query<Admin> query = getDatastore()
                .find(getParentId().toString(), Admin.class)
                .field(AdminNetworkConstants.USERNAME).equal(admin.getUsername())
                .limit(1);

        if (query.countAll() == 0) {
            return null;
        }

        Admin adminStored = query.iterator().next();

        if (adminStored.getUsername().equals(admin.getUsername()) &&
                new PasswordAuthentication()
                        .authenticate(admin.getPassword().toCharArray(),
                                adminStored.getPassword())) {
            return adminStored.getId();
        }

        return null;
    }

    @Override
    public WriteResult delete(ObjectId objectId) {
        WriteResult writeResult = super.delete(objectId);
        AppDao.getInstance(objectId).deleteAll();
        return writeResult;
    }

    public String regenerateAuthorizationToken(ObjectId adminId) {
        String token = Functions.getRandomString();
        updateField(adminId, AdminDataConstants.AUTHORIZATION_TOKEN, token);
        return token;
    }

    public String getAdminIdFromAuthorizationToken(String token) throws NotAuthorizedException {
        Query<Admin> query = getDatastore()
                .find(getParentId().toString(), Admin.class)
                .field(AdminDataConstants.AUTHORIZATION_TOKEN).equal(token).limit(1);

        if (query.countAll() == 0) {
            throw new NotAuthorizedException("Authorization Token is invalid");
        }

        return query.iterator().next().getId().toString();
    }

    private static LruCache<String, ObjectId> cache;

    public String getAppIdFromAuthorizationToken(String token) throws NotAuthorizedException {
        //TODO Need to reduce the complexity of this!

        if (cache == null) {
            cache = new LruCache<>();
        }

        if (cache.getMap().containsKey(token)) {
            ObjectId appId = cache.get(token);
            if (appId != null) {
                return appId.toString();
            }
        }

        //TODO replace the below with a Mongo Query!
        for (Key<Admin> adminKey :
                getDatastore().find(getParentId().toString(), Admin.class).asKeyList()) {
            for (App app : AppDao.getInstance((ObjectId) adminKey.getId()).getAll()) {
                if (app.getAppToken().equals(token)) {
                    final ObjectId appId = app.getId();
                    cache.put(token, appId);
                    return appId.toString();
                }
            }
        }

        throw new NotAuthorizedException("App Token is invalid");
    }

    public boolean usernameExists(String username) {
        return exists(AdminNetworkConstants.USERNAME, username);
    }

    public boolean logOutAdmin(ObjectId adminId) {
        UpdateResults updateResults = removeField(adminId, AdminDataConstants.AUTHORIZATION_TOKEN);
        return updateResults.getWriteResult().getN() > 0;
    }

    public void removeAppTokenFromCache(ObjectId appId) {
        if (cache != null && !cache.getMap().isEmpty() && cache.getMap().containsValue(appId)) {
            cache.removeByValue(appId);
        }
    }

    @Override
    public String getModelsPackage() {
        return AdminDataConstants.MODELS_PACKAGE;
    }

    @Override
    public String getDbName() {
        return AdminDataConstants.DB_NAME;
    }
}
