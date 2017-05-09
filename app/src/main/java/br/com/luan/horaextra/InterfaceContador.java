package br.com.luan.horaextra;

import android.os.Handler;

/**
 * Created by luan on 06/10/2015.
 */
public interface InterfaceContador {

    public void iniciar(boolean ativo);
    public void parar();
    public void pausar();
    public void setHandler(Handler handler);

}
