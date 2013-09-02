
package org.antczak.whereIsMyPackage.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.antczak.whereIsMyPackage.MainApp;
import org.antczak.whereIsMyPackage.R;
import org.antczak.whereIsMyPackage.util.ReadResource;

import java.util.Hashtable;

public class DBWrapper {

    private static final String TAG = "DBWrapper";

    private final String DATABASE_NAME = "whereIsMyPackage";

    private SQLiteDatabase mDB;
    private Hashtable<Integer, String> mQueryMap = new Hashtable<Integer, String>();

    public DBWrapper() {
        mDB = MainApp.getAppContext().openOrCreateDatabase(DATABASE_NAME, 0,
                null);
    }

    public void updateSchema(int oldVersionCode) {
        switch (oldVersionCode) {
            case 9:
                // TODO add other versions
                break;
            default:
            case 10:
                mDB.execSQL(getQuery(R.raw.create_tags));
                fillTags();

        }
    }

    // ////////////////////////////////////////////////////////
    // Tags
    // ////////////////////////////////////////////////////////

    private void fillTags() {
        for (String tag : ReadResource.getStringArray(
                MainApp.getAppContext(), R.array.tags)) {
            mDB.execSQL(getQuery(R.raw.insert_tags), new Object[] {
                    tag
            });
        }
    }

    public Cursor getTags() {
        return mDB.rawQuery(getQuery(R.raw.select_tags), null);
    }

    // ////////////////////////////////////////////////////////
    // Commons
    // ////////////////////////////////////////////////////////

    private String getQuery(int queryId) {
        if (!mQueryMap.containsKey(queryId)) {
            mQueryMap.put(queryId,
                    ReadResource.getDrawable(MainApp.getAppContext(), queryId));
        }
        return mQueryMap.get(queryId);
    }

    public void closeConnection() {
        if (mDB != null && mDB.isOpen())
            mDB.close();
    }
}
