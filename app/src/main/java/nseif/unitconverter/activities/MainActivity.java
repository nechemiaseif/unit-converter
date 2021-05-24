package nseif.unitconverter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import nseif.unitconverter.R;
import nseif.unitconverter.models.UnitConverter;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static nseif.unitconverter.lib.Utils.showInfoDialog;

public class MainActivity extends AppCompatActivity {
    private UnitConverter mConverter;
    private EditText mEtInput;
    private TextView mTvOutput;

    private final String mKEY_INPUT_TYPE = "INPUT_TYPE";

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
        outState.putInt(mKEY_INPUT_TYPE, mConverter.getInputType().ordinal());
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
        setupConverter();
    }

    private void setupConverter() {
        mConverter = new UnitConverter();
    }

    private void setupViews() {
        mEtInput = findViewById(R.id.input);
        mTvOutput = findViewById(R.id.output);
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
        mEtInput.setText(String.valueOf(mConverter.getInput()));
        mTvOutput.setText(String.valueOf(mConverter.getOutput()));
    }

    private void convert() {
        // TODO use input type from preferences
        String inputStr = mEtInput.getText().toString();

        if(!inputStr.isEmpty()) {
            mConverter.convert(UnitConverter.InputType.MILES, Double.parseDouble(mEtInput.getText().toString()));
            updateUI();
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
