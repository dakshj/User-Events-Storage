package daksh.userevents.storage.common.util;

import com.sun.istack.internal.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by daksh on 24-May-16.
 */
public class LruCache<Key, Value> {

    private static final int LRU_MAX = 50;

    private LinkedHashMap<Key, Value> map;

    public LruCache() {
        map = new LinkedHashMap<Key, Value>(
                LRU_MAX * 4 / 3, 0.75f, true
        ) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Key, Value> eldest) {
                return size() > LRU_MAX;
            }
        };
    }

    @Nullable
    public Value get(Key key) {
        if (map == null) {
            return null;
        }

        return map.get(key);
    }

    public void put(Key key, Value value) {
        if (map == null) {
            map = new LinkedHashMap<>();
        }

        map.put(key, value);
    }

    public LinkedHashMap<Key, Value> getMap() {
        if (map == null) {
            map = new LinkedHashMap<>();
        }

        return map;
    }

    public void removeByValue(Value value) {
        if (map == null) {
            return;
        }

        map.values().removeAll(Collections.singleton(value));
    }
}
