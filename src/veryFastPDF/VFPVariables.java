package veryFastPDF;
import eas.GlobalVariables;
import veryFastPDF.web.ConvenienceMethods;

/*
 * File name:        VFPVariables.java (package )
 * Author(s):        hq0976
 * Java version:     8.0 (at generation time)
 * Generation date:  03.02.2017 (12:10:16)
 * Part of the EAS => VFP => XWizard webapp implementation.
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

/**
 * Global variables for VFP.
 * 
 * @author hq0976
 */
public class VFPVariables {

    public static final String MAIL_TO_LUKAS = HelpTexts.link("mailto:lkoenig.science@gmail.com", "lkoenig.science@gmail.com");

    private static final String COPYRIGHT_PERIOD = 
            GlobalVariables.COPYRIGHT_START_YEAR + "-" + GlobalVariables.COPYRIGHT_END_YEAR;

    /**
     * This date is used in case no date can be retrieved from the
     * compilation time information.
     */
    private static final String VERSION_DATE = "_2017-04-11";
    
    /* ************************************
     * Very Fast PDF (VFP).
     **************************************/
    
    /**
     * Name of the 2016 textbook by lko, fpf, hsch.
     */
    public static final String LB_NAME = "Theoretische Informatik - ganz praktisch";
    
    /**
     * Short program version of VFP.
     */
    public static final String PROG_VERSION_PDF_GEN_SHORT = "3_3.0";
    
    /**
     * Short program version of XWizard.
     */
    public static final String PROG_VERSION_XWIZZ_SHORT = "3_3.0";

    /**
     * Program name of the XWIZZ.
     */
    public static final String PROG_NAME_XWIZZ = "XWizard";

    /**
     * Scripting language name of XWIZZ scripts.
     */
    public static final String XWIZZ_SCRIPTING_LANGUAGE = PROG_NAME_XWIZZ + "-SCRIPT";

    public static final String LINK_TO_XWIZ_PROJECT = "https://sourceforge.net/projects/xwiz";
    public static final String LINK_TO_VFP = "https://sourceforge.net/projects/xwiz/files/XWizard_VFP.zip";

    /**
     * Complete program version of VFP.
     */
    public static final String PROG_VERSION_PDF_GEN = PROG_VERSION_PDF_GEN_SHORT + VERSION_DATE;

    /**
     * Program name of the VFP.
     */
    public static final String PROG_NAME_PDF_GEN = "Very Fast PDF Generator";

    /**
     * Short program name of the VFP.
     */
    public static final String PROG_NAME_PDF_GEN_SHORT = "VFP";

    private static final String GENERAL_COPYRIGHT_NOTES = "<B>" 
            + GlobalVariables.PROG_NAME_EAS + "</B> and <B>" + PROG_NAME_PDF_GEN
            + "</B> (also called PDF " + PROG_NAME_XWIZZ + " or " + PROG_NAME_XWIZZ 
            + " desktop version) as well as <B>" + PROG_NAME_XWIZZ 
            + "</B>, the Web version of the latter,"
            + " are open source programs; the complete sources, particularly code in Java, SQL, XML, HTML, LaTeXCode, GraphViz, " + XWIZZ_SCRIPTING_LANGUAGE + " etc., "
            + "native as well as generated, "
            + "are protected by the <B>Creative Commons by-nc-sa</B> license.\n \n"
            + "The complete sources as well as Javadoc for most Java classes are available from Sourceforge: " + HelpTexts.link(GlobalVariables.LINK_TO_EAS_PROJECT, "EAS on Sourceforge", true) + ", " + HelpTexts.link(LINK_TO_XWIZ_PROJECT, HelpTexts.XWIZZ_HTML + " on Sourceforge", true)
            + "<P><B>In a nutshell, you are free:</B>\n" +
            "<UL>" +
            "  <LI>to Share -- to copy, distribute and transmit the work,</LI>" + 
            "  <LI>to Remix -- to adapt the work.</LI>" + 
            "</UL>" +
            "The licensor cannot revoke these freedoms as long as you follow the license terms.</P>" +
            "<P><B>Under the following conditions:</B>\n" +
            "<UL>" +
            "  <LI>Attribution -- You must attribute the work in the manner specified by the " + 
            "   author or licensor (but not in any way that suggests that they endorse " + 
            "   you or your use of the work).</LI>" + 
            "  <LI>Noncommercial -- You may not use this work for commercial purposes.</LI>" + 
            "  <LI>Share Alike -- If you alter, transform, or build upon this work, you may " + 
            "   distribute the resulting work only under the same or a similar license to " + 
            "   this one.</LI>" + 
            "</UL>" +
            "No additional restrictions -- You may not apply legal terms or technological measures that "
            + "legally restrict others from doing anything the license permits.</P>" + 
            "<P><B>Detailed license conditions (Germany):</B>\n" + 
            "   " + HelpTexts.link("http://creativecommons.org/licenses/by-nc-sa/3.0/de/") + "</P>" + 
            "<P><B>Detailed license conditions (unported):</B>\n" + 
            "   " + HelpTexts.link("http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en") + "</P> \n" + 
            "&copy; " + COPYRIGHT_PERIOD
            + "\n"
            + "Lukas König,\n"
            + "Marlon Braun (red-black trees, 2-3-4 trees),\n"
            + "Marc Mültin (pat trees),\n"
            + "Nils Koster (web design),\n"
            + "Friederike Pfeiffer-Bohnen (web design)\n\n"
            + "Explanation and help texts by Lukas König, Friederike Pfeiffer-Bohnen and Micaela Wünsche; "
            + "where referenced, "
            + "passages from Wikipedia articles may have been adapted or borrowed for inspiration "
            + "(rarely by copying single sentences).";

    private static final String GENERAL_COPYRIGHT_NOTES_G = "<B>" 
            + GlobalVariables.PROG_NAME_EAS + "</B> und <B>" + PROG_NAME_PDF_GEN
            + "</B> (auch PDF-" + PROG_NAME_XWIZZ + " oder " + PROG_NAME_XWIZZ + "-Desktopversion genannt) sowie <B>" + PROG_NAME_XWIZZ + "</B>, die Web-Version des letztgenannten,"
            + " sind Open-Source-Programme; sämtliche Quelltexte, insbesondere Programmcode in Java, SQL, XML, HTML, LaTeXCode, GraphViz, " + XWIZZ_SCRIPTING_LANGUAGE + " usw., "
            + "ob nativ oder automatisch generiert, "
            + "sind geschützt durch die <B>Creative Commons by-nc-sa</B>-Lizenz.\n \n"
            + "Alle Quelltexte sowie Javadoc für die meisten Java-Klassen können bei Sourceforge heruntergeladen werden: " + HelpTexts.link(GlobalVariables.LINK_TO_EAS_PROJECT, "EAS auf Sourceforge", true) + ", " + HelpTexts.link(LINK_TO_XWIZ_PROJECT, HelpTexts.XWIZZ_HTML + " auf Sourceforge", true)
            + "<P> <B>Kurzgefasst dürfen Sie:</B>\n" +
            "<UL>" +
            "  <LI>Teilen -- das Material in jedwedem Format oder Medium vervielfältigen und weiterverbreiten,</LI>" + 
            "  <LI>Bearbeiten -- das Material remixen, verändern und darauf aufbauen.</LI>" + 
            "</UL>" +
            "Der Lizenzgeber kann diese Freiheiten nicht widerrufen solange Sie sich an die Lizenzbedingungen halten.\n</P>" + 
            "<P> <B>Unter folgenden Bedingungen:</B>\n" +
            "<UL>" +
            "  <LI>Namensnennung -- Sie müssen angemessene Urheber- und Rechteangaben machen, "
            + "einen Link zur Lizenz beifügen und angeben, ob Änderungen vorgenommen wurden. "
            + "Diese Angaben dürfen in jeder angemessenen Art und Weise gemacht werden, "
            + "allerdings nicht so, dass der Eindruck entsteht, der Lizenzgeber unterstütze "
            + "gerade Sie oder Ihre Nutzung besonders.</LI>" + 
            "  <LI>Nicht kommerziell -- Sie dürfen das Material nicht für kommerzielle Zwecke nutzen.</LI>" + 
            "  <LI>Weitergabe unter gleichen Bedingungen -- Wenn Sie das Material remixen, verändern oder "
            + "anderweitig direkt darauf aufbauen, dürfen Sie Ihre Beiträge nur unter derselben "
            + "Lizenz wie das Original verbreiten.</LI>" + 
            "</UL>" + 
            "Keine weiteren Einschränkungen -- Sie dürfen keine zusätzlichen Klauseln oder technische "
            + "Verfahren einsetzen, die anderen rechtlich irgendetwas untersagen, was die Lizenz erlaubt.</P>"
            + "<P><B>Detaillierte Lizenzbedingungen (Deutschland):</B>\n" + 
            "   " + HelpTexts.link("http://creativecommons.org/licenses/by-nc-sa/3.0/de/") + "</P>" + 
            "<P><B>Detaillierte Lizenzbedingungen (weltweit):</B>\n" + 
            "   " + HelpTexts.link("http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en") + "</P> \n" + 
            "&copy; " + COPYRIGHT_PERIOD
            + "\n"
            + "Lukas König,\n"
            + "Marlon Braun (red-black trees, 2-3-4 trees),\n"
            + "Marc Mültin (pat trees),\n"
            + "Nils Koster (Web-Design),\n"
            + "Friederike Pfeiffer-Bohnen (Web-Design)\n\n"
            + "Erklärungen und Hilfetexte: Lukas König, Friederike Pfeiffer-Bohnen und Micaela Wünsche; "
            + "wo referenziert, "
            + "können Wikipedia-Artikel als Vorlage oder zur Inspiration genutzt bzw. einzelne Sätze daraus kopiert worden sein.";

    private static final String VFP_INFO_FIRST_LINE = PROG_NAME_PDF_GEN + " (" + PROG_NAME_PDF_GEN_SHORT + ")\n"
                + "========================\n \n"
                + "This is "+ PROG_NAME_PDF_GEN_SHORT + ", version " + PROG_VERSION_PDF_GEN + ", part of the " + GlobalVariables.PROG_NAME_EAS + " (" + GlobalVariables.PROG_NAME_SHORT_EAS + "), version " + GlobalVariables.PROG_VERSION_EAS + ".\n"
                + "Minimal system requirements: Java V. " + GlobalVariables.JAVA_VERSION + ", GraphViz V. 2.34, SumatraPDF (or similar PDF viewer)." + "\n \n";

    private static final String VFP_INFO_FIRST_LINE_G = PROG_NAME_PDF_GEN + " (" + PROG_NAME_PDF_GEN_SHORT + ")\n"
            + "========================\n \n"
            + "Das ist "+ PROG_NAME_PDF_GEN_SHORT + ", Version " + PROG_VERSION_PDF_GEN + ", Teil von " + GlobalVariables.PROG_NAME_EAS + " (" + GlobalVariables.PROG_NAME_SHORT_EAS + "), Version " + GlobalVariables.PROG_VERSION_EAS + ".\n"
            + "Minimalanforderungen: Java V. " + GlobalVariables.JAVA_VERSION + ", GraphViz V. 2.34, SumatraPDF (oder ein ähnlicher PDF-Betrachter)." + "\n \n";

    public static final String VFP_INFO = VFP_INFO_FIRST_LINE + GENERAL_COPYRIGHT_NOTES;
    
    public static final String VFP_INFO_G = VFP_INFO_FIRST_LINE_G + GENERAL_COPYRIGHT_NOTES_G;
    
    /* ************************************
     * XWizard (Web version of VFP).
     **************************************/
    
    public static final String URL_TO_VFP_DOWNLOAD = LINK_TO_VFP;
    public static final String URL_TO_SOFT_LINK_TO_XWIZZ = "http://www.dasinfobuch.de/links/Wizz";
    public static final String URL_TO_DIRECT_XWIZZ_SERVER = "http://www.xwizard.de:8080/Wizz";
    // Root-relative equivalent for in-app navigation; resolves against whatever host/scheme was actually used to reach the app.
    public static final String URL_TO_DIRECT_XWIZZ_SERVER_RELATIVE = "/XWizard/Wizz";
    public static final String URL_TO_XWIZZ_SERVER_SIMPLE = "www.xwizard.de";

    /**
     * Complete program version of XWIZZ.
     */
    public static final String PROG_VERSION_XWIZZ = PROG_VERSION_XWIZZ_SHORT + VERSION_DATE;

    public static final String HTML_COPYRIGHT_PARAGRAPH_XWIZZ // Requires style class for simpleLink.
        = "<span style=\"white-space: nowrap;\"><b>"
        + PROG_NAME_XWIZZ
        + " "
        + PROG_VERSION_XWIZZ_SHORT
        + "</b> is the web version of " + HelpTexts.link(LINK_TO_VFP, PROG_NAME_PDF_GEN, true, "Link to ZIP download")
        + ".</span> "
        + "<span style=\"white-space: nowrap;\">&copy; Lukas K&ouml;nig et al., "
        + COPYRIGHT_PERIOD 
        + " &#124; "
        + HelpTexts.link("mailto:lkoenig.science@gmail.com", "Contact", false, "Email to webmaster")
        + "</span>";

    public static final String XWIZZ_COPYRIGHT_HTML = ""
            + "You are using <B>" + (PROG_NAME_XWIZZ + " version " + PROG_VERSION_XWIZZ + "</B>.\n\n"
            + GENERAL_COPYRIGHT_NOTES
            ).replace("\n", "<BR/>");
    
    public static final String XWIZZ_COPYRIGHT_HTML_G = ""
            + "Sie benutzen <B>" + (PROG_NAME_XWIZZ + ", Version " + PROG_VERSION_XWIZZ + "</B>.\n\n"
            + GENERAL_COPYRIGHT_NOTES_G
            ).replace("\n", "<BR/>");

    public static final String XWIZZ_IMPRESSUM_HTML = ""
            + "    <div id=\"impressum-wrapper\">\n" + 
            "        <h3>Provider</h3>\n" + 
            "<P>Provider of services in the sense of Art. 5 of the State Agreement on Media Services and Art. 55 of the German Act on Teleservices.</P>" +
            "        <h4>Registered Office</h4>\n" + 
            "            <p>\n" + 
            "                Karlsruhe Institute of Technology (KIT) </br>\n" + 
            "                Kaiserstraße 12 </br>\n" + 
            "                76131 Karlsruhe </br>\n" + 
            "                Germany </br>\n" + 
            "            </p>\n" + 
            "        <h4>Contact</h4>\n" + 
            "            <p>\n" + 
            "                Phone:   +49 721 608-0 </br>\n" + 
            "                Fax:    +49 721 608-44290 </br>\n" + 
            "                E-mail: " + HelpTexts.link("mailto:info@kit.edu", "info@kit.edu") + " </br>\n" + 
            "            </p>\n" + 
            "        <h4>Legal Form</h4>\n" + 
            "            <p>\n" + 
            "                Corporation governed by public law\n" + 
            "            </p>\n" + 
            "        <h4>Authorized Representatives</h4>\n" + 
            "            <p>\n" + 
            "                Prof. Dr. Holger Hanselka (Präsident des KIT)\n" + 
            "            </p>\n" + 
            "        <h4>Turnover Tax Identification Number</h4>\n" + 
            "            <p>" + 
            "                DE266749428\n" + 
            "            </p>" + 
            "        <h3>Responsible for Content</h3>\n" + 
            "        <p>\n" + 
            "            Institut of Applied Informatics and Formal Description Methods (AIFB),  " + 
            "            Research Group Efficient Algorithms </br>\n" + 
            "            Dr. Lukas König </br>\n" + 
            "            Dipl.-Wi.-Ing. Friederike Pfeiffer-Bohnen </br>\n" + 
            "        </p>\n" + 
            "        <p>\n" + 
            "            E-mail: " + MAIL_TO_LUKAS + "\n" + 
            "        </p>\n" + 
            "        \n" + 
            "        <h3>Disclaimer</h3>\n" + 
            "        <p>\n" + 
            "The contents of these web pages were compiled with due diligence. However, "
            + "Karlsruhe Institute of Technology shall not assume any liability, neither expressly nor implied, "
            + "for the type or correctness of the material offered and shall not be liable "
            + "(including liability for indirect loss or loss of profit) for the material or use of this material. "
            + "In case contents of websites of the Karlsruhe Institute of Technology violate valid legal regulations, "
            + "we kindly ask you to inform us immediately. We will then remove the site or the respective contents "
            + "as quickly as possible." + 
            "        </p>\n" +
            "        <H3>References to External Websites</H3>" + 
            "        <P>The " + PROG_NAME_XWIZZ + " websites contain links to information "
                    + "offered by servers which are not subject to the control and responsibility of "
                    + "the Karlsruhe Institute of Technology. Karlsruhe Institute of Technology shall "
                    + "not assume any responsibility or guarantee for this information and shall not "
                    + "approve of or support such information in terms of contents."
                    + "</P>" +
            "        <h3>Data Protection and IT Safety</h3>\n" + 
            "        <p>\n" + 
            "            The website " + HelpTexts.link(URL_TO_DIRECT_XWIZZ_SERVER_RELATIVE, "www.xwizard.de") + " does not "
                    + "set any cookies in the visitor's web browser." + 
            "        </p>\n" + 
            "    </div>";

    public static final String XWIZZ_IMPRESSUM_HTML_G = ""
            + "    <div id=\"impressum-wrapper\">\n" + 
            "        <h3>Anbieter</h3>\n" + 
            "<P>Diensteanbieter im Sinne von &sect;5 Telemediengesetz (TMG) und &sect;55 Rundfunkstaatsvertrag (RStV).</P>" +
            "        <h4>Rechtlicher Sitz</h4>\n" + 
            "            <p>\n" + 
            "                Karlsruher Institut für Technologie (KIT) </br>\n" + 
            "                Kaiserstraße 12 </br>\n" + 
            "                76131 Karlsruhe </br>\n" + 
            "                Deutschland </br>\n" + 
            "            </p>\n" + 
            "        <h4>Kontakt</h4>\n" + 
            "            <p>\n" + 
            "                Tel.:   +49 721 608-0 </br>\n" + 
            "                Fax:    +49 721 608-44290 </br>\n" + 
            "                E-Mail: " + HelpTexts.link("mailto:info@kit.edu", "info@kit.edu") + " </br>\n" + 
            "            </p>\n" + 
            "        <h4>Rechtsform</h4>\n" + 
            "            <p>\n" + 
            "                Körperschaft des öffentlichen Rechts\n" + 
            "            </p>\n" + 
            "        <h4>Vertretungsberechtigt</h4>\n" + 
            "            <p>\n" + 
            "                Prof. Dr. Holger Hanselka (Präsident des KIT)\n" + 
            "            </p>\n" + 
            "        <h4>USt-Identifikationsnummer</h4>\n" + 
            "            <p>" + 
            "                DE266749428\n" + 
            "            </p>" + 
            "        <h3>Verantwortlich für den Inhalt</h3>\n" + 
            "        <p>\n" + 
            "            Institut für Angewandte Informatik und Formale Beschreibungsverfahren (AIFB),  " + 
            "            Forschungsgruppe Effiziente Algorithmen </br>\n" + 
            "            Dr. Lukas König </br>\n" + 
            "            Dipl.-Wi.-Ing. Friederike Pfeiffer-Bohnen </br>\n" + 
            "        </p>\n" + 
            "        <p>\n" + 
            "            E-Mail: " + MAIL_TO_LUKAS + "\n" + 
            "        </p>\n" + 
            "        \n" + 
            "        <h3>Haftung</h3>\n" + 
            "        <p>\n" + 
            "            Der Inhalt dieser Website wurde mit gebührender Sorgfalt zusammengestellt. Das Karlsruher Institut für Technologie " + 
            "            übernimmt aber keine Garantie, weder ausdrücklich noch implizit, für die Art oder Richtigkeit des dargebotenen " + 
            "            Materials und übernimmt keine Haftung (einschließlich Haftung für indirekten Verlust oder Gewinn- oder Umsatzverluste) " + 
            "            bezüglich des Materials bzw. der Nutzung dieses Materials. Sollten Inhalte gegen geltende Rechtsvorschriften verstoßen, " + 
            "            dann bitten wir um umgehende Benachrichtigung. Wir werden den betreffenden Inhalt dann schnellstmöglich entfernen." + 
            "        </p>\n" +
            "        <H3>Verweise auf externe Web-Seiten</H3>" + 
            "        <P>Die " + PROG_NAME_XWIZZ + "-Seiten enthalten Verweise (Links) zu Informationsangeboten auf Servern, " + 
            "           die nicht der Kontrolle und Verantwortlichkeit des Karlsruher Instituts f&uuml;r Technologies unterliegen. " +
            "           Das Karlsruher Institut f&uuml;r Technologie &uuml;bernimmt keine Verantwortung und keine Garantie " +
            "           f&uuml;r diese Informationen und billigt oder unterst&uuml;tzt diese auch nicht inhaltlich.</P>" +
            "        <h3>Datenschutz und IT-Sicherheit</h3>\n" + 
            "        <p>\n" + 
            "            Die Seite " + HelpTexts.link(URL_TO_DIRECT_XWIZZ_SERVER_RELATIVE, "www.xwizard.de") + " setzt keine Cookies im " +
            "            Browser des Benutzers." + 
            "        </p>\n" + 
            "    </div>";

    public static final String URL_TO_ASK_QUESTION = "http://info2.aifb.kit.edu/qa/index.php?qa=ask&cat=228";
    public static final String URL_TO_QUESTION_CATALOG = "http://info2.aifb.kit.edu/qa/index.php?qa=activity&qa_1=infoii-generator";

    public static final String URL_PAR_LANGUAGE = "lang";
    public static final String LANGUAGE_ENGLISH = "eng";
    public static final String LANGUAGE_GERMAN = "ger";
    
    public static final String QUARTER_AS_MANY_WHITE_SPACES = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
    public static final String HALF_AS_MANY_WHITE_SPACES = QUARTER_AS_MANY_WHITE_SPACES + QUARTER_AS_MANY_WHITE_SPACES;
    public static final String MANY_WHITE_SPACES = HALF_AS_MANY_WHITE_SPACES + HALF_AS_MANY_WHITE_SPACES;

    public static final String DEUTSCH_LINK =
            MANY_WHITE_SPACES
            + "&gt;&gt;&gt; <a class=\"simpleLink\" href=\""
            + URL_TO_DIRECT_XWIZZ_SERVER_RELATIVE + "?" + URL_PAR_LANGUAGE + "=" + LANGUAGE_GERMAN
            + "\">DEUTSCH</a> &lt;&lt;&lt;";
    
    public static final String ENGLISH_LINK =
            MANY_WHITE_SPACES
            + "&gt;&gt;&gt; <a class=\"simpleLink\" href=\""
            + URL_TO_DIRECT_XWIZZ_SERVER_RELATIVE + "?" + URL_PAR_LANGUAGE + "=" + LANGUAGE_ENGLISH
            + "\">ENGLISH</a> &lt;&lt;&lt;";

    public static final String BLITZUMFRAGE_NAME = "3-minute survey";
    public static final String BLITZUMFRAGE_NAME_G = "3-Minuten-Umfrage";
    public static final String BLITZUMFRAGE_NOTICE = "Tell us what you like and dislike - it only takes 3 minutes";
    public static final String BLITZUMFRAGE_NOTICE_G = "Teile uns mit, was du gut oder schlecht findest - es dauert nur 3 Minuten";

    public static final String SURVEY_LINK = "https://www.soscisurvey.de/XWizard";
    
    public static final String HTML_WELCOME_TEXT_XWIZZ_ENG = "\n<ul><p>"
//            + "You can " + DEUTSCH_LINK + "\n<ul>"
            + "<li>Enter a script and hit <b>Draw!</b> "
            + "The result is shown as a graph in the <a class=\"simpleLink\" href=\"#Output\">output area</a>. Feel free to play around!</li>"
            + "<li>Click on one of the <a class=\"simpleLink\" href=\"#Examples\">" + "examples" + "</a> to get the general idea.</li>"
            + "<li>Use the <B>conversion methods</B> to apply algorithms to the script.</li>"
            + "<li>Or solve the <B>exercise</B> if there is one shown above the script.</li>"
            + "</ul></p>"
            + "<p>Overwhelmed...? Bored...? " + HelpTexts.link(URL_TO_QUESTION_CATALOG, "Join discussions group", true, "Within the Info II Question/Answer platform") + " "
            + "or tell us what you like and dislike by taking a " 
            + HelpTexts.link(SURVEY_LINK, BLITZUMFRAGE_NAME, true, BLITZUMFRAGE_NOTICE)
            + ".";
    
    public static final String HTML_WELCOME_TEXT_XWIZZ_GER = "\n<ul><p>"
//            + ENGLISH_LINK 
            + "<li>Tippe ein Skript in das <a class=\"simpleLink\" href=\"#Codebox\">Textfeld</a> und klicke auf <b>Draw!</b> "
            + "Das Ergebnis wird im <a class=\"simpleLink\" href=\"#Output\">Ausgabebereich</a> angezeigt.</li>"
            + "<li>Nutze die <a class=\"simpleLink\" href=\"#Examples\">" + "Beispiele ganz unten" + "</a> zum Einstieg.</li>"
            + "<li>Durch die <B>Konversions-Methoden</B> k&ouml;nnen Algorithmen auf das Skript angewendet werden.</li>"
            + "<li>Oder l&ouml;se die <B>&Uuml;bungsaufgabe</B>, falls eine angezeigt wird.</li>"
            + "</ul></p>"
            + "<p>&Uuml;berfordert...? Gelangweilt...? "
            + HelpTexts.link(URL_TO_QUESTION_CATALOG, "Beteilige Dich an Diskussionen", true, "Im Info-II-Forum")
            + " oder sage uns, was gut bzw. schlecht ist, in der " 
            + HelpTexts.link(SURVEY_LINK, BLITZUMFRAGE_NAME_G, true, BLITZUMFRAGE_NOTICE_G)
            + ".";

    public static final String HTML_WELCOME_BACK_TEXT_XWIZZ = HTML_WELCOME_TEXT_XWIZZ_ENG.replace("Welcome to ", "Welcome *back* to ");
    
    public static final String GENERAL_ERROR_MESSAGE_XWIZZ = "<i><font color=\"red\">Something went wrong during script conversion. "
            + "Often this is just a timing problem which disappears at a second trial. "
            + "Another likely possibility is that the script is syntactically incorrect or that its interpretation took too much time.<BR/><BR/>"
            + "The error trace follows below.<BR/><BR/>"
            + "</i><div style=\"border-style: solid;\"><B>"
            + "<font color=\"black\">Try repeating the last action, usually this should work!</font>"
            + "</div></B><i><BR/><BR/>"
            + "If the script is correct and doesn't take lots of time, please contact <a class=\"simpleLink\" href=\"mailto:lkoenig.science@gmail.com\">lkoenig.science@gmail.com</a>, "
            + "and accept his appologies for any "
            + "inconveniences.</font></i>";

    public static final String GENERAL_ERROR_MESSAGE_XWIZZ_G = ConvenienceMethods.replaceSpecialCharsHTML_G(
              "<i><font color=\"red\">Etwas ist während der Skript-Interpretation schiefgelaufen. "
              + "Oft ist es nur ein Timing-Problem. Eine andere Möglichkeit ist, dass das eingegebene Skript syntaktisch fehlerhaft ist "
              + "oder dass die Berechnung zu lange gedauert hätte. "
            + "<BR/><BR/>"
            + "Im Anschluss folgt eine detaillierte Auflistung der Fehlerursache.<BR/><BR/>"
            + "</i><div style=\"border-style: solid;\"><B>"
            + "<font color=\"black\">Versuche die letzte Aktion erneut auszuführen, meistens klappt das!</font>"
            + "</div></B><i><BR/><BR/>"
            + "Wenn das Skript korrekt ist und kein Timing-Problem vorliegt, schicke bitte eine Email "
            + "mit Details zum aufgetretenen Fehler an "
            + "<a class=\"simpleLink\" href=\"mailto:lkoenig.science@gmail.com\">lkoenig.science@gmail.com</a>. "
            + "Wir bitten um Entschuldigung für das Problem!.</font></i>");
    
    public static String xwizzLogo(double widthPercent) {
        double newWidth = widthPercent;
        
        return "<a href=\"" + URL_TO_DIRECT_XWIZZ_SERVER_RELATIVE + "\">" +
            "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"" + 
            newWidth + 
            "%\" "
            + "viewBox=\"0 0 229.606 233.858\" version=\"1.1\">\n" + 
            "<defs>\n" + 
            "<image id=\"image7\" width=\"802\" height=\"175\" xlink:href=\"data:image/png;base64,"
            + "iVBORw0KGgoAAAANSUhEUgAAAyIAAACvCAAAAAAk4DUZAAAAAmJLR0QA/4ePzL8AAB2sSURBVHic7Z1XcyP"
            + "HlufPOVkWHvRkG7baSX2lO1d3TOzjfvaJ2JjYmNmN2RlJV1cttSebFgThUSbz7AMdUFUgYUmgkL8HRQgNFs"
            + "rkvzKPTQCNRqPRaDQazUwQD/z7aBnMD3wOGs0t4MP9dHazuJ5xpIl+s9v+WA1AS0UzhzycRHDrH5+brC50g"
            + "YTdD3v7Zw92NhrNAB5MIkR/+b5IqudMKGwd/d8TNfhPNJoH4MFsEcZOuYg9aytWYK98mz0NHuqMNJokHtBc"
            + "96GQwT4DhJlWc+dKPtgpaTQxHlAifC5zNkVWelS0aqwnEs388JBOX64ZJaJ+RxZTyVQtPY9o5oYHjYuoMzN"
            + "HIqIRkbfMevhQp6TRRHjY0KGsWMKlS4lcLrlYWJZT0fOIZk544Oh6WAXKRs4BTTsDVe381cwHD52AErQ9Ki"
            + "D3gWTm/IbWiGYuGCgRJDSEmP049f2mk6e+jxjJddpNnY+imQeMAZ8XtwtrKw4yKlk/fFcNZ6eVdvizsYX9J"
            + "jvAxhv/SM8jmjlgwCyy8ebbx2ULEZBEduvNiy3b82d0BqyaQc6lyKeUM+uhttk1D88AiVjfr4JSF6aBUsos"
            + "PPnzX3MNfyZrH4Y6FGyMxkfyVJNaI5oHZ4BEfKcUqeNgXnuzlgtmohJ1LvImxGKITJ3xNGJs5E0Y6k/Jypk"
            + "6lq+5jQES4RMzK/oNBGDO73z341ajM32VqDPbjaaiMOUltMfSiGr6uz/+z79umT7cas9gbvPld487rREuyF"
            + "gvAA5VBGaKjBVql8PiMzgZPvNqZzs+FhDg8Kdaa+qWtP0vKytRewSb1Y8fxzSBKFv664YIWpWvX5JPloT75"
            + "HneZaz99G6kWL714/YafPh84N86+2Bme70EP30d/kahWBH1IBhGVAZZ0tPOjHvitnoRK/M/dpJew4jdxn8e"
            + "TtlOyH63tUb94wOhc/rp47jrIMSX35cAgKh9dn583O9DNpx8ZuVR2VSs1Id/74524ML2yy3hV2tfD5rJ6rP"
            + "ctd0N05LNX/8+0tlb//zUlgfvTrq33lt0H+9k8Ke9kTSCJio5yl+gLgK95I6SKrHxYz4Tv1eIcPrL3nQ9wa"
            + "W1lxvEfSeE7Nf+9mX8fC3nnx7bggGIWJ1//NQIr0ocS7u7BQOVAkBWn/5ffcTRgPj8hxICAgdfTuu1fqEYl"
            + "FtbL6+bilWw979HVN/W45114Z8dnFaSQ6dkZsubW3mDm7/9bbR3B2Y2d1eq+/uBN8yX7fLKxsE7baUBDFN1"
            + "WFp7tkOJc8n52fujKd7F3OaLreg8osLjvx1OoMTs492rYxKwbDT2jiBXdDeLDsDVKrL++6+jq9D58RUKBiA"
            + "hw8bnj+eXBcYk8ru7JYNZAaDyKv9RG1F95Gz9sIIEwM3jr61qt+/aySiuFjfWTMWqe/xvnRFPmdztP6+rWu"
            + "P089mt1ytEbvNRIWtW/vV8xF9IJ0MV5oryXx7FP2WksPG3z8O8lYaC7I3v13pcBAgAwGrvl9MJpnzKbv5Do"
            + "eeYQvkgDJTXH6Hy3v48jtDzfy6vEgMAAoLqeicHZ5DNrxYLmWv1Se+3X0ZXn/3XFwYCAAnltT9/PL88Alnu"
            + "02dlgxUDILQa/1EZ/baI7/6UFQCqu1dtVNqJrx60imvP12zF8vxfdSsBgOFr1wtvymsJ6SiMVHt3VJ3OXEL"
            + "W4+9WIm40wPDjL/WJVnTmX17YvQcEZAC+vnBWe/91Po4IaWP1VelGakjKZ1P0qk8Gv//3OLcm99e1/OWaE1"
            + "F6J83KsSqsOauFLPLlrQjbH8YRNub/YSNPDChAtvY/nPavlRGzO89K6BAyI3f++62uSYCR2jsYhe+fJnwdy"
            + "at+/jDamnsARK+/zUfNRAz/eNuaSINY+MdiTgxQATIf/3Qw3jxF4odXbs+fIgIj96hPHv7XGO96ALGz8bTY"
            + "oz4Ejy1x1S4GADBsfv3PsW4K5jcfPaXL44btTvVrtakYANDgbPbpxqpQzACAzF/+16wyKhaKETJ9Vefze5G"
            + "JRkuAFWV2fnhhdNXE1jurmsgZsTh7UYaTuTi9j7WG6URP/PLwCNw5G+/wrI6+2q64eW8w9547ggwqjbEOXD"
            + "/+5BdMvjqqUsIA5h71IUJ1VGPkAq921Fk1GQCY0cquvfjTt2tZI6CVN//0w5tNmy8UAowyOJnKi2/RGS0ZP"
            + "tj/9d2BLEYDGABsbj8tOt7ECy5ZdTIUHcsiF4jkhfOQuNvbK5loaPIKBquY84aKRyTQ/VT1hT1AfQSiczqm"
            + "+oLjr1ZvBjQz96kvDCuN8c6Zg5PfwrIBF3YUKylKj3a/f/V4rWCwvHEpIlOzqh2/Y9SLBI39P7hoxrzmbJQ"
            + "fvXntNCcMKMtK3jGjg9nMGDy+Rsh59vpJKWkYX/2Oufb61aPdvDeGF9td3VzPcPydAQAADFTKeeP6M7qfTg"
            + "2yBqkPWR6PfUvUyalA6/qsmQlNy+hZyAEAK6if6iS5sVvN5bbWt52EMUfeu73GeAuAK/Lfl6I2O3Kz+mVvL"
            + "NvRXF8trmYxeaD1gxQefWqcjTITGubTb1YNFUtC6LmriN75WeOoPvpgs4tb60VrULUCqGD/jwnql2l1c3fl"
            + "ti8gyLN/q419/PQwdjdGYbx8lUlKViLp7b2tT3BK5e0n5Vh8pNv87evo40G4heflrMXD/iWhH7YPv54Hw7y"
            + "fzc21Qtk26G71IaI8/qPSGUl9uY1na2ZUfX2PC7F7et74enss/pZfML59nfCau/mpoPbvlfEOnSomalhqfr"
            + "uTt5Ji78Gnt82xHYZYWP1mPTqPqPD475URFxZWYfPpCqpRV35I0KxUvt4RNyNj9flaxoBhD08YdtqV982hZ"
            + "CLWnjjFrEF3J4Egojz548gb426TWfzmsXuZYJ3kqlTyy//xdR7KhD19jfz2N4XEuSSsn34YM/QksuXXq7G1"
            + "ltr7+0jWI22srTk5IQVcWFzXVhdzdFgjXazErjxIgMjQOf5QD/0Bb+hMbvPRCo2sPhDQODw7rd5x5kbuxZM"
            + "MjqK+Zr26Xx/JWeI42e1yYYCb7xr0T04Ox/T3pYUptL0WL166CStmUurol/EmarJ2XhejGsHw46+jpK0DRc"
            + "u0AAAcABtXt91M36ey2VZO/AAk28dJqca4sVUq2DHtJagPERF6tAcAxNw63DsLB/k1shsrq6uCRx6WRM290"
            + "7NhjQd3Z7dsG/Lu+0kYyi+fKry8IZKpdIYX2xubhaQZGdXJ0cEYmT4kXr3IXHolr4+FwftfB73VR8Tc2N0x"
            + "enJQ5O9/vxwCdPGTePV/yEmlj+TEakYcABsANx/ZVt/nshWq+E1GETar75PUV368mXFIEUa8jQnqgxsvxGW"
            + "GGEvv8I9zuMOrKNzi+k7GJBh+8yPBrfr+x6nlGi0U09o8AWn3ZSlpwaW8xs8nox/PfPNNzE1L3tsP3SlN+s"
            + "L97tmVSBDk/tuxouAJmC9XtnvUJ/3Pv/gAUe0BASIEyerrv0QHAGwABwuP+juOsQrayeprHB4MHs1orJZWV"
            + "oq3LeOSBgUiqpOPe1N6Ry0SU9xfhLafF93EuYQr76qjRpntPz+NBfKx/f74fFp5Q7j93epFpiSyrP42ST5x"
            + "P07u6fNr9XG3+uskaZi9mN9ulXrV1zn42UvQHiBCovoAAMjeerZhKMU41qMnksHnP5YrvXGKrea48fmPd9X"
            + "IKv+C7OOtErdGOpqsZjOxcIZp26ozrbHc+HIOwmIAQKXq02ttF7YPz5zLu8AsZXVaHcHUycFxULx6YghKNd"
            + "sXYfeLJhxKKaWklDIcZOiYK5uPnpZNYBREaIheKMFyQyDCSy7sKmZaff0sY8vlqSWZ+i5VuPF61UxY5CLy+"
            + "W97owyWwnflfMyvVTs72Z/eXG+ubu3aClEFv/8+1WcuNt9kHAAAlLWPn6Z3aBSr323gRZYIdxrv90cUNgkR"
            + "rSN1L9ZyDkBxYyXaP4B9jxOGCBOiPHg//frseWQWG7lhYeXxeuKBG1+OR2jWu/povRSLj3SrH8ZPvIhDxut"
            + "XBij5/o8pd60wH29s2cwkm/tvp+oNMja/LQsGAAyaX6ZbGEhP/pTrjYSi8vd/9rl3IXedwQVAFAzhEVt8Zt"
            + "LT1zv/emjmEsLO1vo3r5+oYetgu6E0Io57JrLs1hQTUFmentkOsZx2Yxd1fvglLBqspFeb6jhWjb2uzAhg5"
            + "K4/3SQqrn1uBa55cyOQodZi6Om3rG6QM2zROU/MqO216uz99jUpBZbR3n4iusPZ3F4Q2k7/dMREWaM5Te8j"
            + "t76ceQGfjxRzGerAwUnFyqFU9faU1Xd2uO8XDeag0xg3Q3kAslo59ks3tTWye9Ke6g8sIDPsDN89OTyAnOC"
            + "IYcJsbrx8+ZSGqOpm35MZs/clxsyIOWhM11hsVSpNmpob4AZufTlBGTSmrj7VPTk18yS73akfOuwc3WThI5"
            + "K39NtYzHTzhLBz8HYP4wUgzGRt7hp3p3FxWDOy0dx4FHmoDx/1Ggblt5L7/UxM+7jWwhmoD9p7FVN2vTFrR"
            + "m7FO6jnBQEDsAxbS7/N98z3F/GPDmpBxoDoVMJi9dXzlbtKpZhrVrSmlplysj16ftTDoLrNGW2V0jpotcNp"
            + "hVL74PqnesuxARAC73x53LvJ3MMWPEH98PfPskAYdR8yFTa/sRu3zyWqls1GDX82sszegmhkdqhWY0ZzH6h"
            + "6teIVBXCozicr/1l87mmXquB0zwNHRINTDFR++cI2bnsXyvOsEz1LFFYIs+lTr7lAdU6+qiJxpzl1c2fBuL"
            + "eN3MLq3ruPftaM/QPjyuZm+RYDPGzbZEZ8v2ibluou+bObNf5JzSUMljwXfiahw1uwHn9jJsfe1d7bYNCSa"
            + "/VxKRplQdWqHCz7w5s9aGaM2pL3t79niQCAmSs9yybmBB/VjpIdNLhe2snG4uxe9d2ozXg1mpG5/x1zVbf2"
            + "pZWz4y1UILu++yqXWDvXCaVpRdRMIiNay1voo7kvHmRTaVX/+P68Y9ocmcSYofB8pSNiMzv7HmQjlY2MmIW"
            + "WbqmpmTEPte+6alUPalk3VtjD7D5+9my1EZkeOKyLrBGJj6DIqVmEzjSaHh5KIgAsm5/fn1toxqYMxsyz1a"
            + "7qb3zGDSe2rS5TRo25HaJGMyQPJxEAAG4dHdeseKkiK/vRN4+LQW9KrzzPZmNNRw03UEvucNHMmIeVCAAHj"
            + "f33FdchiBkmRv7xpt+9kY9sZGI9TdC0ApxytqtG08tDSwQAgDuHJ20j2tYAmNncerFuXofRg3aGKBZDtFDn"
            + "omhmyDxIBIC98733x0409gHAbK9t7Fwl6/meC0ZUIxZRuJzdazT3wnxIBAAAvKMjYZoU7XvDZG483/ZDCQB"
            + "eCEY0N54s0x6pW65GMwpzJBEA//jL530rltgLwOb21orwJHeUsiIbNzAaNun4iGZWzJVEAEAFJwdQiPV7Zk"
            + "WZ9Wdbqt31VSZ6zkQ5aGnfr2Y2zJtEACCsfNyrYja2rQ2zubFjqiZnojVWQBm5HA1rNPfP/acxDgeJJ4/sp"
            + "FGPSjFdyefm7LufDpeiY43m3pnDWQQAAFhVP3+R4JCC6MYzJBJ0bTjhoB6dGs0kzOsscgG5K0+d20f+VQs0"
            + "Vf/amEUbBc2yM6+zyAUc1L98kq6gpK6Z/d9EyxSh9v1qps58SwQAgGtf656dsF9c5GtoMSqtEc20me+F1g2"
            + "4vWvf0TsLZefsaOl7B2qmzfzPIpc09ltsxLd774HJsEVHxxAnxLDEdPv4LTqLMotcIFaeuWrwOaNS+1/1Wm"
            + "tCDHe9UDtv60XrJYslEQBYfeRYg/8Vu/tH2vc7MfmXZdVpnZ6N0hrgjr13F5eFkwgAuLlHOR506p2900XpZ"
            + "TrHiI0nhgXSq1W99p2zCdqlsiUan1Laa2MRJQIApZ1i8qkjN/drOs4+Oc52MUeMCLJ7cjo44CRI2IVMwaWg"
            + "+X7U7SwXhAWVCADkS+XchfUeuQb/uJXYaUgzEuhkn+QAABDYbzbazYTCNXLLRTsjgBnCxruUNv9dXIkAGO5"
            + "OKZbsCAAAsnNQ1SbJpBDtbtxsvI3+2XG7pwbazphuMUMCAZiRg+6Xajon70WWCADg1oYtEp4MQvf4lFO6OL"
            + "5HMs8KNxlxCH4nqFU9BUiGXS7aluBLLzyC396rPthpzpQFlwjgWjlnJUZLELxm7UyHSSZDbOcKfT3+kMKmR"
            + "7ZrAF+pAwAAwtbxaTpT5BYmdDgIz1Cidx7p0bxwS+uGrxdck8D1akNmqKc9DaOTy9gUDS9Kz0/pLgsLLxHu"
            + "KrSSLRIAoPzWqiGWZGvX2cBes+1avVGPiz0nI18Lg6aXztu88BIBFfjkRNeLPf9vFItrlq4lmQDVOQ5svGt"
            + "JHvp3bMq3qCy+RIDDpmNHP+vbY1fkNlcp0JlH40ImKJsHTtUAABC0O14630OLbq4DAADZT/KxD6NXJtuN85"
            + "lsnplyDNcuZN0+2yNh0KBqNM9a93dW90gqJAKQuYhy3QEGlWPd3XR40KBMPmvaIGCIKRiZmycp3NIqJRKBw"
            + "k5mmK+hbNWbun53OMyMXcjaNDDlLT52EMJGu5ayZjRpkQiWN83h7CpUXD3SHU5vR5hmuWgLxdE+48OA2Dmt"
            + "d1NjmKRFIoBrJceIPk5EBkaIVr6jbFXaQbredVNEOK7jWrbBd9+hQcMH2e+eV9ORKZcaiQCt5TLR7XxQKQ/"
            + "cpGtEFXQOU5p3NzGuS0AEZCCgABICwQS89GgNP6sgdurt9sKXSqdHImDkSrnoWguVf1otrFsGMET7cTG3K+"
            + "faE3wXBEiEYCASIhIhoenacQ/w5c2N7P8CKjw7W+xXUYokAiJbLlxcT89VSXlcBTebyyVfaatV7Sb+g2Ywi"
            + "NlNO1L7icoPGBGRCAAJqTfWGAaN84XtupwmiQA5m/H4CHRO6goQ8+uZ2E5YAMhheKYT50eGSvmc2fP/yMFZ"
            + "JQAABEQgECAQTCA0wETDEEjcrLQX8j6nSiJA1k4uvnJqnzQYADBTNhM2ZgBAaJ12dOL8aKBhrpV7Rg+q+pF"
            + "/a141wTDRlfkjBQkoPXDoWbHmD2iQrxgAgnrtrGFaCW8Fs1gqWN1FfH4Phwrqnn3jH2HVatxeeRBPfVwM0i"
            + "URgEDaRuQjRpPCSwem8mt1oIS5k8zsRjHUofeR6NaUhddd+mU3HU7eKGmTCPgcS0plNAHklZM/bFTO22Bjv"
            + "OxdlMq2rcMlI6BabQlXr6QheqUsJKmTCHhgRmcJFiahvBn7yqvXlAVxS4zs3FpJ6tD78ATtlm9dDKIwpTtO"
            + "pk8i0EUyYluKGobqW0XJVqXpQ0IDVKbiikm8kL6Xh4DDToMcZADVSac1l0KJgA8Q3aWHkUyIWBphp1GVCc0"
            + "hGJ1iaUXpuWRYZKNjmIRhx9cSWRA4lGbUZgckW0atcVbtsxog3VSd4vW38yuWLsIaFr/JQOSlsxwnjRIBJQ"
            + "NhRj9EkbhFj2w1WmFCkjCjs7KSQz2XDIVqNVtKJjSjSwHpCh1eQ9a6GzPaoVMZlFNnlMoiIUEPmZvVxc4wu"
            + "j9QQCo7xaZyFgHgMIzFRwCFGNTmQbVrHUUxu4QBrPKKgekrpZsFKpXrrLRKBCCUsWzUixjigNHOfrNWlXbv"
            + "XHIxCzG4uWJG6p51y0pqJQIBmwkaYb5lawXu1jqCY8F3BjILK4YOKi4n6ZUI+NcbtN8MebL51gUzB41GPbB"
            + "i5YsA4BRzTjoX25pbSbFEoGtQPD5iINxuWbDy6i0DMbrrEoOw8iXdtG7pSLNEwCeMXh+jEHdoBIDDRq3tm0"
            + "mtgq1SloSeS5aJVEuEAxCxC0S0hrG9w269hdEmjwDAws0VHF/bJUtDqiUCHIZGgkbMcCj/lGyfd5ESirDQL"
            + "OaIx2ifo1lA0i0RYOVZSfGRYX24YbvV4ligHoDJyWdtpe2SJSDlEgHmMGFrhbvNkWtU2Km1TbpuudbjHbPy"
            + "WdJBxdSTdokAh8qIa8TEcIR1kmy2gjChoJfJKWbM2yItqQRFFpZo/ky9RABCjJaPAKDBI6VLKL9TaxnRTnY"
            + "AwMJ2c8Zy7fEjjHxBDtGqMSUsgUTAJyO2gQxZatSHrNpNqaKBFmBGtAou4rIMGXSypQyZ3tLMncsgEfCJYi"
            + "MbxigtZL/T6ptLro8qHMe1l2IuEXbOMYDRXJrczqWQCAQmxuwRJAGj5yayajclQkK/TmHmM8N7AeYGMpykX"
            + "bkH4hQdAgYgWJZsnOWQCAcs4hpBGstry3673eWYKxmAyc3Y5oJZsqyUVcxlLXOYPnBE+ax5mZlDBi/eC2Ec"
            + "UlpSFcOw3evwxvU1S9kYt6oQ0c0mWO8AyLLVWSyVAAjLcQUAKPaCIFCDxIKG7dpXuWuogkY6+zlEWBaJgLB"
            + "y8Rc/e80JGtuQY0diLpd3U/mdResOQejmDAZAQFZSBn4QX0YZObu/ja+sLEOb16WRCFCiRrqtSWqlEO1sQu"
            + "wdAJT0Fq7Js5kzzOsOfAzMoQwDeW1fCStrQ3/+s7cMDcOXRyIAbiZBI532hN5LzLgxTzDAxSY/3oL5uIRpu"
            + "X3WKQIwy8D3FWDGjRuu3fMFu8IxWCaJYCYTjyFyqzuxh9807KRm2gAq9L1Fq+i1c/HG4QDqwokVhVvN1Jsj"
            + "y+HRuiQ5zq5g0jehCrttZURSghEA0LBca8FcXLLbZe5XAzMDxqKvgABoqNS7fpdKIhBQ7E3IYPDEGgGAoNN"
            + "VkBChBGFnDFyoULTyup3wxkl+60KDTC/tRQHLJREIBMXehog0lRe9CrrtMB5+AQDTcpzFUgmH3YSUhKQvog"
            + "UjhR4Xj2WyRQAAKGknSym7U/NeGq4Rn6oAAMBvy8WybcmwE25WHGQOw1DKhXoJDM+ySQSEGe9BB0pOsfE/C"
            + "uEmxN4BQIZysdreIloDLiX+VQYGVgFLuWBvgrtYOokAGU7sqTP43aka1cI14+YtADAEncVycZlurB3ZbSAA"
            + "sJJhKDktdvzySQTIcAkiV84w9bbmhmFGo4oIAKD8yb3M9waa9kgKgcuLRAZmpQIl1aJucXjNEkoEwHYTKgi"
            + "9qcf5EC07yR0SBosSUiTDsqP9xGCUQXM5qSgZLq5zeMk8WhfIpMsW09/ymEMvycRBgIVwlKLtmvFA0igHYM"
            + "V8kU1t0GJccwJDGmMpw8N4NBztkesQhyAMicjua4CKGC9eGYghHmwjIBSmgaONawQAYEBgpZiZpWK18M2Ul"
            + "nKhBYBOQs8H5c+qszWaxmVAhoFVEKjhh43popQPkHSOaFkjLTFYKWYlWcHCi6KfJZUIYFLuoQzDmSWLICIS"
            + "EDDIgfUYiZAriGX3nv1giKZhUGysX92zeK4NcCulFVbLKhFAy4yvtaScw67WaNgCAML7PLcLgURgQGalEJj"
            + "jPS4BVGv+7t00WFqJAJlJay3lz99zRnHhPmYO7mmPEzJNAgTASxcGM4NSSjFc+TTIMqJZ8xDe91R3PyyvRI"
            + "CM+DwCLOexo/XNlMfhcP2IJ/s500RmZpbMPMgTRSii6SlBmMYqxCWWCBAlVXmEc7kblXVzqkoFs1YJXuST3"
            + "Ilw+hdcoZ/CeWQp4yKXMCe9IWguo8FSXT8pJMNK2t53EJQQ/LuT4f6Eg0CpnqmEKIVNGpd5FgGAxLVWOJeu"
            + "GbL7sr74jtNEQCIivPOLk2PcNLlg7szjvZuIJZcIGAnhYzWXSy0gI+KnRg6TGkAiICEiAiIiAN/DuozMqwI"
            + "AVqlrHLTsEgEQ8SooOZfTCIAwMZp+Kfkm9RwBEAUi9E43zPdg3wMAGZdJm+GCtUe6Ey0RAMR+lfDcNuelpH"
            + "gESwmEl1MGQqSPDyt5X25scZEUHEyv9GYu0BK5RODlvWAYLfp9vxiJSXWMMMAmZ3mvGbamQIBFyWMeEi2RB"
            + "SNpHonS81ClvGe9I5mYLo0ss9N3EcH+l9pdb7jRNhqaChxKY35n4THQs8higACIgFGJ3A7jAxUyjROJmVu0"
            + "ROYeBKTbn9Ogf2RIWaeFB0FLZF65nDcmeEDMKYx13z/LWXU45yAg8mUL3eGXLPptNxu0uT6f8PWQ762NHVE"
            + "FzFo2k6Pv4XxDABM9pIVv0fPwaIksED3urFH8WlolE6Elsnjg9X+GRqtkfLREFpiR3F1aJWOiJZJm+p+u1o"
            + "hGo9FoNBqNRqPRaDQajUaj0Wg0Go1Go9FoNBqNRqPRaDQajUaj0Wg0Go1Go9FoNBrNBPx/0ejXbnzCq7QAA"
            + "AAASUVORK5CYII=\"/>\n" + 
            "<mask id=\"mask0\">\n" + 
            "<use xlink:href=\"#image7\" transform=\"matrix(1,0,0,1,-0.000000000000001235,0.000000000000010991)\"/>\n" + 
            "</mask>\n" + 
            "<image id=\"image6\" width=\"802\" height=\"175\" xlink:href=\"data:image/png;base64,"
            + "iVBORw0KGgoAAAANSUhEUgAAAyIAAACvCAIAAACO6f2SAAAABmJLR0QA/wD/AP+gvaeTAAAgAElEQVR4nO2dV5fjxrWFC5lg7jAz0ijZS9GyH+z//z9khWXZo6VRmNTdTMjAf"
            + "diXZ9WAJIjuZgDI/T3MYnMAEASBql3nnNqlFCGEEEIIIYQQQgghhBBCCCHkrDGOfQJnjWEYhvH/P0Ge58c9GUIIIYTsFsqsQ2Oapv7asizDMIqiUEplWaaLLQovQgghpNVQZh"
            + "0OiV0ZhjEajUaj0dXV1dOnT8fjsW3bWZZFUTSfz2ez2W+//fbnn38GQQD5VRQFXhBCCCGkRVBmHQgEsSCzPv3003/+85/j8dj3fT24pRMEwS+//PL9999PJpMgCNI0xfsMcRF"
            + "CCCFtwTr2CZwLpXDU8+fPR6ORbdubtrdt++rq6uuvv3769OlwOFRKzWazoigkw0gIIYSQhkOZdTgMw0DsKk3TPM/7/b5pmo7jVO/V7/fH4/FgMIiiiEqLEEIIaRGUWQelKArT"
            + "NPM8v7u7i+NYKeX7vuu61XvZtt3tdnu9Hiq3qLQIIYSQVkCZdWhEac1mszzP0zQdDAYV2UNgmuZwOHRddz6fLxYLKi1CCCGk+VBmHQGIpDzP5/N5FEWGYYzH40218DrD4dA0z"
            + "dlsFoYhDkKxRQghhDQWyqzjIEorDMPZbGZZ1vX19epmYl4qfw6Hw6IoZrNZmqYQWFRahBBCSDOhzDoautJ69epVt9u9vLzcupdpmqPRKM/zxWKRZVmWZXiTSosQQghpGpRZx0"
            + "S0UZqmd3d3/X4f3g3VWJbV6/WiKDJNMwxDKi1CCCGkmVBmHR9kBsMwnE6ng8FgMBhs3cV13cFgkKapYRhRFMG8lEqLEEIIaRS7kVlYmE/HNE3TNEtvKkqBdUgBVhAEQRAMh0P"
            + "f94ttuK7b6XRc143jeLFYwB2edVqEEEJIc9jiI1AHXTn1er3Ly8snT550Oh1oL1QRTafT29vbt2/f5nkuU+q4VB/ANcG1+u2331zX/fvf/z4ajbY6l45GI8/zcJ1fvnwp2UMu"
            + "yEMIIYQ0gR3ILERQTNN8+vTpBx988OWXX/Z6vU0bz+fzP/7449WrV7/++iv8n/D+mSuDPM8R8Muy7MWLF57nffTRR0+ePPE8r3pHz/OeP39u23Ycx69evTrzy0gIIYQ0ih0kD"
            + "ZHzKooCy8JcX19b1sbDOo5zcXHx8ccff/bZZ4PBIAiCMAxxEKQazzm+Jc6lt7e3juPkee77fsXFBJZlDYdDy7IWi0UQBC1yLpW0Ml4c5Rzwucf6dEIIIafNDmQWxIFSKgiCOI"
            + "6jKHry5MnWfstxnOvr66+++urbb7/tdDpIIKKU+5xdN8Xl4e7uLkmSoihGo1EdETAYDAzDgHOpask1xI8u365Uxicvdo4IuxJHv1yrZ3XIU9rfBSeEkLNlNyXwUmUVBMGbN29"
            + "++OEHx3Gurq5qnYFlPXny5LPPPnv27NnFxUWSJBKSaULPd3jw3bMsm8/n8/nctu2aflowg5jNZtBnqiUV8brSEnaugUpzMjadyWM+4vGUdKfSroPaZ8itmbpz9ayOeDKEEPIw"
            + "dmnoIJPg0jT99ddf//3vfyMYg1RgNYZhdLvdq6urzz///Ntvv+33+4vFIkkSpfU0R+8FD4Yk/lBx1e12x+Px1r1M0+z3+3meB0GQ53mL/LREpld0paW+9l6dbp2FjFQDZBbAa"
            + "ZTOeR+6U9cxqxs04YmriHeuVee7opm6U5DTw78NOStCyFp275slyinLsj/++OO777773//+9/LlyzzPLy4u6rSMpmleXl5+8cUXo9EI1fRnuISfZA/zPJ9Op8PhsNvtbu1abN"
            + "uGc2mapkmStM7lYTWcs0qp59skoQzDEJ+ROh/dtGDJfXXnvWij7ix909I9sBPdqTQFs7pB056jVfV5+NNb1aOtGNcRckgO0bVIm25Z1jfffPOPf/zDtu83w7EoihcvXvz0009"
            + "v375FkAac/MQ66UefPXv2r3/9a+26h6tMJpOffvrp9vb29evXKHdTLbxWNaVACXzNh+2rmnqVan4difHkeb7a1T1AizTNcqXmdXjAj7i/I++V1dMu/cR7OmFjObt87f827SoR"
            + "clwO4QKvJwdfv3793Xff/fe//4WjZq/Xq9PAGYYxHo8///zzi4uLoigsy4qi6BziW3JxgiCIomg4HHY6nYrtock8z+v3+6ZppmkahmFLWz3JnN4rYPPgAI9qXrhCqBPk01nNc"
            + "z1Mdzbtgug1BhWb1Uz26Xm3vZ3yfpELIqG40jfdeaDLqEw0y1nt5LMIOQ0OvdgOGoUkSV6/fv3LL7/8+OOP8/n86upqq20BGAwGn3766RdffPHll19eX1/HcSz5RNW8vM/jka"
            + "9WFMVsNkvTdDQa2ba9tdf0PM91Xc/zoiiCP1nTusw64IQrxs27peH3z8OqkR6sO5s8hjmW7mxsRmzrWdWUnpv2xeWqKUkbe9sQchSOs6YhWkmUab979+7777//+eef0zT1fd+"
            + "27TpPMsyi/vrXvz558iTP8zAMJfJxYg+59ChFUUynU9M0IaG2XiXXdV3X7fV6up/WYc555zymazwlStGLM0eP4tTf6yTjnWBrPZ+OLrmqN3vARW7yVSLkwBx/6Wh0G2ma/vnn"
            + "nz/99NPLly/v7u7qLDUDer3eJ5988re//e3DDz9Eib3uB7Hvkz8YUnPz7t27IAiyLHNdd+slchzH8zzHcebzufhptbcFvG8M44Sh7gSlW4L3hnpQ1HM11rXV/YQQUpPjyywgw"
            + "/Qoit6+ffvjjz/+5z//CYKgvt7yff/y8vLTTz999uyZUioMQxTLn0x8S2Jak8nkjz/++PPPP5Mk2Tp5M47jOI7TNM2yLI5j1fKuaN/CQvqYvX7Krtir7mzRfSKl+gc451Y0Jo"
            + "+Pej4m4KcanF0l5PA0RWYJ0kBkWQa99fbt2ziOEZWpc4Rut/vRRx99/fXXH374oWmatm3D2uAE9JZeqhXH8Zs3b6bTaRRFURQZS/cH+ZpRFIVhCPsxSIckSdI0xUFa1w4euFT"
            + "5YVUsh4e6U+cA8c6G3w8ljhj9bdFVImSvtGDAKv2rZVl/+ctfvv7664qlqVeJ43g2m7148eLly5cy7a6lk++A9Hmo0/rggw8Gg4FSSqbx62VqankBsVTiq1ev0jRti8uDZVkN"
            + "bKybcNGOmM0xDEM3VWkae1WEorEafhFWObBQbt31IWR/NC6atYqMUFGZBPcs13Vt267jv2VZlu/7H3zwwVdfffXxxx8rpaIoUstxXrvGpkByAahpS9PUcRwUxcuSMqXSCnxZb"
            + "JOmqQiyJse0GhtBOWKUSwxXj5vR00N9RzwNHePg1gxtiXeCUl71ABeq+deEkMPQlFbyXiDIYVlWp9O5vr7+8ssvLy4u6u8eBMG7d+9+//33169fw75LNSNEUR8962cYxmg0ur"
            + "6+9n2/uvXEEPPm5mY6nc7ncxluNuq7N1ZdbWXfl7HJVwY33uEDGHgKmqP2VMOephIHvoWafCkIORgtiGatIiOzNE1vb29//vnnV69e+b5fM5lo2/ZgMHj+/PkXX3zx6aefWpY"
            + "FZyn1IKfsY6G3mIjPwQuj2oHMNE0EApVSssJ0c2iyktjKnmIbW90gm4Me4DlAoPQBRgMHoMlRrgNfK+N9mnY1CDkMrZRZoNAIw/DFixc//PDD77//vlgsLMvqdrt1DuK67rNn"
            + "zz755BPf9x3HwYw81YZC19IgPgxD1PtXp06QcDRN07Isy7LiOEbVfEO+7CO7gcdnsnbSD+2qdzHaU3u+lr3qzlZcmYbojIYo9aNfB0KOQrMGgo9EKqZN0xwOh998881HH31Uf"
            + "/c0TWez2d3d3du3b1+/fj2fz3G0hoe+5VtblnV1deX7Plbaqd4ry7IgCCaTye3tLZSlxAhPiQcX0Vf/6Ft7LEMriTMeUQ78+An5j/xN99EpPviBaoJW2AkHaFIsy5KV4/f9WY"
            + "+k4Q0sIY+kxdGsVfT4VhzHv/766w8//PDq1Ss4QXieV707Ju45jmMYxmAwGA6HWZZJP9fYQZheEY+plMay2r1iL6QXbdvW/bSa+QUfw31n+O+qTxLN+kjxWuyae+k2/ZbAtIl"
            + "Vah5HafHXx+QTH/AD6fGkxx+zUfHOOh+x8yPvnBadKiEP4MTvbOlUXNcdj8fPnz9HMdam7bMsWywWd3d3QRCoZZOapmkURe/evbu7u9N7nf2f/j2Qb2rbtud5l5eXo9GoouVC"
            + "414UxXw+10N3j5QFTUaa8tWOzVhaYOjvN+0n3iEP6NVO9ca4V7xTv09Wb486l3TtNo+Jd5ZoRS61Jif8AJKz4sRllqB7TVmW1e/3P/zww06n0+/3HcexLCtJkiiK0jSFhydiQ"
            + "qWDwKF+MpnACFQ1rO/Rv6PjOE+fPh0Oh1ub/qIoZrPZmzdvZB6AOukGrtQP6ddH/yl32PM1lgrdKRucg+7ceh3W0qhnv5kOczukUVebkHtxLjJL0MfxKAPv9/uXl5ee50kvsn"
            + "WsD4P1yWQynU4bFQTSlZbv+0+ePOn3+9W7GIaR5/nd3d2bN2/iOMYXOXmRsdotlbrYk78Cgn4pSrf9WV0Q/amXqJVauQiN1Z0PniW9qzK+1cPur0ls1JUnpJqTqs2qiS6JUAl"
            + "+c3Nze3urlMJMva2Bd9u2EQm7uLiwbTsMw2JZ7HzcCie9bxDn0upFirALthGZpU60VEuQ0iK9Zz3P6pC1NVtrf/0Tvh9A6ZYAm4qomvaArK2c2wn6M1LzZIz3q/pWz3DtoUqX"
            + "V399to8nOQHOUWYJ+gOfZdl8PkdVVhzHtm1vXUIR+cdut/vkyZPRaITC3izLjq63pD2CM5bjOLDUqtgF5f94jXUP936WzWBTi6+a14/ulU0Ko/TiHKDuLLFVfZZYe4S1xyyxd"
            + "YO1WxLScM5aZgH90S2KIo7jxWJxc3Nzc3ODaFC14SdA8hHzEz3Pkxl/R+mf9A/FqtKYPll9PqjoQu8iK0yfQ4smv/7qxTmHr6+z6TqAM7kfQLXulD/P7YKoem4mZ6XLCamGMu"
            + "s99KFSnueLxeL29jaKojzPYf5ZvTu28X3/+vp6PB6bpok0HNqdgy0gWOoeoiiyLEuUVsVeKO+wbTvPc0TCzq0jUZuL4s+HTR3qWd0MYPVSlK7JuV0QQXe6Wf3fUsrvbK8SIYo"
            + "yaxN6iCuKotlsdnNz8/bt2yRJkIOrbjggWfr9/mg06vV6juMkSSL5xAM0OrrSKooiiiJIKMuyqivPrCWGYYhGPKtWkrkJQUYI+PPc7gQd6s4SxX082M72KhGiKLO2oucOiqII"
            + "w/D29naxWCC/Vr2yjVquIdjr9a6urqReXi3j6nuNbxVLQ3y1TIbic7fWaallAhH2Fuq8u5MzhzUxOqU48Zk/F2vzqoSQEpRZtdB7GsMwkiTBMjU3NzdhGEq9fHWjY5pmt9sdj"
            + "8edTsc0zTzPDxDfkoBEnufwA1NKVXvEQ//Ztm3bNqYrqrPvUQgB1J0lKLYIqYYy695Is1IURZ7nURTd3d3NZjOpfyoqMQzDdd1+vz8ej8fjMerlS9mZnZ8wjgxhlyQJdGG1Ip"
            + "S4VxzHspz2wcrLCCEtQkTn2laF7QY5ZyizHkhJOaVpOplMbm5uZrNZHMfIFVYfAZVSvu8jvoWgkeit3bZKutJCebtUmG1CaXMP4zgW8xu2mISQTWyKbLHRIGcLZdYO0ONbaZo"
            + "GQTCdTieTCcRWhWYSQYMlFy8vL8fjseu6+vzEXTVPhVZhlqZplmWu66LUvWIXhOiUUkmS6C75bDQJIWtZjWwxpUjOGcqsnaEHt5RSaZpifuJ8Podgqum/5XnexcVFt9vNsmzn"
            + "ZqfS3qG2XYyyKr4UxGK+hHUYhJA6sIiNEEWZtQ/0wRxCR7PZbDabzedzpZTneXXWeXUcZzQaXV1d+b5vWVaWZTuJb5X8tFAoVme+pOM4MFyVM2HTSQghhFRDmbVH9PgWKqIQ3"
            + "5pMJoZhyOI21RLHcZxerzccDofDYZZlj5z3V4pFYVkhmGnVUVoSqKPSIoQQQrZCmXUI9GRiURRYP3EymaRpahgGMnfVkgU5x+FweHV11ev1YNGuHrSuhT6rsSiKMAyRzdya08"
            + "QajuIRr1gOTwghhFRCmXU49EoF+GaFYTidTm9vbyeTiVLK87ytmqkoCtu2x+PxYDDodrsw8VJLvVVT9OgFqkVRxHGMWvitzqX4FDiXSkyr5tcnhBBCzg3KrOMgYkst41uLxeL"
            + "u7i7Pc1nrpkIzQd8gn3h5eTkYDODYLvXyNc9BXB4wkVDmFW5CVuyxLAvTFRUnHhJCCCEboMw6Jnp8C2IlCIK7u7vb29vpdJrnued5dRYOsyxrNBoNh0Pk9erXyxeaRzwKv5AW"
            + "3Lo96rToXEoIIYRUQJnVFERsQRtlWQbJlWUZJidWx6gQG/N9fzAYIKWIvB4OiCjUWhkkQTUorTiOXdetUFpAdy6VwzKsRQghhOhQZjWLVacZLFZ9c3OzWCzyPN9qLl8sDdwxO"
            + "VHEGewYNsWc9PL8PM9t297qp4WYVrFclJoQQgghJSizGo1eq56m6WKxuL29jeO40+lsDThBCWExn+FwOBgMlFKQRKv1W/qfqPGq6afluq6IM0W7Z0IIIUSDMqsFSHALsag4ju"
            + "/u7m5ubpCww3o+1btjFuFgMED9Fsy3lDY/cdVPCxJqa2UYsoc4AiviCSGEEB3KrDah+8srpeI4XiwW8/l8sVh4noeloCv2RcCp0+lcXFwMBoNOp6OUEr9T9X4sCi4PsHjYGtP"
            + "CZqjuUlRahBBCiFKKMqul6MVbWZYlSTKZTN69e5ckCYJbdSqrXNft9Xqj0cjzvCRJoJD0baIowozCOjEtTHLE4kKKSosQQgihzGo7ut+pUiqO48lkMp/PkySxbRvpvOojQG+N"
            + "RqPxeIygFPwg1NK5FE5adeYeYtEePaa1my9JCCGEtBPKrBNB11tZlkVRhPjWfD7vdDqIM23VPZ1Op9vt9no93/fTNM3zXF9Xp1ppIbcI51KJjVFpEUIIOWcos06NUj17lmWIb"
            + "0EkbU3/QSo5jjMcDkejkW3bURSFYRgEQZqmKIrfFCET51LTNGErr+65ChAhhBBySjDYcPro0gpBqevra8/zau6OgFYURfP5PIqibrc7HA5leuNqvAq18JPJBGtj4029mIwQQg"
            + "g5EyizzoVSHMtxnIuLC+QT1dKgqw7z+fz29jbLsk6ngyTjajJRV1pSWU+lRQgh5NygzDpHdI8Gy7Jc1x0MBr1er44MQgYwjuPpdJokCeY2DgYD3/f1yBZK6e/u7mazmXhJ4F9"
            + "CCCHkTKDMOl9Khli2bY/H416vV7OUStZejKLo7u4uSRLf93u9nu7glSTJdDqNoihJErxDpUVIq9Hj4nycCdkKZRZRaiW+hcmGnufVnCpYFEWSJLPZLAxDFNH3+33Et+ChGoYh"
            + "POsR5drnVyGE7JfSCA0PNWe6ELIWyizyHjJUxYKGo9Go3+9XzC5cJU3TMAzn83kcx3DkUkolSRIEQRiGVFqEnAybZi43IcpF2UcaAmUWWY8+YDVNE2bxmGC41YILrVuaprPZb"
            + "LFYiOdWmqaw40KNPBtBQtoOPPk2/e/BJr5sWhOsCYKPnDmUWWQLenzLdV3f9/v9fs1kIjaLogh5Q6nQApx7SMhpoIe1UDaAiS+rD/gOdY8M+SrC7ZRZ5OhQZpG6lPy3MDkRuU"
            + "VVw/Ada/ggvkU/LUJODL0dgE3xYDDAn2maYgUw2fiRD/5Wm2WBMoscHcoscm/0+Fav13Mcx/f9mg0fBqCYgQg/CLS2LNgi5DSQpsAwDH+J67oQW/P5PE1TWThV2Cq8ZD2JUvW"
            + "9aZpYecJxnCRJwjDUpRUHcuToUGaRB7I62wgG8VsXmZbtsyxLkiSOY32ky9EnISeAPhizbbvf70uxAQZUaZoGQRBFURRFJSVUagTWDuEQMPN933Ec13Wx7H0QBO/evZNgOQdv"
            + "pAlQZpGHs1p2ivFrv9/H+HLrOBK7ix9EEAR6C0vJRUh70avjDcPodDqj0ch1XdkADUie51EUxXEcBEEcx5ueetM0LcuybbvT6TiOY9u2ZVl6E1QUxWKxgIefviObEXJcKLPIo"
            + "9BbUtM0O51OlmVxHONPz/MGg4HjOHUOhQYXa1TDZwvvs5UkpL2gfBOthGma/X6/2+1aloVlvgCkEkrm0zTFpBkkFpEltG3bcRzP8xzHwb4YxZUGclEU3dzcSNMB2ICQ40KZRR"
            + "5LqRkdj8eu64ZhCKmU5zlGsYjq1zwmpimhkkMP+7PFJKSN6Ik/x3GwXITjOJvaBDQpEF5FUUBXqW2rr0ZRdHt7i2iWbMlGgxwXyiyyA/SiV8MwLi4uxEE+y7LpdIpsoG3bnud"
            + "1u91S4qDiyFjMZz6f53nO+i1CWo3k+GD6YNt2KY24ur28qFMjH8fxZDIJgkBPVrI8ixwXa/smhGwDgX2JacVxjBCXaZqmafq+3+12TdNM0xQF73EcI/i/9cgodO12uziI4zhp"
            + "mpY+jhDSFmRIhhVRdfviVYr3qT5ynudSdVA6yE7OnJCHQZlFdoNUUahlyk9kllLKMAzP83q9HgRTnufT6XQymWCSETbTB6Crx8fw13VdBMMQ0DKWsCUlpJlgxCXgTTzOiG17n"
            + "if2e48ELU+pBJ6NAzkuTBqSHSO6ChOtu93ualUWqt0Xi8VsNoMgcxyn3+9jMZ86nwJpFccxqmVl/Mp8IiHHRcZaqzORMaByXddxHMwWxPxBtTsxhGZhPp9HUVRKF3KNL3IUKL"
            + "PI7hGlJcVYm5IC4p6Fkvksy1zXhR9EnZQiwOyk+XyOpakxot3ZlyGE1GatZkJa0LZt13U7nQ6MGB4md+oHvfI8z7IM8xbFKUb/ULYS5DBQZpHdoxe6om2tUFpKq29dLBZBECR"
            + "JgkhYp9OpKI9dpSiKMAxxBGlD2ZgSsj/WeociagWvLMSuZNQksa5DniRamCzLYM0VRVHJKf7wp0TOB8osshd0pQWDeCyAuHVHzExEXArvwEK60+nUHMgimoWkJFbe4GwjQnbO"
            + "pscZzymUlud5qARACWapWVBaeaVaiiH9IGqz+qku5dyE+ETALwYRdP0j2FaQnUOZRfZFyeWh3+/XXPoQjvCISOliCyExWEXUH3pGUTSbzdI0ZXyLkB1Sf/1mndWCLfX+stNSc"
            + "gChhnB4yc60zqesLQ5TK7oNBVvILYZhuHXlH0LuC2UW2SN6Q2ya5nA4FD+tapIk0ZeY1RMNaH97vV6n05EWufpoyBfEcaznC9h6EnIA9Id07eho66ipTpUnwBqpYRimaQqNpT"
            + "dBEGqY5CinJMan+BQkFiXKxVou8ngos8h+KTVzo9GowvpZJ0kSjC9LUX39aJ1OR1reOvEtbJOm6XQ6lRU56ljyEEIOTCkWheBWr9fzfX9TA4L5NFiR+r4P9b1i5ChFYLtB6kD"
            + "fLLJfJBallktEY4nDtfF8HdlAl1mSRsR/xXG8WCwWi0UURWmaYn741mNCn/m+LwNZdc9GlhByAFbd+KIoSpIET+5qA4L0H9APor8uhcbVSgl8nUGgZgTGUAXZAmUW2Tt6W4l2"
            + "sBTMXwu2QU0GnN9Xj6m0OURYcDrLMkwX33pWpmk6jtPpdCC5UBWLD6XeIqQ56JJIKYUCACm90h92NAV4luUp3tR0qA2R7OKeqHrKjJwtlFnkQOhKC1UO+syjtUh1Bab/6IEx/"
            + "bB6o5kkibhCYHLT1spZfATyj5ZlyacwvkVIc5An3TTNPM/jOE6SBIbvUrAFr6w4jvXZgnyKyXGhzCKHo6S0kL+rDmtBYzmOgwYUR1jbbq7Gt+B6miTJ1mQiwAf5vi9+XVmWQe"
            + "qxpSakIZSaEYgqx3HwjGNOMd3eSXOgzCKHRl87Ns/z6hyftJWitHCEija0FNJP0zQIAizIs+mzSm/KzCbf923bRpNNsUVIQ9AHVIhhh2GolLIsC0YwpdWjCTkilFnkCOgmgUV"
            + "RrC56uHYXy7Jkl9Jq05vQy8JQvIVi+a1RNPlQ27Z93+92u9i+2GDGQwg5MMX702uQRpSaBIFPKzkuvP/IERClghfdbrdikrYO1i5E3RUa2XtZNuvSyjAM13V7vV61E48OFB7C"
            + "Y+IHQTcdQo5L9chnbZ07IQeD0SxyHFB0JXk927bruDwjiCXOgfedGKgHt5RSWZaFYYhZS6qGq7XuB9HtdhGQW3VBJIQcmE0z/lhbSY4Oo1nkaJScS/v9vud5dXaMoiiKojiOx"
            + "STwwSGlUnzLcZxut7t2vepNdfcIrUGuKQa3CDkqawc8fCrJEWE0ixyNUmlFlmViOVhtVKNv9sgGdDW+Bf9DY2VNNGMDtm3btg3zLaVNTmRFCCGHR59xjHf4JJLjwvuPHB8ZgC"
            + "KYVKc+HalGLFOI8qxdDVhL6zBifQ8419cEJ4Z/8Q4H04QcHhntsDyLHBFGs8jxKVacS7c6uaMcCpJot4YLenyrWK7vEUWROJ1uHRzDXx4r3ZqmibnlEuJic0/IwaDAIkeHMos"
            + "0BVE28mcdlwfdT2uH2QFJUKqlTVcURZhgiJV/Nq1XXToH+EG4rotcp5TMs+knhJBzgDKLNAgIGn15wa0xLaUUHEQRBttHHUYpvgV/+SiKVI2oGzBN03Vdz/M8z7NtW0Qhq0YI"
            + "IeS0ocwizUIvii+KYpPLQ6kyXZxL1TKfuPNwkcS3JBwVx3EQBNBbSqmahhSol3ddF6ZfyHsyvkUIIScJZRZpHKI50jRFRVSd7CFScnrOcU/CRddbaunpkCQJ/LfqiC21rN/yf"
            + "R/1/jI3inqLEEJOCeYsSOOAUpG6qE1GVqtgOR0YWeGdw0zxK6X/TNPsdDqdTqf+7kiVYi0gXWZxiiIhhLQaRrNI40C1uLxGZq2OzbqeKzxwTEh360F8C4snqto1WCjnx+I/KN"
            + "4quH4iIYS0HLbgpKHougr1THVWmEbJVJqmaZoiFHSUgFBJHhmG4Xlep9Opr5mQAIVck6/A2emEENIuGM0iDUU3Z7/X3EOp5e68St8AAArUSURBVJIKqqNIE71KTC0TmlKkv+l"
            + "bSKoUOI7juq5MpaS/PCGEtAu216TpSFjLdV3HcWrGtGC7kCQJ1AnScMellPf0PM/3fVGB0FW2bW8qREOVfRiGYRjK12HxFiGENBnKLNICdKVVIURKoKgcFfENUVpqGdwSpQj/"
            + "UmgswzAgvLYeJI7ju7s7zG2UNym5yMmg27LwxiathklD0g4gPrIsk9Wj6+wlq/Go/TiXPhhJaOZ5HsdxFEVYANG27TqWEJZl9Xq94XAofhB5nkOosXiLnACl0kbe2KS9NKjjI"
            + "aQCGd3iBYqW6iinNE0x6Q8tdQOryEuSsdfrjUaj+x4kyzLEt1gvT1rNpupDxrRIS2E0i7QJPY9QpyJeLWue9roazyMpNAzDSJJkOp1GUWTb9qag3eq3ME3T9/1er4dFHtVyDs"
            + "E+DPEJ2RMVMzwa+OQSUgfeuKRNSF4MzbHneY7j1NkRMS2xiW94pEfUlWVZjuN0u13P8+51BBhbzOdzTALAm4wHkFUsyxLLN30dhaOgjyvw2jRN+M+B5hRZElITyizSMqRXUEu"
            + "/dfgdGBpiaqrvGMcxNEeL6mpL5mGj0aimrNSJomixWMjai6ol350cDP2ZUu8vVHWwW6X0tMKZxXVd0zSjKEqSRP9f3sCkRVBmkfahZxZM0+z3+57nlXIK+p9pmkJgJUkC29IW"
            + "KS31/ve1LKvT6TiO84D4FoJbshgRAwOkBPRWdb15acGDx3/oamobxZe2bcO9JU3TIAj0e7Xh0WhCdCizSCvRx77dbrfX69X0QQDSZLdOZ+CLo4ezLGs4HNaJb+kXpyiKNE3n8"
            + "7nEt9oiN8nBKMW3wFpdJW8+TPqsVh/atu04jmVZ+pRbKC39RuVNS9oCS+BJK5GCcaVUlmWe59WxeJA0BLKKancj8oNRiiWEYRgEAeIBNU0u1DLZ2uv14AShlgEz1ssTIHMySr"
            + "NMsOyV7/sINemiX8/al2ooNw2BSt4lFZOIcXvrgyLeq6QtMJpF2g0aesuyBoOBbds198qybLFYwN6zCZW/j0G6MZSzdLvdTqdzryNkWZZlWRAE4nfKUAHRKSl4WATryj7LMkz"
            + "mhTgTfzt9LxFe+LNUDWYtqXiKsRCCLrZ4o5LmQ5lF2o2IDJQr1a9YyvN8NpulaXoCSgtIn4dgVU1D+RJBEGAxH4nzsScjOqXJgIgQl9xEpcBLhFe+ZG1u0XEcUVrVn446S1Fa"
            + "LNIizYcyi7QeUVq2bXueVz+WgxIlTBc/pXpwuSBYJNHzvDrLE+k9ZZZlcRynaQpv+mbaupJjUcovG4Yhi41WK3s42InqktULsJhBnVEBKgt1mxLFkQBpNpRZ5BSQ9t2yrHspr"
            + "TiOZRLTiSkJfTImOsKaEwVKRFGkz/Nil0aEkt6CYKrQTPo9iRcSRa5/Z0KiyYIHpzRAIicJS+DJ6SAlSnme16zTQpJCmunWVcTXQYIHqJcXA/2au8NCQleuXDyRCKU5GXmep2"
            + "mKCHFFgOox8VGpshfb0upCe0KOC2UWOR3QzuZ5jtKimkoLm0mQ5lQFhPRqWZYlSYJ1HpHoqbM74mGofkPqRy3nf53k5SL3RZ/8q5a3GZ7EnWsgyCzLskqxVZnkKFJshx9KyMO"
            + "g/CcnhfgrGobh+359z/QwDOHbiWDPaach9G4PU+h9339AnxSGYRiGIuCYTyRAn/0qrhDVswgf81lKm7eISY5wIS5tJv5e6v15joTsFcoscmroE+76/X79YTQc0jE1/eSVliDW"
            + "R6iqqelApoO4hdTKKOotskS3OYX2gvXovj9Xn+eoln4TFWlKbH9i1ZmkIVBmkdNEBtC9Xq/+XpjEJEPhs5ILurpyXdf3/Zo76nmixWKB/uw0PDLIgyklCnF3IUl9AJlVAR5q3"
            + "WZCvV9hprRY11m1AGRPUGaRk0Va9m63Wz+mlWWZvlTtubWzJbNT13Xr12+BxWJxtlePgNV1oG3btiwLd1dN8X2Aknb5CMl6o4R/7X1bOnOGvkhNKLPIKaMXhdTxjgJZloVhKF"
            + "GZM8kerqKnX+G/VSefCJGqT9482wt4huhBLGPpU4pleQ5WkK5HpErCSE8dSqJw9Qj6+3K0ksySvOQ+vwo5BSizyCkjc44sy3Icp35FfJqmUtx95i0p5hKKLRmu5Ka8D3xNS2u"
            + "tHOsCol8/85/vwOirHKplTPS+AdGHIYl+ufFEaemKqn4sjSXzZCdQZpETR4bXtm3Dq7rmjvBARySGXTVYG6uQ/9WDBGppYKaO2kXJzFPpLNlf7oPSjaGWZh+6vUL1lV/VYZiJ"
            + "cq/TwOqcm+46/RwonsjBoMwip4/0AaZpuq5bvwIXcw+PHpVpIHJJS0pLpyGaRs9VGVyocT/IRZYHTS2Vln57rKbz9JtkNeCEF47j1Ek4GkvbPDyznIdBGgJlFjkL9Mrue3kWw"
            + "NKay3q0GmO5Jsza2Bt/08dQmlGo3ldXuktCaTaf7K6WgasKPYRPQY1g9fngIHEc8/clDYEyi5wL+oC70+ncy09Lio3YareUkhpY26kzxPUASsHC0v9ujSSVDBTWRkBXQ2VYNn"
            + "HTMRHBkumuir8sOSqUWeSM0KfO1V9eWi1jWtIHsNVuKbrYkq66KArIaNmMv28dVuNYpf8tJQQf+XElPScxs016K8syLHrIZ5YcF8oscl7oI2PP8+rvWDLUYavdXnS1LUYD+RJ"
            + "9S/7K1ZTq8w5WBbVqLr92MmOaphJ75k9JjgWXjibnBVpnWfC4fpGWzJaqKPomraAkC4rlonvIRukz4+T1cU8Yp8dSbqH0GEo8crVKTF8SnteQHAX2FuRMEY/4+y5ni8X7OImp"
            + "7ZSSUBBY+gbimqYHTvZUmSfzMyo24P1Wgf5r6kFKpVQcx/JfDZn9Ss4KyixyvkhDDNfNmnvBmwevmYloNaVYJszK124p+UT9F3/wry+xtHuFqXizVbN2ziMGUVRa5IhQZpFzR"
            + "+o8ENLYmkaE/yFdeU4G/RevFtyitPRUVFG5ToBu2lmhqErBmB1KuvOk9BTr2UPFi0kOC2UWIUppy/KI5NKL5Uv2P7IX2+uTQQ9t1inGEnUlN4yeW9xqAaVvrFYiMfrSe4rmT4"
            + "9g7aiJ15McEsosQtagh7hK/1Xt8UPaS2kGYp1dqj0+9FDW6p8VByxZIfA2ezz65ETAMRI5DJRZhBDy/+hKS1XqobWUYlQ10WfM6YbppTAqeTyrUcMjngw5EyizCCFEqZXEsao"
            + "Ma+1D+pREFXNb+wNVAaytJAeAMosQcl7o0SMUvOsJ4lK/Wz+B+EhWa7QZx9o3dNIiB4AyixBy4mwtjSotd/iYT3kwMrtQ1B5TWoScAPczZiSEkGZSsk5Qmtt7abPVWX47OYHH"
            + "x0X0sndqLEJOA8osQkgr0a0QShJH1yurKb9Vt/e9niEh5JxhK0AIaTG6lFlbRLXJmGPTQY6FHr5adWgjhLSU4zcuhBCyD0qqqwla6l7QzYGQE6Bl7Q4hhOyWOss2H/B01kC9R"
            + "Uh7ocwihJAyJWl1dKUlUG8R0i6a0nYQQkgrKDmJHwvqLUJawfEbC0IIITr1ZRyVFiGEEEIIIYQQQgghhBBCCCGEEEIIIYQQQgghhBBCCCGEEEIIIYQQQgghhBBCCCGEEEIIIY"
            + "QQQgghhBBCCCGEEEIIIYQQQgghhBBCCCGEEEIIIYQQQgghhBBCCCGEEEIIIYQQQgghhBBCCCGN4v8A9jXyL0w87WEAAAAASUVORK5CYII=\"/>\n" + 
            "<image id=\"image13\" width=\"503\" height=\"365\" xlink:href=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAfcAAAFtCAAAAAA/43zeAAAAA"
            + "mJLR0QA/4ePzL8AACAASURBVHic7Z0/dNvItYfv+u07QRd2y1e8E2wVbhWmWrpaqFq5ilxFLnJMVStXkavIlelK3kp+lXYrKpW8lZxKTkW6kraSUkmpiFRSKimVlPPeOfMKzI"
            + "ADzB1gAMxgAHC+wqLBAXCJi/n/mzsADkcH2fVsW9BIHtk2wDTf7tq2wGGD2f3QtgkOCyzIuSvpV5AFIXu2bXDUDyGEjGwb4agdQgi5dCX9quETQlxJv3pEfncl/aoRRH53JX2"
            + "Kzo/bRAxe27bAUStjQlxJj7Ai+R1g6kp6nq773WcfBq5Nz9N1vy/ZCWxb0CS67vcvlh9dSc/Rdb9zvvb3FdIHO+7t6AInhCPITtvbPLq9d+3+TjDj/b7IyMuDnRkhhIxrs8xh"
            + "kkve7+RAkirYp+lUqgJHC1gk/I6V9L3No1v29ax2+xxmuE/6PV3S09KdfduzY6RDOyTFlPtufT9ZC9wPrJnp0Es/7XeyHn3RGy9Ld8aGXVsd+vAFv1/3AAY7p8JxJ87oEiPRv"
            + "cf7C8TphBzbttWhjwB1McblarXpOj5Oq+zMu6d3Ju1oHM7vEc+uTJrRPJzfAQDg1UejZjQP53cAgA9vzZrhqJf1mVKbzq2g6xD98dF9vssJIeTWt22rQxPDXWxkBuc+sG2tQw"
            + "sbBwtlpxNCdmzb66hOf3ysWLozpvkXdTSb4e55MZ8TQk5dm67dFCzdKdd923Y7ytPfLlq6U5yMsr0MJ8VLd8a2beMd5QgOrks7fZVllP9h24CKhP/5uV+6ZXb2h//TaYujXja"
            + "mgmRKDdeoaznepuqYbJIj24Y7qlLO9Zu2zXZUpzc+Lup3V9J3g8KudyV9V+htz4o43pX03aFfwPWupO8U/R3VQTynm+8YvqLrx7YNdehGSXRzu4olfbf1tEoe7TnlRddQnLMZ"
            + "m7ZjtdZg2UfN7cZL+v4qLbUd2a82PUW/kxOzdkxXJX6Kt3tyTwg5tbx5m7j63UpJH5Bzk5dvDoM4fsjlulU7RAef43O2Jkt6b0EW5q7eIDb4iTGbwUPE1e+LvmTizmBJv0cIM"
            + "Xf15tBPPFabi5DW096lMkrM9WNTRgzvCSGr0KBPzYid2rNknHbuchJGmLgzVtKfE0KIb+jiDUIoXO3t07mdsiQpo+yNE8FrDZX0O4SQ3Mi4XWAn7Xd7688mSUNEzyYm7sYmTP"
            + "CjCmUFQqQdpP1ub8JrL2EHHo2yv80G8Y2U9CcG36lmcUJS2OvETHkz5NEo2cSdgZJ+k958ov/STWNGUtxbM+WINyOzqPV3Lk3kyj6bIFiBBRqC3+11XnlTJnmJB5NL7SV9XOC"
            + "swJSfKGy0NljLTb8rNTIGgd77L7s2KzBAPxX87tsyZRGbYGWfUI+7v4XbZ6Jfd/Gg/YqliZ1998SGVa/9+OMKjNdNhPwe2DLFrgVDfizYhgFZdFlnFWf3l3Mbt0/sV2dfjJDk"
            + "c+1XvBGO2Crk2LM+fKf/2t7+P+dnmZXHTmJ8uic+lo4hTIZYG6yi0+9mIhf5t4ScH2xLJx/85IxfYMKGCujP72I8b1v9uCi/3zw10qYL10694RDg4ezT2QWSmQ+Sv7pp5bx+R"
            + "LHDxJIl64SYjFy0vszRi6Od1G02U8+g+4F0muP3seFKJuXb2f6mz77qpxXcE3NmNARRzGhrkHKbGB4YF6acyfXxJPAAGbyS7VTZHZrj94nx8dE94bcSQsj5gTiG0f119qJo3d"
            + "YE/J75DSCFfC2jaQP0dYzTWuu/PzwxvVnQlur+JE1rz+vvx8Fd2s96+3G9DR8ADsP8lF4NmwU9nan1F5rmdwMs0mWcTsGNP6W9p+MgN21+Cg30FOMrrHd+asak34dc/6ghkWl"
            + "84fdKuJzKR/e6wCz9g/UJrYb8Iqd7q2uwlgyKRMuc7W10tcgX/K5tErKffMJN2bd7VDBg4uJoN7BtswGOhB+qq2pLv1ETTdetynqJSJmn3OheN5gKv9HXc2FhNKQxy0zFOUgl"
            + "rk+i0b1uICyc0OR3pDhtzG4Ru+UcTwgh5wfjbjT3DAmtOJWi3gtrAR+xVeRyXLu9BsZtDHHg27Ygi1f9cdlT3/8412eHRcTKTkdPG61CAw0X1oQnrA9TYrHbmW6dEaGVj3WSb"
            + "5vUKPJK7G50ZG0IwsT4vHCkunu8I6wveNggrT48rJ0WG08IfzzslNjShOBGbCsSQu4bVkSiRso46dyS+JF+v4uvEiGENCwaoK8+enO959u2Vj/6BTc9NN6olTVvGcxUvX7SkB"
            + "klzej3O7p3SFNG5xmKY3adzOoAYEBolQ5PFNEwZbKgoEU5Hdu20yDCr60mLhug9WbTtokQZ6MEbg8aVkRpRuhqVwph511iz/C6YfIVIUSiwGLcsPaIdhbCT65ytX30KQaabNU"
            + "ENnmQovtKaiGDVvH7BvoQJ7ps1QT+ciZompJaPzPhN5e/Ft5cMrPEtTzDfLc3L9SJdnT6XbwWsRvrGENpaP7atpXG0Si0wvUMY43G6kBNdWHbSuNMhZ/sl7wSrlhsWjA4vKMp"
            + "0KjZBBPxbbRNyHlT7MTwRbmrGeMAs7I5y8VQTPj9X8KRkq86Os7x8KxJs68AMA6EQzdv/0uMstGo/G4CXQK7dMyIiF29xlamLwxTRfMuGZsedBRNQitUYtO8bnCqFbucd0m39"
            + "ho2oaAfTUIrdHPXxm3bnRygTUyxpjS2E1sm1oU4Vl1mywlcvdI0lYrHjSsJ8y7ThOmdD3WiRWiFS2wa9+yWA7Sn4rxLUmPbtBlE7egQWvUWmNubJrGJf6pkitXj66rGtUx0o0"
            + "Nw0wqJDRugPZdOsfa4OarGrOYzhQa/46IleztTSdglhNxPs1bpcVERbmszyxaCw4ru2NMOiQ0M7snlds4wHBeqoR6jLCJ4rGDVhs9vNa4LByeZWZ2ynGPwjRtkmapCq1ZIbAA"
            + "8tRH3uFvbjeXOGSzSLivWpMG1ag1bJVEA1lgJbBtimmpCq3ZIbIpAh6DGtu0wzUzwWrWzCSH3viFbayGquJrUHWne/jI7AXb0RVivFXp5+R6gYRPwJqgitBq2QmJTFO+kAz8i"
            + "l6ngN1/1VHyVhPG40sbpnTdr/MFIOV9BaCWR2JiOK22cu7WwUYIbI34vL7TaGGNHvz+rYExDuFu7aZLfjSDqigO1E1sisSnHsPMK+tKCmxnmdu27c9sisG0AR6P6cZMAO7rVl"
            + "eg/c9sGmKak0ErUaxCyAr0fKxjJ7+XWDPTQlcJXTVsl0Q1q6sf9QuEsNBBp41ZJOOSUEty0RGLjkFPG77jEpqhQx2GV4v7z2rFKwpGJ4MDcoZeWSGwcmQjKifOcE/BVEkY39X"
            + "XoZ5H2YI7gpnsSm9WkqNAKjdnfuFUSjjxmghMzk4v7qBOyAnK07lHM73gXrvtx/rrHVPBiRoesqxKbRlPXfFxGE20frcfbL7FpNGb8XkRotYHG/3jTAYnN6lEgspEYFoiQzkh"
            + "smouZ/F5gDg3dKOpuS5spDhQzfhcVMr4kJS6xafcqiTZgWWc1+hN29PB93XasHHXld7xX1jvC2ntOYmOeuup33O/7PnbylpPYGKeufhwqtBqPsaOvXBeuragJbvANFl0Xrg4s"
            + "9uM8tHK/eabZlnYwqnlY2mI/7jUaFKgzqySKsT62bYEWFEpvJ7HhOc1TJLWDfKEVLrE5X1GJTY8QhZBozWeRdqgguGlHINK62OzIejAh8GB6DTC+1+/Yhq1NYErIfRcEBzPBp"
            + "cnvWxKItDYWpBtLg3L87iQ2SYaE1LyhpKF5mVA4khBa7aH1+NbKSmwCAIBBYNcIHUyFzOxz37Zjr98aiYrHDihJ9wSvckF5Jask7FlrGy9q7dzXuBrQUDn/b+EIV3cfYb/vbj"
            + "XHZwEAIIhGLbwad5gz5PesAfrdADv6MjRiSCv4lv79zqoVOsiIaNWOvX5rJe7dBLXdsnadFT4L17i9fuvEj3s39WX42vtxLoqNQBB/2qitZVdbfqe5HJfYrPYqid/Fn+ps2Rl"
            + "hKFTg0TYhnQ5EWhbumdQ6ZmcAidDKRbFBSAgRgppuWm8/bhedZH6xmhIbRsD/54+WjNAFWpY7iQ1Gogysc8zOBJjfe+j4bOP2+q2ZXvJx7NZz188NXVcIst8DmGLv8sPTVe7C"
            + "AcB68r/fvU0n8EYAAcDX3sNPh9ruasrvgjN7MN7AEr68MmRBW/g2+V9//SMA+D70B/DLIXjLFtHd03mthpVCFFo5iQ3OIvVEFrP0kYhz37alKsxEu7Efc72yEhsAAPCCAA/HL"
            + "IIOb5entnIe0F1xVzCKzaAPvg9fDKBXZJ/gl+/0WmGsXaeU6s3c0O0bRn/gjQC+ARiVy7WtqNoBABNaIaxOINJtdHxalcv2LCsQhVYi7d7rtxi9g/JuP25RI0iMaCUytm1krY"
            + "zQuQkFJrYtLwIecjbByklsxuh4ZQ636KhHY8nvnqzgKokevrtC5lMq0uhvALl+v+/E+s+iDAsW9rO2ZQ586o1jYttCS2wWKezbN1WZ5/fVldj0VLo6hBBC7luouhpk/6TO7PV"
            + "bhsFMye1tq9oBABNaJWhXI1U7GwqF/Wkrs0Y/8zcd2DbPNt4eOjvJP6KWjmVm/aZVl9gAAAzQvZRiWrtRZsZvWtkoNkk2FlnPqG0dOEbGj+pCQA8d4Krylj8kud+dxIaRNUnX"
            + "1nh2qLyGkJVfJcEhLiriMTueaWx9nFRI82y1V0lwZHdmW7qCQtZc3bNtWHOYZeb3lq6gmOK/ZnUkNrn0crrwRlt2Ncc9cHtJLAlysoDRIAjG/P7f6NEXq75KguPbnO9bGc9u+"
            + "L9Y0dWBCG36WGQX8618Wi4QaS7pmavj/m6qwm9hy26KvsArKbGRkYrIPQGA/lHyWOvG7Fwg0nwSAfiZxCJIlJNti3qCR7ERth5YaTz+GXESi13+eGDPvjLM0OyueYVXy+GFaA"
            + "n1JF/Yt6tlJ1kz0T6RoEkm8gcTxHMbrWrZjXC3r946iUyWvt0Wv9xhhX2LWna9hcTv7Sq0DBNHtblGOzn9afRti9pEU4nbV1g6jcDWlUjVk6OoQFiXfN045AtlnN85aO6YZoz"
            + "R79yS9ohU8L1+CSHt640a5Vqh+u4dtKZllyUZa1FdZZwhIYTcBnnJRqd1xbOrSNYaoFvbxjWIXaIYoGq7FTq77FVxtq1rEDNCjtUUKL0WzGXhG0U5vwt4952arED3+l3i27av"
            + "MWxYj2KhM47Zju0f0xr6jzukO8IDkXK46ffmoE9f5+U2VJyUtjno8/t+7mpH5/fmoM3vG8i0Uop2DD2tBrr83nezrK1Cl9+PFIYXfE33clRHk98ngZ7rOGpCj99Hf1JJ9YWWe"
            + "zl0oMXv+F6/YjId93JoQYvf0b1+/y0ccX7vFrjERoxI7QQ3nUK2168wbOv83iWke/0u0sec4KY5VK/f8b1+t1wUm24j3+tXKAec0Ko7ZOz1OxMO2zbWEVO1nM/Y61eMZOY6cl"
            + "1hW8zrhJBoam4qHPftGuvQReZev87vDaZSOY9LbG62or//EL5xE/CNoZLf91CJjXyvX1e/N4Yqft9Al3fFe/2KPfgWLAFYFSr4vY/uFnL2ln0SI1M6vzeGCn4/wqrru2exu11"
            + "E0gZT3u+7AXb0ZRh/FMt5v/TNHJop7ffRa+zo4WFpSxxtQBKIlG+xi1vIrfz2Yc2hbH4/wLpwD8+y63TXj2s7m+j4bDI2Q0/43kW0ajkyiU2S3ASOliGT2CRxQquOgQciFVa/"
            + "L9IpnNCq1eASG7Gx7vzeKfBApMhev7N0mnsL1jp0gUaxwfb6FfzuhFYtRrJKAkkpviCuA99aMiU2SaZCMr9mYx0yio7X4Usgb15gad2EXHMp6ve9IXYU3+v3n8IRv+DdHKYo6"
            + "Pd1VGLzdq7BEkdzwQORyvb6FUXWLrBhUyiW31GJjXQWzgmtmkshv+8E2NEXoSS5WzDTDYZoF04ewEwcz53UZ6wjkwL5He/ChS+12eKojwJ+l0hspKskIBSOuIhWTUHd7xtj7O"
            + "j3Z0Xu5ur31qEmsUngqQ3nOprMDHP7bfZKx2KviaOBKEpskjihVdvB9/rNi0G9SJ/gBDdNQW1/mR6qgL5CZ+EcBhjSkc4wrPW2R0hmJ/fo1ByP0CZwQquSsCc50XVBpX7ceBM"
            + "7+uqi8N3s9uNy39MVQsXvA3Rd28d3uSc2aYA+OCXnZLHrRhCUkQYizWUqnOUbt1YC27j2sp1TgjbK+T00ECkusUnSHKFVwJbuDfat2tEY8tvzASqxeTdXuDgitApzb8fW1b+Q"
            + "bKnYo4N+f32Lfw/bv4/+/vh+eeyP8afxS/mEgmNJMYlNEnGwJ8g9J141L9uXbCNvLOCEJljnji2KmNBA6i/n8UCkW0pFeCgcyW8UhCw7/kaS4Bv61/clCViznZ8x4mp117IDy"
            + "Pf7zjp2VFYG56Lw0Jm7ZL2uQPiQpE/frSu+PD9DP64wOX4f7GFH3x+qXbxUP+5n+lfi9158/Bs8AWuFJkYXfoo/zV31DpDndzwQaag6Pis+YoXOH/OXhzs+QD4lYKf9zB+M4y"
            + "3dPMu3YBXI9ju+12+GxCZJqX7cnH3A9xle5nJJBc/aBcnyfCtqxr//rdsHI58NgjFRPl+MaKWyiyxre+M97fPlxcaZpwsl1SgIWtum096ez6JfXGKTpJzfj7Ju1Mu7GEtwrm5"
            + "mC6i1H4fu9Xu3pX7xcpGJMxt2geRzDGvWuWZ7Jhl+x/f6la6SQCi3YIY17Ho+8iXfiEcrePa2/E3hTiuM3O/4Xr+H77GjMoQGoEoFG+dULMMHAADwkf9PErxZ50gh9XsPXSVR"
            + "UGIj+F1lx4kHluG/Fr/zo3fhTVSSfIucPUxdxIEi9fu+jxxUHJ+tCMuqSEcuAACAu7NovBAZS+zRkzrmdu2DTTK/j8fY0VcFS88wfUBphxnWsEPmf6Pq/YK+Gj2xJsAG5ztAX"
            + "X73UYnNPF9ik4NSB5rl1b74lgQAAHDGWm2BkMA169TA598lUWwKj3EiHTmFN/figd59+DH1DW3B/8yy8zfCi1hjs27YH8Gv+wBX/7z5GBY7dTAYfgNw9vcP+NPw/dEvRwCfYD"
            + "6nR7SPMuJ+f61pr1+kI6dSYp0F0V/B7/T4HK6iVyMQTqX5/S49YTh+Hv0V1BpoKAee79NGAAD01r9dZycGAHDxlx/kD4feO95wabQX0BOnvxXbIcPvNvrswq9vPvz5DADg3zl"
            + "G6kG+129BDoSL+Cqn7dPEwnK6KSEkUlzQ4dp0Bc+W5AljfUwCIozxLUgOY9G+jRMx2a1csjlJXMjnwvrdC+cMhEsfDzjzJ7J7FAWr3/voKomLV8WvXjKi1Sf6V2jQBwAQleFz"
            + "/sASdobJYn58eYx1JPbO/ezzoq/XL7nFZVfpAnFyKVx643LfQxQsFcH8flAoio0BWOE3SA3vxdV73G5Lz8Gz+ulnMMX29RSfKITBzM8885cAAOuJme15MoV3hG7as3MquWMFE"
            + "L9vo6sdS0lsQuGIUkcuZHVlqhgPoj9zkOZ31qwz1X0PztFMEeFnO74HAOOTRMn+KZngBF2fAjA8/Z2aeeqIfseVxh8O9dxPbSZUorWKcvfDBcSvRroHT/P7TVjOOoSQH5jem2"
            + "WuufEzm0B9gCDVvEi8nv5pIDu1pz0AnOB3icSmwCwcR9kFM6z3ndJWBgDAXooz/lAMLRA1Vu+J6u0v3Ofw3dba488+e/yEa8lvZPmnB17s9qv3P8wvkqscvRlXmodvn6x99nj"
            + "txYfaKtf9dHuSEFJafFw2otU6TZ2cRB/wl9iJ/pNsuA+ld5G257MNICl9IdMGLHb5Knc3XuiPirsn7Dvavbnejt7+ZGHF9X34jL+5KP74SlBVYpNEXDavdqVYXZEoHqirN/hL"
            + "J3tCLEa62N4u5vd40UB6N4X+PSGEnKfr4WGsUMEyPL33Lc0F++hkNJdFkttyebuc/GWiZH5xJKskyl6unOAGANiuhIkcQTu+0WPzaB4L+BSssBKfbDG/s2wtLvXeJeQa8W1cP"
            + "mARfNi9rwkh5BZvIXgLdoV74aX1lwsUJ0rmF2eGuf3WL3u50n6f0uSJRTPRe8/K/hnyJOgDQkrbQn6Pd8ebCF95iz20icIe3D3S3OeXDV1LGoZxKS+6HcDblxukhV3M7RL9oh"
            + "LCtU7UzqNFemKzIlp5szYzfZqJCp6WiMi4UxG/x7pCbDWYpGEa149IV4z3e4Cfvswg+PIw9lpNFMxXItGer2GvX0VFK9aRC6I/rM87j/6MuCuycZ6KozZTehlUbSBpYH9kjXr"
            + "Z8q6IeIw+Rfy2zH/IMU4TvN8lgUirRLEpJbQCgAv6eHm/07G5Of3vWZTE46aQWOpqozZjVtK+KTBU9XBIP2QOrV3J1vA+Z5cp118uDu/3Ax9JUG18tpTQCgAe6CPnF80EAABw"
            + "wS75IPbgmTCrkt9j6cFc5iSUv9O/6Ewm45XkWQ7Y23IYFrlnBTi/4xKbN5XGQEq/M3P6d5l9aEineXyAFvjcED3TWFVSp0xpkVQw64X0b9aLffFB8kVczP9Y6J4VWPodH2Qs9"
            + "tILCJPSvuKJbMRuqa0Mkl9gFTzNa5Wyexxj/2VY6DwVbcIb2ResmL+oTRYY+91DV0lYW0YoNuxS1TtSwfv0F1TRWMULgD8WbGDFfvelSaTZfcjO+UmSQD+x3mYXrZheVBT4lB"
            + "NaAcDVXeTEpd8DAEgE7nugspxgTg+wtHMoDyvliywLgsAbfaEwVSrNzH5uCu0wvwdoF+6d7A1VpaTQCgAugig5i4gjVO8An6IkcQVP64SHAs3wNBP29r9Ue+H7o69HQ6ygLET"
            + "cJqhPBUz9LglEWkJik6R8BLtYYxdGf+l/+RnrefSujjz6dtEsJ4hY1BmyNUIqkR36G9+M/NK34vkV/XtVqUFaCOp3PIrN08qzgP8SjvQVc2O8OpIWOTQzz7kkZ5G40hvSbDIQ"
            + "kxTDY6X8Te7mKYP15/rCX7KHX+Pa/KhdN0bnjV9WKDArw4o8Nv4VFcCJGWvWg6dls0f9Xr5Z95q5Mq9ZM7683NcY9dSnf+v2Ox6I9IOGIcNQOKI4cBNLZqgz6eLYeSJNsgdfe"
            + "bRuxCZADzObNd72QpTYzQutF03h0781xnn8HKSrJMwMGSqHnLjwAQBgEDUEg+hgUpDGKvjof0w6X9bvsRomu5QPUor7cP63i6sbbuylOOyC/yh/iaJ8DgDbaJGlHMUmi3KRDw"
            + "AA4Gda9wzOANDqPa7g+1Gb/9fRwdLZPQ7ms5X1yyd8xyf88OlMY+H8C32XyuMRgIcudJfNHBWjwlahrIIfcf+mwu4nh+jp21vW7wGbAf0BWyBD8Y6Xbg/ffPXlyw863M6uoVo"
            + "HauBzgHXsdmfVxmcZFWos5r8oG0dOnafS0B78b7gkZSdhe6yUz9wI8SBuAIdvDsvdCIE9pRrDLj0C+A6zRNMqCTE3/ApJhcLq6SFAPGrzKZVmHv0JAAD6tCQpOfYRr/fPWuK/"
            + "M2af3nx5WO4+GBby+yP8LSsSxcYUvN8lMy50iH7owbJZF5a6WbwrYtZe9kM2eH+3Nil1FwmsPVGv3xG0SWxC4Yj6b6P9cG8ArFknNNX5Hnwl6Xyf9WSvpHNmAPCcTdE+mZe6i"
            + "wyW3wf1FfRofje516/6T5vTvwOQVe9xwR9A3JwvV73v09cxM5CLN6YfXmgeR2eajWyxjlYeAfw1fSxrr9+ilBVawXKcfRDLbtLVe/wmfA3VJt/jjZMyt7vdpC2ImypjNBjxTT"
            + "WOAebwCOAwfazYXr/ZlBVawTIk1dexT+dCGlrBVyzn+0xycpHZi6ElCvyP7nE1piaE32u+sJxHAOE8eWg+0Xj9Ko9oOd0S5QNkJI5W8L0hqxtDsQeRTyygze7F+PRvWOIWmcQ"
            + "zx2iX2giPAGAr5I+EWiU2pYVWEFfVA4+OwM+RNLToH8XaOvXLx2wrCmh9+lf/9ElcSFUY7C3GIwAI17jS+OJxjbNC2cThD0BWvXMVPPV7iWZdLCzMG6timbGyzkLgz+zDnyTN"
            + "H+23fAQAEK7FQ5PzNb1uD4Uj6kUZkyEM+z4A4PmdVvABm68tUb3HAtq8gi6kf5E4mgCT4jdechaHbkNlTzDV3uCL+u8XT746nF9B+ObLNX1NeQkF+qjUib+JmnXoRBut4P3yc"
            + "Sp3A/ohV0DLcgTihN4x7jBV4gy/g3l4Oq50cQw2bnO1tfbVZ19OQt3XLy+0gmUFL+u9Ayx78JHfiyuVBsxh+SuUmN9HQpE1OK0YkOJ9PER/7Ke/8wy4XW1f4AogQiv1k2nmDa"
            + "IZBKx6j9+Gg+h1KlzMx9Kqu/zmLDNAECPunlcdcLn5nn3yZ6lrjc7HFS+OYdrvlbq68+iP1+f/l4JW8PRtKqyxiuXjCgLaj+zHxLuOAgDA6BRfGl2It3FXwj/nl8T29wwEswK"
            + "VfUKrIT5NX/3ku5BLLIkszlT0EUXzeyyghefPZWninXAfPrLSfO93r+bRJ2/z98iC9eI8vIgXdHsHf/zz+xAAANafm+rYmfZ7NajWCgDkLqVz8PSEYtfnFGaBPFH86c06+zya"
            + "XYR/A/hiEIvnr36q1rKbv9uJPw/29uDsAThh/g/rfqWr1846SbOTf1LMDnfeRJKGj50k30wIj3sgbmCL4C/T70kTTXsDMXnmvQWmchO22aa3sodQmPrr9yJDEHz2nUvSnHG3K"
            + "FrMK4tAKG8kEqy7ra2S8/4cW4eSL+7WftA+Rmja72K/qoh4kPPjg8yn/Bemo84/PEOteP/VIcBDZddsvUVbwR++muuX2tbv9yJTD1xb7kzaM+D6d8bXl909Fsdyzx5HLb/qWf"
            + "LVb8XyJHzy9Ab0zwWZ9ntFlo7Ee+8AfAVQx2ZCrx4nFlU8HD55TI0Mq1/86snT5JKN+dZXH3VdvF6ENopiRKsGM9g9uSeEkNvZ8Vi/Mqo3Po7CsV3O9nztV68Nwe8F9hltMv3"
            + "MQDaVGfhGL28eIQBmtzZubSvG63ehOaZ/9tpRnPr9UWnPfgAAAJJJREFU7mgCxv1eRWjlMEbD+3EOQxj3eygcqXE1kEOGhfxe46pPhwzjfq8vRJOjAMb9Lm4d6N6EBmDc78JU"
            + "yY3z+yrg3afG60pvVuPQiPlxm3SGry/2rsMmo2SGP3fN+RVhJ+H3wLY5jro4Wnr9tuK6Ekeb2GCTsae+bVMcEZ/Vcpfe+NeDwdWn+byWuzkcDpz/B7EB+/l9SYv7AAAAAElFT"
            + "kSuQmCC\"/>\n" + 
            "<mask id=\"mask1\">\n" + 
            "<use xlink:href=\"#image13\" transform=\"matrix(1,0,0,1,-0.000000000000000336,-0.000000000000000413)\"/>\n" + 
            "</mask>\n" + 
            "<image id=\"image12\" width=\"503\" height=\"365\" xlink:href=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAfcAAAFtCAIAAACV6rRVAAAAB"
            + "mJLR0QA/wD/AP+gvaeTAAAgAElEQVR4nO3df2wb9f0/8PvQo9warz3BrTshCyzqrZaIwFsjYdEMB3lS0NzJlYJUNjZRCWnrpGnwx/7oJm3tqETpNtQVwTp+iXVd15rGa1xqwA"
            + "N3yRqEK1zFGa7qChc5k2EuNd0VDDVwlO8f16+bH/7xOufOft/d8/FXl7xjv9GSZy6ve937xXEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAxPO8JEm93gWAJV3V6w0"
            + "AtOdyuSKRCM/zvd4IgPUg5cECeJ53u91r167t9UYArAcpDxbgcrk4jhscHJRludd7AbAYpDxYgFar4XkedRsAvZDyYAHatTzHcbIso24DoAtSHixg9vU76jYAuiDlwQL6+vrq"
            + "/0bdBkAXpDxYwLxMR90GgA4pDxZQr8vXoW4DQISUBwtYWJ9B3QaACCkPFrDwWp7jOFmWg8Fg9zcDYC1IebCAZtfsg4ODbre7y5sBsBakPFhAw2t5jeF1G5/PFwqFUAsC28C3M"
            + "lhAi8yVJCkYDKZSqcW8vsvl8vl8q1at8nq9PM/v2bNHVdXFvCAAO5DyYAEtruU5jhscHDx9+nSpVNL7srIsr1692ufzzW7XSSaTxWKxg00CsAkpD6yjFE8ikciTTz5JuQDned"
            + "7r9a5atcrn8y385ZHL5dLpdIcbBWASUh5Y1/pCXtO2bjOvJtNwTalUisfjnW8UgElIeWAd8UZow7pNw5pMQ9VqNRaLoRwP9oOUB9ZRruU1Wt2G47gWNZmGVFWNRqOKonS+SwB"
            + "WIeWBdfSmRkmS7r//fkmS9PZBJpPJDm7eAlgCUh5YR7+W5ziug8NtMplMJpPR+1UAVoGnooB1pj6gVCwWk8mkea8P0HNIeWDd7MPljaUoCu64gu0h5YF1giCY8bLaHddqtWrG"
            + "iwOwA3V5YJH27NLq1au9Xq+uujxdPB4vl8tmvDIAU5DywBBRFOtNkKa+0eTkZC6XM/UtABiBlIfek2X55ptv9nq93Rn/VCgUFnm6GYCFIOWhN7pQk2lIu+PatbcD6DmkPHRV1"
            + "2oyDdVqtX379tVqte6/NUCvIOWhG7pck2kmHo9XKpUebgCg+5DyYJZe1WSaSaVS+Xy+17sA6Db0y4NZeJ4XBEEURRYivlwuT05O9noXAD2wpNcbANtSVbVcLk9PT584ceJ///"
            + "vf0qVLRVHs1WZcLtfMzAxOnQQH+r9ebwAcRBvlceutt7rd7u6/u6IoTzzxBM4zAKfBtTx0z6effvruu+9OTU1NT09fuHDB5XJ1s5gjCMKyZcveeuutrr0jAAuQ8tADtVqtVCq"
            + "dOHFCi3tRFJctW9aF973++utRtwGnQcpDL2lx/8Ybb5w8efLjjz92uVxmx73H45menkbdBpwDKQ9M+Pjjj2dmZupxL4qiSUdRCoLgcrlOnz5txosDMAgpD2zR4v748eOnT59W"
            + "VdXlchke97Isv/POO+fPnzf2ZQHYhJQHRlWr1TNnzgiC4PF4DH/xm266aWpqCnUbcAI8FQVMM6nF3uVyDQ8Pm/HKAKxByoND+f1+r9fb610AmA4pD0wz9XHZSCRi0j1eAHYg5"
            + "cEmarWa3jo76jbgBLj7CkwbGhqiXG6rqrp3795kMnnu3LlLly5de+21V11FuoJBvw3YHlIemBYIBCgp/9JLL+Xz+UuXLr333nunTp16/fXX6XGPfhuwN6Q8MO2uu+5quyadTh"
            + "87dmz2R+px/8Ybb7z//vscx0mS1OzLtcMyT506tfjdAjAIKQ9MGxoaar2gWCzG4/FLly41/Kx2+vHJkyePHz/eIu5Xrlz5/vvvv/fee4veLwBzkPJXyLIsSVK1Wm0WGdBloig"
            + "GAoEWCxRFIc5xnRf32niT2QtuuOGGN99889NPP13spgEYg4mA3Fe/+tVt27bdfvvtN954Y6VSURRlfHz8ySefxPQ4xqmqGo1Gq9Wqrq+q1WrZbDabzWqH3d98883as7Vav00s"
            + "FjNlrwC94/Rr+VAo9Morr9xxxx0rV67U6rOyLAcCgcHBwQsXLuRyuV5v0NFkWfb7/c0+e+jQobfffrvjF9cOu6+Pslq2bJnX60XdBuzH0Skvy/KJEyeWL1/e8FNut/vkyZOlU"
            + "qn7GwONKIrNUn5ycvL48eOGvMvs2SbLly8/f/48SnZgJ45+Kupvf/vb1Vdf3eyzgUBg06ZNPI+iVs80e/C1UCikUinD305RlFwuh5ZKsBnnprwsy3feeWfrNb2aUAotKIqC6n"
            + "mXuVwuWZZ7vQvokHNTvkXBt87j8Zhx7C0QrVixYt5HarUasakGDDQ8PIyUty7npjwlvkVRXBg00EPxeLxSqfR6F87i9Xr7+/u7OYcdjOXclCdeD65cudLsnUAz8+ryqVQK7a1"
            + "dxvN8JBLhOK6vr6/Xe4EOOTfliRp24ED35fP5ycnJXu/CcYaHh7WreBzRbF3OTflisUhZtmzZMpM3Ak3Vr+UrlQruuHaf2+0eGBjQ/m3qQf9gKuemPNF1113X6y04nXbHFQ2O"
            + "XcbzfDgcrv9PXMtbl3NTXlEUyjJ8c/eQdv0YjUaJ/2eBgdauXTu7rwY/CNaFlG8Df6j2VjKZJNbWwECSJA0ODs7+CH4QrMu5KU/ssbnmmmvM3gk0UywW0+l0r3fhRJFIZOFT3"
            + "7ictyjnpny5XKYsQ5twDyUSiV5vwYkGBgYaPvKNlLco56Y8Ec/z+Fu1V3DHtftaTDzHD4JFOTrlteFBreH6BRwlHA43O6EPPwsW5eiU/+STT9quEQQB39zgEP39/T6fr9ln8Y"
            + "NgUY4+VvfixYtt1/A8j29uYJYoimvWrDl79mypVFpkv6kgCM1qNRqc6WRRjk75c+fOrVq1qvUal8uFI+aBWYqinD17dmRkhOO4arVaKpXK5fLMzEypVNJ7V6N+mEEzuNyxKEf"
            + "nF3EkENpsgGW5XE4QhHA4rE2yrZdcyuVyqVR65513yuVy244yj8fT9ixupLxFOTrli8Xi7bff3noNIh7Yl8lk+vr6hoaGZn9QlmVZlrWDaFRVLZVKxWLxnXfeKZVK8x4WqR88"
            + "2Rp6bCzK0Sn/xRdftF3D8zyCHtg3MTHRYkwuz/OzR+IoiqJd5pdKpVKpFAwGKQmOa3mLcnTKnzt3ru0aQRBQlwdLiMfjgiC0aJKpE0VRFMX+/n5dr4+UtyhHd1JeuHCh7RpBE"
            + "HDIAVhFLBYz79gfVGwsytEpT+S0QSI+n++Pf/zj1NTU1NTUvn375p1aBSxTVTUajRJP7+gALuetyNEpT7zqcc4ljCAIr7766qlTp37yk5/4/X6/3//973//2LFj4+PjmHJuFb"
            + "Vazbyzmu+9995gMOj1ehH3FoKKc3sOuZYXBOHFF1+88847F34qGAzGYrFwOGzeRSIYSFGUPXv2/PjHPzY8i91ud/0gs0qlUu/ULJVKxr4RGMjRKU/MLIc88vfUU081jHjNN7/"
            + "5zT/84Q8/+MEPcIKYJWhBf//995vXOyBJkiRJ9a6eYrFY79upVqsmvSl0wNEpTzxi3gnX8vfcc88Pf/jD1mvWrFnj8/lyuVx3tgSLVC6Xo9Hohg0butMk1rBTs1wuYwhMzyHl"
            + "27N9v7zH43nqqacoy2688UakvIUUCoV4PK6df9BN8zo1ta587TIfwx27z9EpT6zY2P5a/uDBg1/+8pfbLuN5Ho8OWE4ul2txZHx3aNX8arWazWZPnDiBoO8y/NC2d/XVV/d6C"
            + "yZ66KGHtIfgwa7S6XRfX18PO2ILhcL09DT+CuwVR6c88Vrexp2UQ0NDv/rVr4iL8/n8zMyMqfsBk6RSKZfL1fY8MmPh4p0Rjk55Yl2e4zhRFO33nSqK4vPPP09fn06ncTlmXf"
            + "F43OVyeb3eLrxXqVQ6fvw4vlsY4eiU5zjuww8/bFuStuvd1717937lK18hLi4Wi7t27UIbpaWZ/ShTrVbL5XLHjx+vVCqmvhHogpRvn/LasZQ2u5bftGnTunXriIur1eojjzy"
            + "SzWZN3RKYamBgoP5Ak+FKpdKJEydyuRyuAxjk9JSnDBKx37GUPp9v165d9PX79u3bs2ePefsBs7lcrlAoZPjL4uLdEmwVXh0olUptL3BslvKCILzwwgtLly4lrk+n04888gj9"
            + "HgYwKBwOG1uuKZfLWuUdF+/ss094debzzz+nLLNTaX7Hjh30W3DlcnnLli14fNHS+vv7KYfOExUKhYmJCRxcYyGOPpOS47izZ8+2XWOnTsr169f/7Gc/Iy5WVfXxxx8/evSoq"
            + "VsCUwmCYOwjUSdPnkTEW4vTU/7ixYuUZfYIelmWn376afr6Q4cOPfHEE/iT3NJCoZCxf4na6e9ah3B6yn/44Ydt19jm2/rgwYOSJBEX5/P5hx9+2GadRU7j8XgMf7AZo9Msx+"
            + "kpT6nY8Dxvg+/szZs3059xVxRl27ZtaJ20NJ7nw+Gw4S9rm4se53B6yhNdd911vd7CogQCgd/85jf09X/96191PRYLDAoGg/Q/3eiQ8pbj9JR3wiARQRAOHjxIb508evTojh0"
            + "7UI63NFmWA4EAcXGhUIjFYsQDCTAL0HKc3klJbANftmyZ2Tsxz1NPPUV/6LFYLG7ZsgVNFFYXDofbPuQx7zSxfD4vCELbLltcy1uO01OeeHfRjL98u2Pjxo1th0DV1Wq1xx9/"
            + "fHJy0tQtgdkCgUDr3+vagQTz7ruoqhqNRu+//35Zllt8rT36zRwFKU9K+S996Utm78QMHo/nscceo68/cODAk08+ad5+oAtEUWx2mEHbAwlUVdXGgreOckEQ8Cy0hTg95YmWL"
            + "FnS6y3oppXjKUOgNNlsdseOHZjLbHUNazX008RqtdqePXvuu+++FkGPlLcWp6c8sVnwo48+MnsnhvvlL39J75WuVCq//vWv8/m8qVsCs/X3988urHd2mpiiKNFo9L777mt2o9"
            + "WW4xZszOkpryjKmTNnVq1a1WKNqqrER2TZoWsIlKqqzzzzzEsvvWTqlsBss+e7ViqV1157rePTxMrlcjQavffeexvewkWbjbU4PeU5jjt69GjrlLfc3Ui9Q6D+8Y9/PProo2i"
            + "dtLpQKCQIgtY2s/guqWKxqAX9wk8h5a3FeuVmw507d+7b3/52syqkoiijo6OXLl2anp62Si3y4MGD3/jGN4iLC4XCT3/600KhYOqWwGySJC1ZsiQej588efKDDz4w5DXPnz9/"
            + "4cKFhedZlstlTAC2EKc/FcVxXK1WO3DgQMOSdLFYfOaZZ6x1kfvggw/qGgL1+9//PpPJmLol6IJKpZLJZAy/EMlms6lUat4HcS1vLajYcNz/D3qPx+Pz+bRm4UqlUiwWZz8Na"
            + "ImhgD6fb8eOHfT1e/fuxRAoaG1ycrKvr2/2k7RIeWtByl9pmS8Wiy3GZbA/LkoQhMOHD+saAvXb3/7WKmUo6KFkMikIgt/v1/4nHoyyFlRsqNi/ftm1a9fXvvY14uJSqfTQQw"
            + "9hCBQQJRKJ+s0b9n8WYDakPEd8Dojx7+z169f/6Ec/Ii5WVfVPf/rTK6+8YuqWwE608w+00/0Y/1mAeZDynLVurjYky/Jf/vIX+vrDhw9jCBTopZ1/UKlUULGxFqQ89VhKlr+"
            + "zn3/+efpJBrlcbtu2bezfSQYG1Wq1ffv2KYqCy3kLQcpTU55ZW7du/da3vkVcrCjK9u3bMQQKOqadf8B+MwLU4f8qqr6+vl5voYFAIPCLX/yCvv7Pf/7z3//+d/P2A05AnL0D"
            + "jMC1PMfRzh9m8+LlmWee0TUE6tFHH7X63y4AoAtSnorBlL/jjjtuvvlm4uJisbht2zYMgQJwGqQ8x9GaKRkchEav1WhDoMbHx83cDgCwCCnPcdZspvT7/a0nt8128OBBDIECc"
            + "CakPMfRUp6pTkpRFMPhMPGXUyaTefjhhzEECsCZkPIcR378lRE8z4+MjPA8TzmfoFKpPPzwwxgCBeBYSHkqdh4DCQaDbreb47h8Pt+6YUZV1d27d7/wwgvd2hoAMAcpz3Ecd+"
            + "HChbZrGEl5j8czODio/VtV1dHR0RaLDx8+/Nhjj1nxrgMAGAUpr0PPmykFQRgZGZn9kUKhMDY2tjDHVVU9cODA7373O11jnQHAfpjrAe8J4oNCPR8kEolEFjZ0ZrPZfD4/MDD"
            + "g9Xq1j+Tz+Vwul8lk0ul01/cIAGxBynOcRY6yGRgYWDiBU1Or1SYnJ2dPIa9Wq4lEoltbAwB2oWLDceR++R4+GCVJ0vDwMH19LBazVuMQAJgEKc9x5E7KXtXleZ7fsGED/d3T"
            + "6TSGQAGABimvQ6/abEKhkCRJxMWlUimVSpm6HwCwEKQ8x7E9FNDn8wUCAeJiVVXj8ThaJwGgDinPcQyfY+NyucLhMH19IpFA6yQAzIaU5ziGhwKOjIzQb/nmcjkMgQKAeZDyH"
            + "MdqJ+Xg4KDH4yEuVhQFrZMAsBBS/jJK0aabQwHdbncwGKSvj8VibP6uAoDeQspfRrkB27VOyvqpk8T1ExMTGAIFAA0h5XXoWo9NOBym3wMoFosYAgUAzSDlL6Ncy3cn5f1+v9"
            + "/vJy6u1WrxeNzU/QCApSHlL2OkmVIURV0nGcTj8d4eoAYAjEPKX0a5dWl2J6VWjqf/xaCdRmnqlgDA6pDyl7HQoDI4OKgNgaKoVCponQSAtpDyOph6JqXH46G3TqqqGovFGKk"
            + "yAQDLkPKXUYYCmtdJKQhCJBKhr0+lUuVy2aTNAICdIOX1MSnoI5EIvehfKBQwBAoAiJDylxGPpTSjaOP3+5sNgVqoWq2idRIA6JDyl/Wqxi1Jkq5TJ+PxOIZAAQAdUv4yYsob"
            + "20yp9ySDdDpdKBQM3AAA2B5S/rKeXCCHQiFZlomLy+UyhkABgF5IeX0MPOTA6/XqGgKF1kkA6ABS/jLiOQFGpbzL5dLVOokhUADQGaS8PkZ1UuoaApXP5zEECgA6g5S/jHjCg"
            + "SGdlIFAQNcQKLROAkDHkPKXde0cG7fbHQqF6Ovj8TgLZ+wAgEUh5a/owlBAnucjkYiuIVDFYnEx7wgADoeUv6ILQwHD4bAkScTFpVIJQ6AAYJGQ8vospsemv79f1xCoWCzW8X"
            + "sBAGiQ8leYOhRQFEVdJxkkEgkMgQKAxUPKX2HqM0d6h0DlcjnzNgMAzoGUv8K8oYBDQ0MYAgVAR69tQltI+StMalh0u926hkDF43GcZABOxvP88PCwqaPZHAUpfwUlW/V+5wm"
            + "CMDIyQl+fSqVKpZKutwCwGY/HIwhCf39/rzdiE0j5Kz766KO2a/R2UobDYXqRp1gsYggUwKpVqziOW7NmTa83YhNIed3oN1H9fj/9eqRaraJ1EoDjOK/Xy3GcJEn0g0CgBaT8"
            + "FcQj5okpr3cIVCKRwBAoAFEU608O4nLeEEj5Kwy856n3JIN0Op3P5416dwDrmn397vP5cA928ZDyVxB7bCh19lAopKt1EkOgADSrV6+u/5vnedyDXTyk/BVGdVJ6PB5dQ6Ci0"
            + "ShaJwE082rxKNosHlJet9Z/QrpcLl2tk8lkEkOgADRaD+Xsj+Ae7OIh5a8gnhvTutoeiUR0DYHKZDLExQC2d+ONNy784G233db9ndgJUl63FikfCAS0JjCKarWKIVAAszX88f"
            + "F6vbgHuxhI+SuIjYzNvuFkWdY1BCoWi2EIFECdIAgNexZ4nsexNouBlL9iMbdAeZ4fGRnBECiAjrX4Oxj3YBcDKT8HJehXrFix8IN6h0BNTk7q2xmA3WkHGzQkiiK9FgrzIOX"
            + "n6OzpU5/Pp3cIFFonAeZp3UuDy/mOLWqKqTPN6/QSRTESidC/PJlMYggUwDyyLLd+3lC7B9vsOqz+G6LepeN2u7UKqizLExMTTj4HECk/h6IobR9tnZfykUhE1xCobDbb4eYA"
            + "7KttUzzP8+FwuFwuC4Igy7L2kbZPmNdqtWg06vB7YEh53T777LP6v4eGhuiPbCiKgiFQAHWSJGkda6IoUgoyPp/P5/PRX79cLkejUfzpjJSfg9La+K9//Uv7h9vtXrt2LfGVV"
            + "VVFOR6co14wcbvdS5Ys4ThOlmXtr956uJsqn8/jJ06DlJ+jbcqn02ltlpM2BEpX6ySGQIG98Tx/33330c/pM8/4+PjExESvd8EK9NjM0fo3f6lUqh8eOTw8rGsIFFonwfZUVX"
            + "322WdjsVgPJyVohXhE/Gy4lp+jxVDA2XO3/X6/3tZJY/YHwLxcLlcoFNauXTs4ONjlt65UKtFoFMf/zYOUp0okEtp3jyiKuoZAxeNxDIECR6nVaqlUanp6enh4uGtPM+Xz+Xg"
            + "8jlNDFlrS6w2wRZKkr3/96ws/nsvl/vnPf3Icx/P89773PXqtJpPJvP7660ZuEcAiPv744zfffPPs2bMej2fp0qWmvtfk5OQLL7yAe60N4Vp+jobfJbM7IIPBoK4hUMlk0rDN"
            + "AVhQPp+vF3Do3Qp0Wik1l8sZ/sq2gZSfo+Gfe/XDIz0eD73UiCFQABpVVScmJqanp8PhsLEFHEVRotFouVw28DXtByk/x8KUHx8f1zog9Q6BSqVSuAsEUKcoyr59+7xebzgcp"
            + "tc8WygWi9FoFIX4ttBJOce8S+9isVhvyQqHw7qGQDn53AyAZgqFwhNPPJFKpRb5Z66qqnv27EHEUyDl55jdDFOr1eqznAKBAP3R6mq1ipMMAJpRVXVycnLXrl35fL7jF+F5Xt"
            + "dpB06GlG8qkUhoJ2BIkqR3CBRaJwFaW/xEzFtvvdWozdgbUn6Oejpns1ntrj3P8xs2bKD3BkxOTjr8ADwAokVejPt8PsyDpUDKz6HVCiuVSr3kMjw8rGsIFB6tBiCqnwXfMYw"
            + "WoUDKz6f9IanFvc/nGxgYIH4hTp0E0GXxXZX0H08nQ8rPd/To0XrrpK4hUPU6PgC0Jcvy4ustLpcL92DbQsrPNzU1pf1jZGQEQ6AATLJ69WpDXgf3YNtCyjc2ODioawgUTjIA"
            + "0KXtz1cul9uzZ0/bqQy4B9sWTitrwO12RyKRq64i/QpUVXX//v3nz583e1cAtiEIwl133dXiR2x8fPyll15SFGVqauqjjz664YYbWvS5ffrppzMzM+bs1A6Q8vNp826WLVtGX"
            + "H/s2LF///vfpm4JwGa8Xu8tt9zS8FPaPIYTJ07UP/Luu++eOHFCEITrr7++4ZdIkoSTX1tAys/33e9+l16rKRaLi3yyA8CBbrvttoaRXalU9u7d+5///Gfex1VVfeutt86cOb"
            + "Ny5crly5fP++zSpUvPnj2LY6OaQcrP4ff7h4aGiItrtdq+fftwkgaAXt/5zncWtjYUCoV9+/Z98MEHzb7qgw8+aFbA4Xn+5MmTpuzV+nAm5RWiKA4PD9PXx+NxtE4C6CVJ0sI"
            + "zKdPpNLGFIZPJ5HK5YDAYCATqH9TuweJkkYZwLX+ZNgTq2muvJa7PZrOvvfaaqVsCsKVbbrll9vNQqqoeOnRIV2FdVdUzZ86cPn1aluV6AQf3YJtByl92xx13NLsdtFClUtm/"
            + "f/+lS5dM3RKALQWDwfrllKIoe/fuffvttzt4nWq1OjU1deHCBbfbvXTpUtyDbQYpz3Ec5/F46I+5qqraunoIAM3wPL9u3Tqth7JUKj333HOLLHuWy+WpqSme52+66Sbcg20Id"
            + "XlOEAS9Q6AwgQygM263W7txmslkksmkIec+1Wq1ZDI5PT1t7LhB20DKc5FIhP7sXKFQ0IZA4VYPQAdWr16tqmoymcxkMsa+crlcxuVXQ04/4WBgYEDXECitO16SpMUfmgrgQJ"
            + "Ik7dmzx/CIhxYcnfKSJOlqndSGQPE8PzIycvXVV5u3MQBb4nk+kUi0PZoGjOXclNc7BCqdTmtDoEKhkCzL9OMqAUCjqioeMek+56Z8KBTSNQQqlUpxHOf1erVnMZDyAGAJDk1"
            + "5n883+8G51lRV1aZHzZ4rsmQJmlABwAKcmPIulyscDtPXJxIJrQl3ZGSk3o2DI60BwBKcmPKzw7qtXC6nDYEKBAL0syoBABjhuJTXFdaKoiQSCY7jZFkOhUKzP4VreQCwBGel"
            + "vNvtnhfWrcVisVqtprVOLjzp1OjdAQAYz0Epz/N8JBKhp/PExITW2BsOhxd246DHBgAswUEp3zCsmymVSuPj4xzH+Xw+v9+/cAFSHgAswSkp39/f3zCsG9ImT3IcJ4oi/axKA"
            + "AAGOSLlRVHU2zqpPaEXiUSaXbMvHHYDAMAgR6T8yMgIvcCSzWZzuRzHccFgEK2TAGB19k/5oaEht9tNXFypVLTWSbfb3XbMN5opAYB9Nk95j8cTDAaJi+snGSxZsoQyVwTNlA"
            + "DAPjunvCAIuu6dplIprXXS7XZTyu5IeQBgn51TPhKJ0O+RFotFbQgUx3HEs+NRsQEA9tk25f1+v64hUFrrpMaQWZQAACywZ8pLkqSrdTIej88e4kpMeTRTAgD7bJjyDY+daSG"
            + "dThcKhdkfwdhuALANG6a8NrGPuLhcLmtDoDqAQw4AgH12S/n6xD4KVVVjsdjC+gxxNCVSHgDYZ6uUnz2xj6I+BKozGAoIAOyzVcpHIhF6d2M+n9eGQDVUq9XavgI6KQGAffZJ"
            + "+UAg4PV6iYsVRYnH4y0WUFIeAIB9Nkn5hRP7WovH461znNJMiWt5AGCfHVJeb+vkxMREsVhsvYbSTIkTDgCAfXZIeb1DoCYnJw15X/TYAAD7LJ/yzSb2NaQNgaJUYyjX8kh5A"
            + "GCftVNe78S+ZDJJ7IWn/CZAxQYA2GftlG8xsW+hbDbbonVyHnRSAoA9WDjlh4aG6BP7FEXRhkARffLJJ53sCQCAMVZNebfbrWsIFLEcP/tLKMtwOQ8AjLNkyguCQJnYVzcxMa"
            + "ENgaIjHkuJ0jwAMM6SKR8Oh3UNgTKqdXIhtNkAAOOsl/J+v7+/v5+4eN4QKDritTxSHgAYZ7GU1zsEKpFIdDYSBEMBAcAerJTyPM9HIhFdQ6Dy+Xxn70U8rQxDAQGAcVZK+VA"
            + "o5Ha7iYsrlUrHQ6A4nEkJAHZhmZT3eDy6hkBFo9EuVF3QSUZSFrsAAAo4SURBVAkAjLNGyrtcLl2tk8lkcjFDoDjyUEB0UgIA46yR8uFwWNcQqEwmY+p+6jAUEAAYZ4GUDwQC"
            + "Pp+PuLharbYeAkVHac5BxQYAGMd6ykuSpGsIVCwWM+rGKZopAcAGmE55nuc3bNhAr31PTk62HQJFR0l5dFICAOOYTvnh4WFdQ6AmJiYMfPfOHqcCAGAKuynv8/kGBgaIizs4d"
            + "dIQOOEAABjHaMq7XC5dQ6ASiQSx95GO8oJIeQBgHKMpPzIyYtIQKGOhXx4AGMdiyusdApVMJs3YBoYCAoANMJfybrd77dq1xMVaOd6kM2cwFBAAbICtlNeGQNHLIK+99preIV"
            + "B0GAoIADbAVsoPDw/rGgI1Pj5u3mYwFBAAbIChlPf7/X6/n7i4Vqt1NgTKcGizAQCWsZLyoijqGgIVj8fNfmoJQwEBwAaYSHme53WV4zOZTMdDoOiIdXlUbACAZUykfDAY1DU"
            + "EyqTWyXmIrTu4+woALOt9yns8nsHBQeLirg2B4jAUEABsoccpr7VO0tenUqlFDoGiQyclANhAj1M+EonoGgKVTqdN3c9s6KQEABvoZcoPDAzoGgKVSCRM3U9nrrnmml5vAQCg"
            + "qZ6lvCRJw8PD9PWxWKz7B75T3hGdlADAst6kvN4hUOl02sAhUHQYCggAVteblA+FQrqGQKVSKVP30wylzQZDAQGAZT1IeZ/PFwgEiItVVY3H4726pkYzJQBYXbdT3uVy6TrJI"
            + "JFIdK11sjPopAQAlnU75UdGRuixmMvlejUESkMZCohOSgBgWVdTfnBwUNcQKDZbJ+dBygMAy7qX8m63OxgM0tebNwSKjtJJiYoNALCsSymv99TJiYkJ84ZA0X3++ee93gIAwK"
            + "J0KeXD4TA7Q6DoiL09aKYEAGZ1o6a8YcOGn//851qDvKIo6XS6XC43W1yr1eLxeBd2RdH9p20BAIxlbsoLgnDkyJFQKDT7g36/P5/Pj46ONrxSjsfjlM4WpuCQAwBglokVG1E"
            + "Ux8fH50W8xufz3XPPPQvL9NlstgtDoOiIv2+Q8gDALBNTfufOnbfddluzz3q93oGBgdkfqVQqlmidXAjNlADALLNSfuPGjRs3bmy9xu/31/+tqmosFmPtdDAMBQQAqzMl5T0e"
            + "z+7du9suk2W5no+pVKrFLdle6XnDPgDAIhmf8oIg7N+/n1iq1modhUKhm0Og6DAUEACszviU37Jli64jJ6vVKjutk/NgKCAAWJ3BKT80NLR582bi4nw+r0W81dvSMRQQAJhlZ"
            + "MrLsrx//376+mw2m06nC4WCgXswHKWZEp2UAMAsI1N+9+7dsiwTF+fz+fHx8V4NgTIWKjYAwCzDUv7BBx9cv349cXG1Wh0bG2OwdXIhSpsN7r4CALOMSXmfz7d9+3b6+tHR0W"
            + "g0yvgQKA2aKQHA0gxIeUEQDh06RK9Np9Ppl19+ubdDoOgof23gWh4AmGVAyu/cudPn8xEXl0qlWCzGbOvkQpT+H9TlAYBZi0359evXb9q0ibhYVdWxsbHR0VGblUHQYwMAzFp"
            + "Uysuy/Nxzz9HXHzlyJBaLFYvFxbxpl1Gu5ZHyAMCsRaX8/v376WOScrnckSNHGBkCRYehgABgaZ2n/NatW4eGhoiLFUWJxWKxWKzjt+sVYnEJQwEBgE0dpnwgEKCfZMBxnFaO"
            + "t9wQKA6dlABgcZ2kvHbqJP3wlvHx8bGxsVwu18F7WQWaKQGATZ20AO7evdvj8RAXl0ql0dFRiw6B4madY+NyuTwej8fjEUWxVCpVKpXZv7fQTAkAbNKdTZQhUHW1Wi0ajcbjc"
            + "fZPMmjN4/Hcfffd9Qt2r9fLcVwgEHj55ZdLpRKHlAcAe/B4PIqifEF28OBB+lnzbJIkaf/+/c3+Ay9evLhz584tW7bMnm4IAMAOHXV5rRy/YsUK4vpsNnvkyBE2h0DRLVmyZN"
            + "26dc0+KwjC3Xff3c39AADooiPlN2/eTL8wVxTlwIEDVmydnGfjxo2t76y63W5ZltFJCQBsoqb80NDQli1biItVVR0dHbXBECiO4yj3men3ogEAuoyU8qIo6hoCNT4+Pjo6ms/"
            + "nO90VQygHscmyjKGAAMAmUso/99xz9CFQxWJxbGzMHkOgiFwuF46yAQA2tU/5TZs26RoCdeDAgWg0avXWSV14nkcnJQCwqU3K+3y+nTt30l9OO3XSEkOgiMrlcts1PM/j2VcA"
            + "YFOrlNc7BCqTyYyNjWUyGSM2xgrMfQUAS2uV8tu3b6cPgapUKlpfjRG7YgjxtDIEPQCwqWnKr1+//sEHHyS+iqqqWjnefic4nj17tu0aURRRlwcANjVOeVmWd+/eTX+VV1991"
            + "XJDoIyFHhsAYFPjlN+/fz+9dTKfz4+Ojk5OThq3K4YQf3VJkmTyRgAAOtEg5Tdv3kwfAlWtVrUhUI5qnVwIzZQAwKb5KR8IBLZu3Ur/+tHRUYsOgSIi/qehmRIA2DQ/5bdv30"
            + "5/WD+dTo+NjWWzWaN3xRBiyiPiAYBNc1Le7/fTazWlUikWi1l3CBQRvWsIQQ8ADJqT8vSTDFRVjcViBw4csH05nvLsK4dmSgBg1ZyUX758OfHLjhw5Mjo6qg3DAw2aKQGAQXN"
            + "S/qqrSEdU5nK5sbExu7ZOzkO8lsexlADApjmxfvTo0bZfoCiK1jpp2pbYQqzLo1wDAGyak/Ivvvji+fPnW3/B2NjYgQMHbDAEio4S9DzPYyggADBoTsqrqrp169YWoTY2Nmab"
            + "IVB0lKINGmwAgE3zC/FPP/30rl27GjaJaxGfTCa7sjHr6evr6/UWAADmm19NrtVqW7dufeutt9atW+f3+7UP5nK5fD6fzWadNgRKUywW287vRiclALCpQTDVarVnn312YmJiz"
            + "Zo1Wt+IoijT09NOPnKSAikPAAxqGkyFQqFQKHRzK8yiHHIgCAJK8wDAIFKDvMMRU74LOwEA0Aspbwx0UgIAm5Dy7c3MzLRdg3INALAJKW8YFG0AgEFI+faIR9nQZygCAHQNUr"
            + "49+hHzaKYEANYg5dsjpjyaKQGAQUj59ogVG9TlAYBBSHnDYMA3ADAIKd8e8WgHl8uFujwAsAYpbyQUbQCANUj59ignHHAcJ4oiUh4AWIOUb4+Y8hw6KQGAPUh5w6CTEgAYhJQ"
            + "nodyARbkGABiElDcMz/MYCggArEHKkxAHfKMuDwCsQcqT0A85MHsnAAC6IOVJKCnvcrmQ8gDAGqQ8CaVig3INADAIKW8kDAUEANYg5UlOnz7ddk21WqWfRA8A0B1IeZKXX365"
            + "7ZpKpVKpVLqwGQAAOqQ8STabfe+991qvyWQyZ8+e7c5+AACIkPJUGzZsaPHZfD6fz+fT6XTX9gMAAAZ74IEHPvvssy8WePPNN7dt2+b1enu9QQAAWJxbbrnl2LFj//3vf7/44"
            + "ouLFy+eOnXq4MGDDzzwgNvt7vXWAAAa+L9eb8CSPB6Px+NZsWJFtVqdmZkpFouqqvZ6UwAAAAAAAAAAAABgAf8PcQ1FytAKWNoAAAAASUVORK5CYII=\"/>\n" + 
            "</defs>\n" + 
            "<g id=\"surface1\">\n" + 
            "<rect x=\"0\" y=\"0\" width=\"229.606\" height=\"233.858\" style=\"fill:rgb(100%,100%,100%);fill-opacity:0;stroke:none;\"/>\n" + 
            "<use xlink:href=\"#image6\" mask=\"url(#mask0)\" transform=\"matrix(0.35985,0,0,0.36,6.912,176.571)\"/>\n" + 
            "<use xlink:href=\"#image12\" mask=\"url(#mask1)\" transform=\"matrix(0.480119,0,0,0.480027,2.402,3.061)\"/>\n" + 
            "</g>\n" + 
            "</svg>"+ 
            "</a>";
    }
}
