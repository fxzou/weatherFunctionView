package cn.izouxiang.myview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FunctionView fv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fv = (FunctionView) findViewById(R.id.fv);
        List<FunctionView.Element> elems = new ArrayList<>();
        elems.add(new FunctionView.Element(10f,8f,"周一"));
        elems.add(new FunctionView.Element(12f,6f,"周二"));
        elems.add(new FunctionView.Element(11f,9f,"周三"));
        elems.add(new FunctionView.Element(14f,9f,"周四"));
        elems.add(new FunctionView.Element(20f,6f,"周五"));
        fv.setElements(elems);
    }
    public void onClick(View view){
//        List<FunctionView.Element> elems = new ArrayList<>();
//        elems.add(new FunctionView.Element(12f,6f));
//        elems.add(new FunctionView.Element(10f,8f));
//        elems.add(new FunctionView.Element(20f,6f));
//        elems.add(new FunctionView.Element(11f,9f));
//        fv.setmHeightCircleColor(Color.BLACK);
//        fv.setmTextColor(Color.WHITE);
//        fv.setmTextSize(20);
//        fv.setElements(elems);
        fv.startAnimation();
    }
}
