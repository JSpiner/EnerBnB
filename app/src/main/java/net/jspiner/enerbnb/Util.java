package net.jspiner.enerbnb;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.internal.Primitives;

import net.jspiner.enerbnb.Model.HttpService;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import retrofit.Endpoint;
import retrofit.RestAdapter;
import retrofit.client.Response;

/**
 * Copyright 2015 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project TraTalk
 * @since 2015. 12. 31.
 */
public class Util {

    //로그에 쓰일 tag
    public static final String TAG = Util.class.getSimpleName();

    //debug mode
    public static boolean DEBUG_MODE = true;

    //context
    public static Context context;

    //aes 암호 seed
    private static String seed = "ZCXpveXjFTRA83Yh73hgACFq";

    //앱내에서 모든 http 통신은 httpservice로 동작
    private static HttpService httpService;

    //http통신시 endPoint를 이 변수로 변경, ex) endPoint.setPort("80");
    public static FooEndPoint endPoint;

    //HttpService의 EndPoint
    public static class FooEndPoint implements Endpoint {
        private static final String BASE = context.getResources().getString(R.string.API_SERVER);

        private String url = BASE;

        public void setPort(String port) {
            url = BASE +":"+ port;
        }

        @Override
        public String getName() {
            return "default";
        }

        @Override
        public String getUrl() {
            Log.d(TAG, "url : " + url);
            if (url == null) setPort("80");
            return url;
        }
    }

    //Singleton Endpoint
    public static FooEndPoint getEndPoint(){
        if(endPoint==null){
            endPoint = new FooEndPoint();
        }
        return endPoint;
    }

    //Singleton HttpService
    public static HttpService getHttpSerivce() {

        if(httpService==null) {

            RestAdapter restAdapter =
                    new RestAdapter.Builder()
                            .setEndpoint(getEndPoint())
                            .build();
            httpService = restAdapter.create(HttpService.class);
        }
        return httpService;
    }

    public static String getPhoneNum(){
        TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
//        return "01021289446";
        return mPhoneNumber;
    }

    public static String hash(){
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()";
        Random rnd = new Random();

        int len = 16;

        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }

    private static long setMilisec(long timestamp){
        if(Math.log10(timestamp)>10) return timestamp;
        return timestamp*1000;
    }

    public static String timeToString(long timestamp){
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 a HH:mm");
//        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return  format.format(setMilisec(timestamp));
    }

    public static String timeToSimpleString(long timestamp){
        SimpleDateFormat format = new SimpleDateFormat("HH시 mm분");
//        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return  format.format(setMilisec(timestamp));
    }

    public static String timeToDateString(long timestamp){
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");
//        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return  format.format(setMilisec(timestamp));
    }

    //파일 이미지 리사이징 클래스
    /*
    파일 크기가 아닌 해상도 기준으로 1024미만으로 리사이징함
     */

    public static File resizeImage(File file){
        if(file==null) return file;

        Log.d(TAG,"resize start");

        File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        Bitmap bmpPic= BitmapFactory.decodeFile(file.getPath());
        Log.d(TAG,"resize loaded");

        int MAX_IMAGE_SIZE = 200 * 1024; // max final file size
        File finalPath = new File(dir+"/resize.png");
        if ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
            BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
            bmpOptions.inSampleSize = 1;
            while ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
                bmpOptions.inSampleSize++;
                bmpPic = BitmapFactory.decodeFile(file.getPath(), bmpOptions);
            }
            Log.d(TAG, "Resize: " + bmpOptions.inSampleSize);
        }

        int compressQuality = 104; // quality decreasing by 5 every loop. (start from 99)
        int streamLength = MAX_IMAGE_SIZE;

        while (streamLength >= MAX_IMAGE_SIZE) {
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            compressQuality -= 5;
            Log.d(TAG, "Quality: " + compressQuality);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            Log.d(TAG, "Size: " + streamLength);
        }
        try {
            Log.d(TAG,"path : "+finalPath);
            FileOutputStream bmpFile = new FileOutputStream(finalPath);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
            Log.e(TAG, "Error on saving file" + e.getMessage());
        }

        return finalPath;
    }

    /*
    잠금화면을 위한 활성 액티비티 갯수 관리 클래스

    public static class AppStateTracker{
        private static int activeActivities = 0;

        public static void addActivity(){
            //어플리케이션 최초 실행
            if(activeActivities==0){

                List<TSettingObject> object = TSettingObject.find(TSettingObject.class, "t_key = ?", "setpass");
                if(object==null || object.size()==0) {

                }
                else {
                    if(object.get(0).gettValue().equals("true")) {
                        Intent intent = new Intent(context, LockActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            }
            activeActivities++;
            Log.d(TAG,"add activity count : "+activeActivities);
        }

        public static void delActivity(){
            activeActivities--;
            //어플리케이션의 모든 액티비티가 종료됨
            if(activeActivities==0){

            }
            Log.d(TAG,"del activity count : "+activeActivities);
        }

    }
*/
    /*

    유저정보 캐싱하는 모듈
    캐싱기준
        1. 반드시 업데이트후 정보를 보여주야하는경우(결제 등) : updateName(id)
        2. 업데이트에 일정시간이 소요되도 상관 없는경우(채팅, 프로필이미지 등) : getName(id)

    getName 업데이트기준 : 마지막 업데이트 이후 1~2시간후(랜덤) 조회시 update함

    이모티콘 캐싱기준 : 내 id로 캐싱할 경우 캐싱 안된 이모티콘이 발견되면 캐싱안된부분만 영구저장
    (이모티콘 구매시 업데이트 호출 필요)

    결제리스트 캐싱기준 : 내 id로 캐싱할경우 완전캐싱함.



    public static class NameCacher{

        public static NameModel updateName(final String id){
            Log.d(TAG, "update name id : " + id);
            final NameModel[] nameModel = {null};
            Thread netThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        Log.d(TAG, "id : " + id);
                        nameModel[0] = getJSonfromReponse(
                                getHttpSerivce().getuserinformation(id),
                                NameModel.class);
                        nameModel[0].setData(id);
                        nameModel[0].save();
                        Log.d(TAG, "user info : " + new Gson().toJson(nameModel[0]));


                        if(id.equals(LoginToken.getLoginToken().getUserid()) && nameModel[0]!=null){

                             if(nameModel[0].emoticonlist!=null)
                            for (EmoModel model : nameModel[0].emoticonlist){
                                List<EmoticonModel> tmp = EmoticonModel.find(EmoticonModel.class,
                                        "packnum = ?",String.valueOf(model.packnum));

                                if(tmp==null || tmp.size()==0){
                                    EmoticonModel cachModel = getJSonfromReponse(
                                            getHttpSerivce().emoticon_list(model.packnum),
                                            EmoticonModel.class
                                    );
                                    for(EmoticonModel.EmoticonObject saveModel : cachModel.emotlist){
                                        EmoticonModel emoModel = new EmoticonModel();
                                        emoModel.idx = saveModel.idx;
                                        emoModel.packnum = saveModel.groupnum;
                                        emoModel.link = saveModel.link;
                                        emoModel.save();
                                    }
                                    Log.d(TAG,"emo cached");
                                }
                                else{

                                }
                            }
                        }

                    }
                    catch (Exception error){
                        Log.e(TAG,"error : "+error.getMessage());
                        error.printStackTrace();
                    }


                }
            });
            netThread.start();
            try {
                Log.d(TAG,"join");
                netThread.join();
                Log.d(TAG, "thread end");
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d(TAG, "error");
            }

            return nameModel[0];

        }

        public static NameModel getName(String id){

            Log.d(TAG,"getName id : "+id);
            NameModel nameModel = null;
            try {
                nameModel = NameModel.getNameModel(id);//NameModel.find(NameModel.class, "uid = ?", id).get(0);

            }
            catch (Exception error){
                Log.e(TAG, "name error : " + error.getMessage());
                error.printStackTrace();
            }

            if(nameModel==null){ //저장된게 없음
                Log.d(TAG,"saved id not found");

                nameModel = updateName(id);
            }
            else{ //저장된게 있음
                long diffTime = System.currentTimeMillis() - nameModel.savedtime;
                diffTime = diffTime/1000/60/60;
                if(diffTime*10> (new Random()).nextInt(10)+10){
                    Log.d(TAG,"id time out" + "lasttime : "+nameModel.savedtime+" nowtime : "+System.currentTimeMillis());
                    nameModel = updateName(id);
                }
                Log.d(TAG,"loaded");
            }
            return nameModel;
        }
    }

    public static String getAnnanimousName(String id){

        int sum=0;
        for(int i=0;i<id.length();i++){
            sum += (int) id.substring(i,i+1).charAt(0);
        }

        return "익명의 "+ annanimous[sum%annanimous.length];
    }*/

    // 암호화
    public static String encrypt(String clearText) {
        byte[] encryptedText = null;
        try {
            byte[] keyData = seed.getBytes();
            SecretKey ks = new SecretKeySpec(keyData, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, ks);
            encryptedText = c.doFinal(clearText.getBytes("UTF-8"));
            return Base64.encodeToString(encryptedText, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    //복호화
    public static String decrypt (String encryptedText) {
        byte[] clearText = null;
        try {
            byte[] keyData = seed.getBytes();
            SecretKey ks = new SecretKeySpec(keyData, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, ks);
            clearText = c.doFinal(Base64.decode(encryptedText, Base64.DEFAULT));
            return new String(clearText, "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    //getContentResolver -> contentResolver
    public static String getRealPathFromURI(ContentResolver contentResolver, Uri contentURI) {
        String result;
        Cursor cursor = contentResolver.query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    //view 메모리 정리
    public static void recursiveRecycle(View root) {
        if (root == null)
            return;
        root.setBackgroundDrawable(null);
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)root;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                recursiveRecycle(group.getChildAt(i));
            }

            if (!(root instanceof AdapterView)) {
                group.removeAllViews();
            }

        }

        if (root instanceof ImageView) {
            ((ImageView)root).setImageDrawable(null);
        }
        root = null;

        return;
    }

    public static float dpToPx(float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float pxTodp(float px){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    /**
     * 뷰에 대해 새로운 id값을 할당
     */
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    //retrofit response를 string으로 변경
    public static String getStringfromReponse(Response response){
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {

            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));

            String line;

            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        String result = sb.toString();
        return  result;
    }

    //<T> T fromJson(String json, Class<T> classOfT)
    //retrofit response를 json 변경
    public static <T> T getJSonfromReponse(Response response, Class<T> classT){
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));

            String line;

            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        String result = sb.toString();
        Log.d(TAG,"json convert : "+result);
        return  Primitives.wrap(classT).cast((new Gson()).fromJson(result, (Type) classT));
    }

}
