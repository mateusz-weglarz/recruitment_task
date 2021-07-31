package pl.recruitment_task_1.dao;

import org.mindrot.jbcrypt.BCrypt;
import pl.recruitment_task_1.model.User;
import pl.recruitment_task_1.utils.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    //SQL query's
    private static final String CREATE_USER_QUERY = "INSERT INTO user(username,password,permission,readonly) VALUES (?,?,?,?);";
    private static final String DELETE_USER_QUERY = "DELETE FROM user WHERE userid = ?;";
    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM user";
    private static final String READ_USER_QUERY = "SELECT * FROM user WHERE userid = ?;";
    private static final String UPDATE_USER_QUERY = "UPDATE user SET username = ?, password = ?, permission = ?, readonly = ? WHERE id = ?;";
    private static final String READ_PASSWORD_QUERY = "SELECT password FROM user WHERE username = ?";

    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    //create new user
    public User create(User user) {
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(CREATE_USER_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUserName());
            statement.setString(2, hashPassword(user.getPassword()));
            statement.setString(3, user.getPermission());
            statement.setString(4, user.getReadOnly());

            int result = statement.executeUpdate();
            if (result != 1) {
                throw new RuntimeException("Execute update returned " + result);
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.first()) {
                    user.setUserId(generatedKeys.getLong(1));
                    return user;
                } else {
                    throw new RuntimeException("Generated key was not found");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    //read user
    public User read(Long userId) {
        User user = new User();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(READ_USER_QUERY)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    user.setUserId(resultSet.getLong("userid"));
                    user.setPassword(resultSet.getString("password"));
                    user.setPermission(resultSet.getString("permission"));
                    user.setReadOnly((resultSet.getString("readonly")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    //update user
    public void update(User user) {
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(UPDATE_USER_QUERY)) {
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getPermission());
            statement.setString(4, user.getReadOnly());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //delete user
    public void delete(Long userId) {
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(DELETE_USER_QUERY)) {
            statement.setLong(1, userId);
            int deleted = statement.executeUpdate();
            if (deleted <= 0) {
                throw new NullPointerException("User not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //read all users
    public List<User> findAll() {
        List<User> userList = new ArrayList<>();
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_USERS_QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                User userToAdd = new User();
                userToAdd.setUserId(resultSet.getLong("userid"));
                userToAdd.setUserName(resultSet.getString("username"));
                userToAdd.setPassword(resultSet.getString("password"));
                userToAdd.setPermission(resultSet.getString("permission"));
                userToAdd.setReadOnly(resultSet.getString("readonly"));
                userList.add(userToAdd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    //authorization
    //I changed the maximum password length in the database because the hashed password did not fit
    public boolean authorization(String username, String password) {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(READ_PASSWORD_QUERY)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return BCrypt.checkpw(password, rs.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
