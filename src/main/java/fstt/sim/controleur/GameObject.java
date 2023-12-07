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

import jade.core.Agent;


import java.awt.*;

public abstract class GameObject extends Agent {
    // Déclaration des variables pour la position (x, y), la visibilité, l'identifiant,
    // et les vitesses en x et y.
    protected int x, y;
    protected boolean visible = false;
    protected String id;
    protected int velX, velY;

    // Méthode pour initialiser l'objet avec position et identifiant.
    public void setGameObject(int x, int y, String id) {
        //super(); // Commenté car la classe parent 'Agent' n'a peut-être pas de constructeur pertinent.
        this.x = x;
        this.y = y;
        this.id = id;
    }

    // Surcharge de la méthode setGameObject pour inclure également les vitesses.
    public void setGameObject(int x, int y, String id, int velX, int velY) {
        setGameObject(x, y, id); // Appel de la méthode précédente pour les valeurs de base.
        this.velX = velX; // Définition de la vitesse en x.
        this.velY = velY; // Définition de la vitesse en y.
    }

    // Méthode pour vérifier si l'objet est visible.
    public boolean isVisible() {
        return visible;
    }

    // Méthode pour définir la visibilité de l'objet.
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    // Accesseurs et mutateurs pour les propriétés de l'objet (position, id, vitesse).
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVelX() {
        return velX;
    }

    public void setVelX(int velX) {
        this.velX = velX;
    }

    public int getVelY() {
        return velY;
    }

    public void setVelY(int velY) {
        this.velY = velY;
    }

    // Méthode abstraite pour le rendu, devant être implémentée dans les sous-classes.
    public abstract void render(Graphics g);
}
