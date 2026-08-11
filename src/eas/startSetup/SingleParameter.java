/*
 * Datei: SinglePar.java
 * Autor(en):        Lukas König
 * Java-Version:     6.0
 * Erstellt:         22.04.2009
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

package eas.startSetup;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;

import javax.swing.Icon;
import javax.swing.JFrame;

import eas.math.geometry.Vector2D;
import eas.math.matrix.Matrix;
import eas.miscellaneous.StaticMethods;
import eas.plugins.Plugin;
import eas.plugins.PluginFactory;
import eas.plugins.masterScheduler.MasterScheduler;
import eas.startSetup.parameterDatatypes.ArrayListBool;
import eas.startSetup.parameterDatatypes.ArrayListDouble;
import eas.startSetup.parameterDatatypes.ArrayListInt;
import eas.startSetup.parameterDatatypes.ArrayListLong;
import eas.startSetup.parameterDatatypes.ArrayListString;
import eas.startSetup.parameterDatatypes.ArrayListVec2D;
import eas.startSetup.parameterDatatypes.Datatypes;

/**
 * Implementation of a single parameter (static or generic, \ie. plugin-related) 
 * in EAS. To be on the safe side, try avoiding these symbols in all parameter 
 * names and values:
 * '"'</BR>
 * ' ' (allowed in "...")</BR>
 * '-' (allowed in values)</BR>
 * ','</BR>
 * '='</BR>
 * (All of them CAN be used, though, if you know what you are doing. cf. class
 * {@code ParCollection}.)
 * 
 * @author Lukas König
 */
public class SingleParameter implements Serializable {

    private static final long serialVersionUID = -3256024481095638733L;

    /**
     * Der Name des Parameters.
     */
    private String parNam;
    
    /**
     * Der Typ des Parameters.
     */
    private String parDataType;
    
    /**
     * Value of this parameter.
     */
    private Object value;
    
    /**
     * Eine Beschreibung des Parameters.
     */
    private String description;
    
    /**
     * Die Kategorie des Parameters.
     */
    private String category;
    
    /**
     * Ob der Parameter ein generischer Parameter ist, also durch ein Plugin
     * generiert wurde.
     */
    private boolean generic = false;

    /**
     * Ob das Element im Editor verändert wurde.
     */
    private boolean changed;
    
    /**
     * Ob die Kategorie angezeigt werden soll.
     */
    private int showCategory;
    
    /**
     * Ob der Parameter im Starter angezeigt werden soll.
     */
    private boolean anzeigen;

    /**
     * Das zum Parameter gehörende Bildchen.
     */
    private transient Icon icon;

    /**
     * The <code>listener</code> parameter can be used to connect a program parameter to
     * a Java class variable. For this purpose, a <code>Class</code> object, say C, can be
     * given to this method. The <code>ParCollection</code> initialization will
     * automatically try to set the static C.*pNam* variable via reflections by first calling the
     * static method C.set*PNam*(*pTyp*), if it exists. If not, a warning is printed
     * and the variable is set directly. For correct usage, the static field C.*pNam* as
     * well as the static method C.set*PNam*(.) have to be provided.
     * 
     * @param pNam      Name of this parameter.
     * @param pTyp      Type of this parameter.
     * @param stdVal    Value of this parameter.
     * @param listener  See JavaDoc of most comprehensive constructor below.
     */
    public SingleParameter(
            final String pNam, 
            final String pTyp,
            final Object stdVal,
            final Class<?> listener) {
        this(pNam, pTyp, stdVal, null, "MISC", listener);
    }

    /**
     * @param pNam    Name of this parameter.
     * @param pTyp    Type of this parameter.
     * @param stdVal  Value of this parameter.
     */
    public SingleParameter(
            final String pNam, 
            final String pTyp,
            final Object stdVal) {
        this(pNam, pTyp, stdVal, (String) null);
    }

    /**
     * @param pNam         Name of this parameter.
     * @param pTyp         Type of this parameter.
     * @param stdVal       Value of this parameter.
     * @param description  Additional description of this parameter.
     */
    public SingleParameter(
            final String pNam, 
            final String pTyp,
            final Object stdVal,
            final String description) {
        this(pNam, pTyp, stdVal, description, "MISC");
    }

    /**
     * @param pNam         Name of this parameter.
     * @param pTyp         Type of this parameter.
     * @param stdVal       Value of this parameter.
     * @param description  Additional description of this parameter.
     * @param category     Category of this parameter.
     */
    public SingleParameter(
            final String pNam, 
            final String pTyp,
            final Object stdVal,
            final String description,
            final String category) {
        this(pNam, pTyp, stdVal, description, category, null);
    }
    
    public SingleParameter(
            final String pNam, 
            final String pTyp,
            final Object stdVal,
            final String description,
            final String category,
            final Class<?> listener) {
        this(pNam, pTyp, stdVal, description, category, listener, true);
    }

    private Class<?> listener;
    
    public Class<?> getStaticListenerClass() {
        return this.listener;
    }
    
    /**
     * The <code>listener</code> parameter can be used to connect a program parameter to
     * a Java class variable. For this purpose, a <code>Class</code> object, say C, can be
     * given to this method. The <code>ParCollection</code> initialization will
     * automatically try to set the static C.*pNam* variable via reflections by first calling the
     * static method C.set*PNam*(*pTyp*), if it exists. If not, a warning is printed
     * and the variable is set directly. For correct usage, the static field C.*pNam* as
     * well as the static method C.set*PNam*(.) have to be provided.
     * 
     * At program start and at any time the respective parameter is changed, the ParCollection 
     * calls the setter method in "push" manner or, if the setter fails, sets the field directly.
     * This way of connecting a program parameter to a java field should be
     * preferred above checking parameters in "pull" manner using the getParValue...
     * methods of ParCollection as it is more efficient and secure.

     * @param pNam       Name of this parameter.
     * @param pTyp       Type of this parameter.
     * @param stdVal     Value of this parameter.
     * @param descript   Additional description of this parameter.
     * @param category   Category of this parameter.
     * @param listener   A class containing a static field named 'pNam' which
     *                   this parameter should be connected to. The parameter
     *                   collection provides a push service setting the field
     *                   automatically on ever change of the parameter.
     *                   Preferrably, the class should also contain a setter
     *                   named set'pNam' (setXy() for field xy).
     *                   (Caution: datentypes must be matching!).
     * @param warnAboutMissingSetter  Iff to produce a warning on missing setter.
     */
    public SingleParameter(
            final String pNam, 
            final String pTyp,
            final Object stdVal,
            final String descript,
            final String category,
            final Class<?> listener,
            final boolean warnAboutMissingSetter) {
        super();
        
        this.warnAboutMisingSetter = warnAboutMissingSetter;
        
        if (pNam != null && pNam.contains("-")) {
            throw new RuntimeException("Program parameters may not contain "
                    + "'-' in their names (parameter '" + pNam + "')");
        }
        
        if (listener != null) {
            this.listener = listener;
        }
        
        this.parNam = pNam;
        this.parDataType = pTyp;
        this.value = stdVal;
        this.changed = false;
        this.description = descript;
        this.category = category;
        this.showCategory = 0;
        this.anzeigen = true;
        if (!this.isConsistent()) {
            throw new RuntimeException(
                    "\n" 
                        + this.parNam 
                        + " - Parameter value \"" 
                        + this.value 
                        + "\" (" 
                        + this.value.getClass() 
                        + ")"
                        + " does not match parameter datatype " 
                        + this.parDataType
                        + ".");
        }
        
        this.generic = false;
        this.icon = StaticMethods.createImageIcon(
                "./sharedDirectory/icons" + File.separator + "staticParIcon.png", "Statischer Parameter.");
    }

    /**
     * @return  Ob der Parameterwert mit dem Parameterdatentyp übereinstimmt.
     *          Falls der Datentyp nicht spezifiziert ist, wird der Datentyp
     *          als String angenommen. Falls der Parameterwert 
     *          <code>null</code> ist, wird <code>true</code> zurückgegeben.
     *          Falls der Standardwert eine leere Liste ist, wird 
     *          <code>false</code> zurückgegeben.
     */
    public boolean isConsistent() {
        if (this.value == null) {
            return true;
        }

        if (this.parDataType.startsWith(Datatypes.FIXED_STRING_SET_PREFIX)) {
            if (this.value.getClass().equals(String.class)) {
                if (this.parDataType.replaceFirst(Datatypes.FIXED_STRING_SET_PREFIX + "-", "").contains(this.value.toString())) {
                    return true;
                }
            }
            return false;
        }

        if (this.parDataType.startsWith(Datatypes.INTEGER_RANGE_PREFIX)) {
            if (this.value.getClass().equals(Integer.class)) {
                int min;
                int max;
                String[] minmax = this.parDataType.replaceFirst(Datatypes.INTEGER_RANGE_PREFIX + "\\|", "").split("\\|");
                min = Integer.parseInt(minmax[0]);
                max = Integer.parseInt(minmax[1]);
                
                if (((Integer) this.getParValue()) >= min && ((Integer) this.getParValue()) <= max) {
                    return true;
                }
            }
            
            return false;
        }
        
        if (this.parDataType.startsWith(Datatypes.DOUBLE_RANGE_PREFIX)) {
            if (this.value.getClass().equals(Double.class)) {
                double min;
                double max;
                String[] minmax = this.parDataType.replaceFirst(Datatypes.DOUBLE_RANGE_PREFIX + "\\|", "").split("\\|");
                min = Double.parseDouble(minmax[0]);
                max = Double.parseDouble(minmax[1]);
                
                if (((Double) this.getParValue()) >= min && ((Double) this.getParValue()) <= max) {
                    return true;
                }
            }
            
            return false;
        }

//      if (this.stdValue.getClass().getSuperclass().equals(ArrayList.class)) {
//          if (((ArrayList) this.stdValue).size() == 0) {
//              return false;
//          }
//      }
        
        if (this.parDataType.equals(Datatypes.BOOLEAN)) {
            return this.value.getClass().equals(Boolean.class);
        }
        if (this.parDataType.equals(Datatypes.DOUBLE)) { 
            return this.value.getClass().equals(Double.class);
        }
        if (this.parDataType.equals(Datatypes.INTEGER)) {
            return this.value.getClass().equals(Integer.class);
        }
        if (this.parDataType.equals(Datatypes.LONG)) {
            return this.value.getClass().equals(Long.class);
        }
        if (this.parDataType.equals(Datatypes.STRING)) {
            return this.value.getClass().equals(String.class);
        }
        if (this.parDataType.equals(Datatypes.VECTOR2D)) {
            return this.value.getClass().equals(Vector2D.class);
        }
        
        // Matrix.
        if (this.parDataType.equals(Datatypes.MATRIX)) {
            return this.value.getClass().equals(Matrix.class);
        }
        
        // Arrays.
        if (this.parDataType.equals(Datatypes.BOOLEAN_ARR)) {
            return this.value.getClass().equals(ArrayListBool.class);
        }
        if (this.parDataType.equals(Datatypes.DOUBLE_ARR)) {
            return this.value.getClass().equals(ArrayListDouble.class);
        }
        if (this.parDataType.equals(Datatypes.INTEGER_ARR)) {
            return this.value.getClass().equals(ArrayListInt.class);
        }
        if (this.parDataType.equals(Datatypes.LONG_ARR)) {
            return this.value.getClass().equals(ArrayListLong.class);
        }
        if (this.parDataType.equals(Datatypes.STRING_ARR)) {
            return this.value.getClass().equals(ArrayListString.class);
        }
        if (this.parDataType.equals(Datatypes.VECTOR2D_ARR)) {
            return this.value.getClass().equals(ArrayListVec2D.class);
        }
        
        // Falls kein Datentyp spezifiziert ist, String annehmen.
        return this.value.getClass().equals(String.class);
    }
    
    /**
     * @return The name of this parameter.
     */
    public String getParameterName() {
        return this.parNam;
    }

    /**
     * @return  The datatype of this parameter.
     */
    public String getParameterType() {
        return this.parDataType;
    }

    /**
     * @return The parameter value of this parameter.
     */
    public Object getParValue() {
        if (this.value.toString().indexOf(" ") > 0 && this.value.toString().charAt(0) != '"') {
            return "\"" + this.value + "\"";
        } else {
            return this.value;
        }
    }
    
    @Override
    public String toString() {
        boolean b = true;
        if (b) {
            return this.parNam;
        }
        
        String s = "";
        
        if (this.anzeigen) {
            if (this.changed) {
                s += ""; // " => ";
            }

            if (this.showCategory % 2 == 0) {
                s = s + this.category + " | ";
            }

            s = s + this.parNam + " - \"" + this.value
                + "\"" + " (" + this.parDataType + ")";
            if (this.description != null && this.showCategory <= 1) {
                s = s + this.emptyString((70 - s.length()) * 2) + "     ["
                        + this.description + "]";
            }
        }
        
        return s;
    }

    /**
     * @return The showCategory property.
     */
    public int isShowCategory() {
        return this.showCategory;
    }

    /**
     * @param isCategoryShown The kategorieAnzeigen to set.
     */
    public void setShowCategory(final int isCategoryShown) {
        this.showCategory = isCategoryShown;
    }

    /**
     * @param num  A number of white spaces.
     * 
     * @return  String containing <code>num</code> white spaces.
     */
    private String emptyString(final int num) {
        String s = "";
        
        for (int i = 0; i < num; i++) {
            s += " ";
        }
        
        return s;
    }

    /**
     * @param value  The parameter value to set.
     */
    public void setParValue(final Object value) {
        this.value = value;
    }
    
    /**
     * Sets the changed property - meaning that the parameter has been
     * changed by the user since its initialization (what this exactly means 
     * depends on the UI method).
     */
    public void setChanged() {
        if (this.isPluginParameter()) {
            this.icon = StaticMethods
                    .createImageIcon("./sharedDirectory/icons" + File.separator + "genericParIconChanged.png",
                            "Statischer Parameter.");
        } else {
            this.icon = StaticMethods.createImageIcon(
                    "./sharedDirectory/icons" + File.separator + "staticParIconChanged.png", "Statischer Parameter.");
        }

        this.changed = true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((this.description == null) ? 0 : this.description
                        .hashCode());
        result = prime * result
                + ((this.category == null) ? 0 : this.category.hashCode());
        result = prime * result
                + ((this.parNam == null) ? 0 : this.parNam.hashCode());
        result = prime * result
                + ((this.parDataType == null) ? 0 : this.parDataType.hashCode());
        result = prime * result
                + ((this.value == null) ? 0 : this.value.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SingleParameter other = (SingleParameter) obj;
        if (this.description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!this.description.equals(other.description)) {
            return false;
        }
        if (this.category == null) {
            if (other.category != null) {
                return false;
            }
        } else if (!this.category.equals(other.category)) {
            return false;
        }
        if (this.parNam == null) {
            if (other.parNam != null) {
                return false;
            }
        } else if (!this.parNam.equals(other.parNam)) {
            return false;
        }
        if (this.parDataType == null) {
            if (other.parDataType != null) {
                return false;
            }
        } else if (!this.parDataType.equals(other.parDataType)) {
            return false;
        }
        if (this.value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!this.value.equals(other.value)) {
            return false;
        }
        return true;
    }

    /**
     * @return  If the parameter has been changed.
     */
    public boolean isChanged() {
        return this.changed;
    }
    
    /**
     * @param show  If the parameter is shown.
     */
    public void setShow(final boolean show) {
        this.anzeigen = show;
    }
    
    /**
     * @return  If this parameter is generic.
     */
    public boolean isPluginParameter() {
        return this.generic;
    }

    /**
     * Set this to be a generic parameter (cannot be undone).
     */
    public void setGeneric() {
        if (this.isChanged()) {
            this.icon = StaticMethods
                    .createImageIcon("./sharedDirectory/icons" + File.separator + "genericParIconChanged.png",
                            "Statischer Parameter.");
        } else {
            this.icon = StaticMethods.createImageIcon(
                    "./sharedDirectory/icons" + File.separator + "genericParIcon.png", "Statischer Parameter.");
        }

        this.generic = true;
    }
    
    /**
     * @return Returns the icon image.
     */
    public Icon getIcon() {
        if (this.anzeigen) {
            return this.icon;
        }
        
        return null;
    }
    
    /**
     * Can be overriden to return an image of this parameter.
     * 
     * @return  <code>null</code> here.
     */
    public BufferedImage getImage(@SuppressWarnings("unused") final ParCollection params) {
        return null;
    }
    
    /**
     * @return  A textual description of the parameter.
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Re-sets the parameter datatype to a new value - use carefully! (Even
     * better: don't ever use... leads to trouble almost always.)
     * 
     * @param newDatatype  The parameter datatype as defined in class Datatypes.
     */
    public void setParDatatype(String newDatatype) {
        this.parDataType = newDatatype;
    }

    /**
     * @return  The parameter category.
     */
    public String getParameterCategory() {
        return this.category;
    }

    boolean warnAboutMisingSetter = true;

    public boolean isWarnAboutMissingSetter() {
        return warnAboutMisingSetter;
    }
}
