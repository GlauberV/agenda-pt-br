package br.com.glauber.agenda;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.glauber.agenda.modelo.Prova;

public class ListaProvasFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Relembrando, o "false" ira fazer com que essa view NÂO seja filha do "container" apenas por enquando.
        View view = inflater.inflate(R.layout.fragment_lista_provas, container, false);

        List<String> topicosPort = Arrays.asList("Sujeito", "Objeto direto", "Objeto indireto");
        Prova provaPortugues = new Prova("Português", "05/07/2017", topicosPort);

        List<String> topicosMat = Arrays.asList("Trigonometria", "Equações do segundo grau");
        Prova provaMatematica = new Prova("Matemática", "08/07/2017", topicosMat);

        List<Prova> provas = new ArrayList<>(); // Ou -> List<Prova> provas = Arrays.asList(provaMatematica, provaPortugues);
        provas.add(provaMatematica);
        provas.add(provaPortugues);

        //O "Fragment" não é considerado um contexto, mas possui um método que traz o contexto associado a ele. O mesmo será feito em baixo.
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, provas);
        ListView lista = (ListView) view.findViewById(R.id.provas_lista); //Como o "findViewById()"  não possui uma "View" por padrão, teremos que mostrar uma a ele.
        lista.setAdapter(adapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Prova prova = (Prova) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), "Você clicou na prova de " + prova, Toast.LENGTH_SHORT).show();
                //Mas ai você me pergunta: como ele sabe que quando eu clicar no item da lista eu vou querer ver a materia da prova? R.: "ToString();"

                //Se colocarmos esse codigo em uma activity onde nao existe o "fragment_principal" teriamos um retorno nulo
                //e nós tentariamos substituir esse valor pelo Fragment. Nossa aplicação iria parar, pois o Android não
                //encontraria esse frame, então construir dessa forma é ruim, pois estamos prendendo nosso fragment a uma
                //activity específica. Então vamo deixar essa responsabilidade para a "ListaProvasFragment".
//                FragmentManager manager = getFragmentManager();
//                FragmentTransaction tx = manager.beginTransaction();
//                tx.replace(R.id.fragment_principal, new DetalhesProvaFragment());
//                tx.commit();

                ListaProvasActivity provasActivity = (ListaProvasActivity) getActivity();
                provasActivity.selecionaProva(prova);

            }
        });

        return view;
    }
}
