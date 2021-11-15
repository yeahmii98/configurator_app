package com.example.jjinmak;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> implements ItemTouchHelperListener{

    private ArrayList<Data> listData = new ArrayList<>();

    public interface OnCheckedChangeListener {
        void OnCheckedChange(TextView v, boolean b);
    }
    private OnCheckedChangeListener mListener = null ;
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.mListener = listener ;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.setItem(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(Data data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }


    @Override
    public boolean onItemMove(int from_position, int to_position) {
        return false;
    }

    public void onItemSwipe(int position) {
        listData.remove(position);
        notifyItemRemoved(position);
    }


    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView text_mac;
        private TextView text_netRole;
        private TextView text_status;
        private TextView text_name;
        private ToggleButton tog_status;
        private ImageView img_netRole;


        ItemViewHolder(View itemView) {
            super(itemView);
            tog_status = itemView.findViewById(R.id.toggleBtn);
            img_netRole = itemView.findViewById(R.id.netRoleImage);
            text_mac = itemView.findViewById(R.id.text_mac);
            text_name = itemView.findViewById(R.id.text_name);
            text_netRole = itemView.findViewById(R.id.text_netRole);

            if(text_name.getText().equals("dpp_ap"))
            {
                Log.d("fuck", "disable");
                tog_status.setEnabled(false);
                tog_status.setVisibility(View.INVISIBLE);
            }

            tog_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b) {
                        Log.d("fuck", "True" + text_mac.getText());
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            if (mListener != null) {
                                mListener.OnCheckedChange(text_mac, b);
                            }
                        }
                    }
                    else {
                        Log.d("fuck", "False" + text_mac.getText());
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            if (mListener != null) {
                                mListener.OnCheckedChange(text_mac, b);
                            }
                        }
                    }
                }
            });

        }

        void setItem(Data data) {
            tog_status.setTag(data.getID());
            text_mac.setText(data.getAddr());
            text_name.setText(data.getName());
            img_netRole.setImageResource(data.getNetRoleImage());
            if(text_name.getText().equals("dpp_ap"))
            {
                Log.d("fuck", "disable");
                tog_status.setEnabled(false);
                tog_status.setVisibility(View.INVISIBLE);
            }
        }
    }
}

