package br.com.glauber.agenda;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import br.com.glauber.agenda.modelo.Prova;

public class ListaProvasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_provas);

        //Temos agora um especialista em manipular Fragment's e precisamos dizer a ele para trocar o FrameLayout com o Fragment.
        FragmentManager fragmentManager = getSupportFragmentManager(); //Da classe com support pelo mesmo motivo.
        FragmentTransaction transaction = fragmentManager.beginTransaction(); //Temos que iniciar a transação.

        //Precisamos saber se o dispositivo está no modo paisagem ou no modo retrato, caso contrário, tentaremos preencher um fragment que não existe.
        transaction.replace(R.id.fragment_principal, new ListaProvasFragment()); //Aqui fazemos a troca do placeholder com o nosso fragment.
        if (estaNoModoPaisagem()) { //Usando o application resources.
            transaction.replace(R.id.fragment_secundario, new DetalhesProvaFragment()); //Nossa outra tela. O transaction vai agrupar todas elas.
        }

        transaction.commit(); //E precisamos comitar.

    }

    //Deixando o código mais facíl de ser lido.
    private boolean estaNoModoPaisagem() {
        return getResources().getBoolean(R.bool.modoPaisagem);
    }

    public void selecionaProva(Prova prova) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (!estaNoModoPaisagem()) {
            FragmentTransaction tx = fragmentManager.beginTransaction();

            //Assim recuperamos os dados:
            DetalhesProvaFragment detalhesFragment = new DetalhesProvaFragment();
            Bundle parametros = new Bundle(); //O Bundle é um pacote para guardar dados.
            parametros.putSerializable("prova", prova);// Colocamos a prova dentro do Bundle.
            detalhesFragment.setArguments(parametros);// E passamos para o fragment.
            //Agora poderemos levar esses dados para o fragment de detalhes.

            tx.replace(R.id.fragment_principal, detalhesFragment);
            tx.addToBackStack(null);//Esse "null" serve para buscarmos um estado que foi marcado para
            //ser retornado depois.
            tx.commit();
        } else {
            DetalhesProvaFragment detalhesFragment =
                    (DetalhesProvaFragment) fragmentManager.findFragmentById(R.id.fragment_secundario);
            detalhesFragment.populaCamposCom(prova);
        }
    }
}
