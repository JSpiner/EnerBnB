package net.jspiner.enerbnb.Adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.jspiner.enerbnb.R;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project EnerBnB
 * @since 2016. 3. 26.
 */
public class NearSellerAdapter  extends PagerAdapter {

    //로그에 쓰일 tag
    public static final String TAG = NearSellerAdapter.class.getSimpleName();

    private LayoutInflater mInflater;
    Context context;

    int res[] = {R.drawable.img_seller_1, R.drawable.img_seller_2, R.drawable.img_seller_3, R.drawable.img_seller_4, R.drawable.img_seller_5, R.drawable.img_seller_6, R.drawable.img_seller_7};

    public NearSellerAdapter(Context c){
        super();
        this.context = c;
        mInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object instantiateItem(View pager, int position) {
        View v = null;

        v = mInflater.inflate(R.layout.item_seller_image_row, null);

        ViewBinder binder = new ViewBinder(v);

        ((ViewPager)pager).addView(v, 0);

        return v;
    }

    @Override
    public void destroyItem(View pager, int position, Object view) {
        ((ViewPager)pager).removeView((View)view);
    }

    @Override
    public boolean isViewFromObject(View pager, Object obj) {
        return pager == obj;
    }

    @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
    @Override public Parcelable saveState() { return null; }
    @Override public void startUpdate(View arg0) {}
    @Override public void finishUpdate(View arg0) {}

    public class ViewBinder{

        @Bind(R.id.imv_seller_image_row)
        ImageView imvRow;

        public ViewBinder(View view){
            ButterKnife.bind(this, view);

            init();
        }

        void init(){

            Log.d(TAG,"init image");

            Picasso.with(context)
                    .load(res[new Random().nextInt(res.length)])
                    .fit()
                    .into(imvRow);
        }
    }
}
