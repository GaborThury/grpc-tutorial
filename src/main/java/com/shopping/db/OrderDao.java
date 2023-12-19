package com.shopping.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderDao {
    private final Logger LOGGER = Logger.getLogger(OrderDao.class.getName());

    public List<Order> getOrders(int userId) {
        Connection connection = null;
        List<Order> orders = new ArrayList<>();
        try {
            connection = getConnection();
            ResultSet resultSet = getResultSet(userId, connection);
            while(resultSet.next()) {
                mapToList(orders, resultSet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not execute query", e);
        }
        return orders;
    }

    private Connection getConnection() {
        return H2DatabaseConnection.getConnectionToDatabase();
    }

    private ResultSet getResultSet(int userId, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from orders where user_id=?");
        preparedStatement.setInt(1, userId);
        return preparedStatement.executeQuery();
    }

    private void mapToList(List<Order> orders, ResultSet resultSet) throws SQLException {
        Order order = new Order();
        order.setUserId(resultSet.getInt("user_id"));
        order.setOrderId(resultSet.getInt("order_id"));
        order.setNoOfItems(resultSet.getInt("no_of_items"));
        order.setTotalAmount(resultSet.getDouble("total_amount"));
        order.setOrderDate(resultSet.getDate("order_date"));

        orders.add(order);
    }
}
