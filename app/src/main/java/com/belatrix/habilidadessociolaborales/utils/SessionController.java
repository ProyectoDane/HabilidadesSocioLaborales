package com.belatrix.habilidadessociolaborales.utils;

import com.belatrix.habilidadessociolaborales.datamodel.Session;
import com.belatrix.habilidadessociolaborales.datamodel.User;

import java.util.Date;

public class SessionController {

    private static SessionController sInstance = null;
    private Session mSession = null;

    private SessionController(){

    }

    public static SessionController getInstance(){
        if (sInstance == null){
            sInstance = new SessionController();
        }
        return sInstance;
    }

    public Session getCurrentSession(){
        if (mSession == null){
            mSession = startNewSession(getTestUser());
        }
        return mSession;
    }

    public Session startNewSession(User user){
        mSession = new Session();
        mSession.setUser(user);
        mSession.setStartDate(new Date());

        return mSession;
    }

    private User getTestUser(){
        return null;
    }
}
