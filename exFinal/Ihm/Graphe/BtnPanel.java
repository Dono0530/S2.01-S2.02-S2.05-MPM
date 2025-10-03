package Ihm.Graphe;

import exFinal.Controleur;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Panel contenant les contrôles pour gérer le diagramme MPM
 * Permet de calculer les dates au plus tôt/tard, afficher le chemin critique
 * et définir les dates de début/fin du projet
 */
public class BtnPanel extends JPanel implements ActionListener 
{
	/** Panel pour la saisie des dates */
	private JPanel panelDte;
	
	/** Panel pour les boutons de calcul */
	private JPanel panelClcDte;

	/** Bouton pour calculer les dates au plus tôt */
	private JButton btnTot;
	
	/** Bouton pour calculer les dates au plus tard */
	private JButton btnTar;
	
	/** Bouton pour afficher le chemin critique */
	private JButton btnCrt;

	/** Bouton de validation de la date saisie */
	private JButton btnDteValider;

	/** Groupe de boutons radio pour le choix de date */
	private ButtonGroup btnGroupDte;
	
	/** Option pour définir la date de début */
	private JRadioButton rbDteDebut;
	
	/** Option pour définir la date de fin */
	private JRadioButton rbDteFin;

	/** Champ de saisie de la date */
	private JTextField txtDte;

	/** Référence vers le graphe MPM */
	private MPMGrapheAuto graphe;
	
	/** Référence vers le contrôleur */
	private Controleur ctrl;

	/**
	 * Constructeur du panel de contrôle
	 * @param graphe Le graphe MPM à contrôler
	 * @param ctrl  Le contrôleur de l'application
	 */
	public BtnPanel(MPMGrapheAuto graphe, Controleur ctrl) 
	{
		this.graphe = graphe;
		this.ctrl = ctrl;
		
		// Initialisation des composants
		this.initComposants();
		
		// Configuration de la disposition
		this.configurerLayout();
		
		// Ajout des écouteurs d'événements
		this.ajouterEcouteurs();
	}

	/**
	 * Initialise les composants du panel
	 */
	private void initComposants()
	{
		// Création des boutons et champs
		this.btnCrt        = new JButton("Chemin Critique");
		this.btnTar        = new JButton("Plus tard");
		this.btnTot        = new JButton("Plus tot");
		this.rbDteDebut    = new JRadioButton("Date de début");
		this.rbDteFin      = new JRadioButton("Date de fin");
		this.btnGroupDte   = new ButtonGroup();
		this.txtDte        = new JTextField();
		this.btnDteValider = new JButton("Valider Date");
		
		// Configuration initiale des états
		this.rbDteDebut.setSelected(false);
		this.rbDteFin  .setSelected(false);

		this.btnDteValider.setEnabled(false);
		this.txtDte       .setEnabled(false);
		this.btnTar       .setEnabled(false);
		this.btnCrt       .setEnabled(false);
		
		this.txtDte.setToolTipText("Entrez une date au format jj/mm/aaaa");
	}

	/**
	 * Configure la disposition des composants
	 */
	private void configurerLayout()
	{
		this.panelDte    = new JPanel();
		this.panelClcDte = new JPanel();
		JPanel panelrb   = new JPanel(new GridLayout(1, 2, 10, 10));
		
		this            .setLayout(new GridLayout(1, 2));
		this.panelDte   .setLayout(new GridLayout(2, 1, 10, 10));
		this.panelClcDte.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

		this.btnGroupDte.add(this.rbDteDebut);
		this.btnGroupDte.add(this.rbDteFin);

		this.panelClcDte.add(this.btnTot);
		this.panelClcDte.add(this.btnTar);
		this.panelClcDte.add(this.btnCrt);

		panelrb.add(this.rbDteDebut);
		panelrb.add(this.rbDteFin);
		panelrb.add(this.btnDteValider);

		this.panelDte.add(panelrb);
		this.panelDte.add(this.txtDte);

		this.add(this.panelDte);
		this.add(this.panelClcDte);
	}

	/**
	 * Ajoute les écouteurs d'événements aux composants
	 */
	private void ajouterEcouteurs()
	{
		this.btnTot       .addActionListener(this);
		this.btnTar       .addActionListener(this);
		this.btnCrt       .addActionListener(this);
		this.rbDteDebut   .addActionListener(this);
		this.rbDteFin     .addActionListener(this);
		this.txtDte       .addActionListener(this);
		this.btnDteValider.addActionListener(this);
	}

	/**
	 * Valide la date saisie et effectue les actions nécessaires
	 */
	private void validerDate()
	{
		String date = this.txtDte.getText();
		if (this.ctrl.dateValide(date))
		{
			if (this.rbDteDebut.isSelected())
			{
				this.ctrl.setDateDebut(date, null);
			}
			else if (this.rbDteFin.isSelected())
			{
				this.ctrl.setDateDebut(null, date);
			}
			this.graphe.majIhm();
			this.txtDte.setText("");
			this.txtDte.setEnabled(false);
			this.btnDteValider.setEnabled(false);
			this.rbDteDebut.setSelected(false);
			this.rbDteFin.setSelected(false);
		}
		else
		{
			JOptionPane.showMessageDialog(this, this.ctrl.getErreur(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Gère les événements des boutons et champs
	 * @param e L'événement déclenché
	 */
	public void actionPerformed(ActionEvent e) 
	{
		// Gestion du calcul au plus tôt
		if (e.getSource() == this.btnTot)
		{
			 if (!graphe.AugmenterEtapePlusTotMax())
			{
				this.btnTot.setEnabled(false);
				this.btnTar.setEnabled(true);
			}
		}

		// Gestion du calcul au plus tard
		if (e.getSource() == this.btnTar)
		{
			if (!graphe.AugmenterEtapePlusTarMax())
			{
				this.btnTar.setEnabled(false);
				this.btnCrt.setEnabled(true);
			}
		}

		// Affichage du chemin critique
		if (e.getSource() == this.btnCrt)
			this.graphe.activerChemin();
		
		// Gestion de la saisie des dates
		this.gererSaisieDates(e);
	}

	/**
	 * Gère les événements liés à la saisie des dates
	 * @param e L'événement à traiter
	 */
	private void gererSaisieDates(ActionEvent e)
	{
		if (e.getSource() == this.rbDteDebut || e.getSource() == this.rbDteFin)
		{
			this.txtDte.setEnabled(true);
			this.txtDte.setText("");
			this.txtDte.requestFocus();
		}

		if (e.getSource() == this.txtDte)
		{
			this.btnDteValider.setEnabled(!this.txtDte.getText().isEmpty());
		}

		if (e.getSource() == this.btnDteValider)
		{
			this.validerDate();
		}
	}

	/**
	 * Réinitialise l'état des boutons de calcul
	 */
	public void resetBtn() 
	{
		this.btnTot.setEnabled(true);
		this.btnTar.setEnabled(false);
		this.btnCrt.setEnabled(false);
	}
}