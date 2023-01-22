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

/**
 * Juego de Simón dice en Android, con animaciones y sonidos.
 *
 * @author LuciaLM
 */
public class MainActivity extends AppCompatActivity {
    // Muy fácil (se puede cambiar) pero sirve para hacer pruebas rápidas.
    private final int TURNOS_FACIL = 5, TURNOS_DIFICIL = 10;

    // Se usarán en el método elegirAudio()
    private final int VICTORIA = 1;
    private final int DERROTA = 0;

    private ArrayList<Button> botones = new ArrayList<Button>();
    private TextView textoCifrasRecordar, textoRespuesta;
    private String numeroRecordar, numeroJugador;
    private Animation scaleUp, scaleDown;
    private Button botonIniciar;
    private Context contexto;
    private MediaPlayer mp0, mp1, mp2, mp3, mp4, mp5, mp6, mp7, mp8, mp9;

    private Random rnd = new Random();
    private int contadorTurnos;
    private boolean ganado = false;
    private char posicion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contexto = getApplicationContext();
        contadorTurnos = 0;

        // MediaPlayers
        mp0 = MediaPlayer.create(contexto, R.raw.illojuan0);
        mp1 = MediaPlayer.create(contexto, R.raw.illojuan1);
        mp2 = MediaPlayer.create(contexto, R.raw.illojuan2);
        mp3 = MediaPlayer.create(contexto, R.raw.illojuan3);
        mp4 = MediaPlayer.create(contexto, R.raw.illojuan4);
        mp5 = MediaPlayer.create(contexto, R.raw.illojuan5);
        mp6 = MediaPlayer.create(contexto, R.raw.illojuan6);
        mp7 = MediaPlayer.create(contexto, R.raw.illojuan7);
        mp8 = MediaPlayer.create(contexto, R.raw.illojuan8);
        mp9 = MediaPlayer.create(contexto, R.raw.illojuan9);

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
                Intent intent = new Intent(contexto, MainActivity.class);
                startActivity(intent);
                finish();

                // Efecto de sonido
                MediaPlayer mpInicio = MediaPlayer.create(contexto, R.raw.jump8bit);

                mpInicio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mpInicio.release();
                    }
                });

                mpInicio.start();

                // La animación termina de ejecutarse (100ms) antes de que la siguiente comience
                botonIniciar.startAnimation(scaleUp);
                botonIniciar.startAnimation(scaleDown);
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

    /**
     * Crea una nueva partida.
     *
     * @param dificultad
     */
    private void inicializar(int dificultad) {
        numeroRecordar = String.valueOf(rnd.nextInt(10));

        switch (numeroRecordar) {
            case "0":
                mp0.start();
                break;
            case "1":
                mp1.start();
                break;
            case "2":
                mp2.start();
                break;
            case "3":
                mp3.start();
                break;
            case "4":
                mp4.start();
                break;
            case "5":
                mp5.start();
                break;
            case "6":
                mp6.start();
                break;
            case "7":
                mp7.start();
                break;
            case "8":
                mp8.start();
                break;
            case "9":
                mp9.start();
                break;
        }

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
                    // TextField (se va rellenando hasta el penúltimo número adivinado)
                    textoRespuesta.setText((textoRespuesta == null) ? x.getText() : textoRespuesta.getText() + String.valueOf(x.getText()));

                    // Animación y efecto de sonido
                    x.startAnimation(scaleUp);
                    x.startAnimation(scaleDown);
                    MediaPlayer mpBoton = MediaPlayer.create(contexto, R.raw.jump8bit);
                    mpBoton.start();
                    mpBoton.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mpBoton.release();
                        }
                    });

                    // Si es la primera vez que se juega (numeroJugador igual a null), solo ponemos el número del botón,
                    // pero si ya hemos acumulado números se lo sumamos a la String.
                    numeroJugador = (numeroJugador == null) ? (String) x.getText() : numeroJugador + x.getText();

                    // Funcionalidad
                    manejoTurno(dificultad, x);
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
    private void manejoTurno(int dificultad, Button boton) {
        System.out.println("Botones bloqueados");

        if ((numeroRecordar != null) && (numeroRecordar.length() == numeroJugador.length()) && comprobarRespuesta()) {
            bloquearBotones(botones);

            int ultimoGenerado = rnd.nextInt(10);
            numeroRecordar = (numeroRecordar == null) ? String.valueOf(ultimoGenerado) : numeroRecordar + ultimoGenerado;

            // Handler con delay encontrado como alternativa a Thread.sleep, que congelaba la interfaz
            for (int i = 0; i < numeroRecordar.length(); i++) {
                int finalI = i; // El IDE obliga a añadir esta línea
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        // Detiene la ejecución del método cuando se ha ganado
                        // (si no se pone, el MediaPlayer continúa reproduciendo)
                        if (numeroRecordar == null || finalI >= numeroRecordar.length()) return;
                        switch (numeroRecordar.charAt(finalI)) {
                            case '0':
                                mp0.start();
                                break;
                            case '1':
                                mp1.start();
                                break;
                            case '2':
                                mp2.start();
                                break;
                            case '3':
                                mp3.start();
                                break;
                            case '4':
                                mp4.start();
                                break;
                            case '5':
                                mp5.start();
                                break;
                            case '6':
                                mp6.start();
                                break;
                            case '7':
                                mp7.start();
                                break;
                            case '8':
                                mp8.start();
                                break;
                            case '9':
                                mp9.start();
                                break;
                        }
                        // Para que desbloquee los botones solo cuando los audios hayan finalizado
                        if (finalI == numeroRecordar.length() - 1) {
                            desbloquearBotones(botones);
                        }
                    }
                    // Delay dinámico dependiendo de la posición en la que deba reproducirse
                    // y de la dificultad (para que no se superpongan)
                }, dificultad == TURNOS_FACIL ? i * 1500 : i * 750);
            }

            contadorTurnos++;
            textoCifrasRecordar.setText("Cifras a adivinar: " + numeroRecordar.length() + " de " + dificultad);
            textoRespuesta.setText("");
            numeroJugador = ""; // Resetear el número introducido

            if (contadorTurnos == dificultad) {
                ganar();
            }
        } else {
            // Varias veces intenté implementar un contador que detectara el turno del botón pulsado
            // y comparase el valor con el correspondiente en el número a recordar (no sé si tendría
            // algo que ver con el tipo de dato String) conclusión: siempre había que llegar a la longitud de la
            // cadena a recordar para que reconociera una derrota. Sigo sin entender qué falla.
            perder();
        }
    }

    /**
     * Elige el recurso de audio (id tipo int) que se reproducirá en caso
     * de victoria o de derrota. Elige al azar entre 6 sonidos para cada
     * ocasión.
     *
     * @param victoriaDerrota
     * @return
     */
    private int elegirAudio(int victoriaDerrota) {
        int azar = rnd.nextInt(6);
        int recurso = R.raw.illojuan0;
        if(victoriaDerrota == 1) {
            switch(azar) {
                case 0:
                    recurso = R.raw.epicardo;
                    break;
                case 1:
                    recurso = R.raw.zelda;
                    break;
                case 2:
                    recurso = R.raw.beti;
                    break;
                case 3:
                    recurso = R.raw.shootingstars;
                    break;
                case 4:
                    recurso = R.raw.wenomechainsama;
                    break;
                case 5:
                    recurso = R.raw.bais;
                    break;
            }
        } else {
            switch(azar) {
                case 0:
                    recurso = R.raw.decepcion;
                    break;
                case 1:
                    recurso = R.raw.plinplinplon;
                    break;
                case 2:
                    recurso = R.raw.cagaste;
                    break;
                case 3:
                    recurso = R.raw.nani;
                    break;
                case 4:
                    recurso = R.raw.tobecontinued;
                    break;
                case 5:
                    recurso = R.raw.youdied;
                    break;
            }
        }
        return recurso;
    }

    /**
     * Lógica a seguir cuando se gane el juego.
     *
     * Reproduce un sonido y permite crear un nuevo juego o salir (esta última opción
     * obliga a pulsar el botón de Iniciar para seguir jugando)
     */
    private void ganar() {
        ganado = true;
        numeroRecordar = null;
        numeroJugador = null;

        AlertDialog.Builder victoria = new AlertDialog.Builder(this);
        victoria.setTitle("¡HAS GANADO!");
        victoria.setCancelable(false); // Para que no se pueda cerrar clicando la pantalla.

        MediaPlayer mpVictoria = MediaPlayer.create(contexto, elegirAudio(VICTORIA));
        mpVictoria.start();

        victoria.setPositiveButton("Gracias :)", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "¡Felicidades!", Toast.LENGTH_LONG).show();
                mpVictoria.stop();
            }
        });
        victoria.setNeutralButton("Otra", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(contexto, MainActivity.class);
                startActivity(intent);
                finish();
                mpVictoria.stop();
            }
        });
        AlertDialog alert = victoria.create();
        alert.show();
        // En el texto del inferior de la pantalla:
        textoCifrasRecordar.setText("Has ganado :)");
    }

    /**
     * Lógica a seguir cuando se pierde.
     *
     * Reproduce un sonido y da la opción de intentarlo de nuevo o salir de la aplicación.
     */
    private void perder() {
        if(numeroRecordar.length() == numeroJugador.length()) {
            AlertDialog.Builder derrota = new AlertDialog.Builder(this);
            derrota.setTitle("Has perdido");
            derrota.setCancelable(false);

            MediaPlayer mpDerrota = MediaPlayer.create(contexto, elegirAudio(DERROTA));
            mpDerrota.start();

            derrota.setPositiveButton("Otra", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(contexto, MainActivity.class);
                    startActivity(intent);
                    finish();
                    mpDerrota.stop();
                }
            });

            derrota.setNeutralButton("Salir :(", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mpDerrota.release();
                    finish();
                }
            });

            AlertDialog alert = derrota.create();
            alert.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Se crea el menú principal (3 puntos)
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    public void seleccionarDificultad(View view) {
        registerForContextMenu(view);
        openContextMenu(view);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info) {
        // Se crea el submenú de dificultades
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
        Intent intent = new Intent(contexto, MainActivity.class);
        switch (item.getItemId()) {
            case R.id.facil:
                inicializar(TURNOS_FACIL);
                return true;
            case R.id.dificil:
                inicializar(TURNOS_DIFICIL);
                return true;
        }
        return true;
    }
}