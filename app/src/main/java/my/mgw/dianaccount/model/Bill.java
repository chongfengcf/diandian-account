package my.mgw.dianaccount.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import my.mgw.dianaccount.greendao.DaoSession;
import my.mgw.dianaccount.greendao.CategoryDao;
import my.mgw.dianaccount.greendao.BillDao;
import org.greenrobot.greendao.annotation.NotNull;

//账单
@Entity
public class Bill {

    @Id(autoincrement = true)
    private Long id;

    //名称
    private String text;

    //是否为支持,true为支出,false为收入
    private boolean pay;

    //金额
    private long money;

    //日期
    private Date date;

    //外键，类别的ID
    @ToOne(joinProperty = "id")
    private Category categoryId;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 318225070)
    private transient BillDao myDao;

    @Generated(hash = 1247577443)
    public Bill(Long id, String text, boolean pay, long money, Date date) {
        this.id = id;
        this.text = text;
        this.pay = pay;
        this.money = money;
        this.date = date;
    }

    @Generated(hash = 1399599325)
    public Bill() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getPay() {
        return this.pay;
    }

    public void setPay(boolean pay) {
        this.pay = pay;
    }

    public long getMoney() {
        return this.money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Generated(hash = 1308331471)
    private transient Long categoryId__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 505868191)
    public Category getCategoryId() {
        Long __key = this.id;
        if (categoryId__resolvedKey == null
                || !categoryId__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CategoryDao targetDao = daoSession.getCategoryDao();
            Category categoryIdNew = targetDao.load(__key);
            synchronized (this) {
                categoryId = categoryIdNew;
                categoryId__resolvedKey = __key;
            }
        }
        return categoryId;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 802209928)
    public void setCategoryId(Category categoryId) {
        synchronized (this) {
            this.categoryId = categoryId;
            id = categoryId == null ? null : categoryId.getId();
            categoryId__resolvedKey = id;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 173933155)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBillDao() : null;
    }

}
