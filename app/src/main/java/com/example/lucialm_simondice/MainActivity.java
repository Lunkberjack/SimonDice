package com.example.lucialm_simondice;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final int TURNOS_FACIL = 10, TURNOS_DIFICIL = 20;
    // Para que cuando iniciemos desde una dificultad, el juego no
    // vuelva a la dificultad por defecto.
    private int dificultadActual;

    private ArrayList<Button> botones = new ArrayList<Button>();
    private TextView textoCifrasRecordar, textoRespuesta;
    private String numeroRecordar, numeroJugador;
    private Button botonIniciar;
    private MediaPlayer mp;

    private Random rnd = new Random();
    // Si el primer turno cuenta (el que tiene solo un número acumulado)
    private int contadorTurnos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numeroRecordar = "1";
        //numeroRecordar = String.valueOf(rnd.nextInt(10));
        textoRespuesta = findViewById(R.id.textoRespuesta);
        textoCifrasRecordar = findViewById(R.id.textoCifrasRecordar);

        botonIniciar = findViewById(R.id.botonIniciar);
        botonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inicializar(dificultadActual);
            }
        });

        botones.add(findViewById(R.id.button0));
        botones.add(findViewById(R.id.button1));
        botones.add(findViewById(R.id.button2));
        botones.add(findViewById(R.id.button3));
        botones.add(findViewById(R.id.button4));
        botones.add(findViewById(R.id.button5));
        botones.add(findViewById(R.id.button6));
        botones.add(findViewById(R.id.button7));
        botones.add(findViewById(R.id.button8));
        botones.add(findViewById(R.id.button9));

        inicializar(TURNOS_FACIL);
    }

    private void inicializar(int dificultad) {
        contadorTurnos = 1;
        dificultadActual = dificultad;
        inicializarBotones(botones, dificultad);
    }

    private void inicializarBotones(ArrayList<Button> botones, int dificultad) {
        for(Button x : botones) {
            x.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Si es la primera vez que se juega (numeroJugador igual a null), solo ponemos el número del botón,
                    // pero si ya hemos acumulado números se lo sumamos a la String.
                    numeroJugador = (numeroJugador == null) ? (String) x.getText() : numeroJugador + x.getText();
                    manejoTurno(dificultad);
                }
            });
            x.setEnabled(true);
        }
    }

    private boolean comprobarRespuesta() {
        if(numeroJugador.equals(numeroRecordar)) {
            return true;
        } else {
            return false;
        }
    }

    private void manejoTurno(int dificultad) {
        if(comprobarRespuesta() && numeroRecordar.length() == numeroJugador.length()) {
            contadorTurnos++;
            int ultimoGenerado = rnd.nextInt(10);
            numeroRecordar = (numeroRecordar == null) ? String.valueOf(ultimoGenerado) : numeroRecordar + ultimoGenerado;
            textoCifrasRecordar.setText("Asertao nene");
            numeroJugador = "";

            if(contadorTurnos == dificultad) {
                // TODO: Dialog con que ha ganao
                textoCifrasRecordar.setText("Has ganao");
            }
        } else {
            // TODO: Dialog con que la ha cagao (decirle los turnos que ha durao)
            // TEST:
            textoCifrasRecordar.setText(numeroJugador + " " + numeroRecordar);
        }
    }
}