package com.seu.restaurante;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin; // Importe para permitir a conexão do front-end

// 1. Marca a classe como um controlador REST
@RestController
// 2. Define o caminho base para todos os métodos desta classe
@RequestMapping("/api/restaurante") 
// 3. 🚨 Resolve o problema de CORS (Cross-Origin Resource Sharing)
@CrossOrigin(origins = "http://127.0.0.1:5500") // Use a porta que o Live Server do VS Code usar
public class RestauranteController {

    // Exemplo de Injeção de Dependência (se você usar Spring)
    // @Autowired
    // private ContaDAO contaDAO; // Ou a Service que chama o DAO

    // 4. Cria o seu primeiro Endpoint GET
    // O endpoint completo será: http://localhost:8080/api/restaurante/contas
    @GetMapping("/contas")
    public String listarContas() {
        // Por enquanto, apenas retorne um JSON simples para teste.
        // Depois, você chamará seu ContaDAO ou Service aqui.
        return "{\"status\": \"OK\", \"mensagem\": \"Dados recebidos do Java!\"}";
        
        // Exemplo real:
        // return contaDAO.buscarTodasAsContas();
    }
}