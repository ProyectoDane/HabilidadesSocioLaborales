package com.belatrix.habilidadessociolaborales.datamodel;

import android.content.Context;
import android.graphics.Bitmap;

import com.belatrix.habilidadessociolaborales.utils.ImageUtils;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table QUESTION.
 */
public class Question {

    private Long id;
    private String question;
    private String imageUrl;
    private byte[] image;
    private String validAnswer;
    private String invalidAnswer1;
    private String invalidAnswer2;
    private String invalidAnswer3;
    private Long scenarioId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient QuestionDao myDao;

    private Scenario scenario;
    private Long scenario__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Question() {
    }

    public Question(Long id) {
        this.id = id;
    }

    public Question(Long id, String question, String imageUrl, byte[] image, String validAnswer, String invalidAnswer1, String invalidAnswer2, String invalidAnswer3, Long scenarioId) {
        this.id = id;
        this.question = question;
        this.imageUrl = imageUrl;
        this.image = image;
        this.validAnswer = validAnswer;
        this.invalidAnswer1 = invalidAnswer1;
        this.invalidAnswer2 = invalidAnswer2;
        this.invalidAnswer3 = invalidAnswer3;
        this.scenarioId = scenarioId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getQuestionDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getValidAnswer() {
        return validAnswer;
    }

    public void setValidAnswer(String validAnswer) {
        this.validAnswer = validAnswer;
    }

    public String getInvalidAnswer1() {
        return invalidAnswer1;
    }

    public void setInvalidAnswer1(String invalidAnswer1) {
        this.invalidAnswer1 = invalidAnswer1;
    }

    public String getInvalidAnswer2() {
        return invalidAnswer2;
    }

    public void setInvalidAnswer2(String invalidAnswer2) {
        this.invalidAnswer2 = invalidAnswer2;
    }

    public String getInvalidAnswer3() {
        return invalidAnswer3;
    }

    public void setInvalidAnswer3(String invalidAnswer3) {
        this.invalidAnswer3 = invalidAnswer3;
    }

    public Long getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Long scenarioId) {
        this.scenarioId = scenarioId;
    }

    /** To-one relationship, resolved on first access. */
    public Scenario getScenario() {
        Long __key = this.scenarioId;
        if (scenario__resolvedKey == null || !scenario__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ScenarioDao targetDao = daoSession.getScenarioDao();
            Scenario scenarioNew = targetDao.load(__key);
            synchronized (this) {
                scenario = scenarioNew;
            	scenario__resolvedKey = __key;
            }
        }
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        synchronized (this) {
            this.scenario = scenario;
            scenarioId = scenario == null ? null : scenario.getId();
            scenario__resolvedKey = scenarioId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    public Bitmap getBitmapImage(Context context) {
        if (getImage() != null) {
            return ImageUtils.getInstance(context).byteArrayToBitmap(getImage());
        } else {
            return null;
        }

    }
    // KEEP METHODS END

}
