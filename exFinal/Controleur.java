package exFinal;

import Ihm.Graphe.FrameMpm;
import Metier.CheminCritique;
import Metier.Mpm;
import Metier.Tache;
import java.util.ArrayList;

public class Controleur
{
	/*---------------- */
	/*----Atributs---- */
	/*---------------- */
	private Mpm      metier;  // Métier
	private FrameMpm ihm;     // IHM
	private String   date;

	/*---------------- */
	/*--Constructeur-- */
	/*---------------- */
	public Controleur()
	{
		this.metier = new Mpm();
		this.ihm    = new FrameMpm(this);
	}

	// ========== MÉTHODES DE GESTION DU MÉTIER ==========

	/**
	 * Définit un nouveau métier à partir d'un fichier
	 * @param fichier le chemin vers le fichier de données
	 */

	public void setNouvMetier(String fichier)
	{
		String type = Mpm.determineFichier(fichier);
		if (type == null) 
		{
			System.out.println("Fichier non reconnu : " + fichier);
			return;
		}
		if (type.equals("base"))
			this.metier = new Mpm(fichier,this.date, false);

		else if (type.equals("pos"))
			this.metier = new Mpm(fichier, this.date, true);

		this.ihm.enableBtn();
		
		this.majIhm();

		ihm.getMpmGraphe().resetEtape();
		ihm.getBtnPanel ().resetBtn  ();

	}

	public void setDate(String date){this.date = date;}

	public Mpm getMpm() {return this.metier;}

	// ========== MÉTHODES D'ACCÈS AUX DONNÉES ==========

	/**
	 * Retourne la liste des tâches
	 * @return ArrayList des tâches
	 */
	public ArrayList<Tache> getTaches()                     { return metier.getTaches();               }

	/**
	 * Retourne la liste des chemins critiques
	 * @return ArrayList des chemins critiques
	 */
	public ArrayList<CheminCritique> getCheminCritiques( ) { return  this.metier.getCheminsCritiques();}

	/**
	 * Recherche une tâche par son nom
	 * @param nom le nom de la tâche à rechercher
	 * @return la tâche trouvée ou null
	 */
	public Tache chercherTacheParNom(String nom)           { return  metier.chercherTacheParNom(nom);  }

	/**
	 * Retourne la date de début du projet
	 * @return la date de début sous forme de chaîne
	 */
	public String getDateDebut()                           { return this.metier.getDateDebut();        }

	/**
	 * Retourne le message d'erreur s'il y en a un
	 * @return le message d'erreur
	 */
	public String getErreur()                              { return this.metier.getErreur();           }

	

	// ========== MÉTHODES DE GESTION DES TÂCHES ==========

	/**
	 * Valide les valeurs saisies pour une tâche
	 * @param nom nom de la tâche
	 * @param duree durée de la tâche
	 * @param ant tâches antérieures
	 * @param svt tâches suivantes
	 * @return true si les valeurs sont valides
	 */
	public boolean valeursValides(String nom, String duree, String ant, String svt)
	{
		return this.metier.valeursValides(nom, duree, ant, svt);
	}

	/**
	 * Ajoute une nouvelle tâche
	 * @param nom nom de la tâche
	 * @param prc tâches précédentes
	 * @param svt tâches suivantes
	 * @param duree durée de la tâche
	 */
	public void ajouterTache(String nom, String prc, String svt, int duree) {this.metier.ajouterTache(nom, prc, svt, duree);}

	/**
	 * Supprime une tâche
	 * @param nom nom de la tâche à supprimer
	 */
	public void supprimerTache(String nom)                                  {this.metier.supprimerTache(nom);               }

	/**
	 * Modifie la durée d'une tâche
	 * @param val nouvelle durée
	 * @param tache tâche à modifier
	 */
	public void setDure(int val, Tache tache)
	{
		this.metier.setDure(val, tache);
		this.majIhm();
	}

	public void setDateDebut(String dateDebut, String dateFin)
	{
		this.metier.setDateDebut(dateDebut, dateFin);
		this.majIhm();
	}

	public boolean dateValide(String date)
	{
		return this.metier.dateValide(date);
	}

	// ========== MÉTHODES DE GESTION DE L'IHM ==========

	/**
	 * Met à jour l'interface homme-machine
	 */
	public void majIhm()    {this.ihm.majIhm();   }

	/**
	 * Active le mode date dans l'IHM
	 */
	public void setEnDate() {this.ihm.setEnDate();}

	/**
	 * Ferme l'application
	 */
	public void dispose()   {this.ihm.dispose();  }

	// ========== MÉTHODES DE SAUVEGARDE ==========
	/**
	 * Enregistre les données dans un fichier
	 * @param cheminAbsolue chemin absolu du fichier de sauvegarde
	 * @return true si l'enregistrement a réussi
	 */
	public boolean enregistrer(String cheminAbsolue)
	{
		String infosNoeuds;
		infosNoeuds = this.ihm.getInfos();
		return this.metier.enregistrer(infosNoeuds, cheminAbsolue);
	}

	/*---------------- */
	/*------Main------ */
	/*---------------- */
	public static void main(String[] args)
	{
		Controleur ctrl = new Controleur();
	}
}