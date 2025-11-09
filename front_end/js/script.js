// Função assíncrona para buscar dados da API Java
async function buscarDadosDoBackend() {
    const dadosDiv = document.getElementById('dados-api');
    dadosDiv.innerHTML = 'Buscando dados no servidor Java...';

    try {
        const urlAPI = "http://localhost:8080/api/restaurante/contas";

        // Usa 'fetch' para fazer a requisição HTTP
        const resposta = await fetch(urlAPI);
        
        // Verifica se a resposta foi bem-sucedida (código 200-299)
        if (!resposta.ok) {
            throw new Error(`Erro na rede ou servidor. Status: ${resposta.status}`);
        }

        // Converte a resposta para JSON
        const dados = await resposta.json();

        // Exibe os dados no HTML
        dadosDiv.innerHTML = `✅ Sucesso! Mensagem do servidor: <strong>${dados.mensagem}</strong>`;

    } catch (erro) {
        // Exibe a mensagem de erro no console e na página
        console.error('Falha ao buscar a API:', erro);
        dadosDiv.innerHTML = `❌ Erro ao conectar ao Backend: ${erro.message}. Verifique se o servidor Java está rodando e se há problemas de **CORS**.`;
    }
}

// Chama a função assim que o script é carregado
buscarDadosDoBackend();