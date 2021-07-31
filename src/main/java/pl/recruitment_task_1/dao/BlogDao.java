package pl.recruitment_task_1.dao;

import pl.recruitment_task_1.model.Blog;
import pl.recruitment_task_1.utils.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BlogDao {
    //SQL query's
    private static final String CREATE_BLOG_QUERY = "INSERT INTO blog(text,userid) VALUES (?,?);";
    private static final String DELETE_BLOG_QUERY = "DELETE FROM blog WHERE id = ?;";
    private static final String FIND_ALL_BLOGS_QUERY = "SELECT * FROM blog";
    private static final String READ_BLOG_QUERY = "SELECT * FROM blog WHERE id = ?;";
    private static final String UPDATE_BLOG_QUERY = "UPDATE blog SET text = ?, userid = ? WHERE id = ?;";

    //create new blog
    public Blog create(Blog blog) {
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(CREATE_BLOG_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, blog.getText());
            statement.setLong(2, blog.getUserId());

            int result = statement.executeUpdate();
            if (result != 1) {
                throw new RuntimeException("Execute update returned " + result);
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.first()) {
                    blog.setId(generatedKeys.getLong(1));
                    return blog;
                } else {
                    throw new RuntimeException("Generated key was not found");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //read blog
    public Blog read(Long id) {
        Blog blog = new Blog();
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(READ_BLOG_QUERY)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    blog.setId(resultSet.getLong("id"));
                    blog.setText(resultSet.getString("text"));
                    blog.setUserId(resultSet.getLong("userid"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return blog;
    }

    //update blog
    public void update(Blog blog) {
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(UPDATE_BLOG_QUERY)) {
            statement.setString(1, blog.getText());
            statement.setLong(2, blog.getUserId());
            statement.setLong(3, blog.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //delete blog
    public void delete(Long id) {
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement statement = conn.prepareStatement(DELETE_BLOG_QUERY)) {
            statement.setLong(1, id);
            int deleted = statement.executeUpdate();
            if (deleted <= 0) {
                throw new NullPointerException("Blog not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //read all blogs
    public List<Blog> findAll() {
        List<Blog> blogList = new ArrayList<>();
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_BLOGS_QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Blog blogToAdd = new Blog();
                blogToAdd.setId(resultSet.getLong("id"));
                blogToAdd.setText(resultSet.getString("text"));
                blogToAdd.setUserId(resultSet.getLong("userid"));
                blogList.add(blogToAdd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return blogList;
    }
}
