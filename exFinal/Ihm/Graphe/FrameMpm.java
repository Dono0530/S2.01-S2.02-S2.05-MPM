package Ihm.Graphe;

import exFinal.Controleur;
import java.awt.*;
import javax.swing.*;

/**
 * Fenêtre principale de l'application affichant le diagramme MPM
 * (Méthode des Potentiels Metra).
 * Contient le graphe, une barre de menu et un panel de boutons.
 */
public class FrameMpm extends JFrame 
{
	/** Référence vers le contrôleur de l'application */
	private Controleur ctrl;

	/** Panel contenant le graphe MPM */
	private MPMGrapheAuto graphPanel;
	
	/** Panel contenant les boutons de contrôle */
	private BtnPanel btnPanel;

	/**
	 * Constructeur de la fenêtre principale
	 * @param ctrl Le contrôleur de l'application
	 */
	public FrameMpm(Controleur ctrl) 
	{
		this.ctrl = ctrl;

		this.setTitle("Diagramme MPM");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000, 700);
		this.setLayout(new BorderLayout());

		// Création et configuration du panel de graphe avec scrolling
		this.graphPanel = new MPMGrapheAuto(ctrl);
		JScrollPane scrollFrame = new JScrollPane(this.graphPanel,
												JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
												JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		this.add(scrollFrame, BorderLayout.CENTER);

		// Ajout de la barre de menu
		JMenuBar menubMaBarre = new BarreMenu(this.ctrl);
		this.setJMenuBar(menubMaBarre);

		// Configuration du panel de boutons (initialement caché)
		this.btnPanel = new BtnPanel(this.graphPanel, this.ctrl);
		this.btnPanel.setVisible(false);
		this.add(this.btnPanel, BorderLayout.SOUTH);

		this.setVisible(true);
	}

	/**
	 * Met à jour l'affichage du graphe
	 */
	public void majIhm() {
		this.graphPanel.majIhm();
	}

	/**
	 * Active l'affichage du panel de boutons
	 */
	public void enableBtn() {
		this.btnPanel.setVisible(true);
	}

	/**
	 * Récupère les informations du graphe
	 * @return Les informations sous forme de chaîne
	 */
	public String getInfos()
	{
		return this.graphPanel.getInfos();
	}

	/**
	 * @return Le panel contenant le graphe MPM
	 */
	public MPMGrapheAuto getMpmGraphe() {
		return this.graphPanel;
	}

	/**
	 * @return Le panel contenant les boutons de contrôle
	 */
	public BtnPanel getBtnPanel() {
		return this.btnPanel;
	}

	/**
	 * Active le mode d'affichage en dates
	 */
	public void setEnDate() {
		this.graphPanel.setEnDate();
	}
}