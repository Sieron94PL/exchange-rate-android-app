package pl.dmcs.a187538.exchangerate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.WindowDecorActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Damian on 18.12.2016.
 */

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.MyViewHolder> {

    private List<Currency> currencyList;
    private OwnCurrencyActivity ownCurrencyActivity;


    public CurrencyAdapter(OwnCurrencyActivity ownCurrencyActivity, List<Currency> currencyList) {
        this.currencyList = currencyList;
        this.ownCurrencyActivity = ownCurrencyActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.own_currency_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Currency currency = currencyList.get(position);
        holder.ownDescriptionRV.setText(currency.getDescription());
        holder.ownCodeRV.setText(currency.getCode());
        holder.ownMidRV.setText(currency.getMid());
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currencyList.remove(position);
                notifyDataSetChanged();
                OwnCurrencyActivity.convertToJson(currencyList);
                ownCurrencyActivity.fillUpSpinner(currencyList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return currencyList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView ownCodeRV, ownDescriptionRV, ownMidRV;
        public Button btnRemove;

        public MyViewHolder(final View itemView) {
            super(itemView);
            ownCodeRV = (TextView) itemView.findViewById(R.id.ownCodeRV);
            ownDescriptionRV = (TextView) itemView.findViewById(R.id.ownDesriptionRV);
            ownMidRV = (TextView) itemView.findViewById(R.id.ownMidRV);
            btnRemove = (Button) itemView.findViewById(R.id.btnRemove);
        }
    }
}
