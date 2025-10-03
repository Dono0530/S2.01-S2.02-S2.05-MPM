package Ihm.Ajout;

import exFinal.Controleur;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Panel permettant l'ajout d'une nouvelle tâche dans le diagramme MPM
 * Gère la saisie des informations : nom, durée, tâches précédentes et suivantes
 */
public class PanelAjout extends JPanel implements ActionListener
{

	/** Champ de saisie du nom de la tâche */
	private JTextField txtNom;
	
	/** Champ de saisie de la durée */
	private JTextField txtDure;
	
	/** Champ de saisie des tâches précédentes */
	private JTextField txtPrec;
	
	/** Champ de saisie des tâches suivantes */
	private JTextField txtSvt;

	/** Bouton de validation du formulaire */
	private JButton    btnSubmit;

	/** Référence vers le contrôleur */
	private Controleur ctrl;

	/**
	 * Constructeur du panel d'ajout
	 * @param ctrl Le contrôleur de l'application
	 */
	public PanelAjout(Controleur ctrl)
	{
		this.ctrl = ctrl;

		// Configuration de la disposition
		this.setLayout(new GridLayout(11,1));

		// Initialisation des composants
		this.initComposants();
		
		// Construction de l'interface
		this.construireInterface();
		
		// Ajout des écouteurs
		this.btnSubmit.addActionListener(this);
	}

	/**
	 * Initialise les composants du formulaire
	 */
	private void initComposants()
	{
		this.txtNom    = new JTextField(30);
		this.txtDure   = new JTextField(30);
		this.txtPrec   = new JTextField(30);
		this.txtSvt    = new JTextField(30);
		this.btnSubmit = new JButton("Valider");
	}

	/**
	 * Construit l'interface utilisateur du formulaire
	 */
	private void construireInterface()
	{
		// Titre du formulaire
		JPanel panelTemp = new JPanel();
		JLabel label     = new JLabel("Ajouter une Tache");

		label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 22));
		panelTemp.add(label);
		this.add(panelTemp);


		// Champ Nom
		this.add(new JLabel("   Nom de la Tache (30 caractère max) : "));
		panelTemp = new JPanel();
		panelTemp.add(this.txtNom);
		this.add(panelTemp);

		// Champ Précédents
		this.add(new JLabel("   Précedent (ex: A, B) : "));
		panelTemp = new JPanel();
		panelTemp.add(this.txtPrec);
		this.add(panelTemp);

		// Champ Suivants
		this.add(new JLabel("   Suivant   (ex: C, D) : "));
		panelTemp = new JPanel();
		panelTemp.add(this.txtSvt);
		this.add(panelTemp);

		// Champ Durée
		this.add(new JLabel("   Durée (en jour) : "));
		panelTemp = new JPanel();
		panelTemp.add(this.txtDure);
		this.add(panelTemp);

		// Bouton de validation
		panelTemp = new JPanel();
		panelTemp.add(this.btnSubmit);
		this.add(panelTemp);

		this.add(new JLabel());
	}

	/**
	 * Gère l'événement de validation du formulaire
	 * Vérifie les données saisies et ajoute la tâche si valides
	 * @param e L'événement déclenché
	 */
	public void actionPerformed(ActionEvent e) 
	{
		// Validation des données saisies
		if (this.ctrl.valeursValides(this.txtNom.getText(), this.txtDure.getText(), this.txtPrec.getText(), this.txtSvt.getText()))
		{
			// Ajout de la nouvelle tâche
			this.ctrl.ajouterTache(this.txtNom.getText(), this.txtPrec.getText(), this.txtSvt.getText(), Integer.parseInt(this.txtDure.getText()));

			this.ctrl.majIhm();
	
			// Fermeture de la fenêtre
			Window fenetre = SwingUtilities.getWindowAncestor(this);
			if (fenetre != null) 
				fenetre.dispose();
		}
		else
		{
			// Affichage du message d'erreur
			JOptionPane.showMessageDialog(null, this.ctrl.getErreur(), " Invalide", JOptionPane.ERROR_MESSAGE);
		}
	}
}