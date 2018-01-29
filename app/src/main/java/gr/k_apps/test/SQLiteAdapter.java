package gr.k_apps.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by konbak on 27/1/2018.
 */

public class SQLiteAdapter {

    public static final String KEY_NAME = "Name";
    public static final String KEY_COUNT = "Count";
    public static final String KEY_TRACKID = "TrackId";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "BasketAdapter";
    private DatabaseHelper DbHelper;
    private SQLiteDatabase Db;


    private static final String DATABASE_CREATE =
            "create table playlist (_id integer primary key autoincrement, "
                    + "Name text not null, Count text,  TrackId text);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "playlist";
    private static final int DATABASE_VERSION = 3;

    private final Context Ctx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS playlist");
            onCreate(db);
        }
    }

    public SQLiteAdapter(Context ctx){
        this.Ctx = ctx;
    }

    public SQLiteAdapter open() throws SQLException {
        DbHelper = new DatabaseHelper(Ctx);
        Db = DbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        DbHelper.close();
    }

    public void createItem(String Name, String Count, String itemId){

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, Name);
        initialValues.put(KEY_COUNT, Count);
        initialValues.put(KEY_TRACKID, itemId);

        long _id = Db.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteNote (long rowId){
        return Db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllNotes(){
        return Db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME,
                KEY_COUNT, KEY_TRACKID }, null, null, null, null, null);
    }

    public Cursor fetchNote(long rowId) throws SQLException{

        Cursor Cursor =
                Db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                                KEY_NAME, KEY_COUNT, KEY_TRACKID},
                        KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);

        if (Cursor != null){
            Cursor.moveToFirst();
        }

        return Cursor;

    }

    public void deleteTable() {
        Db = DbHelper.getWritableDatabase();;
        // Delete All Rows
        Db.delete(DATABASE_TABLE, null, null);
        Db.close();
    }

    public boolean chkDB() {
        Cursor mCursor = Db.rawQuery("SELECT * FROM " + DATABASE_TABLE, null);
        Boolean rowExists;

        if (mCursor.moveToFirst())
        {
            // DO SOMETHING WITH CURSOR
            rowExists = true;

        } else
        {
            // I AM EMPTY
            rowExists = false;
        }

        return rowExists;

    }


}
