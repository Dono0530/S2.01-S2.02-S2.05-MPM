package Metier;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Classe Tache - Représente une tâche dans un projet MPM
 * Une tâche possède un nom, une durée, des dates de planification
 * et des relations de dépendance avec d'autres tâches.
 */
public class Tache
{
	// ========== ATTRIBUTS ==========
	
	private String           nom;                               // Nom de la tâche

	private int              duree;                             // Durée de la tâche en jours
	private int              datePlusTot  = 0;                  // Date au plus tôt (en jours depuis le début)
	private int              datePlusTard = 0;                  // Date au plus tard (en jours depuis le début)

	private ArrayList<Tache> precedents   = new ArrayList<>();  // Liste des tâches prédécesseurs
	private ArrayList<Tache> suivants     = new ArrayList<>();  // Liste des tâches suivantes

	// ========== CONSTRUCTEUR ==========

	/**
	 * Constructeur d'une tâche
	 * 
	 * @param nom Le nom de la tâche
	 * @param duree La durée de la tâche en jours
	 */
	public Tache(String nom, int duree)
	{
		this.nom   = nom;
		this.duree = duree;
	}
	// ========== MÉTHODES D'ACCÈS (GETTERS) ==========

	/**
	 * Retourne le nom de la tâche
	 * 
	 * @return Le nom de la tâche
	 */
	public String getNom()      { return this.nom;       }

	/**
	 * Retourne la durée de la tâche
	 * 
	 * @return La durée en jours
	 */
	public int getDuree()       { return this.duree;     }

	/**
	 * Retourne la date au plus tôt de début de la tâche
	 * 
	 * @return La date au plus tôt (en jours depuis le début du projet)
	 */
	public int getDatePlusTot() { return this.datePlusTot;}

	/**
	 * Retourne la date au plus tard de début de la tâche
	 * 
	 * @return La date au plus tard (en jours depuis le début du projet)
	 */
	public int getDatePlusTard(){ return this.datePlusTard;}

	/**
	 * Retourne la liste des tâches prédécesseurs
	 * 
	 * @return Liste des tâches qui doivent être terminées avant celle-ci
	 */
	public ArrayList<Tache> getPrecedents() { return this.precedents; }

	/**
	 * Retourne la liste des tâches suivantes
	 * 
	 * @return Liste des tâches qui dépendent de celle-ci
	 */
	public ArrayList<Tache> getSuivants()   { return this.suivants;   }

	// ========== MÉTHODES DE MODIFICATION (SETTERS) ==========

	/**
	 * Modifie la durée de la tâche
	 * 
	 * @param duree La nouvelle durée en jours
	 */
	public void setDuree(int duree) { this.duree = duree; }
	// ========== MÉTHODES DE GESTION DES DÉPENDANCES ==========

	/**
	 * Ajoute une tâche prédécesseur (dépendance)
	 * Cette méthode établit une relation bidirectionnelle :
	 * - Cette tâche aura t comme prédécesseur
	 * - La tâche t aura cette tâche comme suivant
	 * 
	 * @param t La tâche prédécesseur à ajouter
	 */
	public void ajouterPrecedent(Tache t)
	{
		if (!this.precedents.contains(t))
		{
			this.precedents.add(t);
		}

		if (!t.suivants.contains(this))
		{
			t.suivants.add(this);
		}
	}

	/**
	 * Ajoute une tâche suivante
	 * Cette méthode établit une relation bidirectionnelle :
	 * - Cette tâche aura t comme suivant
	 * - La tâche t aura cette tâche comme prédécesseur
	 * 
	 * @param t La tâche suivante à ajouter
	 */
	public void ajouterSuivant(Tache t)
	{
		if (!this.suivants.contains(t))
		{
			this.suivants.add(t);
		}

		if (!t.precedents.contains(this))
		{
			t.precedents.add(this);
		}
	}

	// ========== MÉTHODES DE CALCUL DES DATES ==========

	/**
	 * Calcule la date au plus tôt à laquelle cette tâche peut commencer
	 * La date au plus tôt correspond au maximum des dates de fin
	 * de toutes les tâches prédécesseurs
	 */
	public void calculerDatePlusTot()
	{
		int max = 0;
		for (Tache t : this.precedents)
		{
			int finTache = t.getDatePlusTot() + t.getDuree();
			if (finTache > max)
			{
				max = finTache;
			}
		}
		this.datePlusTot = max;
	}

	/**
	 * Calcule la date au plus tard à laquelle cette tâche peut commencer
	 * sans retarder le projet. La date au plus tard correspond au minimum
	 * des dates au plus tard des tâches suivantes moins la durée de cette tâche
	 */
	public void calculerDatePlusTard()
	{
		int min = Integer.MAX_VALUE;

		for (Tache t : this.suivants)
		{
			int debutSuivant = t.getDatePlusTard() - this.duree;
			if (debutSuivant < min)
			{
				min = debutSuivant;
			}
		}

		// Si pas de suivants, on laisse la date actuelle
		if (!this.suivants.isEmpty())
		{
			this.datePlusTard = min;
		}
	}

	/**
	 * Définit manuellement la date au plus tard
	 * Utilisée principalement pour les tâches finales du projet
	 * 
	 * @param date La date au plus tard à fixer
	 */
	public void setDatePlusTard(int date) { this.datePlusTard = date; }

	// ========== MÉTHODES D'ANALYSE ==========

	/**
	 * Vérifie si la tâche est critique
	 * Une tâche est critique si elle n'a aucune marge,
	 * c'est-à-dire si sa date au plus tôt égale sa date au plus tard
	 * 
	 * @return true si la tâche est critique, false sinon
	 */
	public boolean estCritique() { return this.datePlusTot == this.datePlusTard; }
	// ========== MÉTHODES UTILITAIRES STATIQUES ==========

	/**
	 * Ajoute un nombre de jours à une date donnée
	 * Gère les formats de date dd/MM/yyyy et dd/MM
	 * 
	 * @param dateDebut La date de début au format dd/MM/yyyy ou dd/MM
	 * @param nbJours Le nombre de jours à ajouter (peut être négatif)
	 * @return La date résultante au format dd/MM
	 */
	public static String ajouterJours(String dateDebut, int nbJours)
	{
		try
		{
			SimpleDateFormat formatEntree;

			// Détecter le format d'entrée et ajuster si nécessaire
			if (dateDebut.matches("\\d{2}/\\d{2}/\\d{4}"))
			{
				// Format complet : dd/MM/yyyy
				formatEntree = new SimpleDateFormat("dd/MM/yyyy");
			}
			else if (dateDebut.matches("\\d{2}/\\d{2}"))
			{
				// Format court : dd/MM - ajouter l'année courante
				Calendar currentYear = Calendar.getInstance();
				dateDebut = dateDebut + "/" + currentYear.get(Calendar.YEAR);
				formatEntree = new SimpleDateFormat("dd/MM/yyyy");
			}
			else
			{
				System.err.println("Format de date non reconnu : " + dateDebut);
				return "??/??";
			}

			formatEntree.setLenient(false);
			Calendar cal = Calendar.getInstance();
			cal.setTime(formatEntree.parse(dateDebut));
			cal.add(Calendar.DAY_OF_MONTH, nbJours);

			return new SimpleDateFormat("dd/MM").format(cal.getTime());
		}
		catch (ParseException e)
		{
			System.err.println("Erreur de parsing pour la date : " + dateDebut);
			e.printStackTrace();
			return "??/??";
		}
	}
	// ========== MÉTHODES D'AFFICHAGE ==========

	/**
	 * Retourne une représentation textuelle complète de la tâche
	 * Affiche toutes les informations : nom, durée, dates, marge, dépendances
	 * 
	 * @param dateDebut La date de début du projet pour calculer les dates calendaires
	 * @return Chaîne de caractères représentant la tâche
	 */
	public String toString(String dateDebut)
	{
		// ========== TITRE ET DURÉE ==========
		String res = this.nom + " : " + this.duree + " jour" + (this.duree > 1 ? "s" : "") + "\n";

		// ========== CALCUL DES DATES AU FORMAT JJ/MM ==========
		String datePlusTotStr  = ajouterJours(dateDebut, this.datePlusTot );
		String datePlusTardStr = ajouterJours(dateDebut, this.datePlusTard);

		res += "date au plus tôt : "  + datePlusTotStr  + "\n";
		res += "date au plus tard : " + datePlusTardStr + "\n";

		// ========== CALCUL ET AFFICHAGE DE LA MARGE ==========
		int marge = this.datePlusTard - this.datePlusTot;

		res += "marge : " + marge + " jour" + (marge > 1 ? "s" : "") + "\n";

		// ========== LISTE DES PRÉDÉCESSEURS ==========
		if (this.precedents.isEmpty()) 
		{
			res += "pas de tâche précédente\n";
		} 
		else 
		{
			res += "liste des tâches précédentes :\n";

			for (Tache t : this.precedents) 
			{
				res += t.getNom() + "\n";
			}
		}

		// ========== LISTE DES SUIVANTS ==========
		if (this.suivants.isEmpty()) 
		{
			res += "pas de tâche suivante\n";
		} 
		else
		{
			res += "liste des tâches suivantes :\n";

			for (Tache t : this.suivants) 
			{
				res += t.getNom() + "\n";
			}
		}
		return res;
	}
}
