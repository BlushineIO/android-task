package io.blushine.android.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Base class for all SQLite gateways
 */
public abstract class SqliteGateway {
private static final String TAG = SqliteGateway.class.getSimpleName();
private static SQLiteOpenHelper mSqlite = null;
private static BlockingQueue<SqlExecute> mExecuteLater = new ArrayBlockingQueue<>(10);

/**
 * Initialize SQLite
 * @param sqliteOpenHelper object which we want to open sqlite within
 */
public static synchronized void setSqlite(SQLiteOpenHelper sqliteOpenHelper) {
	mSqlite = sqliteOpenHelper;
	
	while (!mExecuteLater.isEmpty()) {
		SqlExecute sqlExecute = mExecuteLater.remove();
		sqlExecute.execute();
	}
}

/**
 * Convenience method for deleting rows in the database.
 * @param table the table to delete from
 * @param whereClause the optional WHERE clause to apply when deleting. Passing null will delete all
 * rows.
 * @return the number of rows affected if a whereClause is passed in, 0 otherwise. To remove all
 * rows and get a count pass "1" as the whereClause.
 */
protected int delete(String table, String whereClause) {
	int rowsAffected = 0;
	if (isInitialized()) {
		SQLiteDatabase db = mSqlite.getWritableDatabase();
		rowsAffected = db.delete(table, whereClause, null);
	} else {
		mExecuteLater.add(new SqlExecute(ExecuteTypes.DELETE, table, null, whereClause, null));
	}
	return rowsAffected;
}

/**
 * @return true if initialized
 */
public static synchronized boolean isInitialized() {
	return mSqlite != null;
}

/**
 * Convenience method for inserting a row into the database.
 * @param table the table to insert the row into
 * @param values this map contains the initial column values for the row. The keys should be the
 * column names and the values the column values
 * @return the row ID of the newly inserted row, or -1 if an error occurred
 */
protected long insert(String table, ContentValues values) {
	long rowId = -1;
	if (isInitialized()) {
		if (values.size() > 0) {
			SQLiteDatabase db = mSqlite.getWritableDatabase();
			rowId = db.insert(table, null, values);
		}
	} else {
		mExecuteLater.add(new SqlExecute(ExecuteTypes.INSERT, table, values, null, null));
	}
	return rowId;
}

/**
 * Convenience method for replacing a row in the database.
 * @param table the table in which to replace the row
 * @param initialValues this map contains the initial column values for the row.
 * @return the row ID of the newly inserted row, or -1 if an error occurred
 */
protected long replace(String table, ContentValues initialValues) {
	long rowId = -1;
	if (isInitialized()) {
		if (initialValues.size() > 0) {
			SQLiteDatabase db = mSqlite.getWritableDatabase();
			rowId = db.replace(table, null, initialValues);
		}
	} else {
		mExecuteLater.add(new SqlExecute(ExecuteTypes.REPLACE, table, initialValues, null, null));
	}
	return rowId;
}

/**
 * Convenience method for updating rows in the database.
 * @param table the table to update in
 * @param values a map from column names to new column values. null is a valid value that will be
 * translated to NULL.
 * @param whereClause the optional WHERE clause to apply when updating. Passing null will update all
 * rows.
 * @return the number of rows affected
 */
protected int update(String table, ContentValues values, String whereClause) {
	int rowsAffected = 0;
	if (isInitialized()) {
		SQLiteDatabase db = mSqlite.getWritableDatabase();
		rowsAffected = db.update(table, values, whereClause, null);
	} else {
		mExecuteLater.add(new SqlExecute(ExecuteTypes.UPDATE, table, values, whereClause, null));
	}
	return rowsAffected;
}

/**
 * Execute a single SQL statement that is NOT a SELECT or any other SQL statement that returns data.
 * <p> It has no means to return any data (such as the number of affected rows). Instead, you're
 * encouraged to use {@link #insert(String, ContentValues)}, {@link #update(String, ContentValues,
 * String)}, et al, when possible. </p>
 * @param sql the SQL statement to be executed. Multiple statements separated by semicolons are not
 * supported.
 * @throws SQLException if the SQL string is invalid
 */
protected void execSQL(String sql) throws SQLException {
	if (isInitialized()) {
		SQLiteDatabase db = mSqlite.getWritableDatabase();
		db.execSQL(sql);
	} else {
		mExecuteLater.add(new SqlExecute(ExecuteTypes.EXEC, null, null, null, sql));
	}
}

/**
 * Runs the provided SQL and returns a {@link Cursor} over the result set.
 * @param sql the SQL query. The SQL string must not be ; terminated
 * @return A {@link Cursor} object, which is positioned before the first entry. Note that {@link
 * Cursor}s are not synchronized, see the documentation for more details.
 */
protected Cursor rawQuery(String sql) {
	waitUntilInitialized();
	SQLiteDatabase db = mSqlite.getWritableDatabase();
	return db.rawQuery(sql, null);
}


/**
 * Wait for the class to be initialized
 */
protected static void waitUntilInitialized() {
	while (!isInitialized()) {
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			// Does nothing
		}
	}
}

/**
 * Closes the cursor and DB associated with this cursor
 * @param cursor the cursor to close
 */
protected void close(Cursor cursor) {
	if (cursor != null) {
		cursor.close();
	}
}

private enum ExecuteTypes {
	REPLACE,
	UPDATE,
	DELETE,
	INSERT,
	EXEC,
}

private static class SqlExecute extends SqliteGateway {
	private String mTable = null;
	private ContentValues mContentValues = null;
	private String mSql = null;
	private String mWhereClause = null;
	private ExecuteTypes mExecuteType = null;
	
	SqlExecute(ExecuteTypes executeType, String table, ContentValues contentValues, String whereClause, String sql) {
		mTable = table;
		mContentValues = contentValues;
		mSql = sql;
		mWhereClause = whereClause;
		mExecuteType = executeType;
	}
	
	void execute() {
		switch (mExecuteType) {
		case REPLACE:
			replace(mTable, mContentValues);
			break;
		case UPDATE:
			update(mTable, mContentValues, mWhereClause);
			break;
		case DELETE:
			delete(mTable, mWhereClause);
			break;
		case INSERT:
			insert(mTable, mContentValues);
			break;
		case EXEC:
			try {
				execSQL(mSql);
			} catch (SQLException e) {
				Log.e(TAG, "invalid SQL statement: " + mSql, e);
			}
			break;
		}
	}
}
}
