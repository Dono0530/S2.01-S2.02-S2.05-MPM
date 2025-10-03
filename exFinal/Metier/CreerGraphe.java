package Metier;

import Ihm.Noeud;
import exFinal.Controleur;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CreerGraphe
{

	private Controleur  ctrl;

	private List<Noeud> noeuds;

	public CreerGraphe(Controleur ctrl, int boxSize,int nbCol, List<Noeud> noeuds) 
	{
		this.ctrl   = ctrl;
		this.noeuds = noeuds;

		int startX  = 5;
		int startY  = 375;
		int distX   = boxSize + 100;
		int distY   = boxSize;

		// Construction du dictionnaire colonne -> nœuds
		HashMap<Integer, ArrayList<Noeud>> dicColNoeud = new HashMap<>();

		for (int col = 0; col < nbCol; col++)
		{
			// Liste temporaire pour les nœuds de la colonne actuelle
			ArrayList<Noeud> ListNoeudDsCol = new ArrayList<>();

			for (Noeud n : noeuds) 
			{
				if (n.getCol() == col)
					ListNoeudDsCol.add(n);
			}

			// Stocker la liste dans le dictionnaire
			dicColNoeud.put(col, ListNoeudDsCol);	
		}

		// Calcul des lignes (lig) pour chaque nœud dans chaque colonne
		for (Integer col : dicColNoeud.keySet())
		{
			ArrayList<Noeud> lstCol       = dicColNoeud.get(col);
			ArrayList<Noeud> lstNGrpFait  = new ArrayList<>();

			// La première colonne est initialisée à 0 ligne pour le premier nœud
			if (col == 0)
			{
				lstCol.get(0).setLig(0);
			}
			else
			{
				// Tri des nœuds de la colonne
				ArrayList<Noeud> lstColOrga = triCol(col, dicColNoeud);
				int              cptSup     = 0;
				int              cptInf     = 0;

				for (Noeud n : lstColOrga)
				{
					ArrayList<Noeud> lstNSvtDePre       = new ArrayList<>();
					ArrayList<Noeud> lstNoeudPreActuel  = getPrecedents(n);

					// Obtenir les nœuds ayant les mêmes suivants
					for ( Noeud nPreActuel : lstNoeudPreActuel )
						lstNoeudPreActuel = getNoeudsMemeSvt(nPreActuel, lstNSvtDePre, lstNoeudPreActuel);

					// Créer une copie pour éviter ConcurrentModificationException
					ArrayList<Noeud> copieNoeudPreActuel = new ArrayList<>(lstNoeudPreActuel);
					
					// Supprimer les éléments de la même colonne (maintenant sur la copie)
					for ( Noeud nGrp : copieNoeudPreActuel )
					{
						if ( nGrp.getCol() != n.getCol() )
							lstNSvtDePre.remove(nGrp);
					}

					// Vérifie si le groupe est nouveau
					if ( estNewGrp(lstNSvtDePre, lstNGrpFait, col) )
					{
						cptSup = -1;

						for (Noeud nGrp : lstNSvtDePre)
						{
							if (nGrp.getCol() == col)
								cptInf ++;
						}

						cptInf = (int) Math.ceil(cptInf/2);
					}

					if (!lstNSvtDePre.isEmpty()) 
					{
						int    milieu       = 0;
						int    lig          = 0;
						int    emplacementN = lstNSvtDePre.indexOf(n);
						double tailleGrpDeN = lstNSvtDePre.size();

						// Cas où le groupe a une taille impaire
						if (lstNSvtDePre.size()%2 != 0)
						{
							if ( n == lstNSvtDePre.get((int)tailleGrpDeN/2))
							{
								if (n.getNbPre()%2 == 0)
									milieu += getMil(n);
								else
									milieu += getMilieu(getPrecedents(n));

								lig += milieu;
							}
							else
							{
								if (emplacementN >= Math.ceil(tailleGrpDeN/2))
								{
									lig += getMilieu(getPrecedents(n)) + (((emplacementN+1) - Math.ceil(tailleGrpDeN/2)) + ++cptSup +1);
								}
								else
								{
									lig += getMilieu(getPrecedents(n)) + (((emplacementN+1) - Math.ceil(tailleGrpDeN/2)) - cptInf--);
								}
							}
						}
						// Cas pair
						else
						{
							if (emplacementN >= tailleGrpDeN/2)
							{	
								lig += getMil(n) + ((emplacementN - (tailleGrpDeN/2)) + ++cptSup) + 1;
							}
							else
							{
								lig += getMil(n) + ((emplacementN - (tailleGrpDeN/2)) - --cptInf) ;
							}
						}

						lstNGrpFait.add(n);
						n.setLig(lig);
					}

				}
			}
		}

		// Affectation des coordonnées x/y pour affichage
		for (Noeud n : noeuds) 
		{
			int x = startX + n.getCol() * distX;
			int y = startY + n.getLig() * distY;

			n.setX(x);
			n.setY(y);
		}

	}

	// Vérifie si tous les nœuds de lstNGrp ne sont pas déjà traités (dans lstNGrpFait)
	public boolean estNewGrp(ArrayList<Noeud> lstNGrp, ArrayList<Noeud> lstNGrpFait, int col) 
	{
		for (Noeud n : lstNGrp) 
		{
			if (n.getCol() == col && lstNGrpFait.contains(n)) 
				return false;
		}
		return true;
	}

	// Trie les nœuds d'une colonne en alternant selon le nombre de précédents
	public ArrayList<Noeud> triCol(int colActuel, HashMap<Integer, ArrayList<Noeud>> dicColNoeud)
	{
		ArrayList<Noeud> noeudsActuels = dicColNoeud.get(colActuel);

		for (int i = 0; i < noeudsActuels.size() - 1; i++)
		{
			if (i % 2 == 0)
			{
				if (noeudsActuels.get(i).getNbPre() > noeudsActuels.get(i + 1).getNbPre())
					Collections.swap(noeudsActuels, i, i + 1);
			}
			else
			{
				if (noeudsActuels.get(i).getNbPre() < noeudsActuels.get(i + 1).getNbPre())
					Collections.swap(noeudsActuels, i, i + 1);
			}
		}

		return triCroissant(noeudsActuels);
	}

	// Trie les nœuds par ordre croissant selon leur ligne moyenne
	public ArrayList<Noeud> triCroissant(ArrayList<Noeud> noeuds)
	{
		for (int i = 0; i < noeuds.size() - 1; i++)
		{
			int milieu1 = getMil(noeuds.get(i));
			int milieu2 = getMil(noeuds.get(i + 1));

			if (milieu2 < milieu1)
				Collections.swap(noeuds, i, i + 1);
		}
		return noeuds;
	}

	// Calcule la ligne moyenne des précédents d’un nœud
	private int getMil(Noeud n)
	{
		int sumLig = 0;

		for (Noeud nPreActuel : getPrecedents(n))
			sumLig += nPreActuel.getLig();

		return sumLig / getPrecedents(n).size();
	}

	// Retourne la ligne du milieu des prédécesseurs triés
	private int getMilieu(ArrayList<Noeud> lstNoeudPreActuel)
	{
		if (lstNoeudPreActuel == null || lstNoeudPreActuel.isEmpty())
			return 0;

		ArrayList<Noeud> copieTriee = new ArrayList<>();

		for (Noeud n : lstNoeudPreActuel)
		{
			int i = 0;
			while (i < copieTriee.size() && n.getLig() > copieTriee.get(i).getLig()) 
			{
				i++;
			}
			copieTriee.add(i, n);
		}

		int indexMilieu = copieTriee.size() / 2;

		int i = 0;
		for (Noeud n : copieTriee) 
		{
			if (i == indexMilieu) 
			{
				return n.getLig();
			}
			i++;
		}

		return 0;
	}

	// Récupère les nœuds suivants d’un nœud
	private ArrayList<Noeud> getSuivants(Noeud n)
	{
		Tache  t                 = ctrl.chercherTacheParNom(n.getNom());
		ArrayList<Tache> lstTSvt = t.getSuivants();
		ArrayList<Noeud> lstNSvt = new ArrayList<>();

		for (Tache tSvt : lstTSvt) 
			lstNSvt.add(getNoeud(tSvt.getNom()));

		return lstNSvt;
	}

	// Récupère les nœuds précédents d’un nœud
	private ArrayList<Noeud> getPrecedents(Noeud n)
	{
		Tache t                  = ctrl.chercherTacheParNom(n.getNom());
		ArrayList<Tache> lstTPre = t.getPrecedents();
		ArrayList<Noeud> lstNPre = new ArrayList<>();

		for (Tache tSvt : lstTPre)
			lstNPre.add(getNoeud(tSvt.getNom()));

		return lstNPre;
	}

	// Retourne le nœud correspondant à un nom
	public Noeud getNoeud(String nom)
	{
		for (Noeud n : noeuds)
		{
			if (n.getNom().equals(nom))
				return n;
		}
		return null;
	}

	// Récupère les nœuds ayant les mêmes suivants que n, en tenant compte de lstPre
	public ArrayList<Noeud> getNoeudsMemeSvt(Noeud n, ArrayList<Noeud> lstNSvt, ArrayList<Noeud> lstPre)
	{
		ArrayList<Tache> lstTSvt = ctrl.chercherTacheParNom(n.getNom()).getSuivants();

		for (Tache tacheSvt : lstTSvt) 
		{
			Noeud noeudSvt = getNoeud(tacheSvt.getNom());

			ArrayList<Tache> tachesPrecedentes = ctrl.chercherTacheParNom(tacheSvt.getNom()).getPrecedents();
			ArrayList<Noeud> noeudsPrecedents  = new ArrayList<>();

			for (Tache t : tachesPrecedentes) 
				noeudsPrecedents.add(getNoeud(t.getNom()));

			// Vérifie si tous les nœuds de lstPre sont des prédécesseurs du suivant
			if (contientTous(noeudsPrecedents, lstPre)) 
			{
				if (!lstNSvt.contains(noeudSvt))
					lstNSvt.add(noeudSvt);
			}
		}

		return triCroissant(lstNSvt);
	}

	// Vérifie si tous les nœuds de contenu sont présents dans conteneur
	private boolean contientTous(ArrayList<Noeud> conteneur, ArrayList<Noeud> contenu)
	{
		for (Noeud n : contenu)
		{
			if (!conteneur.contains(n)) 
				return false;
		}
		return true;
	}

}
