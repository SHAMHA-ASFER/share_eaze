package com.example.share.application.ui.lend;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.share.R;
import com.example.share.connection.ServerAPI;
import com.example.share.connection.inteface.ItemListener;
import com.example.share.constants.Category;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LendFragment extends Fragment {

    LayoutInflater in;

    EditText itemName;
    Spinner spinner;
    EditText itemPrice;
    EditText itemWidth;
    EditText itemHeight;
    EditText itemLength;
    EditText itemDescription;

    Button addItem;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lend, container, false);
        in = inflater;

        itemName = root.findViewById(R.id.itemName);
        spinner = root.findViewById(R.id.spinner);
        itemPrice = root.findViewById(R.id.price);
        itemWidth = root.findViewById(R.id.width);
        itemHeight = root.findViewById(R.id.height);
        itemLength = root.findViewById(R.id.length);
        itemDescription = root.findViewById(R.id.description);
        addItem = root.findViewById(R.id.addItem);

        String[] categories = new String[Category.values().length];
        int count = 0;
        for (Category cat : Category.values()) {
            categories[count] = cat.getCategory();
            count++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    ServerAPI.addItem(new ItemListener() {
                        @SuppressLint("UseRequireInsteadOfGet")
                        @Override
                        public void onItemAdded() {
                            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Item added", Toast.LENGTH_SHORT).show();
                            });
                        }
                        @Override
                        public void onItemAlreadyExists() {
                            popMessage("Item Error!", "The item name is already exists");
                        }
                    },      itemName.getText().toString(),
                            spinner.getSelectedItem().toString(),
                            itemPrice.getText().toString(),
                            itemDescription.getText().toString(),
                            itemWidth.getText().toString(),
                            itemHeight.getText().toString(),
                            itemLength.getText().toString());
                });
                executorService.shutdown();
            }
        });

        return root;
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