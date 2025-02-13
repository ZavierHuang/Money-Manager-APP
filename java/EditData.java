package com.example.project;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Calendar;

public class EditData extends AppCompatActivity implements View.OnClickListener{
    TextView add_income,add_expense;
    ActionBar actionbar;
    EditText date,type,dollar,main_choice,remark;
    Button store;
    String year,month,day,types,price,remarks;
    View bottomSheetView;
    BottomSheetDialog bottomSheetDialog;
    static String type_income,type_expense;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);



        setting_id();
        Intent intent = getIntent();
        Bundle bag = intent.getExtras();
        year = bag.getString("year");
        month = bag.getString("month");
        day = bag.getString("day");
        types = bag.getString("type");
        price = bag.getString("price");
        remarks = bag.getString("remark");

        type_income = getString(R.string.salary);
        if(types.equals("薪資"))
            type_expense = getString(R.string.choose_type);
        else
            type_expense = types;

        initial_background();
        date.setOnClickListener(this);
        add_expense.setOnClickListener(this);
        add_income.setOnClickListener(this);
        type.setOnClickListener(this);
        dollar.setOnClickListener(this);
        store.setOnClickListener(this);
    }

    private void initial_background() {
        date.setText(year+"年"+month+"月"+day+"日");
        if(types.equals("薪資"))
        {
            add_income.setTextColor(Color.rgb(0,255,255));
            add_expense.setTextColor(Color.rgb(0,0,0));
            main_choice.setText("收入");
            type.setText(type_income);
        }
        else
        {
            add_income.setTextColor(Color.rgb(0,0,0));
            add_expense.setTextColor(Color.rgb(0,255,255));
            main_choice.setText("支出");
            type.setText(type_expense);
        }
        dollar.setText(price);
        remark.setText(remarks);
    }

    private void setting_id() {
        date = this.findViewById(R.id.date);
        type = this.findViewById(R.id.type);
        dollar = this.findViewById(R.id.dollars);
        main_choice = this.findViewById(R.id.main_choice);
        store = this.findViewById(R.id.store);
        add_income = this.findViewById(R.id.add_income);
        add_expense = this.findViewById(R.id.add_outcome);
        remark = this.findViewById(R.id.remark);
        actionbar = getSupportActionBar();
        actionbar.hide();
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.add_income) {
            add_income.setTextColor(Color.rgb(0,255,255));
            add_expense.setTextColor(Color.rgb(0,0,0));
            main_choice.setText(R.string.income);
            type.setText(type_income);
        }
        else if(view.getId()==R.id.add_outcome) {
            add_income.setTextColor(Color.rgb(0,0,0));
            add_expense.setTextColor(Color.rgb(0,255,255));
            main_choice.setText(R.string.expense);
            type.setText(type_expense);
        }
        else if(view.getId()==R.id.date) {

            DatePickerDialog datePicDlg = new DatePickerDialog(EditData.this,
                    datePickerDlgOnDateSet,     // OnDateSetListener型態的物件
                    Integer.parseInt(year),
                    Integer.parseInt(month)-1,
                    Integer.parseInt(day));

            datePicDlg.setCancelable(false);

            datePicDlg.show();
        }
        else if(view.getId()==R.id.type) {
            System.out.println("main_choice:"+main_choice.getText().toString());
            if(main_choice.getText().toString().equals(getString(R.string.expense))) {
                bottomSheetDialog = new BottomSheetDialog(
                        EditData.this, R.style.Base_V14_ThemeOverlay_MaterialComponents_BottomSheetDialog
                );
                bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.bottom_sheet_view,
                                (LinearLayout) findViewById(R.id.bottomSheetContainer)
                        );
                bottomSheetView.findViewById(R.id.breakfast).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        type_expense = getResources().getString(R.string.breakfast);
                        type.setText(type_expense);
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.lunch).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        type_expense = getResources().getString(R.string.lunch);
                        type.setText(type_expense);
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.dinner).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        type_expense = getResources().getString(R.string.dinner);
                        type.setText(type_expense);
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.daily_necessity).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        type_expense = getResources().getString(R.string.daily_necessity);
                        type.setText(type_expense);
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.entertainment).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        type_expense = getResources().getString(R.string.entertainment);
                        type.setText(type_expense);
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.drink).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        type_expense = getResources().getString(R.string.drink);
                        type.setText(type_expense);
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.traffic).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        type_expense = getResources().getString(R.string.traffic);
                        type.setText(type_expense);
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.medical).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        type_expense = getResources().getString(R.string.medical);
                        type.setText(type_expense);
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetView.findViewById(R.id.other).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        type_expense = getResources().getString(R.string.other);
                        type.setText(type_expense);
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

            }

        }
        else if(view.getId()==R.id.store)
        {
            if(type.getText().toString().equals(getString(R.string.choose_type))||dollar.getText().toString().equals(""))
                Toast.makeText(this, "請填寫完整資料\n(日期,類別,金額)", Toast.LENGTH_SHORT).show();
            else
            {
                Intent intent = new Intent();
                String date_u = date.getText().toString();
                String type_u = type.getText().toString();
                String dollar_u = dollar.getText().toString();
                String choice_u = main_choice.getText().toString();
                String remark_u = remark.getText().toString();

                System.out.println("UpdateData:");
                System.out.println("date_u" + date_u);
                System.out.println("type_u:" + type_u);
                System.out.println("dollar_u:" + dollar_u);
                System.out.println("choice_u:" + choice_u);
                System.out.println("remark_u:" + remark_u);

                Bundle bag = new Bundle();
                bag.putString("date", date_u);
                bag.putString("type", type_u);
                bag.putString("dollar", dollar_u);
                bag.putString("choice", choice_u);
                bag.putString("remark", remark_u);
                intent.putExtras(bag);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
    private DatePickerDialog.OnDateSetListener datePickerDlgOnDateSet = new DatePickerDialog.OnDateSetListener() {
        @SuppressLint("SetTextI18n")
        public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            date.setText(Integer.toString(year) + "年"+
                    Integer.toString(monthOfYear + 1) + "月" +
                    Integer.toString(dayOfMonth) + "日");
        }
    };

}