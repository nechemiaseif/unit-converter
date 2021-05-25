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
import com.google.android.material.textfield.TextInputLayout;

import nseif.unitconverter.R;
import nseif.unitconverter.models.UnitConverter;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static nseif.unitconverter.lib.Utils.showInfoDialog;

public class MainActivity extends AppCompatActivity {
    private UnitConverter mConverter;
    private TextInputLayout mInput;
    private TextInputLayout mOutput;

    private final String mKEY_INPUT_TYPE = "INPUT_TYPE";

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

        editor.putInt(mKEY_INPUT_TYPE, mConverter.getInputType().ordinal());

        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();

        restoreOrSetFromPreferences();
    }

    private void restoreOrSetFromPreferences() {
        SharedPreferences sp = getDefaultSharedPreferences(this);
        int inputTypeOrdinal = sp.getInt(mKEY_INPUT_TYPE, 0);
        mConverter.setInputTypeFromOrdinal(inputTypeOrdinal);
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
        mConverter.setInputTypeFromOrdinal(savedInstanceState.getInt(mKEY_INPUT_TYPE, 0));
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

        updateUI();
    }

    private void setupViews() {
        mInput = findViewById(R.id.input);
        mOutput = findViewById(R.id.output);
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
        EditText inputEditText = mInput.getEditText();
        EditText outputEditText = mOutput.getEditText();

        if (inputEditText != null) {
            inputEditText.setText(String.valueOf(mConverter.getInput()));
        }

        if (outputEditText != null) {
            outputEditText.setText(String.valueOf(mConverter.getOutput()));
        }
    }

    private void convert() {
        EditText editText = mInput.getEditText();

        if (editText != null) {
            // TODO use input type from preferences
            String inputStr = editText.getText().toString();

            if (!inputStr.isEmpty()) {
                mConverter.convert(UnitConverter.InputType.MILES, Double.parseDouble(inputStr));
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
        showInfoDialog(MainActivity.this, "About Unit Converter",
                "Convert between miles and kilometers!\nby Nechemia Seif");
    }

    private void showSettings() {
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
