package com.mackenzie.lunchexpress.service;

import com.mackenzie.lunchexpress.dto.request.CadastroRequest;
import com.mackenzie.lunchexpress.dto.request.LoginRequest;
import com.mackenzie.lunchexpress.dto.response.JwtResponse;
import com.mackenzie.lunchexpress.entity.Usuario;
import com.mackenzie.lunchexpress.exception.BadRequestException;
import com.mackenzie.lunchexpress.repository.UsuarioRepository;
import com.mackenzie.lunchexpress.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public JwtResponse cadastrar(CadastroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email já cadastrado: " + request.getEmail());
        }

        Usuario usuario = Usuario.builder()
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .nome(request.getNome())
                .telefone(request.getTelefone())
                .cidade(request.getCidade())
                .estado(request.getEstado())
                .cep(request.getCep())
                .logradouro(request.getLogradouro())
                .numero(request.getNumero())
                .bairro(request.getBairro())
                .perfil(request.getPerfil())
                .build();

        Usuario salvo = usuarioRepository.save(usuario);
        String token = jwtTokenProvider.gerarTokenPorEmail(salvo.getEmail());

        return JwtResponse.builder()
                .token(token)
                .usuarioId(salvo.getId())
                .email(salvo.getEmail())
                .nome(salvo.getNome())
                .perfil(salvo.getPerfil())
                .build();
    }

    public JwtResponse autenticar(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        String token = jwtTokenProvider.gerarToken(authentication);
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));

        return JwtResponse.builder()
                .token(token)
                .usuarioId(usuario.getId())
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .perfil(usuario.getPerfil())
                .build();
    }

    public Usuario obterPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado: " + email));
    }

    public Usuario obterPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado: " + id));
    }
}
