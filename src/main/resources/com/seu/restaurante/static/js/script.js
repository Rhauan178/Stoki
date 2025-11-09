async function buscarDadosDoBackend() {
    const dadosDiv = document.getElementById('dados-api');
    dadosDiv.innerHTML = 'Buscando dados no servidor Java...';

    try {
        const urlAPI = "http://localhost:8080/api/restaurante/contas"; 
        const resposta = await fetch(urlAPI);

        if (!resposta.ok) {
            throw new Error(`Erro na rede ou servidor. Status: ${resposta.status}`);
        }

        
        const dados = await resposta.json();

        
        if (dados.length === 0) {
            dadosDiv.innerHTML = '✅ **Sucesso!** O servidor está conectado, mas **nenhuma conta paga** foi encontrada no banco de dados.';
        } else {
            
            let htmlContent = '<h2>✅ Contas Encontradas:</h2><ul>';
            
           
            dados.forEach(conta => {
                
                htmlContent += `
                    <li>
                        ID da Conta: ${conta.id} 
                        | Valor Total: R$ ${conta.valorTotal} 
                        | Status: ${conta.status}
                    </li>
                `;
            });

            htmlContent += '</ul>';
            dadosDiv.innerHTML = htmlContent;
        }
      

    } catch (error) {
        console.error('Falha ao buscar a API:', error);
        dadosDiv.innerHTML = `❌ Erro ao conectar ao Backend: **${error.message}**. Verifique se o servidor Java está rodando.`;
    }
}


buscarDadosDoBackend();