# API de Cadastro de Pessoas

![Java](https://img.shields.io/badge/Java-21-E76F00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Wrapper-C71A36?logo=apachemaven&logoColor=white)

API REST desenvolvida com Spring Boot para gerenciar pessoas e tipos de acesso. O projeto aplica uma arquitetura em camadas, persistência com Spring Data JPA, validação de dados e tratamento centralizado de exceções.

## Funcionalidades

### Pessoas

- Cadastrar e atualizar pessoas.
- Listar todos os cadastros ou apenas os ativos.
- Buscar por ID, CPF ou parte do nome.
- Ativar e desativar sem excluir o registro.
- Excluir permanentemente.
- Impedir CPF e e-mail duplicados.
- Validar campos obrigatórios, formato do CPF, e-mail e data de nascimento.

### Tipos de acesso

- Cadastrar e atualizar tipos de acesso.
- Listar todos ou apenas os ativos.
- Consultar por ID.
- Desativar e excluir.
- Informar a quantidade de pessoas associadas a cada tipo.

## Tecnologias

- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- Jakarta Validation
- Hibernate
- MySQL
- Lombok
- Maven Wrapper

## Arquitetura

O projeto está organizado em camadas com responsabilidades separadas:

```text
src/main/java/com/empresa/cadrastro_pessoas/
├── config/                 # Configuração de CORS
├── exeption/               # Exceções e tratamento global
├── pessoas/
│   ├── controller/         # Endpoints de pessoas
│   ├── dto/                # Dados recebidos pela API
│   ├── model/              # Entidade JPA Pessoa
│   ├── repository/         # Acesso ao banco de dados
│   └── service/            # Regras de negócio
└── tipoAcesso/
    ├── controller/         # Endpoints de tipos de acesso
    ├── dto/                # Objetos de entrada e saída
    ├── repository/         # Acesso ao banco de dados
    └── service/            # Regras de negócio
```

O fluxo principal de uma requisição é:

```text
Cliente HTTP → Controller → Service → Repository → MySQL
```

## Pré-requisitos

Antes de executar o projeto, instale:

- JDK 21
- MySQL 8 ou superior
- Git

Não é necessário instalar o Maven globalmente, pois o projeto possui Maven Wrapper.

## Como executar

### 1. Clonar o repositório

```bash
git clone https://github.com/oPauLoHiga/SpringBootProjetoPessoa.git
cd SpringBootProjetoPessoa
```

### 2. Criar o banco de dados

Execute no MySQL:

```sql
CREATE DATABASE cadastro_pessoas_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar a conexão

Atualize `src/main/resources/application.properties` com as credenciais da sua instalação do MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cadastro_pessoas_db?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

O Hibernate está configurado com `ddl-auto=update`, portanto as tabelas são criadas ou atualizadas quando a aplicação inicia.

### 4. Iniciar a aplicação

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

## Endpoints de pessoas

URL base: `/api/pessoas`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/pessoas` | Lista todas as pessoas |
| `GET` | `/api/pessoas/ativas` | Lista apenas pessoas ativas |
| `GET` | `/api/pessoas/{id}` | Busca uma pessoa por ID |
| `GET` | `/api/pessoas/cpf/{cpf}` | Busca uma pessoa por CPF |
| `GET` | `/api/pessoas/buscar?nome=Maria` | Busca pessoas pelo nome |
| `POST` | `/api/pessoas` | Cadastra uma pessoa |
| `PUT` | `/api/pessoas/{id}` | Atualiza uma pessoa |
| `PATCH` | `/api/pessoas/{id}/ativar` | Ativa uma pessoa |
| `PATCH` | `/api/pessoas/{id}/desativar` | Desativa uma pessoa |
| `DELETE` | `/api/pessoas/{id}` | Exclui uma pessoa permanentemente |

### Exemplo de cadastro

```http
POST /api/pessoas
Content-Type: application/json
```

```json
{
  "nome": "Maria da Silva",
  "cpf": "123.456.789-00",
  "email": "maria@email.com",
  "telefone": "(61) 99999-8888",
  "dataNascimento": "1995-08-20",
  "endereco": "Rua das Flores, 100",
  "cidade": "Brasília",
  "estado": "DF"
}
```

Campos obrigatórios:

- `nome`: entre 2 e 100 caracteres.
- `cpf`: formato `000.000.000-00`.
- `email`: endereço de e-mail válido.
- `dataNascimento`: data anterior ao dia atual, no formato `AAAA-MM-DD`.

## Endpoints de tipos de acesso

URL base: `/api/tipos-acesso`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/tipos-acesso` | Lista todos os tipos de acesso |
| `GET` | `/api/tipos-acesso/ativos` | Lista apenas os tipos ativos |
| `GET` | `/api/tipos-acesso/{id}` | Busca um tipo por ID |
| `POST` | `/api/tipos-acesso` | Cadastra um tipo de acesso |
| `PUT` | `/api/tipos-acesso/{id}` | Atualiza um tipo de acesso |
| `PATCH` | `/api/tipos-acesso/{id}/desativar` | Desativa um tipo de acesso |
| `DELETE` | `/api/tipos-acesso/{id}` | Exclui um tipo de acesso |

### Exemplo de cadastro

```http
POST /api/tipos-acesso
Content-Type: application/json
```

```json
{
  "nome": "ADMIN",
  "descricao": "Acesso administrativo ao sistema"
}
```

Exemplo de resposta:

```json
{
  "id": 1,
  "nome": "ADMIN",
  "descricao": "Acesso administrativo ao sistema",
  "ativo": true,
  "totalPessoas": 0
}
```

## Validação e respostas de erro

Erros de validação retornam status `400 Bad Request` e uma lista com os campos inválidos:

```json
{
  "timestamp": "2026-07-31T16:00:00",
  "status": 400,
  "error": "Dados inválidos",
  "messages": [
    "email: E-mail inválido",
    "cpf: CPF deve estar no formato 000.000.000-00"
  ]
}
```

Recursos inexistentes retornam `404 Not Found`:

```json
{
  "timestamp": "2026-07-31T16:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Pessoa não encontrada com ID: 99"
}
```

## Integração com frontend

O CORS está configurado para permitir requisições do frontend executado em:

```text
http://localhost:5173
```

São permitidos os métodos `GET`, `POST`, `PUT`, `PATCH` e `DELETE`.

## Testes e build

Executar os testes:

```powershell
.\mvnw.cmd test
```

Gerar o pacote da aplicação:

```powershell
.\mvnw.cmd clean package
```

O arquivo `.jar` será gerado na pasta `target/`.

## Próximas melhorias

- Documentação interativa com Swagger/OpenAPI.
- Autenticação e autorização com Spring Security e JWT.
- Paginação e ordenação da listagem de pessoas.
- Testes unitários e de integração para controllers e services.
- Migrations do banco com Flyway ou Liquibase.
- Variáveis de ambiente para credenciais e configurações sensíveis.

## Autor

Desenvolvido por [Paulo Higa](https://github.com/oPauLoHiga).
