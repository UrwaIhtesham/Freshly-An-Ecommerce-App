package com.example.l215404.freshlyanecommerceapp.Activities.SignUp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.l215404.freshlyanecommerceapp.R;

public class CustomerSignUp extends AppCompatActivity {

    private Spinner genderSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_sign_up);


        genderSpinner = findViewById(R.id.genderSpinner);

        String[] data = getResources().getStringArray(R.array.gender_options);

        GenderSpinnerAdapter adapter = new GenderSpinnerAdapter(this, data);
        genderSpinner.setAdapter(adapter);
        genderSpinner.setSelection(0);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) view;
                if (position == 0) {
                    Toast.makeText(CustomerSignUp.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    genderSpinner.setSelection(0);
                    selectedText.setTextColor(0xFF808080);
                } else {
                    String selectedGender = parent.getItemAtPosition(position).toString();
                    Toast.makeText(CustomerSignUp.this, "Selected: "+selectedGender, Toast.LENGTH_SHORT).show();
                    selectedText.setTextColor(0xFF000000);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}