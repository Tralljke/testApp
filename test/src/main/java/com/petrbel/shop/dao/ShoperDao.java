package com.petrbel.shop.dao;

import com.petrbel.shop.model.pojo.Shoper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShoperDao {

    static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/postgres";
    static final String USER = "postgres";
    static final String PASSWORD = "2295300";

    private Connection getNewConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
    }

    private List<Shoper> toPojo(ResultSet resultSet) throws SQLException {
        List<Shoper> shopers = new ArrayList<>();
        while (resultSet.next()) {
            shopers.add(new Shoper(resultSet.getInt("id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name")));
        }
        return shopers;
    }

    public List<Shoper> findByLastName(String lastName) throws SQLException {
        String query = "SELECT * FROM shoper " +
                "       WHERE last_name = ?";
        try (PreparedStatement statement = getNewConnection().prepareStatement(query)) {
            statement.setString(1, lastName);
            ResultSet resultSet = statement.getResultSet();
            return toPojo(resultSet);
        }
    }

    public List<Shoper> findByProductName(String productName, int minTimes) throws SQLException {
        String query = "SELECT * FROM shoper\n" +
                "       INNER JOIN (\n" +
                "       SELECT shoper_id, count(*)\n" +
                "       FROM purchase\n" +
                "       WHERE goods_id = (SELECT id FROM goods WHERE name = ?)\n" +
                "       GROUP BY shoper_id) as sic ON sic.shoper_id = shoper.id WHERE sic.count >=?";
        try (PreparedStatement statement = getNewConnection().prepareStatement(query)) {
            statement.setString(1, productName);
            statement.setInt(2, minTimes);
            ResultSet resultSet = statement.getResultSet();
            return toPojo(resultSet);
        }
    }

    public List<Shoper> findByExpenses(int minExpenses, int maxExpenses) throws SQLException {
        String query = "SELECT id, first_name, last_name FROM shoper \n" +
                "    INNER JOIN \n" +
                "    (SELECT shoper_id, sum(price) FROM shoper INNER JOIN (\n" +
                "        SELECT shoper_id, price FROM purchase \n" +
                "            INNER JOIN goods g on g.id = purchase.goods_id) as sigip\n" +
                "              ON shoper.id = sigip.shoper_id GROUP BY sigip.shoper_id) as price_sum \n" +
                "        ON shoper_id=shoper.id\n" +
                "WHERE price_sum.sum > ? AND price_sum.sum < ?";
        try (PreparedStatement statement = getNewConnection().prepareStatement(query)) {
            statement.setInt(1, minExpenses);
            statement.setInt(2, maxExpenses);
            ResultSet resultSet = statement.getResultSet();
            return toPojo(resultSet);
        }
    }

    public List<Shoper> findBadCustomers(int badCustomers) throws SQLException {
        String query = "SELECT id, first_name, last_name FROM shoper\n" +
                "    INNER JOIN\n" +
                "    (SELECT shoper_id, count(goods_id) FROM purchase GROUP BY shoper_id) AS goods_count\n" +
                "        ON shoper.id = goods_count.shoper_id\n" +
                "ORDER BY goods_count.count LIMIT ?";
        try (PreparedStatement statement = getNewConnection().prepareStatement(query)) {
            statement.setInt(1, badCustomers);
            ResultSet resultSet = statement.getResultSet();
            return toPojo(resultSet);
        }
    }
}
