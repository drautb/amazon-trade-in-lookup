package io.github.drautb.amazontradeinlookup;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by drautb on 5/12/15.
 */
public class TradeInDataRequester {

  public JSONObject getTradeInData(String urlStr) throws IOException, JSONException {
    Log.i(this.getClass().toString(), "getTradeInData urlStr=" + urlStr);

    HttpURLConnection urlConnection = null;
    JSONObject result = null;
    try {
      URL url = new URL(urlStr);
      urlConnection = (HttpURLConnection) url.openConnection();
      InputStream in = new BufferedInputStream(urlConnection.getInputStream());

      String responseStr = convertStreamToString(in);
      Log.i(this.getClass().toString(), "getTradeInData response=" + responseStr);
      result = new JSONObject(responseStr);
    }
    catch (FileNotFoundException e) { // 404
      result = null;
    }
    finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
    }

    return result;
  }

  private String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }

}
