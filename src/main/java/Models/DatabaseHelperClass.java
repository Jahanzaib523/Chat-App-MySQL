package Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelperClass extends SQLiteOpenHelper {

    public static final String CREATE_TABLE = "CREATE TABLE ";
    public static final String COLUMN_CUSTOMER_EMAIL = "CUSTOMER_EMAIL";
    public static final String COLUMN_CUSTOMER_PASSWORD = "CUSTOMER_PASSWORD";
    public static final String COLUMN_CUSTOMER_FIRSTNAME = "CUSTOMER_FIRSTNAME";
    public static final String COLUMN_CUSTOMER_L_ASTNAME = "CUSTOMER_lASTNAME";
    public static final String COLUMN_CUSTOMER_PHONE = "CUSTOMER_PHONE";
    public static final String COLUMN_CUSTOMER_DOB = "CUSTOMER_DOB";
    public static final String COLUMN_CUSTOMER_GENDER = "CUSTOMER_GENDER";
    public static final String COLUMN_CUSTOMER_BIO = "CUSTOMER_BIO";
    public static final String COLUMN_CUSTOMER_ONLINESTATUS = "CUSTOMER_ONLINESTATUS";
    public static final String COLUMN_ID = "ID";
    public static final String USER_TABLE = "USER_TABLE";

    public DatabaseHelperClass(@Nullable Context context) {
        super(context, "user.db", null, 1);
    }

    //will be accessed first time to create a db.
    //There should be a some to generate new table.
    @Override
    public void onCreate(SQLiteDatabase db)
    {
          String createTableStatement = CREATE_TABLE + " " + USER_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_CUSTOMER_EMAIL + " TEXT, " + COLUMN_CUSTOMER_PASSWORD + " TEXT, " +
                  COLUMN_CUSTOMER_FIRSTNAME + " TEXT, " + COLUMN_CUSTOMER_L_ASTNAME + " TEXT, " + COLUMN_CUSTOMER_PHONE + " TEXT, " + COLUMN_CUSTOMER_DOB + " TEXT, " + COLUMN_CUSTOMER_GENDER + " TEXT, " + COLUMN_CUSTOMER_BIO + " TEXT," +
                  " " + COLUMN_CUSTOMER_ONLINESTATUS + " TEXT)";
          db.execSQL(createTableStatement);
    }

    //This is called when DB version has been changed.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean AddUser(UserModel userModel)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put(COLUMN_ID, userModel.getUserId());
        contentValues.put(COLUMN_CUSTOMER_EMAIL, userModel.getUserEmail());
        contentValues.put(COLUMN_CUSTOMER_PASSWORD, userModel.getUserPassword());
        contentValues.put(COLUMN_CUSTOMER_FIRSTNAME, userModel.getFirstName());
        contentValues.put(COLUMN_CUSTOMER_L_ASTNAME, userModel.getLastName());
        contentValues.put(COLUMN_CUSTOMER_PHONE, userModel.getPhoneNo());
        contentValues.put(COLUMN_CUSTOMER_DOB, userModel.getDateOfBirth());
        contentValues.put(COLUMN_CUSTOMER_GENDER, userModel.getGender());
        contentValues.put(COLUMN_CUSTOMER_BIO, userModel.getBio());
        contentValues.put(COLUMN_CUSTOMER_ONLINESTATUS, userModel.getOnlineStatus());

        long result = db.insert(USER_TABLE, null, contentValues);
        if(result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public Cursor SignInUsingSQLiteDB(String email, String password) throws SQLException
    {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String query = "SELECT * FROM "+ USER_TABLE + " WHERE "
                + "email" + " = " + COLUMN_CUSTOMER_EMAIL + " OR " + password + " = " + COLUMN_CUSTOMER_PASSWORD;

        cursor = db.rawQuery(query, new String[] {email, password});

        return  cursor;
    }
}
