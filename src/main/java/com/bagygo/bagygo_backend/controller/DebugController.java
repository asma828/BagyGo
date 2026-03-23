package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.service.BaggageRequestService;
import com.bagygo.bagygo_backend.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import java.io.PrintWriter;
import java.io.StringWriter;

@RestController
@RequiredArgsConstructor
public class DebugController {
    private final BaggageRequestService service;
    private final UserRepository userRepo;

    @GetMapping("/api/debug/accept")
    public String accept() {
        try {
            service.acceptRequest(26L, userRepo.findById(23L).get());
            return "SUCCESS";
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            return "EXCEPTION: " + e.getClass().getName() + "\n" + e.getMessage() + "\n" + sw.toString();
        }
    }
}
