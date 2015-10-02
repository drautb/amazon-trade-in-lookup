package io.github.drautb.amazontradeinlookup;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.NumberFormat;

/**
 * Created by drautb on 5/13/15.
 */
public class TradeIn implements Parcelable {

  private NumberFormat formatter = NumberFormat.getCurrencyInstance();

  private String title;

  private String isbn;

  private double purchasePrice;

  private double tradeInValue;

  public TradeIn(String title, String isbn, double tradeInValue) {
    this.title = title;
    this.isbn = isbn;
    this.purchasePrice = 0.0;
    this.tradeInValue = tradeInValue;
  }

  private TradeIn(Parcel in) {
    title = in.readString();
    isbn = in.readString();
    purchasePrice = in.readDouble();
    tradeInValue = in.readDouble();
  }

  public int describeContents() {
    return 0;
  }

  public void writeToParcel(Parcel out, int flags) {
    out.writeString(title);
    out.writeString(isbn);
    out.writeDouble(purchasePrice);
    out.writeDouble(tradeInValue);
  }

  public void setPurchasePrice(double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  public String getTitle() {
    return title;
  }

  public String getIsbn() {
    return isbn;
  }

  public double getPurchasePrice() {
    return purchasePrice;
  }

  public double getTradeInValue() {
    return tradeInValue;
  }

  public String getFormattedPurchasePrice() {
    return formatter.format(purchasePrice);
  }

  public String getFormattedTradeInValue() {
    return formatter.format(tradeInValue);
  }

  @Override
  public String toString() {
    return String.format("[TRADE_IN TITLE: %s ISBN: %s PURCHASE PRICE: %s TRADE IN VALUE: %s", title, isbn, getFormattedPurchasePrice(), getFormattedTradeInValue());
  }

  public static final Parcelable.Creator<TradeIn> CREATOR = new Parcelable.Creator<TradeIn>() {
    public TradeIn createFromParcel(Parcel in) {
      return new TradeIn(in);
    }

    public TradeIn[] newArray(int size) {
      return new TradeIn[size];
    }
  };

}
