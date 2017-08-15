package br.com.glauber.agenda.converter;

import org.json.JSONException;
import org.json.JSONStringer;

import java.util.List;

import br.com.glauber.agenda.modelo.Aluno;

public class AlunoConverter {

    public String converteParaJSON(List<Aluno> alunos) {
        JSONStringer js = new JSONStringer();


        try {
            js.object().key("list").array().object().key("aluno").array();
            //Chave("Object()") -> Nome("key()") -> itens("array()").
            for (Aluno aluno : alunos) {
                js.object();
                js.key("nome").value(aluno.getNome());
                js.key("nota").value(aluno.getNota());
                js.endObject();
            }
            js.endArray().endObject().endArray().endObject();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return js.toString();
    }

    public String converteParaJSONCompleto(Aluno aluno) {
        JSONStringer js = new JSONStringer();

        try {
            js.object()
                    .key("nome").value(aluno.getNome())
                    .key("endereco").value(aluno.getEndereco())
                    .key("site").value(aluno.getSite())
                    .key("telefone").value(aluno.getTelefone())
                    .key("nota").value(aluno.getNota())
                    .endObject();
            return js.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

//Usando o JSONStringer não teremos que concatenar na mão o que diminui bastante as chances de
//digitarmos o codigo errado.