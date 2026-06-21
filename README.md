# Tea Shop API 🍵

Uma API RESTful robusta desenvolvida para gerenciar o backend de uma loja de chás. Este sistema é o núcleo de uma aplicação full stack, projetado para fornecer dados de forma segura, eficiente e escalável para o consumo de um aplicativo móvel.

## 🚀 Tecnologias e Arquitetura

O projeto foi inicializado com foco em performance e concorrência moderna, utilizando as seguintes tecnologias:

* **Linguagem:** Java 21
* **Framework:** Spring Boot 4.0.7 (WebMVC)
* **Banco de Dados:** PostgreSQL
* **Controle de Versão de Banco (Migrações):** Flyway
* **Mapeamento Objeto-Relacional (ORM):** Spring Data JPA / Hibernate
* **Segurança:** Spring Security
* **Validação de Dados:** Spring Boot Validation
* **Documentação:** SpringDoc OpenAPI 3 (Swagger)
* **Produtividade:** Lombok

## 🏗️ Modelagem e Banco de Dados

A arquitetura de dados utiliza modelagem conceitual e mapeamento relacional sólidos para garantir a integridade do catálogo de produtos, usuários e fluxo de pedidos. Todas as alterações e evoluções no esquema do banco de dados do PostgreSQL são gerenciadas de forma automatizada pelo Flyway, garantindo consistência entre os ambientes de desenvolvimento e produção.

## ⚙️ Como executar o projeto localmente

### Pré-requisitos
* Java Development Kit (JDK) 21
* Maven
* PostgreSQL rodando localmente (nativamente ou via Docker) na porta padrão `5432`

### Passos para execução
1. Clone o repositório para a sua máquina.
2. Crie um banco de dados no PostgreSQL chamado `teashop_db`.
3. Configure as credenciais de acesso no arquivo `src/main/resources/application.properties` (ou `application.yml`):
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/teashop_db
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   
   # O Flyway rodará as migrations automaticamente ao subir a aplicação
# tea-shop-api
