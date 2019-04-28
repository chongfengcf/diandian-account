package my.mgw.dianaccount.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;


//账单
@Entity
public class Bill{

    @Id(autoincrement = true)
    private Long id;

    //名称
    private String category;

    //是否为支持,true为支出,false为收入
    private boolean pay;

    //金额
    private double money;

    //时间
    private String date;

    //备注
    private String text;

    @Generated(hash = 24139183)
    public Bill(Long id, String category, boolean pay, double money, String date,
            String text) {
        this.id = id;
        this.category = category;
        this.pay = pay;
        this.money = money;
        this.date = date;
        this.text = text;
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

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean getPay() {
        return this.pay;
    }

    public void setPay(boolean pay) {
        this.pay = pay;
    }

    public double getMoney() {
        return this.money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
