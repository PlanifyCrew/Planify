package com.mosbach.demo.data.impl;

import com.mosbach.demo.data.api.Event;
import com.mosbach.demo.data.api.EventManager;
import com.mosbach.demo.model.user.User;
import org.apache.commons.dbcp2.BasicDataSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.LocalTime;

public class PostgresEventManagerImpl implements EventManager  {

    String databaseURL = "jdbc:postgresql://localhost:5432/PlanifyDB";
    String username = "postgres";
    String password = "Slay123";
    BasicDataSource basicDataSource;

    // Singleton
    static PostgresEventManagerImpl postgresEventManager = null;
    private PostgresEventManagerImpl() {
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(databaseURL);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);
    }
    public static PostgresEventManagerImpl getPostgresEventManagerImpl() {
        if (postgresEventManager == null)
            postgresEventManager = new PostgresEventManagerImpl();
        return postgresEventManager;
    }


    @Override
    public List<Event> getAllEventsPerEmail(String email) {
        final Logger readEventLogger = Logger.getLogger("ReadEventLogger");
        readEventLogger.log(Level.INFO,"Start reading events from DB. ");

        List<Event> events = new ArrayList<>();
        //Statement stmt = null;
        //Connection connection = null;
        String sql = "SELECT * FROM events WHERE email = ?";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Event event = new EventImpl(
                        rs.getString("name"),
                        rs.getObject("date", LocalDate.class),
                        rs.getString("description"),
                        rs.getObject("start_time", LocalTime.class),
                        rs.getObject("end_time", LocalTime.class),
                        rs.getString("email")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }

    @Override
    public boolean addEvent(Event event) {
        final Logger createEventLogger = Logger.getLogger("CreateEventLogger");
        createEventLogger.log(Level.INFO,"Start creating event " + event.getName());
        //Statement stmt = null;
        //Connection connection = null;

        String sql = "INSERT INTO events (name, date, description, start_time, end_time, email) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, event.getName());
            pstmt.setObject(2, event.getDate());
            pstmt.setString(3, event.getDescription());
            pstmt.setObject(4, event.getStartTime());
            pstmt.setObject(5, event.getEndTime());
            pstmt.setString(6, event.getEmail());
            pstmt.executeUpdate();

            pstmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void createTaskTable() {
        // Be carefull: It deletes data if table already exists.
        Statement stmt = null;
        Connection connection = null;
        try {
            connection = basicDataSource.getConnection();
            stmt = connection.createStatement();

            stmt.executeUpdate("DROP TABLE IF EXISTS tasks");
            stmt.executeUpdate("CREATE TABLE tasks (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name varchar(100) NOT NULL, " +
                    "priority int NOT NULL, " +
                    "email varchar(100) NOT NULL)");

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


}
