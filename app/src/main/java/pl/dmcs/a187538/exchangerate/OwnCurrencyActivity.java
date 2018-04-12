package pl.dmcs.a187538.exchangerate;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Damian on 19.12.2016.
 */

public class OwnCurrencyActivity extends Activity implements View.OnClickListener {

    private static final String PREFERENCES_OWN_CURRENCY = "preferencesOwnCurrency";
    private EditText ownCode;
    private EditText ownDescription;
    private EditText ownMid;
    private List<Currency> ownCurrencyList;
    private RecyclerView recyclerView;
    private CurrencyAdapter mAdapter;
    private static SharedPreferences sharedPreferencesOwnCurrency;
    private Button btnAddCurrency;
    private Button btnEditCurrency;
    private Spinner spinner;
    private int selectedPosition;
    private ArrayList<String> codes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_currency);
        initTextViews();
        sharedPreferencesOwnCurrency = getSharedPreferences(PREFERENCES_OWN_CURRENCY, Activity.MODE_PRIVATE);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        btnAddCurrency = (Button) findViewById(R.id.btnAddCurrency);
        btnAddCurrency.setOnClickListener(this);
        btnEditCurrency = (Button) findViewById(R.id.btnEditCurrency);
        btnEditCurrency.setOnClickListener(this);
        ownCurrencyList = new ArrayList<Currency>();
        spinner = (Spinner) findViewById(R.id.currencySpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ownCode.setText(ownCurrencyList.get(position).getCode());
                ownDescription.setText(ownCurrencyList.get(position).getDescription());
                ownMid.setText(ownCurrencyList.get(position).getMid());
                selectedPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mAdapter = new CurrencyAdapter(this, ownCurrencyList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        restoreOwnCurrency();
    }

    public void fillUpSpinner(List<Currency> currencyList) {
        codes = new ArrayList<>();
        if (currencyList != null) {
            for (Currency currency : currencyList) {
                codes.add(currency.getCode());
            }
        }
        ArrayAdapter<String> currencyArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, codes);
        spinner.setAdapter(currencyArrayAdapter);
    }


    private void addOwnCurrency(String description, String code, String mid) {
        Currency currency = new Currency(description, code, mid);
        boolean isContains = ownCurrencyList.contains(currency);
        if (!isContains) {
            ownCurrencyList.add(currency);
            fillUpSpinner(ownCurrencyList);
            mAdapter.notifyDataSetChanged();
            convertToJson(ownCurrencyList);
        }
    }

    public int countNumberEqual(List<Currency> currencies, Currency currency) {

        int count = 0;
        for (Currency i : currencies) {
            if (i.equals(currency)) {
                count++;
            }
        }
        return count;
    }

    private void editCurrency(String description, String code, String mid, int position) {
        Currency currency = new Currency(description, code, mid);
        if (ownCurrencyList.size() != 0) {
            ownCurrencyList.set(position, currency);
            if (countNumberEqual(ownCurrencyList, currency) == 1) {
                ownCurrencyList.set(position, currency);
                fillUpSpinner(ownCurrencyList);
                ownCode.setText(ownCurrencyList.get(position).getCode());
                ownDescription.setText(ownCurrencyList.get(position).getDescription());
                ownMid.setText(ownCurrencyList.get(position).getMid());
                mAdapter.notifyDataSetChanged();
                convertToJson(ownCurrencyList);
            }
        }
    }


    private void initTextViews() {
        ownCode = (EditText) findViewById(R.id.ownCode);
        ownDescription = (EditText) findViewById(R.id.ownDescription);
        ownMid = (EditText) findViewById(R.id.ownMid);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddCurrency:
                addOwnCurrency(ownCode.getText().toString(), ownMid.getText().toString(), ownDescription.getText().toString());
                break;
            case R.id.btnEditCurrency:
                editCurrency(ownCode.getText().toString(), ownMid.getText().toString(), ownDescription.getText().toString(), selectedPosition);
                break;
        }
    }


    public static void convertToJson(List<Currency> currencyList) {
        String json = new Gson().toJson(currencyList);
        SharedPreferences.Editor editor = sharedPreferencesOwnCurrency.edit();
        editor.putString(PREFERENCES_OWN_CURRENCY, json);
        editor.commit();
    }

    private void restoreOwnCurrency() {
        String json = sharedPreferencesOwnCurrency.getString(PREFERENCES_OWN_CURRENCY, "");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Currency>>() {
        }.getType();
        List<Currency> currencyList = gson.fromJson(json, listType);
        if (currencyList != null) {
            for (Currency currency : currencyList) {
                addOwnCurrency(currency.getCode(), currency.getMid(), currency.getDescription());
            }
        }
        fillUpSpinner(currencyList);
    }

}
