package net.jspiner.enerbnb.Activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.jspiner.enerbnb.Adapter.StoreMenuAdapter;
import net.jspiner.enerbnb.BillingUtil.IabHelper;
import net.jspiner.enerbnb.BillingUtil.IabResult;
import net.jspiner.enerbnb.BillingUtil.Purchase;
import net.jspiner.enerbnb.Model.Menu;
import net.jspiner.enerbnb.Model.SubMenu;
import net.jspiner.enerbnb.R;
import net.jspiner.enerbnb.View.AnimatedExpandableListView;

import org.json.JSONObject;

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
public class PayActivity extends AppCompatActivity {


    IInAppBillingService mService;
    IabHelper mHelper;

    @Bind(R.id.lv_menu)
    AnimatedExpandableListView listView;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        init();
    }

    void init(){

        new IntentIntegrator(PayActivity.this).initiateScan();

        ButterKnife.bind(this);
        initPayList();
        initBilling();
    }

    @OnClick(R.id.btn_checkmenu_submit)
    void onPayClick(){
        Buy("0");
    }

    void initPayList(){

        ArrayList<Menu> mList = new ArrayList<>();
        Menu m = new Menu("용량단위 걸제", R.drawable.sample1);
        m.items.add(new SubMenu("100kwh",1000));
        m.items.add(new SubMenu("150kwh", 1500));
        m.items.add(new SubMenu("200kwh",2000));
        m.items.add(new SubMenu("300kwh", 3000));
        m.items.add(new SubMenu("400kwh",4000));
        m.items.add(new SubMenu("500kwh", 5000));
        mList.add(m);
        m = new Menu("시간단위 결제", R.drawable.sample2);
        m.items.add(new SubMenu("10분", 1000));
        m.items.add(new SubMenu("30분", 3000));
        m.items.add(new SubMenu("1시간", 6000));
        mList.add(m);
        m = new Menu("초과요금 결제", R.drawable.sample3);
        m.items.add(new SubMenu("10분", 1000));
        m.items.add(new SubMenu("30분", 3000));
        m.items.add(new SubMenu("1시간", 6000));
        mList.add(m);

        StoreMenuAdapter adapter = new StoreMenuAdapter(getBaseContext());
        adapter.setData(mList);

        listView.setAdapter(adapter);
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                if (listView.isGroupExpanded(i)) {
                    listView.collapseGroupWithAnimation(i);
                } else {
                    listView.expandGroupWithAnimation(i);
                }
                return true;
            }
        });
    }


    void initBilling(){
        helperInit();
    }


    private void helperInit()
    {
        Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);

        // 구글에서 발급받은 바이너리키를 입력해줍니다
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyJ3Iv7GcGHex8ZfaNJzH4eu6tdUKUmeFcuF5NRJ8cfCM1eITYnPLmQBb1Qgs0UXzGVrkVHU0QEoOBF/GYr/iq7aGkHxUw6NGeNQFOWUAOY0ExXKSkZzy+/+vYEGheb2vu382h0Zp7CGfy2F0nUdZRik4x0s3EA8kunLiDD/uinyRdLbO4xFf/hYfupBty0YhTbfmV8k2hBIw+iD7Edx9vB8snKswMoUlCf0ThJnSjpl2/IIUKGrn07uRaK1NEjOigFa7J7jxMLDryP5rTqpWooz/w45m+wzIeXDaQkRaIhuY7fzJP0GGVX37lsxxmrVXHMaGMTF5yrU3BRowS0ALKwIDAQAB\n";

        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // 구매오류처리 ( 토스트하나 띄우고 결제팝업 종료시키면 되겠습니다 )
                    Toast.makeText(PayActivity.this, "결제가 막혔습니다.22", Toast.LENGTH_SHORT).show();
                }

                // 구매목록을 초기화하는 메서드입니다.
                // v3으로 넘어오면서 구매기록이 모두 남게 되는데 재구매 가능한 상품( 게임에서는 코인같은아이템은 ) 구매후 삭제해주어야 합니다.
                // 이 메서드는 상품 구매전 혹은 후에 반드시 호출해야합니다. ( 재구매가 불가능한 1회성 아이템의경우 호출하면 안됩니다 )
                //AlreadyPurchaseItems();
            }
        });
    }
    public void AlreadyPurchaseItems()
    {
        try {
            Bundle ownedItems = mService.getPurchases(3, getPackageName(),
                    "inapp", null);
            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> purchaseDataList = ownedItems
                        .getStringArrayList("INAPP_PURCHASE_DATA_LIST");

                String[] tokens = new String[purchaseDataList.size()];

                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = (String) purchaseDataList.get(i);
                    JSONObject jo = new JSONObject(purchaseData);
                    tokens[i] = jo.getString("purchaseToken");
                    // 여기서 tokens를 모두 컨슘 해주기
                    mService.consumePurchase(3, getPackageName(), tokens[i]);
                }
            }

            // 토큰을 모두 컨슘했으니 구매 메서드 처리
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Buy(String id_item) {
        // Var.ind_item = index;
        try {
            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                    id_item, "inapp", "서버에서 발급한 영수증 키");
            PendingIntent pendingIntent = buyIntentBundle
                    .getParcelable("BUY_INTENT");
            //mHelper.launchPurchaseFlow(this, id_item, 1001, mPurchaseFinishedListener);

            if (pendingIntent != null) {
                startIntentSenderForResult(pendingIntent.getIntentSender(),
                        1001, new Intent(), Integer.valueOf(0),
                        Integer.valueOf(0), Integer.valueOf(0));

            } else {
                Toast.makeText(this, "결제가 막혔습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            // 여기서 아이템 추가 해주시면 됩니다.
            // 만약 서버로 영수증 체크후에 아이템 추가한다면, 서버로 purchase.getOriginalJson() ,
            // purchase.getSignature() 2개 보내시면 됩니다.
            if(result.isSuccess())
            {
                AlreadyPurchaseItems();

                Toast.makeText(getBaseContext(),"결제가 완료되었습니다",Toast.LENGTH_LONG).show();

            }

        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            if (!mHelper.handleActivityResult(requestCode, resultCode, data))
            {
                super.onActivityResult(requestCode, resultCode, data);

                int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

                AlreadyPurchaseItems();
            }
            else
            {
                Toast.makeText(getBaseContext(),"결제가 완료되었습니다33",Toast.LENGTH_LONG).show();
                Log.d("PurchaseActivity", "onActivityResult handled by IABUtil.");
            }
        }
        else{
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null) {
                if(result.getContents() == null) {
                    Log.d("MainActivity", "Cancelled scan");
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("MainActivity", "Scanned");
                    Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                }
            } else {
                // This is important, otherwise the result will not be passed to the fragment
                super.onActivityResult(requestCode, resultCode, data);
            }
        }


    }

}
