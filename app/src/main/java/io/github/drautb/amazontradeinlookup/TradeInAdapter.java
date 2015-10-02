package io.github.drautb.amazontradeinlookup;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by drautb on 5/13/15.
 */
public class TradeInAdapter extends ArrayAdapter<TradeIn> {

  Context context;
  int layoutResourceId;
  List<TradeIn> data;

  public TradeInAdapter(Context context, int layoutResourceId, List<TradeIn> data) {
    super(context, layoutResourceId, data);

    this.layoutResourceId = layoutResourceId;
    this.context = context;
    this.data = data;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View rowView = convertView;
    TradeInHolder holder = null;

    if (rowView == null) {
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      rowView = inflater.inflate(layoutResourceId, parent, false);

      holder = new TradeInHolder();

      holder.txtTitle = (TextView) rowView.findViewById(R.id.txtTitle);
      holder.txtTitle.setSelected(true);

      holder.txtIsbn = (TextView) rowView.findViewById(R.id.txtIsbn);
      holder.txtIsbn.setSelected(true);

      holder.txtPurchasePrice = (TextView) rowView.findViewById(R.id.txtPurchasePrice);
      holder.txtTradeInValue = (TextView) rowView.findViewById(R.id.txtTradeInValue);

      rowView.setTag(holder);
    } else {
      holder = (TradeInHolder) rowView.getTag();
    }

    TradeIn tradeIn = data.get(position);
    holder.txtTitle.setText(tradeIn.getTitle());
    holder.txtIsbn.setText(tradeIn.getIsbn());
    holder.txtPurchasePrice.setText(tradeIn.getFormattedPurchasePrice());
    holder.txtTradeInValue.setText(tradeIn.getFormattedTradeInValue());

    return rowView;
  }

  static class TradeInHolder {
    TextView txtTitle;
    TextView txtIsbn;
    TextView txtPurchasePrice;
    TextView txtTradeInValue;
  }
}
