package com.sacredindiagallery.visitorsurvey;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.sacred.gallery.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {

    // 18 Nov 2015
    private String version = "1.0";

    private EditText mFirstName, mLastName, mEmailAdd, mContactNumber, mDateVisit, mHearAbout,mStruckMost,mImprove;
    private Switch mFirstVisit, mReceiveMails;
    private Button mSave, mDiscard;
    private ImageButton mDateSelect;

    private File file_raw = null;
    private FileOutputStream file_output_raw = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirstName = (EditText) findViewById(R.id.edittext_firstname);
        mLastName = (EditText) findViewById(R.id.edittext_lastname);
        mEmailAdd = (EditText) findViewById(R.id.edittext_emailaddress);
        mContactNumber = (EditText) findViewById(R.id.edittext_contact);
        mDateVisit = (EditText) findViewById(R.id.edittext_datevisit);

        mFirstVisit = (Switch) findViewById(R.id.switch_firstvisit);
        mReceiveMails = (Switch) findViewById(R.id.switch_receiveemail);

        mHearAbout = (EditText) findViewById(R.id.editText_hearaboutus);

        mStruckMost  = (EditText) findViewById(R.id.editText_struckMost);
        mImprove  = (EditText) findViewById(R.id.editText_improve);


        mSave = (Button) findViewById(R.id.button_save);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateData()) {
                    saveDataToCSV();
                }
            }
        });

        mDiscard = (Button) findViewById(R.id.button_discard);
        mDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFields();
            }
        });

        mDateSelect = (ImageButton) findViewById(R.id.button_dateselect);
        mDateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                mDateVisit.setText(String.format("%02d", day) + "/" + String.format("%02d", (month + 1)) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });

    }

    private void clearFields() {
        mFirstName.setText("");
        mLastName.setText("");
        mEmailAdd.setText("");
        mContactNumber.setText("");
        mDateVisit.setText("");

        mFirstVisit.setChecked(false);
        mReceiveMails.setChecked(false);
        mHearAbout.setText("");
        mStruckMost.setText("");
        mImprove.setText("");
    }

    private boolean validateData() {
        if (mFirstName.getText().toString().trim().length() == 0) {
            mFirstName.requestFocus();
            Toast.makeText(MainActivity.this, "Please enter First Name.", Toast.LENGTH_SHORT).show();
            setCompulsory(mFirstName);
            return false;
        }

        if (mLastName.getText().toString().trim().length() == 0) {
            mLastName.requestFocus();
            Toast.makeText(MainActivity.this, "Please enter Last Name.", Toast.LENGTH_SHORT).show();
            setCompulsory(mLastName);
            return false;
        }

        /**
        String emailAddress = mEmailAdd.getText().toString().trim();
        if (emailAddress.length() == 0 || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            mEmailAdd.requestFocus();
            Toast.makeText(MainActivity.this, "Please enter valid email address.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mContactNumber.getText().toString().trim().length() != 0 && mContactNumber.getText().toString().trim().length() < 10) {
            mContactNumber.requestFocus();
            Toast.makeText(MainActivity.this, "Please enter valid contact number.", Toast.LENGTH_SHORT).show();
            return false;
        }
        */


        if (mDateVisit.getText().toString().trim().length() == 0) {
            mDateVisit.requestFocus();
            Toast.makeText(MainActivity.this, "Please enter Date of Visit.", Toast.LENGTH_SHORT).show();
            setCompulsory(mDateVisit);
            return false;
        }

        if (mHearAbout.getText().toString().trim().length() == 0) {
            mHearAbout.requestFocus();
            Toast.makeText(MainActivity.this, "Please enter How did you hear about us.", Toast.LENGTH_SHORT).show();
            setCompulsory(mHearAbout);
            return false;
        }

        if (mStruckMost.getText().toString().trim().length() == 0) {
            mStruckMost.requestFocus();
            Toast.makeText(MainActivity.this, "Please enter What struck you the most.", Toast.LENGTH_SHORT).show();
            setCompulsory(mStruckMost);
            return false;
        }


        return true;
    }

    private void saveDataToCSV() {
        openFileConnection();

        StringBuilder sb = new StringBuilder();
        sb.append(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()) + ",");
        sb.append(mDateVisit.getText().toString() + ",");
        sb.append(mFirstName.getText().toString() + ",");
        sb.append(mLastName.getText().toString() + ",");
        sb.append(mEmailAdd.getText().toString() + ",");
        sb.append(mContactNumber.getText().toString() + ",");
        sb.append((mFirstVisit.isChecked() ? "yes" : "no") + ",");
        sb.append((mReceiveMails.isChecked() ? "yes" : "no") + ",");
        sb.append("\"" + mHearAbout.getText().toString() + "\""+ ",");
        sb.append("\"" + mStruckMost.getText().toString() + "\""+ ",");
        sb.append("\"" + mImprove.getText().toString() + "\"");


        writeToFile(sb.toString() + System.getProperty("line.separator"));

        closeFileConnection();

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("The Sacred India Gallery")
                .setMessage("Thank you for your support, and please tell others about the gallery! \nVisit www.sacredindia.com.au for more information.")

                //.setMessage("<pre style=\"tab-size: 4; color: rgb(0, 0, 0); font-family: Menlo; font-size: 12pt; text-align: center;\">Thank you for your support, and please tell others about the gallery! Visit <a href=\"http://www.sacredindia.com.au\">www.sacredindia.com.au</a> for more information.</pre>")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearFields();
                    }
                }).create().show();
    }

    private boolean openFileConnection() {
        File root = Environment.getExternalStorageDirectory();

        File dir = new File(root.getAbsolutePath() + "/SacredIndiaGallery");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        boolean isFileExists;
        file_raw = new File(dir, "VisitorFeedback.csv");

        if (file_raw.exists()) {
            isFileExists = true;
        } else {
            isFileExists = false;
            try {
                file_raw.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d("SACRED", "TEMP = " + isFileExists);

        try {
            file_output_raw = new FileOutputStream(file_raw, true);

            if (!isFileExists) {
                String header = "Datetime Of Entry,Date Of Visit,First Name,Last Name,Email,Contact Number,Is this your first visit,Would like to receive emails for future events,How did you hear about us?,What struck you the most?,How can we improve?" + System.getProperty("line.separator");
                writeToFile(header);
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    private void writeToFile(String strCSV) {
        Log.d("SACRED", "TEMP = " + strCSV.toString());
        try {
            file_output_raw.write(strCSV.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeFileConnection() {
        try {
            file_output_raw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCompulsory(EditText editText){
        Spannable str = editText.getText();
        int loc = editText.getText().toString().indexOf("*");
        if(loc >= 0) {
            str.setSpan(new ForegroundColorSpan(Color.RED), loc, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            editText.setText(str);
        }

    }
}
