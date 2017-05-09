package br.com.luan.horaextra;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by luan on 26/09/2015.
 */
public class ContadorService extends Service implements Runnable, InterfaceContador {

    private static final int MAX = 60;

    private int segundos;
    private int minutos;
    private int horas;

    private String h = "";
    private String m = "";
    private String s = "";

    private String tempo;

    private Double salario;
    private Double adicional;
    private Integer horasMes;

    private Double valorExtra;
    private Double dinheiro;

    private Handler myHandler;
    private Handler activityHandler;
    private IBinder conexao = new ContadorServiceBinder();
    private SharedPreferences preferencias;

    public void onCreate(){
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        segundos = 0;
        minutos = 0;
        horas = 0;
        dinheiro = 0.0;

        myHandler = new Handler();
    }

    public IBinder onBind(Intent intent){
        return conexao;
    }

    public void run() {
        myHandler.postAtTime(this, SystemClock.uptimeMillis() + 1000);
        count();
    }

    public void count() {
        segundos++;

        if (segundos == 60) {
            segundos = 0;
            minutos++;
        }

        if (minutos == 60) {
            minutos = 0;
            horas++;
        }

        if (horas == 100) {
            horas = 0;
        }

        dinheiro += valorExtra;


        Bundle bundle = new Bundle();

        String d = new DecimalFormat("0.00").format(dinheiro);
        d.replace(".",",");

        bundle.putString("tempo", montarTempo());
        bundle.putString("dinheiro", "R$ " + d);

        Message msg = new Message();
        msg.setData(bundle);

        activityHandler.sendMessage(msg);

    }

    public String montarTempo(){

        if(horas < 10){
            h = "0" + horas;
        }else{
            h = "" + horas;
        }
        if(minutos < 10){
            m = "0" + minutos;
        }else{
            m = "" + minutos;
        }
        if(segundos < 10){
            s = "0" + segundos;
        }else{
            s = "" + segundos;
        }

        tempo = h + ":" + m + ":" + s;

        return tempo;
    }

    public void onDestroy() {
        pausar();
    }


    @Override
    public void iniciar(boolean ativo) {
        if(!ativo) {
            salario = Double.parseDouble(preferencias.getString("salario", "0.0"));
            horasMes = Integer.parseInt(preferencias.getString("horasMes", "0"));
            adicional = Double.parseDouble(preferencias.getString("adicional", "0.0"));

            valorExtra = salario / horasMes / 60 / 60;
            valorExtra = valorExtra + (valorExtra * adicional / 100);
        }

        myHandler.post(this);
    }



    @Override
    public void parar() {
        pausar();
        horas = 0;
        minutos = 0;
        segundos = 0;
        dinheiro = 0.0;
    }

    @Override
    public void pausar() {
        myHandler.removeCallbacks(this);
    }

    @Override
    public void setHandler(Handler handler) {
        this.activityHandler = handler;
    }

    public class ContadorServiceBinder extends Binder {
        public InterfaceContador getInterface(){
            return ContadorService.this;
        }
    }
}
