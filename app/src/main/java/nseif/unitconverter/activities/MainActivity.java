package nseif.unitconverter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import nseif.unitconverter.R;
import nseif.unitconverter.models.UnitConverter;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static nseif.unitconverter.lib.Utils.showInfoDialog;

public class MainActivity extends AppCompatActivity {
    private UnitConverter mConverter;
    private TextInputLayout mInput;
    private TextInputLayout mOutput;
    private Snackbar mSnackBar;

    private final String mKEY_IS_INPUT_TYPE_KM = "IS_INPUT_TYPE_KM";

    private final String mKEY_INPUT = "INPUT";
    private final String mKEY_OUTPUT = "OUTPUT";

    @Override
    protected void onStop() {
        super.onStop();
        updateSharedPrefs();
    }

    private void updateSharedPrefs() {
        SharedPreferences defaultSharedPreferences = getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        editor.putBoolean(mKEY_IS_INPUT_TYPE_KM, mConverter.getInputType().equals(UnitConverter.InputType.KM));

        editor.apply();

        updateUI();
    }

    @Override
    protected void onStart() {
        super.onStart();

        restoreOrSetFromPreferences();
    }

    private void restoreOrSetFromPreferences() {
        SharedPreferences sp = getDefaultSharedPreferences(this);
        boolean isInputTypeKm = sp.getBoolean(mKEY_IS_INPUT_TYPE_KM, false);
        mConverter.setInputType(isInputTypeKm ? UnitConverter.InputType.KM : UnitConverter.InputType.MILES);
        updateUI();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        EditText inputEditText = mInput.getEditText();
        EditText outputEditText = mOutput.getEditText();

        if (inputEditText != null) {
            outState.putDouble(mKEY_INPUT, Double.parseDouble(inputEditText.getText().toString()));
        }

        if (outputEditText != null) {
            outState.putDouble(mKEY_OUTPUT, Double.parseDouble(outputEditText.getText().toString()));
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mConverter.setInputType(savedInstanceState.getBoolean(mKEY_IS_INPUT_TYPE_KM, false) ? UnitConverter.InputType.KM : UnitConverter.InputType.MILES);
        updateUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupViews();
        setupFAB();
        setupConverter(savedInstanceState);
    }

    private void setupConverter(Bundle savedInstanceState) {
        mConverter = new UnitConverter();

        if (savedInstanceState != null) {
            double savedInput = savedInstanceState.getDouble(mKEY_INPUT);
            double savedOutput = savedInstanceState.getDouble(mKEY_OUTPUT);

            if (savedInput != 0.0) {
                mConverter.setInput(savedInput);
            }

            if (savedOutput != 0.0) {
                mConverter.setOutput(savedOutput);
            }
        }


        SharedPreferences sp = getDefaultSharedPreferences(this);
        boolean isInputTypeKm = sp.getBoolean(mKEY_IS_INPUT_TYPE_KM, false);

        mConverter.setInputType(isInputTypeKm ? UnitConverter.InputType.KM : UnitConverter.InputType.MILES);

        updateUI();
    }

    private void dismissSnackBarIfShown() {
        if (mSnackBar.isShown()) {
            mSnackBar.dismiss();
        }
    }

    private void setupViews() {
        mInput = findViewById(R.id.input);
        mOutput = findViewById(R.id.output);
        mSnackBar = Snackbar.make(findViewById(android.R.id.content), "Welcome!", Snackbar.LENGTH_LONG);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupFAB() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convert();
            }
        });
    }

    private void updateUI() {
        dismissSnackBarIfShown();

        EditText inputEditText = mInput.getEditText();
        EditText outputEditText = mOutput.getEditText();

        if (inputEditText != null) {
            inputEditText.setText(String.valueOf(mConverter.getInput()));
        }

        if (outputEditText != null) {
            outputEditText.setText(String.valueOf(mConverter.getOutput()));
        }

        switch (mConverter.getInputType()) {
            case MILES:
                mInput.setHint("Miles");
                mOutput.setHint("Kilometers");
                break;
            case KM:
                mInput.setHint("Kilometers");
                mOutput.setHint("Miles");
                break;
        }
    }

    private void convert() {
        EditText editText = mInput.getEditText();

        if (editText != null) {
            String inputStr = editText.getText().toString();

            if (inputStr.isEmpty()) {
                mSnackBar.setText("Please enter a value");
                mSnackBar.show();
                return;
            }

            double inputVal = Double.parseDouble(inputStr);

            if (inputVal <= 0) {
                mSnackBar.setText("Please enter a value greater than 0");
                mSnackBar.show();
            } else {
                mConverter.convert(inputVal);
                updateUI();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            showSettings();
            return true;
        } else if (itemId == R.id.action_about) {
            showAbout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAbout() {
        dismissSnackBarIfShown();
        showInfoDialog(MainActivity.this, "About Unit Converter",
                "Convert between miles and kilometers!\nby Nechemia Seif");
    }

    private void showSettings() {
        dismissSnackBarIfShown();
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            restoreOrSetFromPreferences();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
