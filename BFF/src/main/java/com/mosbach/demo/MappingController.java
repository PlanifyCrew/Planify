package com.mosbach.demo;

import com.mosbach.demo.data.api.TaskManager;
import com.mosbach.demo.data.api.UserManager;
import com.mosbach.demo.data.impl.*;
import com.mosbach.demo.model.alexa.AlexaRO;
import com.mosbach.demo.model.alexa.OutputSpeechRO;
import com.mosbach.demo.model.alexa.ResponseRO;
import com.mosbach.demo.model.event.Event;
import com.mosbach.demo.model.event.TokenEvent;
import com.mosbach.demo.model.user.Token;
import com.mosbach.demo.model.user.TokenAnswer;
import com.mosbach.demo.model.user.User;
import com.mosbach.demo.model.task.*;
import com.mosbach.demo.model.user.UserWithName;

import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
//import software.amazon.awssdk.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.sqs.*;
import software.amazon.awssdk.services.sqs.model.*;
import software.amazon.awssdk.regions.Region;

// Variante Postgres
import com.mosbach.demo.data.impl.PostgresTaskManagerImpl;
import com.mosbach.demo.data.impl.PostgresUserManagerImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                new TokenAnswer(token,"200");
    }


    @DeleteMapping(
            path = "/login",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public com.mosbach.demo.model.user.MessageAnswer logOffUser(@RequestBody Token token) {

        Logger myLogger = Logger.getLogger("UserLoggingOff");
        myLogger.info("Received a DELETE request on login with token " + token.getToken());

        boolean couldLogoffUser =
                //userManager.logUserOff(userManager.getUserEmailFromToken(token.getToken()));
                pgUserManager.logUserOff("dummy");

        myLogger.info("User logged off " + couldLogoffUser);

        // Fehlerfall behandeln
        if (!couldLogoffUser) {
            return new com.mosbach.demo.model.user.MessageAnswer("User could not be logged out.");
        }

        return new com.mosbach.demo.model.user.MessageAnswer("User logged out.");
    }


    @PostMapping(
            path = "/user",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public com.mosbach.demo.model.user.MessageAnswer createUser(@RequestBody UserWithName userWithName) {

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
                new com.mosbach.demo.model.user.MessageAnswer("User created.");
    }


    @PostMapping(
            path = "/event",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public com.mosbach.demo.model.user.MessageAnswer addTokenEvent(@RequestBody TokenEvent tokenEvent) {

        Logger myLogger = Logger.getLogger("AddEvent");
        myLogger.info("Received a POST request on event with token " + tokenEvent.getToken());

        int userId = pgUserManager.getUserIdFromToken(tokenEvent.getToken());
        myLogger.info("Found the following userId for this token " + userId);
        if (userId == -1)
            return new com.mosbach.demo.model.user.MessageAnswer("No user found or not logged on.");

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

        return new com.mosbach.demo.model.user.MessageAnswer("Event created.");
    }


    @GetMapping("/event")
    public List<Event> getEventList(@RequestParam(value = "token", defaultValue = "123") String token,
                                    @RequestParam(value = "startDate") LocalDate startDate,
                                    @RequestParam(value = "endDate") LocalDate endDate) {

        Logger myLogger = Logger.getLogger("EventLogger");
        myLogger.info("Received a GET request on event with token " + token);

        int userId = pgUserManager.getUserIdFromToken(token);
        List<com.mosbach.demo.data.api.Event> events = pgEventManager.getAllEventsPerUserId(userId);
        List<Event> result = new ArrayList<>();

        for (com.mosbach.demo.data.api.Event e : events)
            if (e.getDate().isAfter(startDate) && e.getDate().isBefore(endDate)) {
                result.add(new Event(e.getName(), e.getDate(), e.getDescription(), e.getStartTime(), e.getEndTime()));
            }

        return result;
    }


    @PostMapping(
            path = "/task",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public com.mosbach.demo.model.user.MessageAnswer loginUser(@RequestBody TokenTask tokenTask) {

        Logger myLogger = Logger.getLogger("AddTask");
        myLogger.info("Received a POST request on task with token " + tokenTask.getToken());

        String userId = "dummy";
        myLogger.info("Found the following userId for this token " + userId);
        if (userId.equals("NOT-FOUND"))
            return
                    new com.mosbach.demo.model.user.MessageAnswer("No user found or not logged on.");
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
                new com.mosbach.demo.model.user.MessageAnswer("Task created.");
    }
/* 
    @GetMapping("/task")
    public TaskList getTasks(@RequestParam(value = "token", defaultValue = "123") String token) {

        Logger myLogger = Logger.getLogger("TaskLogger");
        myLogger.info("Received a GET request on task with token " + token);

        String userId = userManager.getUserIdFromToken(token);
        List<com.mosbach.demo.data.api.Task> tasks = taskManager.getAllTasksPerUserId(userId);
        List<Task> result = new ArrayList<>();
        for (com.mosbach.demo.data.api.Task t : tasks)
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






    @PostMapping(
            path = "/alexa",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @ResponseStatus(HttpStatus.OK)
    public AlexaRO createTask(@RequestBody AlexaRO alexaRO) {

        Logger.getLogger("MappingController").log(Level.INFO,"MappingController POST /alexa ");
        String outText = "";

        if (alexaRO.getRequest().getType().equalsIgnoreCase("LaunchRequest"))
            outText += "Welcome to the Mosbach Task Organizer. ";

        if (alexaRO.getRequest().getType().equalsIgnoreCase("IntentRequest")
                &&
                (alexaRO.getRequest().getIntent().getName().equalsIgnoreCase("TaskReadIntent"))
        ) {
            List<com.mosbach.demo.data.api.Task> tasks = taskManager.getAllTasksPerEmail("mh@test.com");
            if (!tasks.isEmpty()) {
                outText += "You have to do the following tasks. ";
                int i = 1;
                for (com.mosbach.demo.data.api.Task t : tasks) {
                    outText += "Task Number " + i + " with Name " + t.getName()
                        + " and priority " + t.getPriority() + " . ";
                    i++;
                }
            }
            else outText += "This is your lucky day. You have no tasks to do. ";
        }
        return
                prepareResponse(alexaRO, outText, true);
    }


    @PostMapping(
            path = "/alexa",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public AlexaRO getTasks(@RequestBody AlexaRO alexaRO) {

        String outText = "";


        return alexaRO;
    }

    private AlexaRO prepareResponse(AlexaRO alexaRO, String outText, boolean shouldEndSession) {

        alexaRO.setRequest(null);
        alexaRO.setSession(null);
        alexaRO.setContext(null);
        OutputSpeechRO outputSpeechRO = new OutputSpeechRO();
        outputSpeechRO.setType("PlainText");
        outputSpeechRO.setText(outText);
        ResponseRO response = new ResponseRO(outputSpeechRO, shouldEndSession);
        alexaRO.setResponse(response);
        return alexaRO;
    }

}
