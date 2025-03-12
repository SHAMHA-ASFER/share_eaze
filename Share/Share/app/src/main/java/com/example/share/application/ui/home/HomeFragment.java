package com.example.share.application.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.share.R;
import com.example.share.connection.ItemAccepted;
import com.example.share.connection.ItemDeclined;
import com.example.share.connection.ItemRequest;
import com.example.share.connection.ServerAPI;
import com.example.share.connection.inteface.BorrowListener;
import com.example.share.connection.inteface.ItemListener;
import com.example.share.connection.inteface.VerificationListener;
import com.example.share.constants.StaticData;
import com.google.android.material.internal.TextWatcherAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {
    LinearLayout borrowsLayout;
    LinearLayout boxesLayout;
    ConstraintLayout constraintLayout;
    LayoutInflater in;
    View root;

    Activity activity;
    Context context;
    static boolean isStared = false;
    static boolean isEditMode = false;
    static boolean isMsgPoped = false;
    static PopupWindow popupWindow;
    static Calendar calendar = Calendar.getInstance();
    LinearLayout layout;
    RenderThread renderThread;
    @SuppressLint("SimpleDateFormat")
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    ArrayList<ItemRequest> removed = new ArrayList<>();
    ArrayList<ItemDeclined> declineds = new ArrayList<>();
    ArrayList<ItemAccepted> accepteds = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        in = inflater;
        this.root = root;
        borrowsLayout = root.findViewById(R.id.borrowsContainer);
        boxesLayout = root.findViewById(R.id.boxesContainer);
        constraintLayout = root.findViewById(R.id.constraint);

        activity = getActivity();
        context = getContext();

        if (!isStared) {
            renderThread = new RenderThread();
            renderThread.start();
        }

        return root;
    }

    @SuppressLint("UseRequireInsteadOfGet")
    public void popupRequest() {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            if (!isMsgPoped) {
                for (int i = 0; i < StaticData.itemRequests.size(); i++) {
                    if (removed.isEmpty()) {
                        View view = in.inflate(R.layout.requestblock, null);
                        TextView name = view.findViewById(R.id.borrower);
                        TextView item = view.findViewById(R.id.item);
                        Button accept = view.findViewById(R.id.accept);
                        Button decline = view.findViewById(R.id.decline);
                        name.setText(StaticData.itemRequests.get(i).getReplyUsr());
                        item.setText(StaticData.itemRequests.get(i).getItemName());
                        accept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ServerAPI.acceptRequest(new ItemListener() {
                                    @Override
                                    public void onItemRequestAccepted() {
                                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                            popupWindow.dismiss();
                                        });
                                        isMsgPoped = false;
                                    }
                                    }, StaticData.itemRequests.get(i).getReplyUsr(), StaticData.itemRequests.get(i).getItemName(), String.valueOf(StaticData.itemRequests.get(i).getRid()));
                                removed.add(StaticData.itemRequests.get(i));
                                StaticData.itemRequests.remove(StaticData.itemRequests.get(i));
                            }
                        });

                        decline.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ServerAPI.declineRequest(new ItemListener() {
                                    @Override
                                    public void onItemRequestDeclined() {
                                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                            popupWindow.dismiss();
                                        });
                                        isMsgPoped = false;
                                    }
                                }, StaticData.itemRequests.get(i).getReplyUsr(), StaticData.itemRequests.get(i).getItemName(), String.valueOf(StaticData.itemRequests.get(i).getRid()));
                                removed.add(StaticData.itemRequests.get(i));
                                StaticData.itemRequests.remove(StaticData.itemRequests.get(i));
                            }
                        });
                        popView(view);
                        isMsgPoped = true;
                    } else {
                        for (int j = 0; j < removed.size(); j++) {
                            int finalJ = j;
                            if (StaticData.itemRequests.get(i).getRid() != removed.get(j).getRid()) {
                                View view = in.inflate(R.layout.requestblock, null);
                                TextView name = view.findViewById(R.id.borrower);
                                TextView item = view.findViewById(R.id.item);
                                Button accept = view.findViewById(R.id.accept);
                                Button decline = view.findViewById(R.id.decline);
                                name.setText(StaticData.itemRequests.get(i).getReplyUsr());
                                item.setText(StaticData.itemRequests.get(i).getItemName());

                                accept.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int j = finalJ;
                                        ServerAPI.acceptRequest(new ItemListener() {
                                            @Override
                                            public void onItemRequestAccepted() {
                                                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                                    removed.add(StaticData.itemRequests.get(j));
                                                    StaticData.itemRequests.remove(StaticData.itemRequests.get(j));
                                                    popupWindow.dismiss();
                                                });
                                                isMsgPoped = false;
                                            }
                                            }, StaticData.itemRequests.get(j).getReplyUsr(), StaticData.itemRequests.get(j).getItemName(), String.valueOf(StaticData.itemRequests.get(j).getRid())
                                        );
                                    }
                                });

                                decline.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int j = finalJ;
                                        ServerAPI.declineRequest(new ItemListener() {
                                            @Override
                                            public void onItemRequestDeclined() {
                                                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                                    popupWindow.dismiss();
                                                });
                                                isMsgPoped = false;
                                            }
                                        }, StaticData.itemRequests.get(j).getReplyUsr(), StaticData.itemRequests.get(j).getItemName(), String.valueOf(StaticData.itemRequests.get(j).getRid()));
                                        StaticData.itemRequests.remove(StaticData.itemRequests.get(j));
                                    }
                                });
                                popView(view);
                                isMsgPoped = true;
                            }
                        }
                    }
                    break;
                }
            }
        });
    }

    @SuppressLint({"UseRequireInsteadOfGet", "SetTextI18n"})
    public void popupDecline() {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            if (!isMsgPoped) {
                for (int i = 0; i < StaticData.itemDeclines.size(); i++) {
                    if (declineds.isEmpty()) {
                        View view = in.inflate(R.layout.declinedblock, null);
                        TextView msg = view.findViewById(R.id.message);
                        Button ok = view.findViewById(R.id.ok);
                        msg.setText(StaticData.itemDeclines.get(i).getReplyMsg() + " has declined your request for this " + StaticData.itemDeclines.get(i).getItemName());
                        int finalI = i;
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int i = finalI;
                                ServerAPI.okDecline(new ItemListener() {
                                    @Override
                                    public void onDeclineOk() {
                                        Log.d("OK DEC", "OK");
                                        declineds.add(StaticData.itemDeclines.get(i));
                                        StaticData.itemDeclines.remove(StaticData.itemDeclines.get(i));
                                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                            popupWindow.dismiss();
                                        });
                                        isMsgPoped = false;
                                    }
                                }, String.valueOf(StaticData.itemDeclines.get(finalI).getRid()));
                            }
                        });
                        popView(view);
                        isMsgPoped = true;
                        break;
                    } else {
                        for (int j = 0; j < declineds.size(); j++) {
                            if (declineds.get(j).getRid() != StaticData.itemDeclines.get(i).getRid()) {
                                View view = in.inflate(R.layout.declinedblock, null);
                                TextView msg = view.findViewById(R.id.message);
                                Button ok = view.findViewById(R.id.ok);
                                msg.setText(StaticData.itemDeclines.get(i).getReplyMsg() + " has declined your request for this " + StaticData.itemDeclines.get(i).getItemName());

                                int finalI = i;
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int i = finalI;
                                        ServerAPI.okDecline(new ItemListener() {
                                            @Override
                                            public void onDeclineOk() {
                                                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                                    popupWindow.dismiss();
                                                });
                                                isMsgPoped = false;
                                            }
                                        }, String.valueOf(StaticData.itemDeclines.get(i).getRid()));
                                        declineds.add(StaticData.itemDeclines.get(i));
                                        StaticData.itemDeclines.remove(StaticData.itemDeclines.get(i));
                                    }
                                });
                                popView(view);
                                isMsgPoped = true;
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    @SuppressLint("UseRequireInsteadOfGet")
    public void popupAccept() {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            if (!isMsgPoped) {
                for (int i = 0; i < StaticData.itemsAccepted.size(); i++) {
                    if (declineds.isEmpty()) {
                        View view = in.inflate(R.layout.acceptedblock, null);
                        TextView msg = view.findViewById(R.id.message);
                        Button ok = view.findViewById(R.id.ok);
                        msg.setText(StaticData.itemsAccepted.get(i).getReplyUsr() + " has accepted your request for this " + StaticData.itemsAccepted.get(i).getItemName() + ".");
                        int finalI = i;
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int i = finalI;
                                ServerAPI.okDecline(new ItemListener() {
                                    @Override
                                    public void onDeclineOk() {
                                        Log.d("OK DEC", "OK");
                                        accepteds.add(StaticData.itemsAccepted.get(i));
                                        StaticData.itemsAccepted.remove(StaticData.itemsAccepted.get(i));
                                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                            popupWindow.dismiss();
                                        });
                                        isMsgPoped = false;
                                    }
                                }, String.valueOf(StaticData.itemsAccepted.get(finalI).getRid()));
                            }
                        });
                        popView(view);
                        isMsgPoped = true;
                        break;
                    } else {
                        for (int j = 0; j < declineds.size(); j++) {
                            if (declineds.get(j).getRid() != StaticData.itemsAccepted.get(i).getRid()) {
                                View view = in.inflate(R.layout.declinedblock, null);
                                TextView msg = view.findViewById(R.id.message);
                                Button ok = view.findViewById(R.id.ok);
                                msg.setText(StaticData.itemsAccepted.get(i).getReplyUsr() + " has accepted your request for this " + StaticData.itemsAccepted.get(i).getItemName() + ".");
                                int finalI = i;
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int i = finalI;
                                        ServerAPI.okDecline(new ItemListener() {
                                            @Override
                                            public void onDeclineOk() {
                                                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                                    popupWindow.dismiss();
                                                });
                                                isMsgPoped = false;
                                            }
                                        }, String.valueOf(StaticData.itemsAccepted.get(i).getRid()));
                                        accepteds.add(StaticData.itemsAccepted.get(i));
                                        StaticData.itemsAccepted.remove(StaticData.itemsAccepted.get(i));
                                    }
                                });
                                popView(view);
                                isMsgPoped = true;
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    @SuppressLint({"UseRequireInsteadOfGet", "RestrictedApi", "SetTextI18n"})
    public void renderBorrows() {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            if (!isEditMode) {
                if (StaticData.borrows.isEmpty()) {
                    borrowsLayout.removeAllViews();
                    View NA = in.inflate(R.layout.notavailable, borrowsLayout, false);
                    borrowsLayout.addView(NA);
                } else {
                    borrowsLayout.removeAllViews();
                    for (int i = 0; i < StaticData.borrows.size(); i++) {
                        if (StaticData.borrows.get(i).getComplete() != 1) {
                            View Borrow = in.inflate(R.layout.borrow_block_not_paid, borrowsLayout, false);
                            TextView username = Borrow.findViewById(R.id.username);
                            TextView itemName = Borrow.findViewById(R.id.itemname);
                            TextView rate = Borrow.findViewById(R.id.rate);
                            EditText start_date = Borrow.findViewById(R.id.start_date);
                            EditText end_date = Borrow.findViewById(R.id.end_date);
                            TextView total = Borrow.findViewById(R.id.total);
                            Button pay = Borrow.findViewById(R.id.pay);

                            username.setText(StaticData.borrows.get(i).getItemOwner());
                            itemName.setText(StaticData.borrows.get(i).getItemName());
                            rate.setText(StaticData.borrows.get(i).getItemRate());
                            start_date.setText(StaticData.borrows.get(i).getStart_d().toString());
                            end_date.setText(StaticData.borrows.get(i).getEnd_d().toString());

                            int final_i = i;

                            start_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (hasFocus) {
                                        isEditMode = true;
                                    }
                                }
                            });

                            end_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (!hasFocus) {
                                        isEditMode = true;
                                    }
                                }
                            });

                            start_date.addTextChangedListener(new TextWatcherAdapter() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    int idx = final_i;
                                    if (checkDate(start_date.getText().toString()) && checkDate(end_date.getText().toString())) {
                                        if (start_date.getText().length() != end_date.getText().length()) {
                                            total.setText("Dates not correct");
                                        } else {
                                            Date today = getDate(simpleDateFormat.format(new Date()));
                                            Date sdate = getDate(start_date.getText().toString());
                                            Date edate = getDate(end_date.getText().toString());
                                            long dift = sdate.getTime() - today.getTime();
                                            long diff = edate.getTime() - sdate.getTime();
                                            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                                            if (dift >= 0) {
                                                if (days >= 1) {
                                                    StaticData.borrows.get(idx).setStart_d(sdate);
                                                    StaticData.borrows.get(idx).setStart_d(edate);
                                                    total.setText("Rs. " + Long.toString(days * Long.parseLong(StaticData.borrows.get(idx).getItemRate())) + "/-");
                                                } else {
                                                    total.setText("Least one day!");
                                                }
                                            } else {
                                                total.setText("Dates not correct");
                                            }
                                        }
                                    }
                                }
                            });

                            end_date.addTextChangedListener(new TextWatcherAdapter() {
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    int idx = final_i;
                                    if (checkDate(start_date.getText().toString()) && checkDate(end_date.getText().toString())) {
                                        if (start_date.getText().length() != end_date.getText().length()) {
                                            total.setText("Dates not correct");
                                        } else {
                                            Date today = getDate(simpleDateFormat.format(new Date()));
                                            Date sdate = getDate(start_date.getText().toString());
                                            Date edate = getDate(end_date.getText().toString());
                                            long dift = sdate.getTime() - today.getTime();
                                            long diff = edate.getTime() - sdate.getTime();
                                            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                                            if (dift >= 0) {
                                                if (days >= 1) {
                                                    StaticData.borrows.get(idx).setStart_d(sdate);
                                                    StaticData.borrows.get(idx).setStart_d(edate);
                                                    total.setText("Rs. " + Long.toString(days * Long.parseLong(StaticData.borrows.get(idx).getItemRate())) + "/-");
                                                } else {
                                                    total.setText("Least one day!");
                                                }
                                            } else {
                                                total.setText("Dates not correct");
                                            }
                                        }
                                    } else {
                                        total.setText("Format DD/MM/YYYY");
                                    }
                                }
                            });

                            pay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int i = final_i;
                                    View pop_card = in.inflate(R.layout.popup_card_info, null);
                                    EditText owner = pop_card.findViewById(R.id.name);
                                    EditText number = pop_card.findViewById(R.id.number);
                                    EditText expire = pop_card.findViewById(R.id.expir);
                                    EditText cvc = pop_card.findViewById(R.id.cvc);
                                    EditText amount = pop_card.findViewById(R.id.amount);
                                    Button makepay = pop_card.findViewById(R.id.makepay);

                                    makepay.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ServerAPI.makePayment(new BorrowListener() {
                                                @Override
                                                public void onPaid() {
                                                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                                        popupWindow.dismiss();
                                                    });
                                                    HomeFragment.isEditMode = false;
                                                }
                                            },
                                                    Integer.toString(StaticData.borrows.get(i).getBid()),
                                                    start_date.getText().toString(), end_date.getText().toString(),
                                                    owner.getText().toString(), number.getText().toString(),
                                                    expire.getText().toString(), cvc.getText().toString(),
                                                    amount.getText().toString(), Integer.toString(StaticData.borrows.get(i).getIid()));
                                        }
                                    });

                                    popView(pop_card);
                                }
                            });
                            borrowsLayout.addView(Borrow);
                        } else {
                            View Borrow = in.inflate(R.layout.paid_borrow_block, borrowsLayout, false);
                            TextView name = Borrow.findViewById(R.id.username);
                            TextView item = Borrow.findViewById(R.id.itemname);
                            TextView rate = Borrow.findViewById(R.id.rate);

                            name.setText(StaticData.borrows.get(i).getItemOwner());
                            item.setText(StaticData.borrows.get(i).getItemName());
                            rate.setText(Float.toString(StaticData.borrows.get(i).getPayment()));

                            borrowsLayout.addView(Borrow);
                        }
                    }
                }
            }
        });
    }

    @SuppressLint({"UseRequireInsteadOfGet", "SetTextI18n", "DefaultLocale"})
    public void renderBoxes() {
        Objects.requireNonNull(getActivity()).runOnUiThread(()-> {
            boxesLayout.removeAllViews();
            for (int i = 0; i < StaticData.boxes.size(); i++) {
                boolean found = false;
                for (int j = 0; j < StaticData.lends.size(); j++) {
                    if (StaticData.boxes.get(i).getLock_id() == StaticData.lends.get(j).getLock_id() &&
                            StaticData.bid != StaticData.lends.get(j).getBid()  &&
                            StaticData.lends.get(j).getTarget() == 'l'          &&
                            StaticData.lends.get(j).getPhase() == 'p'           &&
                            StaticData.lends.get(j).getComplete() == 1          &&
                            StaticData.lends.get(j).getFinished() != 1) {
                        Log.d("STAGE", "LP");
                        View Box = in.inflate(R.layout.selectedboxesblock, boxesLayout, false);
                        TextView name = Box.findViewById(R.id.boxname);
                        TextView online = Box.findViewById(R.id.online);
                        TextView status = Box.findViewById(R.id.status);
                        TextView dimension = Box.findViewById(R.id.dimension);
                        Button send_code = Box.findViewById(R.id.send_code);
                        int finalJ = j;
                        Log.d("OUTPUT", "LP");
                        send_code.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int j = finalJ;
                                ServerAPI.sendVerificationCode(new VerificationListener() {
                                    @Override
                                    public void onCodeSend() {
                                        popMessage("Info", "Code Send");
                                    }
                                },
                                        String.valueOf(StaticData.lends.get(j).getLock_id()),
                                        String.valueOf(StaticData.lends.get(j).getBid()),
                                        String.valueOf(StaticData.lends.get(j).getIid()),
                                        String.valueOf(StaticData.lends.get(j).getVid()),
                                        String.valueOf(StaticData.lends.get(j).getTarget()) + String.valueOf(StaticData.lends.get(j).getPhase())
                                        );
                            }
                        });

                        name.setText(StaticData.boxes.get(i).getName());
                        online.setText(StaticData.boxes.get(i).getStatus());
                        status.setText("Available");
                        dimension.setText(
                                String.format("%dx%dx%d",
                                        StaticData.boxes.get(i).getHeight(),
                                        StaticData.boxes.get(i).getLength(),
                                        StaticData.boxes.get(i).getWidth())
                        );
                        found = true;
                        boxesLayout.addView(Box);
                    } else if (StaticData.boxes.get(i).getLock_id() == StaticData.lends.get(j).getLock_id() &&
                            StaticData.bid != StaticData.lends.get(j).getBid()  &&
                            StaticData.lends.get(j).getTarget() == 'l'          &&
                            StaticData.lends.get(j).getPhase() == 'g'           &&
                            StaticData.lends.get(j).getComplete() == 1          &&
                            StaticData.lends.get(j).getFinished() != 1) {
                        View Box = in.inflate(R.layout.selectedboxesblock, boxesLayout, false);
                        TextView name = Box.findViewById(R.id.boxname);
                        TextView online = Box.findViewById(R.id.online);
                        TextView status = Box.findViewById(R.id.status);
                        TextView dimension = Box.findViewById(R.id.dimension);Button send_code = Box.findViewById(R.id.send_code);
                        int finalJ = j;
                        send_code.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int j = finalJ;
                                ServerAPI.sendVerificationCode(new VerificationListener() {
                                                                   @Override
                                                                   public void onCodeSend() {
                                                                       popMessage("Info", "Code Send");
                                                                   }
                                                               },
                                        String.valueOf(StaticData.lends.get(j).getLock_id()),
                                        String.valueOf(StaticData.lends.get(j).getBid()),
                                        String.valueOf(StaticData.lends.get(j).getIid()),
                                        String.valueOf(StaticData.lends.get(j).getVid()),
                                        String.valueOf(StaticData.lends.get(j).getTarget()) + String.valueOf(StaticData.lends.get(j).getPhase())
                                );
                            }
                        });
                        name.setText(StaticData.boxes.get(i).getName());
                        online.setText(StaticData.boxes.get(i).getStatus());
                        status.setText("Available");
                        dimension.setText(
                                String.format("%dx%dx%d",
                                        StaticData.boxes.get(i).getHeight(),
                                        StaticData.boxes.get(i).getLength(),
                                        StaticData.boxes.get(i).getWidth())
                        );
                        found = true;
                        boxesLayout.addView(Box);
                    }
                }
                for (int j = 0; j < StaticData.borrows.size(); j++) {
                    int finalJ = j;
                    Log.d("STAGE",
                            String.valueOf(StaticData.boxes.get(i).getLock_id()) + " " +
                                    String.valueOf(StaticData.borrows.get(j).getLock_id()) + " " +
                                    String.valueOf(StaticData.borrows.get(j).getTarget()) + " " +
                                    String.valueOf(StaticData.borrows.get(j).getPhase()));
                    if (StaticData.boxes.get(i).getLock_id() == StaticData.borrows.get(j).getLock_id() &&
                            StaticData.bid == StaticData.borrows.get(j).getBid()    &&
                            StaticData.borrows.get(j).getTarget() == 'b'            &&
                            StaticData.borrows.get(j).getPhase() == 'g'             &&
                            StaticData.borrows.get(j).getComplete() == 1            &&
                            StaticData.borrows.get(j).getFinished() != 1) {
                        View Box = in.inflate(R.layout.selectedboxesblock, boxesLayout, false);
                        TextView name = Box.findViewById(R.id.boxname);
                        TextView online = Box.findViewById(R.id.online);
                        TextView status = Box.findViewById(R.id.status);
                        TextView dimension = Box.findViewById(R.id.dimension);
                        Button send_code = Box.findViewById(R.id.send_code);
                        name.setText(StaticData.boxes.get(i).getName());
                        online.setText(StaticData.boxes.get(i).getStatus());
                        status.setText("Available");
                        dimension.setText(
                                String.format("%dx%dx%d",
                                        StaticData.boxes.get(i).getHeight(),
                                        StaticData.boxes.get(i).getLength(),
                                        StaticData.boxes.get(i).getWidth())
                        );
                        found = true;
                        boxesLayout.addView(Box);
                        send_code.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int j = finalJ;
                                ServerAPI.sendVerificationCode(new VerificationListener() {
                                    @Override
                                    public void onCodeSend() {
                                        popMessage("Info", "Code Send");
                                    }
                                    },
                                        String.valueOf(StaticData.borrows.get(j).getLock_id()),
                                        String.valueOf(StaticData.borrows.get(j).getBid()),
                                        String.valueOf(StaticData.borrows.get(j).getIid()),
                                        String.valueOf(StaticData.borrows.get(j).getVid()),
                                        String.valueOf(StaticData.borrows.get(j).getTarget()) + String.valueOf(StaticData.borrows.get(j).getPhase())
                                );
                            }
                        });
                    } else if (StaticData.boxes.get(i).getLock_id() == StaticData.borrows.get(j).getLock_id() &&
                            StaticData.bid == StaticData.borrows.get(j).getBid()    &&
                            StaticData.borrows.get(j).getTarget() == 'b'            &&
                            StaticData.borrows.get(j).getPhase() == 'p'             &&
                            StaticData.borrows.get(j).getComplete() == 1            &&
                            StaticData.borrows.get(j).getFinished() != 1) {
                        View Box = in.inflate(R.layout.selectedboxesblock, boxesLayout, false);
                        TextView name = Box.findViewById(R.id.boxname);
                        TextView online = Box.findViewById(R.id.online);
                        TextView status = Box.findViewById(R.id.status);
                        TextView dimension = Box.findViewById(R.id.dimension);
                        Button send_code = Box.findViewById(R.id.send_code);
                        name.setText(StaticData.boxes.get(i).getName());
                        online.setText(StaticData.boxes.get(i).getStatus());
                        status.setText("Available");
                        dimension.setText(
                                String.format("%dx%dx%d",
                                        StaticData.boxes.get(i).getHeight(),
                                        StaticData.boxes.get(i).getLength(),
                                        StaticData.boxes.get(i).getWidth())
                        );
                        found = true;
                        send_code.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int j = finalJ;
                                ServerAPI.sendVerificationCode(new VerificationListener() {
                                                                   @Override
                                                                   public void onCodeSend() {
                                                                       popMessage("Info", "Code Send");
                                                                   }
                                                               },
                                        String.valueOf(StaticData.borrows.get(j).getLock_id()),
                                        String.valueOf(StaticData.borrows.get(j).getBid()),
                                        String.valueOf(StaticData.borrows.get(j).getIid()),
                                        String.valueOf(StaticData.borrows.get(j).getVid()),
                                        String.valueOf(StaticData.borrows.get(j).getTarget()) + String.valueOf(StaticData.borrows.get(j).getPhase())
                                );
                            }
                        });
                        boxesLayout.addView(Box);
                    }
                }
                if (!found && !(StaticData.borrows.isEmpty() && StaticData.lends.isEmpty())) {
                    Log.d("FOUND", "1");
                    View Box = in.inflate(R.layout.boxesblock, boxesLayout, false);
                    TextView name = Box.findViewById(R.id.boxname);
                    TextView online = Box.findViewById(R.id.online);
                    TextView status = Box.findViewById(R.id.status);
                    TextView dimension = Box.findViewById(R.id.dimension);
                    name.setText(StaticData.boxes.get(i).getName());
                    online.setText(StaticData.boxes.get(i).getStatus());
                    if (StaticData.boxes.get(i).getAvailability() == 1) {
                        status.setText("Not Available");
                    } else {
                        status.setText("Available");
                    }
                    dimension.setText(
                            String.format("%dx%dx%d",
                                    StaticData.boxes.get(i).getHeight(),
                                    StaticData.boxes.get(i).getLength(),
                                    StaticData.boxes.get(i).getWidth())
                    );
                    boxesLayout.addView(Box);
                }
                if (StaticData.borrows.isEmpty() && StaticData.lends.isEmpty()) {
                    Log.d("FOUND", "2");
                    View Box = in.inflate(R.layout.boxesblock, boxesLayout, false);
                    TextView name = Box.findViewById(R.id.boxname);
                    TextView online = Box.findViewById(R.id.online);
                    TextView status = Box.findViewById(R.id.status);
                    TextView dimension = Box.findViewById(R.id.dimension);
                    name.setText(StaticData.boxes.get(i).getName());
                    online.setText(StaticData.boxes.get(i).getStatus());
                    if (StaticData.boxes.get(i).getAvailability() == 1) {
                        status.setText("Not Available");
                    } else {
                        status.setText("Available");
                    }
                    dimension.setText(
                            String.format("%dx%dx%d",
                                    StaticData.boxes.get(i).getHeight(),
                                    StaticData.boxes.get(i).getLength(),
                                    StaticData.boxes.get(i).getWidth())
                    );
                    boxesLayout.addView(Box);
                }
            }
        });
    }

    public Date getDate(String date) {
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException ex) {
            return null;
        }
    }

    public boolean checkDate(String str) {
        try {
            Date date = simpleDateFormat.parse(str);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    private class RenderThread extends Thread {
        @Override
        public void run() {
            try {
                while (isAlive()) {
                    renderBorrows();
                    renderBoxes();
                    popupRequest();
                    popupDecline();
                    popupAccept();
                    Thread.sleep(500);
                }
                join();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void popView(View view) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        if (popupWindow != null) {
            getActivity().runOnUiThread(() -> {
                popupWindow.dismiss();
            });
            popupWindow = new PopupWindow(view, width, height, focusable);
            constraintLayout.post(new Runnable() {
                @Override
                public void run() {
                    popupWindow.showAtLocation(constraintLayout, Gravity.BOTTOM, 0, 0);
                }
            });
        } else {
            popupWindow = new PopupWindow(view, width, height, focusable);
            constraintLayout.post(new Runnable() {
                @Override
                public void run() {
                    popupWindow.showAtLocation(constraintLayout, Gravity.BOTTOM, 0, 0);
                }
            });
        }
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