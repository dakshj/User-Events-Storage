package daksh.userevents.storage.users.db;

import org.bson.types.ObjectId;

import daksh.userevents.storage.common.db.CollectionSpecificDao;
import daksh.userevents.storage.users.constants.UserDataConstants;
import daksh.userevents.storage.users.model.User;

/**
 * Created by daksh on 22-May-16.
 */
public class UserDao extends CollectionSpecificDao<User> {

    public static UserDao getInstance(ObjectId appId) {
        UserDao userDao = (UserDao) getFromMap(appId);

        if (userDao == null) {
            userDao = new UserDao(appId);
            putIntoMap(appId, userDao);
        }

        return userDao;
    }

    private UserDao(ObjectId appId) {
        super(appId, User.class);
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
