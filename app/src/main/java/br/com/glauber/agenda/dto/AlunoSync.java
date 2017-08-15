package br.com.glauber.agenda.dto;

import java.util.List;

import br.com.glauber.agenda.modelo.Aluno;

/**
 * Created by Glauber on 14/08/2017.
 */

public class AlunoSync {

    private List<Aluno> alunos;
    private String momentoDaUltimaModificacao;

    public String getMomentoDaUltimaModificacao() {
        return momentoDaUltimaModificacao;
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }
}
