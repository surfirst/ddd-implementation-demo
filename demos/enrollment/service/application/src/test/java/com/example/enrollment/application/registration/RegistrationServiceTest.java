package com.example.enrollment.application.registration;

import com.example.enrollment.domain.emailtemplates.EmailTemplate;
import com.example.enrollment.domain.emailtemplates.EmailTemplateManager;
import com.example.enrollment.domain.emailtemplates.EmailTemplateRepository;
import com.example.enrollment.domain.enrollmentprocess.EnrolledPlayer;
import com.example.enrollment.domain.enrollmentprocess.Enrollment;
import com.example.enrollment.domain.enrollmentprocess.PlayerInfo;
import com.example.enrollment.domain.enrollmentprocess.RegistrationEnrollmentRepository;
import com.example.enrollment.domain.enrollmentprocess.ports.CaptchaService;
import com.example.enrollment.domain.enrollmentprocess.ports.GlobalSettings;
import com.example.enrollment.domain.enrollmentprocess.ports.Logger;
import com.example.enrollment.domain.enrollmentprocess.ports.MailService;
import com.example.enrollment.domain.enrollmentprocess.ports.PlayerManagementProvider;
import com.example.enrollment.domain.enrollmentprocess.ports.WalletService;
import com.example.enrollment.domain.shared.SupportedLanguage;
import com.example.enrollment.domain.time.DateTimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RegistrationServiceTest {

    CaptchaService captchaService;
    MailService mailService;
    RegistrationEnrollmentRepository repo;
    PlayerManagementProvider pm;
    EmailTemplateRepository templateRepo;
    EmailTemplateManager manager;
    Set<WalletService> wallets;
    GlobalSettings globalSettings;
    Logger logger;
    DateTimeProvider dateTimeProvider;

    @BeforeEach
    void setup() {
        captchaService = mock(CaptchaService.class);
        mailService = mock(MailService.class);
        repo = mock(RegistrationEnrollmentRepository.class);
        pm = mock(PlayerManagementProvider.class);
        templateRepo = mock(EmailTemplateRepository.class);
        wallets = new HashSet<>();
        globalSettings = mock(GlobalSettings.class);
        logger = mock(Logger.class);
        when(globalSettings.logger()).thenReturn(logger);
        dateTimeProvider = mock(DateTimeProvider.class);
        when(dateTimeProvider.nowUtc()).thenReturn(Instant.now());
    }

    EmailTemplateManager managerWith(String subject, String body) {
        when(templateRepo.findTemplateByNameAndLanguage(eq("otp"), eq(SupportedLanguage.EN)))
                .thenReturn(new EmailTemplate("otp", subject, body, SupportedLanguage.EN));
        when(templateRepo.findTemplateByNameAndLanguage(eq("welcome"), eq(SupportedLanguage.EN)))
                .thenReturn(new EmailTemplate("welcome", subject, body, SupportedLanguage.EN));
        return new EmailTemplateManager(templateRepo, SupportedLanguage.EN);
    }

    PlayerInfo playerInfo() {
        return PlayerInfo.create("surfirst@yeah.net", "Fei Yi");
    }

    RegistrationService serviceWith(EmailTemplateManager m) {
        return new RegistrationService(captchaService, mailService, repo, pm, m, wallets, globalSettings, dateTimeProvider);
    }

    @Test
    void captchaShouldBeVerified() {
        EmailTemplateManager m = managerWith("otp", "$$otp$$");
        RegistrationService s = serviceWith(m);
        doThrow(new RuntimeException("Reason")).when(captchaService).validate(anyString());

        assertThrows(IllegalArgumentException.class, () -> s.verifyCaptchaAndStartEnrollment(null, null, SupportedLanguage.EN));
        assertThrows(IllegalArgumentException.class, () -> s.verifyCaptchaAndStartEnrollment(playerInfo(), null, SupportedLanguage.EN));
        assertThrows(com.example.enrollment.domain.exceptions.InvalidCaptchaException.class,
                () -> s.verifyCaptchaAndStartEnrollment(playerInfo(), "c", SupportedLanguage.EN));
    }

    @Test
    void emailShouldBeSentWithValidCaptcha() {
        EmailTemplateManager m = managerWith("otp", "$$otp$$");
        RegistrationService s = serviceWith(m);

        ArgumentCaptor<Enrollment> captor = ArgumentCaptor.forClass(Enrollment.class);

        Enrollment enrollment = s.verifyCaptchaAndStartEnrollment(playerInfo(), "captcha", SupportedLanguage.EN);
        verify(repo, times(1)).addEnrollment(captor.capture());
        verify(mailService, times(1)).send(eq("surfirst@yeah.net"), eq("otp"), eq(captor.getValue().getOtp().getPassword()));
        assertNotNull(enrollment);
    }

    @Test
    void exceptionShouldBeThrownIfIDAndDoB() {
        EmailTemplateManager m = managerWith("otp", "$$otp$$");
        RegistrationService s = serviceWith(m);
        when(pm.duplicationCheck(any())).thenReturn(new EnrolledPlayer("mock_id", null));
        assertThrows(com.example.enrollment.domain.exceptions.ExistingIDNDobEnrollmentException.class,
                () -> s.verifyCaptchaAndStartEnrollment(playerInfo(), "captcha", SupportedLanguage.EN));
    }

    @Test
    void playerShouldBeEnrolledAfterVerification() {
        EmailTemplateManager m = managerWith("otp", "$$player_id$$");
        RegistrationService s = serviceWith(m);

        ArgumentCaptor<Enrollment> captor = ArgumentCaptor.forClass(Enrollment.class);
        Enrollment enrollment = s.verifyCaptchaAndStartEnrollment(playerInfo(), "captcha", SupportedLanguage.EN);
        verify(repo).addEnrollment(captor.capture());

        when(repo.getEnrollment(eq(captor.getValue().getId()))).thenReturn(captor.getValue());
        when(pm.enrollPlayer(any())).thenReturn(new EnrolledPlayer("mock_id", null));
        when(pm.getPlayerDetail(anyString())).thenReturn(Collections.emptyMap());

        s.verifyOtp(enrollment.getId(), enrollment.getOtp().getPassword(), SupportedLanguage.EN);

        verify(repo, times(1)).addEnrolledPlayer(eq(enrollment.getId()), any());
    }

    @Test
    void playerShouldBeEnrolledIfExistingEmailAfterOtp() {
        EmailTemplateManager m = managerWith("welcome", "$$player_id$$");
        RegistrationService s = serviceWith(m);

        Enrollment enrollment = s.verifyCaptchaAndStartEnrollment(playerInfo(), "captcha", SupportedLanguage.EN);
        when(repo.getEnrollment(enrollment.getId())).thenReturn(enrollment);
        when(pm.enrollPlayer(any())).thenReturn(new EnrolledPlayer("mock_id", null));
        when(pm.getPlayerDetail(anyString())).thenReturn(Collections.emptyMap());

        s.verifyOtp(enrollment.getId(), enrollment.getOtp().getPassword(), SupportedLanguage.EN);
        verify(mailService, times(1)).send(eq("surfirst@yeah.net"), eq("welcome"), eq("mock_id"));
        verify(repo, times(1)).addEnrolledPlayer(eq(enrollment.getId()), any());
    }

    @Test
    void welcomeEmailShouldUseEnrolledName() {
        EmailTemplateManager m = managerWith("welcome", "$$player_name$$|$$rank$$");
        RegistrationService s = serviceWith(m);

        Enrollment enrollment = s.verifyCaptchaAndStartEnrollment(playerInfo(), "captcha", SupportedLanguage.EN);
        when(repo.getEnrollment(enrollment.getId())).thenReturn(enrollment);
        when(pm.enrollPlayer(any())).thenReturn(new EnrolledPlayer("mock_id", null));
        when(pm.getPlayerDetail(anyString())).thenReturn(new HashMap<>() {{
            put(PlayerManagementProvider.CMS_PLAYER_NAME, "cms_name");
            put(PlayerManagementProvider.CMS_RANK, "cms_rank");
        }});

        s.verifyOtp(enrollment.getId(), enrollment.getOtp().getPassword(), SupportedLanguage.EN);
        verify(mailService, times(1)).send(eq("surfirst@yeah.net"), eq("welcome"), eq("cms_name|cms_rank"));
    }

    @Test
    void welcomeEmailShouldHaveWalletLink() {
        EmailTemplateManager m = managerWith("welcome", "$$google_wallet$$|$$apple_wallet$$");

        WalletService google = mock(WalletService.class);
        when(google.name()).thenReturn("google_wallet");
        when(google.getLink(anyString(), anyString())).thenReturn("google|mock_id|cms_name");
        WalletService apple = mock(WalletService.class);
        when(apple.name()).thenReturn("apple_wallet");
        when(apple.getLink(anyString(), anyString())).thenReturn("apple|mock_id|cms_name");
        WalletService broken = mock(WalletService.class);
        when(broken.name()).thenReturn("broken");
        when(broken.getLink(anyString(), anyString())).thenThrow(new RuntimeException("Wallet Exception"));

        wallets.add(google);
        wallets.add(apple);
        wallets.add(broken);

        RegistrationService s = serviceWith(m);
        Enrollment enrollment = s.verifyCaptchaAndStartEnrollment(playerInfo(), "captcha", SupportedLanguage.EN);
        when(repo.getEnrollment(enrollment.getId())).thenReturn(enrollment);
        when(pm.enrollPlayer(any())).thenReturn(new EnrolledPlayer("mock_id", null));
        when(pm.getPlayerDetail(anyString())).thenReturn(new HashMap<>() {{
            put(PlayerManagementProvider.CMS_PLAYER_NAME, "cms_name");
            put(PlayerManagementProvider.CMS_RANK, "cms_rank");
        }});

        s.verifyOtp(enrollment.getId(), enrollment.getOtp().getPassword(), SupportedLanguage.EN);
        verify(mailService, times(1)).send(eq("surfirst@yeah.net"), eq("welcome"), eq("google|mock_id|cms_name|apple|mock_id|cms_name"));
        verify(logger, atLeastOnce()).warn(anyString(), any());
    }
}
