package com.example.share.application.ui.item;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.share.R;
import com.example.share.application.Dashboard;
import com.example.share.connection.ServerAPI;
import com.example.share.connection.inteface.ItemListener;
import com.example.share.constants.StaticData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ItemFragment extends Fragment {

    LinearLayout itemlistLayout;
    LayoutInflater in;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_item, container, false);
        in = inflater;
        itemlistLayout = root.findViewById(R.id.itemlistlayout);

        updateList();

        return root;
    }

    @SuppressLint("SetTextI18n")
    public void updateList() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            getActivity().runOnUiThread(() -> {
                itemlistLayout.removeAllViews();
                for (int i = 0; i < StaticData.userItems.size(); i++) {
                    View itemView = in.inflate(R.layout.itemblock, itemlistLayout, false);
                    TextView itemname = itemView.findViewById(R.id.itemName);
                    TextView category = itemView.findViewById(R.id.categoryName);
                    TextView price = itemView.findViewById(R.id.price);
                    TextView description = itemView.findViewById(R.id.description);
                    ImageButton deleteItem = itemView.findViewById(R.id.deleteItem);
                    itemname.setText(StaticData.userItems.get(i).getName());
                    category.setText(StaticData.userItems.get(i).getCate());
                    price.setText(Float.toString(StaticData.userItems.get(i).getPrice()));
                    description.setText(StaticData.userItems.get(i).getDesc());
                    itemlistLayout.addView(itemView);
                    int final_i = i;
                    deleteItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int inner_i = final_i;
                            ServerAPI.deleteItem(new ItemListener() {
                                @Override
                                public void onItemDeleted() {
                                    updateList();
                                    StaticData.userItems.remove(StaticData.userItems.get(inner_i));
                                }
                            }, Integer.toString(StaticData.userItems.get(inner_i).getIid()));
                        }
                    });
                }
            });
        });
        executorService.shutdown();
    }

}