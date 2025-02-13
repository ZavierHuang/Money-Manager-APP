package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class month_statistics extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    static final String DB_NAME = "ProjectDB";      //建立資料庫
    static final String TB_NAME = "PTB7";           //建立資料表

    SQLiteDatabase db;      //資料庫
    Cursor cur;            //資料庫的指標
    NavigationView navigationview;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    ImageButton left_pic,right_pic;
    TextView top_month,top_year;
    int total=0;
    Map<String,Integer> treemap = new TreeMap<String,Integer>();
    List<DataEntry> dataEntries = new ArrayList<>();
    AnyChartView anychartview;
    Pie pie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_statistics);

        System.out.println("Month");
        setting_id();

        Intent intent = getIntent();
        String get_top_year = intent.getStringExtra("top_year");
        String get_top_month = intent.getStringExtra("top_month");

        System.out.println("get:"+get_top_year+" "+get_top_month);

        top_year.setText(get_top_year);
        top_month.setText(get_top_month);

        left_pic.setOnClickListener(this);
        right_pic.setOnClickListener(this);
        pie = AnyChart.pie();

        drawer_setting();
        //創建資料庫
        db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null);

        //創建資料表
        String createTable = "CREATE TABLE IF NOT EXISTS "+TB_NAME+
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "year VARCHAR(32),"+
                "month VARCHAR(32),"+
                "day VARCHAR(32),"+
                "type VARCHAR(32),"+
                "price VARCHAR(32),"+
                "state VARCHAR(32),"+
                "remark VARCHAR(32))";



        db.execSQL(createTable);
        cur = db.rawQuery("SELECT * FROM "+TB_NAME,null);


        search_all();
        anychartview.setChart(pie);
        setupPieChart(top_year.getText().toString(),top_month.getText().toString());




    }

    private void setting_id() {
        left_pic = this.findViewById(R.id.left_pic);
        right_pic = this.findViewById(R.id.right_pic);
        top_month = this.findViewById(R.id.top_month);
        top_year = this.findViewById(R.id.top_year);
        anychartview = this.findViewById(R.id.any_chart);

    }

    private void search_all() {
        cur = db.rawQuery("SELECT * FROM "+TB_NAME,null);
        if(cur.moveToFirst()) {
            String str = "(cur)總共有" + cur.getCount() + "筆資料\n";
            str += "-----\n";

            do {
                str += "year:" + cur.getString(1);
                str += " month:" + cur.getString(2);
                str += " day:" + cur.getString(3);
                str += " type:" + cur.getString(4);
                str += " price:" + cur.getString(5);
                str += " state:" + cur.getString(6) + '\n';
                str += " remark:" + cur.getString(7) + '\n';
                str += "----\n";
            } while (cur.moveToNext());
            System.out.println(str);
        }
    }

    private void search_fit(String year,String month) {
        System.out.println("year:"+year+" month:"+month);
        cur = db.rawQuery("SELECT * FROM "+TB_NAME,null);
        if(cur.moveToFirst()) {
            do {
                if(year.equals(cur.getString(1))&&month.equals(cur.getString(2))
                    && !cur.getString(6).equals("收入"))
                {
                    String type = cur.getString(4);
                    int price = Integer.parseInt(cur.getString(5));
                    if(treemap.containsKey(type))
                    {
                        treemap.put(type,treemap.get(type)+price);
                    }
                    else
                        treemap.put(type,price);
                    total+=price;
                }
            } while (cur.moveToNext());
        }
        System.out.println("map:"+treemap);
        System.out.println("total:"+total);

    }


    private void setupPieChart(String year,String month)
    {
        search_fit(year,month);
        System.out.println("map.size:"+treemap.size());

        pie.title("Expense:"+total);
        System.out.println("map:"+treemap);
        if(treemap.size()!=0)
        {
            for(Object key : treemap.keySet())
            {
                System.out.println("treemap.get(key):"+treemap.get(key));
                dataEntries.add(new ValueDataEntry((String)key,treemap.get(key)));
            }
        }
        else
        {
            dataEntries.add(new ValueDataEntry("Empty",0));
        }

        System.out.println("(M)dataEntries:"+dataEntries);


        pie.data(dataEntries);


    }

    private void drawer_setting() {
        navigationview = this.findViewById(R.id.navigation_view);
        navigationview.setNavigationItemSelectedListener(this);

        drawer = this.findViewById(R.id.drawer);
        toggle =  new ActionBarDrawerToggle(this,drawer,R.string.open,R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                Intent intent = new Intent();
                intent.setClass(month_statistics.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            case R.id.interval_statistics:
                Intent intent1 = new Intent();
                intent1.putExtra("top_year",top_year.getText().toString());
                intent1.putExtra("top_month",top_month.getText().toString());
                intent1.setClass(month_statistics.this, interval_statistics.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                return true;
            case R.id.month_statistics:
                Intent intent2 = new Intent();
                intent2.putExtra("top_year",top_year.getText().toString());
                intent2.putExtra("top_month",top_month.getText().toString());
                intent2.setClass(month_statistics.this, month_statistics.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.left_pic)
        {
            int Month = Integer.parseInt(top_month.getText().toString());
            int Year = Integer.parseInt(top_year.getText().toString());
            if(Month==1)
            {
                Year=Year-1;
            }
            Month=(Month-1)%13;
            if(Month==0)
                Month=12;
            top_month.setText(Integer.toString(Month));
            top_year.setText(Integer.toString(Year));
            treemap.clear();
            dataEntries.clear();
            total=0;
            setupPieChart(top_year.getText().toString(),top_month.getText().toString());


        }
        else if(view.getId()==R.id.right_pic)
        {
            int Month = Integer.parseInt(top_month.getText().toString());
            int Year = Integer.parseInt(top_year.getText().toString());
            if(Month==12)
            {
                Year=Year+1;
            }
            Month=(Month+1)%13;
            if(Month==0)
                Month=1;
            top_month.setText(Integer.toString(Month));
            top_year.setText(Integer.toString(Year));
            treemap.clear();
            dataEntries.clear();
            total=0;
            setupPieChart(top_year.getText().toString(),top_month.getText().toString());


        }
    }
    protected void onDestroy(){

        super.onDestroy();

        db.close(); //關閉資料庫

    }
}