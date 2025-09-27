# Startup-sem-nome
Startup voltada para auxílio no gerenciamento de mesas e ingredientes de restaurantes e bares.


Este projeto tem como objetivo modelar e desenvolver um sistema de gerenciamento completo para restaurantes, bares e estabelecimentos similares. A solução é focada em otimizar a operação, integrando o atendimento ao cliente diretamente nas mesas com um controle de estoque inteligente e ferramentas de gestão para a equipe. A estrutura do sistema busca garantir eficiência, reduzir erros operacionais e melhorar a experiência do cliente.

Funcionalidades
Cardápio Digital e Pedidos na Mesa: Permite que clientes acessem o cardápio via QR Code, visualizem detalhes dos pratos e façam seus pedidos diretamente do smartphone, enviando-os para a cozinha em tempo real.

Gestão de Estoque Inteligente: Cadastro de ingredientes, associação aos itens do cardápio (ficha técnica), baixa automática de insumos a cada pedido e alertas de estoque baixo para evitar a falta de produtos.

Painel Operacional para a Equipe: Telas otimizadas para a cozinha visualizar e gerenciar o status dos pedidos, e para os garçons acompanharem o status das mesas, receberem notificações e auxiliarem os clientes.

Gerenciamento do Salão: Controle do status das mesas (livre, ocupada, reservada), facilitando a organização do fluxo de clientes.

Módulo Administrativo: Ferramentas para gerentes cadastrarem e alterarem itens do cardápio (preços, descrições, disponibilidade), gerenciarem contas de funcionários e acessarem relatórios de vendas.

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
O modelo de dados foi projetado para refletir os requisitos de um ambiente de restaurante dinâmico, utilizando o conceito de entidades e relacionamentos. Abaixo estão algumas das principais entidades e seus atributos essenciais:

Mesa: ID_Mesa, Numero, Status (Livre, Ocupada, Reservada), QRCode

ItemCardapio: ID_Item, Nome, Descricao, Preco, Categoria, Disponivel

Pedido: ID_Pedido, ID_Mesa, Status (Aberto, Enviado, Pronto, Pago), Data_Hora

Usuario: ID_Usuario, Nome, Login, Senha, Cargo (Gerente, Garçom, etc.)

Ingrediente: ID_Ingrediente, Nome, Estoque_Atual, Unidade_Medida, Estoque_Minimo
