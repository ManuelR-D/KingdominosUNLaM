package SwingApp;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import reyes.Ficha;

public class PanelFicha extends JPanel {

	private static final long serialVersionUID = 5537172421677141208L;
	private Ficha ficha;
	static final int LARGO_CARTA = 160;
	static final int ALTO_CARTA = 80;
	static final int LARGO_FICHA = LARGO_CARTA / 2;
	static final int ALTO_FICHA = ALTO_CARTA;
	private static final int LARGO_CORONA = 22;
	private int x, y;
	private BufferedImage bufferFicha;
	double escala;

	public PanelFicha(Ficha f, int y, int x) {
		this.x = x;
		this.y = y;
		this.ficha = f;
		this.escala = 1;

		bufferFicha = getTexturaFicha(f);
	}

	public PanelFicha(Ficha f, int y, int x, double escala) {
		this.x = x;
		this.y = y;
		this.ficha = f;
		this.escala = escala;
		bufferFicha = getTexturaFicha(f);

		
	}

	private BufferedImage getTexturaFicha(Ficha f) {
		/*
		 * Nos traemos una copia de bufferCarta, puesto que vamos a dibujar las coronas.
		 * Si trabajaramos sobre la referencia directa de VentanaJueguito.bufferCarta,
		 * perderķamos la textura original. Esto genera un bug para los mazos
		 * personalizados que pueden reutilizar la misma textura con coronas distintas
		 */
		ColorModel cm = VentanaJueguito.bufferCarta.getColorModel();
		boolean isAlphaPremultiplied = VentanaJueguito.bufferCarta.isAlphaPremultiplied();
		WritableRaster raster = VentanaJueguito.bufferCarta.copyData(null);
		BufferedImage imagen = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		
		
		if (f == null)
			return VentanaJueguito.bufferVacio;
		else if (f.getId() <0) {
			int indice=f.getId();
			BufferedImage castillo = null;
			switch(indice) {
			case -1:
				castillo=VentanaJueguito.bufferCastilloAmarillo;
				break;
			case -2:
				castillo=VentanaJueguito.bufferCastilloAzul;
				break;
			case -3:
				castillo=VentanaJueguito.bufferCastilloRojo;
				break;
			case -4:
				castillo=VentanaJueguito.bufferCastilloVerde;
				break;
			}
			return castillo;
		} else {
			int idFicha = f.getId()-2;
			//System.out.println(idFicha);
			if(idFicha == 96)
				imagen = imagen.getSubimage((idFicha%16) * LARGO_FICHA, (idFicha/16-1) * (ALTO_FICHA), LARGO_FICHA, ALTO_FICHA);
			imagen = imagen.getSubimage((idFicha%16) * LARGO_FICHA, (idFicha/16) * (ALTO_FICHA), LARGO_FICHA, ALTO_FICHA);
			
			//imagen = getTexturaCarta(f.getId() / 2, f.getId() % 2 == 0);
		}
		
		Graphics2D g2d = (Graphics2D) imagen.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		if(ficha != null && ficha.getCantCoronas() > 0) {
			int cantidadCoronas = ficha.getCantCoronas();
			if(ficha.getId()%2 != 0)
				g2d.translate((LARGO_FICHA-LARGO_CORONA*(cantidadCoronas))*escala-7, 5);
			else
				g2d.translate(7, 5);
			AffineTransform scale = new AffineTransform();
			scale.scale(escala, escala);
			for(int i = 0; i < cantidadCoronas; i++) {
				//System.out.println(i);
				g2d.drawImage(VentanaJueguito.bufferCorona, scale, null);
				g2d.translate(LARGO_CORONA*escala, 0);
			}
		}
		return imagen;
	}

	public void fichaClickeada(int xMouse, int yMouse) {
		if (PanelTableroSeleccion.idCartaElegida == Integer.MIN_VALUE)
			return;
		VentanaJueguito.coordenadasElegidas[0] = x;
		VentanaJueguito.coordenadasElegidas[1] = y;
		VentanaJueguito.coordenadasElegidas[2] = xMouse;
		VentanaJueguito.coordenadasElegidas[3] = yMouse;
		VentanaJueguito.getLatchCartaElegida().countDown();
	}

	public Ficha getFicha() {
		return this.ficha;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.scale(escala, escala);
		if (ficha != null) {
			int rotacion = ficha.getRotacion() - 1;
			if (rotacion != 0) {
				affineTransform.rotate((rotacion) * Math.PI / 2);
				switch (rotacion) {
				case 1:
					affineTransform.translate(0, -bufferFicha.getWidth());
					break;
				case 2:
					affineTransform.translate(-bufferFicha.getHeight(), -bufferFicha.getWidth());
					break;
				case 3:
					affineTransform.translate(-bufferFicha.getHeight(), 0);
					break;

				default:
					break;
				}
			}
		}
		g2d.drawImage(bufferFicha, affineTransform, null);
	}
}
