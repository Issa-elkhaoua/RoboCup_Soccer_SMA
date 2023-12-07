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
import fstt.sim.controleur.Constants;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AgentTerrain extends Agent {
    private AgentHandler handler;
    private Position ballonPos = new Position(ReglesDuJeu.MILIEU_DE_TERRAIN);
    private boolean possessionEquipe1;
    private boolean possessionEquipe2;
    private Position posJoueurAuBallon;
    private boolean ballonDisponible = true;
    private AID joueurAuBallon;
    private MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF),
            MessageTemplate.MatchOntology(MessagesConstantes.ONTOLOGY_BALLON));

    public Position getBallonPos() {
        return ballonPos;
    }

    public void setBallonPos(Position ballonPos) {
        this.ballonPos = ballonPos;
    }

    public boolean isBallonDisponible() {
        return ballonDisponible;
    }

    public void setBallonDisponible(boolean ballonDisponible) {
        this.ballonDisponible = ballonDisponible;
    }

    public void rendreBallonDisponible() {
        this.ballonDisponible = true;
        this.possessionEquipe1 = false;
        this.possessionEquipe2 = false;
    }

    public AID getJoueurAuBallon() {
        return joueurAuBallon;
    }

    public void setJoueurAuBallon(AID joueurAuBallon) {
        this.joueurAuBallon = joueurAuBallon;
    }

    protected void setup() {
        Object[] args = getArguments();
        handler = (AgentHandler) args[0];
        //handler.getObjects().add(this);
        handler.setTerrainId(this.getAID());
        handler.setTerrain(this);

        System.out.println("Agent" + getLocalName() + " est créé");
        // Make this agent terminate
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = myAgent.receive(template);
                if (msg != null) {
                    System.out.println("Received QUERY_IF message from agent " + msg.getSender().getName());
                    ACLMessage reply = msg.createReply();
                    reply.setOntology(MessagesConstantes.ONTOLOGY_BALLON);
                    if (MessagesConstantes.QUERY_BALLON.equals(msg.getContent())) {
                        if (ballonDisponible) {
                            reply.setPerformative(ACLMessage.INFORM);
                            reply.setContent(MessagesConstantes.BALLON_DISPO);
                            ballonDisponible = false;
                            joueurAuBallon = msg.getSender();
                        } else {
                            reply.setPerformative(ACLMessage.INFORM);
                            reply.setContent(joueurAuBallon.getLocalName());
                        }
                    } else {
                        reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                        reply.setContent("Unknown-content");
                    }
                    myAgent.send(reply);
                } else {
                    block();
                }
            }
        });
    }

    public void doDelete() {
        super.doDelete();
    }

    protected void takeDown() {
        System.out.println("Agent " + getLocalName() + ": terminating");
    }


    public void render(Graphics g) {
        //Terrain
        try {
            BufferedImage imgTerrain = ImageIO.read(new File("src/main/resources/images/terrain.jpg"));
            g.drawImage(imgTerrain, 1, 1, ReglesDuJeu.LONGUEUR_TERRAIN, ReglesDuJeu.LARGEUR_TERRAIN, null);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Pb de chargement d'image");
        }

        //Ballon
        g.setColor(Color.WHITE);
        g.fillOval(ballonPos.getX() - ReglesDuJeu.TAILLE_BALLON / 2, ballonPos.getY() - ReglesDuJeu.TAILLE_BALLON / 2, ReglesDuJeu.TAILLE_BALLON, ReglesDuJeu.TAILLE_BALLON);
        g.setColor(Color.BLACK);
        g.drawOval(ballonPos.getX() - ReglesDuJeu.TAILLE_BALLON / 2, ballonPos.getY() - ReglesDuJeu.TAILLE_BALLON / 2, ReglesDuJeu.TAILLE_BALLON, ReglesDuJeu.TAILLE_BALLON);

        //Arbitre
        g.setColor(ReglesDuJeu.COULEUR_ARBITRE);
        g.fillOval(ballonPos.getX(), ballonPos.getY() - 40, ReglesDuJeu.TAILLE_ARBITRE, ReglesDuJeu.TAILLE_ARBITRE);
        g.setColor(Color.WHITE);
        g.drawOval(ballonPos.getX(), ballonPos.getY() - 40, ReglesDuJeu.TAILLE_ARBITRE, ReglesDuJeu.TAILLE_ARBITRE);

        //Milieu de terrain + lignes pour debug
        if (Constants.DEBUG) {
            int taille = 10;
            int x = ReglesDuJeu.MILIEU_DE_TERRAIN.getX();
            int y = ReglesDuJeu.MILIEU_DE_TERRAIN.getY();
            g.setColor(Color.RED);
            g.fillOval(x - taille / 2, y - taille / 2, taille, taille);
            g.drawLine(0, y, ReglesDuJeu.LONGUEUR_TERRAIN, y);
            g.drawLine(x, 0, x, ReglesDuJeu.LARGEUR_TERRAIN);
        }
    }

    public boolean isPossessionEquipe1() {
        return possessionEquipe1;
    }

    public void setPossessionEquipe1(boolean possessionEquipe1) {
        this.possessionEquipe1 = possessionEquipe1;
    }

    public boolean isPossessionEquipe2() {
        return possessionEquipe2;
    }

    public void setPossessionEquipe2(boolean possessionEquipe2) {
        this.possessionEquipe2 = possessionEquipe2;
    }

    public void setPossession(int numero) {
        if (numero == 1) {
            setPossessionEquipe1(true);
            setPossessionEquipe2(false);
        } else if (numero == 2) {
            setPossessionEquipe2(true);
            setPossessionEquipe1(false);
        } else {
            setPossessionEquipe1(false);
            setPossessionEquipe2(false);
        }
    }

    public void setPosJoueurAuBallon(Position posJoueurAuBallon) {
        this.posJoueurAuBallon = posJoueurAuBallon;
    }
}
