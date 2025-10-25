package com.planify.data.impl;

import org.apache.commons.dbcp2.BasicDataSource;

import com.planify.data.api.Event;
import com.planify.data.api.EventManager;

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
    public List<Event> getAllEventsPerUserId(int user_id, LocalDate startDate, LocalDate endDate) {
        final Logger readEventLogger = Logger.getLogger("ReadEventLogger");
        readEventLogger.log(Level.INFO,"Start reading events from DB. ");

        List<Event> events = new ArrayList<>();
        //Statement stmt = null;
        //Connection connection = null;
        String sql = "SELECT e.* FROM events e " +
                     "JOIN participants p ON e.event_id = p.event_id " +
                     "WHERE p.user_id = ?";

        // Datumsfilter hinzufügen (für monatliche Kalendersicht)
        if (startDate != null && endDate != null)
            sql += " AND e.date BETWEEN ? AND ?";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, user_id);

            // Datumsfilter hinzufügen (für monatliche Kalendersicht)
            if (startDate != null && endDate != null) {
                pstmt.setObject(2, startDate);
                pstmt.setObject(3, endDate);
            }

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


    @Override
    public boolean updateEvent(int event_id, Event event) {
        final Logger updateEventLogger = Logger.getLogger("UpdateEventLogger");
        updateEventLogger.log(Level.INFO,"Start updating event " + event.getName());

        String sql = "UPDATE events SET name = ?, date = ?, description = ?, start_time = ?, end_time = ? WHERE event_id = ?";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, event.getName());
            pstmt.setObject(2, event.getDate());
            pstmt.setString(3, event.getDescription());
            pstmt.setObject(4, event.getStartTime());
            pstmt.setObject(5, event.getEndTime());
            pstmt.setInt(6, event_id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
    public boolean deleteEvent(int event_id) {
        final Logger deleteEventLogger = Logger.getLogger("DeleteEventLogger");
        deleteEventLogger.log(Level.INFO,"Start deleting event with ID " + event_id);

        String sql = "DELETE FROM events WHERE event_id = ?";

        try (Connection connection = basicDataSource.getConnection()) {

            // Transaktion starten
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

                pstmt.setInt(1, event_id);
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    String sql_part = "DELETE FROM participants WHERE event_id = ?";
                    try (PreparedStatement psPart = connection.prepareStatement(sql_part)) {

                        psPart.setInt(1, event_id);
                        psPart.executeUpdate();
                    }
                }
            }
        
        // alles commiten
        connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    @Override
    public List<com.planify.model.teilnehmer.Teilnehmerliste> getParticipants(int event_id) {
        final Logger getParticipantsLogger = Logger.getLogger("GetParticipantsLogger");
        getParticipantsLogger.log(Level.INFO,"Start getting participants for event " + event_id);

        List<com.planify.model.teilnehmer.Teilnehmerliste> participants = new ArrayList<>();

        String sql = "SELECT * FROM participants WHERE event_id = ?";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, event_id);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                com.planify.model.teilnehmer.Teilnehmerliste pList = new com.planify.model.teilnehmer.Teilnehmerliste(
                        rs.getInt("event_id"),
                        rs.getInt("user_id"),
                        rs.getString("role"),
                        rs.getString("status")
                );
                participants.add(pList);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return participants;
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


    @Override
    public boolean removeParticipants(int event_id, List<Integer> user_ids) {
        final Logger removeParticipantLogger = Logger.getLogger("RemoveParticipantLogger");
        removeParticipantLogger.log(Level.INFO,"Start removing participants from event " + event_id);

        String sql = "DELETE FROM participants WHERE event_id = ? AND user_id = ?";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            for (int user_id : user_ids) {
                pstmt.setInt(1, event_id);
                pstmt.setInt(2, user_id);
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            for (int result : results) {
                if (result == PreparedStatement.SUCCESS_NO_INFO || result > 0) {
                    // Erfolgreich entfernt
                } else {
                    // Fehler beim Entfernen
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
