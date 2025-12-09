# Deployment

## Schaffbar

1. Join `Schaffbar` WLAN

2. Connect with server

```bash
ssh <odoo_server>
(enter password)
```

3. Pull updates

```bash
cd kassensystem/
git pull
```

4. Create `.env` file

```
# Database configuration
POSTGRES_USER=<>
POSTGRES_PASSWORD=<>
POSTGRES_DB=<>

# Backend configuration
# BACKEND_ENV=dev
# BACKEND_ENV=prod

# Frontend configuration
# FRONTEND_ENV=development
# FRONTEND_ENV=production
```

5. Run docker compose

```bash
sudo su -
(enter password)
cd /home/schaffbar/kassensystem/schaffbar-backend
docker compose up -d --build
```

## Local (only Docker required)

Before proceeding, ensure that Docker and Docker Compose are installed on your system. These tools are required to build and run the application in isolated containers. You can download Docker from [https://www.docker.com/get-started](https://www.docker.com/get-started). Verify installation by running:

```bash
docker --version
docker compose --version
```

### 1. Clone the repository or download the source code

You can obtain the code by cloning the repository and switching to the desired branch, or by downloading it directly from the GitHub page.

- To clone and switch branch:

```bash
git clone https://github.com/schaffbar/kassensystem.git
cd kassensystem
git checkout piotr
```

- Alternatively, visit [https://github.com/schaffbar/kassensystem](https://github.com/schaffbar/kassensystem) and use the "Download ZIP" option if you prefer not to use Git.

### 2. Create `.env` file (in `schaffbar-backend` folder) with postgres credentials

```bash
cd schaffbar-backend
```

- `.env` file content

```bash
POSTGRES_USER=your_postgres_user
POSTGRES_PASSWORD=your_postgres_password
POSTGRES_DB=schaffbardb
```

### 3. Start all services with Docker Compose

```bash
docker compose up --build -d
```

- The `--build` parameter is required only the first time you run this command, or whenever you change the source code and need to rebuild the images. Subsequent runs can omit `--build` unless a rebuild is necessary.

- To start only two services (for example, `postgres` and `backend`), run:

```bash
docker compose up -d postgres backend
```

- Replace the service names with the ones you want to start, as defined in `docker-compose.yml`.

### 4. Stop all running containers

```bash
docker compose down
```
