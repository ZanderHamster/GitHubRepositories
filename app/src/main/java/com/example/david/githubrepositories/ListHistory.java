package com.example.david.githubrepositories;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.david.githubrepositories.Database.Repositories;

import java.util.List;

class ListHistory extends RecyclerView.Adapter<ListHistory.HistoryViewHolder> {
    private List<Repositories> historyList;
    private Context context;

    ListHistory(Context context, List<Repositories> historyList) {
        this.historyList = historyList;
        this.context = context;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_mapping, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, final int position) {
        final Repositories historyItem = historyList.get(position);

        holder.name.setText("Пользователь: " + historyItem.getUser_name());
        holder.type.setText("Тип: " + historyItem.getOwner_type());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ResultActivity.class);
                intent.putExtra("userName", historyItem.getUser_name());
                intent.putExtra("ownerType", historyItem.getOwner_type());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView type;

        HistoryViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.tv_user_name_history);
            this.type = (TextView) itemView.findViewById(R.id.tv_owner_type_history);
        }
    }
}
