package pl.dmcs.a187538.exchangerate;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CURRENCY = "currency";
    private static final String RATES = "rates";
    private static final String CODE = "code";
    private static final String MID = "mid";
    private static final String BID = "bid";
    private static final String ASK = "ask";
    private static final String URL_TABLE_A = "http://api.nbp.pl/api/exchangerates/tables/A/?format=json";
    private static final String URL_TABLE_C = "http://api.nbp.pl/api/exchangerates/tables/c/?format=json";

    private ArrayList<HashMap<String, String>> rateList;
    private ArrayList<HashMap<String, String>> currencyInformation;
    private ListView listView;
    private SimpleAdapter listAdapter;
    private static SharedPreferences sharedPreferencesTableA;
    private SharedPreferences sharedPreferencesTableC;
    private static final String PREFERENCES_TABLE_A = "preferencesTableA";
    private static final String PREFERENCES_TABLE_C = "preferencesTableC";
    private AlertDialog.Builder alertDialog;
    private Button btnOwnCurrencyView;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferencesTableA = getSharedPreferences(PREFERENCES_TABLE_A, Activity.MODE_PRIVATE);
        sharedPreferencesTableC = getSharedPreferences(PREFERENCES_TABLE_C, Activity.MODE_PRIVATE);
        btnOwnCurrencyView = (Button) findViewById(R.id.btnOwnCurrencyView);
        btnOwnCurrencyView.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new InformationAboutCurrency(rateList.get(position).get(CODE).toString()).execute();
            }
        });
        rateList = new ArrayList<>();
        currencyInformation = new ArrayList<>();
        new DownloadExchangeRates().execute();
        alertDialog = new AlertDialog.Builder(MainActivity.this);

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onClick(View v) {
        intent = new Intent(getBaseContext(), OwnCurrencyActivity.class);
        startActivity(intent);
    }


    private class InformationAboutCurrency extends AsyncTask<Void, Void, CurrencyDetails> {

        private String selectedCode;

        public InformationAboutCurrency(String code) {
            this.selectedCode = code;
        }


        @Override
        protected CurrencyDetails doInBackground(Void... params) {


            String jsonStr = getJsonFromTableC();

            if (jsonStr != null) {
                try {
                    saveDataTableC(jsonStr);
                    JSONArray mainArray = new JSONArray(jsonStr);
                    JSONObject jsonObj = mainArray.getJSONObject(0);
                    JSONArray rates = jsonObj.getJSONArray(RATES);

                    for (int i = 0; i < rates.length(); i++) {

                        JSONObject object = rates.getJSONObject(i);
                        String currency = object.getString(CURRENCY);
                        String code = object.getString(CODE);
                        String bid = object.getString(BID);
                        String ask = object.getString(ASK);

                        HashMap<String, String> rate = new HashMap<>();
                        rate.put(CURRENCY, currency);
                        rate.put(CODE, code);
                        rate.put(BID, bid);
                        rate.put(ASK, ask);

                        currencyInformation.add(rate);

                        String getCurrency = currencyInformation.get(i).get(CODE);

                        if (selectedCode.equals(getCurrency)) {
                            return new CurrencyDetails(currencyInformation.get(i).get(CURRENCY), currencyInformation.get(i).get(CODE), currencyInformation.get(i).get(BID), currencyInformation.get(i).get(ASK));
                        }


                    }
                } catch (final JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            return null;


        }

        @Override
        protected void onPostExecute(final CurrencyDetails currency) {
            if (currency != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(currency.getCode().toString() + " - " + currency.getCurrency().toString());
                builder.setMessage("Kurs kupna waluty: " + currency.getBid().toString() + "\n" + "Kurs sprzedaży waluty: " + currency.getAsk().toString());
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Brak szczegółowych danych");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

        }
    }


    private String restoreDataFromTableC() {
        return sharedPreferencesTableC.getString(PREFERENCES_TABLE_C, "");
    }

    private void saveDataTableC(String jsonStr) {
        SharedPreferences.Editor editor = sharedPreferencesTableC.edit();
        editor.putString(PREFERENCES_TABLE_C, jsonStr);
        editor.commit();
    }


    private String restoreDataFromTableA() {
        return sharedPreferencesTableA.getString(PREFERENCES_TABLE_A, "");
    }

    private void saveDataTableA(String jsonStr) {
        SharedPreferences.Editor editor = sharedPreferencesTableA.edit();
        editor.putString(PREFERENCES_TABLE_A, jsonStr);
        editor.commit();
    }

    private void checkInternetConnection() {

        if (isOnline())
            Toast.makeText(getApplicationContext(), "Data are downloading from NBP API", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "No Internet Access", Toast.LENGTH_SHORT).show();
    }

    private String getJsonFromTableA() {
        HttpHandler httpHandler = new HttpHandler();
        if (isOnline()) {
            return httpHandler.makeServiceCall(URL_TABLE_A);
        } else {
            return restoreDataFromTableA();
        }
    }

    private String getJsonFromTableC() {
        HttpHandler httpHandler = new HttpHandler();
        if (isOnline())
            return httpHandler.makeServiceCall(URL_TABLE_C);
        else
            return restoreDataFromTableC();
    }

    private class DownloadExchangeRates extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            checkInternetConnection();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String jsonStr = getJsonFromTableA();

            if (jsonStr != null) {
                try {
                    saveDataTableA(jsonStr);
                    JSONArray mainArray = new JSONArray(jsonStr);
                    JSONObject jsonObj = mainArray.getJSONObject(0);
                    JSONArray rates = jsonObj.getJSONArray(RATES);

                    for (int i = 0; i < rates.length(); i++) {

                        JSONObject object = rates.getJSONObject(i);
                        String currency = object.getString(CURRENCY);
                        String code = object.getString(CODE);
                        String mid = object.getString(MID);

                        HashMap<String, String> rate = new HashMap<>();
                        rate.put(CURRENCY, currency);
                        rate.put(CODE, code);
                        rate.put(MID, mid);

                        rateList.add(rate);

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            return null;


        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            listAdapter = new SimpleAdapter(MainActivity.this, rateList, R.layout.list_item, new String[]{CODE, CURRENCY, MID}, new int[]{R.id.code, R.id.description, R.id.mid});
            listView.setAdapter(listAdapter);
        }
    }
}
