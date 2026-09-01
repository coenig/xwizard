#!/usr/bin/env bash
set -e

cd "$CATALINA_HOME"
mkdir -p "$CATALINA_HOME/logging" "$CATALINA_HOME/workingDir"

# Cap the JVM heap well below the container's mem_limit (see docker-compose.prod.yml)
# so it leaves room for the native pdflatex/dot/pdftk/pdf2svg child processes that
# run in the same container but outside the JVM heap. ExitOnOutOfMemoryError makes
# the JVM die (rather than limp along) on OOM, so Docker's healthcheck/restart or
# autoheal can recover it instead of the process hanging in a broken state.
export CATALINA_OPTS="${CATALINA_OPTS:--Xms512m -Xmx1024m -XX:MaxMetaspaceSize=256m -XX:+ExitOnOutOfMemoryError}"

# Periodically delete stale generated render output so workingDir doesn't grow
# unbounded (every LaTeX/Graphviz render writes PDFs/SVGs here that are never
# otherwise cleaned up).
WORKINGDIR_MAX_AGE_MIN="${WORKINGDIR_MAX_AGE_MIN:-720}"
WORKINGDIR_CLEAN_INTERVAL_SEC="${WORKINGDIR_CLEAN_INTERVAL_SEC:-3600}"
(
	while true; do
		sleep "$WORKINGDIR_CLEAN_INTERVAL_SEC"
		find "$CATALINA_HOME/workingDir" -mindepth 1 -mmin "+$WORKINGDIR_MAX_AGE_MIN" -delete 2>/dev/null || true
	done
) &

DB_HOST="${DB_HOST:-db}"
DB_PORT="${DB_PORT:-3306}"
DB_NAME="${DB_NAME:-xwizard}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-xwizard}"
DB_WAIT_RETRIES="${DB_WAIT_RETRIES:-90}"
DB_WAIT_SLEEP="${DB_WAIT_SLEEP:-1}"

wait_for_db() {
	if [[ "${DB_WAIT_RETRIES}" -le 0 ]]; then
		return
	fi

	echo "Waiting for database ${DB_HOST}:${DB_PORT}..."
	for ((i = 1; i <= DB_WAIT_RETRIES; i++)); do
		if (echo > "/dev/tcp/${DB_HOST}/${DB_PORT}") >/dev/null 2>&1; then
			echo "Database is reachable."
			return
		fi
		sleep "${DB_WAIT_SLEEP}"
	done

	echo "Database did not become reachable after ${DB_WAIT_RETRIES} attempts; starting Tomcat anyway."
}

mkdir -p "$CATALINA_HOME/conf/Catalina/localhost"
cat > "$CATALINA_HOME/conf/Catalina/localhost/XWizard.xml" <<EOF
<Context>
	<Resource name="jdbc/xwizard" auth="Container" type="javax.sql.DataSource"
			  username="${DB_USER}" password="${DB_PASSWORD}" driverClassName="com.mysql.cj.jdbc.Driver"
			  url="jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}" maxTotal="15" maxIdle="3" />
	<Resources>
		<PostResources className="org.apache.catalina.webresources.DirResourceSet"
						base="$CATALINA_HOME/workingDir" internalPath="/" webAppMount="/workingDir" />
	</Resources>
</Context>
EOF

cat > "$CATALINA_HOME/logging/conf.txt" <<'EOF'
/usr/bin/dot
/usr/bin/pdflatex
/usr/bin/pdf2svg
/usr/bin/python3
/usr/bin/true
/usr/bin/pdftk
EOF

wait_for_db

exec catalina.sh run