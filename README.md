# 📚 API de Gerenciamento de Biblioteca

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Framework-Spring_Boot-green.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Infra-Docker-blue.svg)](https://www.docker.com/)

Uma API REST de alta performance para o gerenciamento completo de bibliotecas, construída com foco em **boas práticas de engenharia de software**, **segurança robusta** e **validação rigorosa de regras de negócio**.

---

## 🏗️ Arquitetura e Destaques Técnicos

O sistema foi desenhado para ser desacoplado, escalável e seguro, utilizando padrões modernos do ecossistema Spring com **Java 21**.

### 🛡️ Fluxo de Segurança com JWT
O núcleo da segurança da API. Nenhuma requisição a rotas protegidas é processada sem antes passar por um filtro de validação customizado, que garante a autenticidade e a autorização do usuário.
*   **Stateless:** A autenticação é 100% sem estado, ideal para arquiteturas distribuídas.
*   **Controle de Acesso:** O token carrega a `role` do usuário (`ADMIN` ou `USER`), permitindo um controle de acesso granular nos endpoints.

<p align="center">
  <img src="img/excalidraw.png" width="750" alt="Arquitetura Técnica" />
</p>

---

### 💼 Lógica de Negócio Transacional
O sistema vai além de um simples CRUD, implementando regras de negócio complexas de forma segura dentro de transações atômicas.
*   **Integridade Garantida:** Operações críticas como `realizarEmprestimo` são anotadas com `@Transactional`. Se qualquer etapa falhar (ex: o livro fica indisponível no último segundo), a operação inteira é revertida (`rollback`), garantindo que não haja dados inconsistentes no banco.
*   **Lógica Encapsulada:** As regras (ex: "usuário pode emprestar?") são encapsuladas dentro das próprias entidades, tornando o código mais limpo e orientado a objetos.

<details>
<summary>📄 <strong>Clique para ver a Documentação Interativa da API (Swagger UI)</strong></summary>
<br />

<p align="center">
  <img src="img/swagger-ui.png" width="750" alt="Swagger UI" />
</p>
<p align="center">
  <em>Todos os endpoints, DTOs e requisitos de segurança estão documentados e são testáveis em tempo real.</em>
</p>

</details>

---

## ✨ Funcionalidades Principais

#### 👨‍💼 Módulo do Administrador (`ROLE_ADMIN`)
*   **Gestão de Usuários:** CRUD completo de usuários, incluindo listagem, busca por ID, bloqueio/desbloqueio manual e alteração de nível de acesso (`role`).
*   **Gestão do Acervo:** Cadastro e remoção de livros do sistema.

#### 👨‍🎓 Módulo do Usuário (`ROLE_USER`)
*   **Autosserviço:** Cadastro, login e um endpoint para que o próprio usuário possa "pagar" suas multas e se desbloquear.
*   **Consulta de Livros:** Acesso à lista de livros disponíveis para empréstimo.
*   **Fluxo de Empréstimo:** Capacidade de realizar empréstimos e devoluções.

---

## 🛠️ Stack Tecnológica e Engenharia

*   **Linguagem:** Java (JDK 21).
*   **Framework Core:** Spring Boot 3.4+.
*   **Segurança:** Spring Security com autenticação JWT (via `auth0/java-jwt`).
*   **Persistência:** Spring Data JPA / Hibernate com otimizações de performance (índices `UNIQUE` em `cpf` e `email`).
*   **Banco de Dados:** MySQL.
*   **Infraestrutura:** Docker e Docker Compose para um ambiente de desenvolvimento e produção consistente.
*   **Documentação:** Swagger UI (OpenAPI 3).

---

## 🚀 Como Executar o Projeto

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/gu-pereira/api-libary.git
    cd api-libary
    ```
2.  **Inicie o ambiente com Docker:**
    O `docker-compose.yml` possui um `healthcheck` que garante que a API só inicie após o banco de dados estar 100% operacional.
    ```bash
    docker-compose up --build
    ```
3.  **Acesse a documentação** e comece a testar: `http://localhost:8080/swagger-ui.html`

---

## 📄 Licença

Este projeto está sob a licença MIT. Consulte o arquivo [LICENSE](LICENSE) para mais detalhes.

---
<p align="center">
  Desenvolvido por <strong>Gustavo Pereira</strong>
</p>
<p align="center">
  <em>"A elegância em software não é a ausência de complexidade, mas a sua maestria. É sobre construir sistemas que são não apenas funcionais, mas fundamentalmente sólidos."</em>
</p>
