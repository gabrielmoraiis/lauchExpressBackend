package com.mackenzie.lunchexpress.controller;

import com.mackenzie.lunchexpress.dto.response.LogResponse;
import com.mackenzie.lunchexpress.service.LogAtividadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogAtividadeController {

    private final LogAtividadeService logService;

    @GetMapping
    public ResponseEntity<List<LogResponse>> listar(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(logService.listarParaUsuario(token));
    }
}
