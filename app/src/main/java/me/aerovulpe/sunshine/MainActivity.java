package me.aerovulpe.sunshine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import me.aerovulpe.sunshine.sync.SunshineSyncAdapter;


public class MainActivity extends Activity implements ForecastFragment.Callback{

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.weather_detail_container) != null){
            mTwoPane = true;

            if (savedInstanceState == null){
                getFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment())
                        .commit();
            }
        }else{
            mTwoPane = false;
        }
        ((ForecastFragment)getFragmentManager().findFragmentById(R.id.fragment_forecast)).setIsSinglePane(!mTwoPane);
        SunshineSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String date) {
        if (mTwoPane){
            Bundle args = new Bundle();
            args.putString(ForecastFragment.EXTRA_WEATHER_DATE, date);
            DetailFragment swap = new DetailFragment();
            swap.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, swap)
                    .commit();
        }else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(ForecastFragment.EXTRA_WEATHER_DATE, date);
            startActivity(intent);
        }
    }
}
