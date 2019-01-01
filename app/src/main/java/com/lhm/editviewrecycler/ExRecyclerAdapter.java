package com.lhm.editviewrecycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created on 2018/12/20 10:21
 * <p>
 * author lhm
 * <p>
 * Description:
 * <p>
 * Remarks: Recycler 解决editView 复用的adapter
 */
public class ExRecyclerAdapter extends RecyclerView.Adapter<ExRecyclerAdapter.ViewHolder> {

    private ArrayList<ItemBean> datas;
    private LayoutInflater mInflater;
    private int mLayoutId;

    public ExRecyclerAdapter(Context context, ArrayList<ItemBean> data, int layoutId) {
        this.datas = data;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(mLayoutId, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //设置指示 位置
        holder.num.setText(position + 1 + "");
        //第0 不能有删除功能
        if (position == 0) {
            holder.tvDelete.setVisibility(View.GONE);
        } else {
            holder.tvDelete.setVisibility(View.VISIBLE);
        }
        //判断是否有TextWatcher监听事件，有的话先移除
        if (holder.place.getTag(R.id.place_value) instanceof TextWatcher) {
            holder.place.removeTextChangedListener(((TextWatcher) holder.place.getTag(R.id.place_value)));
        }
        //移除了TextWatcher事件后设置item对应的文本
        holder.place.setText(datas.get(position).getPlace());

        //设置焦点
        if (datas.get(position).isFocus()) {
            if (!holder.place.isFocused()) {
                holder.place.requestFocus();
            }
            CharSequence text = datas.get(position).getPlace();
            holder.place.setSelection(TextUtils.isEmpty(text) ? 0 : text.length());
        } else {
            if (holder.place.isFocused()) {
                holder.place.clearFocus();
            }
        }
        //edit 事件处理
        holder.place.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                final boolean focus = datas.get(position).isFocus();
                check(position);
                if (!focus && !holder.place.isFocused()) {
                    holder.place.requestFocus();
                    holder.place.onWindowFocusChanged(true);
                }
            }
            return false;
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    datas.get(position).setPlace(null);
                } else {
                    //监听edit 值
                    datas.get(position).setPlace(s.toString());
                }
            }
        };
        //设置tag为TextWatcher
        holder.place.addTextChangedListener(textWatcher);
        holder.place.setTag(R.id.place_value, textWatcher);

        //删除该条数据
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0) {
                    datas.remove(position);
                    notifyItemRemoved(position);
                    if (position != datas.size()) {
                        notifyItemRangeChanged(position, getItemCount());
                    }
//                    datas.remove(position);
//                    notifyDataSetChanged();
                }
            }
        });
    }

    //  添加数据
    public void addData(ItemBean itemBean) {
        itemBean.setFocus(true);
//      在list中添加数据，并通知条目加入一条
        datas.add(datas.size(), itemBean);
        //添加动画
        notifyItemInserted(datas.size());

//        if (position != mData.size()) {
//            otifyItemRangeChanged(position, mData.size() - position);
//        }
    }

    //设置状态
    private void check(int position) {
        for (ItemBean l : datas) {
            l.setFocus(false);
        }
        datas.get(position).setFocus(true);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView num;
        private EditText place;
        private TextView tvDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            num = itemView.findViewById(R.id.num);
            place = itemView.findViewById(R.id.place_value);
            tvDelete = itemView.findViewById(R.id.tv_detele);

        }


    }
}
