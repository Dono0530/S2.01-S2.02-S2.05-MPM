package Ihm;

import exFinal.Controleur;

/**
 * Classe représentant un nœud dans un graphe pour visualiser les tâches d'un projet.
 * Cette classe gère les informations de positionnement et les caractéristiques temporelles
 * d'une tâche dans l'interface graphique.
 */
public class Noeud
{
	/** Nom identifiant le nœud */
	private String nom;
	
	/** Date au plus tôt */
	private int tot;
	
	/** Date au plus tard */
	private int tard;
	
	/** Position X dans l'interface graphique */
	private int x;
	
	/** Position Y dans l'interface graphique */
	private int y;
	
	/** Numéro de colonne dans la grille */
	private int col;
	
	/** Numéro de ligne dans la grille */
	private int lig;
	
	/** Nombre de tâches précédentes */
	private int nbPre;
	
	/** Nombre de tâches suivantes */
	private int nbSvt;
	
	/** Position médiane pour le tracé */
	private int milieu;
	
	/** Indique si le nœud fait partie du chemin critique */
	private boolean estChemin;
	
	/** Référence vers le contrôleur de l'application */
	private Controleur ctrl;

	/**
	 * Constructeur de la classe Nœud
	 * @param nom       Nom identifiant le nœud
	 * @param tot       Date au plus tôt
	 * @param tard      Date au plus tard
	 * @param col       Numéro de colonne
	 * @param estChemin Indique si le nœud est sur le chemin critique
	 * @param ctrl      Référence vers le contrôleur
	 */
	public Noeud(String nom, int tot, int tard, int col, boolean estChemin, Controleur ctrl)
	{
		this.ctrl      = ctrl;
		this.nom       = nom;
		this.tot       = tot;
		this.tard      = tard;
		this.col       = col;
		this.estChemin = estChemin;
		this.nbPre     = ctrl.chercherTacheParNom(this.getNom()).getPrecedents().size();
		this.nbSvt     = ctrl.chercherTacheParNom(this.getNom()).getSuivants().size();
	}

	// Getters
	/** @return Position X du nœud */
	public int              getX()         {return         x;}
	/** @return Position Y du nœud */
	public int              getY()         {return         y;}
	/** @return Date au plus tôt */
	public int              getTot()       {return       tot;}
	/** @return Date au plus tard */
	public int              getTard()      {return      tard;}
	/** @return Numéro de colonne */
	public int              getCol()       {return       col;}
	/** @return Numéro de ligne */
	public int              getLig()       {return       lig;}
	/** @return Nom du nœud */
	public String           getNom()       {return       nom;}
	/** @return true si le nœud est sur le chemin critique */
	public boolean          getEstChemin() {return estChemin;}
	/** @return Nombre de tâches précédentes */
	public int              getNbPre()     {return     nbPre;}
	/** @return Nombre de tâches suivantes */
	public int              getNbSvt()     {return     nbSvt;}
	/** @return Position médiane */
	public int              getMil()       {return    milieu;}

	// Setters
	/** @param x Nouvelle position X */
	public void setX        (int x)             {this.x         =         x;}
	/** @param y Nouvelle position Y */
	public void setY        (int y)             {this.y         =         y;}
	/** @param lig Nouveau numéro de ligne */
	public void setLig      (int lig)           {this.lig       =       lig;}
	/** @param milieu Nouvelle position médiane */
	public void setMil      (int milieu)        {this.milieu    =    milieu;}
	/** @param estChemin Nouvel état du chemin critique */
	public void setEstChemin(boolean estChemin) {this.estChemin = estChemin;}
}