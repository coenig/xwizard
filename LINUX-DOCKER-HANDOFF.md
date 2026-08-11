# XWizard Linux + Docker Handoff

This file is the continuation guide for moving XWizard web mode to a Linux machine and finishing Docker packaging there.

For a short command-only startup guide, see `DOCKER-LOCALHOST-RUN.md`.

## Current status

These code changes are already implemented in this repository:

- Linux-safe external command execution via `Runtime.exec(String[])` was added in `src/veryFastPDF/web/ConvenienceMethods.java`.
- The Linux-sensitive quoted command strings were converted to argv-style execution in:
  - `src/veryFastPDF/pdfProcessors/LaTeXPDF.java`
  - `src/veryFastPDF/pdfProcessors/PDFProcessor.java`
- Known Windows-only path joins were replaced with platform-safe separators in:
  - `src/veryFastPDF/pdfProcessors/GraphViz.java`
  - `src/veryFastPDF/pdfProcessors/LaTeXPDF.java`
  - `src/veryFastPDF/algorithms/bdd/BDD.java`
- Database access was hardened so web rendering can proceed without MySQL in:
  - `src/mainServlet/SQLQueries.java`
- Initial Docker packaging files were added:
  - `Dockerfile`
  - `.dockerignore`
  - `docker/entrypoint.sh`
  - `docker-compose.yml`
  - `docker/init.sql`

Validation on this Linux host is complete:

- `mvn clean package` succeeds with Java 21 target.
- Docker image builds successfully.
- `docker compose up -d --build` starts both app and MySQL containers.
- Smoke tests pass:
  - `GET /XWizard/Wizz?help` (~202729 bytes)
  - `GET /XWizard/Wizz?impressum` (~69094 bytes)
- Graphviz and LaTeX web-mode flows were exercised via POST and returned normal HTML pages containing generated SVG content.
- DB writes are confirmed (rows created in `SCRIPTS` and `SESSION_DATA`).

The immediate goal is now operational handoff and repeatable local startup on Linux.

## Scope

This handoff covers:

- XWizard web mode only
- Linux runtime support for Graphviz, pdflatex, pdftk, and pdf2svg
- Docker packaging for repeatable deployment

This handoff does not cover:

- Desktop mode portability
- Production hardening (secrets management, TLS, reverse proxy, backup/restore)

## Recommended Linux target

Use a Debian-based Linux machine or VM, not Alpine.

Recommended baseline:

- JDK 21
- Maven 3.9+
- Docker Engine with Compose
- Internet access for apt and Maven dependencies

## Required Linux packages

Install these packages on the Linux machine for native validation outside Docker:

```bash
sudo apt-get update
sudo apt-get install -y \
  graphviz \
  pdf2svg \
  pdftk-java \
  ghostscript \
  texlive-latex-base \
  texlive-latex-recommended \
  texlive-latex-extra \
  texlive-pictures \
  texlive-fonts-recommended \
  lmodern
```

If specific LaTeX scripts fail because of missing packages, add the missing TeX packages incrementally.

## Files that matter most

- `src/veryFastPDF/web/ConvenienceMethods.java`
- `src/veryFastPDF/pdfProcessors/LaTeXPDF.java`
- `src/veryFastPDF/pdfProcessors/PDFProcessor.java`
- `src/veryFastPDF/pdfProcessors/GraphViz.java`
- `src/mainServlet/SQLQueries.java`
- `Dockerfile`
- `docker/entrypoint.sh`
- `docker-compose.yml`

## Step 1: move the repo to Linux

Copy or clone the repository to the Linux machine.

Expected project root:

```bash
cd /path/to/xwiz-code
```

## Step 2: verify tool availability

Run:

```bash
java -version
mvn -version
dot -V
pdf2svg --help | head -1
pdftk --version | head -1
pdflatex --version | head -1
docker --version
docker compose version
```

Expected result:

- Java is version 21
- Maven runs normally
- `dot`, `pdf2svg`, `pdftk`, and `pdflatex` are on PATH
- Docker and Compose are available

## Step 3: build the WAR on Linux

Run from the repo root:

```bash
mvn -U clean package
```

Expected artifact:

```bash
target/XWizard.war
```

If this fails, stop here and fix the Java/Maven environment before touching Docker.

## Step 4: review the Docker wiring

The current Docker setup does the following:

- Uses Tomcat 11 + JDK 21
- Installs Graphviz, pdf2svg, pdftk-java, and TeX packages
- Copies `target/XWizard.war` into Tomcat
- Generates `logging/conf.txt` at container startup with Linux paths
- Creates writable `logging` and `workingDir` directories

The generated `logging/conf.txt` currently contains these entries in required order:

1. `/usr/bin/dot`
2. `/usr/bin/pdflatex`
3. `/usr/bin/pdf2svg`
4. `/usr/bin/python3`
5. `/usr/bin/true`
6. `/usr/bin/pdftk`

That order matches how `WebLink.loadPaths()` reads the config.

## Step 5: build the Docker image

Run:

```bash
docker build -t xwizard:local .
```

If the build fails because a package name differs on your Linux base, adjust the package list in `Dockerfile` and rebuild.

## Step 6: run the containers

Run either of these:

```bash
docker run --rm -p 8080:8080 xwizard:local
```

or

```bash
docker compose up -d --build
```

The current `docker-compose.yml` starts:

- `app` (Tomcat + XWizard WAR)
- `db` (MySQL 8.4)

To follow logs:

```bash
docker compose logs -f app db
```

## Step 7: smoke-test the web app

In another terminal:

```bash
curl -s http://localhost:8080/XWizard/Wizz?help | wc -c
curl -s http://localhost:8080/XWizard/Wizz?impressum | wc -c
```

Expected rough sizes based on earlier Windows/Tomcat validation:

- `?help` around 202 KB
- `?impressum` around 69 KB

If those load, the servlet deployment is basically healthy.

## Step 8: validate rendering paths

You should validate both major external-tool flows:

### Graphviz flow

This needs to succeed:

- XWizard accepts a graph-oriented script
- Graphviz renders the PDF
- The PDF is turned into SVG
- The response returns SVG content instead of an error page

### LaTeX flow

This needs to succeed:

- `pdflatex` generates the PDF
- `pdftk` splits pages if needed
- `pdf2svg` converts output to SVG
- The response returns SVG content

Use either the browser UI or scripted POST requests, depending on what is easier on the Linux machine.

For scripted POST checks, use the form field name `mainTextArea`.

## Step 9: inspect logs if rendering fails

Check:

```bash
docker logs <container-id>
```

and inside the container if needed:

```bash
docker run --rm -it xwizard:local bash
```

Then test tools directly:

```bash
dot -V
pdf2svg --help | head -1
pdftk --version | head -1
pdflatex --version | head -1
cat /usr/local/tomcat/logging/conf.txt
ls -la /usr/local/tomcat/workingDir
```

## Known open items

### 1. Wider path sweep

The known Windows path issues on the main rendering path were fixed, but there may still be older desktop-only or low-traffic paths elsewhere in the codebase.

On Linux, grep for remaining Windows-style path concatenations:

```bash
grep -R "\\\\" src
```

Review hits carefully before changing them.

### 2. TeX package drift

Some specific scripts may still require extra TeX packages beyond the current image.

## What to do first on Linux

Use this exact order:

1. Install JDK 21, Maven, Docker, and the external render tools.
2. Run `mvn -U clean package`.
3. Run `docker compose up -d --build`.
4. Wait for containers to be healthy (`docker compose ps`).
5. Verify `?help` and `?impressum`.
6. Verify one Graphviz render.
7. Verify one LaTeX render.
8. Confirm DB writes (optional but recommended).

## If you resume work with Copilot later

Start from this statement:

> The Linux portability code and initial Docker files are already in the repo. Please continue from `LINUX-DOCKER-HANDOFF.md`, validate on this Linux machine, and then add MySQL containerization only if the app-only container works.
