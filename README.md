# Tea Shop API 🍵

API RESTful desenvolvida em Spring Boot para gerenciar o backend de uma loja de chás. O sistema fornece recursos para gerenciamento de catálogo de produtos, clientes, endereços e processamento de pedidos (com validação e baixa de estoque).

---

## 🚀 Tecnologias

* **Linguagem:** Java 21
* **Framework:** Spring Boot 4.0.7
* **Mapeamento Objeto-Relacional (ORM):** Spring Data JPA / Hibernate
* **Banco de Dados Padrão (Dev):** H2 Database (em memória, sem necessidade de instalação externa)
* **Banco de Dados (Produção/Opcional):** PostgreSQL
* **Containerização:** Docker & Docker Compose
* **Validação:** Bean Validation (`jakarta.validation`)
* **Documentação Interativa:** SpringDoc OpenAPI 3 (Swagger UI)
* **Monitoramento:** Spring Boot Actuator
* **Produtividade:** Lombok

---

## 🐳 Como Executar com Docker (Recomendado)

A melhor forma de rodar sem precisar se preocupar com versões do Java (17 vs 21) ou ferramentas instaladas na sua máquina local é através do Docker.

### Com Docker Compose (1 comando)

Basta executar na raiz do projeto:

```bash
docker compose up --build
```

Para rodar em segundo plano (background):
```bash
docker compose up -d --build
```

Para parar a execução:
```bash
docker compose down
```

### Com Docker puro

```bash
# 1. Build da imagem
docker build -t tea-shop-api .

# 2. Execução do container
docker run -p 8080:8080 tea-shop-api
```

A aplicação subirá pronta em `http://localhost:8080`.

---

## ☕ Como Executar Localmente (com SDKMAN ou JDK Nativo)

Se preferir rodar diretamente na sua máquina local:

### 1. Se você usa SDKMAN e seu Java padrão é o 17

O projeto já contém o arquivo `.sdkmanrc` configurado para Java 21.

#### Opção A: Usar o arquivo `.sdkmanrc` (Automático por pasta)
1. Certifique-se de ter o Java 21 instalado no SDKMAN (ou instale com `sdk install java 21.0.6-tem`).
2. Dentro da pasta do projeto, execute:
   ```bash
   sdk env
   ```
   *(O SDKMAN lerá o `.sdkmanrc` e ativará o Java 21 apenas para essa sessão do terminal).*
3. **Dica:** Ative a troca automática no seu `~/.sdkman/etc/config`:
   ```bash
   sdkman_auto_env=true
   ```
   Com isso ativado, toda vez que você entrar na pasta `cd tea-shop-api`, o SDKMAN alternará para o Java 21 automaticamente!

#### Opção B: Alternar manualmente apenas na sessão atual
```bash
sdk use java 21.0.6-tem
```

*(Caso você já tenha o Java 21 instalado pelo Linux via `apt`/`pacman` e não queira baixar outro pelo SDKMAN, você pode vinculá-lo ao SDKMAN com `sdk install java 21-local /usr/lib/jvm/java-21-openjdk`)*.

---

### 2. Rodando a Aplicação Localmente

Por padrão, a aplicação utiliza banco de dados **H2 em memória**, então não é preciso subir nenhum serviço extra:

```bash
./mvnw spring-boot:run
```
*(No Windows utilize `mvnw.cmd spring-boot:run`)*

---

### 3. Execução com PostgreSQL (Opcional)

A aplicação foi configurada para aceitar variáveis de ambiente para o banco de dados. Caso deseje conectar a um PostgreSQL:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/teashop_db \
SPRING_DATASOURCE_USERNAME=seu_usuario \
SPRING_DATASOURCE_PASSWORD=sua_senha \
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver \
./mvnw spring-boot:run
```

Ou configure as variáveis no arquivo `src/main/resources/application.properties`.

---

## 🧪 Como Executar os Testes

Para executar toda a suíte de testes unitários e de integração:

```bash
./mvnw clean test
```

---

## 📦 Gerar Pacote JAR

```bash
./mvnw clean package
java -jar target/tea-shop-api-0.0.1-SNAPSHOT.jar
```

---

## 📑 Acesso e Documentação

Com a aplicação rodando (localmente ou via Docker), acesse:

| Recurso | URL | Descrição |
| :--- | :--- | :--- |
| **Swagger UI** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | Interface interativa da documentação da API |
| **OpenAPI Docs (JSON)** | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) | Especificação OpenAPI 3 em formato JSON |
| **Console H2** | [http://localhost:8080/h2-console](http://localhost:8080/h2-console) | Gerenciador web do banco de dados em memória |
| **Actuator Health** | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) | Status de saúde da aplicação |

> [!TIP]
> **Dados de conexão para o H2 Console:**
> * **JDBC URL:** `jdbc:h2:mem:teashop`
> * **User Name:** `sa`
> * **Password:** *(deixe em branco)*

---

## 🛣️ Principais Endpoints da API

### 🍵 Produtos (`/api/product`)
* `POST /api/product` - Cadastra um novo produto
* `GET /api/product` - Retorna a lista de produtos
* `GET /api/product/{id}` - Busca produto por ID
* `PUT /api/product/{id}` - Atualiza informações do produto
* `DELETE /api/product/{id}` - Remove produto por ID

### 👤 Usuários (`/api/user`)
* `POST /api/user` - Cadastra um usuário com lista de endereços
* `GET /api/user` - Lista todos os usuários
* `GET /api/user/{id}` - Busca usuário por UUID
* `PUT /api/user/{id}` - Atualiza dados do usuário
* `DELETE /api/user/{id}` - Remove usuário por UUID

### 📦 Pedidos (`/api/order`)
* `POST /api/order` - Realiza um pedido (valida estoque e calcula o valor total)
* `GET /api/order` - Lista o resumo de todos os pedidos
* `GET /api/order/{id}` - Obtém o detalhamento de um pedido por UUID

### 🔐 Autenticação (`/api/auth`)
* `POST /api/auth` - Endpoint base para registro / autenticação
