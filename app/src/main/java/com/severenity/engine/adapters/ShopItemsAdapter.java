package com.severenity.engine.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.severenity.R;
import com.severenity.entity.ShopItem;

import java.util.List;

/**
 * Created by Odinn on 04.09.2016.
 */
public class ShopItemsAdapter extends RecyclerView.Adapter<ShopItemsAdapter.ShopItemsListHolder>{
    private List<ShopItem> shopItemList;

    public ShopItemsAdapter(List<ShopItem> shopItemList){
        this.shopItemList = shopItemList;
    }

    @Override
    public ShopItemsListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_grid_item, parent, false);
        return new ShopItemsListHolder(view);
    }

    @Override
    public void onBindViewHolder(ShopItemsListHolder holder, int position) {
        final ShopItem item = shopItemList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvDescrip.setText(item.getDescription());
        holder.ivItemPhoto.setImageResource(item.getImageURL());
    }

    @Override
    public int getItemCount() {
        return shopItemList.size();
    }

    public class ShopItemsListHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        ImageView ivItemPhoto;
        TextView tvDescrip;
        Button btnPurchase;
        public ShopItemsListHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvShopItemTitle);
            ivItemPhoto = (ImageView) itemView.findViewById(R.id.ivShopItemImage);
            tvDescrip = (TextView) itemView.findViewById(R.id.tvShopItemDescription);
            btnPurchase = (Button) itemView.findViewById(R.id.btnShopItemButton);
        }
    }
}
