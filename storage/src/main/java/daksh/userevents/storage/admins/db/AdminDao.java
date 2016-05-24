package daksh.userevents.storage.admins.db;

import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.NotAuthorizedException;

import daksh.userevents.storage.admins.constants.AdminDataConstants;
import daksh.userevents.storage.admins.constants.AdminNetworkConstants;
import daksh.userevents.storage.admins.model.Admin;
import daksh.userevents.storage.apps.db.AppDao;
import daksh.userevents.storage.apps.model.App;
import daksh.userevents.storage.common.constants.DataConstants;
import daksh.userevents.storage.common.security.PasswordAuthentication;

/**
 * Created by daksh on 22-May-16.
 */
public class AdminDao {

    private static AdminDao adminDao;

    public static AdminDao getInstance() {
        if (adminDao == null) {
            adminDao = new AdminDao();
        }

        return adminDao;
    }

    private final Datastore datastore;

    private AdminDao() {
        //TODO need to make this server independent
        final MongoClient mongoClient = new MongoClient("localhost");
        final Morphia morphia = new Morphia();
        morphia.mapPackage(AdminDataConstants.MODELS_PACKAGE, true);

        datastore = morphia.createDatastore(mongoClient, AdminDataConstants.DB_NAME);
        datastore.ensureIndexes();
    }

    public ObjectId createAdmin(String username, String password, String name) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPasswordHashed(new PasswordAuthentication().hash(password.toCharArray()));
        admin.setName(name);
        return (ObjectId) datastore.save(admin).getId();
    }

    public ObjectId authenticateAdmin(String username, String password) {
        Query<Admin> query = datastore.find(Admin.class, AdminNetworkConstants.USERNAME, username)
                .limit(1);

        if (query.countAll() == 0) {
            return null;
        }

        Admin admin = query.iterator().next();

        if (admin.getUsername().equals(username) &&
                new PasswordAuthentication()
                        .authenticate(password.toCharArray(), admin.getPasswordHashed())) {
            return admin.getId();
        }

        return null;
    }

    private UpdateResults updateField(ObjectId adminId, String field, String value) {
        Query<Admin> query = datastore.createQuery(Admin.class)
                .field(Mapper.ID_KEY).equal(adminId);

        UpdateOperations<Admin> ops = datastore.createUpdateOperations(Admin.class)
                .set(field, value);

        return datastore.update(query, ops);
    }

    private UpdateResults removeField(ObjectId adminId, String field) {
        Query<Admin> query = datastore.createQuery(Admin.class)
                .field(Mapper.ID_KEY).equal(adminId);

        UpdateOperations<Admin> ops = datastore.createUpdateOperations(Admin.class)
                .unset(field);

        return datastore.update(query, ops);
    }

    public WriteResult deleteAdmin(ObjectId adminId) {
        WriteResult writeResult = datastore.delete(Admin.class, adminId);

        AppDao.getInstance(adminId).deleteAll();

        return writeResult;
    }

    public String regenerateAuthorizationToken(ObjectId adminId) {
        Random random = new SecureRandom();
        String token = new BigInteger(130, random).toString(32);
        updateField(adminId, AdminDataConstants.AUTHORIZATION_TOKEN, token);
        return token;
    }

    public String getAdminIdFromAuthorizationToken(String token) throws NotAuthorizedException {
        Query<Admin> query = datastore
                .find(Admin.class, AdminDataConstants.AUTHORIZATION_TOKEN, token).limit(1);

        if (query.countAll() == 0) {
            throw new NotAuthorizedException("Authorization Token is invalid");
        }

        return query.iterator().next().getId().toString();
    }

    private static Map<String, ObjectId> appTokenMap;

    public ObjectId getAppIdFromAuthorizationToken(String token) throws NotAuthorizedException {
        //TODO Need to reduce the complexity of this!

        if (appTokenMap == null) {
            appTokenMap = new LinkedHashMap<String, ObjectId>(
                    DataConstants.LRU_MAX * 4 / 3, 0.75f, true
            ) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, ObjectId> eldest) {
                    return size() > DataConstants.LRU_MAX;
                }
            };
        }

        if (appTokenMap.containsKey(token)) {
            ObjectId appId = appTokenMap.get(token);
            if (appId != null) {
                return appId;
            }
        }

        //TODO replace the below with a Mongo Query!
        for (Key<Admin> adminKey : datastore.find(Admin.class).asKeyList()) {
            for (App app : AppDao.getInstance((ObjectId) adminKey.getId()).getAll()) {
                if (app.getAppToken().equals(token)) {
                    final ObjectId appId = app.getId();
                    appTokenMap.put(token, appId);
                    return appId;
                }
            }
        }

        throw new NotAuthorizedException("App Token is invalid");
    }

    public boolean usernameExists(String username) {
        return datastore.find(Admin.class, AdminNetworkConstants.USERNAME, username)
                .limit(1).countAll() > 0;
    }

    public boolean logOutAdmin(ObjectId adminId) {
        UpdateResults updateResults = removeField(adminId, AdminDataConstants.AUTHORIZATION_TOKEN);
        return updateResults.getWriteResult().getN() > 0;
    }
}
