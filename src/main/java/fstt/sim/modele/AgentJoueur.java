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
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class AgentJoueur extends GameObject {
    protected Position pos;
    private AgentHandler handler;
    private Color couleurMaillot;
    private int numero;
    private int numeroEquipe;
    private boolean matchTermine = false;
    private boolean gardien;
    private boolean possessionEquipe = false;
    private boolean possessionJoueur = false;
    private int vitesse;
    private int tacles;
    private int dribles;
    private int arrets;
    private int tirs;
    private boolean possession;
    private boolean pretAEffectuerUneAction = true;
    private boolean matchCommence;

    public void setPretAEffectuerUneAction(boolean pretAEffectuerUneAction) {
        this.pretAEffectuerUneAction = pretAEffectuerUneAction;
    }

    protected void setup() {
        Object[] args = getArguments();
        try {
            handler = (AgentHandler) args[0];
            handler.getObjects().add(this);
            couleurMaillot = (Color) args[1];
            pos = (Position) args[2];
            numero = (Integer) args[3];
            gardien = (Boolean) args[4];
            numeroEquipe = (Integer) args[5];
            if (gardien)
                handler.ajouteGardien(this.getAID(), numeroEquipe);
            else
                handler.ajouteJoueur(this.getAID(), numeroEquipe);


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("probleme de recuperation des parametres pour : " + getLocalName());
        }
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        vitesse = rand.nextInt(400);
        tacles = rand.nextInt(100);
        dribles = (rand.nextInt(80) + 20);
        arrets = (rand.nextInt(100));
        tirs = (rand.nextInt(80) + 20);
        System.out.println("Agent" + getLocalName() + " est créé");

        ParallelBehaviour comportementparallele = new ParallelBehaviour(ParallelBehaviour.WHEN_ANY);
        comportementparallele.addSubBehaviour(new TickerBehaviour(this, vitesse) {
            @Override
            protected void onTick() {
                if (!matchCommence) {
                    System.out.println("Agent " + getLocalName() + ": en attente du coup d'envoi...");
                    ACLMessage msg = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
                    matchCommence = true;
                    System.out.println("Agent " + getLocalName() + ": a entendu l'arbitre siffler le coup d'envoi");
                    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                    reply.addReceiver(msg.getSender());
                    reply.setContent("OK");
                    send(reply);
                } else if (pretAEffectuerUneAction) {
                    if (numeroEquipe == 1) {
                        possessionEquipe = handler.getTerrain().isPossessionEquipe1();

                    } else possessionEquipe = handler.getTerrain().isPossessionEquipe2();

                    //Si mon equipe n'a pas le ballon
                    if (!possessionEquipe && !gardien) {
                        //Si on est a proximité du ballon
                        if (ReglesDuJeu.SEUIL_PROXIMITE >= PositionHelper.distance(pos, handler.getTerrain().getBallonPos())) {

                            //System.out.println(myAgent.getLocalName() + " est a proximité du ballon");
                            //On demande d'abord au handler.getTerrain() si le ballon est disponible
                            ACLMessage demandeBallon = MessagesHelper.createBallonQuery(handler.getTerrainId());
                            send(demandeBallon);
                            ACLMessage response = blockingReceive();
                            if (MessagesConstantes.BALLON_DISPO.equals(response.getContent()))
                                tenterTacle();
                            else
                                prendreLeBallon();
                        } else {
                            avancerVersBallon();
                        }
                    } else if (!gardien) { // Si mon equipe a le ballon
                        //System.out.println(getLocalName() + ": mon equipe a le ballon");
                        if (!possessionJoueur) {// Si ce n'est pas moi qui ait le ballon, mais un coequipier
                            seDemarquer();
                        } else { // Si j'ai le ballon
                            //System.out.println(getLocalName() + " a le ballon");
                            Position butAdverse;
                            if (numeroEquipe == 1)
                                butAdverse = ReglesDuJeu.BUT_EQUIPE_2;
                            else
                                butAdverse = ReglesDuJeu.BUT_EQUIPE_1;

                            if (ReglesDuJeu.SEUIL_PROXIMITE * 3 < PositionHelper.distance(pos, butAdverse)) {//si je suis loin du but adverse
                                allerAuBut(butAdverse);
                            } else {
                                tenterFrappe();
                            }
                        }
                    }
                } else {
                    pretAEffectuerUneAction = true;
                }
            }


        });
        comportementparallele.addSubBehaviour(new

                                                      CyclicBehaviour(this) {

                                                          public void action() {
                                                              ACLMessage msg = myAgent.receive(MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchOntology("temps")));
                                                              if (msg != null) {
                                                                  // Process the message
                                                                  if (Objects.equals(msg.getContent(), "STOP"))
                                                                      matchTermine = true;
                                                                  System.out.println(myAgent.getLocalName() + " a entendu le coup de sifflet final");
                                                                  myAgent.doDelete();
                                                              }
                                                          }
                                                      });

        addBehaviour(comportementparallele);


        // Make this agent terminate
        //doDelete();
    }

    private void tenterFrappe() {

        System.out.println(getLocalName() + " tente une frappe");
        AID gardienAID = handler.getGardienId(1 - numeroEquipe);

            System.out.println(" BUT de " + getLocalName() + " !!");
        possessionEquipe = false;
        possessionJoueur = false;
        handler.getTerrain().rendreBallonDisponible();
        handler.getTerrain().setBallonPos(ReglesDuJeu.MILIEU_DE_TERRAIN);
        AID arbitreId = handler.getArbitreId();
        AgentArbitre arbitre = handler.getArbitre(arbitreId);
        arbitre.inscrireBut(numeroEquipe);
    }

    private void allerAuBut(Position butAdverse) {
        pos.approcher(butAdverse);
        System.out.println(getLocalName() + " va au but");
    }

    private void seDemarquer() {
        if (numeroEquipe == 1) {
            pos.Fuir(ReglesDuJeu.BUT_EQUIPE_1);
        } else pos.Fuir(ReglesDuJeu.BUT_EQUIPE_2);
    }

    private void avancerVersBallon() {
        pos.approcher(handler.getTerrain().getBallonPos());
    }

    private void prendreLeBallon() {
        handler.getTerrain().setBallonDisponible(false);
        handler.getTerrain().setJoueurAuBallon(getAID());
        handler.getTerrain().setPosJoueurAuBallon(pos);
        possessionJoueur = true;
        handler.getTerrain().setBallonPos(pos);
        possessionEquipe = true;
        handler.getTerrain().setPossession(numeroEquipe);
        if (numeroEquipe == 1) {
            pos.approcher(ReglesDuJeu.BUT_EQUIPE_1);
        } else pos.approcher(ReglesDuJeu.BUT_EQUIPE_2);
        System.out.println(getLocalName() + " a récupéré le ballon");
    }

    private void tenterTacle() {
        System.out.println(getLocalName() + " va tenter un tacle");
        addBehaviour(new Behaviour() {
            public void action() {
                AgentJoueur joueurAuBallon = handler.getJoueur(handler.getTerrain().getJoueurAuBallon());
                if (joueurAuBallon != null) {
                    if (tacles > joueurAuBallon.getDribles()) {
                        handler.getTerrain().setPossession(numeroEquipe);
                        possessionJoueur = true;
                        handler.getTerrain().setBallonPos(pos);
                        joueurAuBallon.setPossessionJoueur(false);
                        // Après avoir subi un tacle on ne peut pas effectuer tout de suite une nouvelle action
                        joueurAuBallon.setPretAEffectuerUneAction(false);
                        System.out.println(getLocalName() + " a reussi un tacle");

                    } else {
                        // Après avoir subi un dribble on ne peut pas effectuer tout de suite une nouvelle action
                        pretAEffectuerUneAction = false;
                        System.out.println(joueurAuBallon.getLocalName() + " a reussi un dribble");
                    }
                } else System.out.println("Cible du tacle non trouvée");
            }

            public boolean done() {
                return true;
            }
        });
    }

    protected void takeDown() {
        System.out.println("Agent " + getLocalName() + ": terminating");
    }

    @Override
    public void render(Graphics g) {
        g.setColor(couleurMaillot);
        g.fillOval(pos.getX() - ReglesDuJeu.TAILLE_JOUEUR / 2, pos.getY() - ReglesDuJeu.TAILLE_JOUEUR / 2, ReglesDuJeu.TAILLE_JOUEUR, ReglesDuJeu.TAILLE_JOUEUR);
        g.setColor(Color.WHITE);
        if (numero < 10) {
            g.drawString(String.valueOf(numero), pos.getX() - 3, pos.getY() + 3);
        } else {
            g.drawString(String.valueOf(numero), pos.getX() - 6, pos.getY() + 4);
        }
        g.drawOval(pos.getX() - ReglesDuJeu.TAILLE_JOUEUR / 2, pos.getY() - ReglesDuJeu.TAILLE_JOUEUR / 2, ReglesDuJeu.TAILLE_JOUEUR, ReglesDuJeu.TAILLE_JOUEUR);
    }

    public int getDribles() {
        return dribles;
    }

    public void setPossessionJoueur(boolean possession) {
        this.possessionJoueur = possession;
    }
}
