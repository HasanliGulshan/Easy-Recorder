package com.gulshan.hasanli.easysoundrecorder.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.gulshan.hasanli.easysoundrecorder.R;
import com.gulshan.hasanli.easysoundrecorder.adapters.ToolbarAdapter;
import com.gulshan.hasanli.easysoundrecorder.fragments.FileViewerFragment;
import com.gulshan.hasanli.easysoundrecorder.fragments.RecordFragment;

import static com.gulshan.hasanli.easysoundrecorder.activities.SettingActivity.CURRENT_THEME;
import static com.gulshan.hasanli.easysoundrecorder.activities.SettingActivity.sCurrentColorTabLayout;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private int option;
    private int statusbarColor;
    private boolean resume = false;
   // private boolean create = false;
    private int tabLayoutColor = 0;
   // public static TabLayout mainTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "oncreate");
        SharedPreferences preferences = getSharedPreferences(CURRENT_THEME,MODE_PRIVATE);
        option = preferences.getInt(CURRENT_THEME, 0);

        Log.i(TAG, String.valueOf(tabLayoutColor) + "tablayoutcolor");

        keepTheme(option);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        ToolbarAdapter toolbarAdapter = new ToolbarAdapter(getSupportFragmentManager());
      //  mainTabLayout = (TabLayout)findViewById(R.id.tabLayout);

        toolbarAdapter.addFragment(new RecordFragment(), getString(R.string.tab_title_record));
        toolbarAdapter.addFragment(new FileViewerFragment(), getString(R.string.tab_title_saved_records));

        viewPager.setAdapter(toolbarAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //bind menu to toolbar menu bar
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //setting click
        switch (item.getItemId()) {
            case R.id.settings:

                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                intent.putExtra("option", option);

                startActivity(intent);
                finish();

                return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostResume() {

        SharedPreferences tabLayoutPreference = getSharedPreferences(CURRENT_THEME,MODE_PRIVATE);
        int currentColorTab = tabLayoutPreference.getInt(sCurrentColorTabLayout, 0);

        if(currentColorTab == -1) {

            currentColorTab = option;

        }
        Log.i("currentColorTab", String.valueOf(currentColorTab));
        resume  = true;
        keepTheme(currentColorTab);

        Log.i(TAG, "onResume after setting");
        super.onPostResume();

    }

    private void keepTheme(int option) {

        switch (option) {
            case 0:
                Log.i(TAG, "here0");
                setTheme(R.style.AppTheme_red_200);
                if(resume) {
                    Log.i(TAG, "resumeBoolean");
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.red_200_colorAccent));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
                    tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.red_200_colorPrimaryDark));
                    resume = false;
                }
                statusbarColor = R.color.red_200_colorPrimaryDark;
                break;
            case 1:
                Log.i(TAG, "here1");
                setTheme(R.style.AppTheme_deep_orange_300);
                if(resume) {
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.deep_orange_300_colorAccent));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
                    tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.deep_orange_300_colorPrimaryDark));
                    resume = false;
                }
                statusbarColor = R.color.deep_orange_300_colorPrimaryDark;
                break;
            case 2:
                setTheme(R.style.AppTheme_red_400);
                if(resume) {
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.red_400_colorAccent));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
                    tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.red_400_colorPrimaryDark));
                    resume = false;
                }
                statusbarColor = R.color.red_400_colorPrimaryDark;
                break;
            case 3:
                setTheme(R.style.AppTheme_red_700);
                if(resume) {
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.red_700_colorAccent));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
                    tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.red_700_colorPrimaryDark));
                    resume = false;
                }
                statusbarColor = R.color.red_700_colorPrimaryDark;
                break;
            case 4:
                setTheme(R.style.AppTheme_pink_400);
                if(resume) {
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.pink_400_colorAccent));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
                    tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.pink_400_colorPrimaryDark));
                    resume = false;
                }
                statusbarColor = R.color.pink_400_colorPrimaryDark;
                break;
            case 5:
                setTheme(R.style.AppTheme_blue_200);
                if(resume) {
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.blue_200_colorAccent));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
                    tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.blue_200_colorPrimaryDark));
                    resume = false;
                }
                statusbarColor = R.color.blue_200_colorPrimaryDark;
                break;
            case 6:
                setTheme(R.style.AppTheme_cyan_200);
                if(resume) {
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.cyan_200_colorAccent));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
                    tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.cyan_200_colorPrimaryDark));
                    resume = false;
                }
                statusbarColor = R.color.cyan_200_colorPrimaryDark;
                break;
            case 7:
                setTheme(R.style.AppTheme_cyan_400);
                if(resume) {
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.cyan_400_colorAccent));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
                    tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.cyan_400_colorPrimaryDark));
                    resume = false;
                }
                statusbarColor = R.color.cyan_400_colorPrimaryDark;
                break;
            case 8:
                setTheme(R.style.AppTheme_cyan_500);
                if(resume) {
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.cyan_500_colorAccent));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
                    tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.cyan_500_colorPrimaryDark));
                    resume = false;
                }
                statusbarColor = R.color.cyan_500_colorPrimaryDark;
                break;
            case 9:
                setTheme(R.style.AppTheme_blue_500);
                if(resume) {
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.blue_500_colorAccent));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
                    tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.blue_500_colorPrimaryDark));
                    resume = false;
                }
                statusbarColor = R.color.blue_500_colorPrimaryDark;
                break;
            case 10:
                setTheme(R.style.AppTheme_green_200);
                if(resume) {
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.green_200_colorAccent));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
                    tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.green_200_colorPrimaryDark));
                    resume = false;
                }
                statusbarColor = R.color.green_200_colorPrimaryDark;
                break;
            case 11:
                setTheme(R.style.AppTheme_green_300);
                if(resume) {
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.green_300_colorAccent));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
                    tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.green_300_colorPrimaryDark));
                    resume = false;
                }
                statusbarColor = R.color.green_300_colorPrimaryDark;
                break;
            case 12:
                setTheme(R.style.AppTheme_green_400);
                if(resume) {
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.green_400_colorAccent));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
                    tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.green_400_colorPrimaryDark));
                    resume = false;
                }
                statusbarColor = R.color.green_400_colorPrimaryDark;
                break;
            case 13:
                setTheme(R.style.AppTheme_lime_800);
                if(resume) {
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.lime_800_colorAccent));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
                    tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.lime_800_colorPrimaryDark));
                    resume = false;
                }
                statusbarColor = R.color.lime_800_colorPrimaryDark;
                break;
            case 14:
                setTheme(R.style.AppTheme_brown_200);
                if(resume) {
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.brown_200_colorAccent));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
                    tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.brown_200_colorPrimaryDark));
                    resume = false;
                }
                statusbarColor = R.color.brown_200_colorPrimaryDark;
                break;

        }

        //toolbar.setBackgroundColor(ContextCompat.getColor(SettingActivity.this, toolbarColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, statusbarColor));
        }

    }

}
