# Going live on www.xwizard.de with HTTPS (IONOS)

This is a step-by-step checklist for switching the domain from the current
IONOS "Weiterleitung" (forwarding) to the real, SSL-secured Docker stack
(`docker-compose.prod.yml` + Caddy), while keeping old printed links like

```
http://www.xwizard.de:8080/XWizard/Wizz?&template=ID-16336
```

working. No prior DNS/server-admin experience assumed — follow it in order.

## The short version

Right now, IONOS's "Weiterleitung" redirects the domain straight to
`http://217.154.21.201:8080/XWizard/Wizz` — this only works for the bare
domain, not for the exact `www.xwizard.de:8080` URL printed in the book. To
make the printed URL itself work (and be secure), the domain needs to point
*directly* at the server via real DNS, and the server needs to run the
`docker-compose.prod.yml` stack (Tomcat + MySQL + Caddy), which:
- Serves the site securely at `https://www.xwizard.de/...` (Caddy gets a free
  Let's Encrypt certificate automatically).
- Still answers on port 8080 for old links, but only to redirect them to the
  secure `https://` version (see `docker/Caddyfile`).

## Step 1 — Free up port 8080 on the server

If the app is currently running via the plain `docker-compose.yml` (no
Caddy, no SSL), it's likely bound directly to port 8080 on the server. The
new setup needs port 8080 for the Caddy *redirect* instead, so the old one
must be stopped first or you'll get a "port already in use" error.

SSH into the server (`217.154.21.201`) and run, from the repo checkout there:

```bash
docker compose -f docker-compose.yml down
```

## Step 2 — Replace the IONOS "Weiterleitung" with real DNS records

The "Weiterleitung" feature only forwards browser requests for the bare
domain on ports 80/443 — it cannot make `www.xwizard.de:8080` resolve at
all, because that requires the domain to have an actual IP address (DNS
"A record"), not just a redirect rule.

In the IONOS control panel (`login.ionos.de` → **Domains & SSL** → your
domain):

1. You'll currently find a **"Weiterleitung"** screen with **Typ: "Beliebige
   URL"**, target `http://217.154.21.201:8080/XWizard/Wizz`, and
   "Weiterleitungsart" set to HTTP-Weiterleitung (or Frame-Weiterleitung —
   don't use that one either; both options on this screen are still just a
   forward, not real DNS). **Leave/ignore this screen for now** — don't just
   toggle its options, the whole feature has to go.
2. Go back to the domain's overview page and look for a separate **"DNS"**
   section (sometimes called "DNS-Einstellungen" / "Nameserver" / "Records
   verwalten" — distinct from the Weiterleitung dialog above).
3. In that DNS section, add these records (look for an "A record" /
   "A-Eintrag" option):

   | Type | Host / Name | Value (Points to) |
   |------|-------------|--------------------|
   | A    | `@` (or blank, = `xwizard.de`) | `217.154.21.201` |
   | A    | `www`                          | `217.154.21.201` |

4. Save, then go back to the Weiterleitung screen from step 1 and **delete
   it** — a domain can't be both DNS-managed and forwarding-managed at
   IONOS at the same time, so the A-records above won't take effect (or
   IONOS won't let you save them) until the forwarding is removed.
5. DNS changes at IONOS are usually live within minutes but can take up to
   ~24h to fully propagate everywhere.

**How to check it worked:** on any computer, run
`nslookup www.xwizard.de` (or use an online tool like
[dnschecker.org](https://dnschecker.org)) and confirm it now returns
`217.154.21.201` — not an IONOS parking/forwarding IP.

## Step 3 — Open the firewall on the server

Make sure ports **80**, **443**, and **8080** (TCP) are reachable from the
internet on `217.154.21.201`. Where this is configured depends on the
hosting provider:
- If it's a plain Linux box with `ufw`: `sudo ufw allow 80,443,8080/tcp`.
- If it's a cloud VM (e.g. a provider's web console): open those ports in
  the "firewall" / "security group" settings for the instance.

Port 80 in particular is required for Caddy to obtain the Let's Encrypt
certificate automatically — if it's blocked, HTTPS setup will fail silently.

## Step 4 — Configure and start the production stack

On the server, in the repo checkout:

```bash
cp .env.example .env
```

Edit `.env` and set at least:

```
SITE_ADDRESS=www.xwizard.de
TLS_EMAIL=<a real email you check — Let's Encrypt sends renewal warnings here>
DB_PASSWORD=<pick a strong password>
MYSQL_ROOT_PASSWORD=<pick a different strong password>
```

Then start everything:

```bash
docker compose -f docker-compose.prod.yml up -d --build
```

The first start takes a little while (image build + Caddy requesting the
certificate). Watch progress with:

```bash
docker compose -f docker-compose.prod.yml logs -f caddy
```

## Step 5 — Verify

Once DNS has propagated and the stack is up, test from your own machine
(not the server):

- `https://www.xwizard.de/XWizard/Wizz?help` → loads the app securely
  (padlock in the browser, no certificate warning).
- `http://www.xwizard.de:8080/XWizard/Wizz?&template=ID-16336` → the exact
  printed legacy link — should redirect (you'll briefly see the URL change
  in the address bar) to `https://www.xwizard.de/XWizard/Wizz?&template=ID-16336`
  and load correctly.

If the first bullet fails but the domain resolves correctly (Step 2
check), see **Troubleshooting** below.

## Troubleshooting

- **Certificate/HTTPS doesn't come up, Caddy logs show errors obtaining a
  certificate:** almost always DNS hasn't propagated yet, or port 80 is
  blocked by a firewall — Let's Encrypt validates ownership over port 80
  before issuing the certificate. Re-check Steps 2 and 3.
- **"port is already allocated" when starting the prod stack:** something
  is still bound to 80/443/8080 on the server — usually the old
  `docker-compose.yml` stack (Step 1) or another web server. Run
  `docker ps` and `sudo ss -tlnp` to see what's listening.
- **`http://www.xwizard.de:8080/...` still times out:** DNS for
  `www.xwizard.de` hasn't propagated to real A records yet (it's probably
  still resolving to an IONOS forwarding IP) — re-check with
  `nslookup www.xwizard.de`.
