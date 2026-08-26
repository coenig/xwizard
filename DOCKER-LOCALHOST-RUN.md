# XWizard Docker Localhost Quick Guide

This is the shortest path to run XWizard on localhost using Docker.

## Prerequisites

- Docker Engine
- Docker Compose plugin (`docker compose`)

## Start

From project root:

```bash
mvn -U clean package
docker compose up -d --build
docker compose ps
```

When `db` is `healthy` and `app` is `Up`, open:

- http://localhost:8080/XWizard/Wizz?help
- http://localhost:8080/XWizard/Wizz?impressum

## Stop

```bash
docker compose down
```

## Reset all data (including MySQL volume)

```bash
docker compose down -v
```

## Seed data (first-init only)

On the **first** startup with an empty database volume, MySQL runs the init scripts mounted into `/docker-entrypoint-initdb.d/` in lexical order:

1. `01-init.sql` (from `docker/init.sql`) — creates the schema
2. `02-seed.sql` (from `docker/seed/xwizard_seed.sql`) — loads the archived xwizard data (21 tables)

This runs **once**. As long as the `db-data` volume persists, later data you
create is kept and never overwritten. To wipe and re-seed from scratch:

```bash
docker compose down -v && docker compose up -d
```

The seed is derived from `archived_data/SQL_DUMP_Ende_WS1516/xwizard_data_20160303.sql`
(uppercased table names + `utf8mb4`). Regenerate the seed only if that archive changes.

## Useful checks

App and DB logs:

```bash
docker compose logs -f app db
```

Quick smoke tests:

```bash
curl -s "http://localhost:8080/XWizard/Wizz?help" | wc -c
curl -s "http://localhost:8080/XWizard/Wizz?impressum" | wc -c
```

Render test via POST (Graphviz):

```bash
curl -sS -X POST "http://localhost:8080/XWizard/Wizz" \
  --data-urlencode "mainTextArea=dot:\ndigraph G { A -> B; B -> C; }" \
  > /tmp/xwiz-dot.html
```

Note: the POST form field must be `mainTextArea`.
