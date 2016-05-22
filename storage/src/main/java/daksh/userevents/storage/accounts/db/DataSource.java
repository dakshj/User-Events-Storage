package daksh.userevents.storage.accounts.db;

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
import java.util.List;
import java.util.Random;

import javax.ws.rs.NotAuthorizedException;

import daksh.userevents.storage.accounts.constants.DataConstants;
import daksh.userevents.storage.accounts.model.Account;

/**
 * Created by daksh on 22-May-16.
 */
public class DataSource {

    private static DataSource dataSource;

    public static DataSource getInstance() {
        if (dataSource == null) {
            dataSource = new DataSource();
        }

        return dataSource;
    }

    private final Datastore datastore;
    private final MongoClient mongoClient;

    private DataSource() {
        mongoClient = new MongoClient("localhost");
        Morphia morphia = new Morphia();
        morphia.mapPackage("daksh.userevents.storage.accounts.model", true);

        datastore = morphia.createDatastore(mongoClient, DataConstants.DB_NAME);
        datastore.ensureIndexes();
    }

    public Key<Account> createAccount(Account account) {
        Key<Account> key = datastore.save(account);
        regenerateAuthorizationToken((ObjectId) key.getId());
        return key;
    }

    public List<Account> getAllAccounts() {
        return datastore.createQuery(Account.class).asList();
    }

    public Account getAccount(String accountId) {
        return datastore.get(Account.class, new ObjectId(accountId));
    }

    private UpdateResults updateField(ObjectId accountId, String field, String value) {
        Query<Account> updateQuery = datastore.createQuery(Account.class)
                .field(Mapper.ID_KEY).equal(accountId);

        UpdateOperations<Account> ops = datastore.createUpdateOperations(Account.class)
                .set(field, value);

        return datastore.update(updateQuery, ops);
    }

    public WriteResult deleteAccount(String accountId) {
        return datastore.delete(Account.class, new ObjectId(accountId));
    }

    public void deleteDatabase(String dbName) {
        mongoClient.getDatabase(dbName).drop();
    }

    private UpdateResults regenerateAuthorizationToken(ObjectId accountId) {
        Random random = new SecureRandom();
        String token = new BigInteger(130, random).toString(32);
        return updateField(accountId, DataConstants.AUTHORIZATION_TOKEN, token);
    }

    public String getAccountIdFromAuthorizationToken(String token) throws NotAuthorizedException {
        Query<Account> accounts = datastore
                .find(Account.class, DataConstants.AUTHORIZATION_TOKEN, token).limit(1);

        if (accounts.countAll() == 0) {
            throw new NotAuthorizedException("Authorization token is invalid");
        }

        return accounts.iterator().next().getId().toString();
    }
}
