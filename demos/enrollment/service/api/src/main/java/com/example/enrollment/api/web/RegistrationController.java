package com.example.enrollment.api.web;

import com.example.enrollment.application.registration.RegistrationService;
import com.example.enrollment.domain.enrollmentprocess.Enrollment;
import com.example.enrollment.domain.enrollmentprocess.PlayerInfo;
import com.example.enrollment.domain.shared.SupportedLanguage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/start")
    public ResponseEntity<StartResponse> start(@RequestBody StartRequest request,
                                               @RequestParam(name = "lang", defaultValue = "EN") String lang) {
        SupportedLanguage language = SupportedLanguage.valueOf(lang);
        PlayerInfo player = PlayerInfo.create(request.email(), request.fullName());
        Enrollment enrollment = registrationService.verifyCaptchaAndStartEnrollment(player, request.captcha(), language);
        return ResponseEntity.created(URI.create("/api/registration/" + enrollment.getId()))
                .body(new StartResponse(enrollment.getId()));
    }

    @PostMapping("/{id}/verify-otp")
    public ResponseEntity<Void> verifyOtp(@PathVariable("id") String id,
                                          @RequestBody VerifyOtpRequest request,
                                          @RequestParam(name = "lang", defaultValue = "EN") String lang) {
        SupportedLanguage language = SupportedLanguage.valueOf(lang);
        registrationService.verifyOtp(id, request.otp(), language);
        return ResponseEntity.ok().build();
    }

    public record StartRequest(String email, String fullName, String captcha) {}
    public record StartResponse(String id) {}
    public record VerifyOtpRequest(String otp) {}
}
