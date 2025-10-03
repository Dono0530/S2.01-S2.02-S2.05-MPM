package Ihm.Ajout;

import exFinal.Controleur;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.*;

/**
 * Fenêtre modale pour l'ajout d'une nouvelle tâche
 * Implémente un système de singleton pour empêcher l'ouverture
 * de plusieurs fenêtres d'ajout simultanément
 */
public class FrameAjout extends JFrame implements WindowListener {
	
	/** 
	 * Indique si une instance de la fenêtre est déjà ouverte 
	 * Utilisé pour implémenter le pattern Singleton
	 */
	private static boolean isFrameOpen = false;

	/** Panel contenant le formulaire d'ajout */
	private PanelAjout panelAjout;

	/**
	 * Constructeur de la fenêtre d'ajout
	 * Ne crée une nouvelle fenêtre que si aucune n'est déjà ouverte
	 * @param ctrl Le contrôleur de l'application
	 */
	public FrameAjout(Controleur ctrl)
	{
		// Vérifie qu'aucune autre fenêtre d'ajout n'est ouverte
		if (FrameAjout.isFrameOpen) {return;}

		// Marque la fenêtre comme ouverte
		FrameAjout.isFrameOpen = true;

		// Configuration de la fenêtre
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(400, 400);
		this.setTitle("Ajouter");

		// Ajout du panel de formulaire
		this.panelAjout = new PanelAjout(ctrl);
		this.add(this.panelAjout);

		// Configuration des événements de fenêtre
		this.addWindowListener(this);
		this.setVisible(true);
	}

	/**
	 * Appelé lors de la fermeture de la fenêtre
	 * Réinitialise le flag permettant l'ouverture d'une nouvelle fenêtre
	 * @param e L'événement de fermeture
	 */
	public void windowClosed(WindowEvent e) {FrameAjout.isFrameOpen = false;}

	// Méthodes de l'interface WindowListener non utilisées
	public void windowOpened(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
}