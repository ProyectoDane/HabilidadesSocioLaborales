package com.belatrix.habilidadessociolaborales.datamodelmanagers;

import android.content.Context;

import com.belatrix.habilidadessociolaborales.datamodel.Log;
import com.belatrix.habilidadessociolaborales.datamodel.LogDao;
import com.belatrix.habilidadessociolaborales.datamodel.Scenario;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class LogManager extends BaseManager {

    private static LogManager sInstance = null;
    private LogDao mLogDao;

    public LogManager(Context context) {
        super(context);
        mLogDao = getDaoSession().getLogDao();
    }

    public static LogManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LogManager(context);
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
        }
        return sInstance;
    }

    public Log getLogById(long id) {
        return mLogDao.queryBuilder().where(LogDao.Properties.Id.eq(id)).unique();
    }

/*    public List<Log> getCorrectAnswersByQuestion(long sessionId, long questionId) {
        return mLogDao.queryBuilder().where(LogDao.Properties.SessionId.eq(sessionId), LogDao.Properties.QuestionId.eq(questionId)).orderDesc(LogDao.Properties.Id).list();
    }*/

    public int getCorrectAnswersByScenario(Scenario scenario,long sessionId) {
        return (int) mLogDao.queryBuilder().where(LogDao.Properties.ScenarioId.eq(scenario.getId()),LogDao.Properties.SessionId.eq(sessionId),LogDao.Properties.IsCorrect.eq(true)).count();
    }

    public long saveLog(Log log) {
        return mLogDao.insertOrReplace(log);
    }

    public List<Log> fetchLogsBySession(long mSessionId) {
        return mLogDao.queryBuilder().where(LogDao.Properties.SessionId.eq(mSessionId)).orderDesc(LogDao.Properties.Id).list();
    }

    public List<Log> fetchLogsByQuestion(long sessionId,long scenarioId, long questionId) {
        return mLogDao.queryBuilder().where(LogDao.Properties.SessionId.eq(sessionId),LogDao.Properties.ScenarioId.eq(scenarioId),LogDao.Properties.QuestionId.eq(questionId)).list();
    }

    public List<Log> fetchLogsByUser(long userId) {
        return mLogDao.queryBuilder().where(LogDao.Properties.User.eq(userId)).list();
    }
}
