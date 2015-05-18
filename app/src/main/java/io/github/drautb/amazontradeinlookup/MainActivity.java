package io.github.drautb.amazontradeinlookup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.NumberFormat;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

  private final static String ISBN_REGEX = "^\\d{10}$|^\\d{13}$";

  private final static String SAVED_RECENT_LOOKUPS_KEY = "recentLookupsKey";

  private static ArrayList<TradeIn> recentLookups = new ArrayList<TradeIn>();
  private static TradeInAdapter lookupAdapter;

  private ListView tradeInList;

  private NumberFormat formatter = NumberFormat.getCurrencyInstance();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (savedInstanceState == null || !savedInstanceState.containsKey(SAVED_RECENT_LOOKUPS_KEY)) {
      recentLookups.clear();
    } else {
      recentLookups = savedInstanceState.getParcelableArrayList(SAVED_RECENT_LOOKUPS_KEY);
      recalculateTotals();
    }

    // Setup the list view
    lookupAdapter = new TradeInAdapter(this, R.layout.listview_tradein_row, recentLookups);

    tradeInList = (ListView) findViewById(R.id.tradein_list);
    tradeInList.setAdapter(lookupAdapter);

    tradeInList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        MainActivity.this.showEditPrice(position);
      }
    });
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putParcelableArrayList(SAVED_RECENT_LOOKUPS_KEY, recentLookups);
    super.onSaveInstanceState(outState);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      Intent intent = new Intent(this, DisplaySettingsActivity.class);
      startActivity(intent);
      return true;
    } else if (id == R.id.action_clear_lookups) {
      recentLookups.clear();
      lookupAdapter.notifyDataSetChanged();
      recalculateTotals();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public void showError(String msg) {
    DialogFragment newFragment = AlertDialogFragment.newInstance(android.R.drawable.ic_dialog_alert,
        R.string.error_dialog_title, msg);
    newFragment.show(getFragmentManager(), "dialog");
  }

  public void showInfo(int title, String msg) {
    DialogFragment newFragment = AlertDialogFragment.newInstance(android.R.drawable.ic_dialog_info,
        title, msg);
    newFragment.show(getFragmentManager(), "dialog");
  }

  public void showEditPrice(int tradeInIndex) {
    final int idx = tradeInIndex;

    final EditText editText = new EditText(this);
    editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

    new AlertDialog.Builder(this)
        .setTitle(R.string.purchase_price_title)
        .setMessage(R.string.purchase_price_message)
        .setView(editText)
        .setPositiveButton(android.R.string.ok,
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                recentLookups.get(idx).setPurchasePrice(Double.parseDouble(editText.getText().toString()));
                recalculateTotals();
                lookupAdapter.notifyDataSetChanged();
              }
            })
        .setNeutralButton(R.string.remove_from_list,
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                recentLookups.remove(idx);
                recalculateTotals();
                lookupAdapter.notifyDataSetChanged();
              }
            })
        .setNegativeButton(android.R.string.cancel,
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
              }
            })
        .show();
  }

  public void addLookup(TradeIn lookup) {
    recentLookups.add(lookup);
    lookupAdapter.notifyDataSetChanged();
    recalculateTotals();
  }

  public void initiateScan(View view) {
    IntentIntegrator integrator = new IntentIntegrator(this);
    integrator.initiateScan();
  }

  public void fetchTradeInData(View view) {
    EditText editText = (EditText) findViewById(R.id.isbn_text);
    String isbn = editText.getText().toString();

    if (!isValidIsbn(isbn)) {
      String msg = String.format(getString(R.string.invalid_isbn_msg_template), isbn);
      Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
      return;
    }

    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    String host = sharedPref.getString(getString(R.string.pref_host), "");
    String port = sharedPref.getString(getString(R.string.pref_port), "");
    String path = sharedPref.getString(getString(R.string.pref_path), "");
    String urlString = String.format(getString(R.string.trade_in_data_url_template), host, port, path, isbn);

    new RequestTradeInDataTask(this).execute(urlString);
  }

  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
    if (scanResult != null) {
      String scannedCode = scanResult.getContents();

      // Put the scanned code in the ISBN text field.
      EditText editText = (EditText) findViewById(R.id.isbn_text);
      editText.setText(scannedCode);

      // Click the "Go" button automatically.
      Button goButton = (Button) findViewById(R.id.go_button);
      goButton.callOnClick();
    }
  }

  private boolean isValidIsbn(String isbn) {
    return isbn.matches(ISBN_REGEX);
  }

  private void recalculateTotals() {
    double totalPurchasePrice = 0.0;
    double totalTradeInValue = 0.0;

    for (TradeIn t : recentLookups) {
      totalPurchasePrice += t.getPurchasePrice();
      totalTradeInValue += t.getTradeInValue();
    }

    double profitValue = totalTradeInValue - totalPurchasePrice;

    ((TextView) findViewById(R.id.profit_total)).setText(formatter.format(profitValue));
    ((TextView) findViewById(R.id.purchase_price_total)).setText(formatter.format(totalPurchasePrice));
    ((TextView) findViewById(R.id.trade_in_value_total)).setText(formatter.format(totalTradeInValue));
  }

  public static class AlertDialogFragment extends DialogFragment {

    private final static String ICON_KEY = "icon";
    private final static String TITLE_KEY = "title";
    private final static String MSG_KEY = "msg";

    public static AlertDialogFragment newInstance(int icon, int title, String msg) {
      AlertDialogFragment frag = new AlertDialogFragment();
      Bundle args = new Bundle();

      args.putInt(ICON_KEY, icon);
      args.putInt(TITLE_KEY, title);
      args.putString(MSG_KEY, msg);
      frag.setArguments(args);

      return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      int icon = getArguments().getInt(ICON_KEY);
      int title = getArguments().getInt(TITLE_KEY);
      String msg = getArguments().getString(MSG_KEY);

      return new AlertDialog.Builder(getActivity())
          .setIcon(icon)
          .setTitle(title)
          .setMessage(msg)
          .setPositiveButton(android.R.string.ok,
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                  // We don't need to do anything when "Ok" is clicked on an error.
                }
              }
          )
          .create();
    }
  }

}
