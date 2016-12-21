package ch.usi.inf.mc.yapt.parc.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.regex.Pattern;

import ch.usi.inf.mc.yapt.parc.R;

import static ch.usi.inf.mc.yapt.parc.service.SendService.DEFAULT_PORT;

public class PreferencesFragment extends PreferenceFragment {

    private final Pattern IP_ADDRESS
            = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))");

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        final EditTextPreference setIP = (EditTextPreference) getPreferenceManager()
                .findPreference("user_ip");
        final EditTextPreference setPort = (EditTextPreference) getPreferenceManager()
                .findPreference("user_port");

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(
                getActivity().getApplicationContext());


        final String ipValue = settings.getString("user_ip", "");
        if (validIp(ipValue)) {
            setIP.setSummary(ipValue);
        } else {
            setIP.setSummary(R.string.no_ip);
        }

        String portValue = settings.getString("user_ip", DEFAULT_PORT);
        if (validPort(portValue)) {
            setPort.setSummary(portValue);
        } else {
            setPort.setSummary(DEFAULT_PORT);
        }

        setIP.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(validIp(newValue.toString())){
                    setIP.setSummary(newValue.toString());
                    return true;
                }
                Toast.makeText(getActivity(),
                        String.format(getString(R.string.ip_error), newValue.toString()),
                        Toast.LENGTH_LONG).show();
                return false;
            }
        });

        setPort.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(validPort(newValue.toString())){
                    setPort.setSummary(newValue.toString());
                    return true;
                }

                Toast.makeText(getActivity(),
                        String.format(getString(R.string.port_error), newValue.toString()),
                        Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    private boolean validPort(String port) {
        int portno;
        try {
            portno = Integer.parseInt(port);
        } catch(NumberFormatException e) {
            return false;
        }
        return 1 <= portno && portno <= 65535;
    }

    private boolean validIp(String ip) {
        return ip != null && IP_ADDRESS.matcher(ip).matches();
    }
}
