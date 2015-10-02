package io.github.drautb.amazontradeinlookup;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class DisplaySettingsActivity extends ActionBarActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_display_settings);

    // Display the settings fragment as the main content.
    getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new SettingsFragment())
        .commit();
  }

}
