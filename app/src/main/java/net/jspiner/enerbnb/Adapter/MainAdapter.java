package net.jspiner.enerbnb.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.google.zxing.integration.android.IntentIntegrator;

import net.jspiner.enerbnb.Activity.MapActivity;
import net.jspiner.enerbnb.Activity.PayActivity;
import net.jspiner.enerbnb.Model.SellerModel;
import net.jspiner.enerbnb.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project EnerBnB
 * @since 2016. 3. 26.
 */
public class MainAdapter extends BaseAdapter {

    //로그에 쓰일 tag
    public static final String TAG = MainAdapter.class.getSimpleName();

    LayoutInflater inflater;

    private SparseArray<WeakReference<View>> viewArray;

    public ArrayList<SellerModel> sellerList;
    Context context;

    public MainAdapter(Context context, ArrayList<SellerModel> sellerList){
        this.context = context;
        this.sellerList = sellerList;
        this.viewArray = new SparseArray<WeakReference<View>>(sellerList.size());
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(viewArray != null && viewArray.get(position) != null) {
            convertView = viewArray.get(position).get();
            if(convertView != null)
                return convertView;
        }

        try {

            SellerModel boardObject = sellerList.get(position);

            switch (position) {
                case 0:
                    convertView = inflater.inflate(R.layout.item_seller_header, null);
                    break;
                case 1:
                    convertView = inflater.inflate(R.layout.item_seller_qr, null);
                    break;
                case 2:
                    convertView = inflater.inflate(R.layout.item_seller_near, null);
                    break;
                case 3:
                    convertView = inflater.inflate(R.layout.item_seller_map, null);
                    break;

            }


            ViewBinder binder = new ViewBinder(convertView, position);

            if(position==2) binder.initPager();


        } finally {
            viewArray.put(position, new WeakReference<View>(convertView));
        }
        return convertView;
    }

    class ViewBinder {

        @Nullable
        @Bind(R.id.pager_seller_near)
        ViewPager pager;

        @Nullable
        @Bind(R.id.linear_seller_qr)
        LinearLayout linearQr;

        int position;

        public ViewBinder(View view, final int position){
            this.position = position;
            ButterKnife.bind(this, view);

        }

        void initPager(){
            NearSellerAdapter adapter = new NearSellerAdapter(context);

            pager.setAdapter(adapter);

            pager.setClipToPadding(false);
            pager.setPadding(80, 0, 80, 0);
        }

        @Nullable
        @OnClick(R.id.linear_seller_qr)
        void onQrClick(){

            Intent intent = new Intent(context, PayActivity.class);
            context.startActivity(intent);

        }

        @Nullable
        @OnClick(R.id.imv_seller_map)
        void onMapClick(){
            Intent intent = new Intent(context, MapActivity.class);
            context.startActivity(intent);
        }

    }

    @Override
    public int getCount() {
        return sellerList.size();
    }

    @Override
    public Object getItem(int position) {
        return sellerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void update() {
        viewArray.clear();
        notifyDataSetChanged();
    }
}
