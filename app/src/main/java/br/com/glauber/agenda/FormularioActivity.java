package br.com.glauber.agenda;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import br.com.glauber.agenda.dao.AlunoDAO;
import br.com.glauber.agenda.modelo.Aluno;
import br.com.glauber.agenda.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormularioActivity extends AppCompatActivity {
    //Colocando uma variavel dessa forma, queremos dizer que ela pertence a classe.
    public static final int CODIGO_CAMERA = 500;
    private FormularioHelper helper;
    private String caminhoFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        helper = new FormularioHelper(this);

        //Dessa forma recuperamos os dados que estão pendurados nessa activity.
        Intent veioDaListaDeAlunos = getIntent();
        //Assim caso houvesse vários dados nessa activity, poderiamos escolher qual deles usar.
        Aluno aluno = (Aluno) veioDaListaDeAlunos.getSerializableExtra("aluno");
        //Agora temos dois caminhos para o formulário.
        //Casso não estejamos criando um aluno do zero(então "aluno = null"), estaremos modificando ele("aluno != null").
        if (aluno != null) {
            helper.preencherFormulario(aluno);
        }

        Button botaofoto = (Button) findViewById(R.id.formulario_botao_foto);
        botaofoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                caminhoFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
                File arquivoFoto = new File(caminhoFoto);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(arquivoFoto));
                startActivityForResult(intentCamera, CODIGO_CAMERA);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODIGO_CAMERA && resultCode == Activity.RESULT_OK) {
            helper.carregaFoto(caminhoFoto);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_formulario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_formulario_ok:
                Aluno aluno = helper.pegaAluno();
                AlunoDAO dao = new AlunoDAO(this);

                if (aluno.getId() != null) {
                    dao.altera(aluno);
                    Toast.makeText(FormularioActivity.this, "Aluno(a) "
                            + aluno.getNome() + " alterado com sucesso!", Toast.LENGTH_SHORT).show();

                } else {
                    dao.insere(aluno); //Você veio da classe "Aluno"? Blz, olha o métoddo "postAluno".
                    Toast.makeText(FormularioActivity.this, "Aluno(a) "
                            + aluno.getNome() + " salvo com sucesso!", Toast.LENGTH_SHORT).show();

                }
                dao.close();

                //(Java com Web Service I) - Nada de novo por aqui...
                //new InsereAlunoTask(aluno).execute(); -> Não precisaremos mais dela, agora que temos o Retrofit.

                Call call = new RetrofitInicializador().getAlunoService().postAluno(aluno);
                // Agora para executar essa ação podemos usar dois métodos:
                // execute() - Realiza requisição síncrona, ou seja, prende a thread principal.
                // enqueue() - Realiza requisição assíincrona, ou seja, cria uma thread separada da
                // thread principal e executa em segundo plano.
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        //Esse método será chamado se não houver erros e requisição for completada com sucesso.
                        Log.i("onResponse", "requisicao com sucesso");
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        //Esse método será chamado se a requisição não for completada.
                        Log.e("onFailure", "requisicao falhou");
                    }
                });

                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
