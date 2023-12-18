package com.shopping.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDao {
    private static final Logger LOGGER = Logger.getLogger(UserDao.class.getName());

    public User getDetails(String username) {
        User user = new User();

        try {
            Connection connection = getConnection();
            ResultSet resultSet = getResultSet(username, connection);
            while(resultSet.next()) {
                map(user, resultSet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not execute query", e);
        }
        return user;
    }

    private Connection getConnection() {
        return H2DatabaseConnection.getConnectionToDatabase();
    }

    private ResultSet getResultSet(String username, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from user where username=?");
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    private void map(User user, ResultSet resultSet) throws SQLException {
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setName(resultSet.getString("name"));
        user.setAge(resultSet.getInt("age"));
        user.setGender(resultSet.getString("gender"));
    }
}
