package pl.edu.mimuw.students.Productivity;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        setListener(listener(1, 100),"session_length");
        setListener(listener(1, 30), "short_break_length");
        setListener(listener(1, 200), "long_break_length");
        setListener(listener(1, 20), "sessions_per_cycle");
        setListener(listener(0, 23), "queue_reset_time");
    }

    private void setListener(EditTextPreference.OnBindEditTextListener listener, String key) {
        EditTextPreference et = getPreferenceManager().findPreference(key);
        et.setOnBindEditTextListener(listener);
    }

    EditTextPreference.OnBindEditTextListener listener(int min, int max) {
        return new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setFilters(filter(min, max));
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        };
    }

    InputFilter[] filter (int min, int max) {
       return new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
           String inp = dest.toString() + source.toString();
           int input;
           try {
               input = inp.equals("") ? 0 : Integer.parseInt(inp);
           } catch (NumberFormatException e) {
               //this allows the app not to crash when given a "-" or other non-digit
               input = 0;
           }
           if (input >= min && input <= max) {
               return null;
           }
           return "";
       }
       };
    }



}
