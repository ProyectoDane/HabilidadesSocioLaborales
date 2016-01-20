package com.belatrix.habilidadessociolaborales.datamodelmanagers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.belatrix.habilidadessociolaborales.common.C;
import com.belatrix.habilidadessociolaborales.datamodel.DaoMaster;
import com.belatrix.habilidadessociolaborales.datamodel.DaoSession;

import de.greenrobot.dao.query.QueryBuilder;

public class BaseManager {

    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;
    private Context mContext;

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public DaoMaster getDaoMaster() {
        return mDaoMaster;
    }

    public void setDaoMaster(DaoMaster daoMaster) {
        BaseManager.mDaoMaster = daoMaster;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public void setDaoSession(DaoSession daoSession) {
        BaseManager.mDaoSession = daoSession;
    }

    public BaseManager(Context context) {
        this.mContext = context;
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, C.DB_NAME, null);
        SQLiteDatabase mDb = helper.getWritableDatabase();

        if (mDaoMaster == null) mDaoMaster = new DaoMaster(mDb);

        if (mDaoSession == null) mDaoSession = mDaoMaster.newSession();

        QueryBuilder.LOG_SQL = false;
        QueryBuilder.LOG_VALUES = false;
    }


}
