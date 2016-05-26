package daksh.userevents.storage.devices.db;

import org.bson.types.ObjectId;

import daksh.userevents.storage.common.db.Dao;
import daksh.userevents.storage.common.util.LruCache;
import daksh.userevents.storage.devices.constants.DeviceDataConstants;
import daksh.userevents.storage.devices.model.Device;
import daksh.userevents.storage.events.db.EventDao;

/**
 * Created by daksh on 22-May-16.
 */
public class DeviceDao extends Dao<Device> {

    private static LruCache<ObjectId, DeviceDao> cache;

    public static DeviceDao getInstance(ObjectId appId) {
        if (cache == null) {
            cache = new LruCache<>();
        }

        DeviceDao deviceDao = cache.get(appId);

        if (deviceDao != null) {
            return deviceDao;
        }

        deviceDao = new DeviceDao(appId);
        cache.put(appId, deviceDao);
        return deviceDao;
    }

    private DeviceDao(ObjectId appId) {
        super(appId, Device.class);
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
    }

    @Override
    public String getModelsPackage() {
        return DeviceDataConstants.MODELS_PACKAGE;
    }

    @Override
    public String getDbName() {
        return DeviceDataConstants.DB_NAME;
    }
}
