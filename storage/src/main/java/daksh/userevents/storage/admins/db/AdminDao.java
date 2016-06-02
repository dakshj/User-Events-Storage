package daksh.userevents.storage.admins.db;

import com.mongodb.WriteResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateResults;

import javax.ws.rs.NotAuthorizedException;

import daksh.userevents.storage.admins.constants.AdminDataConstants;
import daksh.userevents.storage.admins.constants.AdminNetworkConstants;
import daksh.userevents.storage.admins.model.Admin;
import daksh.userevents.storage.apps.constants.AppDataConstants;
import daksh.userevents.storage.apps.db.AppDao;
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

    public ObjectId authenticate(String username, String password) {
        Query<Admin> query = getDatastore()
                .find(getParentId().toString(), Admin.class)
                .field(AdminNetworkConstants.USERNAME).equal(username)
                .limit(1);

        if (query.countAll() == 0) {
            return null;
        }

        Admin adminStored = query.iterator().next();

        if (adminStored.getUsername().equals(username) &&
                new PasswordAuthentication()
                        .authenticate(password.toCharArray(),
                                adminStored.getPassword())) {
            return adminStored.getId();
        }

        return null;
    }

    @Override
    public WriteResult delete(ObjectId objectId) {
        WriteResult writeResult = super.deleteActually(objectId);
        AppDao.getInstance(objectId).deleteAll();
        return writeResult;
    }

    @Override
    public String regenerateToken(ObjectId adminId) {
        String token = Functions.getRandomString();
        updateField(adminId, AdminDataConstants.AUTHORIZATION_TOKEN, token);
        return token;
    }

    public String getAdminIdFromAdminToken(String token) throws NotAuthorizedException {
        Query<Admin> query = getDatastore()
                .find(getParentId().toString(), Admin.class)
                .field(AdminDataConstants.AUTHORIZATION_TOKEN).equal(token).limit(1);

        if (query.countAll() == 0) {
            throw new NotAuthorizedException("Authorization Token is invalid");
        }

        return query.iterator().next().getId().toString();
    }

    private static LruCache<String, ObjectId> cache;

    public String getAppIdFromAppToken(String token) throws NotAuthorizedException {
        //TODO Need to reduce the complexity of this!
        //Right now what happens is that ALL Apps are read into memory and then checked which App ID
        //this App Token is matched with

        if (cache == null) {
            cache = new LruCache<>();
            System.out.println("App Token Cache is null");
        } else if (cache.getMap().containsKey(token)) {
            System.out.println("App Token found in Cache");
            ObjectId appId = cache.get(token);
            if (appId != null) {
                System.out.println("App I.D. = " + appId);
                return appId.toString();
            }
        }

        MongoDatabase database = getDatastore().getMongo().getDatabase(AppDataConstants.DB_NAME);

        MongoCollection<Document> collection;
        FindIterable<Document> documents;
        ObjectId appId;
        for (String collectionName : database.listCollectionNames()) {
            collection = database.getCollection(collectionName);
            documents = collection.find(new Document(AppDataConstants.APP_TOKEN, token));
            for (Document document : documents) {
                System.out.println("App Token found in DB");
                if (document.containsKey("_id")) {
                    appId = document.getObjectId("_id");
                    System.out.println("App I.D. = " + appId);
                    cache.put(token, appId);
                    return appId.toString();
                }
            }
        }

        throw new NotAuthorizedException("App Token is invalid");
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
