package com.example.enrollment.infrastructure.adapters.wallet;

import com.example.enrollment.domain.enrollmentprocess.ports.WalletService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("mock")
public class GoogleWalletMock implements WalletService {
    @Override
    public String name() {
        return "google_wallet";
    }

    @Override
    public String getLink(String cmsId, String cmsPlayerName) {
        return "google|" + cmsId + "|" + cmsPlayerName;
    }
}
