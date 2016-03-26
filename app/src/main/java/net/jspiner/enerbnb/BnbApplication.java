package net.jspiner.enerbnb;

import android.app.Application;

/**
 * Copyright 2016 JSpiner. All rights reserved.
 *
 * @author JSpiner (jspiner@naver.com)
 * @project EnerBnB
 * @since 2016. 3. 26.
 */
public class BnbApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    void init(){
        Util.context = getBaseContext();
    }
}
