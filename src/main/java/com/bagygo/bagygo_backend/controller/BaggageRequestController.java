package com.bagygo.bagygo_backend.controller;

import com.bagygo.bagygo_backend.dto.request.CreateBaggageRequestRequest;
import com.bagygo.bagygo_backend.dto.request.RespondToRequestRequest;
import com.bagygo.bagygo_backend.dto.response.BaggageRequestResponse;
import com.bagygo.bagygo_backend.entity.User;
import com.bagygo.bagygo_backend.service.BaggageRequestService;
import com.bagygo.bagygo_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class BaggageRequestController {

    private final BaggageRequestService service;
    private final UserService userService;

    @GetMapping("/open")
    public List<BaggageRequestResponse> getOpenRequests() {
        return service.getOpenRequests();
    }

    @GetMapping("/{id}")
    public BaggageRequestResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<BaggageRequestResponse> getMyRequests(Authentication auth) {
        User user = userService.getCurrentUser(auth.getName());
        return service.getMySenderRequests(user);
    }

    @PostMapping
    @PreAuthorize("hasRole('EXPEDITEUR')")
    public BaggageRequestResponse createRequest(@RequestBody CreateBaggageRequestRequest req, Authentication auth) {
        User user = userService.getCurrentUser(auth.getName());
        return service.create(req, user);
    }

    @GetMapping("/transporter")
    @PreAuthorize("hasRole('TRANSPORTEUR')")
    public List<BaggageRequestResponse> getRequestsForTransporter(Authentication auth) {
        User user = userService.getCurrentUser(auth.getName());
        return service.getRequestsForTransporter(user);
    }

    @PostMapping("/{id}/respond")
    @PreAuthorize("hasRole('TRANSPORTEUR')")
    public BaggageRequestResponse respondToRequest(
            @PathVariable Long id,
            @RequestBody RespondToRequestRequest req,
            Authentication auth) {
        User user = userService.getCurrentUser(auth.getName());
        return service.respondToRequest(id, req, user);
    }

    @PatchMapping("/{id}/accept")
    public BaggageRequestResponse acceptRequest(
            @PathVariable Long id,
            Authentication auth) {
        User user = userService.getCurrentUser(auth.getName());
        return service.acceptRequest(id, user);
    }

    @PatchMapping("/{id}/reject")
    public BaggageRequestResponse rejectRequest(
            @PathVariable Long id,
            Authentication auth) {
        User user = userService.getCurrentUser(auth.getName());
        return service.rejectRequest(id, user);
    }

    @PatchMapping("/{id}/status")
    public BaggageRequestResponse updateStatus(
            @PathVariable Long id,
            @RequestParam com.bagygo.bagygo_backend.enums.RequestStatus status,
            Authentication auth) {
        User user = userService.getCurrentUser(auth.getName());
        return service.updateStatus(id, status, user);
    }

    @DeleteMapping("/{id}")
    public BaggageRequestResponse cancelRequest(
            @PathVariable Long id,
            Authentication auth) {
        User user = userService.getCurrentUser(auth.getName());
        return service.cancel(id, user);
    }
}