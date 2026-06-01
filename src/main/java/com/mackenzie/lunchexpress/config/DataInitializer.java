package com.mackenzie.lunchexpress.config;

import com.mackenzie.lunchexpress.entity.Servico;
import com.mackenzie.lunchexpress.entity.Usuario;
import com.mackenzie.lunchexpress.enums.PerfilUsuario;
import com.mackenzie.lunchexpress.repository.ServicoRepository;
import com.mackenzie.lunchexpress.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ServicoRepository servicoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Usuario escola     = criarSeNaoExistir("escola@teste.com",     "123456", "Escola Municipal de Teste", PerfilUsuario.CLIENTE,
                "01310-100", "Av. Paulista", "1000", "Bela Vista");
        Usuario fornecedor = criarSeNaoExistir("fornecedor@teste.com", "123456", "Fornecedor de Teste",       PerfilUsuario.PRESTADOR,
                "04026-000", "Rua Vergueiro",  "500",  "Vila Mariana");

        if (servicoRepository.count() == 0) {
            seed(fornecedor, "Arroz Branco Tipo 1",       "graos",       4.90,  500, "kg");
            seed(fornecedor, "Feijao Carioca",            "graos",       6.50,  300, "kg");
            seed(fornecedor, "Macarrao Espaguete",        "graos",       3.80,  200, "kg");
            seed(fornecedor, "Frango Inteiro Resfriado",  "proteinas",  12.90,  150, "kg");
            seed(fornecedor, "Ovo Caipira",               "proteinas",  18.00,  500, "duzia");
            seed(fornecedor, "Carne Moida Bovina",        "proteinas",  28.00,  100, "kg");
            seed(fornecedor, "Cenoura",                   "hortifruti",  2.50,  200, "kg");
            seed(fornecedor, "Chuchu",                    "hortifruti",  1.80,  150, "kg");
            seed(fornecedor, "Alface Crespa",             "hortifruti",  2.00,  300, "unidade");
            seed(fornecedor, "Leite Integral UHT",        "laticinios",  4.50,  400, "L");
            seed(fornecedor, "Queijo Mussarela Fatiado",  "laticinios", 38.00,   80, "kg");
            seed(fornecedor, "Suco de Laranja Natural",   "bebidas",     5.00,  200, "L");
            seed(fornecedor, "Pao Frances",               "panificacao",  1.50, 1000, "unidade");
            seed(fornecedor, "Bolo de Cenoura Caseiro",   "panificacao", 25.00,   30, "unidade");
            seed(fornecedor, "Azeite de Oliva Extra Virgem", "outros",  32.00,   60, "L");
        }
    }

    private Usuario criarSeNaoExistir(String email, String senha, String nome, PerfilUsuario perfil,
                                       String cep, String logradouro, String numero, String bairro) {
        return usuarioRepository.findByEmail(email).orElseGet(() ->
            usuarioRepository.save(Usuario.builder()
                    .email(email)
                    .senha(passwordEncoder.encode(senha))
                    .nome(nome)
                    .telefone("(11) 99999-9999")
                    .cep(cep)
                    .logradouro(logradouro)
                    .numero(numero)
                    .bairro(bairro)
                    .cidade("São Paulo")
                    .estado("SP")
                    .perfil(perfil)
                    .build())
        );
    }

    private void seed(Usuario prestador, String descricao, String categoria, double preco, int qtd, String unidade) {
        servicoRepository.save(Servico.builder()
                .prestador(prestador)
                .descricao(descricao)
                .categoria(categoria)
                .preco(preco)
                .quantidadeDisponivel(qtd)
                .unidadeMedida(unidade)
                .build());
    }
}
