# API de Cadastro de Pessoas

![Java](https://img.shields.io/badge/Java-21-E76F00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Wrapper-C71A36?logo=apachemaven&logoColor=white)

API REST desenvolvida com Spring Boot para gerenciar pessoas, sugestões e contas de acesso. O projeto usa autenticação por sessão, autorização por perfil, proteção CSRF, senhas protegidas e tratamento centralizado de exceções.

## Funcionalidades

### Acesso e segurança

- Cadastro público que sempre cria uma conta `VISITANTE` vinculada à pessoa.
- Login e logout por sessão com cookie `HttpOnly`.
- Senhas armazenadas por hash, nunca em texto puro.
- Perfis `ADMIN`, `OPERADOR` e `VISITANTE` verificados no backend.
- Proteção CSRF para todas as operações de alteração.
- Administração de contas, perfis, status e redefinição de senha.
- Criação opcional do primeiro administrador por variáveis de ambiente.

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
- Spring Security
- Spring Data JPA
- Jakarta Validation
- Hibernate
- MySQL
- Lombok
- Maven Wrapper
- H2 somente nos testes automatizados

## Arquitetura

O projeto está organizado em camadas com responsabilidades separadas:

```text
src/main/java/com/empresa/cadrastro_pessoas/
├── auth/                   # Cadastro público, sessão e autenticação
├── config/                 # Segurança, CORS e inicialização
├── minhaconta/             # Dados e sugestões do visitante autenticado
├── pessoa/
│   ├── controller/         # Endpoints de pessoas
│   ├── dto/                # Dados recebidos pela API
│   ├── model/              # Entidade JPA Pessoa
│   ├── repository/         # Acesso ao banco de dados
│   └── service/            # Regras de negócio
├── tipoacesso/
    ├── controller/         # Endpoints de tipos de acesso
    ├── dto/                # Objetos de entrada e saída
    ├── repository/         # Acesso ao banco de dados
│   └── service/            # Regras de negócio
└── usuario/                # Contas, perfis e administração de acessos
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

### 3. Configurar a conexão e o primeiro administrador

As credenciais não ficam gravadas no repositório. No PowerShell, informe as variáveis antes de iniciar:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "sua_senha_do_mysql"
$env:APP_ADMIN_EMAIL = "admin@exemplo.com"
$env:APP_ADMIN_PASSWORD = "uma_senha_com_8_ou_mais_caracteres"
```

O primeiro administrador é criado somente se ainda não existir nenhuma conta `ADMIN`. Depois disso, as contas de administrador e operador são gerenciadas pela tela de usuários.

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

## Permissões

| Ação | Visitante | Operador | Administrador |
|---|---:|---:|---:|
| Consultar os próprios dados | Sim | — | — |
| Enviar e acompanhar as próprias sugestões | Sim | — | — |
| Gerenciar pessoas e sugestões | Não | Sim | Sim |
| Excluir dados permanentemente | Não | Não | Sim |
| Gerenciar contas e perfis | Não | Não | Sim |

## Endpoints de autenticação

| Método | Rota | Acesso |
|---|---|---|
| `GET` | `/api/auth/csrf` | Público |
| `POST` | `/api/auth/cadastro` | Público; sempre cria visitante |
| `POST` | `/api/auth/login` | Público |
| `POST` | `/api/auth/logout` | Autenticado |
| `GET` | `/api/auth/me` | Autenticado |
| `GET` | `/api/minha-conta` | Visitante |
| `GET` | `/api/minha-conta/sugestoes` | Visitante |
| `POST` | `/api/minha-conta/sugestoes` | Visitante |
| `GET/POST/PATCH/PUT` | `/api/usuarios/**` | Administrador |

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

O CORS está configurado para permitir credenciais do frontend executado em:

```text
http://localhost:5173
```

Para alterar a origem, use `APP_FRONTEND_ORIGIN`. Em produção, execute frontend e backend com HTTPS e defina `SESSION_COOKIE_SECURE=true`.

## Testes e build

Executar os testes:

```powershell
.\mvnw.cmd test
```

Os testes usam um banco H2 temporário e não alteram o banco MySQL local.

Gerar o pacote da aplicação:

```powershell
.\mvnw.cmd clean package
```

O arquivo `.jar` será gerado na pasta `target/`.

## Próximas melhorias

- Documentação interativa com Swagger/OpenAPI.
- Paginação e ordenação da listagem de pessoas.
- Migrations do banco com Flyway ou Liquibase.

## Autor

Desenvolvido por [Paulo Higa](https://github.com/oPauLoHiga).
