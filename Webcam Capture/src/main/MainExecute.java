package main;

import com.github.sarxos.webcam.*;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.peer.WindowPeer;
import java.io.*;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainExecute extends JFrame implements Runnable, WebcamListener, WindowListener, UncaughtExceptionHandler,
		ItemListener, WebcamDiscoveryListener {
	private static final long serialVersionUID = 1L;

	private Webcam webcam = null;
	private WebcamPanel panel = null;
	private WebcamPicker picker = null;

	public static void main(String[] args) throws IOException {
		/** How to display image from webcam in Swing panel (basic) */
		/*
		 * Webcam webcam = Webcam.getDefault();
		 * webcam.setViewSize(WebcamResolution.VGA.getSize());
		 * 
		 * WebcamPanel panel = new WebcamPanel(webcam);
		 * panel.setFPSDisplayed(true); panel.setDisplayDebugInfo(true);
		 * panel.setImageSizeDisplayed(true); panel.setMirrored(true);
		 * 
		 * JFrame window = new JFrame("Test webcam panel"); window.add(panel);
		 * window.setResizable(true);
		 * window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); window.pack();
		 * window.setVisible(true);
		 */
		/** How to display image from webcam in Swing panel (more advanced) */
		SwingUtilities.invokeLater(new MainExecute());
	}

	@Override
	public void webcamFound(WebcamDiscoveryEvent arg0) {
		if (picker != null) {
			picker.addItem(arg0.getWebcam());
		}
	}

	@Override
	public void webcamGone(WebcamDiscoveryEvent arg0) {
		if (picker != null) {
			picker.removeItem(arg0.getWebcam());
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getItem() != webcam) {
			if (webcam != null) {
				panel.stop();

				remove(panel);

				webcam.removeWebcamListener(this);
				webcam.close();

				webcam = (Webcam) e.getItem();
				webcam.setViewSize(WebcamResolution.VGA.getSize());
				webcam.addWebcamListener(this);

				System.out.println("Selected" + webcam.getName());

				panel = new WebcamPanel(webcam, false);
				panel.setFPSDisplayed(true);

				add(panel, BorderLayout.CENTER);
				pack();

				Thread t = new Thread() {
					@Override
					public void run() {
						panel.start();
					}
				};
				t.setName("Example - stoper");
				t.setDaemon(true);
				t.setUncaughtExceptionHandler(this);
				t.start();
			}
		}
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.err.println(String.format("Exception in thred", t.getName()));
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent e) {
		webcam.close();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		System.out.println("Webcam viewer resumed");
	}

	@Override
	public void windowIconified(WindowEvent e) {
		System.out.println("Webcam viewer paused");
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void webcamClosed(WebcamEvent arg0) {
		System.out.println("webcam closed");
	}

	@Override
	public void webcamDisposed(WebcamEvent arg0) {
		System.out.println("Webcam disposed!");
	}

	@Override
	public void webcamImageObtained(WebcamEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void webcamOpen(WebcamEvent arg0) {
		System.out.println("Webcam open");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Webcam.addDiscoveryListener(this);

		setTitle("Java Webcam Capture POC");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		addWindowListener(this);
		picker = new WebcamPicker();
		picker.addItemListener(this);

		webcam = picker.getSelectedWebcam();

		if (webcam == null) {
			System.out.println("NO webcam found");
			System.exit(1);
		}

		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.addWebcamListener(MainExecute.this);

		panel = new WebcamPanel(webcam, false);
		panel.setFPSDisplayed(true);

		add(picker, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);

		pack();
		setVisible(true);

		Thread t = new Thread() {
			@Override
			public void run() {
				panel.start();
			}
		};
		t.setName("example-starter");
		t.setDaemon(true);
		t.setUncaughtExceptionHandler(this);
		t.start();
	}
}