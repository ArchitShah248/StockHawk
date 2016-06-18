package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

/**
 * Created by Archit Shah on 6/6/2016.
 */
public class StockQuotesWidgetRemoteViewsService extends RemoteViewsService {

    public final String LOG_TAG = StockQuotesWidgetRemoteViewsService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        StockQuotesWidgetProvider.FORECAST_COLUMNS,
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_list_item_quote);

                views.setTextViewText(R.id.widget_stock_symbol, data.getString(StockQuotesWidgetProvider.INDEX_SYMBOL));
                views.setTextViewText(R.id.widget_bid_price, data.getString(StockQuotesWidgetProvider.INDEX_BIDPRICE));
                int sdk = Build.VERSION.SDK_INT;
                if (data.getInt(StockQuotesWidgetProvider.INDEX_ISUP) == 1) {
//                        views.setImageViewResource(R.id.widget_change,R.drawable.percent_change_pill_green);
                    views.setInt(R.id.widget_change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
//                    views.setImageViewResource(R.id.widget_change,R.drawable.percent_change_pill_red);
                    views.setInt(R.id.widget_change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }
                if (Utils.showPercent) {
                    views.setTextViewText(R.id.widget_change, data.getString(StockQuotesWidgetProvider.INDEX_PERCENT_CHANGE));
                } else {
                    views.setTextViewText(R.id.widget_change, data.getString(StockQuotesWidgetProvider.INDEX_CHANGE));
                }

                final Intent fillInIntent = new Intent();

                fillInIntent.setData(QuoteProvider.Quotes.CONTENT_URI);
//                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.mipmap.ic_launcher, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(StockQuotesWidgetProvider.INDEX_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }

}
