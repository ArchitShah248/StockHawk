package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.activities.MyStocksActivity;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.service.StockTaskService;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Implementation of App Widget functionality.
 */
public class StockQuotesWidgetProvider extends AppWidgetProvider {


    public static final String[] FORECAST_COLUMNS = {
            QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP, QuoteColumns.CREATED
    };
    // these indices must match the projection
    public static final int INDEX_ID = 0;
    public static final int INDEX_SYMBOL = 1;
    public static final int INDEX_BIDPRICE = 2;
    public static final int INDEX_PERCENT_CHANGE = 3;
    public static final int INDEX_CHANGE = 4;
    public static final int INDEX_ISUP = 5;
    public static final int INDEX_CREATED = 6;


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_stock_quotes);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

//        Cursor data = context.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
//                FORECAST_COLUMNS,
//                QuoteColumns.ISCURRENT + " = ?",
//                new String[]{"1"},
//                null);
//
//        StringBuffer lastUpdated = null;
//        if (data.moveToFirst()) {
//            lastUpdated = new StringBuffer(context.getString(R.string.last_updated_on));
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            lastUpdated.append(" ");
//            lastUpdated.append(sdf.format(new Date(data.getLong(INDEX_CREATED))));
//        }
//        for (int appWidgetId : appWidgetIds) {
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_stocks);
//
//            // Create an Intent to launch MainActivity
//            Intent intent = new Intent(context, MyStocksActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
//
//            if (lastUpdated != null) {
//                views.setTextViewText(R.id.tvLastUpdated, lastUpdated.toString());
//            }
//            // Set up the collection
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                setRemoteAdapter(context, views);
//            } else {
//                setRemoteAdapterV11(context, views);
//            }
////            boolean useDetailActivity = context.getResources().getBoolean(R.bool.use_detail_activity);
////            Intent clickIntentTemplate = useDetailActivity
////                    ? new Intent(context, ChartActivity.class)
////                    : new Intent(context, MainActivity.class);
////            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
////                    .addNextIntentWithParentStack(clickIntentTemplate)
////                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
////            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
//            views.setEmptyView(R.id.widget_list, R.id.widget_empty);
//
//            // Tell the AppWidgetManager to perform an update on the current app widget
//            appWidgetManager.updateAppWidget(appWidgetId, views);
//        }

        updateWidgetViews(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (StockTaskService.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
            updateWidgetViews(context, appWidgetManager, appWidgetIds);
        }
    }

    private void updateWidgetViews(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Cursor data = context.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                FORECAST_COLUMNS,
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);

        StringBuffer lastUpdated = null;
        if (data.moveToFirst()) {
            lastUpdated = new StringBuffer(context.getString(R.string.last_updated_on));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            lastUpdated.append(" ");
            lastUpdated.append(sdf.format(new Date(data.getLong(INDEX_CREATED))));
        }
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_stocks);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            if (lastUpdated != null) {
                views.setTextViewText(R.id.tvLastUpdated, lastUpdated.toString());
            }
            // Set up the collection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
            } else {
                setRemoteAdapterV11(context, views);
            }
//            boolean useDetailActivity = context.getResources().getBoolean(R.bool.use_detail_activity);
//            Intent clickIntentTemplate = useDetailActivity
//                    ? new Intent(context, ChartActivity.class)
//                    : new Intent(context, MainActivity.class);
//            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
//                    .addNextIntentWithParentStack(clickIntentTemplate)
//                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list, new Intent(context, StockQuotesWidgetRemoteViewsService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list, new Intent(context, StockQuotesWidgetRemoteViewsService.class));
    }
}

