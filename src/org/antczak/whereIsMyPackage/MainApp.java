
package org.antczak.whereIsMyPackage;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;

import org.antczak.whereIsMyPackage.db.DBWrapper;

public class MainApp extends Application {

    public static String TAG = "MainApp";
    public static String VERSION_CODE = "AppVersionCode";

    private static DBWrapper mDB;
    private static Context mContext;
    private static SharedPreferences mPrefs;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();

        mContext = getApplicationContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mDB = new DBWrapper();
        try {
            int newVersionCode = getApplicationContext().getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0).versionCode;
            int oldVersionCode = mPrefs.getInt(VERSION_CODE, -1);

            if (oldVersionCode != newVersionCode) {
                Log.e(TAG, "updateSchema. old version:" + oldVersionCode + ", new version:"
                        + newVersionCode);
                mDB.updateSchema(oldVersionCode);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putInt(VERSION_CODE, newVersionCode);
                editor.commit();
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, e.getStackTrace().toString());
        }
    }

    @Override
    public void onTerminate() {
        Log.i(TAG, "onTerminate");
        super.onTerminate();
    }

    public static DBWrapper getDB() {
        return mDB;
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static SharedPreferences getPrefs() {
        return mPrefs;
    }

}
