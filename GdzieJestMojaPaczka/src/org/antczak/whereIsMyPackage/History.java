package org.antczak.whereIsMyPackage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class History {

	private static final String TAG = "History";

	private final String DATABASE_NAME = "history";
	private final String DATABASE_TABLE = "history";
	private SQLiteDatabase myDB;

	public History(Context context) {
		super();
		String createTable = ""
				+ "CREATE TABLE IF NOT EXISTS history "
				+ "             ( "
				+ "                          addDate       INTEGER PRIMARY KEY DESC   , "
				+ "                          packageNumber VARCHAR NOT NULL UNIQUE    , "
				+ "                          courierName   VARCHAR NOT NULL           , "
				+ "                          courierCode   VARCHAR NOT NULL           , "
				+ "                          monitor       INTEGER NOT NULL DEFAULT 0 , "
				+ "                          rows          INTEGER NOT NULL DEFAULT 0 , "
				+ "                          desc          VARCHAR NULL DEFAULT NULL	 , "
				+ "							checkCount	  INTEGERE NOT NULL DEFAULT 0  "
				+ "             )";

		myDB = context.openOrCreateDatabase(DATABASE_NAME, 0, null);
		
		//myDB.execSQL("drop table if exists " + DATABASE_NAME);
		
		String tableExist = "pragma table_info (history)";
		Log.v(TAG, "tableExist: " + tableExist);
		Cursor c = myDB.rawQuery(tableExist, null);
		if (c.getCount() == 6) {
			String rename = "ALTER TABLE history RENAME TO oldhistory";
			Log.v(TAG, "rename: " + rename);
			myDB.execSQL(rename);
			Log.v(TAG, "createTable: " + createTable);
			myDB.execSQL(createTable);
			String moveData = "INSERT 																"
					+ "INTO   history 														"
					+ "SELECT   MAX(adddate) , 												"
					+ "         packagenumber, 												"
					+ "         couriername  , 												"
					+ "         couriercode  , 												"
					+ "         monitor      , 												"
					+ "         rows         , 												"
					+ "         NULL         , 												"
					+ "         0 															"
					+ "FROM     oldhistory 													"
					+ "GROUP BY packagenumber";
			Log.v(TAG, "moveData: " + moveData);
			myDB.execSQL(moveData);
			String dropOld = "DROP TABLE oldhistory";
			Log.v(TAG, "dropOld: " + dropOld);
			myDB.execSQL(dropOld);
		} else {
			Log.v(TAG, "createTable: " + createTable);
			myDB.execSQL(createTable);
		}
	}

	public void addToHistory(String packageNumber, String courierName,
			String courierCode, String rows) {
		String sql = "insert or ignore into history(addDate, packageNumber,courierName, courierCode, rows) "
				+ " values (strftime('%s','now')," + "'" + packageNumber
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

	public Cursor getHistory(int limit) {
		String sql = ""
				+"SELECT MAX(adddate) + 1 AS _id          , " 
				+"       '-1'             AS packageNumber, " 
				+"       COUNT(*)         AS courierName  , " 
				+"       1                AS courierCode  , " 
				+"       1                AS monitor      , " 
				+"       'monitor'        AS DESC " 
				+"FROM   history " 
				+"WHERE  monitor = 1 " 
				+" " 
				+"UNION " 
				+" " 
				+"SELECT adddate      , " 
				+"       packagenumber, " 
				+"       couriername  , " 
				+"       couriercode  , " 
				+"       monitor      , " 
				+"       DESC " 
				+"FROM   history " 
				+"WHERE  monitor = 1 " 
				+" " 
				+"UNION " 
				+" " 
				+"SELECT MAX(adddate) + 1, " 
				+"       '-2'            , " 
				+"       COUNT(*)        , " 
				+"       1               , " 
				+"       0               , " 
				+"       'nie monitor' " 
				+"FROM   history " 
				+"WHERE  monitor = 0 " 
				+" " 
				+"UNION " 
				+" " 
				+"SELECT   adddate      , " 
				+"         packagenumber, " 
				+"         couriername  , " 
				+"         couriercode  , " 
				+"         monitor      , " 
				+"         DESC " 
				+"FROM     history " 
				+"WHERE    monitor = 0 " 
				+"ORDER BY monitor DESC, " 
				+"         _id DESC";

		Log.v(TAG, "getHistory: " + sql);
		return myDB.rawQuery(sql, null);
	}

	/**
	 * Count sum of all package checks
	 * @return int 
	 */
	public int getHistoryCount() {
		Cursor c = (Cursor) myDB.query(DATABASE_TABLE,
				new String[] { "sum(checkCount)" }, "", new String[] {}, "", "", "",
				"");
		c.moveToFirst();
		int count = c.getInt(0);
		c.close();
		Log.v("History", "getHistoryCount: " + count);
		return count;
	}

	/**
	 * Lista monitorowanych wykorzystywana w Service
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
		String sql = "update " + DATABASE_TABLE
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
		String sql = "update " + DATABASE_TABLE 
			+ " set desc = '" + desc + "', adddate = strftime('%s','now') " 
			+ "where packageNumber = '" + packageNumber + "'";
		Log.v(TAG, "addDesc: " + sql);
		myDB.execSQL(sql);
	}

	public void deleteDesc(String packageNumber) {
		String sql = "update " + DATABASE_TABLE
				+ " set desc = null, adddate = strftime('%s','now') " 
				+ "where packageNumber = '" + packageNumber
				+ "'";
		Log.v(TAG, "addDesc: " + sql);
		myDB.execSQL(sql);
	}
}
