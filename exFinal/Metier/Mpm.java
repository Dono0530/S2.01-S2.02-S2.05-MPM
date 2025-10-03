package Metier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.awt.Point;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Classe Mpm - Gestion des projets selon la méthode MPM (Méthode des Potentiels Métra)
 * Cette classe permet de gérer un projet avec ses tâches, leurs dépendances,
 * le calcul des chemins critiques et la gestion des dates.
 */
public class Mpm
{
	// ========== ATTRIBUTS ==========
	
	private ArrayList<CheminCritique> cheminsCritiques;    // Liste des chemins critiques du projet
	private ArrayList<Tache>          taches;              // Liste de toutes les tâches du projet
	private ArrayList<String>         nomsPositions;       // Noms des tâches avec positions sauvegardées
	private ArrayList<Point>          coordsPositions;     // Coordonnées des tâches sauvegardées

	private String dateDebut;                              // Date de début du projet
	private String nomFichier;                             // Nom du fichier de données
	private String msgErreur;                              // Message d'erreur pour la validation

	private boolean estPosition;                           // Indique si les positions sont gérées

	// ========== CONSTRUCTEURS ==========

	/**
	 * Constructeur par défaut
	 * Initialise un projet MPM vide sans fichier de données
	 */
	public Mpm()
	{
		this.cheminsCritiques = new ArrayList<>();
		this.taches           = new ArrayList<>();
		this.estPosition      = false;
	}

	/**
	 * Constructeur avec paramètres
	 * Initialise un projet MPM à partir d'un fichier de données
	 * 
	 * @param nomFichier Chemin vers le fichier de données du projet
	 * @param dateDebut Date de début du projet au format dd/MM/yyyy
	 * @param estPosition Indique si les positions des nœuds sont gérées
	 */
	public Mpm(String nomFichier, String dateDebut, boolean estPosition)
	{
		this.dateDebut        = dateDebut;
		this.nomFichier       = nomFichier;
		this.cheminsCritiques = new ArrayList<>();
		this.taches           = new ArrayList<>();

		this.nomsPositions    = new ArrayList<>();
		this.coordsPositions  = new ArrayList<>();

		this.estPosition      = estPosition;

		this.chargerFichier();
	}
	// ========== CLASSE INTERNE ==========

	/**
	 * Classe interne pour l'exploration des chemins critiques
	 * Représente un état lors du parcours en profondeur du graphe
	 */
	private static class EtatExploration
	{
		Tache            tacheActuelle;         // Tâche actuellement explorée
		ArrayList<Tache> cheminJusquaIci;       // Chemin parcouru jusqu'à cette tâche

		/**
		 * Constructeur de l'état d'exploration
		 * 
		 * @param tacheActuelle La tâche actuellement explorée
		 * @param cheminJusquaIci Le chemin parcouru jusqu'à cette tâche
		 */
		EtatExploration(Tache tacheActuelle, ArrayList<Tache> cheminJusquaIci)
		{
			this.tacheActuelle   = tacheActuelle;
			this.cheminJusquaIci = cheminJusquaIci;
		}
	}

	// ========== MÉTHODES D'ACCÈS ==========

	/**
	 * Retourne la liste des chemins critiques du projet
	 * 
	 * @return Liste des chemins critiques
	 */
	public ArrayList<CheminCritique> getCheminsCritiques() { return this.cheminsCritiques;}
	
	// ========== MÉTHODES DE CALCUL DES CHEMINS CRITIQUES ==========

	/**
	 * Crée et calcule tous les chemins critiques du projet
	 * Utilise un parcours en profondeur pour identifier toutes les séquences
	 * de tâches critiques menant de "Debut" à "Fin"
	 */
	private void creerCheminCritique()
	{
		this.cheminsCritiques.clear();
		Tache tacheDebut = chercherTacheParNom("Debut");

		if (tacheDebut == null || !tacheDebut.estCritique())
		{
			return;
		}

		// Initialisation de l'exploration avec la tâche de début
		ArrayList<EtatExploration> liste         = new ArrayList<>();
		ArrayList<Tache>           cheminInitial = new ArrayList<>();

		cheminInitial.add(tacheDebut                                    );
		liste        .add(new EtatExploration(tacheDebut, cheminInitial));

		// Parcours en profondeur pour trouver tous les chemins critiques
		while (!liste.isEmpty())
		{
			EtatExploration  etatCourant   = liste.remove(liste.size() - 1);
			Tache            tacheActuelle = etatCourant.tacheActuelle;
			ArrayList<Tache> cheminActuel  = etatCourant.cheminJusquaIci;

			if (tacheActuelle.getNom().equals("Fin"))
			{
				// Fin du chemin : créer un nouveau chemin critique
				CheminCritique cTemp = new CheminCritique();
				for (Tache t : cheminActuel)
				{
					cTemp.ajouterTache(t);
				}

				cTemp.setDureeTotale(tacheActuelle.getDatePlusTot());
				this.cheminsCritiques.add(cTemp);
			}
			else
			{
				// Continuer l'exploration avec les successeurs critiques
				List<Tache> succCritiques = new ArrayList<>();
				for (Tache successeur : tacheActuelle.getSuivants())
				{
					if (successeur.estCritique()) succCritiques.add(successeur);
				}

				// Ajouter les successeurs critiques à la pile d'exploration
				for (int i = succCritiques.size() - 1; i >= 0; i--)
				{
					Tache            successeur     = succCritiques.get(i);
					ArrayList<Tache> prochainChemin = new ArrayList<>(cheminActuel);

					prochainChemin.add(successeur);
					liste.add(new EtatExploration(successeur, prochainChemin));
				}
			}
		}
	}

	// ========== MÉTHODES DE RECHERCHE ==========

	/**
	 * Cherche une tâche dans la liste à partir de son nom
	 * 
	 * @param nom Le nom de la tâche à rechercher
	 * @return La tâche trouvée ou null si elle n'existe pas
	 */
	public Tache chercherTacheParNom(String nom)
	{
		for (Tache t : this.taches)
		{
			if (t.getNom().equals(nom))
			{
				return t;
			}
		}
		return null;
	}

	// ========== MÉTHODES DE CALCUL DES DATES ==========

	/**
	 * Calcule les dates au plus tôt et au plus tard pour chaque tâche du projet
	 * Cette méthode détermine également quelles tâches sont critiques
	 */
	public void calculerDates()
	{
		// ========== CALCUL DES DATES AU PLUS TÔT ==========
		for (Tache t : this.taches)
		{
			t.calculerDatePlusTot();
		}

		// ========== DÉTERMINATION DE LA FIN DU PROJET ==========
		int finProjet = 0;
		for (Tache t : this.taches)
		{
			if (t.getSuivants().isEmpty())
			{
				int finTache = t.getDatePlusTot() + t.getDuree();
				if (finTache > finProjet)
				{
					finProjet = finTache;
				}
			}
		}

		// ========== INITIALISATION DES TÂCHES FINALES ==========
		for (Tache t : this.taches)
		{
			if (t.getSuivants().isEmpty())
			{
				t.setDatePlusTard(finProjet - t.getDuree());
			}
		}

		// ========== CALCUL DES DATES AU PLUS TARD ==========
		for (int i = this.taches.size() - 1; i >= 0; i--)
		{
			Tache t = this.taches.get(i);
			if (!t.getSuivants().isEmpty())
			{
				t.calculerDatePlusTard();
			}
		}
	}
	// ========== MÉTHODES DE GESTION DES TÂCHES ==========

	/**
	 * Ajoute une nouvelle tâche au projet avec ses dépendances
	 * 
	 * @param nom Le nom de la tâche
	 * @param prc Liste des prédécesseurs (séparés par des virgules)
	 * @param svt Liste des suivants (séparés par des virgules)
	 * @param duree Durée de la tâche en jours
	 */
	public void ajouterTache(String nom, String prc, String svt, int duree)
	{
		Tache tNew = new Tache(nom, duree);

		// ========== GESTION DES PRÉDÉCESSEURS ==========
		if (prc != null && !prc.trim().isEmpty())
		{
			String[] precedents = prc.split(",");
			for (String nomPre : precedents)
			{
				nomPre = nomPre.trim();
				if (!nomPre.isEmpty())
				{
					Tache precedent = chercherTacheParNom(nomPre);
					if (precedent != null)
					{
						tNew.ajouterPrecedent(precedent);
					}
				}
			}
		}
		else
		{
			// Si pas de prédécesseurs spécifiés, connecter à "Debut"
			Tache Debut = chercherTacheParNom("Debut");
			tNew.ajouterPrecedent(Debut);
		}

		// ========== GESTION DES SUIVANTS ==========
		if (svt != null && !svt.trim().isEmpty())
		{
			String[] suivants = svt.split(",");
			for (String nomSvt : suivants)
			{
				nomSvt = nomSvt.trim();
				if (!nomSvt.isEmpty())
				{
					Tache suivant = chercherTacheParNom(nomSvt);
					if (suivant != null)
					{
						tNew.ajouterSuivant(suivant);
					}
				}
			}
		}
		else
		{
			// Si pas de suivants spécifiés, connecter à "Fin"
			Tache fin = chercherTacheParNom("Fin");
			tNew.ajouterSuivant(fin);
		}

		// ========== FINALISATION ==========
		this.taches.add(tNew);
		this.tri();

		// Recalculer les dates et chemins critiques
		this.calculerDates();
		this.creerCheminCritique();
	}

		public void supprimerTache(String nom) 
		{
			Tache t = chercherTacheParNom(nom);

			if (t == null) return;

			Tache tFin   = chercherTacheParNom("Fin");
			Tache tDebut = chercherTacheParNom("Debut");

			// Retirer la tâche des précédents et suivants de toutes les autres tâches
			for (Tache t1 : this.taches) {
				// Supprimer des précédents
				ArrayList<Tache> precedents = t1.getPrecedents();
				for (int i = precedents.size() - 1; i >= 0; i--) {
					if (precedents.get(i).getNom().equals(nom)) {
						precedents.remove(i);
					}
				}
				// Supprimer des suivants
				ArrayList<Tache> suivants = t1.getSuivants();
				for (int i = suivants.size() - 1; i >= 0; i--) {
					if (suivants.get(i).getNom().equals(nom)) {
						suivants.remove(i);
					}
				}
			}

			// Pour chaque tâche, si elle n'a plus de suivant et ce n'est pas "Fin" ou "Debut", on lui ajoute "Fin"
			for (Tache t1 : this.taches) {
				if (t1.getSuivants().isEmpty() && 
					!t1.getNom().equals("Fin") && 
					!t1.getNom().equals("Debut")) {
					t1.ajouterSuivant(tFin);
				}
			}

			// Pour chaque tâche, si elle n'a plus de précédent et ce n'est pas "Fin" ou "Debut", on lui ajoute "Debut"
			for (Tache t1 : this.taches) {
				if (t1.getPrecedents().isEmpty() && 
					!t1.getNom().equals("Fin") && 
					!t1.getNom().equals("Debut")) {
					t1.ajouterPrecedent(tDebut);
				}
			}

			// Supprimer la tâche elle-même de la liste principale
			this.taches.remove(t);


			// Calculer les dates au plus tôt et au plus tard
			this.calculerDates();

			//Creer le(s) chemin(s) critique(s)
			this.creerCheminCritique();
		}

	public void setDure(int val, Tache tache)
	{
		this.taches.get(this.taches.indexOf(tache)).setDuree(val);
		
		// Calculer les dates au plus tôt et au plus tard
		this.calculerDates();

		//Creer le(s) chemin(s) critique(s)
		this.creerCheminCritique();
	}
	
public boolean enregistrer(String infosNoeuds, String cheminAbsolu) 
	{
		boolean bRet;
		try
		{
			PrintWriter pw = new PrintWriter( new FileOutputStream(cheminAbsolu ) );

			pw.println ( infosNoeuds );

			bRet = true;

			pw.close();
		}
		catch (Exception e)
		{ 
			bRet = false;
			e.printStackTrace(); 
		}
		return bRet;
	}

	public ArrayList<Tache> getTaches() {return this.taches;}

	public boolean valeursValides(String nom, String duree, String ant, String Svt)
	{
		String[] tabAntecedents = ant.trim().split(",");
		String[] tabSuivants    = Svt.trim().split(",");

		this.msgErreur = "";

		// Vérification du nom
		if (nom == null || nom.trim().isEmpty()) 
		{
			this.msgErreur = Erreur.NON_SAISIE.getMessage();
			return false;
		}
		String nomTrim = nom.trim();
		if (nomTrim.contains("|") || nomTrim.contains(",")) 
		{
			this.msgErreur = Erreur.CHAR_NOM_INVALIDE.getMessage();
			return false;
		}
		for (Tache t : this.taches)
		{
			if (t.getNom().equals(nomTrim)) 
			{
				this.msgErreur = Erreur.DEJA_EXISTANT.getMessage();
				return false;
			}
		}
		
		if (nom.length() > 50) 
		{
			this.msgErreur = Erreur.NOM_TROP_LONG.getMessage();
			return false;
		}

		if (nom.equals("Debut") || nom.equals("Fin")) 
		{
			this.msgErreur = Erreur.NOM_RESERVE.formater(nom);
			return false;
		}

		// Vérification de la durée
		if (duree == null || duree.trim().isEmpty()) 
		{
			this.msgErreur = Erreur.DUREE_INVALIDE.getMessage();
			return false;
		}
		int duration;
		try 
		{
			duration = Integer.parseInt(duree.trim());
			if (duration <= 0) 
			{
				this.msgErreur = Erreur.DUREE_NEGATIF.getMessage();
				return false;
			}
		} 
		catch (NumberFormatException e) 
		{
			this.msgErreur = Erreur.DUREE_INT.getMessage();
			return false;
		}

		// Vérification des antécédents
		if (ant != null && !ant.trim().isEmpty()) 
		{
			for (String unAntecedent : tabAntecedents)
			{
				unAntecedent = unAntecedent.trim();

				if (unAntecedent.isEmpty()) continue;

				if (unAntecedent.equals("Fin")) 
				{
					this.msgErreur = Erreur.PRECEDENT_FIN.getMessage();
					return false;
				}

				if (unAntecedent.equals("Debut")) 
				{
					this.msgErreur = Erreur.PRECEDENT_DEBUT.getMessage();
					return false;
				}
				if (unAntecedent.equals(nomTrim)) 
				{
					Erreur.TACHE_DEPENDANCE_REFLEXIVE.formater(nomTrim);
					return false;
				}

				boolean trouve = false;
				for (Tache t : this.taches)
					if (t.getNom().equals(unAntecedent)) 
					{
						trouve = true;
						break;
					}
				if (!trouve) 
				{
					this.msgErreur = Erreur.PRECEDENT_NON_EXISTANT.formater(unAntecedent);
					return false;
				}
			}
		}

		// Vérification des suivants
		if (Svt != null && !Svt.trim().isEmpty()) 
		{
			for (String unSuivant : tabSuivants) 
			{
				unSuivant = unSuivant.trim();
				if (unSuivant.isEmpty()) continue;
				if (unSuivant.equals("Debut")) 
				{
					this.msgErreur = Erreur.SUIVANT_DEBUT.getMessage();
					return false;
				}
				if (unSuivant.equals("Fin")) 
				{
					this.msgErreur = Erreur.SUIVANT_FIN.getMessage();
					return false;
				}
				if (unSuivant.equals(nomTrim)) 
				{
					this.msgErreur = Erreur.TACHE_DEPENDANCE_REFLEXIVE.formater(nomTrim);
					return false;
				}
				// Vérifier que le suivant n'est pas aussi un antécédent
				if (ant != null && !ant.trim().isEmpty()) 
				{
					for (String unAntecedent : tabAntecedents) 
					{
						if (unSuivant.equals(unAntecedent.trim())) 
						{
							this.msgErreur = Erreur.SUIVANT_ET_PRECEDENT.formater(unSuivant);
							return false;
						}
					}
				}
				boolean trouve = false;
				for (Tache t : this.taches)
					if (t.getNom().equals(unSuivant)) 
					{
						trouve = true;
						break;
					}
				if (!trouve) 
				{
					this.msgErreur = Erreur.SUIVANT_INEXISTANT.formater(unSuivant);
					return false;
				}
			}
		}

		if (!ant.trim().isEmpty() && !Svt.trim().isEmpty()) 
		{
			
			// Verifier si on peut aller d'un suivant vers un antécédent
			for (String unAntecedent : tabAntecedents) 
			{
				for (String unSuivant : tabSuivants) 
				{
					if (cyclique(unSuivant.trim(), unAntecedent.trim())) 
					{
						this.msgErreur = Erreur.CYCLIQUE.formater( unSuivant.trim()) + unAntecedent.trim();
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * Vérifie s'il existe un chemin entre les deux taches
	 */
	private boolean cyclique(String depuis, String vers) 
	{
		
		Tache tacheDepuis = chercherTacheParNom(depuis);
		if (tacheDepuis == null) return false;
		
		ArrayList<String> aVisiter       = new ArrayList<String>();
		ArrayList<String> dejaParcourues = new ArrayList<String>();
		
		aVisiter.add(depuis);
		
		while (!aVisiter.isEmpty()) 
		{
			String tacheActuelle = aVisiter.remove(0);
						
			if (tacheActuelle.equals(vers)) return true;
			
			Tache t = chercherTacheParNom(tacheActuelle);
			if (t != null) 
			{
				for (Tache suivant : t.getSuivants()) 
				{
					if (!dejaParcourues.contains(suivant.getNom())) 
					{
						aVisiter.add(suivant.getNom());
					}
				}
			}
		}
		
		return false;
	}

	public String            getErreur()          { return this.msgErreur;      }

	public String            getDateDebut()       { return this.dateDebut;      }

	public ArrayList<String> getNomsPositions()   { return this.nomsPositions;  }

	public ArrayList<Point>  getCoordsPositions() { return this.coordsPositions;}

	public boolean           estPosition()        { return this.estPosition;    }

	public void chargerFichier()
	{
		List<String[]> lignesPourDependances = new ArrayList<>();

		this.taches.clear();
		this.cheminsCritiques.clear();
		this.nomsPositions.clear();
		this.coordsPositions.clear();

		try (Scanner sc = new Scanner(new File(this.nomFichier)))
		{
			while (sc.hasNextLine())
			{
				String ligneEnCours = sc.nextLine().trim();
				
				if (!ligneEnCours.isEmpty()) 
				{
					String[] parts = ligneEnCours.split("\\|");

					if (parts.length >= 2)
					{
						String nomTache = parts[0].trim();
						
						if (this.estPosition && (nomTache.equals("Debut") || nomTache.equals("Fin"))) 
						{
							if (parts.length >= 3 && parts[2].matches("\\d+,\\d+")) 
							{
								String[] coords = parts[2].split(",");
								this.nomsPositions.add(nomTache);
								this.coordsPositions.add(new Point(Integer.parseInt(coords[0].trim()), 
																Integer.parseInt(coords[1].trim())));
							}
						}
						else 
						{
							// Traitement standard des tâches
							int duree = Integer.parseInt(parts[1].trim());
							Tache t = new Tache(nomTache, duree);
							this.taches.add(t);
							lignesPourDependances.add(parts);

							// Stockage des positions pour tâches normales (mode position)
							if (this.estPosition && parts.length == 4 && parts[3].matches("\\d+,\\d+")) 
							{
								String[] coords = parts[3].split(",");
								this.nomsPositions.add(nomTache);
								this.coordsPositions.add(new Point(Integer.parseInt(coords[0].trim()), 
																Integer.parseInt(coords[1].trim())));
							}
						}
					}
				}
			}

			// Ajout des tâches système
			this.taches.add(0, new Tache("Debut", 0));
			this.taches.add(new Tache("Fin", 0));

			// Établissement des dépendances
			for (String[] lig : lignesPourDependances)
			{
				if (lig.length > 2 && !lig[2].trim().isEmpty()) 
				{
					Tache courant = chercherTacheParNom(lig[0].trim());
					if (courant != null) 
					{
						for (String nomPre : lig[2].trim().split(",")) 
						{
							Tache precedent = chercherTacheParNom(nomPre.trim());
							if (precedent != null) {
								courant.ajouterPrecedent(precedent);
							}
						}
					}
				}
			}

			// Connexion automatique des tâches orphelines
			for (Tache t : this.taches)
			{
				if (t.getPrecedents().isEmpty() && !t.getNom().equals("Fin") && !t.getNom().equals("Debut")) {
					t.ajouterPrecedent(chercherTacheParNom("Debut"));
				}
				if (t.getSuivants().isEmpty() && !t.getNom().equals("Fin") && !t.getNom().equals("Debut")) {
					t.ajouterSuivant(chercherTacheParNom("Fin"));
				}
			}
			
			this.tri();
			this.calculerDates();
			this.creerCheminCritique();
		}
		catch (Exception e) 
		{
			this.msgErreur = Erreur.LECTURE_FICHIER_ERREUR.formater(e.getMessage());
			e.printStackTrace();
		}
		
	}

	public static String determineFichier(String cheminFichier) 
	{

		File fichier = new File(cheminFichier);
		
		if (!fichier.canRead()) 
		{
			System.err.println("Fichier illisible: " + cheminFichier);
			return null;
		}

		int maxChamps = 0;


		try (Scanner sc = new Scanner(fichier)) {

			// 4. On parcourt TOUT le fichier pour trouver le nombre maximum de champs
			while (sc.hasNextLine()) 
			{
				String ligne = sc.nextLine().trim();
				if (ligne.isEmpty()) 
				{
					continue;
				}

				
				String[] parts = ligne.split("\\|");
				int champsActuels = parts.length;

				
				if (champsActuels > maxChamps) 
				{
					maxChamps = champsActuels;
				}
			}

		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return null;
		}

		if (maxChamps == 4){return "pos";} 
		else if (maxChamps == 2 || maxChamps == 3) {return "base";} 
		else {return null;}
	}

	public void tri()
	{
		HashMap<Tache, Integer> dicTaches = new HashMap<>();
		
		// Première étape : calculer les niveaux (indices) de chaque tâche
		while(dicTaches.size() != taches.size())
		{
			for (Tache t : taches)
			{
				// Si la tâche n'est pas encore dans le dictionnaire
				if (!dicTaches.containsKey(t))
				{
					int colPlusGrd = -1;
					
					if (t.getPrecedents().isEmpty())
					{
						dicTaches.put(t, 0);
					}
					else
					{
						// Vérifier si tous les précédents ont déjà un niveau assigné
						boolean tousPrecsTraites = true;
						for (Tache pred : t.getPrecedents())
						{
							if (!dicTaches.containsKey(pred))
							{
								tousPrecsTraites = false;
								break;
							}
							
							int niveauPred = dicTaches.get(pred);
							if (niveauPred + 1 > colPlusGrd)
								colPlusGrd = niveauPred + 1;
						}
						
						// Ne l'ajouter que si tous ses précédents sont traités
						if (tousPrecsTraites && colPlusGrd != -1)
						{
							dicTaches.put(t, colPlusGrd);
						}
					}
				}
			}
		}
		
		// Deuxième étape : trier les tâches par leur niveau
		ArrayList<Tache> tachesTriees = new ArrayList<>();
		
		// Trouver le niveau maximum
		int niveauMax = 0;
		for (int niveau : dicTaches.values())
		{
			if (niveau > niveauMax)
				niveauMax = niveau;
		}
		
		// Ajouter les tâches niveau par niveau (0, puis 1, puis 2, etc.)
		for (int niveau = 0; niveau <= niveauMax; niveau++)
		{
			for (Tache t : taches)
			{
				if (dicTaches.get(t) == niveau)
				{
					tachesTriees.add(t);
				}
			}
		}
		
		this.taches = tachesTriees;
	}

		public boolean dateValide(String date) 
	{
		if (date == null || date.trim().isEmpty()) 
		{
			this.msgErreur = Erreur.DATE_VIDE.getMessage();
			return false;
		}
		
		
		if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) 
		{
			this.msgErreur = Erreur.DATE_FORMAT_INVALIDE.getMessage();
			return false;
		}
		
		
		String[] parties = date   .split("/");
		int      jour    = Integer.parseInt(parties[0]);
		int      mois    = Integer.parseInt(parties[1]);
		int      annee   = Integer.parseInt(parties[2]);
		
		
		if (annee < 1900 || annee > 2100) 
		{
			this.msgErreur = Erreur.ANNEE_INVALIDE.getMessage();
			return false; 
		}
		
		if (mois < 1 || mois > 12) 
		{
			this.msgErreur = Erreur.MOIS_INVALIDE.getMessage();
			return false;
		}
		
		if (jour < 1) 
		{
			this.msgErreur = Erreur.JOUR_INVALIDE.formater(jour, mois);
			return false; 
		}
		
		int[] joursParMois = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		
		if (estAnneeBissextile(annee)) 
		{
			joursParMois[1] = 29; 
		}
		
		if (jour > joursParMois[mois - 1]) 
		{
			this.msgErreur = Erreur.JOUR_INVALIDE.formater(jour, mois);
			return false;
		}
		
		return true;
	}
	
	private boolean estAnneeBissextile(int annee) 
	{
		return (annee % 4 == 0) && (annee % 100 != 0 || annee % 400 == 0);
	}

	public void setDateDebut(String dateDebut, String dateFin)
	{

		if (dateDebut != null && dateDebut.matches("\\d{2}/\\d{2}/\\d{4}")) 
		{
			this.dateDebut = dateDebut;
		}
		if (dateFin !=null && dateFin.matches("\\d{2}/\\d{2}/\\d{4}")) 
		{
			
			Tache tFin = chercherTacheParNom("Fin");
			if (tFin != null) 
			{	
				this.dateDebut = Tache.ajouterJours(dateFin, - tFin.getDatePlusTard());
			}
		}

	}
		// ========== MÉTHODES D'AFFICHAGE ==========

	/**
	 * Retourne une représentation textuelle complète du projet MPM
	 * Affiche l'analyse MPM avec toutes les tâches et leurs informations
	 * 
	 * @return Chaîne de caractères représentant l'analyse MPM
	 */
	public String toString()
	{
		String texte = "=== ANALYSE MPM ===\n\n";
		
		for (Tache t : this.taches) 
		{
			texte += t.toString(dateDebut) + "\n";
		}
		return texte;
	}
}
