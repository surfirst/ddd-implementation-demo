package com.example.enrollment.domain.enrollmentprocess.ports;

public interface WalletService {
    String name();
    String getLink(String cmsId, String cmsPlayerName);
}
