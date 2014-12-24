package me.aerovulpe.sunshine;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import me.aerovulpe.sunshine.data.WeatherContract;

/**
 * Created by Aaron on 20/12/2014.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int FORECAST_DETAIL_LOADER = 1;
    private ShareActionProvider mShareActionProvider;
    private static final String SUNSHINE_HASHTAG = " #SunshineApp";
    private String mForecastStr;
    private String mDateStr;

    TextView mDateView;
    TextView mDetailView;
    TextView mHighView;
    TextView mLowView;
    TextView mHumidityView;
    TextView mWindView;
    TextView mPressureView;
    ImageView mIconView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) menuItem.getActionProvider();
        mShareActionProvider.setShareIntent(createShareForecastIntent());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mDateView = (TextView) rootView.findViewById(R.id.detail_view_date_textview);
        mDetailView = (TextView) rootView.findViewById(R.id.detail_view_detail_textview);
        mHighView = (TextView) rootView.findViewById(R.id.detail_view_high_textview);
        mLowView = (TextView) rootView.findViewById(R.id.detail_view_low_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_view_humidity_textview);
        mWindView = (TextView) rootView.findViewById(R.id.detail_view_wind_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_view_pressure_textview);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_view_icon_textview);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (getArguments() != null && getArguments().containsKey(ForecastFragment.EXTRA_WEATHER_DATE)) {
            mDateStr = getArguments().getString(ForecastFragment.EXTRA_WEATHER_DATE);
            getLoaderManager().initLoader(FORECAST_DETAIL_LOADER, null, this);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        if (getArguments() != null && getArguments().containsKey(ForecastFragment.EXTRA_WEATHER_DATE)) {
            mDateStr = getArguments().getString(ForecastFragment.EXTRA_WEATHER_DATE);
            getLoaderManager().restartLoader(FORECAST_DETAIL_LOADER, null, this);
        }
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mDateStr == null) return null;
        String location = Utility.getPreferredLocation(getActivity());
        Uri uri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(location, mDateStr);
        return new CursorLoader(getActivity(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case FORECAST_DETAIL_LOADER:
                // The asynchronous load is complete and the data
                // is now available for use.

                populateViews(data);
                break;
        }
    }

    private void populateViews(Cursor data) {
        if (!data.moveToFirst()) return;

        boolean isMetric = Utility.isMetric(getActivity());

        mDateView.setText(Utility.getDayName(getActivity(), data.getString(data
                .getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT))) + "\n"
                + Utility.getFormattedMonthDay(getActivity(), data.getString(data
                .getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT))));
        mDetailView.setText(data.getString(data
                .getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC)));
        mHighView.setText(Utility.formatTemperature(getActivity(), data.getDouble(data
                        .getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)),
                isMetric));
        mLowView.setText(Utility.formatTemperature(getActivity(), data.getDouble(data
                        .getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)),
                isMetric));
        mHumidityView.setText(String.format(getActivity().getString(R.string.format_humidity),
                data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY))));
        mWindView.setText(Utility.getFormattedWind(getActivity(),
                data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED)),
                data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED))));
        mPressureView.setText(String.format(getActivity().getString(R.string.format_pressure),
                data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE))));
        mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(data.getInt(data
                .getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID))));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + SUNSHINE_HASHTAG);
        return shareIntent;
    }
}
