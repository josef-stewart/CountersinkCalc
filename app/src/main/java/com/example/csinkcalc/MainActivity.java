package com.example.csinkcalc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    // Declare all member variables
    TextView outOfTolerance;
    TextView calcResult;
    TextInputEditText finalResultText;
    TextInputEditText heightFromSurface;
    TextInputEditText nominalInput;
    TextInputEditText tolerancePlus;
    TextInputLayout finalLayout;
    TextInputEditText toleranceMinus;
    Button resetButton;
    Button calcButton;
    AlertDialog allFieldsEmpty;
    AlertDialog calculationError;
    AutoCompleteTextView ballList;
    boolean errors;
    boolean calcError;

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide top bar for cleaner appearance
       getSupportActionBar().hide();



        // Create all fields for DropDown
        String ballArray[] = getResources().getStringArray(R.array.ball_sizes);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, R.layout.dropdown_item, R.id.textView, ballArray);
        ballList = (AutoCompleteTextView) findViewById(R.id.autoCompleteBallSize);
        ballList.setAdapter(arrayAdapter);

        // Instantiate all text fields, buttons, and spinners as variables
        resetButton = (Button) findViewById(R.id.ResetButton);
        outOfTolerance = (TextView) findViewById(R.id.outOfTolerance);
        nominalInput = (TextInputEditText) findViewById(R.id.nominalInputEditText);
        tolerancePlus = (TextInputEditText) findViewById(R.id.plusInputEditText);
        toleranceMinus = (TextInputEditText) findViewById(R.id.minusInputEditText);
        heightFromSurface = (TextInputEditText) findViewById(R.id.heightInputEditText);
        finalResultText = (TextInputEditText) findViewById(R.id.finalResultText);
        finalLayout = (TextInputLayout) findViewById(R.id.finalResultTextLayout);
        calcButton = (Button) findViewById(R.id.CalculateButton);
        calcResult = (TextView) findViewById(R.id.finalResult);

        // Create all fields empty error message
        allFieldsEmpty = new AlertDialog.Builder(this).create();
        allFieldsEmpty.setTitle("Error");
        allFieldsEmpty.setMessage("All fields are empty!");
        allFieldsEmpty.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }});

        // Create a calculation error message
        calculationError = new AlertDialog.Builder(this).create();
        calculationError.setTitle("Calculation Error");
        calculationError.setMessage("Verify input!");
        calculationError.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }});
    }


    // Method for checking if result is in tolerance
    public void checkInTolerance(String result, double nom, double plus, double minus) {

        double resultNum = Double.parseDouble(result);
        double highLimit = nom + plus;
        double lowLimit = nom - minus;

        // Change text color if result is out of tolerance
        if (resultNum > highLimit || resultNum < lowLimit) {
            finalResultText.setTextColor(Color.RED);
            outOfTolerance.setVisibility(View.VISIBLE);
        }
    }

    public void checkCalculation(double result, TextView finalResult) {
        if (result < 0 || result > 1) {
            calculationError.show();
            calcError = true;
        }
        else if (finalResult.getText().toString().equals("-NaN") || finalResult.getText().toString().equals("NaN")) {
            calculationError.show();
            calcError = true;
        }
        else {
            calcError = false;
        }
    }

    // Method for checking for invalid user input
    public void checkForEmptyFields(String height, String nominal, String plus, String minus, String ballSize) {

        if (nominal.isEmpty() && height.isEmpty() && plus.isEmpty() && minus.isEmpty() && ballSize.equals("")) {
            allFieldsEmpty.show();
            errors = true;
        }
        else if (nominal.isEmpty()){
            nominalInput.setError("Required!");
            errors = true;
        }
        else if (plus.isEmpty()) {
            tolerancePlus.setError("Required!");
            errors = true;
        }
        else if (minus.isEmpty()) {
            toleranceMinus.setError("Required!");
            errors = true;
        }
        else if (ballSize.equals("")) {
            ballList.setError("Required!");
            errors = true;
        }
        else if (height.isEmpty()) {
            heightFromSurface.setError("Required!");
            errors = true;
        }
        else {
            errors = false;
        }
    }


    // Full calculation method
    public void calculateOnClick(View view) {

        // Set text fields to number variables
        String heightText = heightFromSurface.getText().toString();
        String nominalText = nominalInput.getText().toString();
        String plusText = tolerancePlus.getText().toString();
        String minusText = toleranceMinus.getText().toString();
        String ballDropDown = ballList.getText().toString();

        // Check for errors
        checkForEmptyFields(heightText, nominalText, plusText, minusText, ballDropDown);

        // If errors are found allow user to change fields, re-run error check
        if (errors) {
            checkForEmptyFields(heightText, nominalText, plusText, minusText, ballDropDown);
        }

        // If no errors are found continue program
        else {
            // Parse Strings to Doubles For Calculations
            double height = Double.parseDouble(heightText);
            double nominal = Double.parseDouble(nominalText);
            double plus = Double.parseDouble(plusText);
            double minus = Double.parseDouble(minusText);
            double ballInDecimal;


            // Convert the Gage Ball Fraction to decimal format
            if (ballDropDown.equals("1")) {
                ballInDecimal = 1;
            }
            else {
            String[] ballFraction = ballDropDown.split("/");
            double numerator = Double.parseDouble(ballFraction[0]);
            double denominator = Double.parseDouble(ballFraction[1]);
            ballInDecimal = numerator / denominator;
            }

            // Two step Calculation
            double calcPartOne = height * (ballInDecimal - height);
            double calcPartTwo = (2 * Math.sqrt(calcPartOne));

            // Format Decimal to 3 places
            DecimalFormat countersink = new DecimalFormat(".###");

            // Show Calculation Result on Screen
            finalResultText.setText(countersink.format(calcPartTwo));

            checkCalculation(calcPartTwo,finalResultText);

            if (calcError) {
                finalResultText.setVisibility(view.GONE);
                checkCalculation(calcPartTwo,finalResultText);
            }


            else {
                ballList.setError(null);
                calcResult.setVisibility(view.VISIBLE);
                finalResultText.setVisibility(view.VISIBLE);
                finalLayout.setVisibility(view.VISIBLE);
                String resultText = finalResultText.getText().toString();

                // Set focus to result to highlight it, but keep uneditable
                finalLayout.requestFocus();
                checkCalculation(calcPartTwo,finalResultText);
                finalResultText.setKeyListener(null);

                // See if result is in tolerance and change text color accordingly
                checkInTolerance(resultText, nominal, plus, minus);

                // Switch button appearance
                calcButton.setVisibility(view.GONE);
                resetButton.setVisibility(view.VISIBLE);

            }
        }

    }

    public void resetOnClick(View view) {
        // Reset all text Fields
        nominalInput.setText("");
        toleranceMinus.setText("");
        tolerancePlus.setText("");
        heightFromSurface.setText("");
        finalResultText.setText("");
        ballList.setText("");
        heightFromSurface.setText("");

        // Change Visibility of Certain Features
        finalLayout.setVisibility(View.GONE);
        finalResultText.setVisibility(View.GONE);
        outOfTolerance.setVisibility(View.GONE);
        calcResult.setVisibility(View.GONE);
        calcButton.setVisibility(view.VISIBLE);
        resetButton.setVisibility(view.GONE);

        // Reset final result text back to default (black)
        finalResultText.setTextColor(Color.BLACK);


    }

}