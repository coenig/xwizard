FROM tomcat:11.0-jdk21-temurin

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y --no-install-recommends \
      graphviz \
      pdf2svg \
      pdftk-java \
      ghostscript \
      texlive-latex-base \
      texlive-latex-recommended \
      texlive-latex-extra \
      texlive-pictures \
      texlive-fonts-recommended \
      lmodern \
    && rm -rf /var/lib/apt/lists/*

RUN rm -rf "$CATALINA_HOME"/webapps/*

COPY target/XWizard.war "$CATALINA_HOME/webapps/XWizard.war"
COPY docker/entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh \
    && mkdir -p "$CATALINA_HOME/logging" "$CATALINA_HOME/workingDir"

EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]