package br.com.glauber.agenda.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.widget.Toast;

import br.com.glauber.agenda.R;
import br.com.glauber.agenda.dao.AlunoDAO;

public class SMSReceiver extends BroadcastReceiver { //A BroadcastReceiver não é considerada uma classe de contexto.
    @Override
    public void onReceive(Context context, Intent intent) {

        Object[] pdus = (Object[]) intent.getSerializableExtra("pdus"); //Existem vários pdu's, por isso uma array de Objetos.
        byte[] pdu = (byte[]) pdus[0]; //Uma "pdu" em si é uma coleção de bytes.
        String formato = (String) intent.getSerializableExtra("format"); //O nosso conhecido Serialable.

        SmsMessage sms = null; //Precisamos de um "SmsMessage" para recuperarmos o
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            sms = SmsMessage.createFromPdu(pdu, formato);
        }

        // numero de um aluno specifico.
        // O método "createFromPdu" vai criar o que precisamos atraves de uma "bytes[] pdu" e um "format".
        String telefone = "";
        if (sms != null) {
            telefone = sms.getDisplayOriginatingAddress(); //É a mesma coisa que um "getTelephone();".
        }

        AlunoDAO dao = new AlunoDAO(context);
        if (dao.isTelefoneDoAluno(telefone)) {
            //Queremos receber esse Toast somente se o sms vier de um aluno.
            Toast.makeText(context, "Chegou um sms!", Toast.LENGTH_SHORT).show();
            MediaPlayer mp = MediaPlayer.create(context, R.raw.msg);
            mp.start();
        }
        dao.close();
    }
}
