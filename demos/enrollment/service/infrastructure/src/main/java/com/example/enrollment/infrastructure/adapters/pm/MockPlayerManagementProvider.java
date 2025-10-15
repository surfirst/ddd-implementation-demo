package com.example.enrollment.infrastructure.adapters.pm;

import com.example.enrollment.domain.enrollmentprocess.EnrolledPlayer;
import com.example.enrollment.domain.enrollmentprocess.PlayerInfo;
import com.example.enrollment.domain.enrollmentprocess.ports.PlayerManagementProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
@Profile("mock")
public class MockPlayerManagementProvider implements PlayerManagementProvider {
    @Override
    public EnrolledPlayer findPlayerByEmail(String email) {
        return null;
    }

    @Override
    public EnrolledPlayer duplicationCheck(PlayerInfo info) {
        return null;
    }

    @Override
    public EnrolledPlayer enrollPlayer(PlayerInfo info) {
        return new EnrolledPlayer(UUID.randomUUID().toString(), info.getFullName());
    }

    @Override
    public Map<String, String> getPlayerDetail(String cmsId) {
        return Collections.emptyMap();
    }
}
