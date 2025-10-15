package com.example.enrollment.domain.enrollmentprocess.ports;

import com.example.enrollment.domain.enrollmentprocess.EnrolledPlayer;
import com.example.enrollment.domain.enrollmentprocess.PlayerInfo;

import java.util.Map;

public interface PlayerManagementProvider {
    String CMS_PLAYER_NAME = "cms_player_name";
    String CMS_RANK = "cms_rank";

    EnrolledPlayer findPlayerByEmail(String email);
    EnrolledPlayer duplicationCheck(PlayerInfo info);
    EnrolledPlayer enrollPlayer(PlayerInfo info);
    Map<String, String> getPlayerDetail(String cmsId);
}
