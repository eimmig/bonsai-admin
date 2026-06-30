# pw45s

API REST desenvolvida com Spring Boot para gerenciamento de pedidos, com autenticação JWT, armazenamento de arquivos via MinIO, busca com Elasticsearch e observabilidade via Prometheus + Grafana.

## Requisitos

- Java 25 ([Temurin](https://adoptium.net/))
- Docker + Docker Compose
- Maven (ou use o wrapper `./mvnw`)

## Configuração local

Crie o arquivo `src/main/resources/application-local.yml` (já ignorado pelo git):

```yaml
security:
  jwt:
    secret: qualquer-string-longa-e-aleatoria-aqui
```

> Esse arquivo define o `JWT_SECRET` necessário para subir a aplicação. Sem ele a inicialização falha.

## Subir a infraestrutura

```bash
docker compose up -d
```

Para parar:

```bash
docker compose down
```

## Rodar a aplicação

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Ou pelo IntelliJ: Run Configuration da `Pw45sApplication` → **Active profiles:** `local`

## Serviços disponíveis

| Serviço | URL | Credenciais |
|---|---|---|
| API | http://localhost:8080 | — |
| Swagger UI | http://localhost:8080/swagger-ui/index.html | — |
| MinIO Console | http://localhost:9001 | `minioadmin` / `minioadmin` |
| MailHog | http://localhost:8025 | — |
| Kibana | http://localhost:5601 | — |
| Grafana | http://localhost:3000 | `admin` / `admin` |
| Prometheus | http://localhost:9090 | — |
| Elasticsearch | http://localhost:9200 | — |

## Usuário padrão (seed)

```
POST /auth/login
Content-Type: application/json

{
  "email": "admin@utfpr.edu.br",
  "password": "admin"
}
```

> Troque a senha do admin após o primeiro login em produção.

## Rodar os testes

```bash
./mvnw verify
```

Executa os testes e valida cobertura mínima de 80% (JaCoCo). O relatório HTML fica em `target/site/jacoco/index.html`.

## Variáveis de ambiente (produção)

| Variável | Descrição | Default (dev)                            |
|---|---|------------------------------------------|
| `JWT_SECRET` | Segredo para assinar tokens JWT | **obrigatório**                          |
| `DB_URL` | JDBC URL do PostgreSQL | `jdbc:postgresql://localhost:5431/pw45s` |
| `DB_USERNAME` | Usuário do banco | `pw45s`                                  |
| `DB_PASSWORD` | Senha do banco | `pw45s`                                  |
| `MINIO_URL` | Endpoint do MinIO | `http://localhost:9000`                  |
| `MINIO_ACCESS_KEY` | Access key do MinIO | `minioadmin`                             |
| `MINIO_SECRET_KEY` | Secret key do MinIO | `minioadmin`                             |
| `MINIO_BUCKET` | Nome do bucket | `pw45s`                                  |
| `ELASTICSEARCH_URIS` | URI do Elasticsearch | `http://localhost:9200`                  |
| `MAIL_HOST` | Host SMTP | `localhost`                              |
| `MAIL_PORT` | Porta SMTP | `1025`                                   |
