package com.severenity.engine.adapters;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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
 * Created by Novosad on 04.09.2016.
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
        holder.tvDescription.setText(item.getDescription());
        holder.ivItemPhoto.setImageResource(item.getImageURL());

        String value;
        if (item.getCredits() <= 0) {
            value = item.getPrice() + " $";
        } else {
            value = item.getCredits() + " credits";
        }

        holder.tvPrice.setText(value);

        holder.btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want to buy "+ item.getTitle())
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // TODO: Make some realization
                            }
                        })
                        .setNegativeButton("No",null)
                        .setCancelable(true);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return shopItemList.size();
    }

    class ShopItemsListHolder extends RecyclerView.ViewHolder{
        TextView tvTitle;
        ImageView ivItemPhoto;
        TextView tvDescription;
        Button btnPurchase;
        TextView tvPrice;
        ShopItemsListHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvShopItemTitle);
            ivItemPhoto = (ImageView) itemView.findViewById(R.id.ivShopItemImage);
            tvDescription = (TextView) itemView.findViewById(R.id.tvShopItemDescription);
            btnPurchase = (Button) itemView.findViewById(R.id.btnShopItemButton);
            tvPrice = (TextView) itemView.findViewById(R.id.tvShopItemPrice);
        }
    }
}
