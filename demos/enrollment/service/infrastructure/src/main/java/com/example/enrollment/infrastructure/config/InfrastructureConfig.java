package com.example.enrollment.infrastructure.config;

import com.example.enrollment.application.registration.RegistrationService;
import com.example.enrollment.domain.emailtemplates.EmailTemplateManager;
import com.example.enrollment.domain.emailtemplates.EmailTemplateRepository;
import com.example.enrollment.domain.enrollmentprocess.RegistrationEnrollmentRepository;
import com.example.enrollment.domain.enrollmentprocess.ports.CaptchaService;
import com.example.enrollment.domain.enrollmentprocess.ports.GlobalSettings;
import com.example.enrollment.domain.enrollmentprocess.ports.MailService;
import com.example.enrollment.domain.enrollmentprocess.ports.PlayerManagementProvider;
import com.example.enrollment.domain.enrollmentprocess.ports.WalletService;
import com.example.enrollment.domain.shared.SupportedLanguage;
import com.example.enrollment.domain.time.DateTimeProvider;
import com.example.enrollment.domain.time.DefaultDateTimeProvider;
import com.example.enrollment.infrastructure.adapters.emailrenderers.QRCodeRenderer;
import com.example.enrollment.infrastructure.adapters.emailrenderers.Save2PhotoRender;
import com.example.enrollment.infrastructure.adapters.logging.Slf4jLoggerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureConfig {

    // ----- Registration flow wiring (mock/db profiles) -----
    @Bean
    public DateTimeProvider dateTimeProvider() {
        return new DefaultDateTimeProvider();
    }

    @Bean
    public QRCodeRenderer qrCodeRenderer() {
        return new QRCodeRenderer(16);
    }

    @Bean
    public Save2PhotoRender save2PhotoRender() {
        return new Save2PhotoRender();
    }

    @Bean
    public EmailTemplateManager emailTemplateManager(EmailTemplateRepository repo,
                                                     QRCodeRenderer qr,
                                                     Save2PhotoRender s2p) {
        EmailTemplateManager m = new EmailTemplateManager(repo, SupportedLanguage.EN);
        m.addRenderer("welcome", qr);
        m.addRenderer("welcome", s2p);
        return m;
    }

    @Bean
    public com.example.enrollment.domain.enrollmentprocess.ports.Logger domainLogger() {
        return new Slf4jLoggerAdapter();
    }

    @Bean
    public GlobalSettings globalSettings(com.example.enrollment.domain.enrollmentprocess.ports.Logger logger) {
        return () -> logger;
    }

    @Bean
    public RegistrationService registrationService(CaptchaService captchaService,
                                                   MailService mailService,
                                                   RegistrationEnrollmentRepository repo,
                                                   PlayerManagementProvider pm,
                                                   EmailTemplateManager manager,
                                                   java.util.Set<WalletService> walletServices,
                                                   GlobalSettings settings,
                                                   DateTimeProvider dateTimeProvider) {
        return new RegistrationService(captchaService, mailService, repo, pm, manager, walletServices, settings, dateTimeProvider);
    }
}
