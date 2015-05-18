package io.github.drautb.amazontradeinlookup;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by drautb on 5/12/15.
 * http://developer.android.com/guide/topics/ui/settings.html#Fragment
 */
public class SettingsFragment extends PreferenceFragment {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.preferences);
  }

}
