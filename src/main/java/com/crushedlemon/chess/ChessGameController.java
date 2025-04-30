package com.crushedlemon.chess;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChessGameController {
    @GetMapping("/")
    public String hello() {
        return "Hello";
    }
}
