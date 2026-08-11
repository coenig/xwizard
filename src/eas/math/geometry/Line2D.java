/*
 * Datei:            Gerade2D.java
 * Autor(en):        Lukas König
 * Java-Version:     6.0
 * Letzte Aenderung: 24.12.2008
 *
 * (c) This file and the EAS (Easy Agent Simulation) framework containing it
 * is protected by Creative Commons by-nc-sa license. Any altered or
 * further developed versions of this file have to meet the agreements
 * stated by the license conditions. 
 * 
 * In a nutshell
 * -------------
 * You are free:
 * - to Share -- to copy, distribute and transmit the work
 * - to Remix -- to adapt the work
 * 
 * Under the following conditions:
 * - Attribution -- You must attribute the work in the manner specified by the 
 *   author or licensor (but not in any way that suggests that they endorse 
 *   you or your use of the work).
 * - Noncommercial -- You may not use this work for commercial purposes.
 * - Share Alike -- If you alter, transform, or build upon this work, you may 
 *   distribute the resulting work only under the same or a similar license to 
 *   this one. 
 * 
 * + Detailed license conditions (Germany):
 *   http://creativecommons.org/licenses/by-nc-sa/3.0/de/
 * + Detailed license conditions (unported):
 *   http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en
 * 
 * This header must be placed in the beginning of any version of this file.
 */

package eas.math.geometry;

import java.io.Serializable;


/**
 * Implementierung einer Gerade.
 * 
 * @author Lukas König
 */
public class Line2D implements Serializable {

    private static final long serialVersionUID = -8641854298711777125L;

    /**
     * Der Stützpunkt der Gerade.
     */
    private Vector2D basePoint;
    
    /**
     * Die Richtung der Geraden.
     */
    private Vector2D direction;
    
    /**
     * Konstruktor.
     * 
     * @param stuetz  Der Stützpunkt.
     * @param richt   Die Richtung.
     */
    public Line2D(final Vector2D stuetz, final Vector2D richt) {
        this.basePoint = new Vector2D(stuetz);
        this.direction = new Vector2D(richt);
    }
    
    /**
     * Konstruktor, der die Gerade als VerLängerung einer Strecke erzeugt.
     * 
     * @param s  Die Strecke, aus der die Gerade erzeugt werden soll.
     */
    public Line2D(final LineSegment2D s) {
        this.basePoint = new Vector2D(s.getBeginPoint());
        this.direction = new Vector2D(s.getEndPoint());
        this.direction.sub(s.getEndPoint());
    }

    /**
     * Berechnet den Schnittpunkt der beiden Geraden this und h.
     * 
     * @param h       Die andere Gerade.
     * 
     * @return  Der Schnittpunkt von this und h. Wenn die Geraden parallel 
     *          sind, wird der Mittelpunkt zwischen den Stützpunkten 
     *          zurückgegeben. Als parallel werden Geraden angenommen, bei 
     *          denen 
     *          v1.x * v2.y - v2.x * v1.y > konst
     *          gilt, mit konst = 0.0001.
     */
    public Vector2D intersectionSpecial(final Line2D h) {
        final double nullKonst = 0.0001;
        final Vector2D p1 = this.basePoint;
        final Vector2D v1 = this.direction;
        final Vector2D p2 = h.basePoint;
        final Vector2D v2 = h.direction;
        Vector2D zwisch;
        
        // Der Schnittpunkt der Geraden.
        Vector2D q = new Vector2D(p1);
        q.translate(p2);
        q.div(2);

        if (v1.distance(Vector2D.NULL_VECTOR) < nullKonst
            || v2.distance(Vector2D.NULL_VECTOR) < nullKonst
            || Double.isNaN(v1.x) || Double.isNaN(v1.y)
            || Double.isNaN(v2.x) || Double.isNaN(v2.y)
            || Double.isNaN(p1.x) || Double.isNaN(p1.y)
            || Double.isNaN(p2.x) || Double.isNaN(p2.y)
            ) {
            if (Double.isNaN(p1.x) || Double.isNaN(p1.y)) {
                q = new Vector2D(p2);
            }
            if (Double.isNaN(p2.x) || Double.isNaN(p2.y)) {
                q = new Vector2D(p1);
            }
        } else {
            zwisch = this.intersection(h);
            
            if (zwisch != null) {
                q = zwisch;
            }
        }
        
        if (Double.isNaN(q.x) || Double.isNaN(q.y)) {
//            SonstMeth.log(
//                    SonstMeth.LOG_ERROR,
//                    "Geradenschnittpunkt nicht berechnet: "
//                    + p1 + " | " + p2,
//                    params);
            throw new RuntimeException("Geradenschnittpunkt nicht berechnet: "
                    + p1 + " | " + p2);
        }

        return q;
    }
    
    /**
     * Gibt den Schnittpunkt zweier Geraden zurück. Wenn sie sich nicht 
     * schneiden oder ein und dieselbe Gerade sind, wird <code>null</code>
     * zurückgegeben.
     * 
     * @param h  Die andere Gerade.
     * @return  Schnittpunkt der Geraden oder <code>null</code>.
     */
    public Vector2D intersection(final Line2D h) {
        double t;
        double div;
        final double nullKonst = 0.0001;
        Vector2D q = null;
        final Vector2D p1 = this.basePoint;
        final Vector2D v1 = this.direction;
        final Vector2D p2 = h.basePoint;
        final Vector2D v2 = h.direction;
        t = v1.y * (p2.x - p1.x) - v1.x * (p2.y - p1.y);
        div = v1.x * v2.y - v2.x * v1.y;
        
        if (Math.abs(div) > nullKonst) {
            t /= div;
            q = new Vector2D(v2);
            q.mult(t);
            q.translate(p2);
        }

        return q;
    }
    
    /**
     * @return Returns the stuetzPunkt.
     */
    public Vector2D getBasePoint() {
        return this.basePoint;
    }

    /**
     * @return Returns the richtung.
     */
    public Vector2D getDirection() {
        return this.direction;
    }
    
    @Override
    public String toString() {
        return this.basePoint + " + v * " + this.direction;
    }
}
