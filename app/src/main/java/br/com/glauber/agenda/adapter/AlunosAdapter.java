package br.com.glauber.agenda.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.glauber.agenda.R;
import br.com.glauber.agenda.modelo.Aluno;

public class AlunosAdapter extends BaseAdapter {

    private final List<Aluno> alunos;
    private final Context context;

    public AlunosAdapter(Context context, List<Aluno> alunos) {
        this.context = context;
        this.alunos = alunos;
    }

    @Override
    public int getCount() {
        return alunos.size(); //Precisamos dizer ao android quantos alunos serão mostrados.
    }

    @Override
    public Object getItem(int position) {
        return alunos.get(position); //Aqui pegamos o aluno.
    }

    @Override
    public long getItemId(int position) {
        //return alunos.get(position).getId(); //Poderia ser outra coisa, só para identificar o aluno.
        // que pegamos no "getItem(int position)"
        // Almejando melhorar o nosso id, ele se tornou uma string e não um long como o método getItemId() espera.
        // Podemos então retornar 0 pois não estamos mais utilizando essa abordagem.
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
        TextView view = new TextView(context); //Para cada item devolveremos uma view, assim como era no adapter normal.
        //view.setText("Aluno na posição " + position);
        Aluno aluno = alunos.get(position); //Pega um aluno em uma posição.
        view.setText(aluno.toString()); //Agora damos um "toString()" nesse aluno específico.
        */
        Aluno aluno = alunos.get(position);

        LayoutInflater inflater = LayoutInflater.from(context); //Dizemos onde a lista será inflada.
        View view = convertView; //O "convertView" instancia e deixa-os carregados para mostrar para o usuário durante o
        // rolamento na lista com grandes dados.
        if (view == null) {
            view = inflater.inflate(R.layout.lista_alunos_personalizada, parent, false); //o "parent" informa quem é o layout pai no xml
            // o "false" informa para o android para não coloacar esse inflater de cara, pois se fizer isso mais pra frente
            //podemos(e iremos) tomar um NullPoiterException!
        } //Fazendo isso não precisamos instanciar todos de uma vez, somente quando o "convertView" estiver vazio.
        //assim, aumentamos a perfomance da aplicação.

        TextView campoNome = (TextView) view.findViewById(R.id.lista_personalizada_de_alunos_nome);
        campoNome.setText(aluno.getNome());

        TextView campoTelefone = (TextView) view.findViewById(R.id.lista_personalizada_de_alunos_telefone);
        campoTelefone.setText(aluno.getTelefone());

        //===================================================================================================
        // Se tentarmos criar essas TextView's quando o celular estiver no modo retrato, vamos tentar preencher
        // o objeto com uma referència vazia(NullPointerException), pois essas duas views so existem no
        // modo paisagem(landscape).
        TextView campoEndereco = (TextView) view.findViewById(R.id.lista_personalizada_de_alunos_endereco);
        if (campoEndereco != null) {
            campoEndereco.setText(aluno.getEndereco());
        }

        TextView campoSite = (TextView) view.findViewById(R.id.lista_personalizada_de_alunos_site);
        if (campoSite != null) {
            campoSite.setText(aluno.getSite());
        }
        //===================================================================================================

        ImageView campoFoto = (ImageView) view.findViewById(R.id.lista_personalizada_de_alunos_foto);
        String caminhoFoto = aluno.getCaminhoFoto();
        if (caminhoFoto != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(caminhoFoto);
            //Bitmap bitmapReduzido = Bitmap.createScaleType(bitmap, 300, 300, true); Isso da alguma erro, por hora não descoberto!
            campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
            campoFoto.setImageBitmap(bitmap);
            //campoFoto.setTag(caminhoFoto); -> Não vamos precisar recuperar isso mais pra frente, deferente
            // da foto do aluno no formulário.
        }
        return view;
    }
}
