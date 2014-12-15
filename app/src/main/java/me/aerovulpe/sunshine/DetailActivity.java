package me.aerovulpe.sunshine;

import android.app.Activity;
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
import android.widget.ShareActionProvider;
import android.widget.TextView;

import me.aerovulpe.sunshine.data.WeatherContract;


public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private static final int FORECAST_DETAIL_LOADER = 1;
        private ShareActionProvider mShareActionProvider;
        private static final String SUNSHINE_HASHTAG = " #SunshineApp";
        private String mForecastStr;
        private String mDateStr;

        TextView mDateView;
        TextView mDetailView;
        TextView mHighView;
        TextView mLowView;

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

            mDateView = (TextView) rootView.findViewById(R.id.list_item_date_textview);
            mDetailView = (TextView) rootView.findViewById(R.id.list_item_detail_textview);
            mHighView = (TextView) rootView.findViewById(R.id.list_item_high_textview);
            mLowView = (TextView) rootView.findViewById(R.id.list_item_low_textview);

            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            mDateStr = getActivity()
                    .getIntent()
                    .getStringExtra(ForecastFragment.EXTRA_WEATHER_DATE);
            getLoaderManager().initLoader(FORECAST_DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
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

                    if (!data.moveToFirst()) return;

                    boolean isMetric = Utility.isMetric(getActivity());

                    mDateView.setText(Utility.formatDate(data.getString(data
                            .getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT))));
                    mDetailView.setText(data.getString(data
                            .getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC)));
                    mHighView.setText(Utility.formatTemperature(data.getDouble(data
                                    .getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)),
                            isMetric));
                    mLowView.setText(Utility.formatTemperature(data.getDouble(data
                                    .getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)),
                            isMetric));
                    break;
            }
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
}
