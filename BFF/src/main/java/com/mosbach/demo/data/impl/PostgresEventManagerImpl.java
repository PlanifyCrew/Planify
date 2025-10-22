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
    public List<Event> getAllEventsPerUserId(int user_id) {
        final Logger readEventLogger = Logger.getLogger("ReadEventLogger");
        readEventLogger.log(Level.INFO,"Start reading events from DB. ");

        List<Event> events = new ArrayList<>();
        //Statement stmt = null;
        //Connection connection = null;
        String sql = "SELECT * FROM events WHERE user_id = ?";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user_id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Event event = new EventImpl(
                        rs.getInt("event_id"),
                        rs.getString("name"),
                        rs.getObject("date", LocalDate.class),
                        rs.getString("description"),
                        rs.getObject("start_time", LocalTime.class),
                        rs.getObject("end_time", LocalTime.class)
                );
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }

    @Override
    public int addEvent(Event event, int user_id) {
        final Logger createEventLogger = Logger.getLogger("CreateEventLogger");
        createEventLogger.log(Level.INFO,"Start creating event " + event.getName());
        int event_id = -1;
        //Statement stmt = null;
        //Connection connection = null;

        String sql_event = "INSERT INTO events (name, date, description, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
        String sql_participant = "INSERT INTO participants (event_id, user_id, role, status) VALUES (?, ?, ?, ?)";

        try (Connection connection = basicDataSource.getConnection()) {

            // Transaktion starten
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(sql_event, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setString(1, event.getName());
                pstmt.setObject(2, event.getDate());
                pstmt.setString(3, event.getDescription());
                pstmt.setObject(4, event.getStartTime());
                pstmt.setObject(5, event.getEndTime());
                pstmt.executeUpdate();

                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    event_id = generatedKeys.getInt(1);
                }

                try (PreparedStatement psPart = connection.prepareStatement(sql_participant)) {
                    // Organisator als Teilnehmer hinzufügen
                    psPart.setInt(1, event_id);
                    psPart.setInt(2, user_id);
                    psPart.setString(3, "Organisator");
                    psPart.setString(4, "erstellt");
                    psPart.executeUpdate();
                }

        }

        // alles committen
        connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        
        return event_id;
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


    @Override
    public boolean addParticipants(int event_id, List<Integer> user_ids) {
        final Logger addParticipantLogger = Logger.getLogger("AddParticipantLogger");
        addParticipantLogger.log(Level.INFO,"Start adding participants to event " + event_id);

        String sql_participant = "INSERT INTO participants (event_id, user_id, role, status) VALUES (?, ?, ?, ?)";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement psPart = connection.prepareStatement(sql_participant)) {

            for (int user_id : user_ids) {
                psPart.setInt(1, event_id);
                psPart.setInt(2, user_id);
                psPart.setString(3, "Teilnehmer");
                psPart.setString(4, "eingeladen");
                psPart.addBatch();
            }

            int[] results = psPart.executeBatch();
            for (int result : results) {
                if (result == PreparedStatement.SUCCESS_NO_INFO || result > 0) {
                    // Erfolgreich hinzugefügt
                } else {
                    // Fehler beim Hinzufügen
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return  false;
        }

        return true;
    }

}
