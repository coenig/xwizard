/*
 * Datei:            Strecke2D.java
 * Autor(en):        Lukas König
 * Java-Version:     6.0
 * Letzte Aenderung: 23.12.2008
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
 * Implementierung einer Strecke.
 * 
 * @author Lukas König
 */
public class LineSegment2D implements Serializable {

    private static final long serialVersionUID = -1523971022976086717L;

    /**
     * Der Anfangspunkt der Strecke.
     */
    private Vector2D begin;
    
    /**
     * Der Endpunkt der Strecke.
     */
    private Vector2D end;
    
    /**
     * Konstruktor.
     * 
     * @param anfang  Der Anfangspunkt der Geraden.
     * @param ende    Der Endpunkt der Geraden.
     */
    public LineSegment2D(final Vector2D anfang, final Vector2D ende) {
       this.begin = new Vector2D(anfang);
       this.end = new Vector2D(ende);
    }
    
    /**
     * Konstruktor.
     * 
     * @param s  Die andere Strecke, von der die Endpunkte übernommen werden.
     */
    public LineSegment2D(final LineSegment2D s) {
        this.begin = s.begin;
        this.end = s.end;
    }

    /**
     * @return Returns the anfPkt.
     */
    public Vector2D getBeginPoint() {
        return this.begin;
    }

    /**
     * @return Returns the endPkt.
     */
    public Vector2D getEndPoint() {
        return this.end;
    }

    /**
     * Gibt die Richtung der Gerade normiert zurück.
     * 
     * @return  Die Richtung der Geraden.
     */
    public Vector2D getNormDir() {
        Vector2D richt = new Vector2D(this.end);
        richt.sub(begin);
        richt.normalize();
        return richt;
    }
    
    /**
     * @return  Die Länge der Strecke.
     */
    public double length() {
        return this.begin.distance(this.end);
    }
    
    /**
     * @param anfang The anfPkt to set.
     */
    public void setBeginPoint(final Vector2D anfang) {
        this.begin = anfang;
    }

    /**
     * @param ende The endPkt to set.
     */
    public void setEndPoint(final Vector2D ende) {
        this.end = ende;
    }
    
    public Vector2D intersects(final Polygon2D p) {
        return p.intersect(this);
    }
    
    /**
     * Berechnet den Schnttpunkt der beiden Strecken this und q.
     * 
     * @param q  Die andere Strecke.
     * 
     * @return  Der Schnittpunkt von g ung h. Wenn die Strecken keinen
     *          Schnittpunkt haben, wird <code>null</code> zurückgegeben.
     */
    public Vector2D intersects(final LineSegment2D q) {
        Vector2D vp, vq;
        Line2D g, h;
        vp = this.getNormDir();
        vq = q.getNormDir();
        
        g = new Line2D(this.begin, vp);
        h = new Line2D(q.begin, vq);
        
        Vector2D strSchnPkt = g.intersection(h);
        
        if (strSchnPkt == null) {
            return null;
        }

        // Ist Punkt auf beiden Strecken?
//        if (strSchnPkt.istInRechteckOR(this.anfPkt, this.endPkt) 
//            && strSchnPkt.istInRechteckOR(q.anfPkt, q.endPkt)) {
        if (this.isPointOnProlongedLineAlsoOnSegment(strSchnPkt) && q.isPointOnProlongedLineAlsoOnSegment(strSchnPkt)) {
            return strSchnPkt;
        } else {
            return null;
        }
    }
    
    private boolean isPointOnProlongedLineAlsoOnSegment(final Vector2D punkt) {
        double aq = this.begin.distance(punkt);
        double ab = this.begin.distance(this.end);
        double qb = punkt.distance(this.end);
        
        return aq <= ab && qb <= ab;
    }
    
    /**
     * @return  Der Text.
     */
    @Override
    public String toString() {
        return "<" + this.begin + " - " + this.end + ">";
    }

    public boolean isLeftOf(final Vector2D point) {
        double x2 = this.begin.x - this.end.x;
        double x3 = this.begin.x - point.x;
        double y2 = this.begin.y - this.end.y;
        double y3 = this.begin.y - point.y;

        return (x2 * y3 - y2 * x3 < 0);
    }

    public Polygon2D toPol2D() {
        Polygon2D p = new Polygon2D();
        
        p.add(new Vector2D(begin));
        p.add(new Vector2D(end));
        
        return p;
    }
}
