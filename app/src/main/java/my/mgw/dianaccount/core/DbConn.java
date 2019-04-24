package my.mgw.dianaccount.core;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

import my.mgw.dianaccount.greendao.DaoMaster;
import my.mgw.dianaccount.greendao.DaoSession;

//Application是单例（singleton）模式的一个类
//可以确保全局只有一个DbConn对象
public class DbConn extends Application {

    //本类的实例
    //只有一个
//    private static DbConn instance;

    private DaoSession daoSession;

    //覆盖Application的默认创建方法
    @Override
    public void onCreate() {
        super.onCreate();

        //创建或连接SQLite数据库
        //数据库名称为 dian-db
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "dian-db");
        Database db = helper.getWritableDb();

        //建立一个新的数据库会话
        //DaomMster是数据库连接
        //多个会话指的同一个数据库连接
        daoSession = new DaoMaster(db).newSession();


//        instance = this;
    }

    //提供方法让外部的类获取会话
    public DaoSession getDaoSession() {
        return daoSession;
    }

    //提供方法让外部的类获取本类的实例
//    public static DbConn getInstance() {
//        return instance;
//    }
}
