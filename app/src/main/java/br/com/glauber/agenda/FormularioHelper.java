package br.com.glauber.agenda;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import br.com.glauber.agenda.modelo.Aluno;

public class FormularioHelper {

    //Colocamos essa variaveis como variaveis de classe para nao ter que repetir codigo.
    private final EditText campoNome;
    private final EditText campoEndereco;
    private final EditText campoTelefone;
    private final EditText campoSite;
    private final RatingBar campoNota;
    private final ImageView campoFoto;

    private Aluno aluno;

    //Buscamos os dados que forem digitados nos campos do formulario.
    public FormularioHelper(FormularioActivity formularioActivity) {
        this.aluno = new Aluno();
        campoNome = (EditText) formularioActivity.findViewById(R.id.formulario_nome);
        campoEndereco = (EditText) formularioActivity.findViewById(R.id.formulario_endereco);
        campoTelefone = (EditText) formularioActivity.findViewById(R.id.formulario_telefone);
        campoSite = (EditText) formularioActivity.findViewById(R.id.formulario_site);
        campoNota = (RatingBar) formularioActivity.findViewById(R.id.formulario_rtb_nota);
        campoFoto = (ImageView) formularioActivity.findViewById(R.id.formulario_foto);
    }

    //Aqui nos atribuimos os dados do aluno baseado nos dados que
    // foram digitados nos campos do formulario.
    public Aluno pegaAluno() {
        //this.aluno = new Aluno(); --> É desnecessário? Nenhuma mudança aparentemente.
        // Espera! É necessario sim, porque sem isso o android não vai entender que o aluno ja
        // existe pois seu "id" estará nulo, pelo fato da classe "Aluno" ser instanciada de novo.
        aluno.setNome(campoNome.getText().toString());
        aluno.setEndereco(campoEndereco.getText().toString());
        aluno.setTelefone(campoTelefone.getText().toString());
        aluno.setSite(campoSite.getText().toString());
        aluno.setNota(Double.valueOf(campoNota.getProgress()));
        aluno.setCaminhoFoto((String) campoFoto.getTag()); // A Tag serve para pendurar arquivos nas Views.
        return aluno;
    }

    public void preencherFormulario(Aluno aluno) {
        this.aluno = aluno; //Fazendo isso não perdemos dados(ex.: id) do aluno com o qual vamos preencher o formulário.
        //Assim descobriremos que esse aluno já existe.
        campoNome.setText(aluno.getNome());
        campoEndereco.setText(aluno.getEndereco());
        campoTelefone.setText(aluno.getTelefone());
        campoSite.setText(aluno.getSite());
        campoNota.setProgress(aluno.getNota().intValue());
        if (aluno.getCaminhoFoto() != null) {
            carregaFoto(aluno.getCaminhoFoto());
        }
    }

    public void carregaFoto(String caminhoFoto) {
        Bitmap bitmap = BitmapFactory.decodeFile(caminhoFoto);
        //Bitmap bitmapReduzido = Bitmap.createScaleType(bitmap, 300, 300, true); Isso da alguma erro, por hora nao descoberto!
        campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
        campoFoto.setImageBitmap(bitmap);
        campoFoto.setTag(caminhoFoto);

        //Por algum motivo é necessário, as vezes, tirar mais de uma foto.
    }
}
