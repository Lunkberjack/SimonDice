package com.example.lucialm_simondice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuInflater;
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
    private Context contexto;
    private String numeroRecordar, numeroJugador;
    private Button botonIniciar;
    private MediaPlayer mp;

    private Random rnd = new Random();
    // Si el primer turno cuenta (el que tiene solo un número acumulado)
    private int contadorTurnos;
    char posicion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contexto = getApplicationContext();

        numeroRecordar = "1";

        mp = MediaPlayer.create(contexto, R.raw.illojuan1);
        mp.start();

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

    private void bloquearBotones(ArrayList<Button> botones) {
        for (Button x : botones) {
            x.setEnabled(false);
        }
    }

    private void desbloquearBotones(ArrayList<Button> botones) {
        for (Button x : botones) {
            x.setEnabled(true);
        }
    }

    private void inicializarBotones(ArrayList<Button> botones, int dificultad) {
        for (Button x : botones) {
            x.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Si es la primera vez que se juega (numeroJugador igual a null), solo ponemos el número del botón,
                    // pero si ya hemos acumulado números se lo sumamos a la String.
                    numeroJugador = (numeroJugador == null) ? (String) x.getText() : numeroJugador + x.getText();
                    try {
                        manejoTurno(dificultad);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            x.setEnabled(true);
        }
    }

    private boolean comprobarRespuesta() {
        if (numeroJugador.equals(numeroRecordar)) {
            return true;
        } else {
            return false;
        }
    }

    private void manejoTurno(int dificultad) throws InterruptedException {
        System.out.println("Botones bloqueados");

        if (numeroRecordar.length() == numeroJugador.length() && comprobarRespuesta()) {
            for(Button x : botones) {
                x.setEnabled(false);
            }

            contadorTurnos++;
            int ultimoGenerado = rnd.nextInt(10);
            numeroRecordar = (numeroRecordar == null) ? String.valueOf(ultimoGenerado) : numeroRecordar + ultimoGenerado;

            for (int i = 0; i < numeroRecordar.length(); i++) {
                int finalI = i; // El IDE obliga a añadir esta línea
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        switch (numeroRecordar.charAt(finalI)) {
                            case '0':
                                mp = MediaPlayer.create(contexto, R.raw.illojuan0);
                                mp.start();
                                break;
                            case '1':
                                mp = MediaPlayer.create(contexto, R.raw.illojuan1);
                                mp.start();
                                break;
                            case '2':
                                mp = MediaPlayer.create(contexto, R.raw.illojuan2);
                                mp.start();
                                break;
                            case '3':
                                mp = MediaPlayer.create(contexto, R.raw.illojuan3);
                                mp.start();
                                break;
                            case '4':
                                mp = MediaPlayer.create(contexto, R.raw.illojuan4);
                                mp.start();
                                break;
                            case '5':
                                mp = MediaPlayer.create(contexto, R.raw.illojuan5);
                                mp.start();
                                break;
                            case '6':
                                mp = MediaPlayer.create(contexto, R.raw.illojuan6);
                                mp.start();
                                break;
                            case '7':
                                mp = MediaPlayer.create(contexto, R.raw.illojuan7);
                                mp.start();
                                break;
                            case '8':
                                mp = MediaPlayer.create(contexto, R.raw.illojuan8);
                                mp.start();
                                break;
                            case '9':
                                mp = MediaPlayer.create(contexto, R.raw.illojuan9);
                                mp.start();
                                break;
                        }
                        // Para que desbloquee los botones solo cuando los audios hayan finalizado
                        if(finalI == numeroRecordar.length() - 1) {
                            desbloquearBotones(botones);
                        }
                    }
                    // Delay dependiendo de la posición en la que deba reproducirse y de la dificultad
                    // (para que no se superpongan)
                }, dificultadActual == TURNOS_FACIL ? i * 1500 : i * 1000);
            }

            textoCifrasRecordar.setText("Asertao nene");
            numeroJugador = "";

            if (contadorTurnos == dificultad) {
                mp.release(); // Libera recursos
                // TODO: Dialog con que ha ganao
                textoCifrasRecordar.setText("Has ganao");
            }
        } else {
            // TODO: Dialog con que la ha cagao (decirle los turnos que ha durao)
            // TEST:
            textoCifrasRecordar.setText(numeroJugador + " " + numeroRecordar);
            mp.release();
        }
    }

    // TODO: No es así pero te haces a la idea
    @NonNull
    @Override
    public MenuInflater getMenuInflater() {
        return super.getMenuInflater();
    }
}