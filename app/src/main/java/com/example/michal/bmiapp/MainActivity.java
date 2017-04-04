package com.example.michal.bmiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    boolean meterFlag = true;

    @BindView(R.id.editText2)
    EditText textHeight;

    @BindView(R.id.editText3)
    EditText textWeight;

    @BindView(R.id.textView6)
    TextView valueBMI;

    @BindView(R.id.textView2)
    TextView lengthUnit;

    @BindView(R.id.textView5)
    TextView weightUnit;

    @BindView(R.id.button1)
    Button buttonShowBMI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



        SharedPreferences savedData = getSharedPreferences("savedData",MODE_PRIVATE);
        double weightSaved = (double)savedData.getFloat("weight",-1);
        double heightSaved = (double)savedData.getFloat("height",-1);
        if(weightSaved!=-1.0 && heightSaved!=-1.0){
            textHeight.setText(String.format(Locale.US,"%.2f",heightSaved));
            textWeight.setText(String.format(Locale.US,"%.2f",weightSaved));
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.optionM:
                lengthUnit.setText("m");
                weightUnit.setText("kg");
                meterFlag = true;
                return true;
            case R.id.optionIN:
                lengthUnit.setText("in");
                weightUnit.setText("lbs");
                meterFlag = false;
                return true;
            case R.id.optionAuthor:
                Intent openAboutAuthor = new Intent(this,AboutAuthor.class);
                startActivity(openAboutAuthor);
                return true;
            case R.id.optionSave:
                saveYourData();
                return true;
            case R.id.optionShare:
                shareOption();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void shareOption() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareMessage="Hi! That's my BMI:";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My BMI");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void saveYourData() {
        float weight = (float) getWeight();
        float height = (float) getHeight();

        SharedPreferences savedData = getSharedPreferences("savedData",MODE_PRIVATE);
        SharedPreferences.Editor editSavedData = savedData.edit();
        editSavedData.putFloat("weight",weight);
        editSavedData.putFloat("height",height);
        editSavedData.apply();
        Toast.makeText(this, "Your data is saved."+weight+"||"+height, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("savedBMI", valueBMI.getText().toString());
        savedInstanceState.putInt("savedBMIColor", valueBMI.getTextColors().getDefaultColor());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        valueBMI.setText(savedInstanceState.getString("savedBMI"));
        valueBMI.setTextColor(savedInstanceState.getInt("savedBMIColor"));
    }


    protected double getHeight(){
        double height;
        try{
            height = Double.parseDouble(textHeight.getText().toString());
        }
        catch (NumberFormatException e){
            height = -1.0;
        }

        return height;
    }

    protected double getWeight(){
        double weight;
        try{
            weight = Double.parseDouble(textWeight.getText().toString());
        }
        catch(NumberFormatException e){
            weight = -1.0;
        }

        return weight;

    }

    protected double countBMI(double weight,double height){
        double bmi = weight/(height*height);
        if(meterFlag){
            return bmi;
        }
        else{
            return bmi*703;
        }

    }

//    protected boolean checkIfEmptyFields(){
//        return textHeight.getText().toString().equals("") && textWeight.getText().toString().equals("");
//
//    }
    
    protected boolean checkValues(double weight,double height){
        if(!meterFlag){
            weight*=0.4535;
            height*=0.0254;
        }
        if(weight<0 || height<0){
            Toast.makeText(this, "The information you provided is invalid", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(height>3 && meterFlag){
            Toast.makeText(this, "Put your height in meters", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(weight>500){
            Toast.makeText(this, "Your weight is too big. Is it really correct?", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            return true;
        }
    }

    protected void showBMI(){
        double weight = getWeight();
        double height = getHeight();
        if(checkValues(weight,height)){
            double bmi = countBMI(weight,height);
            valueBMI.setText(String.format(Locale.US,"%.2f", bmi));
            if(bmi<18.5){
                valueBMI.setTextColor(Color.YELLOW);
            }
            else if (bmi>25){
                valueBMI.setTextColor(Color.RED);
            }
            else{
                valueBMI.setTextColor(Color.GREEN);
            }
        }
        


    }

    @OnClick(R.id.button1)
    public void onShowBMIClick(View view){
        showBMI();
    }


}
