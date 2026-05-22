# Eco Verde Mobile

Aplicativo Android para gestão de uma loja sustentável, com autenticação, recuperação de senha, carrinho, compras e painel de administração de estoque.

## Visão Geral

O **Eco Verde Mobile** foi desenvolvido em Kotlin com Android SDK e SQLite local.
O foco do projeto é oferecer um fluxo completo de app mobile de e-commerce simples, com papéis de usuário e administrador.

## Funcionalidades

### Autenticação e Conta
- Login com validação de e-mail e senha.
- Cadastro de usuário com:
  - nome
  - e-mail
  - endereço
  - pergunta de segurança
  - resposta de segurança
  - senha + confirmação
- Criação opcional de conta admin com código secreto.
- Persistência de sessão em `SharedPreferences`.

### Recuperação de Senha (offline/local)
- Fluxo em 3 etapas:
  1. Informar e-mail cadastrado
  2. Responder pergunta de segurança
  3. Definir nova senha
- Respostas de segurança normalizadas (`trim + lowercase`) para comparação consistente.

### Loja e Produtos
- Listagem de produtos com paginação simples (“carregar mais”).
- Exibição de:
  - nome
  - preço
  - estoque disponível
  - imagem do produto
- Mapeamento automático de imagem por nome/categoria (ex.: banana, tomate, alface, morango, maçã, cenoura, brócolis, batata, laranja).

### Carrinho e Compra
- Adição e remoção de itens no carrinho.
- Bloqueio de quantidade acima do estoque disponível.
- Resumo automático de subtotal, frete e total.
- Finalização de compra com:
  - geração de protocolo
  - gravação em histórico de compras
  - status: **Confirmada - pagamento na entrega**
  - baixa de estoque no banco

### Perfil
- Exibe nome, e-mail e endereço do usuário.
- Alterar senha.
- Alterar endereço.
- Excluir conta.
- Visualizar “Minhas compras”.
- Abrir detalhe de compra com botão de voltar.

### Painel Admin
- Listar produtos.
- Adicionar produto.
- Editar estoque.
- Deletar produto.
- Logout dedicado (limpa sessão e volta ao login).

### UI e Identidade Visual
- Logo do projeto aplicada nas telas.
- Ícone adaptativo do app usando `logo_eco_verde.png`.
- Layout responsivo para uso em emulador/dispositivo Android.

## Tecnologias

- **Kotlin**
- **Android SDK**
- **Material Components**
- **SQLite** (via `SQLiteOpenHelper`)
- **Gradle (KTS)**

## Estrutura Principal

- `app/src/main/java/com/example/projetomobile/`
  - Activities (`MainActivity`, `CadastroActivity`, `RecuperarSenhaActivity`, `HomeActivity`, `CarrinhoActivity`, `PerfilActivity`, `AdminActivity`, `DetalheCompraActivity`)
  - Helpers de banco (`DatabaseHelper`, `UserDatabaseHelper`, `ProductDatabaseHelper`, `PurchaseDatabaseHelper`)
  - Entidades (`User`, `Product`, `Purchase`)
- `app/src/main/res/layout/`
  - Layouts das telas
- `app/src/main/res/drawable/`
  - Logo e imagens de produtos

## Como Executar

### Pré-requisitos
- Android Studio (versão recente)
- JDK 17+
- Emulador Android ou dispositivo físico

### Passos
1. Clone o repositório:
   ```bash
   git clone <url-do-repo>
   cd Eco-Verde-Mobile
   ```
2. Compile e instale:
   ```bash
   ./gradlew installDebug
   ```
3. Execute pelo Android Studio ou via ADB.

## Comandos Úteis

```bash
# Build debug
./gradlew assembleDebug

# Instalar no emulador/dispositivo
./gradlew installDebug

# Rodar testes unitários
./gradlew testDebugUnitTest

# Limpar dados do app (reset da base local)
adb shell pm clear com.example.projetomobile
```

## Credenciais e Observações

- Código para criar conta admin (fluxo atual no app): `SUPERADMIN2026`
- Banco local é criado automaticamente na primeira execução.
- Projeto com foco acadêmico: parte de recuperação de senha é local/offline.

## Melhorias Futuras

- Upload real de imagem de produto pelo admin.
- Recuperação de senha por e-mail real (SMTP/API).
- Testes instrumentados de fluxos críticos (login, compra, recuperação).
- Internacionalização completa de mensagens hoje hardcoded.

---

Desenvolvido para a disciplina/Desenvolvimento Mobile com foco em arquitetura simples, validações de fluxo e experiência prática de app Android.
