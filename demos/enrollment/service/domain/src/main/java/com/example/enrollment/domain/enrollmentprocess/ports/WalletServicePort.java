package com.example.enrollment.domain.enrollmentprocess.ports;

public interface WalletServicePort {
    String name();
    String getLink(String cmsId, String cmsPlayerName);
}
