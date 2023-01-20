package com.example.lucialm_simondice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final int TURNOS_FACIL = 5, TURNOS_DIFICIL = 20;
    // Para que cuando iniciemos desde una dificultad, el juego no
    // vuelva a la dificultad por defecto.
    private int dificultadActual = TURNOS_FACIL;

    private ArrayList<Button> botones = new ArrayList<Button>();
    private TextView textoCifrasRecordar, textoRespuesta;
    private String numeroRecordar, numeroJugador;
    private Animation scaleUp, scaleDown;
    private Button botonIniciar;
    private Context contexto;
    private MediaPlayer mp;

    private Random rnd = new Random();
    private int contadorTurnos;
    private boolean ganado = false;
    private char posicion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contexto = getApplicationContext();

        // Animaciones -------------------------------------------------------------------------------------------------
        scaleUp = AnimationUtils.loadAnimation(contexto, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(contexto, R.anim.scale_down);

        // TextFields --------------------------------------------------------------------------------------------------
        textoRespuesta = findViewById(R.id.textoRespuesta);
        textoCifrasRecordar = findViewById(R.id.textoCifrasRecordar);

        // Botones ------------------------------------------------------------------------------------------------------
        botonIniciar = findViewById(R.id.botonIniciar);
        botonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Funcionalidad
                recreate();

                // La animación termina de ejecutarse (100ms) antes de que la siguiente comience
                botonIniciar.startAnimation(scaleUp);
                botonIniciar.startAnimation(scaleDown);

                // Toast dificultad
                Toast.makeText(MainActivity.this, "Dificultad: " + (dificultadActual == TURNOS_FACIL ? "Fácil" : "Difícil"),
                        Toast.LENGTH_LONG).show();
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

        inicializar(dificultadActual);
    }

    /**
     * Crea una nueva partida de la última dificultad que fuese seleccionada
     * en el menú.
     *
     * @param dificultad
     */
    private void inicializar(int dificultad) {
        numeroRecordar = String.valueOf(rnd.nextInt(10));

        switch (numeroRecordar) {
            case "0":
                mp = MediaPlayer.create(contexto, R.raw.illojuan0);
                mp.start();
                break;
            case "1":
                mp = MediaPlayer.create(contexto, R.raw.illojuan1);
                mp.start();
                break;
            case "2":
                mp = MediaPlayer.create(contexto, R.raw.illojuan2);
                mp.start();
                break;
            case "3":
                mp = MediaPlayer.create(contexto, R.raw.illojuan3);
                mp.start();
                break;
            case "4":
                mp = MediaPlayer.create(contexto, R.raw.illojuan4);
                mp.start();
                break;
            case "5":
                mp = MediaPlayer.create(contexto, R.raw.illojuan5);
                mp.start();
                break;
            case "6":
                mp = MediaPlayer.create(contexto, R.raw.illojuan6);
                mp.start();
                break;
            case "7":
                mp = MediaPlayer.create(contexto, R.raw.illojuan7);
                mp.start();
                break;
            case "8":
                mp = MediaPlayer.create(contexto, R.raw.illojuan8);
                mp.start();
                break;
            case "9":
                mp = MediaPlayer.create(contexto, R.raw.illojuan9);
                mp.start();
                break;
        }

        contadorTurnos = 0;
        dificultadActual = dificultad;
        inicializarBotones(botones, dificultad);
    }

    /**
     * Bloquea los botones numerados (se usa mientras se reproducen los audios).
     * @param botones
     */
    private void bloquearBotones(ArrayList<Button> botones) {
        for (Button x : botones) {
            x.setEnabled(false);
        }
    }

    /**
     * Desbloquea los botones numerados.
     * @param botones
     */
    private void desbloquearBotones(ArrayList<Button> botones) {
        for (Button x : botones) {
            x.setEnabled(true);
        }
    }

    /**
     * Da la funcionalidad a los botones numerados, que llaman al método encargado de manejar cada
     * turno en el juego (manejoTurno()).
     *
     * @param botones
     * @param dificultad
     */
    private void inicializarBotones(ArrayList<Button> botones, int dificultad) {
        for (Button x : botones) {
            x.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TextField (se va rellenando hasta el penúltimo número a adivinar)
                    textoRespuesta.setText((textoRespuesta == null) ? x.getText() : textoRespuesta.getText() + String.valueOf(x.getText()));

                    // Animación
                    x.startAnimation(scaleUp);
                    x.startAnimation(scaleDown);

                    // Si es la primera vez que se juega (numeroJugador igual a null), solo ponemos el número del botón,
                    // pero si ya hemos acumulado números se lo sumamos a la String.
                    numeroJugador = (numeroJugador == null) ? (String) x.getText() : numeroJugador + x.getText();

                    // Funcionalidad
                    manejoTurno(dificultad);
                }
            });
            x.setEnabled(true);
        }
    }

    /**
     * Para evitar anidar mucho el código. Comprueba que el número que ha introducido el jugador sea igual al que
     * debe recordar.
     *
     * @return
     */
    private boolean comprobarRespuesta() {
        if (numeroJugador.equals(numeroRecordar)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Método que se encarga de la mayor parte de la lógica del juego.
     *
     * @param dificultad
     */
    private void manejoTurno(int dificultad) {
        System.out.println("Botones bloqueados");

        if ((numeroRecordar != null) && (numeroRecordar.length() == numeroJugador.length()) && comprobarRespuesta()) {
            bloquearBotones(botones);
            contadorTurnos++;

            int ultimoGenerado = rnd.nextInt(10);
            numeroRecordar = (numeroRecordar == null) ? String.valueOf(ultimoGenerado) : numeroRecordar + ultimoGenerado;

            // Handler con delay encontrado como alternativa a Thread.sleep, que congelaba la interfaz
            for (int i = 0; i < numeroRecordar.length(); i++) {
                int finalI = i; // El IDE obliga a añadir esta línea
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        // Detiene la ejecución del método cuando se ha ganado
                        // (si no se pone, el MediaPlayer continúa reproduciendo)
                        if(numeroRecordar == null || finalI >= numeroRecordar.length()) return;
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
                    // Delay dinámico dependiendo de la posición en la que deba reproducirse
                    // y de la dificultad (para que no se superpongan)
                }, dificultadActual == TURNOS_FACIL ? i * 1500 : i * 600);
            }

            textoCifrasRecordar.setText("Asertao nene");
            textoRespuesta.setText("");
            numeroJugador = ""; // Resetear el número introducido

            if (contadorTurnos == dificultad) {
                ganado = true;
                numeroRecordar = null;
                numeroJugador = null;

                AlertDialog.Builder victoria = new AlertDialog.Builder(this);
                victoria.setTitle("¡HAS GANADO!");
                mp = MediaPlayer.create(contexto, R.raw.plinplinplon);
                mp.start();
                victoria.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "¡Felicidades!", Toast.LENGTH_LONG).show();
                        mp.stop();
                    }
                });
                victoria.setNeutralButton("Otra", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        recreate();
                        Toast.makeText(MainActivity.this, "Dificultad: " + (dificultadActual == TURNOS_FACIL ? "Fácil" : "Difícil"),
                                Toast.LENGTH_LONG).show();
                        mp.stop();
                    }
                });
                AlertDialog alert = victoria.create();
                alert.show();
                // TODO: Dialog con que ha ganao
                textoCifrasRecordar.setText("Has ganao");
            }
        } else if (!comprobarRespuesta()) {
            Toast.makeText(contexto, "Fallaste", Toast.LENGTH_SHORT).show();
        } else {
            // TODO: Dialog con que la ha cagao (decirle los turnos que ha durao)
            // TEST:
            textoCifrasRecordar.setText(numeroRecordar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    public void seleccionarDificultad(View view) {
        registerForContextMenu(view);
        openContextMenu(view);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info) {
        super.onCreateContextMenu(menu, v, info);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.dificultad_menu, menu);
    }

    /**
     * Funcionalidad del menú de los tres puntos.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dificultad:
                seleccionarDificultad(findViewById(R.id.tabla));
                return true;
        }
        return true;
    }

    /**
     * Funcionalidad de cada elemento del ContextMenu
     * (los RadioButton) que cambian la dificultad.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.facil:
                inicializar(TURNOS_FACIL);
                dificultadActual = TURNOS_FACIL;
                return true;
            case R.id.dificil:
                inicializar(TURNOS_DIFICIL);
                dificultadActual = TURNOS_DIFICIL;
                return true;
        }
        return true;
    }
}