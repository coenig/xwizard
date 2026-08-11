/*
 * File name:        Watchdog.java (package veryFastPDF.web)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  18.07.2015 (08:24:40)
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

package veryFastPDF.web;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

import org.joda.time.DateTime;

import eas.GlobalVariables;
import eas.miscellaneous.convenience.GeneralDialog;
import mainServlet.WebLink;
import veryFastPDF.VFPVariables;

/**
 * Watches for long-time operations outside of Java and kills the programs
 * given in reset method if they take too long. Note that inner-Java loops
 * are not covered by this procedure. Note also that watchdog will kill
 * the processes in question completely and immediately, and all processes
 * will be killed regardless of which are actually causing the delay.
 * 
 * @author Lukas König
 */
public class Watchdog extends Thread {
    

    public static final String[] RECIPIENTS = new String[] {"lukas.koenig@kit.edu"};
    
    private static final LinkedList<SessionMetaInf> SCC_SESSIONS_SINCE_LAST_EMAIL = new LinkedList<>();
    private static final LinkedList<SessionMetaInf> NON_SCC_SESSIONS_SINCE_LAST_EMAIL = new LinkedList<>();
    
    private static final int TIME_MS_PER_CYCLE = 4_950;
    public static final long CYCLES_PER_TRIAL = 48;
    private static final int MAX_TRIALS_BEFORE_SHUTDOWN = 1;

    public static final String WATCH_DOG_SIGNATURE = "*EASy watchdog*";
    private long timeLeft;
    private boolean watching = false;
    private boolean running = false;
    private boolean paused = false;
    private int trials;
    private String myID;
    
    private static int numberOfWatchdogsActive;
    
    private static HashSet<String> alreadyNotifiedScripts = new HashSet<>();
    
    public Watchdog(String script) {
        this.currentScript = script;
//        logWeb("CAUTION: Note that " + WATCH_DOG_SIGNATURE
//                + "has been deactivated in the source code. "
//                + "I will keep collecting session data and send daily emails, "
//                + "but I will NOT create any threads or monitor external processes.");
    }
    
    public static void addSession(SessionMetaInf metaInf) {
        if (metaInf.getCompleteUserInformation().contains("SCCCloudService")) {
            SCC_SESSIONS_SINCE_LAST_EMAIL.add(metaInf);
        } else {
            NON_SCC_SESSIONS_SINCE_LAST_EMAIL.add(metaInf);
        }
    }
    
    @SuppressWarnings("unused")
    private long getTimeLeft() {
        return this.timeLeft;
    }
    
    private void killProcess(String process) {
        logWeb("Killin " + process + "...");
        WebLink.killProcess(process);
    }

    private void reset() {
        if (WebLink.isDebugMode()) {
            logDebug("I'd like to kill you all, but I cannot due to DEBUG mode.");
            this.stopWatching();
        } else {
            logWeb("I'm goin to kill you now! "
                    + "(Note that this can, but does not have to result in an error.)");
            killProcess(WebLink.getGraphvizProcessName());
            killProcess(WebLink.getLatexProcessName());
            killProcess(WebLink.getPDF2SVGProcessName());
            
            if (this.trials >= MAX_TRIALS_BEFORE_SHUTDOWN) {
                initiateCompleteShutdown();
                this.stopWatching();
            }
        }
        
        this.trials++;
        this.resetTimeLeft();
    }
    
    private String currentScript = "";
    
    private void initiateCompleteShutdown() {
        boolean seriouslyDoit = false;
        this.resetTrials();

        if (!alreadyNotifiedScripts.contains(currentScript)) {
            WebLink.sendEmail(
                    RECIPIENTS, 
                    "Your tomcat has been shut down", 
                    "Lieber Lhuk, \n\ndein komischer Selfmade-Sicherheitsmechanismus (" + WATCH_DOG_SIGNATURE + ") hat gerade (" 
                            + new Date(System.currentTimeMillis())
                            + ") den Tomcat heruntergefahren (bzw. es versucht).\nMit Glück auch wieder gestartet, aber verlass dich nicht darauf.\n\nToll gemacht und viele Grüße\n\nLhuk"
                            + (seriouslyDoit ? "" : "\n\nPS. Ich habe das NICHT WIRKLICH gemacht, weil die Variable seriouslyDoit = false gesetzt ist. "
                                    + "Aber es wäre eigentlich dran gewesen - check mal, was da los war! "
                                    + "Die üblichen Verdächtigen (LatexPDF etc.) werde ich aber gleich noch killen.")
                            + "\n\nHier kommt noch das Skript, auf dessen Basis ich in diesen Zustand gekommen bin:\n\n"
                                    + currentScript);
            
            alreadyNotifiedScripts.add(currentScript);
        }
        
        if (seriouslyDoit) {
            logWeb("Something bad must have happend. "
                    + "Shutting down (and restarting) server. Sending email to " 
                    + Arrays.toString(RECIPIENTS)
                    + ".");
            
            WebLink.attemptRestartingTomcatServer();
            
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
            }
            
            logDebug("Server shutdown presumably failed (otherwise I could not write this message) -- Initiating emergency exit.");
            
            for (String tomcatProcess : WebLink.getTomcatProcessNames()) {
                this.killProcess(tomcatProcess);
            }
            
            for (String javaProcess : WebLink.getJavaProcessNames()) {
                this.killProcess(javaProcess);
            }
        }
    }

    private void resetTimeLeft() {
        this.timeLeft = CYCLES_PER_TRIAL;
        logDebug("I've reset");
    }
    
    private void resetTrials() {
        this.trials  = 1;
    }
    
    private static DateTime getLastEmailTime() {
        String loadValue = GeneralDialog.loadValue("lastEmailSendTime");
        long timeLong = loadValue == null ? 0 : Long.parseLong(loadValue);
        return new DateTime(timeLong);
    }

    private void setLastEmailTime(DateTime newTime) {
        Long timeLong = newTime.getMillis();
        GeneralDialog.storeValueOf("lastEmailSendTime", timeLong);
    }

    @Override
    public void run() {
        this.running = true;
        numberOfWatchdogsActive++;

        while (!terminationRequested) {
            try {
                if (watching) {
                    logDebug("I'm havin your back");
                    timeLeft--;
                    if (timeLeft < 0) {
                        this.reset();
                    }
                }

                maybeSendEmail();
                
                Thread.sleep(TIME_MS_PER_CYCLE);
            } catch (InterruptedException e) {
                logDebug("Sleep mode failed once - strange" + e);
            }
        }

        numberOfWatchdogsActive--;
    }

    private boolean terminationRequested = false;
    
    private String sessionsSinceLastTime() {
        String s = "Cookie-User-Name: " + this.myID + " (Don't care too much about the Cookie-User-Name, "
                + "it's the Watchdog ID, and it's pretty much random which Watchdog instance -- out of the " 
                + "+++ " + numberOfWatchdogsActive + " +++ active Watchdogs -- gets to send the message.)\n\n";
        
        s += "--SESSIONS--\n\n";
        
        
        if (SCC_SESSIONS_SINCE_LAST_EMAIL.size() + NON_SCC_SESSIONS_SINCE_LAST_EMAIL.size() > 0) {
            s += "Number of sessions: " 
                    + (SCC_SESSIONS_SINCE_LAST_EMAIL.size() + NON_SCC_SESSIONS_SINCE_LAST_EMAIL.size())
                    + "; NON-SCC sessions: "
                    + (NON_SCC_SESSIONS_SINCE_LAST_EMAIL.size())
                    + "; SCC sessions: "
                    + (SCC_SESSIONS_SINCE_LAST_EMAIL.size())
                    + " (details follow)\n\n";
            
            for (SessionMetaInf m : NON_SCC_SESSIONS_SINCE_LAST_EMAIL) {
                s += m.toString() + "\n\n";
            }
            
            s += "\n\n---------------------- SCC -----------------------\n\n";
            
            for (SessionMetaInf m : SCC_SESSIONS_SINCE_LAST_EMAIL) {
                s += m.toString() + "\n\n";
            }
        } else {
            s += "<NONE>\n(This is very unusual!)";
        }
        
        return s;
    }
    
    private String dd(int i) {
        String s = "" + i;
        
        if (s.length() < 2) {
            return "0" + s;
        }
        
        return s;
    }
    
    private String format(DateTime time) {
        return dd(time.getDayOfMonth()) + "." + dd(time.getMonthOfYear()) + "." + time.getYear() + " - " 
               + dd(time.getHourOfDay()) + ":" + dd(time.getMinuteOfHour()) + ":" + dd(time.getSecondOfMinute());
    }
    
    private void maybeSendEmail() {
        DateTime time = new DateTime(System.currentTimeMillis());
        DateTime lastTime = getLastEmailTime();
        if (time.minusDays(1).isAfter(lastTime)) {
            if (this.isAlive() && !this.isInterrupted()) {
                setLastEmailTime(time);
                
                WebLink.sendEmail(RECIPIENTS, 
                        "Daily message from " + VFPVariables.PROG_NAME_XWIZZ, 
                        "This is your daily information about what has been going on at " 
                            + VFPVariables.URL_TO_XWIZZ_SERVER_SIMPLE + ". Now, it's \n\n"
                            + format(time)
                            + "\n\nThe last email has been sent to you on\n\n" 
                            + format(lastTime) + "\n\n"
                            + "If this is an unusual time, maybe it was just the initialization "
                            + "time after a server restart, and you have not received any emails before. "
                            + "You will be receiving (roughly) daily summaries by email "
                            + "as long as you're in the list of recipients. "
                            + "In the following, you will see a summary of what has been going on "
                            + "since the last email you received:\n\n"
                            + sessionsSinceLastTime()
                            + "\n\nTo unsubscribe, you'll have to send a message to lukas.koenig@kit.edu. (Don't reply to this email!)");
                
                logWeb("Email(s) sent to " + Arrays.deepToString(RECIPIENTS) + ".");
                SCC_SESSIONS_SINCE_LAST_EMAIL.clear();
                NON_SCC_SESSIONS_SINCE_LAST_EMAIL.clear();
            } else {
                logWeb("Email has not been sent by Watchdog " + this.myID
                        + ".\nWatchdog alive:       " + this.isAlive() 
                        + ".\nWatchdog interrupted: " + this.isInterrupted()
                        + ".\nDon't worry, probably another watchdog has done this in the meantime"
                        + ".\nIf not, this will only cause the process to be performed a little later.");
            }
        }
    }

    @Override
    public void start() {
        try {
            super.start();
        } catch (Exception e) {
        }
    }
    
    private void logWeb(String s) {
        GlobalVariables.getParameters().logWeb(
                WATCH_DOG_SIGNATURE 
                + "[" + myID
                + "] " + s 
                + " (" + timeLeft + " left; "
                + "trial " + this.trials + "/" + MAX_TRIALS_BEFORE_SHUTDOWN + ").");
    }
    
    private void logDebug(String s) {
        GlobalVariables.getParameters().logDebug(
                WATCH_DOG_SIGNATURE 
                + "[" + myID
                + "] " + s 
                + " (" + timeLeft + " left; "
                + "trial " + this.trials + "/" + MAX_TRIALS_BEFORE_SHUTDOWN + ").");
    }

    public void startWatching() {
        this.paused = false;
        this.watching = true;
        this.resetTimeLeft();
        this.resetTrials();
        try {
            this.start();
        } catch (Exception e) {
        }
    }
    
    public void stopWatching() {
        this.resetTimeLeft();
        this.resetTrials();
        this.paused = false;
        this.watching = false;
        this.currentScript = "Watchdog has been stopped, no script stored.";
        this.terminationRequested = true;
    }
    
    public void pauseWatching() {
        if (!running) {
            this.start();
        }

        if (this.watching) {
            this.paused = true;
            this.watching = false;
            logDebug("Watchdog paused");
        }
    }
    
    public void resumeWatching() {
        if (!running) {
            this.start();
        }
        
        if (this.paused) {
            this.paused = false;
            this.watching = true;
            logDebug("Watchdog resumed; I'm having your back again");
        }
    }
}
