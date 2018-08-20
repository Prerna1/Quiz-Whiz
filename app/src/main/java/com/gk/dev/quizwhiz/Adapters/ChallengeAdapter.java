package com.gk.dev.quizwhiz.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gk.dev.quizwhiz.Model.FriendDetails;
import com.gk.dev.quizwhiz.Model.UserDetails;
import com.gk.dev.quizwhiz.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolder> implements Filterable {

    private Context context;
    private ListItemClickListener mOnClickListener;
    private List<FriendDetails> allFriends, filteredFriends;

    public ChallengeAdapter(Context context, List<FriendDetails> allFriends, ListItemClickListener listItemClickListener) {
        this.allFriends = allFriends;
        this.context = context;
        this.filteredFriends = new ArrayList<>();
        mOnClickListener = listItemClickListener;
        filteredFriends.addAll(allFriends);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                filteredFriends.clear();
                if (charString.isEmpty()) {
                    filteredFriends.addAll(allFriends);
                } else {
                    for (FriendDetails row : allFriends) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredFriends.add(row);
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
                .inflate(R.layout.list_item_friend, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Picasso.with(context).load(filteredFriends.get(position).getPictureURL()).into(holder.profilePicture);
        holder.name.setText(filteredFriends.get(position).getName());
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserDetails").child(filteredFriends.get(position).getFbId());
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails friendDetails = dataSnapshot.getValue(UserDetails.class);
                if (holder.getAdapterPosition() >= 0 && friendDetails != null) {
                    String nameNStatus = filteredFriends.get(holder.getAdapterPosition()).getName();
                    filteredFriends.get(holder.getAdapterPosition()).setStatus(friendDetails.getStatus());
                    if (friendDetails.getStatus() == 0)
                        nameNStatus = nameNStatus.concat(" OFFLINE");
                    else if (friendDetails.getStatus() == 1)
                        nameNStatus = nameNStatus.concat(" ONLINE");
                    else
                        nameNStatus = nameNStatus.concat(" PLAYING");
                    holder.name.setText(nameNStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredFriends.size();
    }

    public interface ListItemClickListener {
        void onListItemClick(FriendDetails selectedFriend);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        ImageView profilePicture;
        RelativeLayout listItemFriend;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.title);
            profilePicture = view.findViewById(R.id.img_android);
            listItemFriend = view.findViewById(R.id.rl_list_item);
            listItemFriend.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            FriendDetails selectedFriend = filteredFriends.get(getAdapterPosition());
            mOnClickListener.onListItemClick(selectedFriend);
        }
    }

}
