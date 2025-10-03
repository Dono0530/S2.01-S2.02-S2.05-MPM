package Metier;

import java.util.ArrayList;
import java.util.List;

public class CheminCritique
{
	/*---------------- */
	/*-----Attribut--- */
	/*---------------- */
	private List<Tache> tachesCritiques;    // Liste des tâches composant le chemin critique
	private int         dureeTotale;        // Durée totale du chemin critique

	/*---------------- */
	/*--Constructeur-- */
	/*---------------- */
	public CheminCritique()
	{
		this.tachesCritiques = new ArrayList<Tache>();
		this.dureeTotale     = 0;
	}

	// ========== MÉTHODES DE GESTION DES TÂCHES ==========

	/**
	 * Ajoute une tâche au chemin critique
	 * @param tache la tâche à ajouter (ne doit pas être null)
	 */
	public void ajouterTache(Tache tache)
	{
		if (tache != null)
		{
			this.tachesCritiques.add(tache);
		}
	}

	// ========== MÉTHODES D'ACCÈS AUX DONNÉES ==========

	/**
	 * Retourne la liste des tâches critiques
	 * @return une copie de la liste des tâches critiques
	 */
	public List<Tache> getTachesCritiques() { return new ArrayList<Tache>(this.tachesCritiques); }

	/**
	 * Définit la durée totale du chemin critique
	 * @param duree la durée totale en jours
	 */
	public void setDureeTotale(int duree)   { this.dureeTotale = duree;                     }

	/**
	 * Retourne la durée totale du chemin critique
	 * Si la durée n'a pas été définie manuellement, elle est calculée
	 * à partir de la date au plus tôt de la dernière tâche (si c'est "Fin")
	 * @return la durée totale en jours
	 */
	public int getDureeTotale()
	{
		// Si la durée a été définie manuellement
		if (this.dureeTotale > 0 ) return this.dureeTotale;

		// Calcul automatique à partir de la dernière tâche
		if (!tachesCritiques.isEmpty())
		{
			Tache derniereTache = tachesCritiques.get(tachesCritiques.size() - 1);

			if (derniereTache.getNom().equals("Fin"))
				return derniereTache.getDatePlusTot();
		}
		return 0;
	}

	// ========== MÉTHODES D'AFFICHAGE ==========
	/**
	 * Retourne une représentation textuelle du chemin critique
	 * Affiche la durée totale et la séquence des tâches avec leurs durées
	 * Les tâches "Debut" et "Fin" sont affichées sans durée
	 * @return chaîne de caractères représentant le chemin critique
	 */
	public String toString()
	{
		String res = "Chemin Critique (Durée: " + getDureeTotale() + " jours) : ";
		for (int i = 0; i < tachesCritiques.size(); i++)
		{
			Tache t = tachesCritiques.get(i);

			if (!t.getNom().equals("Debut") && !t.getNom().equals("Fin"))
			{
				res += t.getNom() + "(" + t.getDuree() + ")";
			}
			else
			{
				res += t.getNom();
			}

			if (i < tachesCritiques.size() - 1) res += " -> ";
		}
		return res;
	}
}