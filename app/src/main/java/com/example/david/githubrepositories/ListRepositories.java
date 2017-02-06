package com.example.david.githubrepositories;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.david.githubrepositories.Database.Repositories;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class ListRepositories extends RecyclerView.Adapter<ListRepositories.RepoViewHolder>{
    private List<Repositories> repositoriesList;

    ListRepositories(List<Repositories> repositoriesList) {
        this.repositoriesList=repositoriesList;
    }

    @Override
    public RepoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.repository_mapping,parent,false);
        return new RepoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RepoViewHolder holder, int position) {
        Repositories repositoriesItem = repositoriesList.get(position);

        SimpleDateFormat format = new SimpleDateFormat("MMM dd,yyyy  hh:mm a");

        holder.name.setText("Имя: "+repositoriesItem.getName());
        holder.description.setText("Описание: "+repositoriesItem.getDescription());
        holder.created_at.setText("Дата создания:"+format.format(repositoriesItem.getCreated_at()));
        holder.updated_at.setText("Дата обновления: "+format.format(repositoriesItem.getUpdated_at()));
        holder.stargazers_count.setText("Количество звезд: "+String.valueOf(repositoriesItem.getStargazers_count()));
        holder.language.setText("Язык: "+repositoriesItem.getLanguage());
    }

    @Override
    public int getItemCount() {
        return repositoriesList.size();
    }

    class RepoViewHolder extends RecyclerView.ViewHolder{
        protected TextView name;
        TextView description;
        TextView created_at;
        TextView updated_at;
        TextView stargazers_count;
        TextView language;

        RepoViewHolder(View itemView) {
            super(itemView);

            this.name= (TextView) itemView.findViewById(R.id.tv_name);
            this.description= (TextView) itemView.findViewById(R.id.tv_description);
            this.created_at= (TextView) itemView.findViewById(R.id.tv_created_at);
            this.updated_at= (TextView) itemView.findViewById(R.id.tv_updated_at);
            this.stargazers_count= (TextView) itemView.findViewById(R.id.tv_stargazers_count);
            this.language= (TextView) itemView.findViewById(R.id.tv_language);
        }
    }
}
