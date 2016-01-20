package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;


public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(2, "com.belatrix.habilidadessociolaborales.datamodel");

        schema.enableKeepSectionsByDefault();

        Entity question = schema.addEntity("Question");
        question.addIdProperty();
        //question.addLongProperty("questionId");
        //question.addLongProperty("scenarioId");
        question.addStringProperty("question");
        question.addStringProperty("imageUrl");
        question.addByteArrayProperty("image");
        question.addStringProperty("validAnswer");
        question.addStringProperty("invalidAnswer1");
        question.addStringProperty("invalidAnswer2");
        question.addStringProperty("invalidAnswer3");


        Entity scenario = schema.addEntity("Scenario");
        scenario.addIdProperty();
        //scenario.addLongProperty("scenarioId");
        scenario.addStringProperty("name");
        scenario.addStringProperty("dependencies");
        scenario.addStringProperty("imageUrl");
        scenario.addByteArrayProperty("image");

        Entity user = schema.addEntity("User");
        user.addIdProperty();
        //user.addLongProperty("userId");
        user.addStringProperty("name");
        user.addStringProperty("lastname");
        user.addDateProperty("bornDate");
        user.addByteArrayProperty("image");
        user.addBooleanProperty("sound");

        Entity session = schema.addEntity("Session");
        session.addIdProperty();
        session.addDateProperty("startDate");
        session.addDateProperty("endDate");
        session.addBooleanProperty("closed");

        Entity log = schema.addEntity("Log");
        log.addIdProperty();
        log.addDateProperty("date");
        log.addLongProperty("scenarioId");
        log.addStringProperty("scenarioName");
        log.addLongProperty("questionId");
        log.addStringProperty("question");
        log.addStringProperty("answer");
        log.addStringProperty("answered");
        log.addBooleanProperty("isCorrect");
        log.addLongProperty("user");

        // relation: a question has a scenario
        Property scenarioId = question.addLongProperty("scenarioId").getProperty();
        question.addToOne(scenario, scenarioId);
        // relation: a scenario has many questions
        ToMany scenarioToQuestions = scenario.addToMany(question, scenarioId);
        scenarioToQuestions.setName("questions");


        // relation: a log belongs to a session
        Property sessionId = log.addLongProperty("sessionId").getProperty();
        log.addToOne(session, sessionId);


        //session belongs to a user
        Property userId = session.addLongProperty("userId").getProperty();
        session.addToOne(user, userId);


        // relation: a user has many sessions
        ToMany userToSessions = user.addToMany(log, userId);
        userToSessions.setName("sessions");

        // relation: a session has many logs
        ToMany sessionToLogs = session.addToMany(log, sessionId);
        sessionToLogs.setName("logs");

        new DaoGenerator().generateAll(schema, args[0]);
    }
}


