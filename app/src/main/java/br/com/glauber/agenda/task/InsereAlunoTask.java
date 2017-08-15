package br.com.glauber.agenda.task;

import android.os.AsyncTask;

import br.com.glauber.agenda.client.WebClient;
import br.com.glauber.agenda.converter.AlunoConverter;
import br.com.glauber.agenda.modelo.Aluno;

public class InsereAlunoTask extends AsyncTask<Void, Void, Void> {

    private final Aluno aluno;

    public InsereAlunoTask(Aluno aluno) {
        this.aluno = aluno;
    }

    @Override
    protected Void doInBackground(Void... params) {
        AlunoConverter converter = new AlunoConverter();
        String alunoJSON = converter.converteParaJSONCompleto(aluno);

        WebClient client = new WebClient();
        client.enviar(alunoJSON);

        return null;
    }
}
