package com.seu.restaurante;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired; 
import java.util.List;
import java.time.LocalDate; // Necessário para os parâmetros do carregarContasPagas

@RestController
@RequestMapping("/api/restaurante")
// Permite que o front-end (porta 5500) acesse esta API
@CrossOrigin(origins = "http://127.0.0.1:5500") 
public class RestauranteController {

    // 1. INJEÇÃO DE DEPENDÊNCIA (Agora deve funcionar com @Repository no DAO)
    @Autowired
    private ContaDAO contaDAO; 

    @GetMapping("/contas")
    // O tipo de retorno agora é a lista de objetos Conta
    public List<Conta> listarContas() {
        
        // 2. CHAMA UM MÉTODO EXISTENTE NO CONTA DAO
        // Este método busca contas pagas, exigindo parâmetros de data e ID (usamos null para teste)
        
        LocalDate dataInicio = null;
        LocalDate dataFim = null;
        Integer funcionarioId = null;
        
        return contaDAO.carregarContasPagas(dataInicio, dataFim, funcionarioId);
    }
}