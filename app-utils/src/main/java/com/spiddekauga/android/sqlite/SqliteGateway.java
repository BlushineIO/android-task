package com.spiddekauga.android.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all SQLite gateways
 */
public abstract class SqliteGateway {
private static SQLiteOpenHelper mSqlite = null;
private Map<Cursor, SQLiteDatabase> mOpenDbs = new HashMap<>();

/**
 * Initialize SQLite
 * @param sqliteOpenHelper object which we want to open sqlite within
 */
public static synchronized void setSqlite(SQLiteOpenHelper sqliteOpenHelper) {
	mSqlite = sqliteOpenHelper;

	// TODO Do all delete/insert/replace/update/execSql that was saved
}

/**
 * Convenience method for deleting rows in the database.
 * @param table the table to delete from
 * @param whereClause the optional WHERE clause to apply when deleting.
 * Passing null will delete all rows.
 * @return the number of rows affected if a whereClause is passed in, 0
 * otherwise. To remove all rows and get a count pass "1" as the
 * whereClause.
 */
protected int delete(String table, String whereClause) {
	if (isInitialized()) {
		SQLiteDatabase db = mSqlite.getWritableDatabase();
		db.delete(table, whereClause, null);
		db.close();
	}
	// TODO Save delete for later
	else {
	}
	return 0;
}

/**
 * @return true if initialized
 */
private static synchronized boolean isInitialized() {
	return mSqlite != null;
}

/**
 * Convenience method for inserting a row into the database.
 * @param table the table to insert the row into
 * @param values this map contains the initial column values for the
 * row. The keys should be the column names and the values the
 * column values
 * @return the row ID of the newly inserted row, or -1 if an error occurred
 */
protected long insert(String table, ContentValues values) {
	if (isInitialized()) {
		if (values.size() > 0) {
			SQLiteDatabase db = mSqlite.getWritableDatabase();
			db.insert(table, null, values);
			db.close();
		}
	}
	// TODO save insert for later
	else {

	}
	return 0;
}

/**
 * Convenience method for replacing a row in the database.
 * @param table the table in which to replace the row
 * @param initialValues this map contains the initial column values for
 * the row.
 * @return the row ID of the newly inserted row, or -1 if an error occurred
 */
protected long replace(String table, ContentValues initialValues) {
	if (isInitialized()) {
		if (initialValues.size() > 0) {
			SQLiteDatabase db = mSqlite.getWritableDatabase();
			db.replace(table, null, initialValues);
			db.close();
		}
	}
	// TODO save replace for later
	else {

	}
	return 0;
}

/**
 * Convenience method for updating rows in the database.
 * @param table the table to update in
 * @param values a map from column names to new column values. null is a
 * valid value that will be translated to NULL.
 * @param whereClause the optional WHERE clause to apply when updating.
 * Passing null will update all rows.
 * @return the number of rows affected
 */
protected int update(String table, ContentValues values, String whereClause) {
	if (isInitialized()) {
		SQLiteDatabase db = mSqlite.getWritableDatabase();
		db.update(table, values, whereClause, null);
		db.close();
	}
	// TODO save replace for later
	else {

	}
	return 0;
}

/**
 * Execute a single SQL statement that is NOT a SELECT
 * or any other SQL statement that returns data.
 * <p>
 * It has no means to return any data (such as the number of affected rows).
 * Instead, you're encouraged to use {@link #insert(String, ContentValues)},
 * {@link #update(String, ContentValues, String)}, et al, when possible.
 * </p>
 * @param sql the SQL statement to be executed. Multiple statements separated by semicolons are
 * not supported.
 * @throws SQLException if the SQL string is invalid
 */
protected void execSQL(String sql) throws SQLException {
	if (isInitialized()) {
		SQLiteDatabase db = mSqlite.getWritableDatabase();
		db.execSQL(sql);
		db.close();
	}
	// TODO save execSql for later
	else {

	}
}

/**
 * Runs the provided SQL and returns a {@link Cursor} over the result set.
 * @param sql the SQL query. The SQL string must not be ; terminated
 * @return A {@link Cursor} object, which is positioned before the first entry. Note that
 * {@link Cursor}s are not synchronized, see the documentation for more details.
 */
protected Cursor rawQuery(String sql) {
	waitUntilInitialized();
	SQLiteDatabase db = mSqlite.getReadableDatabase();
	Cursor cursor = db.rawQuery(sql, null);
	mOpenDbs.put(cursor, db);
	return cursor;
}

/**
 * Wait for the class to be initialized
 */
private static void waitUntilInitialized() {
	while (!isInitialized()) {
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}
	}
}

/**
 * Closes the cursor and DB associated with this cursor
 * @param cursor the cursor to close
 */
protected void close(Cursor cursor) {
	cursor.close();
	SQLiteDatabase db = mOpenDbs.get(cursor);
	if (db != null) {
		db.close();
	}
}
}
