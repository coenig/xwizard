/*
 * File name:        IOTLComb.java (package eas.math)
 * Author(s):        hq0976
 * Java version:     8.0 (at generation time)
 * Generation date:  18.03.2017 (14:41:32)
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

class IOTLComb {
    private String string;
    private String find;
    private int startPos;
    private String beginTag;
    private String endTag;

    public IOTLComb(String string, String find, int startPos,
            String beginTag, String endTag) {
        super();
        this.string = string;
        this.find = find;
        this.startPos = startPos;
        this.beginTag = beginTag;
        this.endTag = endTag;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.beginTag == null) ? 0 : this.beginTag.hashCode());
        result = prime * result
                + ((this.endTag == null) ? 0 : this.endTag.hashCode());
        result = prime * result
                + ((this.find == null) ? 0 : this.find.hashCode());
        result = prime * result + this.startPos;
        result = prime * result
                + ((this.string == null) ? 0 : this.string.hashCode());
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
        IOTLComb other = (IOTLComb) obj;
        if (this.beginTag == null) {
            if (other.beginTag != null)
                return false;
        } else if (!this.beginTag.equals(other.beginTag))
            return false;
        if (this.endTag == null) {
            if (other.endTag != null)
                return false;
        } else if (!this.endTag.equals(other.endTag))
            return false;
        if (this.find == null) {
            if (other.find != null)
                return false;
        } else if (!this.find.equals(other.find))
            return false;
        if (this.startPos != other.startPos)
            return false;
        if (this.string == null) {
            if (other.string != null)
                return false;
        } else if (!this.string.equals(other.string))
            return false;
        return true;
    }
}
