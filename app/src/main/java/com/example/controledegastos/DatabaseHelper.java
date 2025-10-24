package com.example.controledegastos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gastos.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String U_ID = "id";
    private static final String U_NAME = "name";
    private static final String U_PASS = "password";

    private static final String TABLE_GASTOS = "gastos";
    private static final String G_ID = "id";
    private static final String G_USER_ID = "user_id";
    private static final String G_NOME = "nome";
    private static final String G_VALOR = "valor";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                U_NAME + " TEXT NOT NULL UNIQUE, " +
                U_PASS + " TEXT NOT NULL" +
                ")";
        String createGastos = "CREATE TABLE " + TABLE_GASTOS + " (" +
                G_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                G_USER_ID + " INTEGER NOT NULL, " +
                G_NOME + " TEXT NOT NULL, " +
                G_VALOR + " REAL NOT NULL, " +
                "FOREIGN KEY(" + G_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + U_ID + ")" +
                ")";
        db.execSQL(createUsers);
        db.execSQL(createGastos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GASTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ================== Usuários ==================

    public long addUser(String nome, String senha) {
        if (nome == null || nome.trim().isEmpty()) return -1;
        if (senha == null || senha.trim().isEmpty()) return -1;
        if (userExists(nome)) return -1;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_NAME, nome.trim());
        cv.put(U_PASS, senha);
        long id = db.insert(TABLE_USERS, null, cv);
        db.close();
        return id;
    }

    public boolean userExists(String nome) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, new String[]{U_ID},
                U_NAME + " = ?", new String[]{nome.trim()},
                null, null, null);
        boolean exists = (c != null && c.moveToFirst());
        if (c != null) c.close();
        db.close();
        return exists;
    }

    public long checkUser(String nome, String senha) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, new String[]{U_ID},
                U_NAME + " = ? AND " + U_PASS + " = ?", new String[]{nome.trim(), senha},
                null, null, null);
        long id = -1;
        if (c != null && c.moveToFirst()) {
            id = c.getLong(c.getColumnIndexOrThrow(U_ID));
        }
        if (c != null) c.close();
        db.close();
        return id;
    }

    // ================== Gastos ==================

    public long addGasto(long userId, String nomeGasto, double valor) {
        if (nomeGasto == null || nomeGasto.trim().isEmpty()) return -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(G_USER_ID, userId);
        cv.put(G_NOME, nomeGasto.trim());
        cv.put(G_VALOR, valor);
        long id = db.insert(TABLE_GASTOS, null, cv);
        db.close();
        return id;
    }

    public List<String> getGastosByUser(long userId) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_GASTOS,
                new String[]{G_NOME, G_VALOR},
                G_USER_ID + " = ?", new String[]{String.valueOf(userId)},
                null, null, G_ID + " DESC");
        if (c != null) {
            while (c.moveToNext()) {
                String nome = c.getString(c.getColumnIndexOrThrow(G_NOME));
                double valor = c.getDouble(c.getColumnIndexOrThrow(G_VALOR));
                String item = nome + " - R$ " + String.format("%.2f", valor);
                list.add(item);
            }
            c.close();
        }
        db.close();
        return list;
    }

    public boolean deleteGasto(long userId, String nomeGasto) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_GASTOS,
                G_USER_ID + "=? AND " + G_NOME + "=?",
                new String[]{String.valueOf(userId), nomeGasto});
        db.close();
        return rows > 0;
    }

    public boolean updateGasto(long userId, String nomeAntigo, String nomeNovo, double valor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(G_NOME, nomeNovo);
        cv.put(G_VALOR, valor);
        int rows = db.update(TABLE_GASTOS,
                cv,
                G_USER_ID + "=? AND " + G_NOME + "=?",
                new String[]{String.valueOf(userId), nomeAntigo});
        db.close();
        return rows > 0;
    }
}
