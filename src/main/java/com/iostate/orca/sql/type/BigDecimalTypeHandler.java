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
    public BigDecimal getValue(ResultSet rs, int index) throws SQLException {
        return rs.getBigDecimal(index);
    }

    @Override
    public BigDecimal getValue(ResultSet rs, String column) throws SQLException {
        return rs.getBigDecimal(column);
    }
}
