package my.mgw.dianaccount.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

//类别
@Entity
public class Category {

    @Id(autoincrement = true)
    private Long id;

    //名称
    @Unique
    private String text;

    @Generated(hash = 2065566928)
    public Category(Long id, String text) {
        this.id = id;
        this.text = text;
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

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
