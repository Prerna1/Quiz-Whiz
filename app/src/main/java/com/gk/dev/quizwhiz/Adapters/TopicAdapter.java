package com.gk.dev.quizwhiz.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.gk.dev.quizwhiz.R;

import java.util.ArrayList;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> implements Filterable {
    final private ListItemClickListener mOnClickListener;
    private List<String> topicList, filteredTopics;


    public TopicAdapter(List<String> topicList, ListItemClickListener listener) {
        this.topicList = topicList;
        filteredTopics = new ArrayList<>();
        filteredTopics.addAll(topicList);
        mOnClickListener = listener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                filteredTopics.clear();
                if (charString.isEmpty()) {
                    filteredTopics.addAll(topicList);
                } else {
                    for (String row : topicList) {
                        if (row.toLowerCase().contains(charString.toLowerCase())) {
                            filteredTopics.add(row);
                        }
                    }
                }
                return new FilterResults();
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_topic, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String topic = filteredTopics.get(position);
        holder.topic.setText(topic);
    }

    @Override
    public int getItemCount() {
        return filteredTopics.size();
    }

    public interface ListItemClickListener {
        void onListItemClick(String selectedTopic);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView topic;
        CardView topicCard;

        ViewHolder(View view) {
            super(view);
            topic = view.findViewById(R.id.tvTopicName);
            topicCard = view.findViewById(R.id.card_view);
            topicCard.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String selectedTopic = filteredTopics.get(getAdapterPosition());
            mOnClickListener.onListItemClick(selectedTopic);
        }
    }
}