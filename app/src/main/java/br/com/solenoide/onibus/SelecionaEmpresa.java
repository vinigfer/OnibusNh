package br.com.solenoide.onibus;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import br.com.solenoide.empresas.Futura;
import br.com.solenoide.empresas.Hamburguesa;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SelecionaEmpresa extends ActionBarActivity implements OnClickListener {
	
	private Button botaoHamburguesa, botaoFutura;
	private TextView errorMessage;
	DatabaseHelper dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_empresa);

		botaoHamburguesa = (Button)findViewById(R.id.Hamburguesa);
		botaoHamburguesa.setOnClickListener(this);
		errorMessage = (TextView)findViewById(R.id.Erro);
		botaoFutura = (Button)findViewById(R.id.Futura);
		botaoFutura.setOnClickListener(this);
		dbHelper = new DatabaseHelper(getBaseContext());
	}

	@Override
	protected void onResume() {
		super.onResume();
		mostraMensagemDeErro();
	}

	private void mostraMensagemDeErro() {
		int quantidade = dbHelper.quantidadeDeRequestsPendentes();
		if (quantidade > 0)
			errorMessage.setVisibility(View.VISIBLE);
		else
			errorMessage.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.layout_empresa_actions, menu);   
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_atualizar_dados:
	        	if(isConnectedToInternet(SelecionaEmpresa.this) == false) {
					pedeParaAtivarWifi(SelecionaEmpresa.this);
					return true;
	        	}
	        	
				buscaLinhas buscador = new buscaLinhas();
				buscador.execute(new Object());
	            return true;
	        case R.id.action_compartilhar:
	        	Intent intencao = new Intent(Intent.ACTION_SEND);  
	        	intencao.setType("text/plain");
	        	intencao.putExtra(Intent.EXTRA_SUBJECT, "Horários de ônibus offline em NH");
				String strAux = "Experimente esse App\n\n";
				strAux += "https://play.google.com/store/apps/details?id=br.com.solenoide.onibus \n\n";
				intencao.putExtra(Intent.EXTRA_TEXT, strAux);
				startActivity(Intent.createChooser(intencao, "Escolha como compartilhar"));
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}	
	
	private class buscaLinhas extends AsyncTask<Object, String, String> {
		private ProgressDialog loading;
		
		@Override
		protected void onPreExecute() {
			loading = new ProgressDialog(SelecionaEmpresa.this);
			loading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			loading.setCancelable(false);
			loading.setCanceledOnTouchOutside(false);
			loading.setTitle("Carregando ...");
			loading.show();
		}

		@Override
		protected String doInBackground(Object... params) {
			int quantidade = dbHelper.quantidadeDeRequestsPendentes();
			if (quantidade == 0) {
				dbHelper.limpaBanco();
				carregaRequestsTemporarios();
				quantidade = dbHelper.quantidadeDeRequestsPendentes();
			}

			try {
				Connection conn;
				Futura futura = new Futura();
				loading.setProgress(0);
				loading.setMax(quantidade);
				publishProgress("Carregando Linhas ...");
				for (int k = 0; k < quantidade; k++) {
					Map<String, String> linhaAtual = dbHelper.getRequestPendente();
					conn = Jsoup.connect(linhaAtual.get("Url"));
					conn.data("linha", 	 linhaAtual.get("Linha"));
					conn.data("dia", 	 linhaAtual.get("Dia"));
					conn.data("sentido", linhaAtual.get("Sentido"));
					conn.data("acao", 	 linhaAtual.get("Acao"));

					Document doc = conn.timeout(0).post();
					loading.incrementProgressBy(1);

					if (linhaAtual.get("Companhia").equals("Futura")) {
						Elements dados = doc.select(".horarios");
						for (int i = 1; i < dados.size(); i=i+2) {
							String horario = dados.get(i).text();
							if (horario.indexOf("Itinerário") >= 0)
								continue;
							String sentido = futura.getValueDoSentidoUsandoNome(linhaAtual.get("Sentido"));
							String observacoes = "";
							dbHelper.gravaLinha("Futura", linhaAtual.get("Linha"), linhaAtual.get("Dia"),
												horario, sentido, observacoes);
						}
					}
					else {
						Elements dados = doc.select(".style3");
						for (int i = 3; i < dados.size(); i=i+2) {
							String horario = dados.get(i).text();
							if (horario.equals("") || horario.indexOf("ITINER") >= 0)
								continue;
							String sentido = dados.get(i + 1).text();
							String observacoes = "";
							dbHelper.gravaLinha("Hamburguesa", linhaAtual.get("Linha"), linhaAtual.get("Dia"),
												horario, sentido, observacoes);
						}
					}
					dbHelper.deletaRequest(linhaAtual.get("rowId"));
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("EXCEÇÃO!!! " + e.toString());
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			loading.setTitle(values[0]);
		}		
		
		@Override
		protected void onPostExecute(String result) {
			if(loading.isShowing())
				loading.dismiss();

			mostraMensagemDeErro();
		}

		private void carregaRequestsTemporarios() {
			Set<String> idsDasLinhas;
			Set<String> diasUteisSabadosEDomingos;
			Set<String> sentidoDaLinha;

			dbHelper.iniciaTransacao();
			//-----------------------------
			Futura futura = new Futura();
			idsDasLinhas = futura.getIdsDasLinhas();
			diasUteisSabadosEDomingos = futura.getIdsDosDias();
			sentidoDaLinha = futura.getIdsDosSentidos();
			for (String sentidoAtual : sentidoDaLinha) {
				for (String diaAtual : diasUteisSabadosEDomingos) {
					for (String idLinhaAtual : idsDasLinhas)
						dbHelper.gravaRequestsPendentes("Futura", "https://www.viacaofutura.com.br/processa_linha.php",
														idLinhaAtual, diaAtual, sentidoAtual, "");
				}
			}
			//-----------------------------
			Hamburguesa hamburguesa = new Hamburguesa();
			idsDasLinhas = hamburguesa.getIdsDasLinhas();
			diasUteisSabadosEDomingos = hamburguesa.getIdsDosDias();
			for (String diaAtual : diasUteisSabadosEDomingos) {
				for (String idLinhaAtual : idsDasLinhas)
					dbHelper.gravaRequestsPendentes("Hamburguesa", "http://www.hamburguesa.com.br/site/horarios/horarios.php",
													idLinhaAtual, diaAtual, "", "busca");
			}
			//-----------------------------
			dbHelper.commitTransacao();
			dbHelper.encerraTransacao();
		}
	}

	@Override
	public void onClick(View v) {
		Intent intencao = new Intent(this, SelecionaLinha.class);
		switch (v.getId()) {
			case R.id.Hamburguesa:
				intencao.putExtra("Empresa", "Hamburguesa");
				break;
			case R.id.Futura:
				intencao.putExtra("Empresa", "Futura");
				break;
			default:
				break;
		}
		startActivity(intencao);
	}
	
	public boolean isConnectedToInternet(Context myContext) {
		ConnectivityManager connManager = (ConnectivityManager)myContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		final NetworkInfo mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (!wifi.isConnected() && (mobile == null || !mobile.isConnected()))
			return false;
		else
			return true;
	}
	
	public void pedeParaAtivarWifi(Context myContext) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
	    builder.setTitle("Erro!");
	    builder.setMessage("Wifi e 3G desconectados. Deseja conectar agora?");
	    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent telaWifi = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
				startActivity(telaWifi);
			}
		});
	    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	    builder.show();
	}
}
