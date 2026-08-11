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
