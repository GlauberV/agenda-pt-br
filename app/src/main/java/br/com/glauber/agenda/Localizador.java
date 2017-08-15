package br.com.glauber.agenda;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import static br.com.glauber.agenda.ListaAlunosActivity.CODIGO_SMS;

public class Localizador implements GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

    private final GoogleApiClient client;
    private final GoogleMap mapa;

    public Localizador(Context context, GoogleMap mapa){
        client = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API) //Para configurarmos a API "LocationServices".
                .addConnectionCallbacks(this) //Será chamada quando estabelecermos a conexão, atravé dos seus dois métodos: "onConnected" e "onConnectionSuspended".
                // Com o "this" deixamos nossa classe responsavel por reagir a chegada de dados ao inves de uma outra classe fora daqui fizesse o mesmo.
                .build();

        client.connect();
        /*
          O "connect" é um método assícrono, então não precisa esperar receber uma resposta para dar continuidade as suas atividades.
          Quando a conexão for estabelecida, a aplicação consiguirá se comunicar com o GPS do celular e pedir as atualizações de posição.
          Dentro do método "onConnected" vamos dizer como vamos receber esses dados, quais são as nossas regras.

        */

        this.mapa = mapa;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest request = new LocationRequest();
        //Não seria interessante receber atualizações se estivermos parados e pra isso usaremos o:
        request.setSmallestDisplacement(50);//Definindo como 50m o minimo que temos que se locomover.
        //Mas se o usuário se locomover rápido demais teriamos que receber atualizações constantemente
        //e para isso seria interessante estabelecermos um intervalo minimo:
        request.setInterval(1000); //No caso em milisegundos.
        //Podemos controlar a PRECISÃO X ECONÔMIA DE BATERIA:
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Passando o request para o LocationServices.
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
        //Como terceiro parêmetro dizemos quem irá tratar esses dados.
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng posicao = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(posicao);
        mapa.moveCamera(cameraUpdate);
    }
}
