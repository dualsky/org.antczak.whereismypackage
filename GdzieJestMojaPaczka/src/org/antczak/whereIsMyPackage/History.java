package org.antczak.whereIsMyPackage;

import java.util.Hashtable;

import org.antczak.whereIsMyPackage.utils.ReadResource;

import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class History {

    private static final String TAG = "History";

    private final String DATABASE_NAME = "history";
    private final String DATABASE_TABLE = "history";
    private SQLiteDatabase myDB;
    private Hashtable<Integer, String> queryMap = new Hashtable<Integer, String>();

    public History() {
	super();
	myDB = MainApplication.getAppContext().openOrCreateDatabase(
		DATABASE_NAME, 0, null);

    }

    // po nowemu
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void updateSchema() {
	String oldVersion = MainApplication.getPrefs().getString(
		"oldAppVersion", "0");

	if (oldVersion.compareTo("1.2.0") >= 0) {
	    myDB.execSQL(getQuery(R.raw.create_packages));
	    myDB.execSQL(getQuery(R.raw.create_tags));
	    myDB.execSQL(getQuery(R.raw.create_tags_packages));
	    myDB.execSQL(getQuery(R.raw.update_from_120));
	    myDB.execSQL(getQuery(R.raw.drop_history));
	    fillTags();
	} else {
	    myDB.execSQL(getQuery(R.raw.create_packages));
	    myDB.execSQL(getQuery(R.raw.create_tags));
	    myDB.execSQL(getQuery(R.raw.create_tags_packages));
	    myDB.execSQL(getQuery(R.raw.insert_samples_1));
	    myDB.execSQL(getQuery(R.raw.insert_samples_2));
	    fillTags();
	}
    }

    // /////////////////////////////////////////
    // Packages
    // /////////////////////////////////////////

    public Cursor getHistory(int limit) {
	return myDB.rawQuery(getQuery(R.raw.select_packages), null);
    }

    // /////////////////////////////////////////
    // Tags
    // /////////////////////////////////////////

    private void fillTags() {
	for (String tag : ReadResource.getStringArray(
		MainApplication.getAppContext(), R.array.tags)) {
	    myDB.execSQL(String.format(getQuery(R.raw.insert_tags), tag));
	}
    }
    
    public Cursor getTags() {
	return myDB.rawQuery(getQuery(R.raw.select_tags), null);
    }

    // po staremu
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void addToHistory(String packageNumber, String courierName,
	    String courierCode, String rows) {
	String sql = "insert or ignore into history(addDate, packageNumber,courierName, courierCode, rows) "
		+ " values (strftime('%s','now'),"
		+ "'"
		+ packageNumber
		+ "','" + courierName + "','" + courierCode + "'," + rows + ")";
	Log.v(TAG, "addToHistory: " + sql);
	myDB.execSQL(sql);
    }

    public void clearHistory() {
	String sql = "delete from " + DATABASE_TABLE;
	Log.v(TAG, "clearHistory: " + sql);
	myDB.execSQL(sql);
    }

    public void clearMonitored() {
	String sql = "update " + DATABASE_TABLE + " set monitor = 0";
	Log.v(TAG, "clearMonitored: " + sql);
	myDB.execSQL(sql);
    }

    public void deleteFromHistory(String packageNumber) {
	String sql = "delete from " + DATABASE_TABLE
		+ " where packageNumber = '" + packageNumber + "'";
	Log.v(TAG, "deleteFromHistory: " + sql);
	myDB.execSQL(sql);
    }

    /**
     * Count sum of all package checks
     * 
     * @return int
     */
    public int getHistoryCount() {
	Cursor c = (Cursor) myDB.query(DATABASE_TABLE,
		new String[] { "sum(checkCount)" }, "", new String[] {}, "",
		"", "", "");
	c.moveToFirst();
	int count = c.getInt(0);
	c.close();
	Log.v("History", "getHistoryCount: " + count);
	return count;
    }

    /**
     * Lista monitorowanych wykorzystywana w Service
     * 
     * @return Cursor
     */
    public Cursor getMonitored() {
	String sql = "select max(addDate) as _id, packageNumber, courierName, courierCode, monitor, rows "
		+ "from "
		+ DATABASE_TABLE
		+ " "
		+ "where monitor = 1 "
		+ "group by packageNumber, courierName, courierCode, monitor "
		+ "order by addDate desc";
	Log.v("History", "getMonitored: " + sql);
	return myDB.rawQuery(sql, null);
    }

    public void oneMoreCheck(String packageNumber) {
	String sql = "update "
		+ DATABASE_TABLE
		+ " set checkCount = checkCount+1, adddate = strftime('%s','now') "
		+ "where packageNumber = '" + packageNumber + "'";
	Log.v(TAG, "oneMoreCheck: " + sql);
	myDB.execSQL(sql);
    }

    public int getMonitoredCount() {
	String sql = "select count(*) " + "from " + DATABASE_TABLE
		+ " where monitor = 1 ";
	Log.v("History", "getMonitoredCount: " + sql);
	Cursor c = (Cursor) myDB.rawQuery(sql, null);
	c.moveToFirst();
	int count = c.getInt(0);
	c.close();
	Log.v("History", "getMonitoredCount: " + count);
	return count;
    }

    public void startMonitoring(String packageNumber) {
	String sql = "update " + DATABASE_TABLE
		+ " set monitor = 1, adddate = strftime('%s','now') "
		+ " where packageNumber = '" + packageNumber + "'";
	Log.v(TAG, "startMonitoring: " + sql);
	myDB.execSQL(sql);
    }

    public void stopMonitoring(String packageNumber) {
	String sql = "update " + DATABASE_TABLE
		+ " set monitor = 0, adddate = strftime('%s','now') "
		+ "where packageNumber = '" + packageNumber + "'";
	Log.v(TAG, "stopMonitoring: " + sql);
	myDB.execSQL(sql);
    }

    public void closeConnection() {
	if (this.myDB.isOpen())
	    this.myDB.close();
    }

    /**
     * Zmienia iloœæ wierszy. Dla Service.
     * 
     * @param packageNumber
     * @param rows
     */
    public void updateRowsCount(String packageNumber, int rows) {
	String sql = "update " + DATABASE_TABLE + " set rows = " + rows
		+ " where packageNumber = '" + packageNumber
		+ "' and monitor = 1";
	Log.v(TAG, "updateRowsCount: " + sql);
	myDB.execSQL(sql);
    }

    public void addDesc(String packageNumber, String desc) {
	String sql = "update " + DATABASE_TABLE + " set desc = '" + desc
		+ "', adddate = strftime('%s','now') "
		+ "where packageNumber = '" + packageNumber + "'";
	Log.v(TAG, "addDesc: " + sql);
	myDB.execSQL(sql);
    }

    public void deleteDesc(String packageNumber) {
	String sql = "update " + DATABASE_TABLE
		+ " set desc = null, adddate = strftime('%s','now') "
		+ "where packageNumber = '" + packageNumber + "'";
	Log.v(TAG, "addDesc: " + sql);
	myDB.execSQL(sql);
    }

    private String getQuery(int queryId) {
	if (!queryMap.containsKey(queryId)) {
	    queryMap.put(queryId, ReadResource.getString(
		    MainApplication.getAppContext(), queryId));
	}
	return queryMap.get(queryId);

    }
}
