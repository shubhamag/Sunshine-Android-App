package com.example.shubhama.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private  ArrayAdapter<String> forecastListAdapter;
    private String FRAGMENTLOG_TAG = ForecastFragment.class.getSimpleName();

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.forecast_menu, menu);
//        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentRootView =  inflater.inflate(R.layout.fragment_main, container, false);
        final ListView listView  = (ListView) fragmentRootView.findViewById(R.id.listView_forecast);
        String[] Forecasts =
                {
                        "Today - Sunny - 30/18",
                        "Tomorrow - Cloudy - 28/19",
                        "Wednesday - Volcanic ash - 30/18",
                        "Thursday - Sunny - 30/18",
                        "Fridau - Sunny - 30/18",
                        "Sat- Sunny - 30/18",
                        "Sundayy - Rainy - 24/15",
                };
        List forecastData = new ArrayList <String> ( Arrays.asList(Forecasts));
        forecastListAdapter  = new ArrayAdapter <String>(getActivity(), R.layout.list_item_forecast,R.id.list_item_forecast_textview,forecastData );

        listView.setAdapter(forecastListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String dayWeatherDataString = (String)(listView.getItemAtPosition(position));


                Intent downloadIntent = new Intent(getActivity(), DayWeatherDetailActivity.class);
                downloadIntent.putExtra(Intent.EXTRA_TEXT,dayWeatherDataString);
                startActivity(downloadIntent);
            }
        });


    return fragmentRootView;

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_refresh) {
                Context context = getActivity();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, "Refreshing data", duration);
                toast.show();
                Log.i(FRAGMENTLOG_TAG,"refresh buttong pressed");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

                String s = prefs.getString("location",getString(R.string.location_deafult_id));
                 new ForecastFetcher().execute(s);
                return true;
            }
            if(id == R.id.action_about){
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getActivity(), "Sunshine App By Shubham Agrawal", duration);
                toast.show();
            }

        return super.onOptionsItemSelected(item);

    }


    public class ForecastFetcher extends AsyncTask < String, Void, String[] >{
        @Override
        protected void onPostExecute(String resultStrs[]) {
            if(resultStrs!=null){
                List<String> forecastData = new ArrayList <String> ( Arrays.asList(resultStrs));
//                forecastListAdapter  = new ArrayAdapter <String>(getActivity(), R.layout.list_item_forecast,R.id.list_item_forecast_textview,forecastData );
                forecastListAdapter.clear();
                forecastListAdapter.addAll(resultStrs);

                for (String s : resultStrs) {
                    Log.v("FORECAST DATA FORMATTER", "Forecast entry: " + s);
                }
            }
        }

        private final String FETCHER_LOGTAG = ForecastFetcher.class.getSimpleName();
        @Override
        protected String[] doInBackground(String... params) {
            Log.i(FETCHER_LOGTAG,"entering fetcher");
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String mode = "json";
            SharedPreferences msharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String numdays = msharedPrefs.getString("daycount", "8");
            Integer numDays = Integer.parseInt(numdays);
            String cnt = numDays.toString();
            String units = "metric";
            String appid = "2de143494c0b295cca9337e1e96b00e0";
            final String FORECAST_BASE_URL  = "http://api.openweathermap.org/data/2.5/forecast/daily?";
            final String QUERY_PARAM = "id";
            final String FORMAT_PARAM = "mode";
            final String UNIT_PARAM = "units";
            final String DAYCOUNT_PARAM = "cnt";
            final String APPID_PARAM = "appid";
            String forecastJsonStr = null;


            try {


                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM,params[0])
                        .appendQueryParameter(FORMAT_PARAM,mode)
                        .appendQueryParameter(UNIT_PARAM,units)
                        .appendQueryParameter(DAYCOUNT_PARAM,cnt)
                        .appendQueryParameter(APPID_PARAM,appid)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(FETCHER_LOGTAG,"URI : " + builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
//
//            // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream == null) {
//                // Nothing to do.
                    return null;
                }
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    Log.e(FETCHER_LOGTAG, "Empty buffer");
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(FETCHER_LOGTAG, "IO exception Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;

            }
            finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(FETCHER_LOGTAG, "Error closing stream", e);
                    }
                }
            }
//            Log.v(FETCHER_LOGTAG, forecastJsonStr);


            try {
                return getWeatherDataFromJson(forecastJsonStr,numDays);
            } catch (JSONException e) {
                Log.e(FETCHER_LOGTAG,"couldnt parse json " + e.getMessage(),e);
                e.printStackTrace();
            }
            return null;
        }
    }

    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);


        return roundedHigh + "/" + roundedLow;
    }
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.

        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        String[] resultStrs = new String[numDays];
        for(int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime;
            // Cheating to convert this to UTC time, which is what we want anyhow
            dateTime = dayTime.setJulianDay(julianStartDay+i);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }


        return resultStrs;

    }
}
