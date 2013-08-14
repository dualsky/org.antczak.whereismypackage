package org.antczak.whereIsMyPackage;

import java.util.Hashtable;

import org.antczak.whereIsMyPackage.utils.ReadResource;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

public class History {

    private static final String TAG = "History";

    private final String DATABASE_NAME = "history";
    private final String DATABASE_TABLE = "history";

    private SQLiteDatabase myDB;
    private Hashtable<Integer, String> queryMap = new Hashtable<Integer, String>();
    private SparseArray<String> sortField = new SparseArray<String>();

    public History() {
	super();
	myDB = MainApplication.getAppContext().openOrCreateDatabase(
		DATABASE_NAME, 0, null);

	sortField.put(0, "lastUpdate");
	sortField.put(1, "addDate");
	sortField.put(2, "packageNumber");
	sortField.put(3, "courierName");

    }

    // po nowemu
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void updateSchema() {
	String oldVersion = MainApplication.getPrefs().getString(
		"oldAppVersion", "0");

	if (!oldVersion.equals("0") && oldVersion.compareTo("1.2.0") >= 0) {
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
    public Cursor getHistory(int sortField, int sortOrder) {
	String q = String.format(getQuery(R.raw.select_packages),
		this.sortField.get(sortField), sortOrder == 0 ? "ASC" : "DESC");
	Log.v(TAG, q);
	return myDB.rawQuery(q, null);

    }

    public int getMonitoredCount() {
	Cursor c = myDB.rawQuery(getQuery(R.raw.select_monitored_count), null);
	c.moveToFirst();
	int count = c.getInt(0);
	c.close();
	return count;
    }

    public void clearHistory() {
	myDB.rawQuery(getQuery(R.raw.delete_all_from_tags_packages), null);
	myDB.rawQuery(getQuery(R.raw.delete_all_from_packages), null);
    }

    public void clearMonitored() {
	myDB.rawQuery(getQuery(R.raw.update_clear_monitored), null);
    }

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

    public void deleteFromHistory(String packageNumber) {
	String sql = "delete from " + DATABASE_TABLE
		+ " where packageNumber = '" + packageNumber + "'";
	Log.v(TAG, "deleteFromHistory: " + sql);
	myDB.execSQL(sql);
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

    public void closeConnection() {
	if (this.myDB.isOpen())
	    this.myDB.close();
    }
}
