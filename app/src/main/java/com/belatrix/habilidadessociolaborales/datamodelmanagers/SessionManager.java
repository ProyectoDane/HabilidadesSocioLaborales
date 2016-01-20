package com.belatrix.habilidadessociolaborales.datamodelmanagers;

import android.content.Context;

import com.belatrix.habilidadessociolaborales.datamodel.Session;
import com.belatrix.habilidadessociolaborales.datamodel.SessionDao;

import java.util.ArrayList;
import java.util.List;

public class SessionManager extends BaseManager {

    private static SessionManager sInstance = null;
    private SessionDao mSessionDao;

    public SessionManager(Context context) {
        super(context);

        mSessionDao = getDaoSession().getSessionDao();
    }

    public static SessionManager getInstance(Context pContext) {
        if (sInstance == null) {
            sInstance = new SessionManager(pContext);
        }

        return sInstance;
    }

    public ArrayList<Session> fetchSessions() {
        return (ArrayList<Session>) mSessionDao.queryBuilder().orderAsc(SessionDao.Properties.Id).list();
    }

    public Session fetchSessionById(Long sessionId) {
        return mSessionDao.load(sessionId);
    }

    public List<Session> fetchSessionByUser(Long userId) {
        return mSessionDao.queryBuilder().where(SessionDao.Properties.UserId.eq(userId)).orderDesc(SessionDao.Properties.Id).list();
    }

    public long saveSession(Session session) {
        return mSessionDao.insert(session);
    }



    public boolean updateSession(Session session) {
        try {
            mSessionDao.update(session);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean hasOpenedSessions(Long id) {
        return mSessionDao.queryBuilder().where(mSessionDao.queryBuilder().and(SessionDao.Properties.Closed.eq(false),SessionDao.Properties.UserId.eq(id))).count() > 0;
    }

    public long getLastSessionId(Long id) {
        List<Session> sessions = mSessionDao.queryBuilder().where(mSessionDao.queryBuilder().and(SessionDao.Properties.Closed.eq(false),SessionDao.Properties.UserId.eq(id))).list();
        if(sessions.size() > 0) {
            return sessions.get(sessions.size() - 1).getId();
        } else {
            return -1;
        }
    }

    public boolean closeSession(Long id) {
        try {
            Session session = fetchSessionById(id);
            mSessionDao.update(session);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
