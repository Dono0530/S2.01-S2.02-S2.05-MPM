package Ihm.NoeudInfo;

import Metier.Tache;
import exFinal.Controleur;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.*;

/**
 * Fenêtre modale affichant les informations détaillées d'un nœud (tâche).
 * Cette classe assure qu'une seule instance de la fenêtre soit ouverte à la fois.
 */
public class FrameNoeudInfo extends JFrame implements WindowListener
{
	/** Flag indiquant si une instance de la fenêtre est déjà ouverte */
	private static boolean isFrameOpen = false;
	
	/** Panel contenant les informations détaillées du nœud */
	private PanelNoeudInfo panelNoeudInfo;

	/**
	 * Constructeur de la fenêtre d'information
	 * Ne crée une nouvelle fenêtre que si aucune autre n'est déjà ouverte
	 * @param tache La tâche dont on veut afficher les informations
	 * @param ctrl  Le contrôleur de l'application
	 */
	public FrameNoeudInfo(Tache tache, Controleur ctrl)
	{
		if (FrameNoeudInfo.isFrameOpen) {return;}

		FrameNoeudInfo.isFrameOpen = true;

		this.setTitle("Informations du nœud");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Création et ajout du panel d'informations
		this.panelNoeudInfo = new PanelNoeudInfo(tache, ctrl);
		this.add(this.panelNoeudInfo);

		this.pack();

		this.addWindowListener(this);
		this.setVisible(true);
	}


	// Implémentation des méthodes de l'interface WindowListener

	/**
	 * Appelé lors de la fermeture de la fenêtre.
	 * Réinitialise le flag permettant l'ouverture d'une nouvelle fenêtre.
	 * @param e L'événement de fenêtre
	 */
	public void windowClosed(WindowEvent e) {FrameNoeudInfo.isFrameOpen = false;}

	// Méthodes de l'interface WindowListener non utilisées
	public void windowOpened(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}


}