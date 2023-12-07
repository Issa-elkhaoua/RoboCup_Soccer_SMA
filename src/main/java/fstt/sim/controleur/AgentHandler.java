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

package fstt.sim.controleur;

import fstt.sim.modele.AgentJoueur;
import fstt.sim.modele.AgentArbitre;
import fstt.sim.modele.AgentTerrain;
import jade.core.AID;

import java.awt.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class AgentHandler {

    // Liste des objets de jeu (agents, terrain, etc.), utilisant une structure de données thread-safe.
    protected CopyOnWriteArrayList<GameObject> objects = new CopyOnWriteArrayList<>();

    // Ensembles pour stocker les identifiants (AID) des joueurs de chaque équipe.
    private Set<AID> equipe1 = new HashSet<>();
    private Set<AID> equipe2 = new HashSet<>();

    // Identifiants pour l'arbitre, le terrain, et les gardiens de chaque équipe.
    private AID arbitreId;
    private AID terrainId;
    private AgentTerrain terrain; // Référence à l'agent représentant le terrain.
    private AID gardienEquipe1;
    private AID gardienEquipe2;

    // Getters et Setters pour accéder et modifier les identifiants.
    public AID getArbitreId() {
        return arbitreId;
    }

    public void setArbitreId(AID arbitreId) {
        this.arbitreId = arbitreId;
    }

    public CopyOnWriteArrayList<GameObject> getObjects() {
        return objects;
    }

    /**
     * Rendu graphique de tous les agents et du terrain.
     */
    public void render(Graphics g) {
        if (terrain != null)
            terrain.render(g); // Rendu du terrain si présent.
        objects.forEach(agent -> agent.render(g)); // Rendu de chaque agent.
    }

    /**
     * Méthode pour démarrer le match, en sifflant le coup d'envoi via l'arbitre.
     */
    public void startMatch() {
        if (arbitreId != null) {
            objects.stream().filter(go -> go.getAID() == arbitreId).forEach(go -> {
                AgentArbitre arbitre = (AgentArbitre) go;
                arbitre.sifflerCoupDEnvoi(); // Siffle le coup d'envoi.
            });
        }
    }

    // Getters et Setters pour l'ID du terrain.
    public AID getTerrainId() {
        return terrainId;
    }

    public void setTerrainId(AID terrainId) {
        this.terrainId = terrainId;
    }

    // Méthodes pour obtenir ou définir l'agent terrain, marquées comme dépréciées.
    @Deprecated
    public AgentTerrain getTerrain() {
        return terrain;
    }

    @Deprecated
    public void setTerrain(AgentTerrain at) {
        terrain = at;
    }

    // Méthode pour obtenir les identifiants de tous les joueurs des deux équipes.
    public Stream<AID> getJoueursIds() {
        return Stream.concat(equipe1.stream(), equipe2.stream());
    }

    // Ajoute un joueur à une équipe spécifiée par son numéro.
    public void ajouteJoueur(AID joueurAID, int numeroEquipe) {
        if (numeroEquipe == 1)
            equipe1.add(joueurAID);
        else
            equipe2.add(joueurAID);
    }

    // Méthode pour obtenir un agent joueur par son ID, marquée comme dépréciée.
    @Deprecated
    public AgentJoueur getJoueur(AID id) {
        Optional<GameObject> gameObject = objects.stream().filter(j -> j.getAID() == id).findFirst();
        return (AgentJoueur) gameObject.orElse(null);
    }

    // Méthode pour obtenir l'ID du gardien d'une équipe spécifiée.
    public AID getGardienId(int numeroEquipeGardien) {
        if (numeroEquipeGardien == 1)
            return gardienEquipe1;
        else
            return gardienEquipe2;
    }

    // Ajoute un gardien à une équipe spécifiée par son numéro.
    public void ajouteGardien(AID aid, int numeroEquipe) {
        if (numeroEquipe == 1)
            this.gardienEquipe1 = aid;
        else
            this.gardienEquipe2 = aid;
    }

    // Méthode pour obtenir l'arbitre par son ID, marquée comme dépréciée.
    @Deprecated
    public AgentArbitre getArbitre(AID id) {
        Optional<GameObject> gameObject = objects.stream().filter(a -> a.getAID() == id).findFirst();
        return (AgentArbitre) gameObject.orElse(null);
    }

    // Méthode pour arrêter le match, supprime le terrain et tous les agents.
    public void stopMatch() {
        if (terrain != null)
            terrain.doDelete(); // Supprime l'agent terrain.
        objects.forEach(agent -> agent.doDelete()); // Supprime chaque agent.
    }
}
