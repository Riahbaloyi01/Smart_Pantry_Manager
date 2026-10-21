package com.baloyi.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.baloyi.smartpantry.R;
import com.baloyi.smartpantry.model.PantryItem;
import com.baloyi.smartpantry.util.AppSettings;

import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    // The fragment implements this so it can react when a row is tapped or deleted
    public interface OnItemActionListener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private List<PantryItem> items;
    private final OnItemActionListener listener;

    public PantryAdapter(List<PantryItem> items, OnItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    // Swap in fresh data from the database and redraw the list
    public void setItems(List<PantryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    // Creates a blank row from item_pantry.xml
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    // Fills a row with the data of one pantry item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PantryItem item = items.get(position);

        holder.tvName.setText(item.getName());
        holder.tvInitial.setText(item.getName().substring(0, 1).toUpperCase());

        double q = item.getQuantity();
        String qty = (q == Math.floor(q)) ? String.valueOf((long) q) : String.valueOf(q);
        holder.tvQuantity.setText(qty + " " + item.getUnit());

        String expiry = item.getExpiryDate();
        int grey = ContextCompat.getColor(holder.itemView.getContext(), android.R.color.darker_gray);
        int red = ContextCompat.getColor(holder.itemView.getContext(), R.color.danger_red);
        boolean alertsOn = AppSettings.isExpiryAlertsOn(holder.itemView.getContext());

        if (expiry == null || expiry.isEmpty()) {
            holder.tvExpiry.setText("No expiry date");
            holder.tvExpiry.setTextColor(grey);
        } else {
            long days = daysUntil(expiry);
            if (alertsOn && days < 0) {
                holder.tvExpiry.setText("Expired: " + expiry);
                holder.tvExpiry.setTextColor(red);
            } else if (alertsOn && days <= 3) {
                holder.tvExpiry.setText("Expires soon: " + expiry);
                holder.tvExpiry.setTextColor(red);
            } else {
                holder.tvExpiry.setText("Expires: " + expiry);
                holder.tvExpiry.setTextColor(grey);
            }
        }

        holder.itemView.setOnClickListener(v -> listener.onEdit(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Days from today until the expiry date (negative = already expired)
    private long daysUntil(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date target = format.parse(date);
            Date today = format.parse(format.format(new Date()));
            return Math.round((target.getTime() - today.getTime()) / 86400000.0);
        } catch (ParseException e) {
            return 999; // unreadable date: treat as not expiring
        }
    }

    // Holds the views of one row so they are looked up only once
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitial, tvName, tvQuantity, tvExpiry;
        ImageButton btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitial = itemView.findViewById(R.id.tvInitial);
            tvName = itemView.findViewById(R.id.tvName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}