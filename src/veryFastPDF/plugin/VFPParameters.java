/*
 * File name:        PDFParameters.java (package eas.math.veryFastPDF.plugin)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  08.01.2015 (15:39:38)
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

package veryFastPDF.plugin;

import java.util.ArrayList;
import java.util.LinkedList;

import eas.startSetup.SingleParameter;
import eas.startSetup.parameterDatatypes.ArrayListString;
import eas.startSetup.parameterDatatypes.Datatypes;
import veryFastPDF.VFPVariables;
import veryFastPDF.script.RepresentableFactory;

/**
 * @author Lukas König
 */
public class VFPParameters {
    
    private static boolean studentVersion;
    
    public static void setStudentVersion(boolean studentVersion) {
        VFPParameters.studentVersion = studentVersion;
    }

    public static boolean isStudentVersion() {
        return VFPParameters.studentVersion;
    }
    
    public static LinkedList<SingleParameter> getParameters() {
        LinkedList<SingleParameter> list = new LinkedList<>();
        list.add(new SingleParameter(
                "studentVersion", 
                Datatypes.BOOLEAN, 
                false, 
                "If in student mode, a restricted set of representables is used.",
                VFPVariables.PROG_NAME_PDF_GEN_SHORT,
                VFPParameters.class));
        list.add(new SingleParameter(
                "specifyTypes", 
                Datatypes.STRING_ARR, 
                new ArrayListString(RepresentableFactory.getAllRepNames()),
                "If NOT in student mode, specify types of all representables to include (standard: all).",
                VFPVariables.PROG_NAME_PDF_GEN_SHORT,
                VFPParameters.class));
        return list;
    }
    
    private static ArrayListString specifyTypes;

    public static ArrayList<String> getSpecifyTypes() {
        ArrayList<String> list = new ArrayList<>(specifyTypes.size());
        list.addAll(specifyTypes);
        return list;
    }
    
    public static void setSpecifyTypes(ArrayListString specifyTypes) {
        VFPParameters.specifyTypes = specifyTypes;
    }
}
