package Ihm;

/**
 * Classe représentant un arc dans un graphe orienté et pondéré.
 * Un arc est défini par un sommet de départ, un sommet d'arrivée et un poids.
 */
public class Arc
{
	/** Nom du sommet de départ */
	private String from;
	
	/** Nom du sommet d'arrivée */
	private String to;
	
	/** Poids/coût de l'arc */
	private int poids;

	/**
	 * Constructeur de la classe Arc
	 * @param from  Le sommet de départ
	 * @param to    Le sommet d'arrivée
	 * @param poids Le poids de l'arc
	 */
	public Arc(String from, String to, int poids)
	{
		this.from = from;
		this.to = to;
		this.poids = poids;
	}

	/**
	 * @return Le sommet de départ de l'arc
	 */
	public String getFrom() {return this.from;}

	/**
	 * @return Le sommet d'arrivée de l'arc
	 */
	public String getTo()   {return this.to;}

	/**
	 * @return Le poids de l'arc
	 */
	public int getPoids()   {return this.poids;}
}