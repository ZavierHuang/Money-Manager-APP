package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemLongClickListener {
    NavigationView navigationview;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    SQLiteDatabase db;                  //資料庫
    Cursor cur,cur_L;                   //資料庫的指標
    SimpleCursorAdapter adapter;        //用於ListView的版面
    TextView top_month,top_year;        //頂部標題欄
    TextView expense,income,total;
    ListView lv;
    ImageButton left_pic,right_pic;
    String ID_all="";

    ArrayList<String> day_list = new ArrayList<String>();  //蒐集"該月"日期listview準備排列
    ArrayList<String> type_list = new ArrayList<String>();
    ArrayList<String> price_list = new ArrayList<String>();
    ArrayList<String> month_list = new ArrayList<String>();
    ArrayList<String> state_list = new ArrayList<String>();

    static final String DB_NAME = "ProjectDB";      //建立資料庫
    static final String TB_NAME = "PTB7";           //建立資料表
    static final String TB_NAME2 = "LTB7";          //建立ListView資料表
    static final String[] FROM = new String[]{"year","month","day","type","price","state","remark"};  //字串陣列
    static final String[] LIST = new String[]{"month","day","type","price"};  //字串陣列
    private String date_r,year_r,month_r,day_r,type_r,dollar_r,choice_r,remark_r;  //insert
    private String date_u,year_u,month_u,day_u,type_u,dollar_u,choice_u,remark_u;  //update

    int income_cal=0,expense_cal=0,total_cal=0;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setting_id();
        drawer_setting();

        Date date = new Date();
        ZoneId timeZone = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            timeZone = ZoneId.systemDefault();
        }
        LocalDate getLocalDate = date.toInstant().atZone(timeZone).toLocalDate();
        System.out.println("getyear:"+getLocalDate.getYear());
        System.out.println("getmonthValue:"+getLocalDate.getMonthValue());
        top_year.setText(getLocalDate.getYear()+"");
        top_month.setText(getLocalDate.getMonthValue()+"");


        month_list.clear();
        day_list.clear();
        type_list.clear();
        price_list.clear();
        state_list.clear();

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

        String createTable2 = "CREATE TABLE IF NOT EXISTS "+TB_NAME2+
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "month VARCHAR(32),"+
                "day VARCHAR(32),"+
                "type VARCHAR(32),"+
                "price VARCHAR(32))";

        db.execSQL(createTable);
        db.execSQL(createTable2);

        //利用資料庫指標進行搜索All data
        cur = db.rawQuery("SELECT * FROM "+TB_NAME,null);

        //列表
        adapter = new SimpleCursorAdapter(this,
                R.layout.item,cur,LIST,new int[]{R.id.month,R.id.day,R.id.type,R.id.price},0);



        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);

        left_pic.setOnClickListener(this);
        right_pic.setOnClickListener(this);




        redirect();
        requery();



    }

    private void requery() {
        //更新ListView
        cur_L = db.rawQuery("SELECT * FROM "+TB_NAME2,null);
        adapter.changeCursor(cur_L);
    }

    private void search_list() {
        cur_L = db.rawQuery("SELECT * FROM "+TB_NAME2,null);
        if(cur_L.moveToFirst())
        {
            String str2 = "(cur_L)總共有"+ cur_L.getCount()+"筆資料\n";
            str2+="-----\n";
            do {
                str2+=" month:"+ cur_L.getString(1);
                str2+=" day:"+ cur_L.getString(2);
                str2+=" type:"+ cur_L.getString(3);
                str2+=" price:"+ cur_L.getString(4)+'\n';
                str2+="----\n";
                int m = Integer.parseInt(cur_L.getString(1));
            }while(cur_L.moveToNext());
            System.out.println(str2);
        }

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
                str += " state:" + cur.getString(6);
                str += " remark:" + cur.getString(7) + '\n';
                str += "----\n";
            } while (cur.moveToNext());
            System.out.println(str);
        }

    }

    public void addData(String year,String month,String day,String type,String price,String state,String remark) {
        //利用資料庫指標進行搜索Listview
        //cur_L = db.rawQuery("SELECT * FROM "+TB_NAME2,null);
        System.out.println("year:"+year+" month:"+month+" day:"+day+" type:"+type+" price:"+price+" state:"+state+" remark:"+remark);
        search_all();
        ContentValues cv = new ContentValues(7);
        cv.put(FROM[0],year);
        cv.put(FROM[1],month);
        cv.put(FROM[2],day);
        cv.put(FROM[3],type);
        cv.put(FROM[4],price);
        cv.put(FROM[5],state);
        cv.put(FROM[6],remark);
        db.insert(TB_NAME,null,cv);
        //search_all();
        redirect();
    }

    private void redirect() {
        cur = db.rawQuery("SELECT * FROM "+TB_NAME,null);

        month_list.clear();
        day_list.clear();
        type_list.clear();
        price_list.clear();
        state_list.clear();

        System.out.println("before");
        System.out.println("month_list:"+month_list);   //month_list:[12, 12, 12, 12]
        System.out.println("day_list:"+day_list);       //day_list:[12, 12, 11, 12]
        System.out.println("type_list:"+type_list);     //type_list:[薪資, 薪資, 薪資, 薪資]
        System.out.println("state_list:"+state_list);   //state_list:[收入, 收入, 收入, 收入]
        System.out.println("price_list:"+price_list);   //price_list:[50, 50, 100, 500]
        listview_sorting();
        if(cur.moveToFirst())
        {
            do {
                String id_temp = cur.getString(0);
                System.out.println("id_temp:"+id_temp);
                String year_temp = cur.getString(1);
                String month_temp = cur.getString(2);
                String day_temp = cur.getString(3);
                String type_temp = cur.getString(4);
                String price_temp = cur.getString(5);
                String state_temp = cur.getString(6);
                String remark_temp = cur.getString(7);
                System.out.println("price_temp:"+price_temp+" state_temp:"+state_temp+" remark_temp:"+remark_temp);

                if(year_temp.equals(top_year.getText().toString())&&month_temp.equals(top_month.getText().toString()))
                {
                    month_list.add(month_temp);
                    day_list.add(day_temp);
                    type_list.add(type_temp);
                    price_list.add(price_temp);
                    state_list.add(state_temp);
                }
            }while(cur.moveToNext());
        }
        System.out.println("month_list:"+month_list);   //month_list:[12, 12, 12, 12]
        System.out.println("day_list:"+day_list);       //day_list:[12, 12, 11, 12]
        System.out.println("type_list:"+type_list);     //type_list:[薪資, 薪資, 薪資, 薪資]
        System.out.println("state_list:"+state_list);   //state_list:[收入, 收入, 收入, 收入]
        System.out.println("price_list:"+price_list);   //price_list:[50, 50, 100, 500]
        listview_sorting();


    }

    private void listview_sorting() {
        String str = "DROP TABLE "+TB_NAME2;
        db.execSQL(str);
        String createTable2 = "CREATE TABLE IF NOT EXISTS "+TB_NAME2+
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "month VARCHAR(32),"+
                "day VARCHAR(32),"+
                "type VARCHAR(32),"+
                "price VARCHAR(32))";
        db.execSQL(createTable2);
        requery();

        System.out.println("day_list:"+day_list);
        int size = day_list.size();
        int minimum_date = 31;
        int maximum_date = 1;

        for(int i=0;i<size;i++)
        {
            int d = Integer.parseInt(day_list.get(i));
            if(d<minimum_date)
                minimum_date=d;
        }
        for(int i=0;i<size;i++)
        {
            int d = Integer.parseInt(day_list.get(i));
            if(d>maximum_date)
                maximum_date=d;
        }

        System.out.println("size:"+size);
        expense_cal=0;
        income_cal=0;
        System.out.println("minimum_date:"+minimum_date);
        System.out.println("maximum_date:"+maximum_date);
        search_list();

        for(int i=minimum_date;i<=maximum_date;i++)
        {
            System.out.println("i:"+i);
            for(int j=0;j<size;j++)
            {
                int day_int = Integer.parseInt(day_list.get(j));
                if(day_int==i)
                {
                    System.out.println("day_int:"+day_int+" i:"+i);
                    ContentValues cv =  new ContentValues();
                    cv.put(LIST[0],month_list.get(j));
                    cv.put(LIST[1],day_list.get(j));
                    cv.put(LIST[2],type_list.get(j));
                    cv.put(LIST[3],price_list.get(j));
                    System.out.println("state_temp:"+state_list.get(j));
                    int number = Integer.parseInt(price_list.get(j));
                    System.out.println("number:"+number);


                    String result = state_list.get(j);

                    System.out.println("result:"+result);

                    if(result.equals(" 支出")||result.equals("支出"))  //state_temp.len=3 , Expense.len=2 , result1=1
                    {
                        System.out.println("number:"+number+" b_expense:"+expense_cal);
                        expense_cal=expense_cal+number;
                    }
                    else if(result.equals(" 收入")||result.equals("收入"))
                    {
                        System.out.println("number:"+number+" b_income:"+income_cal);
                        income_cal+=number;
                    }
                    else
                        System.out.println("other");
                    db.insert(TB_NAME2,null,cv);
                }
            }
        }

        search_list();
        total_cal = income_cal-expense_cal;

        System.out.println("expense_cal:"+expense_cal+" income_cal:"+income_cal);
        expense.setText("$"+expense_cal);
        income.setText("$"+income_cal);
        total.setText("$"+(income_cal-expense_cal));

        search_list();
        requery();  //更新listview

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

    private void setting_id() {
        lv = (ListView)findViewById(R.id.lv);
        drawer = this.findViewById(R.id.drawer);
        top_month = this.findViewById(R.id.top_month);
        top_year = this.findViewById(R.id.top_year);
        lv = this.findViewById(R.id.lv);
        left_pic = this.findViewById(R.id.left_pic);
        right_pic = this.findViewById(R.id.right_pic);
        expense = this.findViewById(R.id.expense);
        income = this.findViewById(R.id.income);
        total = this.findViewById(R.id.total);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.insert)
        {
            Intent intent = new Intent(this,AddData.class);
            intent.putExtra("top_month",top_month.getText().toString());
            intent.putExtra("top_year",top_year.getText().toString());
            startActivityForResult(intent,100);
        }
        if(toggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if(requestCode==100)
            {
                Bundle bag = data.getExtras();
                date_r = bag.getString("date");
                type_r = bag.getString("type");
                dollar_r = bag.getString("dollar");
                choice_r = bag.getString("choice");
                remark_r = bag.getString("remark");

                int index_y = date_r.indexOf("年");
                int index_m = date_r.indexOf("月");
                int index_d = date_r.indexOf("日");
                year_r = date_r.substring(0, index_y);
                month_r = date_r.substring(index_y+1,index_m);
                day_r = date_r.substring(index_m+1, index_d);

                System.out.println("MainActivity:");
                System.out.println("date_r"+date_r);
                System.out.println("type_r:"+type_r);
                System.out.println("dollar_r:"+dollar_r);
                System.out.println("choice_r:"+choice_r);
                System.out.println("remark_r:"+remark_r);
                addData(year_r,month_r,day_r,type_r,dollar_r,choice_r,remark_r);
            }
            if(requestCode==200)
            {
                Bundle bag = data.getExtras();
                date_u = bag.getString("date");
                type_u = bag.getString("type");
                dollar_u = bag.getString("dollar");
                choice_u = bag.getString("choice");
                remark_u = bag.getString("remark");

                int index_y = date_u.indexOf("年");
                int index_m = date_u.indexOf("月");
                int index_d = date_u.indexOf("日");
                year_u = date_u.substring(0, index_y);
                month_u = date_u.substring(index_y+1,index_m);
                day_u = date_u.substring(index_m+1, index_d);

                System.out.println("MainActivity:");
                System.out.println("date_u"+date_u);
                System.out.println("type_u:"+type_u);
                System.out.println("dollar_u:"+dollar_u);
                System.out.println("choice_u:"+choice_u);
                System.out.println("remark_u:"+remark_u);

                Update(ID_all);

            }
        }
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //編輯
        System.out.println("id:"+id);
        cur_L = db.rawQuery("SELECT * FROM "+TB_NAME2,null);
        cur = db.rawQuery("SELECT * FROM "+TB_NAME,null);

        String ID="";
        String year = top_year.getText().toString();
        String month="";
        String day="";
        String type="";
        String price="";
        String remark="";

        if(cur_L.moveToFirst())
        {
            String str2 = "(cur_L)總共有"+ cur_L.getCount()+"筆資料\n";
            str2+="-----\n";
            do {
                str2+=" id:"+cur_L.getString(0);
                str2+=" month:"+ cur_L.getString(1);
                str2+=" day:"+ cur_L.getString(2);
                str2+=" type:"+ cur_L.getString(3);
                str2+=" price:"+ cur_L.getString(4)+'\n';
                str2+="----\n";
                ID = cur_L.getString(0);
                month = cur_L.getString(1);
                day = cur_L.getString(2);
                type = cur_L.getString(3);
                price = cur_L.getString(4);
                if(Integer.parseInt(ID)==id)
                    break;
            }while(cur_L.moveToNext());
            System.out.println(str2);
        }

        if(cur.moveToFirst()) {
            String str = "(cur)總共有" + cur.getCount() + "筆資料\n";
            str += "-----\n";
            do {
                str+=" id:"+cur.getString(0);
                str+=" year:"+ cur.getString(1);
                str+=" month:"+ cur.getString(2);
                str+=" day:"+ cur.getString(3);
                str+=" type:"+ cur.getString(4);
                str+=" price:"+ cur.getString(5);
                str+=" remark:"+ cur.getString(7)+'\n';
                str+="----\n";
                if(year.equals(cur.getString(1))&&
                    month.equals(cur.getString(2))&&
                    day.equals(cur.getString(3))&&
                    type.equals(cur.getString(4))&&
                    price.equals(cur.getString(5)))
                {
                    remark = cur.getString(7);
                    ID_all = cur.getString(0);
                }

            } while (cur.moveToNext());
            System.out.println(str);
        }

        System.out.println("year:"+top_year.getText().toString());
        System.out.println("month:"+month);
        System.out.println("day:"+day);
        System.out.println("type:"+type);
        System.out.println("price:"+price);
        System.out.println("remark:"+remark);
        System.out.println("ID:"+ID);
        System.out.println("ID_all:"+ID_all);

        Intent intent = new Intent(this,EditData.class);
        Bundle bag = new Bundle();
        bag.putString("year",top_year.getText().toString());
        bag.putString("month",month);
        bag.putString("day",day);
        bag.putString("type",type);
        bag.putString("price",price);
        bag.putString("remark",remark);
        intent.putExtras(bag);
        startActivityForResult(intent,200);



    }

    private void Update(String id_all) {
        int id = Integer.parseInt(id_all);
        ContentValues cv = new ContentValues(3);
        cv.put(FROM[0],year_u);
        cv.put(FROM[1],month_u);
        cv.put(FROM[2],day_u);
        cv.put(FROM[3],type_u);
        cv.put(FROM[4],dollar_u);
        cv.put(FROM[5],choice_u);
        cv.put(FROM[6],remark_u);
        System.out.println("準備更新");
        db.update(TB_NAME, cv,"_id="+id,null);    //更新id上的紀錄
        redirect();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        System.out.println("item id:"+item.getItemId());
        switch (item.getItemId()) {
            case R.id.nav_home:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            case R.id.interval_statistics:
                Intent intent1 = new Intent();
                intent1.putExtra("top_year",top_year.getText().toString());
                intent1.putExtra("top_month",top_month.getText().toString());
                intent1.setClass(MainActivity.this, interval_statistics.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                return true;
            case R.id.month_statistics:
                Intent intent2 = new Intent();
                intent2.putExtra("top_year",top_year.getText().toString());
                intent2.putExtra("top_month",top_month.getText().toString());
                intent2.setClass(MainActivity.this, month_statistics.class);
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

            income_cal=0;
            expense_cal=0;
            total_cal=0;
            redirect();
            day_list.clear();
            type_list.clear();
            price_list.clear();
            month_list.clear();
            state_list.clear();
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

            income_cal=0;
            expense_cal=0;
            total_cal=0;
            redirect();
            day_list.clear();
            type_list.clear();
            price_list.clear();
            month_list.clear();
            state_list.clear();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        final int which_item = position;
        System.out.println("Position:"+position);

        int index=1;
        int delete_list_id=-1;      // Listview table
        int want_delete_id=-1;      // all data table

        String price_L="";
        String month_L="";
        String day_L="";
        String type_L="";
        String year_L=top_year.getText().toString();
        String remark_F="";



        if(cur_L.moveToFirst())
        {
            do {
                if(index==position+1)
                {
                    System.out.println("index:"+index);
                    month_L = cur_L.getString(1);
                    day_L = cur_L.getString(2);
                    type_L = cur_L.getString(3);
                    price_L = cur_L.getString(4);
                    delete_list_id = cur_L.getInt(0);
                    search_list();
                    requery();
                    break;
                }
                index++;
            }while(cur_L.moveToNext());
        }
        /*****************************************************************/
        String standard="";
        standard += "Date: "+year_L+'/';
        standard +=  month_L+'/';
        standard +=  day_L+'\n';
        standard +=  "Type: "+type_L+'\n';
        standard +=  "Price: $"+price_L+'\n';
        System.out.println("standard:"+standard);

        cur = db.rawQuery("SELECT * FROM "+TB_NAME,null);
        if(cur.moveToFirst()) {
            String str ="";
            do {
                str += "Date: "+cur.getString(1)+'/';
                str += cur.getString(2)+'/';
                str += cur.getString(3)+'\n';
                str += "Type: "+cur.getString(4)+'\n';
                str += "Price: $"+cur.getString(5)+'\n';
                System.out.println("s:"+str);
                if(str.compareTo(standard)==0)
                {
                    System.out.println("find");
                    remark_F = cur.getString(7);
                    want_delete_id = cur.getInt(0);
                    break;
                }
                str="";
            } while (cur.moveToNext());

        }

//        System.out.println("want_delete_id:"+want_delete_id);
//        System.out.println("remark_F:"+remark_F);
//        System.out.println("standard:"+standard);
//        System.out.println("處理前:search_all");
//        search_all();

        int finalWant_delete_id = want_delete_id;
        int finalDelete_list_id = delete_list_id;
        String finalStandard = standard+"Remark: "+remark_F+'\n';
        String finalType_L = type_L;
        String finalPrice_L = price_L;

//        System.out.println("finalWant_delete_id:"+finalWant_delete_id);
//        System.out.println("finalStandard:"+finalStandard);
//        System.out.println("finalType_L:"+finalType_L);
//        System.out.println("finalPrice_L:"+finalPrice_L);

        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Are you sure?")
                .setMessage("Do you want to delete this record?\n\n"+finalStandard)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {

                        System.out.println("compare is 0");
                        db.delete(TB_NAME,"_id="+ finalWant_delete_id,null);
                        db.delete(TB_NAME2,"_id="+ finalDelete_list_id,null);
                        System.out.println("處理後");
                        search_all();

                        //收入支出重整
                        System.out.println("原本的收入:"+income.getText().toString());
                        System.out.println("原本的支出:"+expense.getText().toString());
                        System.out.println("扣除的類別:"+ finalType_L);
                        System.out.println("扣除的金額:"+ finalPrice_L);

                        if(finalType_L.equals("薪資"))
                        {
                            income_cal = income_cal-Integer.parseInt(finalPrice_L);
                            income.setText("$"+income_cal);
                        }
                        else
                        {
                            expense_cal = expense_cal-Integer.parseInt(finalPrice_L);
                            expense.setText("$"+expense_cal);
                        }
                        total_cal = income_cal-expense_cal;
                        total.setText("$"+total_cal);
                        redirect();
                    }

                })
                .setNegativeButton("No",null)
                .show();

        return true;
    }

    protected void onDestroy(){

        super.onDestroy();

        db.close(); //關閉資料庫

    }
}

