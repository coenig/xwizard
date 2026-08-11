#!/usr/bin/env bash
set -e

cd "$CATALINA_HOME"
mkdir -p "$CATALINA_HOME/logging" "$CATALINA_HOME/workingDir"

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