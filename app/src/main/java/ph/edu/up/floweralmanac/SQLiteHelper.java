package ph.edu.up.floweralmanac;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ph.edu.up.floweralmanac.models.Flower;

import static ph.edu.up.floweralmanac.AddActivity.getPathThruURI;

/**
 * Created by fulltime on 07/06/2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper {
    //For migration to Firebase

    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase database;
    private Context context;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String DATABASE_NAME = "DBFlowersFinal.db";
    public static final String TABLE_NAME = "FLOWERS";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_REV = "REV";
    public static final String COLUMN_EASE = "EASE";
    public static final String COLUMN_INSTRUCTIONS= "INSTRUCTIONS";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ( " + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, " + COLUMN_NAME + " TEXT, " + COLUMN_REV + " TEXT, " + COLUMN_EASE+ " TEXT, " + COLUMN_INSTRUCTIONS + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void clearTable() {
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                Flower flower = new Flower();
                cursor.moveToNext();
                flower.setId(cursor.getInt(0));
                flower.setName(cursor.getString(1));
                flower.setRev(cursor.getString(2));
                flower.setEase(cursor.getString(3));
                flower.setInstructions(cursor.getString(4));
                deleteRecords(flower);
            }
        }
        cursor.close();
        database.close();

    }

    public void insertItems(Flower flower) {
        database = this.getReadableDatabase();
        database.execSQL("INSERT INTO " + TABLE_NAME + " (" + COLUMN_ID + ", " + COLUMN_NAME + ", " + COLUMN_REV+ ", " + COLUMN_EASE + ", " + COLUMN_INSTRUCTIONS + ") VALUES(?,?,?,?,?)", new Object[] {flower.getId(), flower.getName(), flower.getRev(), flower.getEase(), flower.getInstructions()});
        database.close();
    }

   public void initialValues() {
        Flower flower = new Flower();
        flower.setId(0);
        flower.setName("Lily");
        flower.setRev("");
        flower.setEase("Medium");
        flower.setInstructions("Plant in autumn. Needs a long, cold, hibernation period coinciding well with winter. Blooms in late spring to early summer.");
        insertItems(flower);

        Flower flower1 = new Flower();
        flower1.setId(1);
        flower1.setName("Chocolate Cosmos");
        flower1.setRev("");
        flower1.setEase("Easy");
        flower1.setInstructions("Primarily an ornamental plant. Gives off a sweet chocolate smell. Plant in area with full exposure to sunlight.");
        insertItems(flower1);

        Flower flower3 = new Flower();
        flower3.setId(2);
        flower3.setName("Vanilla");
        flower3.setRev("");
        flower3.setEase("Difficult");
        flower3.setInstructions("Temperature and humidity must be constantly regulated. It takes anywhere between 3-5 years before a vanilla blooms. This plant requires regular care.");
        insertItems(flower3);
    }

    public void updateRecords(Flower flower) {
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, flower.getName());
        contentValues.put(COLUMN_REV, flower.getRev());
        contentValues.put(COLUMN_EASE, flower.getEase());
        contentValues.put(COLUMN_INSTRUCTIONS, flower.getInstructions());
        database.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?", new String[]{String.valueOf(flower.getId())});
        database.close();
    }

    public void updatePhoto(String revId, int id) {
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_REV, revId);
        database.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        database.close();
    }

    public void deleteRecords(Flower flower) {
        database = this.getReadableDatabase();
        database.delete(TABLE_NAME, COLUMN_NAME + " = ?", new String[]{flower.getName()});
        database.close();
    }

    public String searchRecords(String name) {
        Flower flower = new Flower();
        String status = "";

        database = this.getReadableDatabase();

        try {

        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = ?", new String[]{name});

        if (cursor != null) {
            cursor.moveToFirst();
            flower.setId(cursor.getInt(0));
            flower.setName(cursor.getString(1));
            flower.setRev(cursor.getString(2));
            flower.setEase(cursor.getString(3));
            flower.setInstructions(cursor.getString(4));

            status = "exists";

        } } catch (CursorIndexOutOfBoundsException ce) {
            status = "none";
        }

        database.close();
        return status;
    }

    public List<Flower> getAllRecords() {
        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null,null,null,null,null,COLUMN_NAME);
        ArrayList<Flower> flowerArrayList = new ArrayList<>();

        while( cursor.moveToNext()){
            Flower flower = new Flower();
            flower.setId(cursor.getInt(0));
            flower.setName(cursor.getString(1));
            flower.setRev(cursor.getString(2));
            flower.setEase(cursor.getString(3));
            flower.setInstructions(cursor.getString(4));
            flowerArrayList.add(flower);

        }
        database.close();
        return flowerArrayList;
    }

    public String writeDB(Context context) throws IOException {
        List<Flower> flowerArrayList = getAllRecords();
        File file = new File(Environment.getExternalStorageDirectory(), "database.txt");

        Uri filePath = Uri.fromFile(file);
        String textFilePath = getPathThruURI(context, filePath);
        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = new FileOutputStream(file);
            file.createNewFile();
            OutputStreamWriter outWriter = new OutputStreamWriter(fileOutputStream);

            for (Flower flower: flowerArrayList) {
                outWriter.append(flower.getName()+ "_" + String.valueOf(flower.getId()) +"\n");
                outWriter.append(flower.getEase()+"\n");

                if (flower.getInstructions().equals("")) {
                    flower.setInstructions("None");
                }

                outWriter.append(flower.getInstructions()+"\n");
            }

            outWriter.close();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        }
        return textFilePath;
    }
}
