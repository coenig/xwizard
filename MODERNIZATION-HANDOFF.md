# XWizard Modernization — Handoff & Insights

_Snapshot date: 2026-08-10 • Repo: `C:\eig\xwiz-code` • Branch: `master`_

This document captures the full context, decisions, and operational knowledge for
the XWizard modernization. **Steps 0–4 are complete**: the app builds with Maven,
targets **Java 21**, uses the **Jakarta** namespace (servlet + mail), still runs as
a desktop Swing app, and has been **deployed and verified on Tomcat 11**.
See **§7** for the build/run‑on‑localhost quickstart.
For the current Docker-based localhost run flow, see `DOCKER-LOCALHOST-RUN.md`.

---

## 1. What XWizard is

- A Java web application (servlet `/Wizz`) that renders educational computer‑science
  artifacts (automata, BDDs, Huffman trees, grammars, Turing machines, gnuplot
  charts, LaTeX, etc.) to PDF/SVG. It also runs as a **desktop Swing app**
  (`veryFastPDF.VFPStarter`).
- Originally depended on a prebuilt binary `eas.jar` (from a second repo,
  `C:\eig\easyagentsimulation`). That source is now **inlined** under `src/eas/`
  (~90 files), so `eas.jar` and the second repo are no longer needed.

### Source layout (single tree)
```
src/
  eas/            # inlined "EAS" framework + math/util/gnuplot/cloning helpers
  mainServlet/    # Wizz.java (servlet), WebLink.java (mail + paths), SQLQueries.java
  veryFastPDF/    # the actual app: algorithms/, pdfProcessors/, plugin/, script/, web/
WebContent/       # web resources (JS, CSS, WEB-INF/, META-INF/)
pom.xml           # Maven build (added in Step 1)
mvn-repo/         # in-project Maven repo for artifacts not on Central (leores, miglayout 4.0)
```

---

## 2. Toolchain & environment (CRITICAL — no system JDK/Maven on PATH)

Everything runs off a **bundled JDK 21** and a **downloaded Maven**, behind a
**TLS‑intercepting corporate proxy**.

| Thing | Location / value |
|---|---|
| JDK 21 | `C:\Program Files\Freeplane\runtime` (`bin/javac.exe`, `java.exe`, `jar.exe`, `javap.exe`) — JetBrains Runtime 21.0.6 |
| Maven | `C:\eig\tools\apache-maven-3.9.9` (downloaded from `archive.apache.org`; `dlcdn` 404s for 3.9.9) |
| Source encoding | **UTF-8** |
| Proxy | `http://127.0.0.1:3129` (both http & https). `http_proxy`/`https_proxy` env vars are set for `curl`. |
| Maven proxy config | `C:\Users\<you>\.m2\settings.xml` with `<proxies>` (http + https, host 127.0.0.1 port 3129). Machine‑local, **not** committed. |
| TLS trust | The proxy re‑signs TLS with a corporate CA that the JDK's `cacerts` does **not** trust (curl works because Windows trusts it). Maven must run with `MAVEN_OPTS=-Djavax.net.ssl.trustStoreType=Windows-ROOT` so the JVM uses the **Windows cert store**. Without it: `PKIX path building failed`. |

### Canonical Maven invocation (use for every build)
```bash
cd /c/eig/xwiz-code
export JAVA_HOME="/c/Program Files/Freeplane/runtime"
export PATH="/c/eig/tools/apache-maven-3.9.9/bin:$PATH"
export MAVEN_OPTS="-Djavax.net.ssl.trustStoreType=Windows-ROOT"
mvn clean package        # -> target/XWizard.war
```
> Tip: after a failed dependency resolve, the miss is cached — re‑run with `mvn -U …`.

### Run the desktop app (after `mvn package`)
```bash
java -Xss10M -cp "target/XWizard/WEB-INF/classes;target/XWizard/WEB-INF/lib/*" veryFastPDF.VFPStarter
```
This is what `Start-VFP.bat` now does.

> **Ad‑hoc classpath gotcha:** native Windows `java.exe` cannot resolve git‑bash
> `/tmp/...` paths in `-cp`. Use repo‑relative paths when testing manually.

---

## 3. Build artifacts & git hygiene

- `mvn package` → `target/XWizard.war` (finalName `XWizard`) + exploded
  `target/XWizard/WEB-INF/{classes,lib}`.
- `.gitignore` covers `/build/`, `/target/`, `/sharedDirectory/`, `*.log`,
  `/logging/conf.txt`, scratch `/_*.py`, `/_*.txt`, and various runtime `*.dat`.
- **`mvn-repo/` IS committed** (holds artifacts not on Central).
- External runtime tools (full features) are configured via
  `sharedDirectory/externalFilePaths.dat`: Graphviz `dot.exe`, MiKTeX
  `pdflatex.exe`, `pdftk.exe`, `pdf2svg.exe`, optional SumatraPDF.

---

## 4. Work completed (Steps 0–3)

| Step | Commit | Summary |
|---|---|---|
| Carve | `38707a9` | Inline EAS source, drop `eas.jar`, start desktop mode directly (no plugin/starter). |
| git hygiene | `d677e96`, `14bce6a` | `.gitignore`, untrack `build/`, ignore `sharedDirectory/`, remove a bogus `pdf2svg.exe` (was HTML). |
| **0** | `a8d1d34` | **Drop Apache Axis SOAP entirely** — deleted `src/webService/`, `server-config.wsdd`, `wsdl/`, Axis servlets in `web.xml`, and 5 Axis jars. |
| **1** | `5112243` | **Introduce Maven** (WAR). `sourceDirectory=src`, `warSourceDirectory=WebContent`, Java 8, UTF‑8. Removed 17 bundled jars from `WebContent/WEB-INF/lib`. |
| **2** | `940d846` | **Bump safe library versions** (details below). |
| **3** | `23c6588` | **iText 2.1.5 → OpenPDF 1.3.43** (drop‑in maintained fork). |
| **4a** | `b060947` | **Compile & target Java 21** (`maven.compiler.release=21`; no source changes needed). |
| **4b** | `133fbcb` | **Jakarta Mail** — `com.sun.mail:javax.mail` → `org.eclipse.angus:angus-mail`; `javax.mail.*` → `jakarta.mail.*` in `WebLink.java`. |
| **4c** | `8ce86d2` | **Jakarta Servlet + Tomcat 11** — `javax.servlet-api` → `jakarta.servlet-api:6.1.0`; `javax.servlet.*` → `jakarta.servlet.*` in `Wizz.java`; `web.xml` → Jakarta EE 6.1 schema. |

### Validated at each step
- Full app compiles (0 errors, bytecode major 65 / Java 21); `mvn package` builds the WAR.
- **Desktop mode runs with 0 exceptions** (window opens, config/paths load).
- **Web app verified on Tomcat 11.0.24** (see §7): WAR deploys clean and the Jakarta
  servlet serves real pages (`?help` → ~202 KB HTML, `?impressum` → ~69 KB). DB‑backed
  rendering still needs a running MySQL (see §7.5).

---

## 5. Current dependency state (`pom.xml`)

Java release **21**. All from Maven Central except where noted.

| Dependency | Version | Notes |
|---|---|---|
| `jakarta.servlet:jakarta.servlet-api` | 6.1.0 | `provided` — Servlet 6.1 (Tomcat 11 / Jakarta EE 11) |
| `org.eclipse.angus:angus-mail` | 2.0.3 | Jakarta Mail 2.1 impl (`jakarta.mail.*` namespace) |
| `org.apache.xmlgraphics:batik-all` | 1.19 | SVG (import moved: `org.apache.batik.anim.dom.SAXSVGDocumentFactory`) |
| `xml-apis:xml-apis-ext` | 1.3.04 | Batik SVG/CSS DOM interfaces |
| `org.jfree:jfreechart` | 1.5.6 | jcommon dropped (only `chart.*`/`data.xy.*` used) |
| `joda-time:joda-time` | 2.14.3 | (could migrate to `java.time` later) |
| `org.jodd:jodd-util` | 6.3.0 | only `jodd.io.StreamGobbler` |
| `com.miglayout:miglayout` | **4.0** | **from `mvn-repo/`** — do NOT bump (see gotchas) |
| `com.fathzer:javaluator` | 3.0.6 | expression eval in `RepresentableDefault` |
| `com.github.librepdf:openpdf` | 1.3.43 | keeps `com.lowagie.text` package |
| `org.objenesis:objenesis` | 3.6 | deep‑clone strategy |
| `commons-io:commons-io` | 2.22.0 | |
| `org.apache.commons:commons-lang3` | 3.20.0 | |
| `commons-logging:commons-logging` | 1.4.0 | |
| `com.mysql:mysql-connector-j` | 9.1.0 | `runtime`; driver set in `context.xml` |
| `org.leores:leores` | 1.0 | **from `mvn-repo/`** — not on Central; used by gnuplot code |

`WebContent/META-INF/context.xml` uses `driverClassName="com.mysql.cj.jdbc.Driver"`.

---

## 6. Hard‑won insights & gotchas

- **Maven + corporate proxy:** needs both `~/.m2/settings.xml` proxy entries **and**
  `MAVEN_OPTS=-Djavax.net.ssl.trustStoreType=Windows-ROOT`. This is the single most
  important operational fact.
- **MigLayout must stay at 4.0.** Every newer version regresses *this* GUI:
  - 4.2 / 5.x → `ConcurrentModificationException` in `MigLayout.checkCache` at
    `setVisible(true)`.
  - 5.x / 11.x → additionally a cyclic‑size `StackOverflowError`.
  - Building the window on the EDT does **not** fix it. 4.0 runs cleanly, so it's
    pinned via `mvn-repo` (`com.miglayout:miglayout:4.0`). Root cause is a
    MigLayout internal behavior change, not app code — a proper fix would mean
    finding the cyclic size constraint in `VFPWindow`.
- **jodd:** `StreamGobbler` moved from `jodd-core` to `org.jodd:jodd-util` (still
  package `jodd.io`), and its constructor changed to
  `StreamGobbler(InputStream, OutputStream)` (was `(InputStream, String)`).
  `ConvenienceMethods` now pumps process output to `System.out`/`System.err`.
- **jfreechart 1.5.x dropped jcommon** — fine here because only
  `org.jfree.chart.*` and `org.jfree.data.xy.*` are used (no `org.jfree.ui/util`).
- **iText → OpenPDF is zero‑code** because OpenPDF preserves `com.lowagie.text.*`.
  It also removed the old **BouncyCastle `bcmail`** transitive that **partially
  shadowed `javax.mail`** (an order‑fragile classpath bug). The "deprecated" PDF
  processors (`JavaPDF`, `GNUPlotPDF`, `SchemDrawPDF`) are actually **live**
  (wired via `PDFProcessorFactory`, used by `BDD`/`JavaPDFCode`), so iText could
  not simply be deleted.
- **Coordinates that didn't exist at the original pinned versions** (resolved to
  nearest on Central during Step 1/2): batik 1.7→1.17→1.19 (import move),
  jfreechart 1.0.13→1.5.6, miglayout combined not on Central (kept local 4.0).
- **Dropped features during the carve:** MARB and Calc (and their algorithm
  packages) were removed; the plugin/starter framework was bypassed so
  `VFPStarter.main` opens `VFPWindow` directly.

---

## 7. Build & run on localhost (Steps 4a–4c — DONE & verified)

All of Step 4 is complete. The web app has been deployed and smoke‑tested on
**Apache Tomcat 11.0.24** running on the bundled **JDK 21**.

### 7.1 What changed (namespace surface — only 2 code files)
- **`src/mainServlet/Wizz.java`** — 6 imports `javax.servlet.*` → `jakarta.servlet.*`
  (`ServletException`, `annotation.WebServlet`, `http.{Cookie, HttpServlet,
  HttpServletRequest, HttpServletResponse}`). Still `@WebServlet("/Wizz")`.
- **`src/mainServlet/WebLink.java`** — 7 imports `javax.mail.*` → `jakarta.mail.*`
  (+ inline `new javax.mail.Authenticator()` → `jakarta.mail.Authenticator()`).
- **`WebContent/WEB-INF/web.xml`** → Jakarta EE `web-app` 6.1 schema/version.
- **`WebContent/META-INF/context.xml`** `type="javax.sql.DataSource"` left as‑is
  (`javax.sql` is JSE, **not** Jakarta — correct to keep).
- `grep -rn "javax\.(servlet|mail)" src` now returns **zero**.

### 7.2 `pom.xml` (final state)
- `jakarta.servlet:jakarta.servlet-api:6.1.0` (`provided`) — was `javax.servlet-api:4.0.1`.
- `org.eclipse.angus:angus-mail:2.0.3` (brings `jakarta.mail-api` 2.1 +
  `jakarta.activation`) — was `com.sun.mail:javax.mail:1.6.2`.
- `<maven.compiler.release>21</maven.compiler.release>` — was 8.

### 7.3 QUICKSTART — build & run on localhost

**A. Build the WAR** (canonical env from §2):
```bash
cd /c/eig/xwiz-code
export JAVA_HOME="/c/Program Files/Freeplane/runtime"
export PATH="/c/eig/tools/apache-maven-3.9.9/bin:$PATH"
export MAVEN_OPTS="-Djavax.net.ssl.trustStoreType=Windows-ROOT"
mvn -U clean package            # -> target/XWizard.war
```

**B. Run the desktop app** (no container needed):
```bash
java -Xss10M -cp "target/XWizard/WEB-INF/classes;target/XWizard/WEB-INF/lib/*" veryFastPDF.VFPStarter
# or just run: Start-VFP.bat
```

**C. Run the web app on Tomcat 11**:
```bash
# One-time: fetch Tomcat 11 (through the corporate proxy) into the tools dir
cd /c/eig/tools
curl -sSL --proxy http://127.0.0.1:3129 -o tomcat11.zip \
  https://dlcdn.apache.org/tomcat/tomcat-11/v11.0.24/bin/apache-tomcat-11.0.24.zip
unzip -q tomcat11.zip                       # -> apache-tomcat-11.0.24/

# One-time: free the shutdown port (see note below)
#   edit conf/server.xml:  <Server port="8005" ...>  ->  <Server port="8006" ...>

# Deploy the freshly built WAR
cp /c/eig/xwiz-code/target/XWizard.war apache-tomcat-11.0.24/webapps/

# Start Tomcat with the JDK 21 runtime
export JAVA_HOME="C:\\Program Files\\Freeplane\\runtime"
export CATALINA_HOME="C:\\eig\\tools\\apache-tomcat-11.0.24"
cd apache-tomcat-11.0.24/bin
cmd //c catalina.bat run                    # foreground; Ctrl-C (or bin/shutdown.bat) to stop
```
Then open **http://localhost:8080/XWizard/Wizz?help**.

> **Port note:** on this machine the default Tomcat shutdown port **8005** is held
> by the Windows `System` process (PID 4), which makes `catalina.bat` abort right
> after startup with `BindException: Address already in use`. Fix once by changing
> `<Server port="8005" …>` to `8006` in `conf/server.xml`. HTTP `8080` is free.

### 7.4 What was verified (2026‑08‑10, Tomcat 11.0.24 + JDK 21)
| Request | Result |
|---|---|
| WAR deploy | ✅ clean (only a benign DBCP2 `maxActive`→`maxTotal` warning) |
| `GET /XWizard/Wizz?help` | ✅ 200, ~202 KB real HTML (title *"How wizarre!"*, CodeMirror/XWizard) |
| `GET /XWizard/Wizz?impressum=true` | ✅ 200, ~69 KB |
| bare `GET /XWizard/Wizz` | 200, 0 bytes (welcome page needs the DB) |
| `POST` a `dot:` script | 500 — **only** because no MySQL (NPE in `SQLQueries.retrieveCachedSVG`, `connect==null`), **not** a Jakarta issue |

The 500's stack trace flows through `jakarta.servlet.http.HttpServlet.service`, i.e.
the servlet migration is live and correct; the failure is a missing database.

### 7.5 To exercise DB‑backed rendering (optional)
The app caches rendered SVGs in MySQL, so full end‑to‑end conversion needs a DB:
1. Run MySQL on `localhost:3306` with a `xwizard` schema. The JNDI resource
   `jdbc/xwizard` and the bundled `mysql-connector-j` are already wired; credentials
   are in `WebContent/META-INF/context.xml`.
2. External tools: **Graphviz** (`dot`) and **MiKTeX** (`pdflatex`) are already on
   PATH; some renderers also need `pdf2svg`/`pdftk` (paths configured via
   `sharedDirectory/externalFilePaths.dat`).

---

## 8. Known risks / open items
- **Web runtime verified on Tomcat 11** for non‑DB pages (see §7.4). DB‑backed
  rendering (the SVG cache) still needs a running MySQL to exercise end‑to‑end.
- **Secret in repo:** `WebContent/META-INF/context.xml` contains a hard‑coded DB
  password. Consider moving it to a container‑managed resource / env var and
  rotating it. (Pre‑existing; not changed in Steps 0–3.)
- **`mvn-repo` artifacts** (`leores` 1.0, `miglayout` 4.0) are hand‑installed;
  document their provenance if the build must be reproduced elsewhere.
- **MigLayout** stuck at 4.0 — a proper fix (locating the cyclic size constraint
  in `VFPWindow`) would unblock modern MigLayout, but isn't required.
- **Joda‑Time → `java.time`** is an optional future cleanup (only `DateTime` used
  in `Watchdog`/`StaticMethods`).

---

## 9. Quick command reference
```bash
# Env (run once per shell)
cd /c/eig/xwiz-code
export JAVA_HOME="/c/Program Files/Freeplane/runtime"
export PATH="/c/eig/tools/apache-maven-3.9.9/bin:$PATH"
export MAVEN_OPTS="-Djavax.net.ssl.trustStoreType=Windows-ROOT"

# Build the WAR
mvn -U clean package                     # -> target/XWizard.war

# Run desktop app
java -Xss10M -cp "target/XWizard/WEB-INF/classes;target/XWizard/WEB-INF/lib/*" veryFastPDF.VFPStarter

# Install a non-Central jar into the in-project repo (pattern used for leores/miglayout)
mvn org.apache.maven.plugins:maven-install-plugin:3.1.1:install-file \
  -Dfile=<path-to>.jar -DgroupId=<g> -DartifactId=<a> -Dversion=<v> \
  -Dpackaging=jar -DlocalRepositoryPath=mvn-repo

# Deploy & run on Tomcat 11 (see §7.3 for one-time download + shutdown-port fix)
cp target/XWizard.war /c/eig/tools/apache-tomcat-11.0.24/webapps/
export JAVA_HOME="C:\\Program Files\\Freeplane\\runtime"
export CATALINA_HOME="C:\\eig\\tools\\apache-tomcat-11.0.24"
cmd //c /c/eig/tools/apache-tomcat-11.0.24/bin/catalina.bat run
# -> http://localhost:8080/XWizard/Wizz?help
```
