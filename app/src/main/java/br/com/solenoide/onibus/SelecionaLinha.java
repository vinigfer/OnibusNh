package br.com.solenoide.onibus;

import br.com.solenoide.empresas.Empresa;
import br.com.solenoide.empresas.Futura;
import br.com.solenoide.empresas.Hamburguesa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SelecionaLinha extends ActionBarActivity implements OnClickListener {

	String companhia;
	Button mostrar;
	Spinner selecionaLinha, selecionaDia;
	Empresa empresa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_linha);
		
		mostrar = (Button)findViewById(R.id.Mostrar);
		mostrar.setOnClickListener(this);
		selecionaLinha = (Spinner)findViewById(R.id.Linha);
		selecionaDia = (Spinner)findViewById(R.id.Dias);
		
		Bundle pacote = getIntent().getExtras();
	    if(pacote == null)
	    	finish();
	    companhia = pacote.getString("Empresa");
		
	    final ActionBar minhaActionBar = getSupportActionBar();
	    minhaActionBar.setDisplayHomeAsUpEnabled(true);
	    minhaActionBar.setTitle("Viação " + companhia);
	    
		if (companhia.equals("Hamburguesa"))
			empresa = new Hamburguesa();
		else if (companhia.equals("Futura"))
			empresa = new Futura();
		else
			finish();
		String[] linhas = empresa.getNomeDasLinhasAsStringArray();
		ArrayAdapter<String> adapterLinhas = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, linhas);
		selecionaLinha.setAdapter(adapterLinhas);
		selecionaLinha.setSelection(0);
		
		String[] dias = empresa.getNomeDosDiasAsStringArray();
		ArrayAdapter<String> adapterDias = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, dias);
		selecionaDia.setAdapter(adapterDias);
		selecionaDia.setSelection(0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.Mostrar) {
			Intent intencao = new Intent(this, MostraHorarios.class);
			intencao.putExtra("Empresa", companhia);
			
			String linhaSelecionada = selecionaLinha.getSelectedItem().toString();
			linhaSelecionada = empresa.getIdDaLinhaUsandoNome(linhaSelecionada);
			intencao.putExtra("Linha", linhaSelecionada);
			
			String diaSelecionado = selecionaDia.getSelectedItem().toString();
			diaSelecionado = empresa.getIdDoDiaUsandoNome(diaSelecionado);
			intencao.putExtra("Dias", diaSelecionado);

			startActivity(intencao);
		}
	}
}
