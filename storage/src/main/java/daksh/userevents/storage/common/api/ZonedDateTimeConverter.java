package daksh.userevents.storage.common.api;

import org.mongodb.morphia.converters.DateConverter;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.MappingException;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Created by daksh on 27-May-16.
 */
public class ZonedDateTimeConverter extends DateConverter {

    public ZonedDateTimeConverter() {
        super(ZonedDateTime.class);
    }

    @Override
    public Object decode(final Class targetClass, final Object val, final MappedField optionalExtraInfo)
            throws MappingException {
        Date date = (Date) super.decode(targetClass, val, optionalExtraInfo);
        Instant instant = Instant.ofEpochMilli(date.getTime());
        return ZonedDateTime.ofInstant(instant, ZoneOffset.systemDefault());
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        if (value == null) {
            return null;
        }
        return new Date(((ZonedDateTime) value).toInstant().toEpochMilli());
    }
}
