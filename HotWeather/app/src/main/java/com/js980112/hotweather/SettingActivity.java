package com.js980112.hotweather;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class SettingActivity extends AppCompatActivity {
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    Switch swWidget;

    Spinner spSido,spGungu;

    ArrayAdapter<CharSequence> adSpin1,adSpin2;

    SeekBar skPok,skDu,skJuk,skSal,skChu,skHan;
    EditText etPok,etDu,etJuk,etSal,etChu,etHan;
    EditText etPok2,etDu2,etJuk2,etSal2,etChu2,etHan2;

    boolean isChange=false;

    NotificationSet notificationSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setting=getSharedPreferences("setting",MODE_PRIVATE);

        spGungu=findViewById(R.id.spGungu);
        spSido=findViewById(R.id.spSido);

        swWidget=findViewById(R.id.swWidget);

        skPok=findViewById(R.id.skPok);
        skDu=findViewById(R.id.skDu);
        skJuk=findViewById(R.id.skJuk);
        skSal=findViewById(R.id.skSal);
        skChu=findViewById(R.id.skChu);
        skHan=findViewById(R.id.skHan);

        etPok=findViewById(R.id.etPok);
        etDu=findViewById(R.id.etDu);
        etJuk=findViewById(R.id.etJuk);
        etSal=findViewById(R.id.etSal);
        etChu=findViewById(R.id.etChu);
        etHan=findViewById(R.id.etHan);

        etPok2=findViewById(R.id.etPok2);
        etDu2=findViewById(R.id.etDu2);
        etJuk2=findViewById(R.id.etJuk2);
        etSal2=findViewById(R.id.etSal2);
        etChu2=findViewById(R.id.etChu2);
        etHan2=findViewById(R.id.etHan2);

        notificationSet=new NotificationSet(this);

        skPok.setOnSeekBarChangeListener(new SeekListener("Pok",etPok));
        skDu.setOnSeekBarChangeListener(new SeekListener("Du",etDu));
        skJuk.setOnSeekBarChangeListener(new SeekListener("Juk",etJuk));
        skSal.setOnSeekBarChangeListener(new SeekListener("Sal",etSal));
        skChu.setOnSeekBarChangeListener(new SeekListener("Chu",etChu));
        skHan.setOnSeekBarChangeListener(new SeekListener("Han",etHan));

        skPok.setProgress(setting.getInt("tempPok",34)+30);
        skDu.setProgress(setting.getInt("tempDu",30)+30);
        skJuk.setProgress(setting.getInt("tempJuk",25)+30);
        skSal.setProgress(setting.getInt("tempSal",10)+30);
        skChu.setProgress(setting.getInt("tempChu",0)+30);
        skHan.setProgress(setting.getInt("tempHan",-12)+30);

        etPok2.setText(setting.getString("strPok","덥다더워"));
        etDu2.setText(setting.getString("strDu","조급덥다"));
        etJuk2.setText(setting.getString("strJuk","살만하다"));
        etSal2.setText(setting.getString("strSal","쌀쌀하다"));
        etChu2.setText(setting.getString("strChu","춥다"));
        etHan2.setText(setting.getString("strHan","얼어죽겠네"));


        etPok2.addTextChangedListener(new TextChange("Pok",etPok2));
        etDu2.addTextChangedListener(new TextChange("Du",etDu2));
        etJuk2.addTextChangedListener(new TextChange("Juk",etJuk2));
        etSal2.addTextChangedListener(new TextChange("Sal",etSal2));
        etChu2.addTextChangedListener(new TextChange("Chu",etChu2));
        etHan2.addTextChangedListener(new TextChange("Han",etHan2));


        adSpin1=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_do,R.layout.support_simple_spinner_dropdown_item);
        //adSpin1.setDropDownViewResource();
        spSido.setAdapter(adSpin1);

        spSido.setSelection(setting.getInt("pos1",0));

        swWidget.setChecked(setting.getBoolean("widget",true));

        if(setting.getBoolean("widget",true))
            notificationSet.notificationSomethings();
        else
            notificationSet.cancel();

        swWidget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    notificationSet.notificationSomethings();
                else
                    notificationSet.cancel();
                editor=setting.edit();
                editor.putBoolean("widget",b);
                editor.apply();
            }
        });

        spSido.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editor=setting.edit();
                editor.putString("area1",adSpin1.getItem(i).toString());
                editor.putInt("pos1",i);
                switch (adSpin1.getItem(i).toString()){
                    case "서울특별시":
                        adSpin2=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_seoul,R.layout.support_simple_spinner_dropdown_item);
                        break;
                    case "부산광역시":
                        adSpin2=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_busan,R.layout.support_simple_spinner_dropdown_item);
                        break;
                    case "대구광역시":
                        adSpin2=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_daegu,R.layout.support_simple_spinner_dropdown_item);
                        break;
                    case "인천광역시":
                        adSpin2=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_incheon,R.layout.support_simple_spinner_dropdown_item);
                        break;
                    case "광주광역시":
                        adSpin2=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_gwang,R.layout.support_simple_spinner_dropdown_item);
                        break;
                    case "대전광역시":
                        adSpin2=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_daejeon,R.layout.support_simple_spinner_dropdown_item);
                        break;
                    case "울산광역시":
                        adSpin2=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_ulsan,R.layout.support_simple_spinner_dropdown_item);
                        break;
                    case "경기도":
                        adSpin2=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_keong,R.layout.support_simple_spinner_dropdown_item);
                        break;
                    case "강원도":
                        adSpin2=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_kwangwon,R.layout.support_simple_spinner_dropdown_item);
                        break;
                    case "충청북도":
                        adSpin2=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_chungbuk,R.layout.support_simple_spinner_dropdown_item);
                        break;
                    case "충청남도":
                        adSpin2=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_chungnam,R.layout.support_simple_spinner_dropdown_item);
                        break;
                    case "전라북도":
                        adSpin2=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_junbuk,R.layout.support_simple_spinner_dropdown_item);
                        break;
                    case "전라남도":
                        adSpin2=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_junnam,R.layout.support_simple_spinner_dropdown_item);
                        break;
                    case "경상북도":
                        adSpin2=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_gyeongbuk,R.layout.support_simple_spinner_dropdown_item);
                        break;
                    case "경상남도":
                        adSpin2=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_gyeongnam,R.layout.support_simple_spinner_dropdown_item);
                        break;
                    case "제주도":
                        adSpin2=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_jeju,R.layout.support_simple_spinner_dropdown_item);
                        break;
                    case "세종특별자치시":
                        adSpin2=ArrayAdapter.createFromResource(getApplicationContext(),R.array.spinner_sejong,R.layout.support_simple_spinner_dropdown_item);
                        break;
                }
                spGungu.setAdapter(adSpin2);
                //oast.makeText(getApplicationContext(),isChange+" ",Toast.LENGTH_SHORT).show();
                if(!isChange) {
                    spGungu.setSelection(setting.getInt("pos2", 0));
                    isChange=true;
                }
                else
                    spGungu.setSelection(0);

                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spGungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editor=setting.edit();
                editor.putString("area2",adSpin2.getItem(i).toString());
                editor.putInt("pos2",i);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    private class SeekListener implements SeekBar.OnSeekBarChangeListener{
        String type;
        EditText et;
        int t;
        SeekListener(String type,EditText et){
            this.type="temp"+type;
            this.et=et;
        }
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            editor=setting.edit();
            editor.putInt(type,(i-30));
            et.setText((i-30)+"");
            t=i;
            editor.apply();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //Toast.makeText(getApplicationContext(),setting.getInt(type,0)+" "+t,Toast.LENGTH_SHORT).show();

        }
    }

    private class TextChange implements TextWatcher{
        EditText editText;
        String type;
        public TextChange(String type,EditText editText){
            this.editText=editText;
            this.type="str"+type;
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            editor=setting.edit();
            editor.putString(type,editText.getText().toString());
            editor.apply();
        }
    }
}
