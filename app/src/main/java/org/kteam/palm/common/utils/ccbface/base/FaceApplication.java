package org.kteam.palm.common.utils.ccbface.base;

import android.app.Application;

/**
 * Created by wutw on 2018/1/9 0009.
 */
public class FaceApplication extends Application {
    @Override
    public void onCreate() {
        initCcbEverisk();
        super.onCreate();
    }

        public void initCcbEverisk(){
//            com.bangcle.everisk.loader.LibProc.init(getApplicationContext());
        }

}
