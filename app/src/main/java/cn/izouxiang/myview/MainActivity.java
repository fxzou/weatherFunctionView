package cn.izouxiang.myview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FunctionView fv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fv = (FunctionView) findViewById(R.id.fv);
        List<FunctionView.Element> elems = new ArrayList<>();
        elems.add(new FunctionView.Element(10,8,"周一","晴"));
        elems.add(new FunctionView.Element(12,6,"周二","晴"));
        elems.add(new FunctionView.Element(11,9,"周三","晴"));
        elems.add(new FunctionView.Element(14,9,"周四","晴"));
        elems.add(new FunctionView.Element(20,6,"周五","晴"));
        elems.add(new FunctionView.Element(11,9,"周三","晴"));
        elems.add(new FunctionView.Element(14,9,"周四","晴"));
        elems.add(new FunctionView.Element(20,6,"周五","晴"));
        fv.setElements(elems);
    }
    public void onClick(View view){
        List<FunctionView.Element> elems = new ArrayList<>();
        elems.add(new FunctionView.Element(12,6,"周一","晴"));
        elems.add(new FunctionView.Element(10,8,"周二","晴"));
        elems.add(new FunctionView.Element(20,6,"周三","晴"));
        elems.add(new FunctionView.Element(11,9,"周四","晴"));
        fv.setElements(elems);
        fv.startAnimation();
    }
}
