package my.mgw.dianaccount.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import my.mgw.dianaccount.R;
import my.mgw.dianaccount.core.MyApplication;
import my.mgw.dianaccount.model.Category;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.editText);
        query(null);
    }


    public void add(View view) {
        Category category = new Category();
        category.setText(String.valueOf(System.currentTimeMillis())); //获取随机时间作为名称
        MyApplication.getINSTANCE().getDaoSession().getCategoryDao().insert(category);
        Toast.makeText(this,"在最后增加一条", Toast.LENGTH_LONG).show();
        query(view);
    }

    public void del(View view) {
        Category category = MyApplication.getINSTANCE().getDaoSession().getCategoryDao().loadAll().get(0);
        MyApplication.getINSTANCE().getDaoSession().getCategoryDao().delete(category);
        Toast.makeText(this,"删除最上面一条", Toast.LENGTH_LONG).show();
        query(view);
    }

    public void update(View view) {
        Category category = MyApplication.getINSTANCE().getDaoSession().getCategoryDao().loadAll().get(0);
        category.setText(String.valueOf(System.currentTimeMillis())); //获取随机时间作为名称
        MyApplication.getINSTANCE().getDaoSession().getCategoryDao().update(category);
        Toast.makeText(this,"更新最上面一条", Toast.LENGTH_LONG).show();
        query(view);
    }

    //查询所有
    public void query(View view) {
        List<Category> categoryList = MyApplication.getINSTANCE().getDaoSession().getCategoryDao().loadAll();
        display(categoryList);
    }

    public void display(List<Category> categories) {
        StringBuffer sb = new StringBuffer();
        sb.append("id     |    ");
        sb.append("种类\n");
        sb.append("-------------------------------------------------------\n");
        for(Category i:categories) {
            sb.append(i.getId()+ "     |    " + i.getText()+'\n');
        }
        text.setText(sb.toString());
    }

}
