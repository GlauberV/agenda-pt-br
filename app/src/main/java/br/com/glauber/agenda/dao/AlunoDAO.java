package br.com.glauber.agenda.dao;
//DAO = Data Access Object

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.glauber.agenda.modelo.Aluno;

public class AlunoDAO extends SQLiteOpenHelper {

    private final String TABLE_NAME = "Alunos";
    private final String ALUNO_ID = "id";
    private final String ALUNO_NOME = "nome";
    private final String ALUNO_ENDERECO = "endereco";
    private final String ALUNO_TELEFONE = "telefone";
    private final String ALUNO_SITE = "site";
    private final String ALUNO_NOTA = "nota";
    private final String ALUNO_CAMINHO_FOTO = "caminhofoto";

    private final String ALUNOS_NOVOS = "Alunos_novos";

    public AlunoDAO(Context context) {
        super(context, "Agenda", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " ("
                + ALUNO_ID + " INTEGER PRIMARY KEY, "
                + ALUNO_NOME + " TEXT NOT NULL, "
                + ALUNO_ENDERECO + " TEXT, "
                + ALUNO_TELEFONE + " TEXT, "
                + ALUNO_SITE + " TEXT, "
                + ALUNO_CAMINHO_FOTO + " TEXT, "
                + ALUNO_NOTA + " REAL);";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Assim atualizamos a partir da versão existe no celular até a mais recente, por falta do "break;".
        String sql = "";
        switch (oldVersion) {
            case 1:
                sql = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + ALUNO_CAMINHO_FOTO + " TEXT;";
                db.execSQL(sql);

            case 2:
                String criandoTabelaNova = "CREATE TABLE " + ALUNOS_NOVOS + " ("
                        + ALUNO_ID + " CHAR(36) PRIMARY KEY, "
                        + ALUNO_NOME + " TEXT NOT NULL, "
                        + ALUNO_ENDERECO + " TEXT, "
                        + ALUNO_TELEFONE + " TEXT, "
                        + ALUNO_SITE + " TEXT, "
                        + ALUNO_CAMINHO_FOTO + " TEXT, "
                        + ALUNO_NOTA + " REAL);";
                db.execSQL(criandoTabelaNova);

                //nome, endereco, telefone, site, nota, caminhofoto
                String transferidoAlunosParaTabelaNova = "INSERT INTO " + ALUNOS_NOVOS + " ("
                        + ALUNO_ID + ", "
                        + ALUNO_NOME + ", "
                        + ALUNO_ENDERECO + ", "
                        + ALUNO_TELEFONE + ", "
                        + ALUNO_SITE + ", "
                        + ALUNO_NOTA + ", "
                        + ALUNO_CAMINHO_FOTO + ") SELECT "
                        + ALUNO_ID + ", "
                        + ALUNO_NOME + ", "
                        + ALUNO_ENDERECO + ", "
                        + ALUNO_TELEFONE + ", "
                        + ALUNO_SITE + ", "
                        + ALUNO_NOTA + ", "
                        + ALUNO_CAMINHO_FOTO + " FROM " + TABLE_NAME + ";";
                db.execSQL(transferidoAlunosParaTabelaNova);

                String deletarTableAntiga = "DROP TABLE " + TABLE_NAME + ";";
                db.execSQL(deletarTableAntiga);

                String renomearTableNova = "ALTER TABLE " + ALUNOS_NOVOS + " RENAME TO " + TABLE_NAME + ";";
                db.execSQL(renomearTableNova);

            case 3:
                String buscaAlunos = "SELECT * FROM " + TABLE_NAME + ";";
                Cursor cursor = db.rawQuery(buscaAlunos, null);
                List<Aluno> alunos = populaAlunos(cursor);
                cursor.close();

                // Aqui vamo atualizar o id de cada aluno para um UUID:
                String atualizaIdDosAlunos = "UPDATE " + TABLE_NAME + " SET " + ALUNO_ID + " = ? WHERE " + ALUNO_ID + " = ?;";
                for (Aluno aluno : alunos) {
                    db.execSQL(atualizaIdDosAlunos, new String[]{geradorDeUUID(), aluno.getId()});
                }
                // Tudo que fizemos até o momento foi atualizar os id's antigos para um UUID e
                // preparar os próximos para receber o mesmo upgrade, por isso os alunos já existentes
                // possuem um UUID pronto, porém, os novos alunos recebem id=null, pois em nenhum
                // momento criamos o UUID para eles. Contudo o servidor está preparado para criar
                // um UUID se preciso e por isso no servidor eles teram um UUID.
        }
    }

    private String geradorDeUUID() {
        return UUID.randomUUID().toString();
    }

    public void insere(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();
        insereIdSeNecessario(aluno);
        ContentValues dados = pegaDadosDoAluno(aluno);

        db.insert(TABLE_NAME, null, dados);//a String nullColumnHack serve para pular linhas e deixa-las em branco.
        // O "insert" retorna um long que não é usado pelo método "postAluno" já que ele é void, porém
        // ele setando os dados do aluno, por isso ele tem id, mas não há tempo suficiente de enviar
        // para a aplicação web, porque o id só é atribuído depois de salvo no banco de dados.
        // Com isso, vamos setar agora antes de prosseguir.
        // Documentação disponivel com ctrl+espaço.
        //aluno.setId(id);
        //Porém agora não precisaremos fazer isso, já que não trabalharemos com un id(long) e sim UUID.
    }

    private void insereIdSeNecessario(Aluno aluno) {
        // Como não queremos sobrescrever UUID's existentes, vamos precisar fazer uma verificação.
        if (aluno.getId() == null) {
            aluno.setId(geradorDeUUID()); //Estamos começando a tirar a responsabilidade do SQLite de
            // atribuir um id ao aluno.
        }
    }

    public void sincroniza(List<Aluno> alunos) {
        Log.i("Aluno: ", String.valueOf(alunos));
        for (Aluno aluno : alunos) {
            if (existe(aluno)) {
                altera(aluno);
            } else {
                insere(aluno);
            }
        }
    }

    private boolean existe(Aluno aluno) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT " + ALUNO_ID + " FROM " + TABLE_NAME + " WHERE " + ALUNO_ID + " = ? LIMIT 1;";
        String[] params = {aluno.getId()};

        Cursor cursor = db.rawQuery(sql, params);
        int quantidade = cursor.getCount();
        cursor.close();

        return quantidade > 0;
    }

    @NonNull
    private ContentValues pegaDadosDoAluno(Aluno aluno) {
        ContentValues dados = new ContentValues();
        dados.put(ALUNO_ID, aluno.getId()); // Precisamos empacotar o id do aluno também.
        dados.put(ALUNO_NOME, aluno.getNome());
        dados.put(ALUNO_ENDERECO, aluno.getEndereco());
        dados.put(ALUNO_TELEFONE, aluno.getTelefone());
        dados.put(ALUNO_SITE, aluno.getSite());
        dados.put(ALUNO_NOTA, aluno.getNota());
        dados.put(ALUNO_CAMINHO_FOTO, aluno.getCaminhoFoto());
        return dados;
    }

    public List<Aluno> buscarAlunos() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + ";";

        //O execSQL(); nao devolve nada, pois é um método void.
        Cursor c = db.rawQuery(sql, null);//Um cursor começa em uma linha em branco em cima dos resultados.
        List<Aluno> alunos = populaAlunos(c); //Estamos reaproveitando o código novamente.
        //Sempre feche o cursor para que o android libere a memoria usada nessa query(pergunta).
        c.close();
        return alunos;
    }

    @NonNull
    private List<Aluno> populaAlunos(Cursor c) {
        List<Aluno> alunos = new ArrayList<>();
        // cursor.moveToNext() devolve duas coisas: primeiro sai dessa linha em branco indo para a
        // proxima, consequentemente devolve um boolean dizendo se é possivél ou não avançar.
        while (c.moveToNext()) {
            Aluno aluno = new Aluno();
            aluno.setId(c.getString(c.getColumnIndex(ALUNO_ID)));
            aluno.setNome(c.getString(c.getColumnIndex(ALUNO_NOME)));
            aluno.setEndereco(c.getString(c.getColumnIndex(ALUNO_ENDERECO)));
            aluno.setTelefone(c.getString(c.getColumnIndex(ALUNO_TELEFONE)));
            aluno.setSite(c.getString(c.getColumnIndex(ALUNO_SITE)));
            aluno.setNota(c.getDouble(c.getColumnIndex(ALUNO_NOTA)));
            aluno.setCaminhoFoto(c.getString(c.getColumnIndex(ALUNO_CAMINHO_FOTO)));

            alunos.add(aluno);
        }
        return alunos;
    }

    public void deleta(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {aluno.getId().toString()};
        db.delete(TABLE_NAME, "id = ?", params);

        //Dessa forma é mais simples. Muito mais simples.
    }

    public void altera(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues dados = pegaDadosDoAluno(aluno);
        String[] params = {aluno.getId().toString()};
        db.update(TABLE_NAME, dados, ALUNO_ID + " = ?", params);
    }

    public boolean isTelefoneDoAluno(String telefone) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + ALUNO_TELEFONE + " = ?;";
        String[] params = {telefone};
        Cursor c = db.rawQuery(sql, params);
        int resultados = c.getCount();
        c.close();
        return resultados > 0;
    }
}
