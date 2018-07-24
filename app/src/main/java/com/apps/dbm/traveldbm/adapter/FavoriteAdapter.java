package com.apps.dbm.traveldbm.adapter;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.FrameMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.apps.dbm.traveldbm.R;
import com.apps.dbm.traveldbm.classes.Favorite;
import com.apps.dbm.traveldbm.data.CollectionContract;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private static final String LOG = FavoriteAdapter.class.getSimpleName();

    private List<Favorite> listFavorites;

    private int totalItems;

    private Context mContext;

    private UpdateWidgetsItemsListener mlistener;

    public interface UpdateWidgetsItemsListener{
        void updateWidgets();
    }

    public FavoriteAdapter(int numberOfFavorites, List<Favorite> list, Context context,UpdateWidgetsItemsListener listener) {
        listFavorites = list;
        totalItems = numberOfFavorites;
        mContext = context;
        mlistener = listener;
    }


    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.favorite_item_two, parent, false);

        FavoriteViewHolder viewHolher = new FavoriteViewHolder(view);

        return viewHolher;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return totalItems;
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder {

        FrameLayout container;

        TextView favoriteNameTV;
        TextView favoriteAddressTV;
        TextView favoritePhoneTV;
        TextView favoriteLocationTV;

        Button deleteFavoriteButton;


        public FavoriteViewHolder(View itemView) {
            super(itemView);


            container = (FrameLayout) itemView.findViewById(R.id.item_container);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            int heightValue = displayMetrics.heightPixels;
            container.requestLayout();
            if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                container.getLayoutParams().height = heightValue / 2;
            } else {
                container.getLayoutParams().height = heightValue;
            }


            favoriteNameTV = (TextView) itemView.findViewById(R.id.favorite_name);
            favoriteAddressTV = (TextView) itemView.findViewById(R.id.favorite_address);
            favoritePhoneTV = (TextView) itemView.findViewById(R.id.favorite_phone);
            favoriteLocationTV = (TextView) itemView.findViewById(R.id.favorite_location);
            deleteFavoriteButton = (Button) itemView.findViewById(R.id.delete_favorite_button);

        }

        public void bind(final int listIndex) {
            //index = listIndex;
            favoriteNameTV.setText(listFavorites.get(listIndex).getFavoriteName());
            favoriteAddressTV.setText("Address: " + listFavorites.get(listIndex).getFavoriteAddress());
            favoritePhoneTV.setText("Phone: " + listFavorites.get(listIndex).getFavoritePhone());
            favoriteLocationTV.setText("Location: " + listFavorites.get(listIndex).getFavoriteLocation());
            deleteFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //index = listIndex;
                    String propertyCodeFavorite = listFavorites.get(listIndex).getFavoritePropertyCode();
                    Uri wantedUri = Uri.withAppendedPath(CollectionContract.CollectionEntry.CONTENT_URI, propertyCodeFavorite);
                    mContext.getContentResolver().delete(wantedUri, null, null);
                    mlistener.updateWidgets();
                }
            });
        }
    }
}
