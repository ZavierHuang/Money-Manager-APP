package com.example.project;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class interval_statistics extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    static final String DB_NAME = "ProjectDB";      //建立資料庫
    static final String TB_NAME = "PTB7";           //建立資料表

    SQLiteDatabase db;      //資料庫
    Cursor cur;            //資料庫的指標
    NavigationView navigationview;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    TextView start_date, end_date;
    Button search_btn;
    int start_year = 2022, start_month = 1, start_day = 1;
    int end_year = 2022, end_month = 1, end_day = 1;
    String result[] = new String[5000];
    int index=0;
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    int total = 0;
    Map<String, Integer> treemap = new TreeMap<String, Integer>();
    List<DataEntry> dataEntries = new ArrayList<>();
    AnyChartView anychartview;
    Pie pie;
    String get_top_year="",get_top_month="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interval_statistics);

        setting_id();

        Intent intent = getIntent();
        get_top_year = intent.getStringExtra("top_year");
        get_top_month = intent.getStringExtra("top_month");

        pie = AnyChart.pie();

        drawer_setting();
        //創建資料庫
        db = openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);

        //創建資料表
        String createTable = "CREATE TABLE IF NOT EXISTS " + TB_NAME +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "year VARCHAR(32)," +
                "month VARCHAR(32)," +
                "day VARCHAR(32)," +
                "type VARCHAR(32)," +
                "price VARCHAR(32)," +
                "state VARCHAR(32),"+
                "remark VARCHAR(32))";


        db.execSQL(createTable);
        cur = db.rawQuery("SELECT * FROM " + TB_NAME, null);


        search_all();

        start_date.setOnClickListener(this);
        end_date.setOnClickListener(this);
        search_btn.setOnClickListener(this);
        anychartview.setChart(pie);
    }

    private void setting_id() {

        start_date = this.findViewById(R.id.start_date);
        end_date = this.findViewById(R.id.end_date);
        anychartview = this.findViewById(R.id.any_chart);
        search_btn = this.findViewById(R.id.search_interval);

    }

    private void search_all() {
        cur = db.rawQuery("SELECT * FROM " + TB_NAME, null);
        if (cur.moveToFirst()) {
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

    private void search_fit() throws ParseException {
        System.out.println("start_year:" + start_year + " start_month:" + start_month + " start_day:" + start_day);
        System.out.println("end_year:" + end_year + " end_month:" + end_month + " end_day:" + end_day);
        int cur_year = 0, cur_month = 0, cur_day = 0, cur_price = 0;
        String cur_state = "", cur_type = "";

        String start_t = start_year+"-"+start_month+"-"+start_day;
        String end_t = end_year+"-"+end_month+"-"+end_day;
        try {
            getTimesBetweenDates(start_t,end_t);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cur = db.rawQuery("SELECT * FROM " + TB_NAME, null);
        if (cur.moveToFirst()) {
            do {
                cur_year = Integer.parseInt(cur.getString(1));
                cur_month = Integer.parseInt(cur.getString(2));
                cur_day = Integer.parseInt(cur.getString(3));
                cur_type = cur.getString(4);
                cur_price = Integer.parseInt(cur.getString(5));
                cur_state = cur.getString(6);
                System.out.println(cur_year + "年" + cur_month + "月" + cur_day + "日" + cur_type + " " + cur_price + "元 " + cur_state);
                if (!cur_state.equals("收入"))
                {
                    String cur_t = cur_year+"-"+cur_month+"-"+cur_day;
                    if(find_day_success(cur_t))
                        add_data_in_map(cur_type,cur_price);
                }

            } while (cur.moveToNext());
        }
        System.out.println("map:" + treemap);
        System.out.println("total:" + total);

    }

    private boolean find_day_success(String cur_t) throws ParseException {
        Date cur = dateFormat.parse(cur_t);
        cur_t = dateFormat.format(cur.getTime());
        System.out.println("cur_t:"+cur_t);
        for(int i=0;i<index;i++)
        {
            if(cur_t.equals(result[i]))
                return true;
        }
        return false;
    }

    public void getTimesBetweenDates(String startTime, String endTime) throws ParseException {
        Date start = dateFormat.parse(startTime);
        Date end = dateFormat.parse(endTime);
        System.out.println(startTime + "***" + endTime);

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);
        endCal.add(Calendar.DATE, +1);
        while (startCal.before(endCal)) {
            result[index]=dateFormat.format(startCal.getTime());
            System.out.println("result[index]:"+result[index]);
            startCal.add(Calendar.DAY_OF_YEAR, 1);
            index++;
        }
        for(int i=0;i<index;i++)
            System.out.println(result[i]);
    }

    void add_data_in_map(String cur_type,int cur_price)
    {
        System.out.println("type:"+cur_type);
        if(treemap.containsKey(cur_type))
        {
            treemap.put(cur_type,treemap.get(cur_type)+cur_price);
        }
        else
            treemap.put(cur_type,cur_price);
        total+=cur_price;
    }

    private void setupPieChart() throws ParseException {
        search_fit();
        System.out.println("map.size:"+treemap.size());
        System.out.println("清除");
        pie.title("Expense:"+total);
        System.out.println("Total:"+total);
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

        System.out.println("重建");
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
                intent.setClass(interval_statistics.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            case R.id.interval_statistics:
                Intent intent1 = new Intent();
                intent1.putExtra("top_year",get_top_year);
                intent1.putExtra("top_month",get_top_month);
                intent1.setClass(interval_statistics.this, interval_statistics.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                return true;
            case R.id.month_statistics:
                Intent intent2 = new Intent();
                intent2.putExtra("top_year",get_top_year);
                intent2.putExtra("top_month",get_top_month);
                intent2.setClass(interval_statistics.this, month_statistics.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.start_date) {
            DatePickerDialog datePicDlg = new DatePickerDialog(interval_statistics.this,
                    datePickerDlgOnDateSet,     // OnDateSetListener型態的物件
                    start_year,
                    start_month-1,
                    start_day);

            datePicDlg.setCancelable(false);
            datePicDlg.show();
        }
        else if(view.getId()==R.id.end_date) {
            DatePickerDialog datePicDlg = new DatePickerDialog(interval_statistics.this,
                    datePickerDlgOnDateSet2,     // OnDateSetListener型態的物件
                    end_year,
                    end_month-1,
                    end_day);
            datePicDlg.setCancelable(false);
            datePicDlg.show();

        }
        else if(view.getId()==R.id.search_interval)
        {
            treemap.clear();
            dataEntries.clear();
            Arrays.fill(result,"");
            index=0;
            total=0;
            if(start_date.getText().toString().equals("起始日期")
            ||end_date.getText().toString().equals("終止日期"))
            {
                Toast.makeText(this, "請輸入起始日期和終止日期", Toast.LENGTH_SHORT).show();
            }
            else {
                System.out.println("(S)start_year:" + start_year + " start_month:" + start_month + " start_day:" + start_day);
                System.out.println("(S)end_year:" + end_year + " end_month:" + end_month + " end_day:" + end_day);
                try {
                    Date s_t = dateFormat.parse(start_year+"-"+start_month+"-"+start_day);
                    Date e_t = dateFormat.parse(end_year+"-"+end_month+"-"+end_day);
                    if(s_t.getTime()>e_t.getTime())
                    {
                        Toast.makeText(this, "輸入有誤，請重新輸入", Toast.LENGTH_SHORT).show();
                    }

                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }

                try {
                    setupPieChart();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    private DatePickerDialog.OnDateSetListener datePickerDlgOnDateSet = new DatePickerDialog.OnDateSetListener() {
        @SuppressLint("SetTextI18n")
        public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            start_date.setText(Integer.toString(year) + "/"+
                    Integer.toString(monthOfYear+1) + "/" +
                    Integer.toString(dayOfMonth) + "");
            start_year = year;
            start_month = monthOfYear+1;
            start_day = dayOfMonth;
        }
    };
    private DatePickerDialog.OnDateSetListener datePickerDlgOnDateSet2 = new DatePickerDialog.OnDateSetListener() {
        @SuppressLint("SetTextI18n")
        public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            end_date.setText(Integer.toString(year) + "/"+
                    Integer.toString(monthOfYear+1) + "/" +
                    Integer.toString(dayOfMonth) + "");
            end_year = year;
            end_month = monthOfYear+1;
            end_day = dayOfMonth;
        }
    };
    protected void onDestroy(){

        super.onDestroy();

        db.close(); //關閉資料庫

    }
}