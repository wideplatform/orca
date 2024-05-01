package com.iostate.orca.sql.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

public class DatetimeTypeHandler implements TypeHandler<Object> {
    @Override
    public void setValue(PreparedStatement ps, int index, Object value) throws SQLException {
        if (value == null) {
            return;
        }
        Timestamp timestamp;
        if (value instanceof Instant instant) {
            timestamp = Timestamp.from(instant);
        } else if (value instanceof LocalDateTime localDateTime) {
            timestamp = Timestamp.valueOf(localDateTime);
        } else if (value instanceof Date date) {
            timestamp = new Timestamp(date.getTime());
        } else {
            throw new IllegalArgumentException("unknown datetime type: " + value.getClass());
        }
        ps.setTimestamp(index, timestamp);
    }

    @Override
    public Object getValue(ResultSet rs, int index, boolean nullable) throws SQLException {
        Timestamp value = rs.getTimestamp(index);
        if (value == null) {
            return null;
        }
        return value.toInstant();
    }

    @Override
    public Object getValue(ResultSet rs, String column, boolean nullable) throws SQLException {
        Timestamp value = rs.getTimestamp(column);
        if (value == null) {
            return null;
        }
        return value.toInstant();
    }
}
