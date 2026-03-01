# Schaffbar Timemachine

Server, um die ESPs zu managen.
Besteht aus dem Handler (Port 5000) und dem Admin-Interface (Port 5001, <http://localhost:5001/admin>).

## Lokal entwickeln

```bash
# admin ui starten
uv run flask run --host 0.0.0.0 --debug --port 5001
# ESP server starten
uv run app.py
```

## Docker

```bash
sudo docker compose up --build
```
