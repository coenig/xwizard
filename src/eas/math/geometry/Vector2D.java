/*
 * Datei:            Vector2D.java
 * Autor(en):        Lukas König
 * Java-Version:     6.0
 * Erstellt (vor): 05.05.2007
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

import java.awt.Point;
import java.io.Serializable;

/**
 * Ein geometrischer 2D-Vektor.
 *
 * @author Lukas König
 */
public class Vector2D implements Serializable, Comparable<Vector2D> {

    /**
     * Die Versions-ID (07.05.07).
     */
    private static final long serialVersionUID = 2753237232482944204L;

    /**
     * Der Nullvektor (0, 0).
     */
    public static final Vector2D NULL_VECTOR = new Vector2D(0, 0);

    /**
     * X-Richtung.
     */
    public double x;

    /**
     * Y-Richtung.
     */
    public double y;

//private static long counter = 0;
//private static TreeMap<String, Integer> callers = new TreeMap<String, Integer>();
    
    /**
     * Konstruktor, der die Richtungen initialisiert.
     *
     * @param xx  X-Richtung.
     * @param yy  Y-Richtung
     */
    public Vector2D(
            final double xx,
            final double yy) {
        this.x = xx;
        this.y = yy;
    }

    /**
     * Initialisiert den Vektor über Daten von einem bereits vorhandenen 
     * Vektor.
     * 
     * @param anderer  Der andere Vektor.
     */
    public Vector2D(final Vector2D anderer) {
        this(anderer.x, anderer.y);
    }

    /**
     * Initialisiert den Vektor über Daten von einem bereits vorhandenen 
     * Point.
     * 
     * @param anderer  Der Point.
     */
    public Vector2D(final Point anderer) {
        this(anderer.x, anderer.y);
    }

    /**
     * Setzt die Richtung.
     *
     * @param xx  X-Richtung.
     * @param yy  Y-Richtung
     */
    public void setCoordinates(final double xx,
                           final double yy) {
        this.x = xx;
        this.y = yy;
    }

    /**
     * Addiert einen anderen Vektor auf diesen.
     * 
     * @param anderer  Der andere Vektor.
     */
    public Vector2D translate(final Vector2D anderer) {
        this.x += anderer.x;
        this.y += anderer.y;
        
        return this;
    }

    public Vector2D add(final double x, final double y) {
        this.x += x;
        this.y += y;
        
        return this;
    }

    public Vector2D sub(final double x, final double y) {
        this.x -= x;
        this.y -= y;
        
        return this;
    }

    /**
     * Subtrahiert einen anderen Vektor von diesem.
     * 
     * @param anderer  Der andere Vektor.
     */
    public Vector2D sub(final Vector2D anderer) {
        this.x -= anderer.x;
        this.y -= anderer.y;
        
        return this;
    }

    /**
     * Addiert einen anderen Vektor zu diesem.
     * 
     * @param anderer  Der andere Vektor.
     */
    public Vector2D add(final Vector2D anderer) {
        this.x += anderer.x;
        this.y += anderer.y;
        
        return this;
    }

    /**
     * Gibt das Produkt dieses Vektors mit einem anderen zurück.
     * 
     * @param anderer  Der andere Vektor.
     * 
     * @return  Das Produkt der beiden Vektoren.
     */
    public double prod(final Vector2D anderer) {
        return this.x * anderer.x + this.y * anderer.y;
    }
    
    /**
     * @param anderer  Der andere Vektor, zu dem die Drehrichtung berechnet
     *                 werden soll.
     * @return  Ob die Drehrichtung gegen den Uhrzeigersinn ist.
     */
    public boolean orientation(final Vector2D anderer) {
        double orient = this.x * anderer.y - this.y * anderer.x;

        return orient > 0;
    }
    
    /**
     * Multipliziert den Vektor  mit einer Konstanten.
     * 
     * @param constant  Die Konstante.
     */
    public Vector2D mult(final double constant) {
        this.x *= constant;
        this.y *= constant;
        return this;
    }
    
    /**
     * Dividiert den Vektor  durch eine Konstante.
     * 
     * @param constant  Die Konstante.
     */
    public Vector2D div(final double constant) {
        this.x /= constant;
        this.y /= constant;
        return this;
    }
    
    /**
     * Die Länge des Vektors.
     * 
     * @return  Die Länge.
     */
    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }
    
    /**
     * Normiert den Vektor.
     */
    public Vector2D normalize() {
        if (this.length() != 0) {
            this.div(this.length());
        }
        
        return this;
    }
    
    /**
     * Stellt die Länge des Vektors auf den übergebenen Parameter ein.
     * 
     * @param l  Die festzulegende Länge.
     */
    public Vector2D setLength(final double l) {
        this.normalize();
        this.mult(l);
        
        return this;
    }

    /**
     * Verändert den Vektor zu einem orthogonalen Vektor.
     */
    public void ortho() {
        double xOld = this.x;
        double yOld = this.y;
        
        this.x = yOld;
        this.y = -xOld;
    }
    
    /**
     * Berechnet den Abstand von diesem Vektor (als Punkt interpretiert)
     * zu einem anderen.
     * 
     * @param anderer  Der andere Punkt-Vektor.
     * 
     * @return  Der Abstand der beiden Punkte zueinander.
     */
    public double distance(final Vector2D anderer) {
        return Math.sqrt(Math.pow(this.x - anderer.x, 2)
                         + Math.pow(this.y - anderer.y, 2));
    }
    
    /**
     * Berechnet das Vektorprodukt.
     * 
     * @param anderer  Der andere Vektor.
     * 
     * @return  Das Vektorprodukt.
     */
    public double vecProd(final Vector2D anderer) {
        double vekP = this.x * anderer.x + this.y * anderer.y;
        
        return vekP;
    }
   
    /**
     * @param p1  Erster Punkt des Rechtecks.
     * @param p2  Zweiter Punkt des Rechecks.
     * @return  Ob sich der Vektor in der Ebene innerhalb des angegebenen 
     *          Rechtecks (ohne Rand) befindet,
     */
    public boolean isWithinRectangle(final Vector2D p1,
            final Vector2D p2) {
        
        return ((this.x >= p1.x && this.x <= p2.x
                || this.x <= p1.x && this.x >= p2.x)
                && (this.y >= p1.y && this.y <= p2.y
                   || this.y <= p1.y && this.y >= p2.y)); 
    }

    /**
     * @param p1  Erster Punkt des Rechtecks.
     * @param p2  Zweiter Punkt des Rechecks.
     * @return  Ob sich der Vektor in der Ebene innerhalb des angegebenen 
     *          Rechtecks MIT RAND befindet,
     */
    public boolean isWithinRectangleOR(
            final Vector2D p1,
            final Vector2D p2) {
        
        return ((this.x >= p1.x && this.x <= p2.x
                || this.x <= p1.x && this.x >= p2.x)
                && (this.y >= p1.y && this.y <= p2.y
                   || this.y <= p1.y && this.y >= p2.y)); 
    }

    /**
     * Skaliert den Vektor um die angegebene X- und Y-Richtung und den 
     * angegebenen Mittelpunkt.
     * 
     * @param skal   Der Skalierungsvektor.
     * @param mitte  Der Mittelpunkt des Skalierens.
     */
    public void scale(final Vector2D mitte, final Vector2D skal) {
        this.sub(mitte);
        this.x *= skal.x;
        this.y *= skal.y;
        this.translate(mitte);
    }
    
    /**
     * Rotates the vector for a given angle in RAD in counterclockwise
     * direction. Note that {@code this} is rotated in the process, NOT
     * a copy!
     * 
     * @param angleRAD  The angle in RAD.
     * @return  The rotated vector.
     */
    public Vector2D rotate(final double angleRAD) {
        return this.rotate(new Vector2D(0, 0), angleRAD);
    }

    /**
     * Dreht den Punkt um den angegebenen Winkel (in RAD) und den angegebenen
     * Mittelpunkt. Mitte (0, 0) entspricht einer Drehung um den Ursprung.
     * 
     * @param mitte   Der Mittelpunkt.
     * @param winkel  Der Drehwinkel.
     */
    public Vector2D rotate(final Vector2D mitte, final double winkel) {
        double winkelAkt = winkel;
        while (winkelAkt < 0) {
            winkelAkt += Math.PI * 2;
        }
        while (winkelAkt > Math.PI * 2) {
            winkelAkt -= Math.PI * 2;
        }
        
        Vector2D zwisch;
        this.sub(mitte);
        zwisch = new Vector2D(this);
        zwisch.x = Math.cos(winkel) * this.x - Math.sin(winkel) * this.y;
        zwisch.y = Math.sin(winkel) * this.x + Math.cos(winkel) * this.y;
        this.x = zwisch.x;
        this.y = zwisch.y;
        this.translate(mitte);
        return this;
    }

    /**
    * Die Hashcode-Methode.
    * 
    * @return  Der Hashcode.
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        // Damit die int-Koordinaten richtig gesetzt sind.
        this.setCoordinates(this.x, this.y);
        result = prime * result + (int) this.x;
        result = prime * result + (int) this.y;
        return result;
    }

    /**
    * Die Equals-Methode. Dabei werden die gerundeten Werte der Koordinaten
    * verglichen.
    * 
    * @param obj  Das andere Objekt.
    * 
    * @return  Ob this mit dem anderen Objekt identisch ist.
    */
    @Override
    public boolean equals(final Object obj) {
        // Damit die int-Koordinaten richtig gesetzt sind.
        this.setCoordinates(this.x, this.y);
        
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vector2D other = (Vector2D) obj;
        if (((int) this.x) != ((int) other.x)) {
            return false;
        }
        if (((int) this.y) != ((int) other.y)) {
            return false;
        }
        return true;
    }

    /**
     * Erzeugt einen Point aus den gerundeten Koordinaten.
     * 
     * @return  Der Point.
     */
    public Point toPoint() {
        // Damit die int-Koordinaten richtig gesetzt sind.
        this.setCoordinates(this.x, this.y);
        return new Point((int) this.x, (int) this.y);
    }
    
    /**
     * Berechnet den Winkel zwischen dem Aktuellen und dem anderen Vektor.
     * 
     * @param andererVek  Der andere Vektor.
     * 
     * @return  Winkel zwischen den Vektoren
     */
    public double angle(final Vector2D andererVek) {
        return Math.acos(
                this.prod(andererVek) / (this.length() * andererVek.length()));
    }

    /**
     * Die toString-Methode.
     * 
     * @return  Ausgabe.
     */
    @Override
    public String toString() {
        return "" + this.x + "/" + this.y + "";
    }

    /**
     * Klont diesen Vektor.
     * 
     * @return  Eine Kopie dieses Vektors.
     * 
     * @throws CloneNotSupportedException  Kein Klonen möglich.
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Vector2D(this);
    }
    
    /**
     * Parst einen String zu einem Vektor2D.
     * 
     * @param vekString  Der String.
     * 
     * @return  Der Vektor2D.
     */
    public static Vector2D parseVector2D(final String stringVector) {
        double x = 0;
        double y = 0;
        String[] vekStrings = stringVector.replaceAll(" ", "").split("/");

        try {
            x = Double.parseDouble(vekStrings[0]);
        } catch (Exception e) {}
        try {
            y = Double.parseDouble(vekStrings[1]);
        } catch (Exception e) {}
        
        return new Vector2D(x, y);
    }

    @Override
    public int compareTo(Vector2D o) {
        if (o == null) {
            return 0;
        }
        
        double thisDist = this.distance(Vector2D.NULL_VECTOR); 
        double oDist = o.distance(Vector2D.NULL_VECTOR);
        
        if (thisDist < oDist) {
            return -1;
        }
        
        if (thisDist > oDist) {
            return 1;
        }
        
        return 0;
    }
}