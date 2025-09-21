# Deployment

## Schaffbar

Deployment instructions for the Schaffbar server will be provided in a future update.

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
