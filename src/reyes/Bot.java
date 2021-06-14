package reyes;

import java.util.List;
import java.util.Scanner;

import SwingApp.VentanaJueguito;

public class Bot extends Jugador {

	public Bot(String nombre, int tamTablero) {
		super(nombre, tamTablero);
		this.tablero = new TableroBot(tamTablero);
	}

	public Bot(String nombre, String contrasenia) {
		super(nombre, contrasenia);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void setTablero(int tamanioTablero) {
		this.tablero= new TableroBot(tamanioTablero);
	}

	@Override
	public int eligeCarta(List<Carta> cartasAElegir, VentanaJueguito ventana) {
		int puntajeMax = -1;
		int numeroElegido = -1;
		boolean noMostrarRegiones=false;
		
		for (int i = 0; i < cartasAElegir.size(); i++) {
			Carta carta = cartasAElegir.get(i);
			if (carta != null) {
				// si no la eligieron ya...
				insertaEnTablero(carta,null);
				int puntajeActual = tablero.puntajeTotal(noMostrarRegiones);
				if (puntajeMax < puntajeActual) {
					puntajeMax = puntajeActual;
					numeroElegido = i;
				}
				tablero.quitarCarta(carta);
				carta.setDefault();
			}
		}
		return numeroElegido;
	}

	@Override
	public void insertaEnTablero(Carta cartaElegida, VentanaJueguito ventana) {
		int x = -(tablero.getTamanio() - 1);
		int y = -(tablero.getTamanio() - 1);
		int rotaciones = 0;
		while (tablero.ponerCarta(cartaElegida, x, y,false) == false && y < tablero.getTamanio()) {
			// probamos todas las rotaciones posibles
			cartaElegida.rotarCarta();
			rotaciones++;
			if (rotaciones == 3) {
				rotaciones = 0;
				// si fallamos en todas las rotaciones, cambiamos de posicion
				x++;
				if (x == tablero.getTamanio() - 1) {
					x = -4;
					y++;
				}
				cartaElegida.rotarCarta();
			}
		}
		// System.out.println(this.nombre + "\n" + cartaElegida + "\n" + tablero);
	}

}
