package br.com.solenoide.onibus.test;

import com.robotium.solo.Solo;

import br.com.solenoide.onibus.SelecionaEmpresa;
import android.app.Activity;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.TouchUtils;
import android.view.View;

public class TestSelecionaEmpresa extends ActivityInstrumentationTestCase2<SelecionaEmpresa> {

	private Solo solo;
	private Activity atividade;
	
	public TestSelecionaEmpresa() {
		super(SelecionaEmpresa.class);
	}

	@Override
	protected void setUp() throws Exception {
		atividade = getActivity();
		solo = new Solo(getInstrumentation(), atividade);
	}

	@Override
	protected void tearDown() throws Exception {
		//solo.finishOpenedActivities();
	}

	public void testSomething() throws Exception {
		solo.unlockScreen();
		View menuItem = atividade.findViewById(br.com.solenoide.onibus.R.id.action_compartilhar);
		View botao1 = atividade.findViewById(br.com.solenoide.onibus.R.id.Hamburguesa);
		//solo.clickOnView(menuItem);
		
		final IntentFilter intentFilter = new IntentFilter();
	    //intentFilter.addAction(Intent.ACTION_SEND);
		// Set up an ActivityMonitor
		ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(intentFilter, null, false);

		// Validate that ReceiverActivity is started
		//TouchUtils.clickView(this, menuItem);
		solo.setWiFiData(true);
		solo.clickOnView(botao1);
		solo.assertCurrentActivity("ExpectedDifferentActivity", "NoteEditor");
		
		
		
		Activity receiverActivity = receiverActivityMonitor.waitForActivityWithTimeout(3000);
		
		//assertNotNull("ReceiverActivity is null", receiverActivity);
		assertEquals("Monitor for ReceiverActivity has not been called", 1, receiverActivityMonitor.getHits());
		assertEquals("Activity is of wrong type", "AlgumValor", receiverActivity.getClass());

		// Remove the ActivityMonitor
		getInstrumentation().removeMonitor(receiverActivityMonitor);
		
		//assertTrue("Encontrou", solo.waitForDialogToOpen(5000));
	}
	
}
