package my.mgw.dianaccount.core;

import android.app.Application;
import android.content.Context;


import org.greenrobot.greendao.database.Database;

import my.mgw.dianaccount.greendao.DaoMaster;
import my.mgw.dianaccount.greendao.DaoSession;

public class MyApplication extends Application {


    private static MyApplication INSTANCE;

    private static Context context;

    private DaoMaster mDaoMaster;

    private DaoSession mDaoSession;


    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        context = getApplicationContext();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "account-db");
        Database db = helper.getWritableDb();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();

    }

    public static Context getContext() {
        return context;
    }

    public static MyApplication getINSTANCE() {
        return INSTANCE;
    }

    public DaoMaster getDaoMaster() {
        return mDaoMaster;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public DaoSession getNewDaoSession() {
        mDaoSession = mDaoMaster.newSession();
        return mDaoSession;
    }
}
