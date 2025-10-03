package Ihm.NoeudInfo;

import Metier.Erreur;
import Metier.Tache;
import exFinal.Controleur;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Panel affichant les informations détaillées d'une tâche
 * Permet également la modification de la durée et la suppression de la tâche
 */
public class PanelNoeudInfo extends JPanel implements ActionListener
{
	/** Tâche dont les informations sont affichées */
	private Tache      tache;
	
	/** Référence vers le contrôleur de l'application */
	private Controleur ctrl;

	/** Bouton de suppression de la tâche */
	private JButton    btnSupr;
	
	/** Champ de texte pour modifier la durée de la tâche */
	private JTextField txtDure;

	/**
	 * Constructeur du panel d'informations
	 * @param tache La tâche à afficher
	 * @param ctrl  Le contrôleur de l'application
	 */
	public PanelNoeudInfo(Tache tache, Controleur ctrl)
	{
		this.tache = tache;
		this.ctrl  = ctrl;

		JPanel panelBas, panelInfo;
		String precedents = "";
		String suivants   = "";

		this.setLayout(new BorderLayout());

		// Construction de la liste des tâches précédentes
		if (tache.getPrecedents().isEmpty())
			precedents = "Aucun";
		else 
		{
			for (Tache t : tache.getPrecedents())
				precedents += t.getNom() + ", ";
			if (!precedents.isEmpty())
				precedents = precedents.substring(0, precedents.length() - 2);
		}

		// Construction de la liste des tâches suivantes
		if (tache.getSuivants().isEmpty())
			suivants = "Aucun";
		else 
		{
			for (Tache t : tache.getSuivants())
				suivants += t.getNom() + ", ";
			if (!suivants.isEmpty())
				suivants = suivants.substring(0, suivants.length() - 2);
		}

		// Création des panels
		panelInfo = new JPanel(new GridLayout(0, 2, 10, 5));
		panelBas  = new JPanel();

		this.txtDure = new JTextField(String.valueOf(tache.getDuree()));

		// Ajout des informations dans le panel principal
		panelInfo.add(new JLabel("Nom :", JLabel.RIGHT));
		panelInfo.add(new JLabel(tache.getNom(), JLabel.LEFT));

		panelInfo.add(new JLabel("Durée :", JLabel.RIGHT));
		this.txtDure.setEnabled(false);
		panelInfo.add(this.txtDure);

		panelInfo.add(new JLabel("Précédents :", JLabel.RIGHT));
		panelInfo.add(new JLabel(precedents, JLabel.LEFT));

		panelInfo.add(new JLabel("Suivants :", JLabel.RIGHT));
		panelInfo.add(new JLabel(suivants, JLabel.LEFT));

		panelInfo.add(new JLabel("Jour début (plus tôt) :", JLabel.RIGHT));
		panelInfo.add(new JLabel(tache.ajouterJours(ctrl.getDateDebut(), tache.getDatePlusTot())), JLabel.LEFT);

		panelInfo.add(new JLabel("Jour fin (plus tard) :", JLabel.RIGHT));
		panelInfo.add(new JLabel(tache.ajouterJours(ctrl.getDateDebut(), tache.getDatePlusTard())), JLabel.LEFT);

		this.add(panelInfo, BorderLayout.CENTER);

		// Activation des contrôles seulement si ce n'est pas une tâche début ou fin
		if (!tache.getNom().equals("Debut") && !tache.getNom().equals("Fin"))
		{
			this.txtDure.setEnabled(true);

			this.btnSupr = new JButton("Suprimer");
			panelBas.add(btnSupr);
			this.btnSupr.addActionListener(this);
		}

		this.add(panelBas, BorderLayout.SOUTH);
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		this.txtDure.addActionListener(this);
	}

	/**
	 * Gestion des événements du panel
	 * @param e L'événement déclenché
	 */
	public void actionPerformed(ActionEvent e)
	{
		// Gestion du bouton de suppression
		if (e.getSource() == this.btnSupr)
		{
			this.ctrl.supprimerTache(this.tache.getNom());
			this.ctrl.majIhm();

			// Fermeture de la fenêtre parente
			Window window = SwingUtilities.getWindowAncestor(this);
			if (window != null) 
			{
				window.dispose();
			}
		}

		// Gestion de la modification de la durée
		if (e.getSource() == this.txtDure)
		{
			try 
			{
				int val = Integer.parseInt(this.txtDure.getText());
				this.ctrl.setDure(val, tache);
			}
			catch (NumberFormatException ex)
			{
				JOptionPane.showMessageDialog(null, Erreur.FORMAT_INVALIDE.getMessage() , "Invalide", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}