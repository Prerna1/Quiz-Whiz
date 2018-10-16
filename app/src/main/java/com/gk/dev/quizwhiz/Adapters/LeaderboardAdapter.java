package com.gk.dev.quizwhiz.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gk.dev.quizwhiz.Model.LeaderboardDetails;
import com.gk.dev.quizwhiz.R;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.MyViewHolder> {

    private List<LeaderboardDetails> leaderboardList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView rank,name,numberOfWins;

        public MyViewHolder(View view) {
            super(view);
             rank= (TextView) view.findViewById(R.id.rank);
            name = (TextView) view.findViewById(R.id.name);
            numberOfWins = (TextView) view.findViewById(R.id.number_of_wins);
        }
    }


    public LeaderboardAdapter(List<LeaderboardDetails> leaderboardList) {
        this.leaderboardList = leaderboardList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboard_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LeaderboardDetails leaderboardDetails = leaderboardList.get(position);
        holder.rank.setText(Integer.toString(leaderboardDetails.getRank()));
        holder.name.setText(leaderboardDetails.getName());
        holder.numberOfWins.setText(Integer.toString(leaderboardDetails.getNumberOfWins()));
    }

    @Override
    public int getItemCount() {
        return leaderboardList.size();
    }
}