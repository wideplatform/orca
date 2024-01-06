package com.iostate.orca.sql.query;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SqlQueryTest {

    @Test
    public void testOneTable() {
        SqlQuery sqlQuery = new SqlQuery();
        SqlTable table = sqlQuery.addJoinTable(
                "user",
                List.of("id", "name"),
                List.of("id"),
                null,
                null);
        SqlCondition idFilter = new SqlCondition(
                table.columnRef("id"),
                "=",
                sqlQuery.createArgument(1L));
        table.addFilter(idFilter);

        assertEquals(
                "SELECT t1.id,t1.name FROM user t1 WHERE t1.id = ? ORDER BY t1.id",
                sqlQuery.toString()
        );
        assertEquals(
                List.of(1L),
                sqlQuery.getArgumentValues()
        );
    }

    @Test
    public void test1Join() {
        SqlQuery sqlQuery = new SqlQuery();

        SqlTable table1 = sqlQuery.addJoinTable(
                "user",
                List.of("id", "name"),
                List.of("id"),
                null,
                null);
        SqlCondition idFilter = new SqlCondition(
                table1.columnRef("id"),
                "=",
                sqlQuery.createArgument(1L));
        table1.addFilter(idFilter);

        SqlTable table2 = sqlQuery.addJoinTable(
                "article",
                List.of("id", "title", "author_id"),
                List.of("id"),
                table1.columnRef("id"),
                "author_id");

        assertEquals(
                "SELECT t1.id,t1.name,t2.id,t2.title,t2.author_id" +
                        " FROM user t1 LEFT JOIN article t2 ON t1.id = t2.author_id" +
                        " WHERE t1.id = ? ORDER BY t1.id,t2.id",
                sqlQuery.toString()
        );
        assertEquals(
              List.of(1L),
              sqlQuery.getArgumentValues()
        );
    }

    @Test
    public void test2Joins() {
        SqlQuery sqlQuery = new SqlQuery();

        SqlTable table1 = sqlQuery.addJoinTable(
                "user",
                List.of("id", "name"),
                List.of("id"),
                null,
                null);
        SqlCondition idFilter = new SqlCondition(
                table1.columnRef("id"),
                "=",
                sqlQuery.createArgument(1L));
        table1.addFilter(idFilter);

        SqlTable table2 = sqlQuery.addJoinTable(
                "article",
                List.of("id", "title", "author_id"),
                List.of("id"),
                table1.columnRef("id"),
                "author_id");

        SqlTable table3 = sqlQuery.addJoinTable(
                "file",
                List.of("id", "url", "article_id"),
                List.of(),
                table2.columnRef("id"),
                "article_id");
        SqlCondition fileTypeFilter = new SqlCondition(
                table3.columnRef("type"),
                "=",
                sqlQuery.createArgument("attachment"));
        table3.addFilter(fileTypeFilter);

        assertEquals(
                "SELECT t1.id,t1.name,t2.id,t2.title,t2.author_id,t3.id,t3.url,t3.article_id FROM user t1 LEFT JOIN article t2 ON t1.id = t2.author_id LEFT JOIN file t3 ON t2.id = t3.article_id" +
                        " WHERE t1.id = ? AND t3.type = ? ORDER BY t1.id,t2.id",
                sqlQuery.toString()
        );
        assertEquals(
                List.of(1L, "attachment"),
                sqlQuery.getArgumentValues()
        );
    }
}