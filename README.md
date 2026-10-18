# Mercado Express — CP5 (Spring MVC + Security + RabbitMQ + E-mail + Dashboard)

Aplicação web desenvolvida com Spring MVC, Thymeleaf, Spring Security, PostgreSQL, RabbitMQ e JavaMailSender para gerenciamento de produtos, compras com integração assíncrona de pagamentos ("Banco Tranquilo" — [`dbexpress`]), envio automático de e-mail de confirmação e Dashboard gerencial do Mercado Express.

**IDE utilizada:** IntelliJ IDEA

---

## Links do Projeto

- **Link de Produção (Render):** `COLOQUE_AQUI_O_LINK_DO_RENDER`
- **Repositório Worker Banco Tranquilo (`dbexpress`):** `COLOQUE_AQUI_O_LINK_DO_GITHUB_DBEXPRESS`
- **Vídeo de Demonstração (YouTube):** `COLOQUE_AQUI_O_LINK_DO_VIDEO`

---

## Integrantes

| Nome | RM |
|---|---|
| Ana Flavia Camelo | 561489 |
| Gustavo Kenji Terada | 562745 |
| João Guilherme Carvalho Novaes | 566234 |
| Pedro Chasci Puga | 565154 |
| Lucas Figueiredo Vieira | 561342 |

---

## Tecnologias utilizadas

- Java 21
- Spring Boot 4.1.1
- Spring MVC + Thymeleaf + Thymeleaf Extras Spring Security 6
- Spring Security (Autenticação e Autorização com BCrypt)
- Spring Data JPA + PostgreSQL
- Spring for RabbitMQ (AMQP)
- Java Mail Sender (SMTP Gmail)
- MapStruct 1.6.3
- Lombok
- Chart.js (Gráficos do Dashboard)
- Docker & Docker Compose
- Maven

---

## Configuração do Spring Initializr

<img width="1147" height="830" alt="image" src="https://github.com/user-attachments/assets/a8500377-32b7-47f8-a82a-3300bf5b4496" />

---

## Arquitetura do Sistema (Parte 1 + Parte 2)

O sistema é dividido em dois serviços independentes que se comunicam de forma assíncrona via **RabbitMQ**:

1. **`mercadoExpress` (Porta `8080`):** Aplicação principal contendo o Front-end Thymeleaf (Landing Page, Login, Cadastro, Consulta e CRUD de Produtos em Cards, Carrinho, Meus Pedidos e Dashboard Gerencial), APIs REST (`/pagamentos` e `/sending-email`), produtores/consumidores RabbitMQ e envio de e-mail com persistência no PostgreSQL (`TB_EMAIL`).
2. **`dbexpress` (Porta `9090`):** Microsserviço Worker que simula a **API do Banco Tranquilo**, consumindo a fila `pagamento-request-queue`, processando a transação (80% de aprovação / 20% de recusa) e publicando o resultado nas exchanges de sucesso ou erro.

```text
[Front-end Web Thymeleaf / Postman]
               ⬇
     [mercadoExpress (8080)] ⟷ [PostgreSQL]
               ⬇ ⬆
   [Exchanges & Queues (RabbitMQ)]
               ⬇ ⬆
 [dbexpress - Worker Banco Tranquilo (9090)]
               ⬇
 [Envio de E-mail (SMTP Gmail) + Registro em TB_EMAIL]
```

---

## Banco de Dados PostgreSQL

Configurado via `application.yaml`:

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/mercadoexpress}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
```

### Tabelas geradas no PostgreSQL

#### 1. `TDS_Sec_MVC_TB_Mercado` (Produtos)
| Coluna | Tipo | Descrição |
|---|---|---|
| `id` | `BIGINT` (PK, auto increment) | Identificador do produto |
| `nome` | `VARCHAR(255)` | Nome do produto |
| `tipo` | `VARCHAR(255)` | Categoria (`Alimento`, `Bebida`, `Limpeza`, `Higiene`, `Outro`) |
| `setor` | `VARCHAR(255)` | Setor no mercado |
| `tamanho` | `VARCHAR(255)` | Tamanho (`Pequeno`, `Médio`, `Grande`, `Único`) |
| `preco` | `DOUBLE PRECISION` | Preço de venda (R$) |
| `custo` | `DOUBLE PRECISION` | Custo unitário (R$) para cálculo de lucro no Dashboard |
| `estoque` | `INTEGER` | Quantidade disponível em estoque |

#### 2. `TDS_Users_Mercado` (Usuários e Perfis)
| Coluna | Tipo | Descrição |
|---|---|---|
| `id` | `BIGINT` (PK, auto increment) | Identificador do usuário |
| `nome` | `VARCHAR(255)` | Nome completo (exibido na barra de navegação) |
| `email` | `VARCHAR(255)` (UNIQUE) | E-mail usado no login e no recebimento de comprovantes |
| `senha` | `VARCHAR(255)` | Senha criptografada com `BCryptPasswordEncoder` |
| `role` | `VARCHAR(255)` | Perfil de acesso (`ADMIN` ou `USER`) |

#### 3. `TB_EMAIL` (Controle de E-mails Enviados)
| Coluna | Tipo | Descrição |
|---|---|---|
| `email_id` | `UUID` (PK) | Identificador único do e-mail |
| `owner_ref` | `VARCHAR(255)` | Referência do proprietário (`Tranquilo`) |
| `email_from` | `VARCHAR(255)` | Remetente do e-mail |
| `email_to` | `VARCHAR(255)` | Destinatário (e-mail do cliente) |
| `subject` | `VARCHAR(255)` | Assunto do e-mail |
| `text` | `TEXT` | Corpo da mensagem |
| `send_date_email` | `TIMESTAMP` | Data/hora do disparo |
| `status_email` | `SMALLINT` | Status (`0 = SENT`, `1 = ERROR`) |

#### 4. `TDS_TB_Compras_Mercado` e `TDS_TB_Itens_Compra` (Compras e Pagamentos)
Armazena os pedidos realizados (`numeroPedido`, `clienteNome`, `clienteEmail`, `valorTotal`, `status` [`PENDENTE`, `APROVADO`, `RECUSADO`], `dataCompra`) e seus itens para alimentar a tela **Meus Pedidos** e o **Dashboard Gerencial**.

---

## Como executar localmente

1. Suba o PostgreSQL e o RabbitMQ via Docker Compose na pasta `mercadoExpress`:
```bash
docker compose -f docker/docker-compose.yml up -d
```
2. Crie um arquivo `.env` na raiz de `mercadoExpress` com suas credenciais SMTP do Gmail:
```properties
MAIL_USER=seuemail@gmail.com
MAIL_PASSWORD=sua_senha_de_app_gmail
```
3. Execute a aplicação principal (`mercadoExpress`) pelo IntelliJ IDEA ou via terminal:
```bash
./mvnw spring-boot:run
```
4. Execute o Worker do Banco Tranquilo (`dbexpress`) pelo IntelliJ IDEA ou via terminal:
```bash
./mvnw spring-boot:run
```
5. Acesse:
   - **Aplicação Web:** `http://localhost:8080`
   - **RabbitMQ Management:** `http://localhost:15672` (`guest` / `guest`)

---

## Parte 1 — Spring Security, Rotas e Controle de Acesso

A aplicação possui cadastro de novos clientes (`/cadastro`) persistido no PostgreSQL e dois usuários iniciais criados nativamente pelo `DataInitializer`:

| E-mail | Senha | Perfil |
|---|---|---|
| `admin@mercado.com` | `admin123` | `ADMIN` — pode criar, editar, excluir produtos e acessar o Dashboard Gerencial |
| `cliente@mercado.com` | `cliente123` | `USER` — consulta produtos em cards, utiliza o carrinho e realiza compras |

### Rotas públicas (sem login)

| Rota | Descrição |
|---|---|
| `GET /` ou `GET /index` | Landing page (`index.html`) |
| `GET /login` | Tela de login customizada |
| `GET /cadastro` e `POST /cadastro` | Tela de inscrição (Sign Up) de novos clientes |
| `/css/**` | Arquivos estáticos de estilo |

### Rotas privadas (requer login)

| Rota | Acesso |
|---|---|
| `GET /produtos` (e `?busca=...`) | Autenticado (`USER` e `ADMIN`) |
| `GET /produtos/{id}` | Autenticado (`USER` e `ADMIN`) |
| `GET /carrinho`, `POST /carrinho/**`, `GET /compras` | Autenticado (`USER` e `ADMIN`) |
| `POST /pagamentos`, `POST /sending-email` | Autenticado via HTTP Basic Auth |
| `GET /produtos/novo` e `POST /produtos/salvar` | Somente `ADMIN` |
| `GET /produtos/editar/{id}` e `POST /produtos/editar/{id}` | Somente `ADMIN` |
| `GET /produtos/deletar/{id}` | Somente `ADMIN` |
| `GET /dashboard` | Somente `ADMIN` |

---

## Telas — Parte 1 (Autenticação e CRUD)

### Landing Page (`GET /`)

<img width="1919" height="759" alt="image" src="https://github.com/user-attachments/assets/6fcd208e-2c7d-4234-9628-9d0dc6879ad6" />

### Tela de Cadastro — Sign Up (`GET /cadastro`)

<img width="1919" height="940" alt="image" src="https://github.com/user-attachments/assets/32e7bc95-5708-4239-87cf-51fb028bc87d" />

### Tela de Login Customizada (`GET /login`)

<img width="1910" height="944" alt="image" src="https://github.com/user-attachments/assets/4863209d-8aa2-4f82-9a5f-065cf38aba4f" />

### Produtos em Cards (`GET /produtos`)
Logado como **USER** — visualiza os cards de produtos sem exposição de ID ou estoque interno, e sem botões administrativos (`sec:authorize="hasRole('ADMIN')"`).

<img width="1919" height="745" alt="image" src="https://github.com/user-attachments/assets/0d2042d1-0ef8-4cb5-af9c-73b476cfe57f" />

### Detalhes do Produto (`GET /produtos/{id}`)

<img width="1919" height="401" alt="image" src="https://github.com/user-attachments/assets/6215cf55-1ce9-4aea-8230-f3521d19cc28" />


Logado como **ADMIN** — visualiza estoque e todos os botões de gerenciamento (Editar, Excluir, Novo Produto e Dashboard).

<img width="1919" height="776" alt="image" src="https://github.com/user-attachments/assets/72499640-bd02-4318-91d8-d2dfb02fee26" />

### Cadastrar  (`GET /produtos/novo`)

<img width="1919" height="819" alt="image" src="https://github.com/user-attachments/assets/dd14b1e4-b245-46b9-a62b-3360851a1255" />

### Editar Produto (`GET /produtos/editar/{id}`)

<img width="1919" height="818" alt="image" src="https://github.com/user-attachments/assets/8eb6bf44-d55e-4c57-867a-eda92a53a963" />

### Detalhes do Produto (`GET /produtos/{id}`)

<img width="1919" height="490" alt="image" src="https://github.com/user-attachments/assets/26981a23-21f6-4213-bbe0-9105a4c7e235" />

---

## Parte 2 — Mensageria com RabbitMQ, Banco Tranquilo, E-mail e Dashboard

### Mapa de Exchanges, Routing Keys e Queues

| Fluxo | Exchange (`direct`, `durable`) | Routing Key | Queue (`durable`) |
|---|---|---|---|
| **1. Solicitação de Pagamento (API ➔ Worker)** | `pagamento-request-exchange` | `pagamento-request-rout-key` | `pagamento-request-queue` |
| **2. Resposta de Sucesso (Worker ➔ API)** | `pagamento-response-sucesso-exchange` | `pagamento-response-sucesso-rout-key` | `pagamento-response-sucesso-queue` |
| **3. Resposta de Erro (Worker ➔ API)** | `pagamento-response-erro-exchange` | `pagamento-response-erro-rout-key` | `pagamento-response-erro-queue` |
| **4. Envio de E-mail (Fila Direta)** | *(Default Exchange)* | `ms.email` | `ms.email` |

<img width="1098" height="441" alt="image" src="https://github.com/user-attachments/assets/8590d5ce-d7e3-4b45-8626-f9d0401a5790" />

---

### Exemplos de Requisições JSON para Testes no Postman

> Configurar **Authorization -> Basic Auth** com `admin@mercado.com` / `admin123`.

#### 1. Solicitar Compra / Pagamento (`POST http://localhost:8080/pagamentos`)
```json
{
  "numeroPedido": "171",
  "valor": 550,
  "produto": "camiseta",
  "emailCliente": "cliente@mercado.com"
}
```

#### 2. Enviar E-mail Direto (`POST http://localhost:8080/sending-email` ou via fila `ms.email` no RabbitMQ)
```json
{
  "ownerRef": "Tranquilo",
  "emailFrom": "seuemail@gmail.com",
  "emailTo": "destinatario@gmail.com",
  "subject": "Aproveita que está fácil",
  "text": "Olá, tudo bem? To enviando este e-mail somente para ficar tranquilo mesmo!!! Atenciosamente, Prof. Marcel"
}
```

---

### Carrinho de Compras e Meus Pedidos (`GET /carrinho` e `GET /compras`)

<img width="1919" height="412" alt="image" src="https://github.com/user-attachments/assets/94ff19b1-8f4c-44f5-a559-cd879d124e99" />

<img width="1919" height="326" alt="image" src="https://github.com/user-attachments/assets/3cfa7acf-8881-4748-ab2d-0f0f92e206d9" />

<img width="1915" height="460" alt="image" src="https://github.com/user-attachments/assets/b25da573-a1d0-4387-9bdb-6292d3ad4013" />

---

### E-mail de Confirmação Recebido e Persistência em `TB_EMAIL`

<img width="1132" height="259" alt="image" src="https://github.com/user-attachments/assets/cc67c289-4798-4dd5-b066-f0b39edd584f" />

---

### Dashboard Gerencial (`GET /dashboard`)

Exibe o total de compras, compras aprovadas/recusadas/pendentes, receita total, lucro total, controle de e-mails enviados (`TB_EMAIL`) e gráficos interativos (Chart.js) de vendas por dia, produtos vendidos e estoque atual.

<img width="1906" height="920" alt="image" src="https://github.com/user-attachments/assets/f4de489f-5a53-4cc2-98fe-1262d4bd4167" />

---

## Estrutura do Projeto (`mercadoExpress`)

```text
mercadoExpress/
├── docker/
│   └── docker-compose.yml              # PostgreSQL 16 + RabbitMQ 3.13 Management
├── src/main/java/fiap/mercadoExpress/
│   ├── config/
│   │   ├── DataInitializer.java        # Seed nativo de usuários (ADMIN/USER) e produtos
│   │   ├── RabbitConfig.java           # Declaração de Exchanges, Queues e Bindings
│   │   └── SecurityConfig.java         # Rotas, BCrypt, FormLogin e Basic Auth
│   ├── consumer/
│   │   ├── EmailConsumer.java          # Listener da fila ms.email
│   │   ├── PagamentoResponseErroConsumidor.java
│   │   └── PagamentoResponseSucessoConsumidor.java
│   ├── controller/
│   │   ├── AuthController.java         # Landing page, Login e Cadastro (Sign Up)
│   │   ├── CarrinhoController.java     # Carrinho e finalização de compra
│   │   ├── DashboardController.java    # Dashboard gerencial (/dashboard)
│   │   ├── EmailController.java        # POST /sending-email
│   │   ├── PagamentoController.java    # POST /pagamentos
│   │   └── ProdutoController.java      # Consulta e CRUD MVC de produtos
│   ├── dto/
│   │   ├── DashboardDTO.java
│   │   ├── EmailDto.java
│   │   ├── PagamentoDTO.java
│   │   └── ProdutoDTO.java
│   ├── enums/
│   │   ├── StatusEmail.java            # SENT, ERROR, PROCESSING
│   │   └── StatusPagamento.java        # PENDENTE, APROVADO, RECUSADO
│   ├── mapper/                         # Mappers MapStruct
│   │   ├── EmailMapper.java
│   │   ├── PagamentoMapper.java
│   │   └── ProdutoMapper.java
│   ├── model/
│   │   ├── Compra.java
│   │   ├── EmailModel.java
│   │   ├── ItemCompra.java
│   │   ├── Produto.java
│   │   └── Usuario.java
│   ├── producer/
│   │   └── PagamentoRequestProducer.java
│   ├── repository/
│   │   ├── CompraRepository.java
│   │   ├── EmailRepository.java
│   │   ├── ProdutoRepository.java
│   │   └── UsuarioRepository.java
│   ├── service/
│   │   ├── CarrinhoService.java
│   │   ├── CompraService.java
│   │   ├── DashboardService.java
│   │   ├── EmailService.java
│   │   ├── PagamentoService.java       # Orquestra pagamento, estoque e envio de e-mail
│   │   ├── ProdutoService.java
│   │   ├── UsuarioDetailsService.java
│   │   └── UsuarioService.java
│   └── MercadoExpressApplication.java
├── src/main/resources/
│   ├── static/css/style.css
│   ├── templates/
│   │   ├── index.html
│   │   ├── auth/ (login.html, cadastro.html)
│   │   ├── carrinho/ (carrinho.html, compras.html)
│   │   ├── dashboard/ (dashboard.html)
│   │   ├── fragments/ (nav.html)
│   │   └── produto/ (lista.html, form.html, detalhe.html)
│   └── application.yaml
└── Dockerfile
```
