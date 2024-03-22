package com.iostate.orca.sql.type;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BigDecimalTypeHandler implements TypeHandler<BigDecimal> {
    @Override
    public void setValue(PreparedStatement ps, int index, BigDecimal value) throws SQLException {
        ps.setBigDecimal(index, value);
    }

    @Override
    public BigDecimal getValue(ResultSet rs, int index, boolean nullable) throws SQLException {
        BigDecimal value = rs.getBigDecimal(index);
        if (value == null && !nullable) {
            return BigDecimal.valueOf(0L);
        } else {
            return value;
        }
    }

    @Override
    public BigDecimal getValue(ResultSet rs, String column, boolean nullable) throws SQLException {
        BigDecimal value = rs.getBigDecimal(column);
        if (value == null && !nullable) {
            return BigDecimal.valueOf(0L);
        } else {
            return value;
        }
    }
}
