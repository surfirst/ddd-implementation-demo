package com.example.enrollment.infrastructure.adapters.wallet;

import com.example.enrollment.domain.enrollmentprocess.ports.WalletServicePort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("mock")
public class AppleWalletMock implements WalletServicePort {
    @Override
    public String name() {
        return "apple_wallet";
    }

    @Override
    public String getLink(String cmsId, String cmsPlayerName) {
        return "apple|" + cmsId + "|" + cmsPlayerName;
    }
}
