package br.com.luan.horaextra;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ServiceConnection{

    private InterfaceContador contador;


    private boolean ativo = false;
    private Intent intentService;

    private Button btnIniciar;
    private Button btnPausar;
    private Button btnParar;

    final ServiceConnection conexao = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentService = new Intent(this, ContadorService.class);
        startService(intentService);

        btnIniciar = (Button) findViewById(R.id.iniciar);
        btnParar =(Button) findViewById(R.id.parar);
        btnPausar = (Button) findViewById(R.id.pausar);

        btnIniciar.setEnabled(true);
        btnParar.setEnabled(false);
        btnPausar.setEnabled(false);


        btnIniciar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnIniciar.setEnabled(false);
                btnParar.setEnabled(true);
                btnPausar.setEnabled(true);

                contador.iniciar(ativo);
                ativo = true;
            }
        });

        btnParar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnIniciar.setEnabled(true);
                btnParar.setEnabled(false);
                btnPausar.setEnabled(false);

                ativo = false;
                contador.parar();
            }
        });

        btnPausar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnIniciar.setEnabled(true);
                btnParar.setEnabled(true);
                btnPausar.setEnabled(false);

                contador.pausar();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        bindService(intentService, conexao, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("Configurações").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent irParaConfig = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(irParaConfig);
                return true;
            }
        });

        return true;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ContadorService.ContadorServiceBinder binder = (ContadorService.ContadorServiceBinder) service;
        contador = binder.getInterface();
        contador.setHandler(handler);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        contador = null;
    }

    Handler handler = new Handler(new Handler.Callback() {

        public boolean handleMessage(Message msg) {
            btnIniciar.setEnabled(false);
            btnParar.setEnabled(true);
            btnPausar.setEnabled(true);

            TextView tempo = (TextView) findViewById(R.id.tempo);
            tempo.setText(msg.getData().getString("tempo"));
            TextView dinheiro = (TextView) findViewById(R.id.dinheiro);
            dinheiro.setText(msg.getData().getString("dinheiro"));



            return false;
        }

    });

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
