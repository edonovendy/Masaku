package twiscode.masakuuser.Database;

/**
 * Created by ModelUser on 8/3/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import twiscode.masakuuser.Model.ModelUser;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "MasakuDB";
    // ModelUser table name
    private static final String T_USER = "t_user";

    private static final String KEY_USER_ID = "id_user";
    private static final String KEY_USER_NAME = "name_user";

    private static final String KEY_USER_PHONE = "phone_user";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_user = "CREATE TABLE " + T_USER + "("
                + KEY_USER_ID + " TEXT PRIMARY KEY,"
                + KEY_USER_NAME + " TEXT,"
                + KEY_USER_PHONE + " TEXT"
                + ")";

        db.execSQL(CREATE_TABLE_user);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + T_USER);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    public void insertuser(ModelUser modeluser) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, modeluser.getId());
        values.put(KEY_USER_NAME, modeluser.getNama());
        values.put(KEY_USER_PHONE, modeluser.getPonsel());

        // Inserting Row
        db.insert(T_USER, null, values);
        db.close(); // Closing database connection
    }

    public int getuserCount() {
        int count = 0;
        String countQuery = "SELECT  * FROM " + T_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if(cursor != null && !cursor.isClosed()){
            count = cursor.getCount();
            cursor.close();
        }

        // return count
        return count;
    }

    public ModelUser getuser() {
        String allData = "SELECT  * FROM " + T_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(allData, null);
        cursor.close();

        ModelUser modeluser = new ModelUser(cursor.getString(0),
                cursor.getString(1), cursor.getString(2)
        );
        return modeluser;
    }


    public void logout() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ T_USER);

    }


}
