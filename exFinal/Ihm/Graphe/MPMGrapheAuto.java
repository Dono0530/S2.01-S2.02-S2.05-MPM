package Ihm.Graphe;

import Ihm.Arc;
import Ihm.Noeud;
import Ihm.NoeudInfo.*;
import Metier.*;
import exFinal.Controleur;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.*;

public class MPMGrapheAuto extends JPanel
{
	// ========== ATTRIBUTS DE VISUALISATION ==========
	private final List<Noeud>         noeuds  = new ArrayList<>();        // Liste des nœuds du graphe
	private final List<Arc>           arcs    = new ArrayList<>();        // Liste des arcs du graphe
	private final int                 boxSize = 80;                       // Taille des boîtes de nœuds


	private boolean                   cheminActif      = false;              // Indique si l'affichage du chemin critique est activé

	private ArrayList<CheminCritique> cheminsCritiques = new ArrayList<>();  // Liste des chemins critiques du projet

	private Controleur                ctrl;                                  // Référence vers le contrôleur principal
	private CreerGraphe               creerGraphe;                           // Générateur de positions des nœuds

	// ========== ATTRIBUTS DE GLISSER-DÉPOSER ==========
	
	private Noeud                     noeudSelectionne  = null;              // Nœud actuellement sélectionné pour le déplacement
	private int                       offsetX, offsetY;                      // Décalage pour le glisser-déposer fluide

	// ========== ATTRIBUTS DE GESTION DES ÉTAPES ==========
	
	private int                       etapePlusTotMax;                       // Étape maximale pour l'affichage des dates au plus tôt
	private int                       etapePlusTarMax;                       // Étape maximale pour l'affichage des dates au plus tard
	private int                       nbCol;                                 // Nombre de colonnes dans le graphe

	private boolean                   enDate;                                // Mode d'affichage : dates calendaires ou numéros d'étapes

	// ========== CONSTRUCTEUR ==========

	/**
	 * Constructeur principal prenant un contrôleur
	 * Initialise le graphe MPM avec les données du contrôleur
	 * et configure les gestionnaires d'événements pour l'interaction
	 *
	 * @param ctrl Le contrôleur qui fournit les tâches et les données du projet
	 */
	public MPMGrapheAuto(Controleur ctrl)
	{
		this.nbCol           =  0;
		this.etapePlusTarMax = -1;
		this.etapePlusTotMax =  1;

		this.enDate          =  false;

		this.ctrl = ctrl;
		this.cheminsCritiques = this.ctrl.getCheminCritiques();

		this.majIhm();
		this.resetEtape();

		// Ajoute des listeners pour la gestion des événements souris
		GereSouris gereSouris = new GereSouris();

		this.addMouseListener      (gereSouris);
		this.addMouseMotionListener(gereSouris);
	}

	// ========== MÉTHODES DE RECHERCHE ET POSITIONNEMENT ==========

	/**
	 * Retourne le nœud situé à la position donnée, ou null si aucun nœud n'est présent
	 * 
	 * @param x Coordonnée X de la position
	 * @param y Coordonnée Y de la position
	 * @return Le nœud trouvé ou null
	 */
	private Noeud getNoeudAPosition(int x, int y)
	{
		for (Noeud n : noeuds)
		{
			if (x >= n.getX() && x <= n.getX() + boxSize &&
			    y >= n.getY() && y <= n.getY() + boxSize   )
				return n;
		}
		return null;
	}

	// ========== MÉTHODES DE MISE À JOUR DE L'INTERFACE ==========

	/**
	 * Met à jour l'interface graphique en recalculant les positions des nœuds
	 * et en synchronisant avec les données du contrôleur
	 */
	public void majIhm()
	{
		int ancienNbCol;
		int nouveauNbCol;

		ancienNbCol           = this.getNbCol();

		this.cheminsCritiques = this.ctrl.getCheminCritiques();

		this.initialiserNoeudsArcs();

		nouveauNbCol          = this.getNbCol();

		if (nouveauNbCol > ancienNbCol)
		{
			this.augmenteEtapePlusTard();
		}

		this.creerGraphe = new CreerGraphe(this.ctrl, this.boxSize, this.nbCol, new ArrayList<>(this.noeuds));

		if (this.ctrl.getMpm().estPosition())
			this.setNoeudsPosition();

		if (this.etapePlusTotMax == this.nbCol)
			this.etapePlusTotMax = this.nbCol;

		this.invalidate();
		this.revalidate();
		this.repaint();

		Container parent = getParent();

		if (parent instanceof JViewport)
			parent.getParent().revalidate();
	}

	/**
	 * Applique les positions sauvegardées aux nœuds du graphe
	 * Utilisée quand les positions sont chargées depuis un fichier
	 */
	private void setNoeudsPosition()
	{
		ArrayList<String> nomsPositions = this.ctrl.getMpm().getNomsPositions();
		ArrayList<Point> coordsPositions = this.ctrl.getMpm().getCoordsPositions();

		// Vérifier que les deux listes ont la même taille
		if (nomsPositions.size() != coordsPositions.size())
		{
			System.err.println("Erreur: les listes de noms et coordonnées n'ont pas la même taille");
			return;
		}

		// Parcourir toutes les positions enregistrées
		for (int i = 0; i < nomsPositions.size(); i++)
		{
			String nomTache   = nomsPositions  .get(i);
			Point coordonnees = coordsPositions.get(i);

			// Chercher le nœud correspondant à cette tâche
			Noeud noeudCible  = this.creerGraphe.getNoeud(nomTache);

			if (noeudCible != null)
			{
				// Appliquer les coordonnées du fichier
				noeudCible.setX(coordonnees.x);
				noeudCible.setY(coordonnees.y);
			}
			else
			{
				System.err.println("Nœud non trouvé pour la tâche: " + nomTache);
			}
		}

		this.revalidate();
	}

	/**
	 * Calcule la taille préférée du panel selon la position des nœuds
	 * 
	 * @return Les dimensions préférées du panel
	 */
	public Dimension getPreferredSize()
	{
		int maxX = 100;
		int maxY = 100;

		for (Noeud n : noeuds)
		{
			maxX = Math.max(maxX, n.getX() + boxSize + 50);
			maxY = Math.max(maxY, n.getY() + boxSize + 50);
		}

		if (noeuds.isEmpty())
		{
			maxX = 800;
			maxY = 600;
		}

		return new Dimension(maxX, maxY);
	}

		/**
		 * Crée les nœuds et arcs à partir des tâches du contrôleur.
		 */
		private void initialiserNoeudsArcs()
		{
			noeuds.clear();
			arcs  .clear();

			ArrayList<Tache>         taches    = new ArrayList<>(this.ctrl.getTaches());
			HashMap<String, Integer> dicTaches = new HashMap<>();

			dicTaches.clear();

			this.nbCol = 0; // Reset nbCol

			while(dicTaches.size() != taches.size())
			{
				for (Tache t : taches)
				{
					// Si la tâche n'est pas encore dans le dictionnaire
					if (!dicTaches.containsKey(t.getNom()))
					{
						int colPlusGrd = -1;

						if (t.getPrecedents().isEmpty())
						{
							dicTaches.put(t.getNom(), 0);
						}
						else
						{
							// Vérifier si tous les précédents ont déjà une colonne assignée
							boolean tousPrecsTraites = true;
							
							for (Tache pred : t.getPrecedents())
							{
								String nomTache = pred.getNom();
								if (!dicTaches.containsKey(nomTache))
								{
									tousPrecsTraites = false;
									break;
								}
								
								if (dicTaches.get(nomTache) + 1 > colPlusGrd)
									colPlusGrd = dicTaches.get(nomTache) + 1;
							}
							
							// Ne l'ajouter que si tous ses précédents sont traités
							if (tousPrecsTraites && colPlusGrd != -1)
							{
								dicTaches.put(t.getNom(), colPlusGrd);
								// Mettre à jour nbCol avec la colonne maximale
								if (colPlusGrd > this.nbCol)
									this.nbCol = colPlusGrd;
							}
						}
					}
				}
			}

			for (Tache t : taches)
			{

				Noeud n = new Noeud(t.getNom(), t.getDatePlusTot(), t.getDatePlusTard(), dicTaches.get(t.getNom()), false, ctrl);
				noeuds.add(n);
				for (CheminCritique ch : this.cheminsCritiques)
				{

					for (Tache tacheCh : ch.getTachesCritiques())
					{

						if (tacheCh.getNom().equals(t.getNom()))
							n.setEstChemin(true);
					}
				}

				for (Tache tachePrc : t.getPrecedents())
				{
					arcs.add(new Arc(tachePrc.getNom(), t.getNom(), tachePrc.getDuree()));
				}
			}
		}

	// ========== MÉTHODES D'ACCÈS ==========

	/**
	 * Retourne le nombre de colonnes dans le graphe
	 * 
	 * @return Le nombre de colonnes
	 */
	public int getNbCol()               { return this.nbCol;      }

	/**
	 * Augmente l'étape maximum pour l'affichage des dates au plus tard
	 */
	public void augmenteEtapePlusTard() { this.etapePlusTarMax++; }

	// ========== MÉTHODES DE RENDU GRAPHIQUE ==========

	/**
	 * Affiche le graphe (arcs et nœuds) dans la fenêtre graphique
	 * Gère l'affichage des nœuds, des arcs, des dates et des chemins critiques
	 * 
	 * @param g Le contexte graphique pour le dessin
	 */
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setFont(new Font("SansSerif", Font.PLAIN, 14));

		// ========== DESSIN DES ARCS ==========
		for (Arc arc : arcs)
		{
			Noeud noeudDepart  = this.creerGraphe.getNoeud(arc.getFrom());
			Noeud noeudArrivee = this.creerGraphe.getNoeud(arc.getTo());

			int x1 = noeudDepart.getX()  + boxSize;
			int y1 = noeudDepart.getY()  + boxSize / 2;

			int x2 = noeudArrivee.getX();
			int y2 = noeudArrivee.getY() + boxSize / 2;

			// Point du milieu pour afficher le poids
			int milieuX = (x1 + x2) / 2;
			int milieuY = (y1 + y2) / 2;

			// Calculer la direction de la ligne
			double deltaX   = x2 - x1;
			double deltaY   = y2 - y1;
			double longueur = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

			// Direction unitaire
			double directionX = deltaX / longueur;
			double directionY = deltaY / longueur;

			// Espace avant et après le texte du poids
			int espace = 10;

			// Premier bout de ligne (s'arrête avant le texte)
			int arretX   = (int) (milieuX - espace * directionX);
			int arretY   = (int) (milieuY - espace * directionY);

			// Deuxième bout de ligne (reprend après le texte)
			int repriseX = (int) (milieuX + espace * directionX);
			int repriseY = (int) (milieuY + espace * directionY);

			g2.setColor(Color.BLUE);
			// Première ligne
			g2.drawLine(x1, y1, arretX, arretY);
			// Deuxième ligne
			g2.drawLine(repriseX, repriseY, x2, y2);

			// Texte du poids au milieu
			g2.setColor(new Color(201, 87, 30));
			g2.drawString(String.valueOf(arc.getPoids()), milieuX - 5, milieuY + 5);

			// Flèche indiquant la direction
			drawArrowHead(g2, repriseX, repriseY, x2, y2);
			g2.setColor(Color.BLACK);
		}

		// ========== DESSIN DES NŒUDS ==========
		for (Noeud n : this.noeuds)
		{
			// Dessin du rectangle de base
			g2.setColor(Color.WHITE);
			g2.fillRect(n.getX(), n.getY(), boxSize, boxSize); // fond blanc
			g2.setColor(Color.BLACK);

			// Affichage du chemin critique en surbrillance
			if (n.getEstChemin() && this.cheminActif)
			{
				g2.setColor(Color.GRAY);
				g2.fillRect(n.getX(), n.getY(), boxSize, boxSize);
			}
			g2.setColor(Color.BLACK);

			// Structure du nœud MPM
			g2.drawRect(n.getX()              , n.getY(), boxSize, boxSize);
			g2.drawLine(n.getX()              , n.getY() + boxSize / 2, n.getX() + boxSize, n.getY() + boxSize / 2);
			g2.drawLine(n.getX() + boxSize / 2, n.getY() + boxSize / 2, n.getX() + boxSize / 2, n.getY() + boxSize);

			// Dessin du nom de la tâche centré
			FontMetrics fm         = g2.getFontMetrics();
			int         textWidth  = fm.stringWidth(n.getNom());

			g2.drawString(n.getNom(), n.getX() + (boxSize - textWidth) / 2, n.getY() + 20);
		}

		// ========== AFFICHAGE DES DATES ==========
		for (Noeud n : this.noeuds)
		{
			Tache tache = ctrl.chercherTacheParNom(n.getNom());

			String dtPlusTot = this.enDate ? Tache.ajouterJours(ctrl.getDateDebut(), tache.getDatePlusTot ()) : n.getTot () + "";
			String dtPlusTar = this.enDate ? Tache.ajouterJours(ctrl.getDateDebut(), tache.getDatePlusTard()) : n.getTard() + "";

			int ecartTot     = this.enDate ? 3  : 15;
			int ecartTar     = this.enDate ? 38 : 25;

			// Affichage des dates au plus tôt (vert)
			if (n.getCol() < this.etapePlusTotMax)
			{
				g2.setColor(new Color(30, 189, 120));
				g2.drawString(dtPlusTot, n.getX() + ecartTot, n.getY() + boxSize - 10);
			}

			// Affichage des dates au plus tard (rouge)
			g2.setColor(Color.RED);
			if (n.getCol() > this.etapePlusTarMax)
			{
				g2.drawString(dtPlusTar, n.getX() + boxSize - ecartTar, n.getY() + boxSize - 10);
			}
		}

		g2.setColor(Color.BLACK);
	}
	// ========== MÉTHODES DE CONTRÔLE D'AFFICHAGE ==========

	/**
	 * Bascule entre l'affichage des dates calendaires et des numéros d'étapes
	 */
	public void setEnDate()
	{
		if (!this.enDate)
		{
			this.enDate = true;
			this.majIhm();
		}
		else
		{
			this.enDate = false;
			this.majIhm();
		}
	}

	/**
	 * Active ou désactive l'affichage du chemin critique en surbrillance
	 */
	public void activerChemin()
	{
		this.cheminActif = !cheminActif;
		repaint(); // Redessine le panel
	}

	/**
	 * Augmente l'étape maximum pour l'affichage des dates au plus tôt
	 * 
	 * @return true si l'opération est possible, false si on a atteint la limite
	 */
	public boolean AugmenterEtapePlusTotMax()
	{
		++this.etapePlusTotMax;
		repaint();

		if (this.etapePlusTotMax > this.nbCol)
		{
			return false;
		}
		return true;
	}

	/**
	 * Augmente l'étape maximum pour l'affichage des dates au plus tard
	 * 
	 * @return true si l'opération est possible, false si on a atteint la limite
	 */
	public boolean AugmenterEtapePlusTarMax()
	{
		if (this.etapePlusTarMax > this.nbCol)
		{
			this.etapePlusTarMax = this.nbCol;
		}

		this.etapePlusTarMax--;
		repaint();

		if (this.etapePlusTarMax < 0)
		{
			return false;
		}

		return true;
	}

	/**
	 * Remet à zéro les paramètres d'affichage des étapes
	 */
	public void resetEtape()
	{
		this.etapePlusTarMax = this.nbCol;
		this.etapePlusTotMax = 1;
		this.cheminActif = false;
	}

	// ========== MÉTHODES UTILITAIRES ==========

	/**
	 * Dessine une flèche orientée entre deux points
	 * Utilisée pour indiquer la direction des arcs dans le graphe
	 * 
	 * @param g2 Le contexte graphique 2D
	 * @param x1 Coordonnée X du point de départ
	 * @param y1 Coordonnée Y du point de départ
	 * @param x2 Coordonnée X du point d'arrivée
	 * @param y2 Coordonnée Y du point d'arrivée
	 */
	private void drawArrowHead(Graphics2D g2, int x1, int y1, int x2, int y2)
	{
		double phi   = Math.toRadians(25);
		double dy    = y2 - y1, dx = x2 - x1;
		double theta = Math.atan2(dy, dx);

		int barb     = 10;
		
		for (int j = 0; j < 2; j++)
		{
			double rho = theta + (j == 0 ? phi : -phi);

			int    x   = (int) (x2 - barb * Math.cos(rho));
			int    y   = (int) (y2 - barb * Math.sin(rho));

			g2.setColor(Color.BLUE);
			g2.drawLine(x2, y2, x, y);
		}
	}

	/**
	 * Affiche les informations détaillées d'un nœud dans une fenêtre popup
	 * 
	 * @param n Le nœud dont on veut afficher les informations
	 */
	private void afficherInfosNoeud(Noeud n)
	{
		Tache tache = null;
		for (Tache t : ctrl.getTaches())
		{
			if (t.getNom().equals(n.getNom()))
			{
				tache = t;
				break;
			}
		}
		if (tache == null) return;

		new FrameNoeudInfo(tache, this.ctrl);
	}

	/**
	 * Génère une chaîne contenant les informations de tous les nœuds
	 * Format utilisé pour la sauvegarde des positions
	 * 
	 * @return Chaîne formatée avec les informations des nœuds
	 */
	public String getInfos()
	{
		String sRet = "";
		for (Noeud n : this.noeuds)
		{
			sRet += n.getNom();
			
			// Ajouter les poids des arcs sortants
			for (Arc a : this.arcs)
			{
				if (a.getFrom().equals(n.getNom()))
				{
					sRet += "|" + a.getPoids();
					break;
				}
			}

			sRet += "|";
			
			// Ajouter les prédécesseurs
			for (Tache t : ctrl.getTaches())
			{
				if (t.getNom().equals(n.getNom()))
				{
					for (Tache tPre : t.getPrecedents())
					{
						sRet += tPre.getNom() + ",";
					}
				}
			}
			
			// Enlever la dernière virgule
			if (sRet.endsWith(","))
			{
				sRet = sRet.substring(0, sRet.length() - 1);
			}

			sRet += "|" + n.getX() + "," + n.getY() + "\n";
		}
		return sRet;
	}

	// ========== CLASSE INTERNE - GESTIONNAIRE D'ÉVÉNEMENTS SOURIS ==========

	/**
	 * Classe interne gérant les événements de souris
	 * Permet le glisser-déposer des nœuds et l'affichage des informations
	 */
	private class GereSouris extends MouseAdapter
	{
		/**
		 * Gère les clics de souris sur les nœuds
		 * Clic gauche : sélection pour glisser-déposer
		 * Clic droit : affichage des informations
		 * 
		 * @param e L'événement de clic de souris
		 */
		public void mousePressed(MouseEvent e)
		{
			// Chercher le nœud cliqué
			noeudSelectionne = getNoeudAPosition(e.getX(), e.getY());

			if (noeudSelectionne != null)
			{
				if (SwingUtilities.isRightMouseButton(e))
				{
					afficherInfosNoeud(noeudSelectionne);
				}
				else
				{
					// Calculer l'offset pour un déplacement fluide
					offsetX = e.getX() - noeudSelectionne.getX();
					offsetY = e.getY() - noeudSelectionne.getY();
				}
			}
		}

		/**
		 * Gère le relâchement de la souris
		 * Termine l'opération de glisser-déposer
		 * 
		 * @param e L'événement de relâchement de souris
		 */
		public void mouseReleased(MouseEvent e) { noeudSelectionne = null; }

		/**
		 * Gère le déplacement de la souris avec bouton enfoncé
		 * Met à jour la position du nœud sélectionné
		 * 
		 * @param e L'événement de déplacement de souris
		 */
		public void mouseDragged(MouseEvent e)
		{
			if (noeudSelectionne != null && SwingUtilities.isLeftMouseButton(e))
			{
				// Mettre à jour les coordonnées du nœud
				noeudSelectionne.setX(e.getX() - offsetX);
				noeudSelectionne.setY(e.getY() - offsetY);

				repaint();
			}
		}
	}
}