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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Calendar;

public class AddData extends AppCompatActivity implements View.OnClickListener {
    TextView add_income,add_expense;
    static String type_income,type_expense;
    EditText main_choice,type,dollars,date,remark;
    View bottomSheetView;
    BottomSheetDialog bottomSheetDialog;
    Button btn_add;
    int year=0,month=0;
    ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        type_income = getString(R.string.salary);
        type_expense= getString(R.string.choose_type);
        setting_id();

        add_income.setOnClickListener(this);
        add_expense.setOnClickListener(this);
        type.setOnClickListener(this);
        dollars.setOnClickListener(this);
        date.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        setting_date_initial_time();
    }


    private void setting_date_initial_time() {
        Intent intent = getIntent();
        year = Integer.parseInt(intent.getStringExtra("top_year"));
        month = Integer.parseInt(intent.getStringExtra("top_month"));
        date.setText(year+"年"+month+"月1日");
    }

    private void setting_id() {
        add_income = this.findViewById(R.id.add_income);
        add_expense = this.findViewById(R.id.add_outcome);
        type = this.findViewById(R.id.type);
        main_choice = this.findViewById(R.id.main_choice);
        dollars = this.findViewById(R.id.dollars);
        date = this.findViewById(R.id.date);
        btn_add = this.findViewById(R.id.btn_add);
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

            DatePickerDialog datePicDlg = new DatePickerDialog(AddData.this,
                    datePickerDlgOnDateSet,     // OnDateSetListener型態的物件
                    year,
                    month-1,
                    1);

            datePicDlg.setCancelable(false);

            datePicDlg.show();
        }
        else if(view.getId()==R.id.type) {
            System.out.println("main_choice:"+main_choice.getText().toString());
            if(main_choice.getText().toString().equals(getString(R.string.expense))) {
                bottomSheetDialog = new BottomSheetDialog(
                        AddData.this, R.style.Base_V14_ThemeOverlay_MaterialComponents_BottomSheetDialog
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
        else if(view.getId()==R.id.btn_add)
        {
            if(type.getText().toString().equals(getString(R.string.choose_type))||dollars.getText().toString().equals(""))
                Toast.makeText(this, "請填寫完整資料\n(日期,類別,金額)", Toast.LENGTH_SHORT).show();
            else
            {
                Intent intent = new Intent();
                String date_r = date.getText().toString();
                String type_r = type.getText().toString();
                String dollar_r = dollars.getText().toString();
                String choice_r = main_choice.getText().toString();
                String remark_r = remark.getText().toString();

                System.out.println("AddData:");
                System.out.println("date_r" + date_r);
                System.out.println("type_r:" + type_r);
                System.out.println("dollar_r:" + dollar_r);
                System.out.println("choice_r:" + choice_r);
                System.out.println("remark_r:" + remark_r);

                Bundle bag = new Bundle();
                bag.putString("date", date_r);
                bag.putString("type", type_r);
                bag.putString("dollar", dollar_r);
                bag.putString("choice", choice_r);
                bag.putString("remark", remark_r);
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