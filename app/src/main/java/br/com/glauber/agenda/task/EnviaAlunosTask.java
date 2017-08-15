package br.com.glauber.agenda.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

import br.com.glauber.agenda.client.WebClient;
import br.com.glauber.agenda.converter.AlunoConverter;
import br.com.glauber.agenda.dao.AlunoDAO;
import br.com.glauber.agenda.modelo.Aluno;

public class EnviaAlunosTask extends AsyncTask<Void, Void, String> {
//1. O tipo de dado da entrada.
//2. Se formos fazer alguma atualização no meio do processo do doInBackground.
//3. O tipo de saído que teremos.

    private Context context;
    private ProgressDialog dialog;

    public EnviaAlunosTask(Context context) {
        this.context = context;
    }

    //Esse método é executado antes da tarefa começar a ser processada.
    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(context, "Aguarde", "Enviando alunos...", true, true);
        //O primeiro "true" nos diz se o loading é indeterminado, e dessa formas ficará com aparência de um circulo sem fim.
        //O segundo "true" nos diz se o usuário poderá cancelar esse carregamento.
    }

    //Aqui realizaremos nossa tarefas em segundo plano
    @Override
    protected String doInBackground(Void... params) {
        AlunoDAO dao = new AlunoDAO(context);
        List<Aluno> alunos = dao.buscarAlunos();
        dao.close();

        AlunoConverter conversor = new AlunoConverter();
        String json = conversor.converteParaJSON(alunos);

        WebClient client = new WebClient();
        String resposta = client.post(json);
        //Toast.makeText(context, resposta, Toast.LENGTH_LONG).show();
        //O Toast não pode aparecer na Thread secundária.
        return resposta;
    }

    //Esse é o método que levará o retorno do doInBackground por parametro: (Object o).
    //O onPostExecute é executado na Thread Principal, por isso nele podemos executar o nosso Toast.
    @Override
    protected void onPostExecute(String resposta) {
        dialog.dismiss();
        Toast.makeText(context, resposta, Toast.LENGTH_LONG).show();
    }
}
