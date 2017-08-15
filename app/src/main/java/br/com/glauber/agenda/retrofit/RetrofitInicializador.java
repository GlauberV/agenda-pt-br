package br.com.glauber.agenda.retrofit;

import br.com.glauber.agenda.service.AlunoService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Glauber on 02/08/2017.
 */

public class RetrofitInicializador {

    private final Retrofit retrofit;

    public RetrofitInicializador() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(interceptor);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.3:8080/api/")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client.build())
                .build();
    }

    public AlunoService getAlunoService() {
        return retrofit.create(AlunoService.class);
    }
}

// Estamos usando o retrofit para facilitar nossa vida quando precisarmos criar requisições para o
// servidor. Linha 14 - criamos um construtor que incializará o retrofit, passando uma url(linha 16)
// Adicionando um dos conversores que o retrofit possui(linha 17) e finalizando a construção com o
// método "build()"(linha 18) Em momento nenhum tinhamos especificado quando iamos pegar o objeto
// aluno. Agora com o método "getAlunoService" (linha 22) podemos "conectar" a interface AlunoService
// e obter acesso ao método:
//
// @Post("aluno")
// Call<Void> postAluno(Aluno aluno);