package com.sam_chordas.android.stockhawk.activities;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ChartActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ChartActivity.class.getSimpleName();
    private Context mContext;
    private LineChart chartStockHistory;
    private Uri mUri;
    private Cursor mCursor;

    private static final int CURSOR_LOADER_ID = 248;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mContext = this;
        mUri = getIntent().getData();
        initViews();

    }

    private void initViews() {

        chartStockHistory = (LineChart) findViewById(R.id.chartStockHistory);
        chartStockHistory.setVisibleXRange(1, 30);
        chartStockHistory.getXAxis().setTextColor(Color.CYAN);
        chartStockHistory.getAxisLeft().setTextColor(Color.YELLOW);
        chartStockHistory.getAxisRight().setTextColor(Color.YELLOW);
        chartStockHistory.setScaleYEnabled(false);
        chartStockHistory.setDescription(getString(R.string.appwidget_text));
        chartStockHistory.setDescriptionColor(Color.WHITE);

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return new CursorLoader(this, mUri,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.CREATED, QuoteColumns.ISUP},
                QuoteColumns.CREATED + " > ?",
                new String[]{String.valueOf(calendar.getTimeInMillis())},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;

        ArrayList<String> labels = new ArrayList<String>();
        ArrayList<Entry> entries = new ArrayList<>();
        int x = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        float minValue = 0;
        float maxValue = 0;

        while (data.moveToNext()) {
            String bidPrice = data.getString(data.getColumnIndex("bid_price"));
            long time = data.getLong(data.getColumnIndex(QuoteColumns.CREATED));
            float bid = 0;
            try {
                bid = Float.valueOf(bidPrice);

                Entry entry = new Entry(bid, x);
                entries.add(entry);
                labels.add(sdf.format(time));

                x++;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }


            Log.d(TAG, "Data: " + bidPrice);

        }
        LineDataSet dataset = new LineDataSet(entries, getString(R.string.chart_title_stock_price));
        dataset.setDrawCircles(false);
        dataset.setDrawValues(false);

        LineData lineData = new LineData(labels, dataset);


        chartStockHistory.setData(lineData);
        chartStockHistory.invalidate();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }
}
