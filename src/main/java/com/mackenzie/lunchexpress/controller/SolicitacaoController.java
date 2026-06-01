package com.mackenzie.lunchexpress.controller;

import com.mackenzie.lunchexpress.dto.request.AceitarSolicitacaoRequest;
import com.mackenzie.lunchexpress.dto.request.SolicitarServicoRequest;
import com.mackenzie.lunchexpress.dto.response.ServicoResponse;
import com.mackenzie.lunchexpress.dto.response.SolicitacaoResponse;
import com.mackenzie.lunchexpress.service.SolicitacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<SolicitacaoResponse> criar(
            @Valid @RequestBody SolicitarServicoRequest request,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(solicitacaoService.criarSolicitacao(request, token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoResponse> obterDetalhes(@PathVariable Long id) {
        return ResponseEntity.ok(solicitacaoService.obterDetalhes(id));
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<SolicitacaoResponse>> minhasSolicitacoes(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(solicitacaoService.listarSolicitacoesCliente(token));
    }

    @GetMapping("/{id}/matching")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<ServicoResponse>> matching(@PathVariable Long id) {
        return ResponseEntity.ok(solicitacaoService.obterMatchingPrestadores(id));
    }

    @GetMapping("/pendentes")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<List<SolicitacaoResponse>> listarPendentes() {
        return ResponseEntity.ok(solicitacaoService.listarPendentes());
    }

    @GetMapping("/prestador/minhas")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<List<SolicitacaoResponse>> solicitacoesPrestador(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(solicitacaoService.listarSolicitacoesPrestador(token));
    }

    @PutMapping("/{id}/aceitar")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<SolicitacaoResponse> aceitar(
            @PathVariable Long id,
            @Valid @RequestBody AceitarSolicitacaoRequest request,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(solicitacaoService.aceitarSolicitacao(id, request, token));
    }

    @PutMapping("/{id}/rastreio/avancar")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<SolicitacaoResponse> avancarRastreio(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(solicitacaoService.avancarRastreio(id, token));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<SolicitacaoResponse> cancelar(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(solicitacaoService.cancelar(id, token));
    }
}
