/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Arthur Lefebvre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package fstt.sim.modele;

import fstt.sim.controleur.AgentHandler;
import fstt.sim.controleur.GameObject;
import jade.core.behaviours.TickerBehaviour;

import java.awt.*;

public class AgentArbitre extends GameObject {
    // Variables pour le score des deux équipes, le temps de jeu, la position de l'arbitre,
    // le gestionnaire des agents, et un indicateur pour savoir si le coup d'envoi a été donné.
    private int scoreEquipe1 = 0;
    private int scoreEquipe2 = 0;
    private int tempsDeJeu = 0;
    private Position pos;
    private AgentHandler handler;
    private boolean coupDEnvoiDonne;

    // Méthode pour siffler le coup d'envoi du match.
    public void sifflerCoupDEnvoi() {
        // Envoi d'un message pour signaler le début du match aux joueurs.
        send(MessagesHelper.createDebutDuMatchRequest(handler.getJoueursIds()));
        // Indication que le coup d'envoi a été donné.
        coupDEnvoiDonne = true;
    }

    // Configuration initiale de l'agent arbitre.
    protected void setup() {
        // Récupération des arguments passés lors de la création de l'agent.
        Object[] args = getArguments();
        tempsDeJeu = (int) args[0]; // Temps de jeu initial.
        handler = (AgentHandler) args[1]; // Gestionnaire des agents.
        // Ajout de cet arbitre à la liste des objets gérés par le handler.
        handler.getObjects().add(this);
        // Définition de l'ID de l'arbitre dans le handler.
        handler.setArbitreId(this.getAID());

        // Ajout d'un comportement qui s'exécute périodiquement chaque seconde.
        addBehaviour(new TickerBehaviour(this, 1000) {
            protected void onTick() {
                // Affichage du temps de jeu écoulé.
                System.out.println(tempsDeJeu + " minutes jouées");
                // Si le temps de jeu dépasse 90 minutes, fin du match.
                if (tempsDeJeu >= 90 * 60) {
                    sifflerFinDuMatch();
                    myAgent.doDelete(); // Suppression de l'agent.
                } else if (coupDEnvoiDonne) // Sinon, incrémentation du temps de jeu si le match a commencé.
                    tempsDeJeu++;
            }
        });
    }

    // Méthode pour siffler la fin du match.
    public void sifflerFinDuMatch() {
        // Envoi d'un message pour signaler la fin du match aux joueurs.
        send(MessagesHelper.createFinDuMatchRequest(handler.getJoueursIds()));
    }

    // Nettoyage lors de la suppression de l'agent.
    protected void takeDown() {
        // Affichage d'un message lors de la terminaison de l'agent.
        System.out.println("Agent " + getLocalName() + ": terminating");
    }

    // Méthode de rendu graphique pour afficher le score et le temps de jeu.
    @Override
    public void render(Graphics g) {
        // Conversion du temps de jeu en minutes et secondes.
        int minutes = tempsDeJeu / 60;
        int secondes = tempsDeJeu % 60;
        // Construction de la chaîne de caractères pour l'affichage.
        StringBuilder sb = new StringBuilder();
        sb.append("[Equipe1] ")
                .append(scoreEquipe1)
                .append("-")
                .append(scoreEquipe2)
                .append(" [Equipe2] | [");
        // Ajout de zéros pour les valeurs inférieures à 10.
        if (minutes < 10)
            sb.append("0");
        sb.append(minutes);
        sb.append(":");
        if (secondes < 10)
            sb.append("0");
        sb.append(secondes);
        sb.append("]");
        // Définition de la couleur et affichage du texte en noir et en gras, avec une taille de police de 15.
        g.setColor(Color.BLACK);
        Font font = new Font("Arial", Font.BOLD, 15);
        g.setFont(font);
        g.drawString(sb.toString(), 20, 20);
    }


    // Méthode pour incrémenter le score de l'équipe spécifiée.
    public void inscrireBut(int numeroEquipe) {
        // Incrémentation du score de l'équipe correspondante.
        if(numeroEquipe == 1)
            scoreEquipe1++;
        else
            scoreEquipe2++;
    }
}
