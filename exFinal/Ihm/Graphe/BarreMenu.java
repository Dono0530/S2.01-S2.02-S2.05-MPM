package Ihm.Graphe;

import Ihm.Ajout.FrameAjout;
import Metier.Erreur;
import exFinal.Controleur;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

/**
 * Barre de menu de l'application MPM
 * Gère les fonctionnalités de fichier (ouvrir, sauvegarder, fermer)
 * et d'édition (ajout de tâches, basculement date/durée)
 */
public class BarreMenu extends JMenuBar implements ActionListener
{
	/** Menu item pour sauvegarder le projet */
	private JMenuItem menuiSauv;
	
	/** Menu item pour ajouter une tâche */
	private JMenuItem menuiAjou;
	
	/** Menu item pour basculer entre durée et date */
	private JMenuItem menuiDure;
	
	/** Menu item pour ouvrir un projet */
	private JMenuItem menuiOuvr;
	
	/** Menu item pour fermer l'application */
	private JMenuItem menuiFerm;

	/** Texte du bouton de basculement durée/date */
	private String    labelDT;

	/** Référence vers le contrôleur */
	private Controleur ctrl;
	
	/** Référence vers la fenêtre principale */
	private JFrame    frame;

	/**
	 * Constructeur de la barre de menu
	 * @param ctrl Le contrôleur de l'application
	 */
	public BarreMenu(Controleur ctrl)
	{
		this.ctrl = ctrl;
		this.labelDT = "Mettre en Date";
		
		// Création des menus principaux
		this.initMenus();
		
		// Ajout des écouteurs d'événements
		this.ajouterEcouteurs();
	}

	/**
	 * Initialise et configure les menus
	 */
	private void initMenus()
	{
		// Création des menus principaux
		JMenu menuFichier = new JMenu("Fichier");
		JMenu menuEdition = new JMenu("Edition");
		
		// Initialisation des items du menu Fichier
		this.menuiOuvr = new JMenuItem("Ouvrir");
		this.menuiSauv = new JMenuItem("Enregistrer");
		this.menuiSauv.setEnabled(false);
		this.menuiFerm = new JMenuItem("Fermer");

		// Initialisation des items du menu Edition
		this.menuiAjou = new JMenuItem("Ajouter");
		this.menuiAjou.setEnabled(false);
		this.menuiDure = new JMenuItem(this.labelDT);
		this.menuiDure.setEnabled(false);

		// Construction du menu Fichier
		menuFichier.add(this.menuiOuvr);
		menuFichier.add(this.menuiSauv);
		menuFichier.addSeparator();
		menuFichier.add(this.menuiFerm);

		// Construction du menu Edition
		menuEdition.add(this.menuiAjou);
		menuEdition.addSeparator();
		menuEdition.add(this.menuiDure);

		// Ajout des menus à la barre
		this.add(menuFichier);
		this.add(menuEdition);
	}

	private void ajouterEcouteurs()
	{
		this.menuiSauv.addActionListener(this);
		this.menuiOuvr.addActionListener(this);
		this.menuiDure.addActionListener(this);
		this.menuiAjou.addActionListener(this);
		this.menuiFerm.addActionListener(this);
	}

	private void gererOuvertureFichier()
	{
		JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
		JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
		dateSpinner.setEditor(dateEditor);

		int date = JOptionPane.showConfirmDialog(null, dateSpinner,
				"Sélectionnez une date", JOptionPane.OK_CANCEL_OPTION);

		if (date == JOptionPane.OK_OPTION)
		{
			Date selectedDate = (Date) dateSpinner.getValue();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String formattedDate = sdf.format(selectedDate);
			this.ctrl.setDate(formattedDate);
		}
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Ouvrir un graphe Mpm : ");
		fileChooser.setCurrentDirectory(new File("./test"));
		
		int result = fileChooser.showOpenDialog(this.getParent());

		if (result == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = fileChooser.getSelectedFile();
			//Vérifier s'il s'agit du bon format de fichier .data, .txt
			if (selectedFile.getName().endsWith(".data") || selectedFile.getName().endsWith(".txt")) 
			{
				this.ctrl.setNouvMetier(selectedFile.getAbsolutePath());

				this.activerMenus();

			}
			else
				JOptionPane.showMessageDialog(frame, Erreur.FORMAT_FICHIER_INVALIDE.getMessage(), "Format de fichier invalide", JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			System.out.println("Aucun fichier sélectionné");
		}
	}

	private void gererSauvegardeFichier()
	{
		JFileChooser dialogueEnregistrement = new JFileChooser();
		dialogueEnregistrement.setDialogTitle("Enregistrer les positions sous...");
		dialogueEnregistrement.setCurrentDirectory(new File("./enreg"));
	
		dialogueEnregistrement.setSelectedFile(new File("mon_projet_mpm.txt"));
	
		int choixUtilisateur = dialogueEnregistrement.showSaveDialog(this.getParent());
	
		if (choixUtilisateur == JFileChooser.APPROVE_OPTION) 
		{
			File fichierAEnregistrer = dialogueEnregistrement.getSelectedFile();
			String cheminAbsolu = fichierAEnregistrer.getAbsolutePath();
			
			if (this.ctrl.enregistrer(cheminAbsolu))
				JOptionPane.showMessageDialog(null, Erreur.ENREGISTREMENT_SUCCES.formater(fichierAEnregistrer.getName()) + cheminAbsolu, " Enregistrement", JOptionPane.PLAIN_MESSAGE);
			else
				JOptionPane.showMessageDialog(null, Erreur.ERREUR_ENREGISTREMENT.getMessage(), " Enregistrement", JOptionPane.ERROR_MESSAGE);
		} 
		else
		{
			// L'utilisateur a cliqué sur "Annuler" ou a fermé la fenêtre
			JOptionPane.showMessageDialog(null, Erreur.ENREGISTREMENT_ANULLER.getMessage(), "Enregistrement", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Gestion des événements des menus
	 * @param e L'événement déclenché
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Ajout d'une nouvelle tâche
		if (e.getSource() == this.menuiAjou)
		{
			new FrameAjout(this.ctrl);
		}

		// Ouverture d'un fichier
		if (e.getSource() == this.menuiOuvr)
		{
			this.gererOuvertureFichier();
		}

		// Sauvegarde du fichier
		if (e.getSource() == this.menuiSauv) 
		{
			this.gererSauvegardeFichier();
		}

		// Fermeture de l'application
		if (e.getSource() == this.menuiFerm)
		{
			this.ctrl.dispose();
		}

		// Basculement entre mode durée et date
		if (e.getSource() == this.menuiDure)
		{
			this.basculerModeDateDuree();
		}
	}

	/**
	 * Bascule entre l'affichage en durée et en date
	 */
	private void basculerModeDateDuree()
	{
		if (this.labelDT.equals("Mettre en Date"))
		{
			this.labelDT = "Mettre en Jour";
			this.menuiDure.setText(this.labelDT);
			this.ctrl.setEnDate();
			return;
		}

		if (this.labelDT.equals("Mettre en Jour"))
		{
			this.labelDT = "Mettre en Date";
			this.menuiDure.setText(this.labelDT);
			this.ctrl.setEnDate();
		}
	}

	/**
	 * Active les menus après le chargement d'un fichier
	 */
	private void activerMenus()
	{
		this.menuiSauv.setEnabled(true);
		this.menuiAjou.setEnabled(true);
		this.menuiDure.setEnabled(true);
	}
}