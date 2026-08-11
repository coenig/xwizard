/*
 * Datei:          MathMeth.java
 * Autor(en):      Lukas König
 * Java-Version:   1.4
 * Erstellt (vor): 27.03.2008
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

package eas.math;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Collection of math methods.
 * 
 * @author Lukas König
 */
public final class MiscMath {
    
    private MiscMath() {

    }
    
    public static Object randVerteilung(
            final List<?>    obj, 
            final List<Long> vert,
            final Random     rand) {
        Object[] list = new Object[obj.size()];
        
        for (int i = 0; i < obj.size(); i++) {
            list[i] = obj.get(i);
        }
        
        return randVerteilung(list, vert, rand);
    }
    
    public static Object randVerteilungDoubleAngenaehert(
            final List<?>     obj, 
            final List<Double> vert,
            final Random       rand) {
        Object[] list = new Object[obj.size()];
        
        for (int i = 0; i < obj.size(); i++) {
            list[i] = obj.get(i);
        }
        
        return randVerteilungDoubleAngenaehert(list, vert, rand);
    }

    public static Object randVerteilungDoubleAngenaehert(
            final Object[]     obj, 
            final List<Double> vert,
            final Random       rand) {
        double min = Double.POSITIVE_INFINITY;
        LinkedList<Long> vertNeu = new LinkedList<Long>();
        
        for (Double d : vert) {
            if (d < min && Math.abs(d) > 0.0000001) {
                min = d;
            }
        }
        
        for (Double d : vert) {
            vertNeu.add((long) (100000 * d / min));
        }
        
        return randVerteilung(obj, vertNeu, rand);
    }
    
    /**
     * Gibt ein zufälliges Objekt aus obj zurück, wobei die 
     * Wahrscheinlichkeitsverteilung in vert angegeben ist. vert darf nur 
     * Integer-Objekte enthalten. Die Wahrscheinlichkeit für Objekt Nummer i, 
     * aus obj ausgewählt zu werden, ist dabei vert(i) / summe(vert). Achtung:
     * Falls alle Verteilungswerte 0 sind, wird gleichverteilt ein Objekt
     * zurückgegeben.
     * 
     * @param obj   Liste von Objekten.
     * @param vert  Liste für Zufallsverteilung.
     * @param rand  Der Zufallsgenerator.
     * 
     * @return  Ein zufälliges Objekt aus obj.
     */
    public static Object randVerteilung(final Object[]   obj, 
                                        final List<Long> vert,
                                        final Random     rand) {
        if (obj.length != vert.size()) {
            throw new RuntimeException("Listen haben ungleiche Laenge.");
        }
        
        long summe = 0;
        long zufall;
        long akt;
        int i;
        
        Iterator<Long> it = vert.iterator();
        while (it.hasNext()) {
            akt = it.next().longValue();
            if (akt < 0) {
                throw new RuntimeException("Negative Wahrscheinlichkeit: " 
                                           + akt);
            } else {
                summe += akt;
            }
        }

        if (summe > 0) {
            long randNum = Math.abs(rand.nextLong());
            zufall = randNum % summe + 1;
            summe = 0;
            i = -1;
            it = vert.iterator();
            
            while (summe < zufall) {
                summe += it.next().longValue();
                i++;
            }
        } else {
            i = rand.nextInt(obj.length);
        }
        
        return obj[i];
    }

    /**
     * Berechnet einen gleitenden Durchschnitt.
     * 
     * @param werte    Die Werte des gleitenden Durchschnitts.
     * @param abstand  Die Anzahl der Werte, die für einen Durchschnitt 
     *                 genommen werden.
     * 
     * @return  Der gleitende Durchschnitt.
     */
    public static ArrayList<Double> glDurchSchn(
            final ArrayList<Double> werte,
            final int abstand) {
        ArrayList<Double> gleitDurch;
        double aktSumme = 0;
        
        if (werte.size() < abstand) {
            throw new RuntimeException(
                    "Werteanzahl zu gering für Berechnung des gleitenden "
                    + "Durchschnitts.");
        }
        
        gleitDurch = new ArrayList<Double>(werte.size() - abstand + 1);
        
        for (int i = 0; i < abstand; i++) {
            aktSumme += werte.get(i);
        }
        
        gleitDurch.add(aktSumme / abstand);
        
        // Gleitenden Durchschnitt berechnen.
        for (int i = abstand; i < werte.size(); i++) {
            aktSumme -= werte.get(i - abstand);
            aktSumme += werte.get(i);
            gleitDurch.add(aktSumme / abstand);
        }
        
        return gleitDurch;
    }

    /**
     * ACHTUNG! GANZZAHLIGER Binomialkoeffizient aus dem Internet: 
     * http://www.java-forum.org/mathematik/81941-binomialkoeffizient.html#_
     * 
     * @param n  Der n-Parameter.
     * @param k  Der k-Parameter.
     * 
     * @return  Binomialkoeffizient.
     */
    public static double binomialCoefficient(
            double n, 
            double k) {
        return MiscMath.binomialCoefficient(
                new BigInteger((long) n + ""), 
                new BigInteger((long) k + "")).doubleValue();
    }

    /**
     * Binomialkoeffizient aus dem Internet: 
     * http://www.java-forum.org/mathematik/81941-binomialkoeffizient.html#_
     * 
     * @param n  Der n-Parameter.
     * @param k  Der k-Parameter.
     * 
     * @return  Binomialkoeffizient.
     */
    public static BigInteger binomialCoefficient(
            BigInteger n, 
            BigInteger k2) {

        BigInteger k = k2;
        
        BigInteger n_minus_k = n.subtract(k);
        if (n_minus_k.compareTo(k) < 0) {
            BigInteger temp = k;
            k = n_minus_k;
            n_minus_k = temp;
        }

        BigInteger numerator = BigInteger.ONE;
        BigInteger denominator = BigInteger.ONE;

        for (BigInteger j = BigInteger.ONE; j.compareTo(k) <= 0; j = j
                .add(BigInteger.ONE)) {
            numerator = numerator.multiply(j.add(n_minus_k));
            denominator = denominator.multiply(j);
            BigInteger gcd = numerator.gcd(denominator);
            numerator = numerator.divide(gcd);
            denominator = denominator.divide(gcd);
        }

        return numerator;
    }
    
    private static HashMap<String, LinkedList<Double>> runs = new HashMap<>();
    private static HashMap<String, Integer> oldSizes = new HashMap<>();

    public static void resetMovingAverage(String name) {
        runs.put(name, new LinkedList<>());
    }
    
    /**
     * Stores an additional value for a moving average plot with window size
     * {@code windowSize}. Returns the current average value if enough values
     * are present, {@code null} otherwise. If the actual window size is 
     * greater or equal to the parameter {@code windowSize}, the list is
     * shortened accordingly (except when {@code value == null}).
     * 
     * @param name        The identical name of the plot to store.
     * @param value       The value to add (if {@code null}, only getter).
     * @param windowSize  The window size. -1 for avg. of all available values. -2 for 
     *                    window size = window size from last time with value != null.
     * 
     * @return  The current average value or {@code null}.
     */
    public static synchronized Double movingAverage(String name, Double value, int windowSize, boolean return0IfToLittleData) {
        LinkedList<Double> run = runs.get(name);
        Integer windowSize2 = windowSize;
        
        if (run == null) {
            run = new LinkedList<>();
            runs.put(name, run);
        }
        
        if (value == null) {
            if (windowSize == -1) {
                windowSize2 = run.size();
            } else if (windowSize == -2) {
                windowSize2 = oldSizes.get(name);
                if (windowSize2 == null) {
                    windowSize2 = run.size();
                }
            }
            
        } else {
            oldSizes.put(name, windowSize2);
            while (run.size() >= windowSize2) {
                run.pollLast();
            }

            run.push(value);
        }

        Double movAvg;
        if (run.size() >= windowSize2 && run.size() > 0) {
            List<Double> run2 = run.subList(0, windowSize2);
            movAvg = run2.stream().mapToDouble(d -> d).average().getAsDouble();
            return movAvg;
        }
        
        movAvg = return0IfToLittleData ? 0.0 : null;
        return movAvg;
    }
    
    /**
     * Retrieves the current average value if enough values are already stored.
     * 
     * @param name        The identical name of the plot to store.
     * @param windowSize  The window size. -1 for avg. of all available values.
     * 
     * @return  The current average value or {@code null}.
     */
    public static Double getMovingAverage(String name, int windowSize) {
        return getMovingAverage(name, windowSize, false);
    }
    
    /**
     * Retrieves the current average value if enough values are already stored.
     * 
     * @param name        The identical name of the plot to store.
     * @param windowSize  The window size. -1 for avg. of all available values.
     * 
     * @return  The current average value or {@code null}.
     */
    public static Double getMovingAverage(String name, boolean return0IfToLittleData) {
        return movingAverage(name, null, -1, return0IfToLittleData);
    }
    
    /**
     * Retrieves the current average value if enough values are already stored.
     * 
     * @param name        The identical name of the plot to store.
     * @param windowSize  The window size. -1 for avg. of all available values.
     * 
     * @return  The current average value or {@code null}.
     */
    public static Double getMovingAverage(String name, int windowSize, boolean return0IfToLittleData) {
        return movingAverage(name, null, windowSize, return0IfToLittleData);
    }

    private static HashMap<Long, Long> table = new HashMap<>();
    
    /**
     * Hofstadters Q sequence.
     * 
     * @param n  Element position.
     * @return  Element at position.
     */
    public static long q(long n) {
        if (n < 1) {
            return 0;
        }
        
        if (n == 1 || n == 2) {
            return 1;
        }
        
        Long tempResult = table.get(n);
        
        if (tempResult != null) {
            return tempResult;
        }
        
        long result = q(n - q(n - 1)) + q(n - q(n - 2));
        
        table.put(n, result);
        
        return result;
    }
    
    public static double min(Double... vals) {
        double current = Double.POSITIVE_INFINITY;
        
        for (Double v : vals) {
            if(v != null){
                current = Math.min(current, v);
            }
        }
        
        return current;
    }
    
    public static int min(Integer ignoreVal, Integer... vals) {
        int current = Integer.MAX_VALUE;
        
        for (Integer v : vals) {
            if(v != null && !v.equals(ignoreVal)){
                current = Math.min(current, v);
            }
        }
        
        return current;
    }
    
    public static double median(Double... vals) {
        Arrays.sort(vals);
        if (vals.length % 2 == 0) {
            return (vals[vals.length / 2] + vals[vals.length / 2 - 1]) / 2;
        } else {
            return vals[vals.length / 2];
        }
    }
    
    public static double max(Double... vals) {
        double current = Double.NEGATIVE_INFINITY;
        
        for (Double v : vals) {
            if(v != null){
                current = Math.max(current, v);
            }
        }
        
        return current;
    }
    
    public static double avgPrim(double... vals) {
        return avg(Arrays.stream(vals).boxed().toArray(Double[]::new));
    }

    public static double max(Collection<Double> vals) {
        double max = Double.NEGATIVE_INFINITY;
        for (double d : vals) {
            if (d > max) {
                max = d;
            }
        }
        return max;
    }

    public static double min(Collection<Double> vals) {
        double min = Double.POSITIVE_INFINITY;
        for (double d : vals) {
            if (d < min) {
                min = d;
            }
        }
        return min;
    }

    public static double avg(Collection<Double> vals) {
        double sum = 0;
        for (double d : vals) {
            sum += d;
        }
        return sum / vals.size();
    }
    
    public static double avg(Double... vals) {
        double current = 0;
        
        int numVals = 0;
        for (Double v : vals) {
            if (v != null){
                current = current + v;
                numVals = numVals + 1;
            }
        }

        if (numVals == 0) {
            throw new RuntimeException("No values to compute.");
        }

        current = current / numVals;

        return current;
    }
    
    public static long modRange(
            final long zahl,
            final long min,
            final long max) {
        return modRange(zahl, min, max, null);
    }

    public static BigInteger modRange(
            final BigInteger zahl,
            final BigInteger min,
            final BigInteger max) {
        return modRange(zahl, min, max, null);
    }
    
    public static long modRange(
            final long zahl,
            final long min,
            final long max,
            final List<Integer> verboten) {
        List<BigInteger> v2 = null;
        
        if (verboten != null) {
            List<BigInteger> v = new ArrayList<>(verboten.size());
            verboten.forEach(i -> v.add(new BigInteger("" + i)));
            v2 = v;
        }
        
        return modRange(new BigInteger(zahl + ""), new BigInteger(min + ""), new BigInteger(max + ""), v2).intValue();
    }

    /**
     * Gibt eine Zahl zurück, die modulo des angegebenen Bereichs der 
     * eingegebenen Zahl entspricht. Dabei können auch "verbotene" Zahlen
     * angegeben werden, die nicht zurückgegeben werden dürfen.
     *
     * Beispiel (min = 3, max = 7, v = {5}, zahl = i, f = modZwischen):
     * range            :               X, X, -, X, X
     * i                :  -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, ...
     * f(i, min, max, v):   3, 4, 6, 7, 3, 4, 6, 7, 3, 4, 6, 7, ...
     * 
     * @param zahl      Die Zahl.
     * @param min       Der Beginn des Bereichs.
     * @param max       Das Ende des Bereichs.
     * @param verboten  Verbotene Zahlen, die nicht zurückgegeben werden 
     *                  dürfen.
     * 
     * @return  Zahl, die der Eingabe modulo des Bereichs entspricht.
     */
    public static BigInteger modRange(
            final BigInteger zahl,
            final BigInteger min,
            final BigInteger max,
            final List<BigInteger> verboten) {
        if (verboten == null || verboten.size() == 0) {
            if (min.compareTo(max) > 0) {
                throw new RuntimeException("Inconcistency 'min > max'.");
            }
            
            if (zahl.compareTo(min) >= 0) {
                return zahl.subtract(min).mod(max.subtract(min).add(BigInteger.ONE)).add(min);
            } else {
                return (MiscMath.modRange(zahl.negate(), max.negate(), min.negate())).negate();
            }
        } else {
            Stream<BigInteger> betweenOnes = verboten.stream().filter(i -> i.compareTo(max) <= 0 && i.compareTo(min) >= 0);
            BigInteger temp = modRange(zahl, min, max.subtract(new BigInteger("" + betweenOnes.count())));
            Stream<BigInteger> smallerOnes = verboten.stream().filter(i -> i.compareTo(temp) <= 0);
            BigInteger result = temp.add(new BigInteger("" + smallerOnes.count()));
            return result;
        }
    }
    
    /**
     * Same as {@link #extractFirstSubstringLevelwise(String, String, String, int)},
     * only the beginTag position is determined automatically to be on the 
     * first occurrence of <code>beginTag</code>.
     * 
     * @param string       The string to process.
     * @param beginTag     A String denoting the begin of a bracket-like structure.
     * @param endTag       A String denoting the end of a bracket-like structure.
     * 
     * @return  The substring between the matching brackets.
     */
    public static String extractFirstSubstringLevelwise(
            String string, 
            String beginTag, 
            String endTag) {
        return extractFirstSubstringLevelwise(string, beginTag, endTag, 0);
    }

    /**
     * Same as {@link #extractSubstringsLevelwise(String, String, String, int)},
     * only the beginTag position is determined automatically to be on the 
     * first occurrence of <code>beginTag</code>.
     * 
     * @param string       The string to process.
     * @param beginTag     A String denoting the begin of a bracket-like structure.
     * @param endTag       A String denoting the end of a bracket-like structure.
     * 
     * @return  The substring between the matching brackets.
     */
    public static LinkedList<String> extractSubstringsLevelwise(
            String string, 
            String beginTag, 
            String endTag) {
        return extractSubstringsLevelwise(string, beginTag, endTag, 0);
    }

    /**
     * Same as {@link #extractFirstSubstringLevelwise(String, String, String, LinkedList, LinkedList, int)},
     * only there are no ignored regions.
     * 
     * @param string         The string to process.
     * @param beginTag       A String denoting the begin of a bracket-like structure.
     * @param endTag         A String denoting the end of a bracket-like structure.
     * @param beginTagPos    The position from which to start searching.
     * 
     * @return  The first substring between matching brackets or <code>null</code>
     */
    public static String extractFirstSubstringLevelwise(
            String string, 
            String beginTag, 
            String endTag, 
            int beginTagPos) {
        return extractFirstSubstringLevelwise(string, beginTag, endTag, new LinkedList<>(), new LinkedList<>(), beginTagPos);
    }

    /**
     * Same as {@link #extractSubstringsLevelwise(String, String, String, LinkedList, LinkedList, int)},
     * only there are no ignored regions.
     * 
     * @param string         The string to process.
     * @param beginTag       A String denoting the begin of a bracket-like structure.
     * @param endTag         A String denoting the end of a bracket-like structure.
     * @param beginTagPos    The position from which to start searching.
     * 
     * @return  The substrings between matching brackets.
     */
    public static LinkedList<String> extractSubstringsLevelwise(
            String string, 
            String beginTag, 
            String endTag, 
            int beginTagPos) {
        return extractSubstringsLevelwise(string, beginTag, endTag, new LinkedList<>(), new LinkedList<>(), beginTagPos);
    }

    /**
     * Returns the first substring of <code>string</code> right from 
     * <code>beginTagPos</code> starting after a <code>beginTag</code> and ending directly 
     * before the first position of the matching <code>endTag</code>. 
     * Tags within the ignore tags are ignored.</BR>
     * </BR>
     * Example: "<code>text@{more(Text)}@ (even (more) text)</code>" will yield
     * "<code>even (more) text</code>" if <code>@{}@</code> are ignored parts and
     * <code>()</code> denote the begin and end tags.
     * </BR>
     * </BR>
     * Also, "<code>(@{)}@)</code>" will yield "<code>@{)}@</code>", ignoring the
     * first closing tag.
     * </BR>
     * 
     * @param string         The string to process.
     * @param beginTag       A String denoting the begin of a bracket-like structure.
     * @param endTag         A String denoting the end of a bracket-like structure.
     * @param ignoreBegTags  List of tags that denote the beginning parts where
     *                       occurrences of the tags are ignored.
     * @param ignoreEndTags  List of matching end tags.
     * @param beginTagPos    The position from which to start searching.
     * 
     * @return  The single substring between matching bracket pairs directly right from
     *          <code>beginTagPos</code>. If there is no such
     *          region, <code>null</code> is returned.
     */
    public static String extractFirstSubstringLevelwise(
            String string, 
            String beginTag, 
            String endTag, 
            LinkedList<String> ignoreBegTags,
            LinkedList<String> ignoreEndTags,
            int beginTagPos) {
        LinkedList<String> result = extractSubstringsLevelwise(
                        string, 
                        beginTag, 
                        endTag, 
                        ignoreBegTags, 
                        ignoreEndTags, 
                        beginTagPos, 
                        true, 
                        new LinkedList<>());
        
        return result.isEmpty() ? null : result.getFirst();
    }

    /**
     * Returns all the substrings of <code>string</code> right from 
     * <code>beginTagPos</code> starting after a <code>beginTag</code> and ending directly 
     * before the first position of the matching <code>endTag</code>. 
     * Tags within the ignore tags are ignored.</BR>
     * </BR>
     * Example: "<code>text@{more(Text)}@ (even (more) text)</code>" will yield
     * "<code>even (more) text</code>" if <code>@{}@</code> are ignored parts and
     * <code>()</code> denote the begin and end tags.
     * </BR>
     * </BR>
     * Also, "<code>(@{)}@)</code>" will yield "<code>@{)}@</code>", ignoring the
     * first closing tag.
     * </BR>
     * 
     * @param string         The string to process.
     * @param beginTag       A String denoting the begin of a bracket-like structure.
     * @param endTag         A String denoting the end of a bracket-like structure.
     * @param ignoreBegTags  List of tags that denote the beginning parts where
     *                       occurrences of the tags are ignored.
     * @param ignoreEndTags  List of matching end tags.
     * @param beginTagPos    The position from which to start searching.
     * 
     * @return  A list of all the substrings between matching bracket pairs. If there is no such
     *          region, an empty list is returned.
     */
    public static LinkedList<String> extractSubstringsLevelwise(
            String string, 
            String beginTag, 
            String endTag, 
            LinkedList<String> ignoreBegTags,
            LinkedList<String> ignoreEndTags,
            int beginTagPos) {
        return extractSubstringsLevelwise(
                string, 
                beginTag, 
                endTag, 
                ignoreBegTags, 
                ignoreEndTags, 
                beginTagPos, 
                false, 
                new LinkedList<>());
    }
    
    /**
     * Does the actual work for {@link #extractSubstringsLevelwise(String, String, String, LinkedList, LinkedList, int)}.
     * 
     * @param string               The string to process.
     * @param beginTag             A String denoting the begin of a bracket-like structure.
     * @param endTag               A String denoting the end of a bracket-like structure.
     * @param ignoreBegTags        List of tags that denote the beginning parts where
     *                             occurrences of the tags are ignored.
     * @param ignoreEndTags        List of matching end tags.
     * @param beginTagPos          The position from which to start searching.
     * @param soFar                An initially empty list of substrings that is finally returned.
     * @param stopAfterFirstMatch  Iff only the first matching substring is supposed to be returned.
     *  
     * @return  A list of all the substrings between matching bracket pairs. If there is no such
     *          region, an empty list is returned.
     */
    private static LinkedList<String> extractSubstringsLevelwise(
            String string, 
            String beginTag, 
            String endTag, 
            LinkedList<String> ignoreBegTags,
            LinkedList<String> ignoreEndTags,
            int beginTagPos,
            boolean stopAfterFirstMatch,
            LinkedList<String> soFar) {
        int curBegin = string.indexOf(beginTag, beginTagPos);
       
        /* 
         * Find first non-ignored begin tag. This will be the start point of 
         * the string extracted by this method call. The end will be the first
         * non-ignored matching end tag.
         */
        while (curBegin >= 0
                && isWithinAnyLevelwise(string, curBegin, ignoreBegTags, ignoreEndTags)) {
            curBegin = string.indexOf(beginTag, curBegin + 1);
        }

        if (curBegin < 0) {
            return soFar;
        }
        
        Integer endPos = findMatchingEndTagLevelwise(string, beginTag, endTag,
                ignoreBegTags, ignoreEndTags, curBegin);
        
        if (endPos != null) {
            if (!stopAfterFirstMatch) { // Go on recursively with rest of string.
                extractSubstringsLevelwise(string.substring(endPos + endTag.length()), beginTag, endTag, ignoreBegTags, ignoreEndTags, 0, stopAfterFirstMatch, soFar);
            }
            soFar.add(string.substring(curBegin + beginTag.length(), endPos));
        }

        return soFar;
    }

    /**
     * Same as {@link #findMatchingEndTagLevelwise(String, String, String, LinkedList, LinkedList, int)},
     * but does not include ignored parts.
     * 
     * @param string         The string to process.
     * @param beginTag       The begin tag to look for.
     * @param endTag         The end tag to look for.
     * @param pos            The position of the begin tag.
     * 
     * @return  The position of the matching end tag or <code>null</code>.
     */
    public static Integer findMatchingEndTagLevelwise(String string,
            String beginTag, String endTag, int pos) {
        return findMatchingEndTagLevelwise(string, beginTag, endTag, new LinkedList<>(), new LinkedList<>(), pos);
    }

    /**
     * Same as {@link #findMatchingBegTagLevelwise(String, String, String, LinkedList, LinkedList, int)},
     * but does not include ignored parts.
     * 
     * @param string         The string to process.
     * @param beginTag       The begin tag to look for.
     * @param endTag         The end tag to look for.
     * @param pos            The position of the begin tag.
     * 
     * @return  The position of the matching end tag or <code>null</code>.
     */
    public static Integer findMatchingBegTagLevelwise(String string,
            String beginTag, String endTag, int pos) {
        return findMatchingBegTagLevelwise(string, beginTag, endTag, new LinkedList<>(), new LinkedList<>(), pos);
    }

    /**
     * Returns the position of the end tag matching the begin tag at the
     * position given as <code>pos</code>.
     * 
     * @param string         The string to process.
     * @param beginTag       The begin tag to look for.
     * @param endTag         The end tag to look for.
     * @param ignoreBegTags  The tags marking beginnings of ignored parts.
     * @param ignoreEndTags  The tags marking endings of ignored parts.
     * @param pos            The position of the begin tag.
     * 
     * @return  The position of the matching end tag or <code>null</code>.
     */
    public static Integer findMatchingEndTagLevelwise(String string,
            String beginTag, String endTag, LinkedList<String> ignoreBegTags,
            LinkedList<String> ignoreEndTags, int pos) {
        int count = 0;               // Because we start on a begin tag.
        
        for (int i = pos; i < string.length(); i++) {
            if (string.startsWith(beginTag, i)
                    && !isWithinAnyLevelwise(string, i, ignoreBegTags, ignoreEndTags)) {
                count++;
            }

            if (string.startsWith(endTag, i)
                    && !isWithinAnyLevelwise(string, i, ignoreBegTags, ignoreEndTags)) {
                count--;
            }
            
            if (count == 0) {
                return i;
            }
        }
        
        return null;
    }

    /**
     * Returns the position of the begin tag matching the end tag at the
     * position given as <code>pos</code>.
     * 
     * @param string         The string to process.
     * @param beginTag       The begin tag to look for.
     * @param endTag         The end tag to look for.
     * @param ignoreBegTags  The tags marking beginnings of ignored parts.
     * @param ignoreEndTags  The tags marking endings of ignored parts.
     * @param pos            The position of the begin tag.
     * 
     * @return  The position of the matching begin tag or <code>null</code>.
     */
    public static Integer findMatchingBegTagLevelwise(String string,
            String beginTag, String endTag, LinkedList<String> ignoreBegTags,
            LinkedList<String> ignoreEndTags, int pos) {
        int count = 0;               // Because we start on an end tag.
        
        for (int i = pos; i >= 0; i--) {
            if (string.startsWith(beginTag, i)
                    && !isWithinAnyLevelwise(string, i, ignoreBegTags, ignoreEndTags)) {
                count++;
            }

            if (string.startsWith(endTag, i)
                    && !isWithinAnyLevelwise(string, i, ignoreBegTags, ignoreEndTags)) {
                count--;
            }
            
            if (count == 0) {
                return i;
            }
        }
        
        return null;
    }
    
    /**
     * Retrieves the index of the begin of the first innermost bracket part in 
     * the given string. More precisely, the algorithm looks for the first
     * closing bracket in the string, and then returns to the matching opening
     * bracket, returning its position.
     * 
     * @param string  The string to process.
     * @param beg     The begin tag of the bracket-like structure.
     * @param end     The end tag of the bracket-like structure.
     * 
     * @return  The index of the begin tag of the first innermost bracket part.
     *          Returns {@code -1} if there is no such bracket part.
     */
    public static int indexOfFirstInnermostBeginBracket(String string, String beg, String end) {
       int firstBeg = string.indexOf(beg);
       int firstEnd = string.indexOf(end);
       
       if (firstBeg < 0 || firstEnd < 0 || firstBeg >= firstEnd) {
           return -1;
       }
       
       return findMatchingBegTagLevelwise(string, beg, end, firstEnd);
    }

    /**
     * Checks for several begin and end tags if the given position 
     * {@link #isWithinLevelwise(String, int, String, String)} of any of them.
     * 
     * @param string     The string to look at.
     * @param pos        A position in the string.
     * @param beginTags  A list of begin tags to look for.
     * @param endTags    A list of end tags to look for.
     * 
     * @return  Iff the position is (levelwise) enclosed by at least one 
     *          of the tag pairs.
     */
    public static boolean isWithinAnyLevelwise(String string, int pos, LinkedList<String> beginTags, LinkedList<String> endTags) {
        if (beginTags.size() != endTags.size()) {
            throw new RuntimeException("Tag lists have different sizes: " + beginTags + " vs. " + endTags);
        }
        
        if (beginTags.isEmpty()) {
            return false;
        }
        
        LinkedList<String> restBeg = new LinkedList<>(beginTags);
        LinkedList<String> restEnd = new LinkedList<>(endTags);
        
        restBeg.removeFirst();
        restEnd.removeFirst();
        
        return isWithinLevelwise(string, pos, beginTags.getFirst(), endTags.getFirst())
                || isWithinAnyLevelwise(string, pos, restBeg, restEnd);
    }
    
    /**
     * This method returns true iff the given position is within enclosing begin
     * and end tags, by ignoring sub-level tags. Note, however, that the method
     * does not check the syntax and strictly requires well-formatted strings.
     * It searches only from left to right for a closing tag, ignoring what
     * comes to the left.</BR>
     * </BR>
     * (()()e()) => e is enclosed.</BR>
     * (()())e(()) => e is not enclosed.</BR>
     * (e(()) => e is NOT(!) enclosed (due to malformed string).
     * 
     * @param string    The string to look at.
     * @param pos       A position in the string.
     * @param beginTag  The begin tag to look for.
     * @param endTag    The end tag to look for.
     * 
     * @return  Iff the position is (levelwise) enclosed by the tags.
     */
    public static boolean isWithinLevelwise(String string, int pos, String beginTag, String endTag) {
        int curPos = pos;
        int count = 0;

        while (count >= 0 && curPos < string.length()) {
            int curBegin = string.indexOf(beginTag, curPos + 1);
            int curEnd = string.indexOf(endTag, curPos + 1);
            
            if (curBegin < 0) {
                curBegin = curEnd;
            }
            
            if (curEnd < 0) {
                break;
            }
            
            if (curEnd > curBegin) {
                count++;
                curPos = curBegin;
            } else {
                count--;
                curPos = curEnd;
            }
        }
        
        return count < 0;
    }
    
    public static boolean isDouble(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private static final HashMap<IOTLComb, Integer> STORED_VALS = new HashMap<>();
    
    public static int indexOfOnTopLevel(String string, String find, int startPos, String beginTag, String endTag) {
        IOTLComb comb = new IOTLComb(string, find, startPos, beginTag, endTag);
        Integer result = STORED_VALS.get(comb);
        if (result != null) {
            return result;
        }
        
        int pos = startPos;
        while (pos >= 0 && pos < string.length()) {
            int nextPoint = string.indexOf(find, pos);
            int nextOpeningTag = string.indexOf(beginTag, pos);
            
            if (nextPoint < 0) {
                nextPoint = Integer.MAX_VALUE;
            }
            
            if (nextOpeningTag < 0) {
                nextOpeningTag = Integer.MAX_VALUE;
            }
            
            if (nextOpeningTag == nextPoint) {
                STORED_VALS.put(comb, -1);
                return -1;
            }
            
            if (nextPoint < nextOpeningTag) {
                STORED_VALS.put(comb, nextPoint);
                return nextPoint;
            } else {
                pos = findMatchingEndTagLevelwise(string, beginTag, endTag, nextOpeningTag);
            }
        }
        
        STORED_VALS.put(comb, -1);
        return -1;
    }
    
    public static double euclideanDistance(double[] array1, double[] array2) {
        double sum = 0.0;
        for (int i = 0; i < array1.length; i++) {
            sum = sum + Math.pow((array1[i] - array2[i]), 2.0);
        }
        return Math.sqrt(sum);
    }

    public static List<String> roundList(String decimalFormat, Double... nums) {
        return roundList(decimalFormat, Arrays.asList(nums));
    }

    public static List<String> roundList(String decimalFormat, List<Double> list) {
        ArrayList<String> listRounded = new ArrayList<>(list.size());
        
        DecimalFormat df = new DecimalFormat(decimalFormat);
        df.setRoundingMode(RoundingMode.HALF_UP);
        for (Number n : list) {
            Double d = n.doubleValue();
            listRounded.add(df.format(d));
        }
        
        return listRounded;
    }
}
