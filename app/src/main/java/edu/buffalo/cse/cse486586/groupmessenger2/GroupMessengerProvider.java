package edu.buffalo.cse.cse486586.groupmessenger2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */
public class GroupMessengerProvider extends ContentProvider {
    static String P_TAG="PartiksTag";

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
         * TODO: You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         * 
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that we used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */
        //partiks code start
        //References:
        // https://stackoverflow.com/questions/10576930/trying-to-check-if-a-file-exists-in-internal-storage
        // https://stackoverflow.com/questions/3554722/how-to-delete-internal-storage-file-in-android
        String filename = values.getAsString("key");
        //Log.e(P_TAG, "GOT KEY: "+filename);
        String value = values.getAsString("value");
        //Log.e(P_TAG, "GOT VALUE: "+value);
        String path = getContext().getFilesDir().getAbsolutePath() + "/" + values.getAsString("key");
        File f = new File(path);
        //deleting if the record is already there, as mentioned in PA, not updating or storing the previous values.
        if(f.exists()){
            f.delete();
        }
        Log.e(P_TAG, "---- KEY "+ values.getAsString("key"));
        Log.e(P_TAG, "---- VALUE "+ values.getAsString("value"));

        FileOutputStream outputStream;
        try {
            outputStream = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(value.getBytes());
            Log.e(P_TAG,"FILE CREATED = "+path);
            outputStream.close();
        } catch (Exception e) {
            Log.e(P_TAG, "File write failed");
            e.printStackTrace();
        }

        //Log.e(P_TAG, values.toString());
        //partiks code end


        return uri;
    }

    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
        return false;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         *
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         *
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */
        //partiks code start
        String filename = selection;
        String path = getContext().getFilesDir().getAbsolutePath() + "/" + filename;
        //File f = new File(path);
        try {
            FileInputStream fin = new FileInputStream(path);
            String[] cols = {"key","value"};
            MatrixCursor m = new MatrixCursor(cols, 1);
            String val = "";
            int c;
            while( (c = fin.read()) != -1){
                val = val + Character.toString((char) c);
                //Log.e(P_TAG, "----READING from file"+ fin.toString() + "    " + val);
            }
            String[] value = {filename, val};
            m.addRow(value);
            fin.close();
            return m;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        //Log.e(P_TAG, "\n\n>>>>>>> SELECTION = "+selection);
        //partiks code end
        return null;
    }
}
