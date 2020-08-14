package com.sample.batterymonmqtt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingsActivity extends AppCompatActivity {
    Context context=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        /*
         * go back when action bar back is tapped
         */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /*
     * go back when action bar back is tapped
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements
            SharedPreferences.OnSharedPreferenceChangeListener {
        static String TAG="SettingsFragment";
        private String sharedPrefFile = "com.sample.batterymonmqtt";
        private SharedPreferences mPreferences;

        @Override
        public void onResume(){
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }
        @Override
        public void onPause(){
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            Log.d(TAG, "SettingsFragment onCreatePreferences");
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            mPreferences = getPreferenceScreen().getSharedPreferences();
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            androidx.preference.EditTextPreference editTextPreference = getPreferenceManager().findPreference("use_key_from_editTextPreference_in_xml_file");
//            editTextPreference.setOnBindEditTextListener(new androidx.preference.EditTextPreference.OnBindEditTextListener() {
//                @Override
//                public void onBindEditText(@NonNull EditText editText) {
//                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
//
//                }
//            });
        }

        public static SettingsFragment newInstance() {
            Log.d(TAG, "SettingsFragment newInstance");
            SettingsFragment fragment = new SettingsFragment();
            return fragment;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d(TAG, "SettingsFragment onSharedPreferenceChanged called with key="+key);
            Preference preference = findPreference(key);

            if (preference != null) {
                if (!(preference instanceof CheckBoxPreference)) {
                    String value = sharedPreferences.getString(preference.getKey(), "");
                    Log.i(TAG, "changed key/value: " + key+"/"+value);
                }
                if(key==pref.PREF_MQTT_HOST){
                    sharedPreferences.edit().putString(pref.PREF_MQTT_HOST, sharedPreferences.getString(preference.getKey(), "192.168.0.40"));
                }else if(key==pref.PREF_MQTT_INTERVAL){
                    sharedPreferences.edit().putString(pref.PREF_MQTT_INTERVAL, sharedPreferences.getString(preference.getKey(), "30"));
                }else if(key==pref.PREF_MQTT_PORT){
                    sharedPreferences.edit().putString(pref.PREF_MQTT_PORT, sharedPreferences.getString(preference.getKey(), "1883"));
                }
                }
        }

        @Override
        public void onDetach() {
            super.onDetach();
            //Do your process here!
            MainActivity.getInstance().startWorker(getContext());
        }

    }
}