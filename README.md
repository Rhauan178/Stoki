<p align="center">
  <img src=".github/assets/logo-stoki.png" alt="Logo da Stoki" width="200"/>
</p>

<h1 align="center">
  Stoki - Gestão Inteligente para Restaurantes
</h1>

<p align="center">
  O sistema completo para otimizar o gerenciamento de mesas, pedidos e ingredientes de restaurantes e bares.
  <br />
  <br />
  <img alt="Status do Build" src="https://img.shields.io/github/actions/workflow/status/seu-usuario/seu-repo/main.yml?branch=main&style=for-the-badge">
  <img alt="Licença" src="https://img.shields.io/github/license/seu-usuario/seu-repo?style=for-the-badge">
  <img alt="Versão"img src="https://img.shields.io/github/v/release/seu-usuario/seu-repo?style=for-the-badge">
</p>

---

## 📋 Índice

* [Sobre o Projeto](#-sobre-o-projeto)
* [✨ Funcionalidades](#-funcionalidades)
* [📸 Screenshots](#-screenshots)
* [🚀 Tecnologias Usadas](#-tecnologias-usadas)
* [⭐ Pilares de Qualidade](#-pilares-de-qualidade)
* [🗃️ Modelo de Dados](#️-modelo-de-dados)
* [🏁 Começando](#-começando)
* [🤝 Como Contribuir](#-como-contribuir)

---

## 🧐 Sobre o Projeto

Este projeto modela e desenvolve um sistema de gerenciamento completo para restaurantes e bares. A solução foca em otimizar a operação, integrando o atendimento ao cliente diretamente na mesa com um controle de estoque inteligente.

> **O Problema:** Erros operacionais, falta de comunicação entre salão e cozinha, e dificuldade em gerenciar o estoque de ingredientes em tempo real.
>
> **A Solução:** Um sistema centralizado que digitaliza o cardápio, automatiza pedidos, dá baixa em insumos e fornece uma visão clara da operação para os gestores.

## ✨ Funcionalidades

Nosso sistema é dividido em módulos que resolvem problemas específicos do seu negócio:

* 📱 **Cardápio Digital (RF01):** Clientes acessam o cardápio via QR Code, fazem pedidos pelo smartphone e os enviam direto para a cozinha.
* 📦 **Gestão de Estoque (RF02):** Cadastro de ingredientes, associação aos pratos (ficha técnica) e baixa automática de insumos a cada pedido.
* 🔔 **Alertas de Estoque:** Emissão de alertas automáticos quando um ingrediente atinge o estoque mínimo.
* 🖥️ **Painel Operacional (RF03):** Telas otimizadas para a cozinha (status de pedidos) e garçons (status das mesas e notificações).
* 🗺️ **Gerenciamento do Salão (RF04):** Controle visual do status das mesas (livre, ocupada, reservada) para facilitar o fluxo de clientes.
* 📊 **Módulo Administrativo (RF05):** Ferramentas para gerentes cadastrarem/alterarem itens, gerenciarem contas de funcionários e acessarem relatórios de vendas.

## 📸 Screenshots

<p align="center">
  <img src=".github/assets/demo.gif" alt="Demonstração da Aplicação Stoki" width="80%">
</p>

| Tela de Pedidos (Cliente) | Dashboard (Gerente) |
| :---: | :---: |
| ![Tela de Pedidos](.github/assets/screen-pedidos.png) | ![Dashboard](.github/assets/screen-dashboard.png) |


## 🚀 Tecnologias Usadas

O Stoki é construído com tecnologias modernas, escaláveis e robustas:

| Componente | Tecnologia |
| :--- | :--- |
| **Backend** | ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white) |
| **Frontend** | ![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB) ![Next.js](https://img.shields.io/badge/Next.js-000000?style=for-the-badge&logo=nextdotjs&logoColor=white) |
| **Banco de Dados** | ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white) |

## ⭐ Pilares de Qualidade

Mais do que funcionalidades, garantimos uma base sólida (Requisitos Não Funcionais):

* usability: **Usabilidade (RNF01):** Interface intuitiva para clientes (sem necessidade de aprendizado) e ágil para a equipe.
* ⚡ **Desempenho (RNF02):** Respostas rápidas, com carregamento de cardápio e envio de pedidos em menos de 2 segundos.
* 🔒 **Segurança (RNF03):** Acesso protegido por autenticação e autorização baseada em cargos.
* 📈 **Disponibilidade (RNF04):** Alta disponibilidade (99.5% de uptime) para garantir que o sistema funcione durante todo o horário de pico.
* 🌐 **Compatibilidade (RNF05):** Aplicação do cliente 100% responsiva para os principais navegadores de smartphones.

## 🗃️ Modelo de Dados

A arquitetura do banco de dados foi projetada para refletir um ambiente dinâmico, com as seguintes entidades principais:

* `Mesa` (ID_Mesa, Numero, Status, QRCode)
* `ItemCardapio` (ID_Item, Nome, Descricao, Preco, Categoria, Disponivel)
* `Ingrediente` (ID_Ingrediente, Nome, Estoque_Atual, Unidade_Medida, Estoque_Minimo)
* `Pedido` (ID_Pedido, ID_Mesa, Status, Data_Hora)
* `Usuario` (ID_Usuario, Nome, Login, Senha, Cargo)

A entidade `Usuario` é generalizada e pode ser especializada em `Gerente`, `Garcom` e `Cozinheiro`, cada um com permissões distintas.

## 🏁 Começando

Para rodar este projeto localmente, siga os passos:

### Pré-requisitos

* Java 17+
* Node.js 18+
* Um SGBD (Ex: PostgreSQL)

### Instalação

1.  Clone o repositório
    ```sh
    git clone [https://github.com/seu-usuario/seu-repo.git](https://github.com/seu-usuario/seu-repo.git)
    cd seu-repo
    ```
2.  Inicie o Backend (na pasta `/backend`)
    ```sh
    ./mvnw spring-boot:run
    ```
3.  Inicie o Frontend (na pasta `/frontend`)
    ```sh
    npm install
    npm run dev
    ```

## 🤝 Como Contribuir

Contribuições são o que tornam a comunidade open-source um lugar incrível para aprender e criar. Qualquer contribuição que você fizer será **muito bem-vinda**.

1.  Faça um *Fork* do projeto
2.  Crie uma *Branch* para sua feature (`git checkout -b feature/AmazingFeature`)
3.  Faça o *Commit* de suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4.  Faça o *Push* para a Branch (`git push origin feature/AmazingFeature`)
5.  Abra um *Pull Request*

---

Distribuído sob a Licença MIT. Veja `LICENSE.txt` para mais informações.
