# pw45s — Bonsai Admin API

API REST desenvolvida com Spring Boot para gerenciamento de pedidos de uma loja de bonsais, com autenticação JWT, armazenamento de arquivos via MinIO, busca com Elasticsearch e observabilidade via Prometheus + Grafana.

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

| Serviço       | URL                                        | Credenciais            |
|---------------|--------------------------------------------|------------------------|
| API           | http://localhost:8080                      | —                      |
| Swagger UI    | http://localhost:8080/swagger-ui/index.html | —                     |
| MinIO Console | http://localhost:9001                      | `minioadmin/minioadmin` |
| MailHog       | http://localhost:8025                      | —                      |
| Kibana        | http://localhost:5601                      | —                      |
| Grafana       | http://localhost:3000                      | `admin/admin`          |
| Prometheus    | http://localhost:9090                      | —                      |
| Elasticsearch | http://localhost:9200                      | —                      |

## Usuários seed

O seed cria dois usuários internos e dois clientes fictícios (tabela `customers`):

| Email                      | Senha    | Role     | Ativo |
|----------------------------|----------|----------|-------|
| admin@utfpr.edu.br         | `admin1` | ADMIN    | sim   |
| operador@utfpr.edu.br      | `admin1` | OPERATOR | sim   |

**Clientes (customers):** `joao.silva@email.com` e `maria.santos@email.com` — cada um com um pedido de seed.

> Usuários internos (ADMIN/OPERATOR) e clientes são entidades separadas. Clientes vivem na tabela `customers` e representam dados replicados de um sistema externo.

### Login rápido

```
POST /auth/login
Content-Type: application/json

{
  "email": "admin@utfpr.edu.br",
  "password": "admin1"
}
```

## Endpoints

### Autenticação
| Método | Path         | Auth | Descrição            |
|--------|--------------|------|----------------------|
| POST   | /auth/login  | —    | Retorna JWT (60 min) |

### Usuários (usuários internos)
| Método | Path                 | Auth  | Descrição                        |
|--------|----------------------|-------|----------------------------------|
| POST   | /users/register      | —     | Registra usuário (inativo)       |
| PUT    | /users/{id}/activate | ADMIN | Ativa o usuário                  |
| PUT    | /users/{id}/roles    | ADMIN | Substitui roles do usuário       |
| GET    | /users               | ADMIN | Lista usuários (paginado)        |
| GET    | /users/{id}          | ADMIN | Busca usuário por ID             |

### Pedidos
| Método | Path                    | Auth           | Descrição                                              |
|--------|-------------------------|----------------|--------------------------------------------------------|
| GET    | /orders                 | ADMIN/OPERATOR | Lista pedidos com filtros opcionais (paginado)         |
| GET    | /orders/{id}            | ADMIN/OPERATOR | Busca pedido por ID                                    |
| PUT    | /orders/{id}/status     | ADMIN/OPERATOR | Atualiza status (EM_TRANSPORTE exige nota fiscal PDF)  |
| GET    | /orders/{id}/history    | ADMIN/OPERATOR | Histórico de mudanças de status                        |
| GET    | /orders/summary         | ADMIN/OPERATOR | Contagem de pedidos por status                         |
| GET    | /orders/customers       | ADMIN/OPERATOR | IDs distintos de clientes com pedidos (paginado)       |

#### Status de pedido
`AGUARDANDO_PAGAMENTO` → `PAGO` → `EM_TRANSPORTE` → `ENTREGUE` / `CANCELADO`

> A transição para `EM_TRANSPORTE` exige que haja ao menos uma nota fiscal PDF anexada ao pedido.

### Anexos
| Método | Path                           | Auth           | Descrição                      |
|--------|--------------------------------|----------------|--------------------------------|
| GET    | /orders/{orderId}/attachments  | ADMIN/OPERATOR | Lista anexos de um pedido      |
| POST   | /orders/{orderId}/attachments  | ADMIN/OPERATOR | Upload de arquivo (max 10 MB)  |
| GET    | /attachments/{id}/download     | ADMIN/OPERATOR | Download do arquivo            |

#### Tipos de anexo (`type`)
- `NOTA_FISCAL` — obrigatoriamente PDF
- `OUTRO` — qualquer tipo

## Postman

As collections ficam em [`/postman`](../postman/):

| Arquivo                                  | Descrição                                                       |
|------------------------------------------|-----------------------------------------------------------------|
| `bonsai-admin-todos-endpoints.json`      | Um request por endpoint, organizado por domínio                 |
| `bonsai-admin-fluxo-completo.json`       | 15 requests em sequência com scripts que encadeiam variáveis automaticamente |

O fluxo completo cobre: login → criar operador → listar pedidos → upload de nota fiscal → mudança de status → histórico → download.

> Para usar: **Import** no Postman → selecione o arquivo → ajuste a variável `baseUrl` se necessário (default `http://localhost:8080`).

## Rodar os testes

```bash
./mvnw verify
```

Executa os testes e valida cobertura mínima de 80% (JaCoCo). O relatório HTML fica em `target/site/jacoco/index.html`.

## Variáveis de ambiente (produção)

| Variável             | Descrição                    | Default (dev)                            |
|----------------------|------------------------------|------------------------------------------|
| `JWT_SECRET`         | Segredo para assinar tokens  | **obrigatório**                          |
| `DB_URL`             | JDBC URL do PostgreSQL       | `jdbc:postgresql://localhost:5431/pw45s` |
| `DB_USERNAME`        | Usuário do banco             | `pw45s`                                  |
| `DB_PASSWORD`        | Senha do banco               | `pw45s`                                  |
| `MINIO_URL`          | Endpoint do MinIO            | `http://localhost:9000`                  |
| `MINIO_ACCESS_KEY`   | Access key do MinIO          | `minioadmin`                             |
| `MINIO_SECRET_KEY`   | Secret key do MinIO          | `minioadmin`                             |
| `MINIO_BUCKET`       | Nome do bucket               | `pw45s`                                  |
| `ELASTICSEARCH_URIS` | URI do Elasticsearch         | `http://localhost:9200`                  |
| `MAIL_HOST`          | Host SMTP                    | `localhost`                              |
| `MAIL_PORT`          | Porta SMTP                   | `1025`                                   |

## Estrutura de módulos

```
src/main/java/.../
├── auth/          # Login e geração de JWT
├── users/         # Usuários internos (ADMIN, OPERATOR)
├── customers/     # Clientes externos replicados (tabela customers)
├── orders/        # Pedidos e histórico de status
├── attachments/   # Upload/download via MinIO
├── audit/         # Log de auditoria no Elasticsearch
├── notifications/ # E-mails de mudança de status (MailHog em dev)
└── shared/        # Base classes, segurança, config, exceções
```
