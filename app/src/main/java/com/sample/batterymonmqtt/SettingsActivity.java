package com.sample.batterymonmqtt;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
            //mPreferences = getPreferenceScreen().getSharedPreferences();
            mPreferences=this.getActivity().getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
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
                if(key=="mqtt_host"){
                    sharedPreferences.edit().putString("mqtt_host", sharedPreferences.getString(preference.getKey(), "192.168.0.40"));
                }else if(key=="mqtt_interval"){
                    sharedPreferences.edit().putString("mqtt_interval", sharedPreferences.getString(preference.getKey(), "30"));

                }
                }
        }

    }
}