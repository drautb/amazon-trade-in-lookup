package io.github.drautb.amazontradeinlookup;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by drautb on 5/12/15.
 */
public class RequestTradeInDataTask extends AsyncTask<String, Integer, JSONObject> {

  private MainActivity mainActivity;

  private ProgressDialog progressRing;

  public RequestTradeInDataTask(MainActivity activity) {
    this.mainActivity = activity;
  }

  @Override
  protected void onPreExecute() {
    progressRing = new ProgressDialog(mainActivity);
    progressRing.setCancelable(false);
    progressRing.setMessage(mainActivity.getString(R.string.searching_message));
    progressRing.show();
  }

  protected JSONObject doInBackground(String... urls) {
    String urlStr = urls[0];
    TradeInDataRequester requester = new TradeInDataRequester();

    JSONObject result = new JSONObject();
    try {
      result = requester.getTradeInData(urlStr);
    } catch (JSONException | IOException e) {
      result = null;
      mainActivity.showError(e.toString());
    }

    return result;
  }

  /**
   * Called with the result of doInBackground. Returns a result object.
   *
   * @param result * If an error occurred, result == null.
   *               * If no book with that ISBN was found, result.length() == 0
   *               * If a book was found, result == a good json object with keys for Title, ISBN, and TradeInOptions.
   *               TradeInOptions will be an empty list if the book is not currently eligible for trade in.
   */
  protected void onPostExecute(JSONObject result) {
    progressRing.dismiss();

    if (result == null) {
      // Do nothing, doInBackground already presented the error.
      return;
    } else if (result.length() == 0) {
      mainActivity.showError(mainActivity.getString(R.string.book_not_found_error));
      return;
    }

    try {
      String title = result.getString(mainActivity.getString(R.string.data_title_key));
      String isbn = result.getString(mainActivity.getString(R.string.data_isbn_key));
      JSONArray tradeInOptions = result.getJSONArray(mainActivity.getString(R.string.data_trade_in_options_key));

      if (tradeInOptions.length() == 0) {
        String msg = String.format(mainActivity.getString(R.string.not_eligible_msg_template), title, isbn);
        mainActivity.showInfo(R.string.not_eligible_title, msg);
      } else if (tradeInOptions.length() == 1) {
        JSONObject tradeInOption = tradeInOptions.getJSONObject(0);
        for (Iterator<String> keys = tradeInOption.keys(); keys.hasNext(); ) {
          String tradeInValueStr = tradeInOption.getString(keys.next());
          double tradeInValue = Double.parseDouble(tradeInValueStr.replace("$", ""));

          TradeIn tradeIn = new TradeIn(title, isbn, tradeInValue);
          mainActivity.addLookup(tradeIn);
        }
      } else {
        // TODO: Make this dialog a list of checkboxes to select which versions they want.
        String msg = String.format(mainActivity.getString(R.string.select_trade_in_msg_template), title, isbn);
        mainActivity.showInfo(R.string.select_trade_in_title, msg);
        mainActivity.showInfo(R.string.select_trade_in_title, tradeInOptions.toString());
      }
    } catch (JSONException e) {
      mainActivity.showError(e.toString());
    }
  }

}
