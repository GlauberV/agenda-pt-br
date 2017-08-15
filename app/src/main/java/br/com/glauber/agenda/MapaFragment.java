package br.com.glauber.agenda;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import br.com.glauber.agenda.dao.AlunoDAO;
import br.com.glauber.agenda.modelo.Aluno;

import static br.com.glauber.agenda.MapaActivity.CODIGO_LOCALIZADOR;

public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //Iremos usar o método "getMapAsync()" que irá nos dar uma instância do Google Maps, que nos
        //permitirá colocar marcações e outras costumizações no nosso mapa.
        getMapAsync(this);
        //Esse objeto responderá quando o mapa estiver pronto e para funcionar teremos que ter um
        //método especifíco que indique que o mapa está pronto: onMapReadyCallback();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        AlunoDAO alunoDAO = new AlunoDAO(getContext());
        for (Aluno aluno : alunoDAO.buscarAlunos()) {
            LatLng coordenada = pegaCoordenadaDoEndereço(aluno.getEndereco());
            if (coordenada != null) {
                final MarkerOptions marcador = new MarkerOptions();
                marcador.position(coordenada);
                marcador.title(aluno.getNome());
                marcador.snippet(String.valueOf(aluno.getNota()));
                googleMap.addMarker(marcador);
            }
        }
        alunoDAO.close();

        //Não precisamos mais instanciar o LatLnt manualmente:
        LatLng posicaoDaEscola = pegaCoordenadaDoEndereço("Rua Vergueiro 3185, Vila Mariana, Sao Paulo");
        //Agora passamos o endereço e o zoom inicial:
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(posicaoDaEscola, 17);
        googleMap.moveCamera(update);


        if (getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, CODIGO_LOCALIZADOR);
        } else {
            new Localizador(getContext(), googleMap);
        }

        

    }

    private LatLng pegaCoordenadaDoEndereço(String endereco) {

        //Para transformar um endereço em latitude e longitude usaremo o "Geocoder"
        Geocoder geocoder = new Geocoder(getContext());
        try {

            List<Address> resultados =
                    geocoder.getFromLocationName(endereco, 1);
            //Passamos o endereço e o numero de resultados que queremos.

            if (!resultados.isEmpty()) {
                LatLng posicao = new LatLng(resultados.get(0).getLatitude(), resultados.get(0).getLongitude());
                return posicao;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
