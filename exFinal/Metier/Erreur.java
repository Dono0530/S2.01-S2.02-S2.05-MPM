package Metier;

/**
 * Énumération Erreur - Définit tous les types d'erreurs possibles dans l'application MPM
 */
public enum Erreur
{
	// ========== ERREURS DE FORMAT ET SAISIE ==========
	
	FORMAT_INVALIDE  ("Le format est invalide."),
	NON_SAISIE       ("Vous n'avez pas saisi de valeur."),
	DEJA_EXISTANT    ("Une tâche avec ce nom existe déjà."),
	CHAR_NOM_INVALIDE("Le nom de tâche ne peut pas contenir les caractères '|' ou ','."),
	NOM_TROP_LONG("Le nom de la tâche ne peut pas dépasser 50 caractères."),
	NOM_RESERVE("Le nom '%s' est réservé et ne peut pas être utilisé."),

	// ========== ERREURS DE DURÉE ==========
	
	DUREE_INVALIDE("La durée n'a pas été saisie correctement."),
	DUREE_NEGATIF ("La durée doit être strictement supérieure à 0."),
	DUREE_INT     ("La durée doit être un nombre entier."),

	// ========== ERREURS DE PRÉDÉCESSEURS ==========
	
	PRECEDENT_FIN             ("La tâche 'Fin' ne peut pas être un prédécesseur."),
	PRECEDENT_DEBUT           ("La tâche 'Debut' ne peut pas être un prédécesseur."),
	TACHE_DEPENDANCE_REFLEXIVE("La tâche '%s' ne peut pas dépendre d'elle-même."),
	PRECEDENT_NON_EXISTANT    ("Le precedent '%s' n'existe pas."),

	// ========== ERREURS DE SUIVANTS ==========
	
	SUIVANT_ET_PRECEDENT("Un suivant ne peut pas être aussi un prédécesseur : '%s'."),
	SUIVANT_FIN         ("La tâche 'Fin' ne doit pas être spécifiée comme suivant."),
	SUIVANT_INEXISTANT  ("Le suivant spécifié '%s' n'existe pas."),
	SUIVANT_DEBUT       ("La tâche 'Debut' ne peut pas être un suivant."),

	// ========== ERREURS DE STRUCTURE ==========
	
	CYCLIQUE("Boucle détecté de %s vers "),

	// ========== ERREURS DE FICHIER ==========
	
	FORMAT_FICHIER_INVALIDE("Veuillez sélectionner un fichier .data ou .txt"),
	ERREUR_ENREGISTREMENT("Une erreur s'est produite lors de l'enregistrement du fichier."),
	FICHIER_INTROUVABLE("Le fichier '%s' est introuvable."),
	FICHIER_VIDE("Le fichier est vide ou ne contient aucune tâche valide."),
	LECTURE_FICHIER_ERREUR("Erreur lors de la lecture du fichier : %s"),
	ECRITURE_FICHIER_ERREUR("Erreur lors de l'écriture du fichier : %s"),

	// ========== MESSAGES DE SUCCÈS ==========
	
	ENREGISTREMENT_SUCCES("Fichier '%s' enregistré avec succès \nChemin :"),
	ENREGISTREMENT_ANULLER("Enregistrement annulé par l'utilisateur."),

	// ========== MESSAGE DE DATES ==========
	DATE_FORMAT_INVALIDE("La date doit être au format dd/mm/yyyy."),
	DATE_INVALIDE("La date '%s' n'est pas valide."),
	DATE_VIDE("La date de début ne peut pas être vide."),
	ANNEE_INVALIDE("L'année doit être comprise entre 1900 et 2100."),
	MOIS_INVALIDE("Le mois doit être compris entre 1 et 12."),
	JOUR_INVALIDE("Le jour '%d' n'est pas valide pour le mois '%d'.");
	

	// ========== ATTRIBUT ==========
	
	private final String message;    // Message d'erreur associé à chaque énumération

	// ========== CONSTRUCTEUR ==========

	/**
	 * Constructeur de l'énumération
	 * @param message le message d'erreur à associer à cette énumération
	 */
	Erreur(String message)
	{
		this.message = message;
	}

	// ========== MÉTHODES D'ACCÈS ==========

	/**
	 * Retourne le message d'erreur
	 * @return le message d'erreur brut
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Formate le message d'erreur avec des paramètres
	 * Utilise String.format pour remplacer les placeholders (%s, %d, etc.)
	 * @param args les arguments à insérer dans le message
	 * @return le message formaté avec les paramètres
	 */
	public String formater(Object... args)
	{
		return String.format(message, args);
	}
}