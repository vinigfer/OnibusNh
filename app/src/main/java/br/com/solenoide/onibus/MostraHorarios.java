package br.com.solenoide.onibus;

import java.util.List;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MostraHorarios extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_horarios);
		
		final ActionBar minhaActionBar = getSupportActionBar();
		minhaActionBar.setDisplayHomeAsUpEnabled(true);
		
		Bundle pacote = getIntent().getExtras();
	    if(pacote == null)
	    	finish();
	    String companhia = pacote.getString("Empresa");
	    String linha = pacote.getString("Linha");
	    String dias = pacote.getString("Dias");
		
		ListView listviewHorarios = (ListView)findViewById(R.id.ListaDeHorarios);
		DatabaseHelper dbHelper = new DatabaseHelper(getBaseContext());
		List<String> horarios = dbHelper.pesquisaPorCompanhiaLinhaEDias(companhia, linha, dias);
		
	    ArrayAdapter<String> adapterHorarios = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, horarios);
	    listviewHorarios.setAdapter(adapterHorarios);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}
}
