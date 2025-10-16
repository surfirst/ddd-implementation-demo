package com.example.enrollment.application.registration;

import com.example.enrollment.domain.emailtemplates.EmailTemplateManager;
import com.example.enrollment.domain.enrollmentprocess.*;
import com.example.enrollment.domain.enrollmentprocess.ports.CaptchaService;
import com.example.enrollment.domain.enrollmentprocess.ports.GlobalSettings;
import com.example.enrollment.domain.enrollmentprocess.ports.MailService;
import com.example.enrollment.domain.enrollmentprocess.ports.PlayerManagementProvider;
import com.example.enrollment.domain.enrollmentprocess.ports.WalletService;
import com.example.enrollment.domain.shared.SupportedLanguage;
import com.example.enrollment.domain.time.DateTimeProvider;

import java.util.*;

public class RegistrationService {
    private final CaptchaService captchaService;
    private final MailService mailService;
    private final RegistrationEnrollmentRepository enrollmentRepository;
    private final PlayerManagementProvider playerManagementProvider;
    private final EmailTemplateManager templateManager;
    private final Set<WalletService> walletServices;
    private final GlobalSettings globalSettings;
    private final DateTimeProvider dateTimeProvider;

    public RegistrationService(CaptchaService captchaService,
                               MailService mailService,
                               RegistrationEnrollmentRepository enrollmentRepository,
                               PlayerManagementProvider playerManagementProvider,
                               EmailTemplateManager templateManager,
                               Set<WalletService> walletServices,
                               GlobalSettings globalSettings,
                               DateTimeProvider dateTimeProvider) {
        this.captchaService = Objects.requireNonNull(captchaService);
        this.mailService = Objects.requireNonNull(mailService);
        this.enrollmentRepository = Objects.requireNonNull(enrollmentRepository);
        this.playerManagementProvider = Objects.requireNonNull(playerManagementProvider);
        this.templateManager = Objects.requireNonNull(templateManager);
        this.walletServices = Objects.requireNonNull(walletServices);
        this.globalSettings = Objects.requireNonNull(globalSettings);
        this.dateTimeProvider = Objects.requireNonNull(dateTimeProvider);
    }

    public RegistrationEnrollment verifyCaptchaAndStartEnrollment(PlayerInfo playerInfo, String captcha, SupportedLanguage lang) {
        if (playerInfo == null || captcha == null) {
            throw new IllegalArgumentException("playerInfo and captcha are required");
        }
        try {
            captchaService.validate(captcha);
        } catch (RuntimeException ex) {
            throw new com.example.enrollment.domain.exceptions.InvalidCaptchaException(ex.getMessage());
        }

        // duplication checks
        EnrolledPlayer dup = playerManagementProvider.duplicationCheck(playerInfo);
        if (dup != null) {
            throw new com.example.enrollment.domain.exceptions.ExistingIDNDobEnrollmentException("Existing ID & DoB");
        }

        RegistrationEnrollment enrollment = RegistrationEnrollment.start(playerInfo, dateTimeProvider);
        enrollmentRepository.addEnrollment(enrollment);

        Map<String, Object> paras = new HashMap<>();
        paras.put("otp", enrollment.getOtp().getPassword());
        var rendered = templateManager.render("otp", paras, lang);
        mailService.send(playerInfo.getEmail(), rendered.subject(), rendered.content());

        return enrollment;
    }

    public void verifyOtp(String enrollmentId, String otpPassword, SupportedLanguage lang) {
        RegistrationEnrollment enrollment = enrollmentRepository.getEnrollment(enrollmentId);
        if (enrollment == null) throw new IllegalArgumentException("enrollment not found");
        if (!Objects.equals(enrollment.getOtp().getPassword(), otpPassword)) {
            throw new IllegalArgumentException("invalid otp");
        }

        EnrolledPlayer ep = playerManagementProvider.enrollPlayer(enrollment.getPlayerInfo());
        enrollment.setEnrolledPlayer(ep);
        enrollmentRepository.addEnrolledPlayer(enrollment.getId(), ep);

        Map<String, String> detail = playerManagementProvider.getPlayerDetail(ep.getCmsId());
        Map<String, Object> paras = new HashMap<>();
        paras.put("player_id", ep.getCmsId());
        if (detail != null) {
            if (detail.containsKey(PlayerManagementProvider.CMS_PLAYER_NAME)) {
                paras.put("player_name", detail.get(PlayerManagementProvider.CMS_PLAYER_NAME));
            }
            if (detail.containsKey(PlayerManagementProvider.CMS_RANK)) {
                paras.put("rank", detail.get(PlayerManagementProvider.CMS_RANK));
            }
        }
        for (WalletService w : walletServices) {
            try {
                String link = w.getLink(ep.getCmsId(), (String) paras.getOrDefault("player_name", ""));
                paras.put(w.name(), link);
            } catch (Exception ex) {
                if (globalSettings.logger() != null) {
                    globalSettings.logger().warn("Wallet link generation failed", ex);
                }
            }
        }

        var rendered = templateManager.render("welcome", paras, lang);
        mailService.send(enrollment.getPlayerInfo().getEmail(), rendered.subject(), rendered.content());
    }
}
