package com.mackenzie.lunchexpress.controller;

import com.mackenzie.lunchexpress.dto.request.PublicarServicoRequest;
import com.mackenzie.lunchexpress.dto.response.ServicoResponse;
import com.mackenzie.lunchexpress.service.ServicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService servicoService;

    @PostMapping
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<ServicoResponse> publicar(
            @Valid @RequestBody PublicarServicoRequest request,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicoService.publicarServico(request, token));
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<List<ServicoResponse>> meuServicos(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(servicoService.listarMeusServicos(token));
    }

    @GetMapping
    public ResponseEntity<List<ServicoResponse>> listarTodos() {
        return ResponseEntity.ok(servicoService.listarTodos());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ServicoResponse>> listarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(servicoService.listarPorCategoria(categoria));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<ServicoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PublicarServicoRequest request,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(servicoService.atualizar(id, request, token));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        servicoService.deletar(id, token);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/disponibilidade")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<ServicoResponse> alterarDisponibilidade(
            @PathVariable Long id,
            @RequestParam Boolean disponivel,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(servicoService.alterarDisponibilidade(id, disponivel, token));
    }
}
