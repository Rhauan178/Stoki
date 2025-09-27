# Startup-sem-nome
Startup voltada para auxílio no gerenciamento de mesas e ingredientes de restaurantes e bares.


Este projeto tem como objetivo modelar e desenvolver um sistema de gerenciamento completo para restaurantes, bares e estabelecimentos similares. A solução é focada em otimizar a operação, integrando o atendimento ao cliente diretamente nas mesas com um controle de estoque inteligente e ferramentas de gestão para a equipe. A estrutura do sistema busca garantir eficiência, reduzir erros operacionais e melhorar a experiência do cliente.

Requisitos Funcionais (Funcionalidades)
Descrevem as ações e funcionalidades que o sistema deve ser capaz de executar.

RF01 - Cardápio Digital e Pedidos na Mesa: Permite que clientes acessem o cardápio via QR Code, visualizem detalhes dos pratos e façam seus pedidos diretamente do smartphone, enviando-os para a cozinha em tempo real.

RF02 - Gestão de Estoque Inteligente: Permite o cadastro de ingredientes, sua associação aos itens do cardápio (ficha técnica), a baixa automática de insumos a cada pedido e a emissão de alertas de estoque baixo.

RF03 - Painel Operacional para a Equipe: Fornece telas otimizadas para a cozinha visualizar e gerenciar o status dos pedidos, e para os garçons acompanharem o status das mesas, receberem notificações e auxiliarem os clientes.

RF04 - Gerenciamento do Salão: Permite o controle do status das mesas (livre, ocupada, reservada), facilitando a organização do fluxo de clientes.

RF05 - Módulo Administrativo: Disponibiliza ferramentas para gerentes cadastrarem e alterarem itens do cardápio (preços, descrições, disponibilidade), gerenciarem contas de funcionários e acessarem relatórios de vendas.

Requisitos Não Funcionais
Descrevem os critérios de qualidade e as restrições de operação do sistema, ou seja, como o sistema deve se comportar.

RNF01 - Usabilidade: A interface do cliente deve ser extremamente intuitiva, sem necessidade de aprendizado prévio. As telas da equipe devem ser ágeis e de fácil operação para não atrasar o serviço.

RNF02 - Desempenho: O sistema deve ter um tempo de resposta rápido. O carregamento do cardápio e o envio de um pedido não devem levar mais de 2 segundos.

RNF03 - Segurança: O acesso aos módulos de gestão e operação deve ser protegido por um sistema de autenticação e autorização por cargo.

RNF04 - Disponibilidade: O sistema precisa ter alta disponibilidade (ex: 99.5% de uptime), garantindo que esteja funcional durante todo o horário de operação do estabelecimento, especialmente nos horários de pico.

RNF05 - Compatibilidade: A aplicação web voltada para o cliente deve ser responsiva e totalmente compatível com os principais navegadores de smartphones (como Chrome e Safari).

Estrutura do Banco de Dados
Entidades principais:

Mesa

Usuario (Funcionário)

ItemCardapio

Ingrediente

Pedido

Relacionamentos:

Uma mesa pode ter vários pedidos ao longo do tempo.

Um pedido é composto por um ou vários itens do cardápio.

Um item do cardápio pode utilizar vários ingredientes (ficha técnica).

Um funcionário (garçom) pode ser responsável por atender vários pedidos.

Generalização/Especialização:

A entidade Usuario pode ser generalizada para representar qualquer pessoa que interaja com o sistema. Ela pode ser especializada em Funcionario e Cliente. A entidade Funcionario, por sua vez, pode ser especializada em subtipos como Gerente, Garcom e Cozinheiro, que herdam atributos da entidade genérica e possuem permissões de acesso distintas.

Modelo de Dados
O modelo de dados foi projetado para refletir os requisitos de um ambiente de restaurante dinâmico. Abaixo estão algumas das principais entidades e seus atributos essenciais:

Mesa: ID_Mesa, Numero, Status (Livre, Ocupada, Reservada), QRCode

ItemCardapio: ID_Item, Nome, Descricao, Preco, Categoria, Disponivel

Pedido: ID_Pedido, ID_Mesa, Status (Aberto, Enviado, Pronto, Pago), Data_Hora

Usuario: ID_Usuario, Nome, Login, Senha, Cargo (Gerente, Garçom, etc.)

Ingrediente: ID_Ingrediente, Nome, Estoque_Atual, Unidade_Medida, Estoque_Minimo
