/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ocxworker.exception.OciException
 *  com.ocxworker.mapper.OciUserMapper
 *  com.ocxworker.model.dto.SysUserDTO
 *  com.ocxworker.model.dto.SysUserDTO$OciCfg
 *  com.ocxworker.model.entity.OciUser
 *  com.ocxworker.service.OciClientService
 *  com.ocxworker.service.TrafficService
 *  com.oracle.bmc.monitoring.MonitoringClient
 *  com.oracle.bmc.monitoring.model.AggregatedDatapoint
 *  com.oracle.bmc.monitoring.model.MetricData
 *  com.oracle.bmc.monitoring.model.SummarizeMetricsDataDetails
 *  com.oracle.bmc.monitoring.requests.SummarizeMetricsDataRequest
 *  com.oracle.bmc.monitoring.responses.SummarizeMetricsDataResponse
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.ocxworker.service.OciClientService;
import com.oracle.bmc.monitoring.MonitoringClient;
import com.oracle.bmc.monitoring.model.AggregatedDatapoint;
import com.oracle.bmc.monitoring.model.MetricData;
import com.oracle.bmc.monitoring.model.SummarizeMetricsDataDetails;
import com.oracle.bmc.monitoring.requests.SummarizeMetricsDataRequest;
import com.oracle.bmc.monitoring.responses.SummarizeMetricsDataResponse;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TrafficService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TrafficService.class);
    @Resource
    private OciUserMapper userMapper;

    public Map<String, Object> getTrafficData(String userId, String instanceId, int minutes, String region) {
        LinkedHashMap<String, Object> linkedHashMap;
        OciUser ociUser = (OciUser)this.userMapper.selectById((Serializable)((Object)userId));
        if (ociUser == null) {
            throw new OciException("\u79df\u6237\u914d\u7f6e\u4e0d\u5b58\u5728");
        }
        SysUserDTO dto = SysUserDTO.builder().username(ociUser.getUsername()).ociCfg(SysUserDTO.OciCfg.builder().tenantId(ociUser.getOciTenantId()).userId(ociUser.getOciUserId()).fingerprint(ociUser.getOciFingerprint()).region(ociUser.getOciRegion()).privateKeyPath(ociUser.getOciKeyPath()).build()).build();
        String r = region == null || region.isBlank() ? null : region.trim();
        OciClientService client = new OciClientService(dto, r);
        try {
            MonitoringClient monitoringClient = client.getMonitoringClient();
            Date endTime = new Date();
            Date startTime = new Date(endTime.getTime() - (long)minutes * 60L * 1000L);
            LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
            result.put("inbound", this.queryMetric(monitoringClient, client.getCompartmentId(), instanceId, "VnicFromNetworkBytes", startTime, endTime));
            result.put("outbound", this.queryMetric(monitoringClient, client.getCompartmentId(), instanceId, "VnicToNetworkBytes", startTime, endTime));
            linkedHashMap = result;
        }
        catch (Throwable throwable) {
            try {
                try {
                    client.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (Exception e) {
                throw new OciException("\u83b7\u53d6\u6d41\u91cf\u6570\u636e\u5931\u8d25: " + e.getMessage());
            }
        }
        client.close();
        return linkedHashMap;
    }

    private List<Map<String, Object>> queryMetric(MonitoringClient monitoringClient, String compartmentId, String instanceId, String metricName, Date start, Date end) {
        String query = String.format("%s[1m]{resourceId = \"%s\"}.mean()", metricName, instanceId);
        SummarizeMetricsDataResponse response = monitoringClient.summarizeMetricsData(SummarizeMetricsDataRequest.builder().compartmentId(compartmentId).summarizeMetricsDataDetails(SummarizeMetricsDataDetails.builder().namespace("oci_computeagent").query(query).startTime(start).endTime(end).resolution("1m").build()).build());
        ArrayList<Map<String, Object>> dataPoints = new ArrayList<Map<String, Object>>();
        for (MetricData metricData : response.getItems()) {
            for (AggregatedDatapoint dp : metricData.getAggregatedDatapoints()) {
                LinkedHashMap<String, Object> point = new LinkedHashMap<String, Object>();
                point.put("timestamp", dp.getTimestamp().toString());
                point.put("value", dp.getValue());
                dataPoints.add(point);
            }
        }
        return dataPoints;
    }
}

