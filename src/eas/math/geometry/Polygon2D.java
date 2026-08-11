/*
 * File name:    Polygon2D.java
 * Java version: 6.0
 * Author(s):    Lukas König
 * File created: 19.12.2008
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
import java.util.ArrayList;
import java.util.LinkedList;

import eas.GlobalVariables;
import eas.miscellaneous.StaticMethods;

/**
 * Implementierung eines Polygons.
 * 
 * +++++++++++++++++++++++++++++++++++++
 * ++ TODO: KLASSENVERERBUNG ändern:
 * ++
 * ++ "implements Iterable<Vector2D>" statt "extends ArrayList<Vector2D>".
 * ++
 * ++ Dafür muss die ganz unten auskommentierte Methode implementiert werden.
 * ++ Alle super(...) - Aufrufe müssen dann entfernt werden.
 * ++
 * ++ Alle mit TODO kommentierten Methoden müssen überarbeitet werden, sodass
 * ++ sie auf dem Path2D "polygonPath" arbeiten. Die Klasse Polygon2D soll in
 * ++ diesem Sinn nur eine Hülle um Path2D sein, die einige Methoden anders
 * ++ und einige zusätzlich zur Verfügung stellt.
 * ++
 * ++ Neue Kommentare sollten auf englisch geschrieben werden.
 * +++++++++++++++++++++++++++++++++++++
 * 
 * @author Lukas König
 */
public class Polygon2D implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    public static final Polygon2D NORMAL_SQUARE = new Polygon2D(0, 0, 0, 1, 1, 1, 1, 0, 0, 0);
    
    /**
     * Umschließendes Rechteck.
     * 
     * TODO: Diese Variable wird künftig nicht mehr benötigt, da der Path2D
     * seine eigene bounding box berechnet hat.
     */
    private Rectangle2D boundingBox;
    
    private ArrayList<Vector2D> points;
    
    public ArrayList<Vector2D> getPoints() {
        return this.points;
    }
    
    /**
     * Konstruktor aus Vaterklasse.
     */
    public Polygon2D() {
        points = new ArrayList<>();
    }
    
    /**
     * Creates a new Polygon with the corners from a given polygon. Note that
     * the single vectors are cloned, so the new polygon is a deep copy of the
     * old one.
     * 
     * @param c  Aus Collection.
     */
    public Polygon2D(final Polygon2D c) {
//        this.polygonPath = new java.awt.geom.Path2D.Double();
        this();
        
        for (Vector2D v : c.points) {
            this.add(new Vector2D(v));
        }
    }

    public Polygon2D(Vector2D... vecs) {
        this();
        
        for (Vector2D vec : vecs) {
            this.add(vec);
        }
    }
    
    public Polygon2D(double... xyAlternating) {
        this();
        
        for (int i = 0; i < xyAlternating.length - 1; i += 2) {
            this.add(new Vector2D(xyAlternating[i], xyAlternating[i + 1]));
        }
    }
    
    public static Polygon2D generateCircle(double radius) {
        return generateCircle(radius, 40);
    }
    
    public static Polygon2D generateCircle(double radius, final double precision) {
        Polygon2D circle = new Polygon2D();
        
        for (double d = 0; d < Math.PI * 2; d += Math.PI * 2 / precision) {
            circle.add(new Vector2D(Math.sin(d) * radius, Math.cos(d) * radius));
        }
        
        return circle;
    }

    public static Polygon2D generateCircle(double radius, Vector2D center, final double numberOfEdges) {
        Polygon2D pacman = generateCircle(radius, numberOfEdges);
        pacman.translate(center);
        return pacman;
    }

    /**
     * Berechnet einen Schnittpunkt der Strecke s1 mit dem Polygon, falls es
     * einen gibt.
     * 
     * @param s1  Die Strecke.
     * 
     * @return  Ein Schnittpunkt der Strecke mit dem Polygon, falls einer
     *          vorhanden ist, <code>null</code> sonst.
     */
    public Vector2D intersect(final LineSegment2D s1) {
        // TODO
        LineSegment2D s2 
            = new LineSegment2D(this.get(this.nPoints() - 1), this.get(0));
        boolean flip = false;
        Vector2D schnitt;
        
        for (int i = 1; i <= this.nPoints(); i++) {
            schnitt = s1.intersects(s2);
            
            if (schnitt != null) {
                return schnitt;
            }

            if (flip && i < this.nPoints()) {
                s2.setEndPoint(this.get(i));
            } else if (i < this.nPoints()) {
                s2.setBeginPoint(this.get(i));
            }
            
            flip = !flip;
        }
        
        return null;
    }

    /**
     * Berechnet alle Schnittpunkte der Strecke s1 mit dem Polygon, falls es
     * mind. einen gibt. Es werden keine Strecken als Schnittstrecken 
     * berechnet.
     * 
     * @param s1  Die Strecke.
     * 
     * @return  Alle Schnittpunkt der Strecke mit dem Polygon in einer Liste,
     *          die potenziell leer sein kann.
     */
    public LinkedList<Vector2D> intersectAll(final LineSegment2D s1) {
        // TODO
        LineSegment2D s2 
            = new LineSegment2D(this.get(this.nPoints() - 1), this.get(0));
        boolean flip = false;
        Vector2D schnitt;
        LinkedList<Vector2D> punkte = new LinkedList<Vector2D>();
        
        for (int i = 1; i <= this.nPoints(); i++) {
            schnitt = s1.intersects(s2);
            
            if (schnitt != null) {
                punkte.add(schnitt);
            }

            if (flip && i < this.nPoints()) {
                s2.setEndPoint(this.get(i));
            } else if (i < this.nPoints()) {
                s2.setBeginPoint(this.get(i));
            }
            
            flip = !flip;
        }
        
        return punkte;
    }
    
    /**
     * Checks if the two polygons (this and other) intersect. If one polygon is
     * completely inside the other one, false is returned.
     * 
     * @param other  Another polygon.
     * 
     * @return  If the polygon intersects with the other polygon.
     */
    public boolean intersect(final Polygon2D other) {
        // TODO
        if (!this.getBoundingBox().intersect(other.getBoundingBox())) {
            return false;
        }

        LineSegment2D s2 = new LineSegment2D(this.get(this.nPoints() - 1), this.get(0));
        boolean flip = false;
        Vector2D schnitt;

        for (int i = 1; i <= this.nPoints(); i++) {
            schnitt = other.intersect(s2);

            if (schnitt != null) {
                return true;
            }

            if (flip && i < this.nPoints()) {
                s2.setEndPoint(this.get(i));
            } else if (i < this.nPoints()) {
                s2.setBeginPoint(this.get(i));
            }

            flip = !flip;
        }

        return false;
    }
    
    /**
     * Fügt einen Punkt ans Ende des Polygons.
     * 
     * @param punkt  Der einzufügende Punkt.
     * 
     * @return  Ob ein Element eingefügt wurde.
     */    
    public boolean add(final Vector2D punkt) {
        this.add(this.nPoints(), punkt); // TODO: Diese Zeile weglassen.
        
        // Ab hier müsste es passen:
//        if (this.polygonPath.getCurrentPoint() == null) {
//            this.polygonPath.moveTo(punkt.x, punkt.y);
//        } else {
//            this.polygonPath.lineTo(punkt.x, punkt.y);
//        }
        return true;
    }
    
    /**
     * Fügt einen Punkt an einer bestimmten Stelle zum Polygon hinzu.
     * 
     * @param index    Der Index des Elements.
     * @param punkt  Das einzufügende Element.
     */
    public void add(final int index, final Vector2D punkt) {
        // TODO
        points.add(index, punkt);
        
        if (this.getBoundingBox() == null) {
            this.createBoundingBox();
        } else {
            this.extendBoundingBox(punkt);
        }
    }

    /**
     * @return  Die BoundingBox.
     */
    public Rectangle2D getBoundingBox() {
        /* 
         * TODO: Vermutlich einfach BoundingBox des Pfades holen und Rechteck2D 
         * erzeugen. (Am besten trotzdem als Klassenvariable halten, damit
         * nicht bei jedem Aufruf ein neues Objekt erzeugt werden muss.)
         */

        if (this.boundingBox == null) {
            this.createBoundingBox();
        }
        return this.boundingBox;
    }

    /**
     * Erzeugt den umschließenden Rahmen neu.
     */
    private void createBoundingBox() {
        /* 
         * TODO: Diese Methode kann künftig entfallen, da ein Path seine 
         * BoundingBox mitbringt.
         */
        
        if (this.nPoints() == 0) {
            return;
        }
        
        Vector2D punkt = this.get(0);
        
        this.boundingBox = new Rectangle2D(punkt, punkt);
        
        for (int i = 1; i < this.nPoints(); i++) {
            this.extendBoundingBox(this.get(i));
        }
    }
    
    /**
     * Erweitert die BoundingBox um einen Punkt.
     * 
     * @param punkt  Der einzuschließende Punkt.
     */
    private void extendBoundingBox(final Vector2D punkt) {
        /* 
         * TODO: Diese Methode kann künftig entfallen, da ein Path seine 
         * BoundingBox mitbringt.
         */

        if (punkt.x < this.boundingBox.upperLeftCorner().x) {
            this.boundingBox.setLeft(punkt.x);
        } else if (punkt.x > this.boundingBox.lowerRightCorner().x) {
            this.boundingBox.setRight(punkt.x);
        }
        
        if (punkt.y < this.boundingBox.upperLeftCorner().y) {
            this.boundingBox.setTop(punkt.y);
        } else if (punkt.y > this.boundingBox.lowerRightCorner().y) {
            this.boundingBox.setBottom(punkt.y);
        }
    }

    /**
     * @param pol  Polygon.
     * 
     * @return  Immer <code>true</code>.
     */
    public boolean addAll(final Polygon2D pol) {
        for (Vector2D v : pol.points) {
            this.add(v);
        }
        
        return true;
    }

    /**
     * löscht alle Punkte des Polygons.
     */
    public void clear() {
        points.clear();
//        this.polygonPath.reset();
        this.boundingBox = null;
    }

    /**
     * löscht den Punkt mit der angegebenen Nummer. Vorsicht, sollte nicht
     * häufig alternierend mit add-Operationen eingesetzt werden, da nach
     * remove beim nächsten add die boundingBox neu berechnet werden muss. 
     * 
     * @param index  Die Nummer.
     * 
     * @return  Das entfernte Element.
     */
    public Vector2D remove(final int index) {
        // TODO
        this.boundingBox = null;
        return points.remove(index);
    }
    
    /**
     * Verschiebt das Polygon um den angegebenen Vektor.
     * 
     * @param vek  Der Verschiebevektor.
     */
    public Polygon2D translate(final Vector2D vek) {
        // TODO: Sollte über eine Affine Transformation des Pfades erfolgen.
        
        for (Vector2D pVek : this.points) {
            pVek.translate(vek);
        }
        
        if (this.boundingBox != null) {
            this.boundingBox.translate(vek);
        }
        
        return this;
    }
    
    /**
     * Skaliert das Polygon um die angegebene X- und Y-Richtung und den 
     * angegebenen Mittelpunkt.
     * 
     * @param skal   Der Skalierungsvektor.
     * @param mitte  Der Mittelpunkt des Skalierens.
     */
    public Polygon2D scale(final Vector2D mitte, final Vector2D skal) {
        // TODO: Sollte über eine Affine Transformation des Pfades erfolgen.

        for (Vector2D pVek : this.points) {
            pVek.scale(mitte, skal);
        }
        this.createBoundingBox();
        
        return this;
    }
    
    /**
     * Dreht das Polygon um den angegebenen Winkel in RAD und den angegebenen 
     * Mittelpunkt.
     * 
     * @param mitte   Der Mittelpunkt.
     * @param winkel  Der Drehwinkel.
     * @return 
     */
    public Polygon2D rotate(final Vector2D mitte, final double winkel) {
        // TODO: Sollte über eine Affine Transformation des Pfades erfolgen.

        for (Vector2D pVek : this.points) {
            pVek.rotate(mitte, winkel);
        }
        this.createBoundingBox();
        
        return this;
    }
    
    /**
     * @param o  Objekt.
     * 
     * @return  Ob geändert.
     */
    public boolean remove(final Object o) {
        // TODO: Diese Methode komplett löschen.
        points.remove(o);
        return true;
    }

    /**
     * löscht Objekte in einem Bereich.
     * 
     * @param fromIndex  Von.
     * @param toIndex    Bis.
     */
    protected void removeRange(final int fromIndex, final int toIndex) {
        // TODO: Diese Methode komplett löschen.
        for (int i = fromIndex; i < toIndex; i++) {
            this.remove(fromIndex);
        }
    }
    
    /**
     * Setzt einen Punkt neu. Vorsicht, ineffizient; sollte nur zu Testzwecken
     * oder aus Bequemlichkeit, wenn Zeit keine Rolle spielt, eingesetzt 
     * werden (siehe auch Kommentar zu remove).
     * 
     * @param index    Der neu zu setzende Index.
     * @param element  Das neue Element.
     * 
     * @return  Das Element, das vorher an der Position war. Wenn das Element
     *          nicht gesetzt werden konnte, wird <code>null</code> 
     *          zurückgegeben.
     */
    public Vector2D set(final int index, final Vector2D element) {
        // TODO
        Vector2D alt = this.get(index);
        
        this.remove(index);
        this.add(index, element);
        
        return alt;
    }
    
    /**
     * @return  Die Anzahl der Punkte im Polygon.
     */
    public int nPoints() {
        // TODO
        return this.points.size();
    }

    /**
     * @param num  Die Nummer des Eckpunkts, der zurückgegeben werden soll.
     * 
     * @return  Der num-te Eckpunkt des Polygons.
     */
    public Vector2D get(final int num) {
        // TODO
        return points.get(num);
    }

    /**
     * Erzeugt ein Java-Polygon aus <code>this</code> ohne zu skalieren oder
     * zu verschieben.
     * 
     * @return  Polygon.
     */
    public Polygon toPol() {
        return this.toPol(1, Vector2D.NULL_VECTOR);
    }

    /**
     * Erzeugt ein Java-Polygon aus <code>this</code>.
     * 
     * @param skalierung  Die Skalierung des Polygons.
     * @param versch      Die Verschiebung des Polygons NACH der Skalierung.
     * 
     * @return  Polygon.
     */
    public Polygon toPol(final double skalierung, final Vector2D versch) {
        // TODO
        
        Polygon pol = new Polygon();
        
        for (Vector2D punkt : points) {
            pol.addPoint((int) (punkt.x * skalierung + versch.x), 
                         (int) (punkt.y * skalierung + versch.y));
        }
        
        return pol;
    }

    /**
     * Normalisiert das aktuelle Objekt, so dass der Abstand 
     * zwischen zwei in der Liste benachbarten Punkten immer dem 
     * Durchschnittsabstand der Punkte in der ursprünglichen Liste entspricht,
     * wobei der Kurvenverlauf "möglichst nah" am Verlauf der ursprünglichen
     * Kurve liegt. Dabei kann die Anzahl der Punkte sich um 1 erhöhen.
     * 
     * @return  Die normalisierte Kurve.
     */
    public Polygon2D normalize() {
        /*
         * TODO: Diese Methode sollte nur auf dem PolygonPath arbeiten.
         * Implementierung erst, wenn der Rest funktioniert.
         */
        Polygon2D normArr = new Polygon2D();
        ArrayList<Double> distances = new ArrayList<Double>(this.nPoints());
        Vector2D latestV;
        double currentDist;
        double latestDist;
        Vector2D currentDir;
        double avgDistance;
        int i = 0;
        
        if (this.nPoints() <= 1) {
            return this;
        }
        
        latestV = this.get(0);
        latestDist = 0;
        for (Vector2D currentV : this.points) {
            currentDist = currentV.distance(latestV) + latestDist;
            distances.add(currentDist);
            
            latestDist = currentDist;
            latestV = currentV;
        }

        avgDistance = distances.get(distances.size() - 1) 
                    / (distances.size() - 1);
        
        Vector2D currentV = new Vector2D(this.get(0));
        normArr.add(currentV);
        currentDist = 0;
        while (i < this.nPoints()) {
            if (i + 1 < this.nPoints()) {
                currentDir = new Vector2D(this.get(i + 1));
                currentDir.sub(this.get(i));
                currentDir.setLength(avgDistance);
                
                while (currentDist < distances.get(i + 1)) {
                    currentV = new Vector2D(currentV);
                    currentV.translate(currentDir);
                    normArr.add(currentV);
                    currentDist += avgDistance;
                }
            }
            
            i++;
        }

        if (normArr.nPoints() - this.nPoints() > 1) {
            StaticMethods.logWarning(
                    "Normalisierung lieferte zu viele / wenige Punkte: " 
                    + normArr.nPoints() + " vs. " + this.nPoints(), 
                    GlobalVariables.getParameters());
        }

        return normArr;
    }

    /**
     * Berechnet, ob der übergebene Punkt sich innerhalb der Grenzen des
     * Polygons befindet.
     * Algorithmus:
     * http://www-lehre.informatik.uni-osnabrueck.de/~cg/2002/skript/node43.html
     * 
     * @param punkt  Der Punkt, dessen Eigenschaft überprüft werden soll.
     * 
     * @return       Ob der Punkt innerhalb des Polygons liegt.
     */
    public boolean isPointWithin(final Vector2D punkt) {
        /*
         * TODO: Diese Methode durch folgende Zeile ersetzen, wenn der Rest der
         * Klasse tut:
         * 
         * return this.polygonPath.contains(new Point2D.Double(punkt.x, punkt.y));
         */
        
        boolean inside = false;
        double x1 = this.get(this.nPoints() - 1).x;
        double y1 = this.get(this.nPoints() - 1).y;
        double x2 = this.get(0).x;
        double y2 = this.get(0).y;
        boolean startUeber;
        boolean endeUeber;
        
        if (y1 >= punkt.y) {
            startUeber = true;
        } else {
            startUeber = false;
        }
        
        for (int i = 1; i <= this.nPoints(); i++) {
            if (y2 >= punkt.y) {
                endeUeber = true;
            } else {
                endeUeber = false;
            }
          
            if (startUeber != endeUeber) {
                if ((y2 - punkt.y) * (x2 - x1) <= (y2 - y1) * (x2 - punkt.x)) {
                    if (endeUeber) {
                        inside = !inside;
                    }
                } else {
                    if (!endeUeber) {
                        inside = !inside;
                    }
                }
            }

            if (i < this.nPoints()) {
                startUeber = endeUeber;
                y1 = y2;
                x1 = x2;
                x2 = this.get(i).x;
                y2 = this.get(i).y;
            }
        }
        
        return inside;
    }
    
    /**
     * @return  Mittelpunkt des Polygons.
     */
    public Vector2D centerPoint() {
        // TODO
        
        return new Vector2D(
                this.getBoundingBox().upperLeftCorner().x 
                    + this.getBoundingBox().getWidth() / 2,
                this.getBoundingBox().upperLeftCorner().y
                    + this.getBoundingBox().getHeight() / 2);
    }
    
    /**
     * Gibt die Position des (ersten) Vektors zurück, der genau dem 
     * übergebenen Objekt entspricht.
     * 
     * @param vek  Der zu vergleichende Vektor.
     * 
     * @return  Die (erste) Position des Vektors. Falls nicht vorhanden: -1.
     */
    public int realIndexOf(final Vector2D vek) {
        // TODO
        
        for (int i = 0; i < this.nPoints(); i++) {
            if (this.get(i) == vek) {
                return i;
            }
        }
        
        return -1;
    }
    
    @Override
    public Object clone() {
        Polygon2D copy = new Polygon2D();
        for (Vector2D v : points) {
            copy.add(new Vector2D(v));
        }

        return copy;
    }

    /**
     * Returns a polygon in counterclockwise ordering of the vertices for all
     * star-shaped polygons (http://en.wikipedia.org/wiki/Star-shaped_polygon).
     * For other polygons the only assurance is that the step from the first
     * vertex to the second is in counterclockwise ordering.
     * <BR>
     * Note that <code>this</code> is returned if the polygon is already 
     * ordered counterclockwise.
     * 
     * @return
     */
    public Polygon2D getCounterClockwiseOrdered() {
        // TODO
        
        Vector2D richt0 = new Vector2D(this.get(0));
        Vector2D richt1 = new Vector2D(this.get(1));
        richt0.sub(this.centerPoint());
        richt1.sub(this.centerPoint());
        if (richt0.orientation(richt1)) {
            return this;
        } else {
            return this.getReverted();
        }
    }
    
    /**
     * @return  The same polygon as <code>this</code> except for a reverted
     *          ordering of the vertices.
     */
    public Polygon2D getReverted() {
        // TODO
        
        Polygon2D p = new Polygon2D();
        for (int i = this.nPoints() - 1; i >= 0; i--) {
            p.add(this.get(i));
        }
        return p;
    }

    /*
     * TODO: Diese Methode muss einen Iterator auf die Vector2D-Elemente
     * des Polygons zurückliefern. Da der Path2D so etwas nicht direkt liefern
     * kann, sollte hier eine erste Implementierung eine ArrayList<Vector2D>
     * mit den Eckpunkten des Polygons erzeugen und deren Iterator 
     * zurückliefern. Ich werde mir selbst Gedanken dazu machen, wie man das
     * dann effizienter machen kann.
     */
//    public Iterator<Vector2D> iterator() {
//        return null;
//    }

    public void translateRootPointToMiddle() {
        Vector2D v = new Vector2D(this.centerPoint());
        v.mult(-1);
        this.translate(v);
    }

    private void insertNodesOnExistingEdge(int edg1Num, double nodesToInsert) {
        int edg2Num = edg1Num + 1;
        
        if (edg2Num >= points.size()) {
            edg2Num = 0;
        }
        
        Vector2D dir = new Vector2D(this.get(edg2Num));
        dir.sub(this.get(edg1Num));
        double length = this.get(edg2Num).distance(this.get(edg1Num));
        dir.setLength(length / (nodesToInsert + 1));
        Vector2D vec = new Vector2D(this.get(edg1Num));
        
        for (int i = 0; i < nodesToInsert; i++) {
            vec.translate(dir);
            this.add(edg1Num + i + 1, new Vector2D(vec));
        }
    }
    
    /**
     * Inserts the requested number of nodes on any edge of the polygon.
     * Therefore, the number of nodes in the polygon gets multiplied by that
     * number. The shape of the polygon stays unchanged. The new nodes of any
     * edge are inserted with a constant distance to each other and the two
     * original nodes of the edge.
     * 
     * @param nodesToInsert  Number of nodes to insert.
     */
    public void insertNodesOnExistingEdges(int nodesToInsert) {
        int size = points.size();
        
        for (int i = size - 1; i >= 0; i--) {
            this.insertNodesOnExistingEdge(i, nodesToInsert);
        }
    }

    /**
     * Check whether or not the polygon is convex.
     * 
     * @return true iff this polygon is convex
     */
    public boolean isConvex() {
        // check if all angles are smaller or equal to 180 degrees
        int l = points.size();
        
        for ( int i = 0; i < l; i++ ) {
            Vector2D x = this.get(i);
            Vector2D y = this.get((i + 1) % l);
            Vector2D z = this.get((i + 2) % l);
            
            // does the 3d cross product point up or down?
            if ((z.x - x.x) * (y.y - x.y) - (y.x - x.x) * (z.y - x.y) >= 0) {
                return false;
            }
        }
        
        return true;
    }
    
    static private boolean isInside(Vector2D a, Vector2D b, Vector2D c) {
        return (a.x - c.x) * (b.y - c.y) > (a.y - c.y) * (b.x - c.x);
    }
 
    static private Vector2D intersection(Vector2D p, Vector2D q, Vector2D a, Vector2D b) {
        double A1 = q.y - p.y;
        double B1 = p.x - q.x;
        double C1 = A1 * p.x + B1 * p.y;
 
        double A2 = b.y - a.y;
        double B2 = a.x - b.x;
        double C2 = A2 * a.x + B2 * a.y;
 
        double det = A1 * B2 - A2 * B1;
        double x = (B2 * C1 - B1 * C2) / det;
        double y = (A1 * C2 - A2 * C1) / det;
 
        return new Vector2D(x, y);
    }
    
    /**
     * The Sutherland-Hodgman clipping algorithm finds the polygon that is the 
     * intersection between an arbitrary polygon (the "subject polygon") and a 
     * convex polygon (the "clip polygon"). It is used in computer graphics 
     * (especially 2D graphics) to reduce the complexity of a scene being 
     * displayed by eliminating parts of a polygon that do not need to be displayed.
     * This method takes two polygons (this and a parameter), each of which can be 
     * - and one of which has to be convex. Both this and the parameter polygon
     * are not changed in the process, the resulting intersection polygon is 
     * returned.
     * 
     * @author http://rosettacode.org/wiki/Sutherland-Hodgman_polygon_clipping
     */
    public Polygon2D clipPolygonOneConvex(Polygon2D p2) {
        if (!p2.isConvex()) {
            if (this.isConvex()) {
                return p2.clipPolygonOneConvex(this);
            } else {
                throw new RuntimeException("At least one polygon has to be convex.");
            }
        }
        
        Polygon2D result = new Polygon2D(this);
        
        int len = p2.points.size();
        for (int i = 0; i < len; i++) {
 
            int len2 = result.points.size();
            Polygon2D input = new Polygon2D(result);
            result = new Polygon2D();
 
            Vector2D A = new Vector2D(p2.get((i + len - 1) % len));
            Vector2D B = new Vector2D(p2.get(i));
 
            for (int j = 0; j < len2; j++) {
 
                Vector2D P = new Vector2D(input.get((j + len2 - 1) % len2));
                Vector2D Q = new Vector2D(input.get(j));
 
                if (isInside(A, B, Q)) {
                    if (!isInside(A, B, P))
                        result.add(intersection(P, Q, A, B));
                    result.add(Q);
                } else if (isInside(A, B, P))
                    result.add(intersection(P, Q, A, B));
            }
        }
        
        return result;
    }
}
