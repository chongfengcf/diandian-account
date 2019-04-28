package my.mgw.dianaccount.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Category {

    @Id(autoincrement = true)
    private Long id;

    //名称
    private String name;

    //是否是支出
    private boolean pay;

    @Generated(hash = 1006556528)
    public Category(Long id, String name, boolean pay) {
        this.id = id;
        this.name = name;
        this.pay = pay;
    }

    @Generated(hash = 1150634039)
    public Category() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getPay() {
        return this.pay;
    }

    public void setPay(boolean pay) {
        this.pay = pay;
    }
    
}
