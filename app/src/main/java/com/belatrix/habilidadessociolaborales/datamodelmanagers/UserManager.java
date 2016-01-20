package com.belatrix.habilidadessociolaborales.datamodelmanagers;

import android.content.Context;

import com.belatrix.habilidadessociolaborales.datamodel.User;
import com.belatrix.habilidadessociolaborales.datamodel.UserDao;

import java.util.List;

public class UserManager extends BaseManager {

    private static UserManager sInstance = null;

    private UserDao mUserDao;

    private UserManager(Context context) {
        super(context);
        mUserDao = getDaoSession().getUserDao();
    }

    public static UserManager getInstance(Context pContext) {
        if (sInstance == null) {
            sInstance = new UserManager(pContext);
        }
        return sInstance;
    }

    public boolean saveUser(User user) {
        return mUserDao.insert(user) >= 0;
    }

    public List<User> getUsersList() {
        List<User> userList;
        userList = mUserDao.loadAll();
        return userList;
    }

    public User getUser(Long userId) {
        return mUserDao.load(userId);
    }

    public void deleteUser(User user) {
        mUserDao.delete(user);
    }

    public void updateUser(User user) {
        mUserDao.update(user);
    }
}
