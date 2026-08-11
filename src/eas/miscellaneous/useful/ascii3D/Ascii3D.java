/*
 * File name:        Ascii3D.java (package eas.miscellaneous.useful.ascii3D)
 * Author(s):        Rosetta Code [1], Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  02.06.2014 (10:11:08)
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

package eas.miscellaneous.useful.ascii3D;

import eas.GlobalVariables;

/**
 * Main functionality taken from
 * [1] http://rosettacode.org/wiki/Write_language_name_in_3D_ASCII#Java
 * 
 * @author Rosetta Code [1], Lukas König
 */
public class Ascii3D {
    static char[]z={' ',' ','_','/',};
    static long[][]f={
        {87381,87381,87381,87381,87381,87381,87381,},
        {349525,375733,742837,742837,375733,349525,349525,},
        {742741,768853,742837,742837,768853,349525,349525,},
        {349525,375733,742741,742741,375733,349525,349525,},
        {349621,375733,742837,742837,375733,349525,349525,},
        {349525,375637,768949,742741,375733,349525,349525,},
        {351157,374101,768949,374101,374101,349525,349525,},
        {349525,375733,742837,742837,375733,349621,351157,},
        {742741,768853,742837,742837,742837,349525,349525,},
        {181,85,181,181,181,85,85,},
        {1461,1365,1461,1461,1461,1461,2901,},
        {742741,744277,767317,744277,742837,349525,349525,},
        {181,181,181,181,181,85,85,},
        {1431655765,3149249365L,3042661813L,3042661813L,3042661813L,1431655765,1431655765,},
        {349525,768853,742837,742837,742837,349525,349525,},
        {349525,375637,742837,742837,375637,349525,349525,},
        {349525,768853,742837,742837,768853,742741,742741,},
        {349525,375733,742837,742837,375733,349621,349621,},
        {349525,744373,767317,742741,742741,349525,349525,},
        {349525,375733,767317,351157,768853,349525,349525,},
        {374101,768949,374101,374101,351157,349525,349525,},
        {349525,742837,742837,742837,375733,349525,349525,},
        {5592405,11883957,11883957,5987157,5616981,5592405,5592405,},
        {366503875925L,778827027893L,778827027893L,392374737749L,368114513237L,366503875925L,366503875925L,},
        {349525,742837,375637,742837,742837,349525,349525,},
        {349525,742837,742837,742837,375733,349621,375637,},
        {349525,768949,351061,374101,768949,349525,349525,},
        {375637,742837,768949,742837,742837,349525,349525,},
        {768853,742837,768853,742837,768853,349525,349525,},
        {375733,742741,742741,742741,375733,349525,349525,},
        {192213,185709,185709,185709,192213,87381,87381,},
        {1817525,1791317,1817429,1791317,1817525,1398101,1398101,},
        {768949,742741,768853,742741,742741,349525,349525,},
        {375733,742741,744373,742837,375733,349525,349525,},
        {742837,742837,768949,742837,742837,349525,349525,},
        {48053,23381,23381,23381,48053,21845,21845,},
        {349621,349621,349621,742837,375637,349525,349525,},
        {742837,744277,767317,744277,742837,349525,349525,},
        {742741,742741,742741,742741,768949,349525,349525,},
        {11883957,12278709,11908533,11883957,11883957,5592405,5592405,},
        {11883957,12277173,11908533,11885493,11883957,5592405,5592405,},
        {375637,742837,742837,742837,375637,349525,349525,},
        {768853,742837,768853,742741,742741,349525,349525,},
        {6010197,11885397,11909973,11885397,6010293,5592405,5592405,},
        {768853,742837,768853,742837,742837,349525,349525,},
        {375733,742741,375637,349621,768853,349525,349525,},
        {12303285,5616981,5616981,5616981,5616981,5592405,5592405,},
        {742837,742837,742837,742837,375637,349525,349525,},
        {11883957,11883957,11883957,5987157,5616981,5592405,5592405,},
        {3042268597L,3042268597L,3042661813L,1532713813,1437971797,1431655765,1431655765,},
        {11883957,5987157,5616981,5987157,11883957,5592405,5592405,},
        {11883957,5987157,5616981,5616981,5616981,5592405,5592405,},
        {12303285,5593941,5616981,5985621,12303285,5592405,5592405,},};

    public static String create3D(String s) {
        String s3D = "";

        StringBuilder[] o = new StringBuilder[7];
        for (int i = 0; i < 7; i++)
            o[i] = new StringBuilder();
        for (int i = 0, l = s.length(); i < l; i++) {
            int c = s.charAt(i);
            if (65 <= c && c <= 90)
                c -= 39;
            else if (97 <= c && c <= 122)
                c -= 97;
            else
                c = -1;
            long[] d = f[++c];
            for (int j = 0; j < 7; j++) {
                StringBuilder b = new StringBuilder();
                long v = d[j];
                while (v > 0) {
                    b.append(z[(int) (v & 3)]);
                    v >>= 2;
                }
                o[j].append(b.reverse().toString());
            }
        }
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7 - i; j++)
                s3D += " ";
            s3D += o[i] + "\n";
        }
        
        return s3D;
    }

    public static void main(String[] args) {
        System.out.println(create3D(GlobalVariables.PROG_NAME_SHORT_EAS));
    }
}