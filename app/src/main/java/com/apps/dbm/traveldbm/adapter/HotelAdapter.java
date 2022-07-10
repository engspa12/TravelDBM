package com.apps.dbm.traveldbm.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.apps.dbm.traveldbm.R;
import com.apps.dbm.traveldbm.domain.Hotel;

import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder>{

    private static final String LOG = HotelAdapter.class.getSimpleName();

    private int numberOfHotels;

    private List<Hotel> listHotels;

    private ListItemClickListener mListener;

    private Context mContext;

    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex,String typeAction);
    }

    public HotelAdapter(List<Hotel> list, int numberItems, ListItemClickListener listener,Context context){
        numberOfHotels = numberItems;
        mListener = listener;
        listHotels = list;
        mContext = context;
    }


    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.hotel_item,parent,false);

        HotelViewHolder viewHolder = new HotelViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return numberOfHotels;
    }



    public class HotelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView hotelNameTextView;
        TextView hotelAddressTextView;
        TextView hotelMinPriceTextView;

        Button hotelDetailsButton;
        Button hotelMarkAsFavoriteButton;
        ImageButton hotelShareGeneralInfoButton;


        private HotelViewHolder(View itemView){
            super(itemView);

            hotelNameTextView = (TextView) itemView.findViewById(R.id.hotel_name_text_view);
            hotelAddressTextView = (TextView) itemView.findViewById(R.id.hotel_address_text_view);
            hotelMinPriceTextView = (TextView) itemView.findViewById(R.id.hotel_min_price_text_view);

            hotelDetailsButton = (Button) itemView.findViewById(R.id.details_button);
            hotelMarkAsFavoriteButton = (Button) itemView.findViewById(R.id.mark_as_favorite_button);
            hotelShareGeneralInfoButton = (ImageButton) itemView.findViewById(R.id.share_general_button);

            hotelDetailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int clickedPosition = getAdapterPosition();
                    mListener.onListItemClick(clickedPosition,"details");
                }
            });

            hotelMarkAsFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int clickedPosition = getAdapterPosition();
                    mListener.onListItemClick(clickedPosition,"mark_as_favorite");
                }
            });

            hotelShareGeneralInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int clickedPosition = getAdapterPosition();
                    mListener.onListItemClick(clickedPosition,"share_general_data");
                }
            });


        }

        private void bind(int listIndex){
            hotelNameTextView.setText(listHotels.get(listIndex).getHotelName());
            hotelAddressTextView.setText(mContext.getString(R.string.hotel_adapter_address_placeholder,listHotels.get(listIndex).getHotelAddress()));
            if(listHotels.get(listIndex).getHotelMinPrice().equals(mContext.getString(R.string.contact_hotel_price_details_message))) {
                hotelMinPriceTextView.setText(mContext.getString(R.string.contact_hotel_price_details_message));
            } else {
                hotelMinPriceTextView.setText(mContext.getString(R.string.hotel_adapter_min_price_placeholder,listHotels.get(listIndex).getHotelMinPrice()));
            }
        }


        @Override
        public void onClick(View view) {
            //For future use
        }
    }


}
