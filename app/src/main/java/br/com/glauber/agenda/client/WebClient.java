package br.com.glauber.agenda.client;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class WebClient {

    public String post(String json) {
        String endereco = "https://www.caelum.com.br/mobile";
        return realizaConexao(json, endereco);
    }

    public void enviar(String json) {
        String endereco = "http://192.168.1.3:8080/api/aluno";
        realizaConexao(json, endereco);
    }

    @Nullable
    private String realizaConexao(String json, String endereco) {
        try {
            //Guardamos a url do nosso servidor em uma variavel do tipo URL, (JWS I) mas agora vamos deixa-la como atributo e reaproveitar essa conexão.
            URL url = new URL(endereco);
            //Criamos então a coneção, porém ela ainda não está estabelecida.
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //Queremos enviar esses dados em formato JSON e também recebe-los no mesmo formato, para isso:
            connection.setRequestProperty("Content-type", "application/json"); //Como estamos enviando
            connection.setRequestProperty("Accept", "application/json");

            //Como vamos fazer um POST, precisamos indicar que haverá saída.
            connection.setDoOutput(true);

            //Com esse "PrintStream" conectado na saída, poderemos escrever nele.
            PrintStream output = new PrintStream(connection.getOutputStream());
            output.println(json);

            //Conectando ao servidor
            connection.connect();

            //Agora precisamos ler o que será devolvido para nós.
            Scanner scanner = new Scanner(connection.getInputStream());
            String resposta = scanner.next();
            return resposta;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Se passar pelos "catch's" e não encontrar nenhum erro, vamos devolver para quem usar
        //o WebClient nada, dessa forma não há erros.
        return null;
    }
}