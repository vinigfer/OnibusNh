package br.com.solenoide.onibus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "database.db";
	private static final int SCHEMA = 2;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE LINHAS (" +
				"Companhia TEXT," +
				"Id_Linha TEXT, " +
				"Dias TEXT, " +
				"Sentido TEXT, " +
				"Horario TEXT, " +
				"Observacoes TEXT " +
				");");
		db.execSQL("CREATE TABLE REQUESTS (" +
				"Companhia TEXT," +
				"Url TEXT," +
				"Acao TEXT," +
				"Linha TEXT, " +
				"Sentido TEXT, " +
				"Dia TEXT " +
				");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion <= 2) {
			db.execSQL("CREATE TABLE REQUESTS (" +
						"Companhia TEXT," +
						"Url TEXT," +
						"Acao TEXT," +
						"Linha TEXT, " +
						"Sentido TEXT, " +
						"Dia TEXT " +
						");");
		}
	}

	public void iniciaTransacao() {
		SQLiteDatabase db = getReadableDatabase();
		db.beginTransaction();
	}

	public void commitTransacao() {
		SQLiteDatabase db = getReadableDatabase();
		db.setTransactionSuccessful();
	}

	public void encerraTransacao() {
		SQLiteDatabase db = getReadableDatabase();
		db.endTransaction();
	}

	public void limpaBanco() {
		SQLiteDatabase db = getReadableDatabase();
		if (db != null)
			db.delete("LINHAS",null, null);
	}

	public Map<String, String> getRequestPendente() {
		Map<String, String> row = new HashMap<String, String>();
		SQLiteDatabase db = getReadableDatabase();
		if (db != null) {
			String query = "Select rowid, * from REQUESTS limit 1;";
			Cursor result = db.rawQuery(query, null);

			int indexRowId = result.getColumnIndex("rowid");
			int indexCompanhia = result.getColumnIndex("Companhia");
			int indexUrl = result.getColumnIndex("Url");
			int indexLinha = result.getColumnIndex("Linha");
			int indexDias = result.getColumnIndex("Dia");
			int indexSentido = result.getColumnIndex("Sentido");
			int indexAcao = result.getColumnIndex("Acao");
			if (result.moveToNext()) {
				row.put("rowId", result.getString(indexRowId));
				row.put("Companhia", result.getString(indexCompanhia));
				row.put("Url", result.getString(indexUrl));
				row.put("Linha", result.getString(indexLinha));
				row.put("Dia", result.getString(indexDias));
				row.put("Sentido", result.getString(indexSentido));
				row.put("Acao", result.getString(indexAcao));
			}
			result.close();
		}
		return row;
	}

	public void deletaRequest(String rowId) {
		SQLiteDatabase db = getReadableDatabase();
		if (db != null)
			db.delete("REQUESTS","rowId = " + rowId, null);
	}

	public int quantidadeDeRequestsPendentes() {
		SQLiteDatabase db = getReadableDatabase();
		int quantidade = 0;
		if (db != null) {
			String query = "Select count(*) as Quantidade from REQUESTS;";
			Cursor result = db.rawQuery(query, null);

			while (result.moveToNext())
				quantidade = result.getInt(0);
			result.close();
		}
		return quantidade;
	}

	public long gravaRequestsPendentes(String companhia, String url, String idLinha, String dias, String sentido, String acao) {
		SQLiteDatabase db = getReadableDatabase();
		if (db != null) {
			ContentValues cv = new ContentValues();
			cv.put("Companhia", companhia);
			cv.put("Url", url);
			cv.put("Linha", idLinha);
			cv.put("Dia", dias);
			cv.put("Sentido", sentido);
			cv.put("Acao", acao);
			return db.insert("REQUESTS", null, cv);
		}
		return -1;
	}

	public long gravaLinha(String companhia, String idLinha, String dias, String horario, String sentido, String obs) {
		SQLiteDatabase db = getReadableDatabase();
		if (db != null) {
			ContentValues cv = new ContentValues();
			cv.put("Companhia", companhia);
			cv.put("Id_Linha", idLinha);
			cv.put("Dias", dias);
			cv.put("Sentido", sentido);
			cv.put("Horario", horario);
			cv.put("Observacoes", obs);
			return db.insert("LINHAS", null, cv);
		}
		return -1;
	}
	
	public List<String> pesquisaPorCompanhiaLinhaEDias(String nomeCompanhia, String idLinha, String dias) {
		List<String> horarios = new ArrayList<String>();
		SQLiteDatabase db = getReadableDatabase();
		if (db != null) {
			String query =
					"Select * from LINHAS " +
					"Where " +
					"Companhia = '" + nomeCompanhia + "' and " +
					"Id_Linha = " + idLinha + " and " +
					"Dias = '" + dias + "';";
			Cursor result = db.rawQuery(query, null);

			int indexHorario = result.getColumnIndex("Horario");
			int indexSentido = result.getColumnIndex("Sentido");
			while (result.moveToNext()) {
				String horario = result.getString(indexHorario);
				String sentido = result.getString(indexSentido);
				horarios.add(horario + " - " + sentido);
			}
			result.close();
		}
		return horarios;
	}
}