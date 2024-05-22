package weather_app.weatherviewer;

// MainActivity.java
// Displays a 16-day weather forecast for the specified city

import android.content.Context;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Weather> weatherList = new ArrayList<>();
    private WeatherArrayAdapter weatherArrayAdapter;
    private ListView weatherListView;

    private TextView txt_user;
    private TextView txt_tem_day;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Encuentra las vistas
        EditText locationEditText = findViewById(R.id.locationEditText);
        weatherListView = findViewById(R.id.weatherListView);
        FloatingActionButton fab = findViewById(R.id.fab);

        TextView texto_user = findViewById(R.id.txt_user);


        texto_user.setText("Hi, " + Build.BRAND);


        // Configura el adaptador para el ListView
        weatherArrayAdapter = new WeatherArrayAdapter(this, weatherList);
        weatherListView.setAdapter(weatherArrayAdapter);

        // Configura el FloatingActionButton
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                URL url = createURL(locationEditText.getText().toString());

                if (url != null) {
                    dismissKeyboard(locationEditText);
                    GetWeatherTask getLocalWeatherTask = new GetWeatherTask();
                    getLocalWeatherTask.execute(url);
                } else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.invalid_url, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        set_saludo();
    }

    public void set_saludo() {
        TextView tem_day = findViewById(R.id.txt_tem_day);
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        System.out.println("Asi esta la hora " + hora);
        String saludo = "";

        if (hora >= 6 && hora < 12) {
            saludo = "Good Morning";
        }else if (hora >= 12 && hora < 18) {
            saludo = "Good Afternoon";
        } else if (hora >= 18 && hora < 22) {
            saludo = " Good Evening";
        } else{
            saludo = "Good Night";
        }


        tem_day.setText(saludo);

    }


    private void dismissKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private URL createURL(String city) {
        String apiKey = "@string/api_key"; // Reemplaza con tu clave de API de OpenWeatherMap
        String baseUrl = "@string/web_service_url";

        try {
            String urlString = baseUrl + URLEncoder.encode(city, "UTF-8") + "&units=metric&cnt=16&appid=" + apiKey;
            return new URL(urlString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class GetWeatherTask extends AsyncTask<URL, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) params[0].openConnection();
                System.out.println("Imagen descargada exitosamente desde main activity: " + params[0]);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    } catch (IOException e) {
                        Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.read_error, Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    return new JSONObject(builder.toString());
                } else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.connect_error, Snackbar.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.connect_error, Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
                System.out.println("Error descargando la imagen desde activity: " + params[0]);

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject weather) {
            if (weather != null) {
                convertJSONtoArrayList(weather);
                weatherArrayAdapter.notifyDataSetChanged();
                weatherListView.smoothScrollToPosition(0);
            }
        }
    }

    private void convertJSONtoArrayList(JSONObject forecast) {
        weatherList.clear(); // clear old weather data
        try {
            // read the "list" of weather forecast - JSONArray
            JSONArray list = forecast.getJSONArray("list");
            // transform each list element into a Weather object
            for (int i = 0; i < list.length(); ++i) {
                JSONObject day = list.getJSONObject(i); // read data concerning one day
                // read data concerning temperature in that day from JSONObject
                JSONObject temperatures = day.getJSONObject("main");
                // read description and weather icon from JSONObject
                JSONObject weather = day.getJSONArray("weather").getJSONObject(0);
                // add new Weather object to weatherList
                weatherList.add(new Weather(
                        day.getLong("dt"), // timestamp
                        temperatures.getDouble("temp_min"), // minimal temperature
                        temperatures.getDouble("temp_max"), // maximal temperature
                        temperatures.getDouble("humidity"), // humidity
                        weather.getString("description"), // weather conditions
                        weather.getString("icon"))); // icon name
                System.out.println("json " +weather);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
