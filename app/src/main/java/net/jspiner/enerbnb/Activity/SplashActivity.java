package net.jspiner.enerbnb.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import net.jspiner.enerbnb.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project EnerBnB
 * @since 2016. 3. 25.
 */
public class SplashActivity extends Activity {

    //로그에 쓰일 tag
    public static final String TAG = SplashActivity.class.getSimpleName();

    @Bind(R.id.btn_splash_login)
    LoginButton loginButton;
    @Bind(R.id.linear_splash_logo)
    LinearLayout linearLogo;

    CallbackManager callbackManager;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(AccessToken.getCurrentAccessToken() == null){
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
//            moveAnime();
           /* Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();*/
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        showHashKey(getBaseContext());
        init();
    }

    void init() {
        ButterKnife.bind(this);
        initFacebookSdk();
        handler.sendEmptyMessageDelayed(0, 2000);
    }

    void initFacebookSdk() {

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d(TAG, "login success");
                        Toast.makeText(getBaseContext(), "로그인을 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d(TAG, "login cancel");
                        Toast.makeText(getBaseContext(), "로그인을 취소하셨습니다.", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d(TAG, "login error");
                        Toast.makeText(getBaseContext(), "에러가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d(TAG, "login success");
                        Toast.makeText(getBaseContext(), "로그인을 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d(TAG, "login cancel");
                        Toast.makeText(getBaseContext(), "로그인을 취소하셨습니다.", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException error) {
                        // App code
                        Log.d(TAG, "login error");
                        Toast.makeText(getBaseContext(), "에러가 발생했습니다.", Toast.LENGTH_SHORT).show();

                    }
                });

    }

    public void showHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    "net.jspiner.enerbnb", PackageManager.GET_SIGNATURES); //Your            package name here
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }

    void moveAnime(){
        TranslateAnimation ani = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -0.44f);
        ani.setFillAfter(true);
        ani.setDuration(1500);

        loginButton.setVisibility(View.VISIBLE);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(2000);
        loginButton.startAnimation(fadeIn);

        linearLogo.startAnimation(ani);
    }

    @Override
    protected void onResume() {
//        this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        this.overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
