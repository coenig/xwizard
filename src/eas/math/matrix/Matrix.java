/*
 * File name:       Matrix.java
 * Author(s):       Lukas König, free internet code.
 * Java version:    6.0
 * Generation date: 08 April 2010
 *
 * (c) This file is protected by Creative Commons by-nc-sa license. Any 
 * altered or further developed versions of this file have to meet the 
 * agreements stated by the license conditions. 
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

package eas.math.matrix;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import eas.miscellaneous.StaticMethods;
import eas.miscellaneous.datatypes.Integer2D;
import eas.startSetup.parameterDatatypes.ArrayListDouble;


/**
 * @author Lukas König, Thomas Darimont
 * 
 * Matrix multiplication from Thomas Darimont:
 * http://www.tutorials.de/forum/java/164941-n-x-n-matrizenmultiplikation-aber-wie.html
 */
public class Matrix extends GeneralMatrix implements Serializable {

    private static final long serialVersionUID = 1381109405948178031L;

    /**
     * Die Felder der Matrix.
     */
    private double[][] matrix;
    
    /**
     * @return Returns the matrix.
     */
    public double[][] getRawMatrix() {
        return this.matrix;
    }
    
    /**
     * Zeilenüberschriften.
     */
    private Integer2D[] zeilenUeberschr;
    
    /**
     * Spaltenüberschriften.
     */
    private Integer2D[] spaltenUeberschr;

    /* ************* Constructors ************* */

    /**
     * Generates matrix from text file.
     * 
     * @param file  The text file.
     */
    public Matrix(final File file) {
        List<String> dateiInhalt = StaticMethods.readTextArrayFromFile(file, null, true);
        ArrayList<ArrayList<String>> geleseneMatrix = new ArrayList<ArrayList<String>>();

        // Text aus Datei formatieren.
        for (String s : dateiInhalt) {
            String[] aufgeteilt = s.replace(" ", "").split("\\|");
            ArrayList<String> zeile = new ArrayList<String>();
            for (int i = 0; i < aufgeteilt.length; i++) {
                zeile.add(aufgeteilt[i]);
            }
            geleseneMatrix.add(zeile);
        }
        
        // Matrix erzeugen.
        int x = geleseneMatrix.get(1).size() - 3;
        int y = geleseneMatrix.size() - 2;
        this.matrix = new double[x][y];
        this.zeilenUeberschr = new Integer2D[this.getRowCount()];
        this.spaltenUeberschr = new Integer2D[this.getColumnCount()];

        // Einträge.
        for (int i = 0; i < this.getColumnCount(); i++) {
            for (int j = 0; j < this.getRowCount(); j++) {
                this.set(i, j, Double.parseDouble(geleseneMatrix.get(j + 2).get(i + 3)));
            }
        }
        
        // Überschriften.
        for (int i = 0; i < this.getColumnCount(); i++) {
            this.spaltenUeberschr[i] = Integer2D.parseInteger2D(geleseneMatrix.get(1).get(i + 3));
        }
        for (int j = 0; j < this.getRowCount(); j++) {
            this.zeilenUeberschr[j] = Integer2D.parseInteger2D(geleseneMatrix.get(j + 2).get(1));
        }
    }

    /**
     * Generates empty matrix of a certain size.
     * 
     * @param x  The number of columns.
     * @param y  The number of rows.
     */
    public Matrix(final int x, final int y) {
        this.matrix = new double[x][y];
        this.zeilenUeberschr = new Integer2D[this.getRowCount()];
        this.spaltenUeberschr = new Integer2D[this.getColumnCount()];
    }
    
    /**
     * Generates matrix copying an existing matrix.
     * 
     * @param mat  The existing matrix.
     */
    public Matrix(final double[][] mat) {
        if (mat == null) {
            throw new RuntimeException("Matrix darf nicht null sein.");
        }
        if (mat.length == 0) {
            this.matrix = new double[0][0];
        }
        this.matrix = new double[mat.length][mat[0].length];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                this.matrix[i][j] = mat[i][j];
            }
        }
        this.zeilenUeberschr = new Integer2D[this.getRowCount()];
        this.spaltenUeberschr = new Integer2D[this.getColumnCount()];
    }
    
    public Matrix(final Matrix matrix) {
        this(matrix.matrix);
        
        for (int i = 0; i < matrix.getColumnCount(); i++) {
            this.spaltenUeberschr[i] = matrix.spaltenUeberschr[i];
        }
        for (int j = 0; j < matrix.getRowCount(); j++) {
            this.zeilenUeberschr[j] = matrix.zeilenUeberschr[j];
        }
    }

    /* ************* EO Constructors ************* */

    public void storeMatrix(final File file) {
        String path = null; 
        if (file.getParent() != null) {
            path = file.getParentFile().toString();
        } else {
            path = "";
        }
        StaticMethods.saveTextToFile(
                path, 
                file.getName(), 
                this.toStringFormatiert());
    }
    
    public static Matrix rowSum(final Matrix mat) {
        Matrix result = new Matrix(1, mat.getRowCount());
        
        for (int j = 0; j < mat.getRowCount(); j++) {
            for (int i = 0; i < mat.getColumnCount(); i++) {
                result.set(0, j, result.get(0, j) + mat.get(i, j)); 
            }
        }
        
        return result;
    }
    
    public static Matrix columnSum(final Matrix mat) {
        Matrix result = new Matrix(mat.getColumnCount(), 1);
        
        for (int j = 0; j < mat.getRowCount(); j++) {
            for (int i = 0; i < mat.getColumnCount(); i++) {
                result.set(i, 0, result.get(i, 0) + mat.get(i, j)); 
            }
        }
        
        return result;
    }

    public String createMatlabString() {
        String s = "";
        
        int hoehe = this.getRowCount();
        int breite = this.getColumnCount();
        
        s += "[";
        for (int i = 0; i < hoehe; i++) {
            String semikolon = "";
            if (i < hoehe - 1) {
                semikolon = ";";
            }
            s += "[";
            for (int j = 0; j < breite; j++) {
                String komma = "";
                if (j < breite - 1) {
                    komma = ",";
                }
                s += this.get(j, i) + komma;
            }
            s += "]" + semikolon;
        }
        s += "]";
        
        return s;
    }
    
    public void revertRows() {
        double[][] newMatrix = new double[this.matrix.length][this.matrix[0].length];
        Integer2D[] newZeilen = new Integer2D[this.zeilenUeberschr.length];
        
        for (int i = 0; i < newMatrix.length; i++) {
            for (int j = 0; j < newMatrix[0].length; j++) {
                newMatrix[newMatrix.length - i - 1][j] = this.matrix[i][j];
            }
            
            newZeilen[newMatrix.length - i - 1] = this.zeilenUeberschr[i];
        }
        
        this.zeilenUeberschr = newZeilen;
        
        this.matrix = newMatrix;
    }
    
    public void revertColumns() {
        double[][] newMatrix = new double[this.matrix.length][this.matrix[0].length];
        Integer2D[] newSpalten = new Integer2D[this.spaltenUeberschr.length];
        
        for (int i = 0; i < newMatrix.length; i++) {
            for (int j = 0; j < newMatrix[0].length; j++) {
                newMatrix[i][newMatrix[0].length - j - 1] = this.matrix[i][j];
                newSpalten[newMatrix.length - j - 1] = this.spaltenUeberschr[j];
            }
        }
        
        this.spaltenUeberschr = newSpalten;
        
        this.matrix = newMatrix;
    }
    
    /**
     * Performs a rounding operation on the values of all cells of the matrix.
     * 
     * @param digits  Number of digits after decimal point.
     */
    public Matrix round(final int digits) {
        if (this.matrix.length == 0) {
            return this;
        }
        
        for (int i = 0; i < this.matrix.length; i++) {
            for (int j = 0; j < this.matrix[0].length; j++) {
                this.matrix[i][j] = Math.round(this.matrix[i][j]
                        * Math.pow(10, digits))
                        / Math.pow(10, digits);
            }
        }
        
        return this;
    }

    /**
     * Matrix multiplication (by Thomas Darimont).
     * 
     * @param other  The matrix to multiply with.
     * 
     * @return this x other.
     */
    public void mult(final Matrix other) {
        if (this.matrix[0].length != other.getColumnCount()) {
            throw new IllegalArgumentException(
               "Zeilenanzahl von andere muss der "
               + "Spaltenanzahl von this entsprechen");
        }

        double[][] c = new double[this.matrix.length][other.getRowCount()];

        for (int i = 0; i < this.getColumnCount(); i++) {
            for (int j = 0; j < other.getRowCount(); j++) {
                for (int k = 0; k < this.getRowCount(); k++) {
                    c[i][j]  += this.get(i, k) * other.get(k, j);
                }
            }
        }

        this.matrix = c;
    }
    
    /**
     * Raise to a power.
     * 
     * @param n  The power to raise to.
     */
    public void pow(final int n) {
        if (this.getColumnCount() != this.getRowCount()) {
            throw new RuntimeException("Potenzieren nur bei quadratischen"
                    + "Matrizen möglich");
        }
        
        Matrix b = new Matrix(this.matrix);
        
        if (n == 0) {
            this.einheitsMatrix();
        }
        
        for (int i = 1; i < n; i++) {
            this.mult(b);
        }
    }
    
    /**
     * Adds other to this; Caution: this gets overwritten by result.
     * 
     * @param other  The matrix to add to this.
     * 
     * @return this + other.
     */
    public Matrix add(final Matrix other) {
        if (this.getColumnCount() != other.getColumnCount()
                || this.getRowCount() != other.getRowCount()) {
            throw new RuntimeException("Wrong dimension in this vs. other.");
        }
        
        for (int i = 0; i < this.getColumnCount(); i++) {
            for (int j = 0; j < this.getRowCount(); j++) {
                this.set(i, j, this.get(i, j) + other.get(i, j));
            }
        }
        
        return this;
    }

    /**
     * Subtracts other from this; Caution: this gets overwritten by result.
     * 
     * @param other  The matrix to subtract from this.
     * 
     * @return this - other.
     */
    public Matrix sub(final Matrix other) {
        if (this.getColumnCount() != other.getColumnCount()
                || this.getRowCount() != other.getRowCount()) {
            throw new RuntimeException("Summand ist unterschiedlich dimensioniert.");
        }
        
        for (int i = 0; i < this.getColumnCount(); i++) {
            for (int j = 0; j < this.getRowCount(); j++) {
                this.set(i, j, this.get(i, j) - other.get(i, j));
            }
        }
        
        return this;
    }
    
    /**
     * Generates identity matrix.
     */
    public Matrix einheitsMatrix() {
        for (int i = 0; i < this.getRowCount(); i++) {
            for (int j = 0; j < this.getColumnCount(); j++) {
                if (i == j) {
                    this.set(j, i, 1);
                } else {
                    this.set(j, i, 0);
                }
            }
        }
        
        return this;
    }

    @Override
    public String toString() {
        String s = "";
        
        for (int i = 0; i < this.getRowCount(); i++) {
            for (int j = 0; j < this.getColumnCount(); j++) {
                s += this.get(j, i) + ",";
            }
            s = s.substring(0, s.length() - 1);
            s += ";";
        }
        s = s.substring(0, s.length() - 1);
        
        return s;
    }
    
    public String toStringFormatiert(){
        String s = "\n";

        int[] laengen = new int[this.getColumnCount()];
        int zeilUebLaenge = 0;
        
        // Breite der Spalten festlegen - Spaltenüberschriften durchsuchen.
        for (int j = 0; j < this.spaltenUeberschr.length; j++) {
            if ((this.spaltenUeberschr[j] + "").length() > laengen[j]) {
                laengen[j] = (this.spaltenUeberschr[j] + "").length();
            }
        }

        // Breite der Spalten festlegen - Übrige Zellen durchsuchen.
        for (int i = 0; i < this.getRowCount(); i++) {
            if ((this.zeilenUeberschr[i] + "").length() > zeilUebLaenge) {
                zeilUebLaenge = (this.zeilenUeberschr[i] + "").length();
            }
            for (int j = 0; j < this.getColumnCount(); j++) {
                if ((this.matrix[j][i] + "").length() > laengen[j]) {
                    laengen[j] = (this.matrix[j][i] + "").length();
                }
            }
        }

        // Linke obere Zelle.
        s += " | ";
        for (int k = 0; k < zeilUebLaenge; k++) {
            s += " ";
        }
        
        // Obere Spalte.
        s += " || ";
        for (int j = 0; j < this.spaltenUeberschr.length; j++) {
            s += this.spaltenUeberschr[j];
            for (int k = 0; k < laengen[j]
                    - (this.spaltenUeberschr[j] + "").length(); k++) {
                s += " ";
            }
            s += " | ";
        }
        s += "\n";
        
        // Weitere Zellen.
        for (int i = 0; i < this.getRowCount(); i++) {
            s += " | ";
            s += this.zeilenUeberschr[i];
            for (int k = 0; k < zeilUebLaenge - (this.zeilenUeberschr[i] + "").length(); k++) {
                s += " ";
            }
            s += " || ";
            for (int j = 0; j < this.getColumnCount(); j++) {
                s += this.matrix[j][i];
                for (int k = 0; k < laengen[j]
                        - (this.matrix[j][i] + "").length(); k++) {
                    s += " ";
                }
                s += " | ";
            }
            s += "\n";
        }
        
        return s;
    }
    
    public String toPlainString() {
        String s = "\n";

        int[] laengen = new int[this.getColumnCount()];

        // Breite der Spalten festlegen - Übrige Zellen durchsuchen.
        for (int i = 0; i < this.getRowCount(); i++) {
            for (int j = 0; j < this.getColumnCount(); j++) {
                if ((this.matrix[j][i] + "").length() > laengen[j]) {
                    laengen[j] = (this.matrix[j][i] + "").length();
                }
            }
        }
        
        // Weitere Zellen.
        for (int i = 0; i < this.getRowCount(); i++) {
            for (int j = 0; j < this.getColumnCount(); j++) {
                s += this.matrix[j][i];
                for (int k = 0; k < laengen[j]
                        - (this.matrix[j][i] + "").length(); k++) {
                    s += " ";
                }
                s += "\t";
            }
            s += "\n";
        }
        
        return s;
    }

    public double get(final int i, final int j) {
        return this.matrix[i][j];
    }
    
    public void set(final int i, final int j, final double wert) {
        if (i >= 0 && i < this.getColumnCount()
                && j >= 0 && j < this.getRowCount()) {
            this.matrix[i][j] = wert;
        } else {
            StaticMethods.logWarning(
                    "Matrix field (" 
                    + i 
                    + "/" 
                    + j 
                    + ") does not exist. Value \"" 
                    + wert 
                    + "\" not set.", 
                    null);
            throw new RuntimeException();
        }
    }
    
    @Override
    public int getColumnCount() {
        return this.matrix.length;
    }
    
    @Override
    public int getRowCount() {
        if (this.matrix.length == 0) {
            return 0;
        }
        
        return this.matrix[0].length;
    }
    
    public Integer2D getRowTitle(final int j) {
        return this.zeilenUeberschr[j];
    }

    public Integer2D getColumnTitle(final int i) {
        return this.spaltenUeberschr[i];
    }
    
    public void setColumnTitle(final int i, final Integer2D wert) {
        this.spaltenUeberschr[i] = wert;
    }
    
    public void setRowTitle(final int j, final Integer2D wert) {
        this.zeilenUeberschr[j] = wert;
    }
    
    public Matrix transpone() {
        Matrix mat = new Matrix(this.getRowCount(), this.getColumnCount());
        
        for (int i = 0; i < this.getColumnCount(); i++) {
            mat.setRowTitle(i, this.getColumnTitle(i));
            for (int j = 0; j < this.getRowCount(); j++) {
                mat.setColumnTitle(j, this.getRowTitle(j));
                mat.set(j, i, this.get(i, j));
            }
        }
        
        this.matrix = mat.matrix;
        this.spaltenUeberschr = mat.spaltenUeberschr;
        this.zeilenUeberschr = mat.zeilenUeberschr;
        return this;
    }
    
    public Matrix erzeugeSpaltenMatrix(final int[] spalten) {
        Matrix result = new Matrix(spalten.length, this.getRowCount());
        
        for (int k = 0; k < spalten.length; k++) {
            result.setColumnTitle(k, this.getColumnTitle(spalten[k]));
            for (int j = 0; j < this.getRowCount(); j++) {
                result.setRowTitle(j, this.getColumnTitle(j));
                result.set(k, j, this.get(spalten[k], j));
            }
        }
        
        return result;
    }
    
    public Matrix erzeugeZeilenMatrix(final int[] zeilen) {
        Matrix result = new Matrix(this.getColumnCount(), zeilen.length);
        
        for (int k = 0; k < zeilen.length; k++) {
            result.setRowTitle(k, this.getRowTitle(zeilen[k]));
            for (int j = 0; j < this.getColumnCount(); j++) {
                result.setColumnTitle(j, this.getColumnTitle(j));
                result.set(j, k, this.get(j, zeilen[k]));
            }
        }
        
        return result;
    }

    private double getRandomValue() {
        return rand.nextDouble() * (getRandomMaxValue() - getRandomMinValue()) + getRandomMinValue();
    }
    
    private Matrix randomize() {
        for (int i = 0; i < this.getColumnCount(); i++) {
            for (int j = 0; j < this.getRowCount(); j++) {
                this.set(i, j, getRandomValue());
            }
        }
        
        return this;
    }
    
    public double maxValue() {
        double max = Double.NEGATIVE_INFINITY;
        
        for (int i = 0; i < this.getColumnCount(); i++) {
            for (int j = 0; j < this.getColumnCount(); j++) {
                if (this.get(i, j) > max) {
                    max = this.get(i, j);
                }
            }
        }
        
        return max;
    }
    
    public double minValue() {
        double min = Double.POSITIVE_INFINITY;
        
        for (int i = 0; i < this.getColumnCount(); i++) {
            for (int j = 0; j < this.getColumnCount(); j++) {
                if (this.get(i, j) < min) {
                    min = this.get(i, j);
                }
            }
        }
        
        return min;
    }
    
    /**
     * @param selNumberK    Number of agents for selection tournaments.
     * @param agentNumberN  Number of agents in population.
     * 
     * @return  The special probability matrix according to Fig. 10 in 
     *          ECTA2011 paper.
     */
    public static Matrix erzeugeWkeitsMatrixSonder(final int selNumberK, final int agentNumberN) {
        int k = selNumberK;
        int n = agentNumberN;
        
        Matrix wkeitsMatrix = new Matrix(k + 1, n + 1);
        
        // Überschriften.
        generateProbMatrixTitles(wkeitsMatrix, n, k);
        
        // Werte erzeugen.
        for (int i = 0; i < wkeitsMatrix.getColumnCount(); i++) {
            for (int j = 0; j < wkeitsMatrix.getRowCount(); j++) {
                if (wkeitsMatrix.getColumnTitle(i).gute > wkeitsMatrix.getRowTitle(j).gute
                        || wkeitsMatrix.getColumnTitle(i).schlechte > wkeitsMatrix.getRowTitle(j).schlechte) {
                    wkeitsMatrix.set(i, j, 0);
                } else {
                    if (i == k / 2) {
                        wkeitsMatrix.set(i, j, 1); //, rand.nextDouble());
                    } else {
                        wkeitsMatrix.set(i, j, 0.0001); //, rand.nextDouble());
                    }
                }
            }
        }
        
        // Werte normalisieren.
        normalizeRowwise(wkeitsMatrix);
        
        return wkeitsMatrix;
    }
    
    private static void generateProbMatrixTitles(
            final Matrix wkeitsMatrix,
            final int n, 
            final int k) {
        for (int i = 0; i < wkeitsMatrix.getColumnCount(); i++) {
            wkeitsMatrix.setColumnTitle(i, new Integer2D(i, k - i));
        }
        
        for (int j = 0; j < wkeitsMatrix.getRowCount(); j++) {
            wkeitsMatrix.setRowTitle(j, new Integer2D(j, n - j));
        }
    }
    
    /**
     * Sum of all non-zero rows gets 1, relations are preserved rowwise.
     * 
     * @param matrix  The matrix to normalize.
     */
    public static void normalizeRowwise(
            final Matrix matrix) {
        for (int j = 0; j < matrix.getRowCount(); j++) {
            double summe = 0;
            
            for (int i = 0; i < matrix.getColumnCount(); i++) {
                summe += matrix.get(i, j);
            }
            if (summe != 0) {
                for (int i = 0; i < matrix.getColumnCount(); i++) {
                    matrix.set(i, j, matrix.get(i, j) / summe);
                }
            }
        }
    }

    /**
     * Generates probability matrix with a polynomial preference for superior
     * agents (cf. ECTA2011 paper).
     * 0 Gute : 1/k (oder 0, falls unmöglich; dann Anpassung von k)
     * 1 Guter: 2/k (oder 0, falls unmöglich; dann Anpassung von k)
     * 2 Gute : 3/k (oder 0, falls unmöglich; dann Anpassung von k)
     * ...
     * 
     * @param selNumberK    Number of agents for selection tournaments.
     * @param agentNumberN  Number of agents in population.
     * @param exponent      The polynomial exponent.
     * 
     * @return  Probability matrix.
     */
    public static Matrix erzeugeWkeitsMatrixPolynomialGut(
            final int selNumberK, 
            final int agentNumberN, 
            final double exponent) {
        int k = selNumberK;
        int n = agentNumberN;
        
        Matrix wkeitsMatrix = new Matrix(k + 1, n + 1);
        
        // Überschriften.
        generateProbMatrixTitles(wkeitsMatrix, n, k);
        
        // Werte erzeugen.
        for (int i = 0; i < wkeitsMatrix.getColumnCount(); i++) {
            for (int j = 0; j < wkeitsMatrix.getRowCount(); j++) {
                if (wkeitsMatrix.getColumnTitle(i).gute > wkeitsMatrix.getRowTitle(j).gute
                        || wkeitsMatrix.getColumnTitle(i).schlechte > wkeitsMatrix.getRowTitle(j).schlechte) {
                    wkeitsMatrix.set(i, j, 0);
                } else {
                    wkeitsMatrix.set(i, j, Math.pow(wkeitsMatrix.getColumnTitle(i).gute + 1, exponent));
                }
            }
        }
        
        // Werte normalisieren.
        normalizeRowwise(wkeitsMatrix);
        
        return wkeitsMatrix;
    }
    
    /**
     * Generates probability matrix with a polynomial preference for inferior
     * agents (cf. ECTA2011 paper).
     * 0 Schlechte : 1/k (oder 0, falls unmöglich; dann Anpassung von k)
     * 1 Schlechter: 2/k (oder 0, falls unmöglich; dann Anpassung von k)
     * 2 Schlechte : 3/k (oder 0, falls unmöglich; dann Anpassung von k)
     * usw.
     * 
     * @param selNumberK    Number of agents for selection tournaments.
     * @param agentNumberN  Number of agents in population.
     * @param exponent      The polynomial exponent.
     * 
     * @return  Probability matrix.
     */
    public static Matrix erzeugeWkeitsMatrixPolynomialSchlecht(
            final int selNumberK, 
            final int agentNumberN, 
            final double exponent) {
        int k = selNumberK;
        int n = agentNumberN;
        
        Matrix wkeitsMatrix = new Matrix(k + 1, n + 1);
        
        // Überschriften.
        generateProbMatrixTitles(wkeitsMatrix, n, k);
        
        // Werte erzeugen.
        for (int i = 0; i < wkeitsMatrix.getColumnCount(); i++) {
            for (int j = 0; j < wkeitsMatrix.getRowCount(); j++) {
                if (wkeitsMatrix.getColumnTitle(i).gute > wkeitsMatrix.getRowTitle(j).gute
                        || wkeitsMatrix.getColumnTitle(i).schlechte > wkeitsMatrix.getRowTitle(j).schlechte) {
                    wkeitsMatrix.set(i, j, 0);
                } else {
                    wkeitsMatrix.set(i, j, Math.pow(wkeitsMatrix.getColumnTitle(i).schlechte + 1, exponent));
                }
            }
        }
        
        // Werte normalisieren.
        normalizeRowwise(wkeitsMatrix);
        
        return wkeitsMatrix;
    }
    
    private static double getWkeit(
            final Integer2D zeileGesMatrix, 
            final Integer2D spalteWkeitsMatrix, 
            final Matrix wkeitsMatrix) {
        try {
            return wkeitsMatrix.get(spalteWkeitsMatrix.gute, zeileGesMatrix.gute);
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Generates a Markov transition matrix for a population of N agents,
     * where the probabilities for the occurence of tournaments with 
     * l / (k - l) superior / inferior agents for every specific population state
     * i / (n - i) is given in probabilityMatrixC. Selection with an explicit 
     * (fitness) preference for superior individuals can be modeled by shifting 
     * the fitness confidence factor c from 0 towards 1. 
     * 
     * @param agentNumberN        Number of agents in population.
     * @param probabilityMatrixC  Probabilities for occurence of
     *                            specific tournaments.
     * @param cg                  Fitness confidence factor.
     * 
     * @return  The Markov transition matrix.
     */
    public static Matrix generateTransitionMatrixKSelection(
            final int agentNumberN, 
            final Matrix probabilityMatrixC,
            final double cg) {
        int n = agentNumberN;
        int k = probabilityMatrixC.getColumnCount() - 1;
            
        Matrix mat = new Matrix(n + 1, n + 1);

        // Überschriften.
        for (int i = 0; i < mat.getColumnCount(); i++) {
            mat.setColumnTitle(i, new Integer2D(i, n - i));
        }
        
        for (int j = 0; j < mat.getRowCount(); j++) {
            mat.setRowTitle(j, new Integer2D(j, n - j));
        }
        
        // Speichert die Summanden für die asymmetrische Wkeits-Verschiebung zugunsten der "Guten".
        HashMap<Integer2D, Double> summanden = new HashMap<Integer2D, Double>();
        
        // Übernimm Wkeitsmatrix in Gesamtmatrix.
        for (int j = 0; j < mat.getRowCount(); j++) {
            for (int i = Math.max(j - k + 1, 0); i < Math.min(j + k, mat.getColumnCount()); i++) {
                if (i == j) {
                    mat.set(i, j, mat.get(i, j) + getWkeit(
                            mat.getRowTitle(j), 
                            new Integer2D(0, k), 
                            probabilityMatrixC));
                    mat.set(i, j, mat.get(i, j) + getWkeit(
                            mat.getRowTitle(j), 
                            new Integer2D(k, 0), 
                            probabilityMatrixC));
                } else {
                    int gute;
                    double summand;
                    double l;
                    if (i < j) {
                        gute = j - i;
                    } else if (i > j) {
                        gute = k - (i - j);
                    } else {
                        // Dieser Fall kann nicht auftreten.
                        gute = -1;
                        summand = Double.NEGATIVE_INFINITY;
                    }

                    l = k - gute;
                    summand = l / k * cg;
                    
                    Integer2D spalteWkeitsMatrix = new Integer2D(gute, k - gute);
                    
                    double wkeit = getWkeit(
                            mat.getRowTitle(j), 
                            spalteWkeitsMatrix, 
                            probabilityMatrixC);
                    
                    if (wkeit == 0) {
                        summand = 0;
                    }
                    
                    // Achtung: Hier ist das Zweiertupel (x, y) die Koordinate [x][y] in der Wkeits-Matrix.
                    summanden.put(new Integer2D(i, j), summand);
                    
                    mat.set(i, j, mat.get(i, j) + wkeit);
                }
            }
        }
        
        // Füge Wkeit für Konvertierung in Richtung "gut" bzw. "schlecht".
        for (int j = 0; j < mat.getRowCount(); j++) {
            double kk = k;
            double l = kk; // Der Faktor für die Wkeit (=> gut / schlecht). 
            Double summand;
            
            for (int i = 0; i < kk; i++) {
                if (i != 0 && j + i >= 0 && j + i < mat.getColumnCount()) {
                    summand = summanden.get(new Integer2D(j + i, j));
                    if (summand == null) {
                        summand = 0.0;
                    }
                    
                    mat.set(j + i, j, mat.get(j + i, j) * (l / kk + summand));
                }
                l--;
            }

            l = kk;
            for (int i = 0; i < kk; i++) {
                if (i != 0 && j - i >= 0 && j - i < mat.getColumnCount()) {
                    summand = summanden.get(new Integer2D(j - i, j));
                    if (summand == null) {
                        summand = 0.0;
                    }
                    
                    mat.set(j - i, j, mat.get(j - i, j) * (l / kk - summand));
                }
                l--;
            }
        }
        
        return mat;
    }

    /**
     * Generates data for a complete convergence diagram using specific 
     * probability matrices C.
     * 
     * @param n                        Population size.
     * @param k                        Tournament size.
     * @param iterations               Iterations at calculation of inverse 
     *                                 matrix (note that exact calculation
     *                                 as described in ECTA2011 paper is
     *                                 not implemented in Java but has been
     *                                 performed with MatLab). 
     * @param seed                     Random seed used for random matrix.
     * @param confidenceFactorC        The confidence factor in [0, 1].
     * @param fileNameStore            The file name to store convergence data.
     * @param loadMatrixFromFileName   File name from where to get the 
     *                                 probability matrix in case 
     *                                 probMatrixFromFile.
     * @param optionProbabilityMatrix  The number of the probability matrix 
     *                                 type.
     * @param exponent                 The exponent in cases
     *                                 probabilityMatrixPolynomialSuperior and
     *                                 probabilityMatrixPolynomialInferior.
     */
    public static void generateConvergenceDiagram(
            int n, 
            int k, 
            int iterations, 
            long seed,
            double confidenceFactorC, 
            String fileNameStore,
            String loadMatrixFromFileName,
            int optionProbabilityMatrix,
            double exponent) {
        StaticMethods.logInfo("k = " + k, null);
        StaticMethods.logInfo("n = " + n, null);
        StaticMethods.logInfo("seed = " + seed, null);
        StaticMethods.logInfo("iterations = " + iterations, null);
        StaticMethods.logInfo("c (confidence factor) = " + confidenceFactorC, null);
        StaticMethods.logInfo("Option: " + optionProbabilityMatrix, null);
        if (optionProbabilityMatrix == 1 || optionProbabilityMatrix == 2) {
            StaticMethods.logInfo("Exponent (only options 1 and 2): " + exponent, null);
        } else {
            StaticMethods.logInfo("(Exponent: " + exponent + " (not used))", null);
        }
        StaticMethods.logInfo("File name to get probability matrix: " + loadMatrixFromFileName, null);
        StaticMethods.logInfo("File name to store data: " + fileNameStore, null);
        Matrix mat = null;
        
        if (optionProbabilityMatrix == probMatrixUniform) {
            mat = Matrix.erzeugeWkeitsMatrixPolynomialGut(k, n, 0);
        } else if (optionProbabilityMatrix == probMatrixPolynomialSuperior) {
            mat = Matrix.erzeugeWkeitsMatrixPolynomialGut(k, n, exponent);
        } else if (optionProbabilityMatrix == probMatrixPolynomialInferior) {
            mat = Matrix.erzeugeWkeitsMatrixPolynomialSchlecht(k, n, exponent);
        } else if (optionProbabilityMatrix == probMatrixSpecial) {
            mat = Matrix.erzeugeWkeitsMatrixSonder(k, n);
        } else if (optionProbabilityMatrix == probMatrixFromFile) {
            mat = new Matrix(new File(loadMatrixFromFileName));
        }
        
        Matrix matGesamt = Matrix.generateTransitionMatrixKSelection(n, mat, confidenceFactorC);
        Matrix matGesamt2 = Matrix.generateTransitionMatrixKSelection(n, mat, confidenceFactorC);
        StaticMethods.log(StaticMethods.LOG_INFO, "\n\nProbabilities:\n" + mat, null, "plain", "plain");
        StaticMethods.log(StaticMethods.LOG_INFO, "\n\nTransition matrix:\n" + matGesamt, null, "plain", "plain");

        // I-Q erzeugen:
        Matrix q = new Matrix(matGesamt);
        int[] spalten = new int[q.getColumnCount() - 2];
        int[] zeilen = new int[q.getRowCount() - 2];
        for (int i = 1; i < q.getColumnCount() - 1; i++) {
            spalten[i - 1] = i;
        }
        for (int j = 1; j < q.getRowCount() - 1; j++) {
            zeilen[j - 1] = j;
        }
        q = q.erzeugeSpaltenMatrix(spalten).erzeugeZeilenMatrix(zeilen);
        Matrix einheitMinusQ = new Matrix(q);
        einheitMinusQ.einheitsMatrix();
        einheitMinusQ.sub(q);
        StaticMethods.log(StaticMethods.LOG_INFO, "\n\n(I-Q) - for Matlab:\n" + einheitMinusQ.createMatlabString(), null, "plain", "plain");
        ////
        
        StaticMethods.log(StaticMethods.LOG_INFO, "\n\nSummed up probabilties (should all be 1.0):\n" + Matrix.rowSum(matGesamt).round(2), null, "plain", "plain");
        
        ArrayList<String> werte = new ArrayList<String>();
        String wert;
        
//        System.out.println();
        for (int i = 0; i < iterations; i++) {
//            if (i % 10000 == 0) {
//                System.out.println(" (" + i + ")");
//            }
//            if (i % 100 == 0) {
//                System.out.print(".");
//            }
            wert = "";
            for (int j = 0; j < matGesamt.getRowCount(); j++) {
                wert += matGesamt.get(0, j) + "\t";
            }
//            wert = wert.replaceAll("\\.", ",");
            werte.add(wert);
                
            matGesamt.mult(matGesamt2);
        }
        
//        matGesamt.round(5);
        StaticMethods.log(StaticMethods.LOG_INFO, "\n\nRaise to power of " + iterations + " (only columns 0 and n):\n" + matGesamt.erzeugeSpaltenMatrix(new int[] {0, n}), null, "plain", "plain");
        
        StaticMethods.logInfo("Maximum in columns 1-" + (n - 1) + ": " + matGesamt.erzeugeSpaltenMatrix(new int[] {1, n - 1}).maxValue(), null);
        
        if (fileNameStore != null) {
            StaticMethods.storeCollectionElementsAsText(".", fileNameStore, werte);
        }
        
        StaticMethods.log(
                StaticMethods.LOG_INFO, "\nProbabilities for eventual superiority: "
                    + matGesamt.erzeugeSpaltenMatrix(new int[] {n}).transpone().toPlainString(), 
                null, 
                "plain", 
                "");
    }

    public static final int probMatrixUniform = 0;
    // Polynomial preference for selecting superior individuals.
    public static final int probMatrixPolynomialSuperior = 1;
    // Polynomial preference for selecting inferior individuals.
    public static final int probMatrixPolynomialInferior = 2;
    // Special matrix as in Fig. 10 from ECTA2011 paper.
    public static final int probMatrixSpecial = 10;
    public static final int probMatrixFromFile = 11;
    
    /**
     * Parses strings of the form:
     * 
     * "0.0,1.2,2.9; 1.1,2.3,1.4; 2.2,3.3,-4.4; 5.4,-6.4,7.6"
     * ==>
     * 0.0 1.2 2.9
     * 1.1 2.3 1.4
     * 2.2 3.3 -4.4
     * 5.4 -6.4 7.6
     * 
     * @param matrix
     * @return
     */
    public static Matrix parseMatrix(final String matrix) {
        String[] rows = matrix.replace(" ", "").replace("\n", "").split(";");
        ArrayListDouble rowParsed;
        ArrayListDouble[] matrixList = new ArrayListDouble[rows.length];
        int maxColumns = 0;
        int z = 0;
        double[][] matrixArray;
        
        for (String row : rows) {
            rowParsed = ArrayListDouble.parseArrayListDouble(row);
            if (rowParsed.size() > maxColumns) {
                maxColumns = rowParsed.size();
            }
            matrixList[z] = rowParsed;
            z++;
        }

        matrixArray = new double[maxColumns][rows.length];
        
        for (int i = 0; i < matrixArray.length; i++) {
            for (int j = 0; j < matrixArray[0].length; j++) {
                matrixArray[i][j] = matrixList[j].get(i);
            }
        }
        
        return new Matrix(matrixArray);
    }

    private Random rand = new Random();
}