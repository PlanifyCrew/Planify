package com.mosbach.demo.data.impl;

import com.mosbach.demo.data.api.User;
import com.mosbach.demo.data.api.UserManager;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgresUserManagerImpl implements UserManager {

    String databaseURL = "jdbc:postgresql://localhost:5432/PlanifyDB";
    String username = "postgres";
    String password = "Slay123";
    BasicDataSource basicDataSource;

    // Singleton
    static PostgresUserManagerImpl postgresUserManager = null;
    private PostgresUserManagerImpl() {
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(databaseURL);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);
    }
    public static PostgresUserManagerImpl getPostgresUserManagerImpl() {
        if (postgresUserManager == null)
            postgresUserManager = new PostgresUserManagerImpl();
        return postgresUserManager;
    }


    @Override
    public int createUser(User user) {

        final Logger createUserLogger = Logger.getLogger("CreateUserLogger");
        createUserLogger.log(Level.INFO,"Start creating user " + user.getName());
        //Statement stmt = null;
        //Connection connection = null;
        String sql = "INSERT INTO users (name, email, password, token) VALUES (?, ?, ?, ?)";

        try (Connection connection = basicDataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getToken() != null ? user.getToken() : "");

            int rows = stmt.executeUpdate();

            if (!connection.getAutoCommit())
                connection.commit();

            // Wenn funktioniert hat, wird 1 zurückgegeben
            if (rows >= 1) {
                ResultSet rsKeys = stmt.getGeneratedKeys();
                if (rsKeys.next()) {
                    return rsKeys.getInt(1);
                } else {
                    createUserLogger.warning("Creating user failed, no ID obtained.");
                    return -1;
                }
                }
            // Wenn nicht
            return -1;

        } catch (SQLException e) {
            createUserLogger.log(Level.SEVERE, "SQL Exception occurred: " + e.getMessage(), e);
            e.printStackTrace();
            return -1;
        }


        /*
        try {
            connection = basicDataSource.getConnection();

            stmt = connection.createStatement();
            String updateSQL = "INSERT into users (name, email, password, token) VALUES (" +
                    "'" + user.getName() + "', '" + user.getEmail() + "', '" + user.getPassword() + "', '" + user.getToken() + "')";
            stmt.executeUpdate(updateSQL);

            // Fehler mit console.log ausgeben
            if (stmt.getUpdateCount() != 1) {
                System.out.println("Error: Could not insert new user in DB");
                return false;
            }

            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        try {
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
        */
    }

    @Override
    public String logUserOn(String email, String password) {
        final Logger logOnUserLogger = Logger.getLogger("logOnUserLogger");
        logOnUserLogger.log(Level.INFO,"Start logging on user " + email);
        //Statement stmt = null;
        //Connection connection = null;

        // Prüfen, ob Login-Daten korrekt sind
        String selectSQL = "SELECT user_id FROM users WHERE email = ? AND password = ?";

        try (Connection conn = basicDataSource.getConnection();
            PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {

            selectStmt.setString(1, email);
            selectStmt.setString(2, password);

            ResultSet rs = selectStmt.executeQuery();

            if (!rs.next()) {
                // Kein User gefunden
                logOnUserLogger.warning("Login failed for " + email);
                return "OFF";
            }

        int userId = rs.getInt("user_id");

        // Token generieren
        String token = UUID.randomUUID().toString();

        String updateSQL = "UPDATE users SET token = ? WHERE user_id = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
            updateStmt.setString(1, token);
            updateStmt.setInt(2, userId);
            updateStmt.executeUpdate();
        }

        if (!conn.getAutoCommit())
            conn.commit();

        logOnUserLogger.info("Login successful, token generated: " + token);
        return token;

        } catch (SQLException e) {
            logOnUserLogger.log(Level.SEVERE, "SQL Exception occurred: " + e.getMessage(), e);
            e.printStackTrace();
            return "OFF";
        }
        /* 
        try {
            connection = basicDataSource.getConnection();
            stmt = connection.createStatement();
            String updateSQL = "UPDATE users SET token  = " +
                    "'" + newToken + "' " +
                    "WHERE email = '" + email + "' AND password = '" + password + "'";
            stmt.executeUpdate(updateSQL);

            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return "OFF";
        }
        try {
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newToken;
        */
    }

    @Override
    public boolean logUserOff(String token) {
        final Logger logOffUserLogger = Logger.getLogger("logOffUserLogger");
        logOffUserLogger.log(Level.INFO,"Start logging off user with token " + token);
        //Statement stmt = null;
        //Connection connection = null;

        String updateSQL = "UPDATE users SET token = ? WHERE token = ?";

        try (Connection connection = basicDataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            
            stmt.setString(1, "OFF");
            stmt.setString(2, token);
            int rows = stmt.executeUpdate();

            if (!connection.getAutoCommit())
                connection.commit();

            // Bei Erfolg true zurückgeben
            if (rows >= 1)
                return true;

            // Bei Misserfolg false zurückgeben
            return false;

        } catch (SQLException e) {
            logOffUserLogger.log(Level.SEVERE, "SQL Exception occurred: " + e.getMessage(), e);
            e.printStackTrace();
        }
        return false;

        /*
        try {
            connection = basicDataSource.getConnection();
            stmt = connection.createStatement();
            String updateSQL = "UPDATE users SET token  = " +
                    "'OFF' " +
                    "WHERE email = '" + email + "'";
            stmt.executeUpdate(updateSQL);

            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        try {
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
        */
    }

    @Override
    public int getUserIdFromToken(String token) {

        final Logger readEmailLogger = Logger.getLogger("ReadEmailLogger");
        readEmailLogger.log(Level.INFO,"Start reading users from DB. ");

        int foundUserId = -1;
        //Statement stmt = null;
        //Connection connection = null;

        String sql = "SELECT user_id FROM users WHERE token = ?";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, token);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                // Kein User gefunden
                readEmailLogger.warning("No user found with token " + token);
                return -1;
            }

            foundUserId = rs.getInt("user_id");

        } catch (SQLException e) {
            System.err.println("Fehler beim Zugriff auf DB:");
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
        }

        return foundUserId;
    }


    public void createUserTable() {
        // Be carefull: It deletes data if table already exists.
        Statement stmt = null;
        Connection connection = null;
        try {
            connection = basicDataSource.getConnection();
            stmt = connection.createStatement();

            stmt.executeUpdate("DROP TABLE IF EXISTS users");
            stmt.executeUpdate("CREATE TABLE users (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name varchar(100) NOT NULL, " +
                    "email varchar(100) NOT NULL, " +
                    "password varchar(100) NOT NULL, " +
                    "token varchar(100))");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @Override
    public List<Integer> getUserIdsFromEmails(List<String> emails) {
        List<Integer> userIds = new ArrayList<>();

        if (emails == null || emails.isEmpty())
            return userIds;

        // ? Platzhalter für jede Email
        String placeholders = String.join(",", Collections.nCopies(emails.size(), "?"));
        String sql = "SELECT user_id FROM users WHERE email IN (" + placeholders + ")";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            for (int i = 0; i < emails.size(); i++) {
                pstmt.setString(i + 1, emails.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                userIds.add(rs.getInt("user_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userIds;
    }
}
