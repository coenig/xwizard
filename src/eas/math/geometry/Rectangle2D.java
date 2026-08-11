/*
 * Dateiname:        Rechteck2D.java
 * Autor(en):        Lukas König
 * Java-Version:     6.0
 * Letzte Aenderung: 19.12.2008
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

import java.awt.Polygon;
import java.io.Serializable;


/**
 * @author Lukas König
 */
public class Rectangle2D implements Serializable {

    private static final long serialVersionUID = 1563964925325946577L;
    
    private Vector2D upperLeft;
    private Vector2D lowerRight;
    
    public Rectangle2D(final Rectangle2D otherRectangle) {
        this(new Vector2D(otherRectangle.upperLeft), 
             new Vector2D(otherRectangle.lowerRight));
    }
    
    /**
     * Konstruktor.
     * 
     * @param v1  Vektor für linke obere Ecke.
     * @param v2  Vektor für rechte untere Ecke.
     */
    public Rectangle2D(final Vector2D v1, final Vector2D v2) {
        this.upperLeft = new Vector2D(Vector2D.NULL_VECTOR);
        this.lowerRight = new Vector2D(Vector2D.NULL_VECTOR);
        this.set(v1, v2);
    }
    
    /**
     * @param i Links.
     * @param j Oben.
     * @param k Rechts.
     * @param l Unten.
     */
    public Rectangle2D(double i, double j, double k, double l) {
        this(new Vector2D(i, j), new Vector2D(k, l));
    }

    /**
     * Verschiebt das Rechteck um den angegebenen Vektor.
     * 
     * @param vek  Der Verschiebevektor.
     */
    public void translate(final Vector2D vek) {
        this.upperLeft.translate(vek);
        this.lowerRight.translate(vek);
    }
    
    /**
     * Setze Rechteckkoordinaten.
     * 
     * @param v1  Vektor für eine Ecke.
     * @param v2  Vektor für die andere Ecke.
     */
    public void set(final Vector2D v1, final Vector2D v2) {
        if (v1.x < v2.x) {
            this.upperLeft.x = v1.x;
            this.lowerRight.x = v2.x;
        } else {
            this.upperLeft.x = v2.x;
            this.lowerRight.x = v1.x;
        }
        
        if (v1.y < v2.y) {
            this.upperLeft.y = v1.y;
            this.lowerRight.y = v2.y;
        } else {
            this.upperLeft.y = v2.y;
            this.lowerRight.y = v1.y;
        }
    }
    
    /**
     * Setzt eine Seite des Rechtecks neu.
     * 
     * @param xthis.upperLeft.x  Der zu setzende neue Wert.
     */
    public void setLeft(final double xx) {
        this.set(new Vector2D(xx, this.upperLeft.y), this.lowerRight);
    }
    
    /**
     * Setzt eine Seite des Rechtecks neu.
     * 
     * @param xx2  Der zu setzende neue Wert.
     */
    public void setRight(final double xx2) {
        this.set(this.upperLeft, new Vector2D(xx2, this.lowerRight.y));
    }
    
    /**
     * Setzt eine Seite des Rechtecks neu.
     * 
     * @param ythis.upperLeft.y  Der zu setzende neue Wert.
     */
    public void setTop(final double yy) {
        this.set(new Vector2D(this.upperLeft.x, yy), this.lowerRight);
    }
    
    /**
     * Setzt eine Seite des Rechtecks neu.
     * 
     * @param ythis.lowerRight.y  Der zu setzende neue Wert.
     */
    public void setBottom(final double yy) {
        this.set(this.upperLeft, new Vector2D(this.lowerRight.x, yy));
    }
    
    /**
     * @return  Die linke obere Ecke.
     */
    public Vector2D upperLeftCorner() {
        return this.upperLeft;
    }

    /**
     * @return  Die rechte untere Ecke.
     */
    public Vector2D lowerRightCorner() {
        return this.lowerRight;
    }
    
    /**
     * @return  Die Höhe.
     */
    public double getHeight() {
        return this.lowerRight.y - this.upperLeft.y;
    }

    /**
     * @return  Die Breite.
     */
    public double getWidth() {
        return this.lowerRight.x - this.upperLeft.x;
    }
    
    public Vector2D center() {
        return new Vector2D(
                (this.upperLeft.x + this.lowerRight.x) / 2,
                (this.upperLeft.y + this.lowerRight.y) / 2);
    }
    
    /**
     * 
     * @param other  Another rectangle.
     * @return  Iff the rectangles overlap, i.e., iff one contains at least 
     *          one point of the other (border counts as rectangle area).
     */
    public boolean intersect(final Rectangle2D other) {
        return !(this.upperLeft.x > other.lowerRight.x || this.lowerRight.x < other.upperLeft.x ||
                 this.upperLeft.y > other.lowerRight.y || this.lowerRight.y < other.upperLeft.y);
    }
    
    /**
     * Skaliert das Rechteck um die angegebene X- und Y-Richtung und den 
     * angegebenen Mittelpunkt.
     * 
     * @param skal   Der Skalierungsvektor.
     * @param mitte  Der Mittelpunkt des Skalierens.
     */
    public void scale(final Vector2D mitte, final Vector2D skal) {
        this.upperLeft.scale(mitte, skal);
        this.lowerRight.scale(mitte, skal);
    }
    
    public Polygon2D toPol2D() {
        Polygon2D p = new Polygon2D();
        
        p.add(new Vector2D(this.upperLeft.x, this.upperLeft.y));
        p.add(new Vector2D(this.upperLeft.x, this.lowerRight.y));
        p.add(new Vector2D(this.lowerRight.x, this.lowerRight.y));
        p.add(new Vector2D(this.lowerRight.x, this.upperLeft.y));
        
        return p;
    }
    
    public Polygon toPol() {
        Polygon p = new Polygon();
        
        p.addPoint((int) this.upperLeft.x, (int) this.upperLeft.y);
        p.addPoint((int) this.upperLeft.x, (int) this.lowerRight.y);
        p.addPoint((int) this.lowerRight.x, (int) this.lowerRight.y);
        p.addPoint((int) this.lowerRight.x, (int) this.upperLeft.y);
        
        return p;
    }
    
    @Override
    public String toString() {
        return this.upperLeft + "," + this.lowerRight;
    }
    
    public boolean isPointInside(final Vector2D point) {
        return this.upperLeft.x <= point.x && this.lowerRight.x >= point.x
            && this.upperLeft.y <= point.y && this.lowerRight.y >= point.y;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.upperLeft == null) ? 0 : this.upperLeft.hashCode());
        result = prime
                * result
                + ((this.lowerRight == null) ? 0 : this.lowerRight.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Rectangle2D other = (Rectangle2D) obj;
        if (this.upperLeft == null) {
            if (other.upperLeft != null)
                return false;
        } else if (!this.upperLeft.equals(other.upperLeft))
            return false;
        if (this.lowerRight == null) {
            if (other.lowerRight != null)
                return false;
        } else if (!this.lowerRight.equals(other.lowerRight))
            return false;
        return true;
    }
    
    public static Rectangle2D parseRectangle2D(String s) {
        String[] vectors = s.replace(" ", "").split(",");
        return new Rectangle2D(Vector2D.parseVector2D(vectors[0]), Vector2D.parseVector2D(vectors[1]));
    }
}
