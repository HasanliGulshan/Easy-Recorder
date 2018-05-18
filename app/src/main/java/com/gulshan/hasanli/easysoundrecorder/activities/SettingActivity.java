package com.gulshan.hasanli.easysoundrecorder.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gulshan.hasanli.easysoundrecorder.R;
import com.gulshan.hasanli.easysoundrecorder.SharedPreference.MySharedPreference;

import petrov.kristiyan.colorpicker.ColorPicker;

public class SettingActivity extends PreferenceActivity {

    private static final String TAG = "SettingActivity";
    public static final String CURRENT_THEME = "theme";
    private ActionBar actionBar;
    private AppCompatDelegate mDelegate;
    private Toolbar toolbar;
    private boolean firstOne=true;
    private int optionSetting;
    private int currentColorTabLayout=-1;
    public static final String sCurrentColorTabLayout = "";
    private Preference listPreference;
    private CheckBoxPreference highQualityPref;
    private String preferenceStringColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preference);

        addPreferencesFromResource(R.xml.preference);

        View container = findViewById(android.R.id.list);

        Intent intent = getIntent();
        optionSetting = intent.getIntExtra("option", 0);

        listPreference = (Preference) findPreference(getResources().getString(R.string.color));
        highQualityPref = (CheckBoxPreference) findPreference(getResources().getString(R.string.pref_high_quality_key));
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        if(firstOne) {
            changeTheme(optionSetting);
            firstOne = false;
        }

        if(container != null) container.setPadding(0,0,0,0);


        highQualityPref.setChecked(MySharedPreference.getPrefHighQuality(SettingActivity.this));
        highQualityPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                MySharedPreference.setPrefHighQuality(SettingActivity.this, (boolean) newValue);
                return true;
            }
        });

        listPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Log.i(TAG, "onPreferenceClick");

                final ColorPicker colorPicker = new ColorPicker(SettingActivity.this);
                final int[] mColors = getResources().getIntArray(R.array.colors);
                colorPicker.setColors(mColors);
                colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position,int color) {
                        Log.i(TAG, "onChooseColor" + color + " " + position + " " + R.color.red_200_colorPrimaryDark);

                        currentColorTabLayout = position;
                        getSharedPreference(position);
                        Log.i(TAG, "onChooseColor");
                        changeTheme(position);

                    }

                    @Override
                    public void onCancel(){
                        // put code
                    }
                })
                        .setColumns(5)
                        .setRoundColorButton(true)
                        .show();

                return true;
            }

        });

        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                recreate();
                return true;
            }
        });

        initToolbar();
    }

    private void getSharedPreference(int position) {

        SharedPreferences example = getSharedPreferences(CURRENT_THEME, MODE_PRIVATE);
        example.edit().putInt(CURRENT_THEME, position).apply();
        int rep = example.getInt(CURRENT_THEME,0);
        Log.i(TAG + "_Shared", String.valueOf(rep));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {

            Intent moveback = new Intent(this, MainActivity.class);
            Log.i("onOptionsItemSelected", String.valueOf(currentColorTabLayout));

            SharedPreferences tabLayoutExample = getSharedPreferences(CURRENT_THEME, MODE_PRIVATE);
            tabLayoutExample.edit().putInt(sCurrentColorTabLayout, currentColorTabLayout).apply();

            startActivity(moveback);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }


    private void setListPreferenceAndHighQualityPrefColor(String preferenceStringColor) {

        listPreference.setTitle(Html.fromHtml("<font color=" + preferenceStringColor +">" + listPreference.getTitle() + "</font>"));
        listPreference.setSummary(Html.fromHtml("<font color=" + preferenceStringColor +">" + listPreference.getSummary() + "</font>"));
        highQualityPref.setTitle(Html.fromHtml("<font color=" + preferenceStringColor +">" + highQualityPref.getTitle() + "</font>"));
        highQualityPref.setSummary(Html.fromHtml("<font color=" + preferenceStringColor +">" + highQualityPref.getSummary() + "</font>"));

    }



    private void changeTheme(int selectedPosition) {

        int toolbarColor = 0;
        int statusBarColor = 0;

        switch (selectedPosition) {
            case 0:
                Log.i(TAG, "here0");
                setTheme(R.style.AppTheme_red_200);

                preferenceStringColor = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.red_200_colorPrimaryDark) & 0x00ffffff);
                setListPreferenceAndHighQualityPrefColor(preferenceStringColor);
                
                toolbarColor = R.color.red_200_colorPrimary;
                statusBarColor = R.color.red_200_colorPrimaryDark;
                break;
            case 1:
                Log.i(TAG, "here1");
                setTheme(R.style.AppTheme_deep_orange_300);

                preferenceStringColor = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.deep_orange_300_colorPrimaryDark) & 0x00ffffff);
                setListPreferenceAndHighQualityPrefColor(preferenceStringColor);

                toolbarColor = R.color.deep_orange_300_colorPrimary;
                statusBarColor = R.color.deep_orange_300_colorPrimaryDark;
                break;
            case 2:
                setTheme(R.style.AppTheme_red_400);

                preferenceStringColor = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.red_400_colorPrimaryDark) & 0x00ffffff);
                setListPreferenceAndHighQualityPrefColor(preferenceStringColor);

                toolbarColor = R.color.red_400_colorPrimary;
                statusBarColor = R.color.red_400_colorPrimaryDark;
                break;
            case 3:
                setTheme(R.style.AppTheme_red_700);

                preferenceStringColor = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.red_700_colorPrimaryDark) & 0x00ffffff);
                setListPreferenceAndHighQualityPrefColor(preferenceStringColor);

                toolbarColor = R.color.red_700_colorPrimary;
                statusBarColor = R.color.red_700_colorPrimaryDark;
                break;
            case 4:
                setTheme(R.style.AppTheme_pink_400);

                preferenceStringColor = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.pink_400_colorPrimaryDark) & 0x00ffffff);
                setListPreferenceAndHighQualityPrefColor(preferenceStringColor);

                toolbarColor = R.color.pink_400_colorPrimary;
                statusBarColor = R.color.pink_400_colorPrimaryDark;
                break;
            case 5:
                setTheme(R.style.AppTheme_blue_200);

                preferenceStringColor = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.blue_200_colorPrimaryDark) & 0x00ffffff);
                setListPreferenceAndHighQualityPrefColor(preferenceStringColor);

                toolbarColor = R.color.blue_200_colorPrimary;
                statusBarColor = R.color.blue_200_colorPrimaryDark;
                break;
            case 6:
                setTheme(R.style.AppTheme_cyan_200);

                preferenceStringColor = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.cyan_200_colorPrimaryDark) & 0x00ffffff);
                setListPreferenceAndHighQualityPrefColor(preferenceStringColor);

                toolbarColor = R.color.cyan_200_colorPrimary;
                statusBarColor = R.color.cyan_200_colorPrimaryDark;
                break;
            case 7:
                setTheme(R.style.AppTheme_cyan_400);

                preferenceStringColor = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.cyan_400_colorPrimaryDark) & 0x00ffffff);
                setListPreferenceAndHighQualityPrefColor(preferenceStringColor);

                toolbarColor = R.color.cyan_400_colorPrimary;
                statusBarColor = R.color.cyan_400_colorPrimaryDark;
                break;
            case 8:
                setTheme(R.style.AppTheme_cyan_500);

                preferenceStringColor = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.cyan_500_colorPrimaryDark) & 0x00ffffff);
                setListPreferenceAndHighQualityPrefColor(preferenceStringColor);

                toolbarColor = R.color.cyan_500_colorPrimary;
                statusBarColor = R.color.cyan_500_colorPrimaryDark;
                break;
            case 9:
                setTheme(R.style.AppTheme_blue_500);

                preferenceStringColor = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.blue_500_colorPrimaryDark) & 0x00ffffff);
                setListPreferenceAndHighQualityPrefColor(preferenceStringColor);

                toolbarColor = R.color.blue_500_colorPrimary;
                statusBarColor = R.color.blue_500_colorPrimaryDark;
                break;
            case 10:
                setTheme(R.style.AppTheme_green_200);

                preferenceStringColor = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.green_200_colorPrimaryDark) & 0x00ffffff);
                setListPreferenceAndHighQualityPrefColor(preferenceStringColor);

                toolbarColor = R.color.green_200_colorPrimary;
                statusBarColor = R.color.green_200_colorPrimaryDark;
                break;
            case 11:
                setTheme(R.style.AppTheme_green_300);

                preferenceStringColor = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.green_300_colorPrimaryDark) & 0x00ffffff);
                setListPreferenceAndHighQualityPrefColor(preferenceStringColor);

                toolbarColor = R.color.green_300_colorPrimary;
                statusBarColor = R.color.green_300_colorPrimaryDark;
                break;
            case 12:
                setTheme(R.style.AppTheme_green_400);

                preferenceStringColor = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.green_400_colorPrimaryDark) & 0x00ffffff);
                setListPreferenceAndHighQualityPrefColor(preferenceStringColor);

                toolbarColor = R.color.green_400_colorPrimary;
                statusBarColor = R.color.green_400_colorPrimaryDark;
                break;
            case 13:
                setTheme(R.style.AppTheme_lime_800);

                preferenceStringColor = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.lime_800_colorPrimaryDark) & 0x00ffffff);
                setListPreferenceAndHighQualityPrefColor(preferenceStringColor);

                toolbarColor = R.color.lime_800_colorPrimary;
                statusBarColor = R.color.lime_800_colorPrimaryDark;
                break;
            case 14:
                setTheme(R.style.AppTheme_brown_200);

                preferenceStringColor = "#" + Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.brown_200_colorPrimaryDark) & 0x00ffffff);
                setListPreferenceAndHighQualityPrefColor(preferenceStringColor);

                toolbarColor = R.color.brown_200_colorPrimary;
                statusBarColor = R.color.brown_200_colorPrimaryDark;
                break;

        }


        toolbar.setBackgroundColor(ContextCompat.getColor(SettingActivity.this, toolbarColor));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(SettingActivity.this, statusBarColor));
        }

    }

    private void initToolbar() {

        Log.i("Toolbar", "initToolbar");
        this.setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.settings);

    }

    public ActionBar getSupportActionBar() {

        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }
}
