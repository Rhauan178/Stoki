AppRestaurante - Sistema de Gestão
Este projeto é um sistema de gerenciamento completo para restaurantes, bares e estabelecimentos similares, desenvolvido em Java e JavaFX. A solução foca em otimizar a operação, integrando o atendimento ao cliente, um painel para a cozinha (KDS), gestão de estoque inteligente e ferramentas administrativas.

Funcionalidades
O sistema é dividido em vários módulos principais:

Segurança: Sistema de login com permissões por cargo (Gerente, Garçom, Cozinheiro).

Gestão de Salão: Painel visual de mesas (livres, ocupadas, reservadas) com capacidade de adicionar e remover mesas.

Gestão de Pedidos: Fluxo de "envios" (comandas) separadas para a cozinha.

Fecho de Conta: Módulo de conta que agrega todos os "envios" de uma mesa e permite o pagamento por múltiplos métodos (Dinheiro, Cartão, PIX simulado).

Painel da Cozinha (KDS): Uma tela dedicada para a cozinha ver pedidos pendentes e em preparo, com atualização automática.

Gestão de Estoque: Módulo para gerir ingredientes, com baixa automática de estoque a cada venda, com base na Ficha Técnica.

Módulo Administrativo:

Gestão de Cardápio e Fichas Técnicas (Receitas).

Gestão de Funcionários (CRUD completo).

Relatórios de Vendas com filtros (por data, por funcionário) e arquivamento de dados.

Tecnologias Utilizadas
Linguagem: Java 24

Interface Gráfica: JavaFX 17

Base de Dados: MySQL (gerido via XAMPP/MariaDB)

Build: Apache Maven

Bibliotecas: FontAwesomeFX (ícones), ZXing (QR Code)

Instruções de Configuração do Ambiente
Para executar este projeto na sua máquina local, siga estes passos. Cada desenvolvedor na equipa precisa de ter o seu próprio ambiente de base de dados local.

1. Instale os Pré-requisitos
   Java JDK 24: Garanta que tem o JDK 24 (ou superior) instalado e configurado.

XAMPP: Descarregue e instale o XAMPP a partir do site oficial: https://www.apachefriends.org/pt_br/index.html

Importante: Durante a instalação, aceite o caminho padrão (C:\xampp). Não instale em C:\Program Files, pois as permissões do Windows (UAC) podem causar problemas.

2. Inicie o Servidor MySQL
   Abra o XAMPP Control Panel (Painel de Controlo do XAMPP).

Clique em "Start" ao lado do módulo Apache.

Clique em "Start" ao lado do módulo MySQL. (Ambos devem ficar verdes).

3. Crie a Base de Dados
   No XAMPP Control Panel, clique no botão "Admin" na linha do MySQL. O seu navegador irá abrir o phpMyAdmin.

Na página do phpMyAdmin, clique na aba "Bases de dados" (ou "Databases").

No campo "Criar base de dados", digite o nome exato: restaurante_db

No campo de "Agrupamento" (Collation), selecione utf8mb4_general_ci.

Clique em "Criar".

4. Execute o Projeto no IntelliJ
   Abra este projeto no IntelliJ IDEA.

Aguarde alguns momentos para o Maven descarregar todas as dependências (ver pom.xml).

Localize o ficheiro App.java (em src/main/java/com/seu/restaurante/).

Clique com o botão direito em App.java e selecione "Run 'App.main()'".

5. Primeiro Login
   Na primeira vez que a aplicação arrancar, ela irá conectar-se à sua base de dados restaurante_db local e criar todas as tabelas necessárias automaticamente.

Use o login de administrador padrão para aceder ao sistema:

Utilizador: admin

Senha: 1234