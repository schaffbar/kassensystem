# Schaffbar Timemachine

Server, um die ESPs zu managen.
Besteht aus dem Handler mit Admin Interface (Port 5000) <http://localhost:5000/admin>

## Lokal entwickeln

```bash
uv run flask run --host 0.0.0.0 --debug --port 5000
```

## Docker

```bash
sudo docker compose up --build
```
