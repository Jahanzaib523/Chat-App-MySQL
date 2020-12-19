package Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MessagesDatabaseHelperClass extends SQLiteOpenHelper {
    public static final String COLUMN_MESSAGES_TABLE = "MESSAGES_TABLE";
    public static final String COLUMN_MESSAGE_ID = "MESSAGE_ID";
    public static final String COLUMN_SENDER_ID = "SENDER_ID";
    public static final String COLUMN_RECEIVER_ID = "RECEIVER_ID";
    public static final String COLUMN_MESSAGE_TEXT = "MESSAGE_TEXT";
    public static final String COLUMN_TIMESTAMP = "TIMESTAMP";
    public static final String COLUMN_MESSAGE_STATUS = "MESSAGE_STATUS";

    public MessagesDatabaseHelperClass(@Nullable Context context) {
        super(context, "messages.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
          String createstatement = "CREATE TABLE " + COLUMN_MESSAGES_TABLE + " (" + COLUMN_MESSAGE_ID + " TEXT PRIMARY KEY, " + COLUMN_SENDER_ID + " TEXT, " + COLUMN_RECEIVER_ID + " TEXT," +
                  " " + COLUMN_MESSAGE_TEXT + " TEXT, " + COLUMN_TIMESTAMP + " TEXT, " + COLUMN_MESSAGE_STATUS + " TEXT)";

          db.execSQL(createstatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public  boolean AddMessage(MessageModel messageModel)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MESSAGE_ID, messageModel.getMessageId());
        cv.put(COLUMN_SENDER_ID, messageModel.getSenderId());
        cv.put(COLUMN_RECEIVER_ID, messageModel.getReceiverId());
        cv.put(COLUMN_MESSAGE_TEXT, messageModel.getText());
        cv.put(COLUMN_TIMESTAMP, messageModel.getTimestamp());
        cv.put(COLUMN_MESSAGE_STATUS, messageModel.getStatus());

        long res = db.insert(COLUMN_MESSAGES_TABLE, null, cv);

        if(res == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
