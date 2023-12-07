package fstt.sim.modele;

import java.awt.*;

public abstract class ReglesDuJeu {
    public static final int NBJOUEURS = 8;
    public static final Color COULEUR_GARDIENS = Color.BLACK;
    public static final Color COULEUR_EQUIPE_1 = Color.RED;
    public static final Color COULEUR_EQUIPE_2 = Color.CYAN;
    public static final Color COULEUR_ARBITRE = Color.PINK;
    public static final int TAILLE_JOUEUR = 20;
    public static final int TAILLE_BALLON = 12;
    public static final int TAILLE_ARBITRE = TAILLE_JOUEUR;
    public static final int LARGEUR_TERRAIN = 400;
    public static final int LONGUEUR_TERRAIN = 600;
    public static final Position MILIEU_DE_TERRAIN = new Position(LONGUEUR_TERRAIN / 2, LARGEUR_TERRAIN / 2);
    public static final Position BUT_EQUIPE_1 = new Position(25, MILIEU_DE_TERRAIN.getY());
    public static final Position BUT_EQUIPE_2 = new Position(LONGUEUR_TERRAIN - 25, MILIEU_DE_TERRAIN.getY());
    public static final double SEUIL_PROXIMITE = 10;
}
