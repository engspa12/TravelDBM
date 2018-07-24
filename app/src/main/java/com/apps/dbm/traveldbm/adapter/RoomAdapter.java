package com.apps.dbm.traveldbm.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apps.dbm.traveldbm.R;
import com.apps.dbm.traveldbm.classes.Room;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private static final String LOG = RoomAdapter.class.getSimpleName();

    private int totalItems;

    private List<Room> listRoom;

    public RoomAdapter(int numberOfItems, List<Room> list){
        totalItems = numberOfItems;
        listRoom = list;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.room_item_two,parent,false);

        RoomViewHolder viewHolder = new RoomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return totalItems;
    }


    public class RoomViewHolder extends RecyclerView.ViewHolder{

        TextView roomCodeTV;
        TextView roomDescriptionTV;
        TextView roomPriceTV;

        private RoomViewHolder(View itemView){
            super(itemView);

            roomCodeTV = (TextView) itemView.findViewById(R.id.room_code_text_view);
            roomDescriptionTV = (TextView) itemView.findViewById(R.id.room_description_text_view);
            roomPriceTV = (TextView) itemView.findViewById(R.id.room_price_text_view);
        }

        private void bind(int listIndex){
            roomCodeTV.setText(listRoom.get(listIndex).getRoomCode());
            roomDescriptionTV.setText(listRoom.get(listIndex).getRoomDescription());
            roomPriceTV.setText(listRoom.get(listIndex).getRoomPrice());
        }

    }
}
