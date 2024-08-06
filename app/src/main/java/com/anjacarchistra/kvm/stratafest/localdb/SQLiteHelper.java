package com.anjacarchistra.kvm.stratafest.localdb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import com.anjacarchistra.kvm.stratafest.dto.Event;
import com.anjacarchistra.kvm.stratafest.dto.Participant;
import com.anjacarchistra.kvm.stratafest.util.FoodDetails;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "events_db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_EVENTS = "events";
    private static final String TABLE_PARTICIPANTS = "participants";


    private static final String TABLE_FOOD_DETAILS = "food_details";
    private static final String KEY_LOT_ID = "lot_id";
    private static final String KEY_NAME = "name";


    private static final String KEY_ID = "id";
    private static final String KEY_EVENT_ID = "event_id";
    private static final String KEY_EVENT_NAME = "event_name";
    private static final String KEY_MAX_PARTICIPANTS = "max_participants";
    private static final String KEY_MIN_PARTICIPANTS = "min_participants";
    private static final String KEY_PARTICIPANT_NAME = "participant_name";
    private static final String KEY_PARTICIPANT_EMAIL = "participant_email";
    private  static  final  String KEY_EVENT_TIME ="event";
    private  static  final  String KEY_EVENT_VENUE="venue";

    private static SQLiteHelper instance;

    public static synchronized SQLiteHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SQLiteHelper(context.getApplicationContext());
        }
        return instance;
    }

    private SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EVENT_ID + " INTEGER,"
                + KEY_EVENT_NAME + " TEXT,"
                + KEY_MAX_PARTICIPANTS + " INTEGER,"
                + KEY_MIN_PARTICIPANTS + " INTEGER,"
                + KEY_EVENT_TIME + " TEXT,"       // Add Time column
                + KEY_EVENT_VENUE + " TEXT"       // Add Venue column
                + ")";
        db.execSQL(CREATE_EVENTS_TABLE);

        String CREATE_PARTICIPANTS_TABLE = "CREATE TABLE " + TABLE_PARTICIPANTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EVENT_ID + " INTEGER,"
                + KEY_PARTICIPANT_NAME + " TEXT,"
                + KEY_PARTICIPANT_EMAIL + " TEXT" + ")";
        db.execSQL(CREATE_PARTICIPANTS_TABLE);


        String CREATE_FOOD_DETAILS_TABLE = "CREATE TABLE " + TABLE_FOOD_DETAILS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_LOT_ID + " INTEGER,"
                + KEY_NAME + " TEXT" + ")";
        db.execSQL(CREATE_FOOD_DETAILS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANTS);
        onCreate(db);
    }

    public void addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_ID, event.getEventId());
        values.put(KEY_EVENT_NAME, event.getEventName());
        values.put(KEY_MAX_PARTICIPANTS, event.getMaxParticipant());
        values.put(KEY_MIN_PARTICIPANTS, event.getMinParticipant());

        values.put(KEY_EVENT_TIME,event.getTime());
        values.put(KEY_EVENT_VENUE,event.getVenue());
        db.insert(TABLE_EVENTS, null, values);
        db.close();
    }

    @SuppressLint("Range")
    public String getEventNameById(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String eventName = null;

        String selectQuery = "SELECT " + KEY_EVENT_NAME + " FROM " + TABLE_EVENTS + " WHERE " + KEY_EVENT_ID + "=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(eventId)});

        if (cursor.moveToFirst()) {
            eventName = cursor.getString(cursor.getColumnIndex(KEY_EVENT_NAME));
        }

        cursor.close();
        db.close();
        return eventName;
    }


    public void addParticipant(int eventId, Participant participant) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_ID, eventId);
        values.put(KEY_PARTICIPANT_NAME, participant.getName());
        db.insert(TABLE_PARTICIPANTS, null, values);

        db.close();
    }

    @SuppressLint("Range")
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setEventId(cursor.getInt(cursor.getColumnIndex(KEY_EVENT_ID)));
                event.setEventName(cursor.getString(cursor.getColumnIndex(KEY_EVENT_NAME)));
                event.setMaxParticipant(cursor.getInt(cursor.getColumnIndex(KEY_MAX_PARTICIPANTS)));
                event.setMinParticipant(cursor.getInt(cursor.getColumnIndex(KEY_MIN_PARTICIPANTS)));
                event.setTime(cursor.getString(cursor.getColumnIndex(KEY_EVENT_TIME)));
                event.setVenue(cursor.getString(cursor.getColumnIndex(KEY_EVENT_VENUE)));
                events.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return events;
    }

    @SuppressLint("Range")
    public List<Participant> getParticipantsByEventId(int eventId) {
        List<Participant> participants = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PARTICIPANTS + " WHERE " + KEY_EVENT_ID + "=" + eventId;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Participant participant = new Participant();
                participant.setName(cursor.getString(cursor.getColumnIndex(KEY_PARTICIPANT_NAME)));
                participants.add(participant);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return participants;
    }

    @SuppressLint("Range")
    public List<FoodDetails> getAllFoodDetails() {
        List<FoodDetails> foodDetailsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_FOOD_DETAILS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                int lotid = cursor.getInt(cursor.getColumnIndex(KEY_LOT_ID));
                String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                FoodDetails foodDetails = new FoodDetails(lotid, name);
                foodDetailsList.add(foodDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return foodDetailsList;
        }

    public void addFoodDetails(FoodDetails foodDetail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LOT_ID, foodDetail.getLotid());
        values.put(KEY_NAME, foodDetail.getName());
        db.insert(TABLE_FOOD_DETAILS, null, values);
        db.close();
    }
}

