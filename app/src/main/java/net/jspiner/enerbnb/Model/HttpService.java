package net.jspiner.enerbnb.Model;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Copyright 2015 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project MySmartRestaurant
 * @since 2015. 11. 14.
 */
public interface HttpService {

        // 모든 REST API들은 이곳에 기입됨.
        @FormUrlEncoded
        @POST("/enerbnb/api/use.php")
        void push(
                @Field("uuid") String uuid,
                Callback<String> ret);
}
