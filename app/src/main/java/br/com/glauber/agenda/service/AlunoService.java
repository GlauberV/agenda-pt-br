package br.com.glauber.agenda.service;

import br.com.glauber.agenda.dto.AlunoSync;
import br.com.glauber.agenda.modelo.Aluno;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Glauber on 03/08/2017.
 */

public interface AlunoService {

    @POST("aluno")
    Call<Void> postAluno(@Body Aluno aluno);

    @GET("aluno")
    Call<AlunoSync> lista();
}

// Linha 17 - Verbo do Http. Os verbos fazem ações. Com esse "aluno" completamos a url.
// Linha 18 - Queremos enviar um aluno, mas primeiro temos que dizer para a requisição o que esse
// "aluno" representa, se ele faz parte do cabeçalho(header), do caminho da URL(path) ou corpo(body).
// Se fizer parte do corpo, ele será convertindo para JSON e será enviado no corpo da requisição
// As call's do retrofit para serem executadas com sucesso exigem que seja informado um tipo de retorno.
// Como não esperamos nada deixaremos, via generics, como void.