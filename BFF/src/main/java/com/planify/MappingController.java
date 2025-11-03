package com.planify;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
//import software.amazon.awssdk.*;

import com.planify.data.api.TaskManager;
import com.planify.data.api.UserManager;
import com.planify.data.impl.*;
import com.planify.model.event.Event;
import com.planify.model.event.KalenderItem;
import com.planify.model.event.TokenEvent;
import com.planify.model.task.*;
import com.planify.model.teilnehmer.Teilnehmerliste;
import com.planify.model.user.MessageAnswer;
import com.planify.model.user.Token;
import com.planify.model.user.TokenAnswer;
import com.planify.model.user.User;
import com.planify.model.user.UserWithName;
import com.planify.model.email.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.Objects;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class MappingController {

    TaskManager taskManager =
            PropertyFileTaskManagerImpl.getPropertyFileTaskManagerImpl("src/main/resources/tasks.properties");
            // PostgresTaskManagerImpl.getPostgresTaskManagerImpl();

    UserManager userManager =
            PropertyFileUserManagerImpl.getPropertyFileUserManagerImpl("src/main/resources/users.properties");
            // PostgresUserManagerImpl.getPostgresUserManagerImpl();

        // Variante Postgres
        PostgresUserManagerImpl pgUserManager = PostgresUserManagerImpl.getPostgresUserManagerImpl();
        PostgresEventManagerImpl pgEventManager = PostgresEventManagerImpl.getPostgresEventManagerImpl();

    MailPublisher mailPublisher;

    @PostMapping(
            path = "/login",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public TokenAnswer loginUser(@RequestBody User user) {

        Logger myLogger = Logger.getLogger("UserLoggingOn");
        myLogger.info("Received a POST request on login with email " + user.getEmail());

        //String token = userManager.logUserOn(user.getEmail(), user.getPassword());
        //myLogger.info("Token generated " + token);

        // Variante Postgres
        String token = pgUserManager.logUserOn(user.getEmail(), user.getPassword());
        myLogger.info("Token generated " + token);

        // Fehlerfall behandeln
        if (token.equals("OFF"))
            return
                    new TokenAnswer("OFF","0");

        return
                new TokenAnswer(token,"60");
    }


    @PostMapping(
            path = "/logout",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public com.planify.model.user.MessageAnswer logOffUser(@RequestBody Token token) {

        Logger myLogger = Logger.getLogger("UserLoggingOff");
        myLogger.info("Received a DELETE request on login with token " + token.getToken());

        boolean couldLogoffUser =
                //userManager.logUserOff(userManager.getUserEmailFromToken(token.getToken()));
                pgUserManager.logUserOff(token.getToken());

        myLogger.info("User logged off " + couldLogoffUser);

        // Fehlerfall behandeln
        if (!couldLogoffUser) {
            return new com.planify.model.user.MessageAnswer("User could not be logged out.");
        }

        return new com.planify.model.user.MessageAnswer("User logged out.");
    }


    @PostMapping(
            path = "/user",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public com.planify.model.user.MessageAnswer createUser(@RequestBody UserWithName userWithName) {

        Logger myLogger = Logger.getLogger("UserCreate");
        myLogger.info("Received a POST request on user with email " + userWithName.getEmail());

        /*
        boolean couldCreateUser = userManager
                        .createUser(
                            new UserImpl(
                                    userWithName.getName(),
                                    userWithName.getEmail(),
                                    userWithName.getPassword(),
                                    "OFF"
                            )
                        );
                        */
        // Variante Postgres
        UserImpl userImpl = new UserImpl(
                                0,
                                userWithName.getName(),
                                userWithName.getEmail(),
                                userWithName.getPassword(),
                                "OFF"
                        );
        
        userImpl.setUserId(pgUserManager.createUser(userImpl));

        if (userImpl.getUserId() != -1) {
            myLogger.info("User created " + userImpl.getUserId());
        }
        else {
            myLogger.info("User could not be created " + userImpl.getUserId());
        }

        return
                new com.planify.model.user.MessageAnswer("User created.");
    }


    @PostMapping(
            path = "/event",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public int addTokenEvent(@RequestBody TokenEvent tokenEvent) {

        Logger myLogger = Logger.getLogger("AddEvent");
        myLogger.info("Received a POST request on event with token " + tokenEvent.getToken());

        int userId = pgUserManager.getUserIdFromToken(tokenEvent.getToken());
        myLogger.info("Found the following userId for this token " + userId);
        if (userId == -1)
            return 0;

        EventImpl eventImpl = new EventImpl(
                                0,
                                tokenEvent.getEvent().getName(),
                                tokenEvent.getEvent().getDate(),
                                tokenEvent.getEvent().getDescription(),
                                tokenEvent.getEvent().getStartTime(),
                                tokenEvent.getEvent().getEndTime()
                        );

        eventImpl.setEventId(pgEventManager.addEvent(eventImpl, userId));

        myLogger.info("Event created " + eventImpl.getEventId());

        int eventId = eventImpl.getEventId();
        if (eventId != -1) {
            pgEventManager.addParticipants(
                eventId,
                pgUserManager.getUserIdsFromEmails(tokenEvent.getTnListe())
            );
        }

        return eventId;
    }


    @PutMapping(
            path = "/event/{event_id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public com.planify.model.user.MessageAnswer updateEvent(@PathVariable("event_id") int event_id,
                                                            @RequestBody TokenEvent tokenEvent) {

        Logger myLogger = Logger.getLogger("UpdateEvent");
        myLogger.info("Received a PUT request on event with token " + tokenEvent.getToken());

        int userId = pgUserManager.getUserIdFromToken(tokenEvent.getToken());
        myLogger.info("Found the following userId for this token " + userId);
        if (userId == -1)
            return new com.planify.model.user.MessageAnswer("No user found or not logged on.");

        EventImpl eventImpl = new EventImpl(
                                event_id,
                                tokenEvent.getEvent().getName(),
                                tokenEvent.getEvent().getDate(),
                                tokenEvent.getEvent().getDescription(),
                                tokenEvent.getEvent().getStartTime(),
                                tokenEvent.getEvent().getEndTime()
                        );

        boolean updated = pgEventManager.updateEvent(event_id, eventImpl);

        if (updated) {
            myLogger.info("Event updated " + eventImpl.getEventId());

            List<Integer> user_ids = pgUserManager.getUserIdsFromEmails(tokenEvent.getTnListe());
            List<Teilnehmerliste> currentParticipants = pgEventManager.getParticipants(event_id);

            // Änderungen des Users: Teilnehmer hinzufügen bzw. löschen
            // 1. Sets zur effizienten Differenzbildung
            Set<Integer> newUserIds = new HashSet<>(user_ids);
            Set<Integer> currentUserIds = currentParticipants.stream()
                    .map(Teilnehmerliste::getUserId)
                    .collect(Collectors.toSet());

            // 2. Teilnehmer, die hinzugefügt werden müssen
            List<Integer> toAdd = new ArrayList<>(newUserIds);
            toAdd.removeAll(currentUserIds);

            // 3. Teilnehmer, die entfernt werden müssen
            List<Integer> toRemove = new ArrayList<>(currentUserIds);
            toRemove.removeAll(newUserIds);

            // 4. Service-Aufrufe
            if (!toAdd.isEmpty()) {
                pgEventManager.addParticipants(event_id, toAdd);
            }

            if (!toRemove.isEmpty()) {
                pgEventManager.removeParticipants(event_id, toRemove);
            } 
        }
        else {
            myLogger.info("Event could not be updated " + eventImpl.getEventId());
        }

        return new com.planify.model.user.MessageAnswer("Event updated.");
    }


    @GetMapping("/event")
    public List<Event> getEventList(@RequestParam(value = "token", defaultValue = "123") String token,
                                    @RequestParam(value = "startDate") LocalDate startDate,
                                    @RequestParam(value = "endDate") LocalDate endDate) {

        Logger myLogger = Logger.getLogger("EventLogger");
        myLogger.info("Received a GET request on event with token " + token);

        int userId = pgUserManager.getUserIdFromToken(token);

        if (userId == -1)
            return new ArrayList<>();

        List<com.planify.data.api.Event> events = pgEventManager.getAllEventsPerUserId(userId, startDate, endDate);
        List<Event> result = new ArrayList<>();

        for (com.planify.data.api.Event e : events)
            result.add(new Event(e.getEventId(), e.getName(), e.getDate(), e.getDescription(), e.getStartTime(), e.getEndTime()));

        return result;
    }


    @GetMapping("/event/{event_id}")
    public KalenderItem getEvent(@RequestParam(value = "token") String token,
                          @RequestParam(value = "event_id") int event_id) {
        
        int userId = pgUserManager.getUserIdFromToken(token);
        
        if (userId == -1)
            return new KalenderItem();

        com.planify.data.api.Event apiEvent = pgEventManager.getEvent(event_id);
        Event modelEvent = new Event(
            event_id,
            apiEvent.getName(),
            apiEvent.getDate(),
            apiEvent.getDescription(),
            apiEvent.getStartTime(),
            apiEvent.getEndTime()
        );
        KalenderItem ki = new KalenderItem(
            token,
            modelEvent,
            pgEventManager.getParticipants(event_id)
        );
        return ki;
    }


    @DeleteMapping("/event/{event_id}")
    @ResponseStatus(HttpStatus.OK)
    public com.planify.model.user.MessageAnswer deleteEvent(@PathVariable("event_id") int event_id,
                                                            @RequestParam(value = "token", defaultValue = "OFF") String token) {

        Logger myLogger = Logger.getLogger("DeleteEvent");
        myLogger.info("Received a DELETE request on event with token " + token);

        int userId = pgUserManager.getUserIdFromToken(token);
        myLogger.info("Found the following userId for this token " + userId);
        if (userId == -1)
            return new com.planify.model.user.MessageAnswer("No user found or not logged on.");

        boolean deleted = pgEventManager.deleteEvent(event_id);

        if (deleted) {
            myLogger.info("Event deleted " + event_id);
        }
        else {
            myLogger.info("Event could not be deleted " + event_id);
        }

        return new com.planify.model.user.MessageAnswer("Event deleted.");
    }


    @PostMapping(
        path = "/sendEmail",
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public com.planify.model.user.MessageAnswer sendEmail(@RequestBody TokenEvent tokenEvent) {

        Logger myLogger = Logger.getLogger("UserCreate");
        myLogger.info("Received a POST request on user with token " + tokenEvent.getToken());

        int userId = pgUserManager.getUserIdFromToken(tokenEvent.getToken());

        if (userId == -1)
            return new MessageAnswer("Kein User mit dem Token");

        List<Teilnehmerliste> tnListe = pgEventManager.getParticipants(tokenEvent.getEvent().getEventId());
        List<String> emailListe = tnListe.stream()
            .filter(tn -> "Teilnehmer".equals(tn.getRole()))
            .map(Teilnehmerliste::getEmail)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        //boolean emailsSent = pgEventManager.sendEmail(tokenEvent.getEvent().getEventId(), emailListe);

        for (String email : emailListe) {
            EventMailPayload payload = new EventMailPayload(tokenEvent.getEvent().getEventId(), email);
            mailPublisher.sendMailToQueue(payload);
        }
        return new MessageAnswer("E-Mail(s) in Queue gelegt.");


        /*if (!emailsSent)
        return new com.planify.model.user.MessageAnswer("E-Mail sending error.");

        return new com.planify.model.user.MessageAnswer("E-Mail(s) sent.");*/
    }


    @PostMapping(
        path = "/checkParticipant",
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public com.planify.model.user.MessageAnswer checkParticipant(@RequestBody TokenEvent tokenEvent) {

        Logger myLogger = Logger.getLogger("AddTask");
        myLogger.info("Received a POST request on checkParticipant with token " + tokenEvent.getToken());

        int userId = pgUserManager.getUserIdFromToken(tokenEvent.getToken());

        if (userId == -1)
            return new MessageAnswer("Token nicht bestätigt");

        boolean statusChanged = pgEventManager.changeStatus(tokenEvent.getEvent().getEventId(), userId);

        if (!statusChanged)
            return new MessageAnswer("Status konnte nicht geändert werden");

        return new MessageAnswer("Participant angenommen");
    }


    @PostMapping(
            path = "/task",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public com.planify.model.user.MessageAnswer loginUser(@RequestBody TokenTask tokenTask) {

        Logger myLogger = Logger.getLogger("AddTask");
        myLogger.info("Received a POST request on task with token " + tokenTask.getToken());

        String userId = "dummy";
        myLogger.info("Found the following userId for this token " + userId);
        if (userId.equals("NOT-FOUND"))
            return
                    new com.planify.model.user.MessageAnswer("No user found or not logged on.");
        boolean couldCreateTask = taskManager
                .addTask(
                        new TaskImpl(
                                tokenTask.getTask().getName(),
                                tokenTask.getTask().getPriority(),
                                userId
                        )
                );

        myLogger.info("Task created " + couldCreateTask);

        // Turn on if you have a queue and want to push a message into it
        // Then move the SQSClientBuilder up so that it is called only once,
        // keep only the sendMessage command here

        /*
        AwsBasicCredentials accessCredentials = AwsBasicCredentials.builder()
                .accessKeyId("geheim")
                .secretAccessKey("noch mehr geheim")
                .build();

        try (SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_WEST_2)
                .credentialsProvider(new AwsCredentialsProvider() {
                    public AwsCredentials resolveCredentials() {
                        return accessCredentials;
                    }
                })
                .build()) {
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl("http://my-queue-url")
                    .messageBody("Task created. ")
                    .build()
            );
        }
        */

        // TODO
        // Fehlerfall behandeln

        return
                new com.planify.model.user.MessageAnswer("Task created.");
    }
/* 
    @GetMapping("/task")
    public TaskList getTasks(@RequestParam(value = "token", defaultValue = "123") String token) {

        Logger myLogger = Logger.getLogger("TaskLogger");
        myLogger.info("Received a GET request on task with token " + token);

        String userId = userManager.getUserIdFromToken(token);
        List<com.demo.data.api.Task> tasks = taskManager.getAllTasksPerUserId(userId);
        List<Task> result = new ArrayList<>();
        for (com.demo.data.api.Task t : tasks)
            result.add(new Task(t.getName(), t.getPriority()));

        // TODO
        // Fehlerfall behandeln

        return
                new TaskList(result);
    }
                */

    @GetMapping("/task/createtables")
    @ResponseStatus(HttpStatus.OK)
    public String createTask() {
        //PostgresTaskManagerImpl.getPostgresTaskManagerImpl().createTaskTable();
        //PostgresUserManagerImpl.getPostgresUserManagerImpl().createUserTable();
        return "Database Tables created";
    }
}