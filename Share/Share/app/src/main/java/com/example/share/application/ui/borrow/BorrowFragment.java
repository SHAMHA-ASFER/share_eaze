package com.example.share.application.ui.borrow;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.share.R;
import com.example.share.connection.ServerAPI;
import com.example.share.connection.inteface.ItemListener;
import com.example.share.constants.StaticData;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BorrowFragment extends Fragment {

    LinearLayout availableItems;
    LayoutInflater in;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_borrow, container, false);
        in = inflater;
        availableItems = root.findViewById(R.id.availableItems);

        updateList();

        return root;
    }

    @SuppressLint({"SetTextI18n", "UseRequireInsteadOfGet"})
    public void updateList() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                availableItems.removeAllViews();
                for (int i = 0; i < StaticData.otherItems.size(); i++) {
                    View itemView = in.inflate(R.layout.availableblock, availableItems, false);
                    TextView itemname = itemView.findViewById(R.id.itemName);
                    TextView category = itemView.findViewById(R.id.categoryName);
                    TextView price = itemView.findViewById(R.id.price);
                    TextView iid = itemView.findViewById(R.id.iid);
                    TextView description = itemView.findViewById(R.id.description);
                    Button borrow = itemView.findViewById(R.id.borrow);

                    itemname.setText(StaticData.otherItems.get(i).getName());
                    category.setText(StaticData.otherItems.get(i).getCate());
                    price.setText(Float.toString(StaticData.otherItems.get(i).getPrice()));
                    iid.setText(Integer.toString(StaticData.otherItems.get(i).getIid()));
                    description.setText(StaticData.otherItems.get(i).getDesc());
                    int final_i = i;
                    borrow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int inner_i = final_i;
                            ServerAPI.requestItem(new ItemListener() {
                                @Override
                                public void onItemRequested() {
                                    message("Request Sent Successfully");
                                }
                            }, Integer.toString(StaticData.otherItems.get(inner_i).getIid()));
                        }
                    });
                    availableItems.addView(itemView);
                }
            });
        });
        executorService.shutdown();
    }

    @SuppressLint("UseRequireInsteadOfGet")
    public void message(String msg) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            });
        });
        executorService.shutdown();
    }

    @SuppressLint("UseRequireInsteadOfGet")
    public void popMessage(String title, String msg) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(title);
                builder.setMessage(msg);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            });
        });
        executorService.shutdown();
    }
}