package br.com.glauber.agenda;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.com.glauber.agenda.adapter.AlunosAdapter;
import br.com.glauber.agenda.dao.AlunoDAO;
import br.com.glauber.agenda.dto.AlunoSync;
import br.com.glauber.agenda.modelo.Aluno;
import br.com.glauber.agenda.retrofit.RetrofitInicializador;
import br.com.glauber.agenda.task.EnviaAlunosTask;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaAlunosActivity extends AppCompatActivity {

    public static final int CODIGO_SMS = 321;
    public static final int CODIGO_PHONE = 123;
    private ListView listaDeAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);

        //Logo na Activity inicial pedimos ao usuário permissão para usar sms.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, CODIGO_SMS);
            }
        }

        listaDeAlunos = (ListView) findViewById(R.id.lista_de_alunos);

        Button novoAluno = (Button) findViewById(R.id.lista_de_alunos_btn_novo_aluno);
        novoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                startActivity(intent);
            }
        });

        //Precisamos dizer para o android que haverá um menu de contexto em(nesse caso) nossa lista.
        registerForContextMenu(listaDeAlunos);

        //Agora vamos dizer ao android que queremos um clique simples em um item da nossa lista..
        listaDeAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Sem instanciar a classe aluno, não poderemos usar o método getNome();
                Aluno aluno = (Aluno) listaDeAlunos.getItemAtPosition(position);
                //Vamos enviar nosso aluno com seus dados para o formulario para que esses dados possam ser alterados.
                Intent intentVaiParaOFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                //É necessário que esse aluno implemente a interface "Serializable", para ser convertido em binário e assim ser trasferido para a outra Activity.
                //Agora o aluno está pendurado na intent e pode ser identificado.
                intentVaiParaOFormulario.putExtra("aluno", aluno); //Facilita nossa vida passar uma chave em forma de String.
                startActivity(intentVaiParaOFormulario);

                //Toast.makeText(ListaAlunosActivity.this, "Aluno selecionado: " + aluno.getNome(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarLista() {
        //        Recursos que preencheram a nossa lista.
//        String[] alunos = {"Daniel", "Rodolfo", "Marley", "Pop-eye", "Dino do Sauro",
//                "Daniel", "Rodolfo", "Marley", "Pop-eye", "Dino do Sauro",
//                "Daniel", "Rodolfo", "Marley", "Pop-eye", "Dino do Sauro",
//                "Daniel", "Rodolfo", "Marley", "Pop-eye", "Dino do Sauro",
//                "Daniel", "Rodolfo", "Marley", "Pop-eye", "Dino do Sauro",
//                "Daniel", "Rodolfo", "Marley", "Pop-eye", "Dino do Sauro",
//                "Daniel", "Rodolfo", "Marley", "Pop-eye", "Dino do Sauro",
//                "Daniel", "Rodolfo", "Marley", "Pop-eye", "Dino do Sauro",
//                "Daniel", "Rodolfo", "Marley", "Pop-eye", "Dino do Sauro"};

        //Vamos usar nosso banco de dados ao inves da array de strings.
        AlunoDAO dao = new AlunoDAO(this);
        List<Aluno> alunos = dao.buscarAlunos();

        //Com esse foreach poderemos verificar qual o id dos nossos alunos.
        for (Aluno aluno : alunos) {
            Log.i("Id do aluno(a):", String.valueOf(aluno.getId()));
        }

        dao.close();

        //Recuperamos a nossa list do xml para ser manipulada.
        //listaDeAlunos = (ListView) findViewById(R.id.lista_de_alunos);
        //Movido la pra cima.

        //O ArrayAdapter vai receber um contesto, uma lista(no caso uma do próprio android)
        //e os objetos que iram preenche-la.
        AlunosAdapter adapter = new AlunosAdapter(this, alunos); //Meu adapter personalizado!

        //Por fim, vamos colocar esse adapter na nossa lista.
        listaDeAlunos.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Call<AlunoSync> call = new RetrofitInicializador().getAlunoService().lista();
        call.enqueue(new Callback<AlunoSync>() {
            @Override
            public void onResponse(Call<AlunoSync> call, Response<AlunoSync> response) {
                // O "call" possuí dados referentes a requisição em si. O "response" por sua vez
                // possuí a resposta da requisição e o corpo da requisição. A lista de alunos é
                // obtida com o uso do método body.
                AlunoSync alunoSync = response.body();
                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                dao.sincroniza(alunoSync.getAlunos());
                dao.close();
                carregarLista();
                Log.i("onResponse", "Sincronizacao com sucesso");
            }

            @Override
            public void onFailure(Call<AlunoSync> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());
            }
        });

        carregarLista();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //Onde o aluno está? O menuInfo sabe sua posição.
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        //Agora vamos pegar esse aluno em nossa lista, baseado na posição que o menuInfo irá nos disponibilizar.
        final Aluno aluno = (Aluno) listaDeAlunos.getItemAtPosition(info.position);

        //E assim que se cria uma intent inplícita.
        MenuItem visitarSiteDoAluno = menu.add("Visitar site do aluno");
        Intent intentSite = new Intent(Intent.ACTION_VIEW); //Dizemos que tipo de ação queremos tomar, no caso é mostrar algo.
        String alunoSite = aluno.getSite();
        if (!aluno.getSite().startsWith("http://")) {
            alunoSite = "http://" + alunoSite;
        }
        intentSite.setData(Uri.parse(alunoSite)); //o "setData();" é para dados obrigátorios,
        // diferente do "putExtra();", pelo fato de que vamos o usar uma Uri e o método que faz isso
        //pra gente é ele.
        visitarSiteDoAluno.setIntent(intentSite);

        MenuItem itemSMS = menu.add("Mandar SMS");
        Intent intentSMS = new Intent(Intent.ACTION_VIEW);
        intentSMS.setData(Uri.parse("sms:" + aluno.getTelefone())); //Aqui definimos o que será exibido pela intent.
        itemSMS.setIntent(intentSMS);

        MenuItem itemLigar = menu.add("Ligar para aluno");
        itemLigar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Se a checagem de permissão não for igual ao "PERMISSION_GRANTED", quer dizer que
                //não foi pedido ao usuário permissõa alguma e no casso terá de ser feita.
                if (ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    //Aqui pedimos ao usuário a permissão:
                    ActivityCompat.requestPermissions(ListaAlunosActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CODIGO_PHONE);//O "123" é o requestCode.
                } else {
                    Intent intentLigar = new Intent(Intent.ACTION_CALL);
                    intentLigar.setData(Uri.parse("tel:" + aluno.getTelefone()));
                    startActivity(intentLigar);
                }
                return false;
            }
        });

        MenuItem itemMapa = menu.add("Achar no mapa");
        Intent intentMapa = new Intent(Intent.ACTION_VIEW);
        intentMapa.setData(Uri.parse("geo:0,0?q=" + aluno.getEndereco())); //se for: "geo:0,0?z=14&q=" definimos o zoom inicial.
        itemMapa.setIntent(intentMapa);

        MenuItem itemDetalhes = menu.add("Detalhes");
        itemDetalhes.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(ListaAlunosActivity.this, aluno.detalhesDoAluno(), Toast.LENGTH_LONG).show();
                return false;
            }
        });

        MenuItem itemDeletar = menu.add("Deletar");
        itemDeletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                dao.deleta(aluno);
                dao.close();
                //Sem carregar a lista aqui, nossa lista não será atualiza assim que algum aluno seja deletado.
                carregarLista();
                Toast.makeText(ListaAlunosActivity.this, "(" + aluno.getNome() + ")" + " Deletado!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

//    Esse método permite que mudemos o comportamento de quando uma permição for requerida.
//    Caso tenhamos mais de uma, nós podemos diferencialas com o "int requestCode".
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == 123){
//        }
//    }
//


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_aluno, menu);
        return true; //Isso indircará que o menu irá aparecer. //super.onCreateOptionsMenu(menu);//
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_enviar_notas:
                //Não podemos fazer coneções com de rede na Thread Principal, pois isso pode travar
                //a tela do usuário e o android vai derrubar nossa aplicação.
                //Se tentarmos criar uma Thread (ex.: Thread t = new Thread(new Runnable)) e tentarmos
                //usar o nosso Toast, teremos outro exception, pois não podemos manipular a tela
                //em background. A solução para esse problema é o AsyncTask.(Ele retornou...)

                new EnviaAlunosTask(this).execute();
                //Pronto! Agora está em background.
                break;

            case R.id.menu_baixar_provas:
                Intent vaiPraProvas = new Intent(this, ListaProvasActivity.class);
                startActivity(vaiPraProvas);
                break;

            case R.id.menu_mapa:
                Intent vaiParaMapa = new Intent(this, MapaActivity.class);
                startActivity(vaiParaMapa);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
