package net.jspiner.enerbnb.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nirhart.parallaxscroll.views.ParallaxListView;
import com.squareup.picasso.Picasso;

import net.jspiner.enerbnb.Adapter.MainAdapter;
import net.jspiner.enerbnb.Model.SellerModel;
import net.jspiner.enerbnb.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

public class MainActivity extends AppCompatActivity {

    //로그에 쓰일 tag
    public static final String TAG = MainActivity.class.getSimpleName();

    //toolbar
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_toolbar_title)
    TextView tvTitle;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.lv_main_list)
    ParallaxListView lvList;
    @Bind(R.id.bg_loading)
    LinearLayout linearLoading;

    MainAdapter adapter;

    boolean loadingMore;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            initUpdateFrame();
            initParallaxScroll();

            handler2.sendEmptyMessageDelayed(0,1500);
        }
    };

    Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            linearLoading.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    void init(){
        ButterKnife.bind(this);

        linearLoading.setVisibility(View.VISIBLE);
        initToolbar();


        handler.sendEmptyMessageDelayed(0, 2000);
    }

    void initUpdateFrame(){
        PtrClassicFrameLayout mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_list_view_frame);

        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
//                updateData();
//                reloadData();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                Log.d(TAG,"refresh checkCanDoRefresh");
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        mPtrFrame.setPullToRefresh(false);
        mPtrFrame.setKeepHeaderWhenRefresh(true);

        final MaterialHeader header = new MaterialHeader(MainActivity.this);
        int[] colors = {Color.BLUE,Color.RED,Color.GRAY,Color.CYAN};
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 25, 0, 25);
        header.setPtrFrameLayout(mPtrFrame);

        mPtrFrame.setLoadingMinTime(1000);
        mPtrFrame.setDurationToCloseHeader(1500);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);
    }

    void initParallaxScroll(){

        View header = LayoutInflater.from(this).inflate(R.layout.item_main_header,null);
        ArrayList<SellerModel> arrayList = new ArrayList<>();
        for(int i=0;i<4;i++){
            arrayList.add(new SellerModel());
        }
        adapter = new MainAdapter(MainActivity.this,arrayList );

        ViewBinder binder = new ViewBinder(header);

        lvList.addParallaxedHeaderView(header);
        lvList.setAdapter(adapter);

        lvList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if(totalItemCount<=1) return;
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(loadingMore)) {

                    loadmore();
                }
            }
        });
    }

    void loadmore(){

    }

    //actionbar 설정
    void initToolbar(){

        tvTitle.setText("에너비엔비");
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,toolbar,R.string.app_name,R.string.app_name){

            @Override
            public void onDrawerOpened(View drawerView) {
                Log.d(TAG, "drawer opened");
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                Log.d(TAG,"drawer closed");
                super.onDrawerClosed(drawerView);

            }

        };

        drawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

    }

    @OnClick(R.id.fab_room_chat)
    void onSearchClick(){
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

    public class ViewBinder{

        @Bind(R.id.imv_header_row)
        ImageView imvRow;

        public ViewBinder(View view){
            ButterKnife.bind(this,view);

            init();
        }

        void init(){

            Picasso.with(getBaseContext())
                    .load(R.drawable.img_city_1)
                    .fit()
                    .into(imvRow);
        }
    }

}
