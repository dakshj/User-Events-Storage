package daksh.userevents.storage.accounts.db;

import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;

import java.util.List;

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

    private DataSource() {
        MongoClient mongoClient = new MongoClient("localhost");
        Morphia morphia = new Morphia();
        morphia.mapPackage("daksh.userevents.storage.accounts.model", true);

        datastore = morphia.createDatastore(mongoClient, DataConstants.DB_NAME);
        datastore.ensureIndexes();
    }

    public Key<Account> createAccount(Account account) {
        return datastore.save(account);
    }

    public List<Account> getAllAccounts() {
        return datastore
                .createQuery(Account.class)
                .asList();
    }

    public Account getAccount(String accountId) {
        return datastore.get(Account.class, new ObjectId(accountId));
    }

    public WriteResult deleteAccount(String accountId) {
        return datastore.delete(Account.class, new ObjectId(accountId));
    }
}
