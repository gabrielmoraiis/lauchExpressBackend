package com.mackenzie.lunchexpress.controller;

import com.mackenzie.lunchexpress.dto.request.OrcamentoRequest;
import com.mackenzie.lunchexpress.dto.response.OrcamentoResponse;
import com.mackenzie.lunchexpress.service.OrcamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    @PostMapping("/api/solicitacoes/{id}/orcamentos")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<OrcamentoResponse> enviar(
            @PathVariable Long id,
            @Valid @RequestBody OrcamentoRequest request,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orcamentoService.enviarOrcamento(id, request, token));
    }

    @GetMapping("/api/solicitacoes/{id}/orcamentos")
    public ResponseEntity<List<OrcamentoResponse>> listar(@PathVariable Long id) {
        return ResponseEntity.ok(orcamentoService.listarPorSolicitacao(id));
    }

    @PutMapping("/api/orcamentos/{id}/aprovar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<OrcamentoResponse> aprovar(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(orcamentoService.aprovarOrcamento(id, token));
    }
}
