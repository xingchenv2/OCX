package com.ocxworker.service;

import com.ocxworker.exception.OciException;
import com.ocxworker.mapper.OciUserMapper;
import com.ocxworker.model.dto.SysUserDTO;
import com.ocxworker.model.entity.OciUser;
import com.oracle.bmc.monitoring.MonitoringClient;
import com.oracle.bmc.monitoring.model.AggregatedDatapoint;
import com.oracle.bmc.monitoring.model.MetricData;
import com.oracle.bmc.monitoring.model.SummarizeMetricsDataDetails;
import com.oracle.bmc.monitoring.requests.SummarizeMetricsDataRequest;
import com.oracle.bmc.monitoring.responses.SummarizeMetricsDataResponse;
import jakarta.annotation.Resource;
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
        OciUser ociUser = (OciUser)this.userMapper.selectById(userId);
        if (ociUser == null) {
            throw new OciException("租户配置不存在");
        } else {
            SysUserDTO dto = SysUserDTO.builder()
                .username(ociUser.getUsername())
                .ociCfg(
                    SysUserDTO.OciCfg.builder()
                        .tenantId(ociUser.getOciTenantId())
                        .userId(ociUser.getOciUserId())
                        .fingerprint(ociUser.getOciFingerprint())
                        .region(ociUser.getOciRegion())
                        .privateKeyPath(ociUser.getOciKeyPath())
                        .build()
                )
                .build();
            String r = region != null && !region.isBlank() ? region.trim() : null;

            try {
                Object var13;
                try (OciClientService client = new OciClientService(dto, r)) {
                    MonitoringClient monitoringClient = client.getMonitoringClient();
                    Date endTime = new Date();
                    Date startTime = new Date(endTime.getTime() - (long)minutes * 60L * 1000L);
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("inbound", this.queryMetric(monitoringClient, client.getCompartmentId(), instanceId, "VnicFromNetworkBytes", startTime, endTime));
                    result.put("outbound", this.queryMetric(monitoringClient, client.getCompartmentId(), instanceId, "VnicToNetworkBytes", startTime, endTime));
                    var13 = result;
                }

                return (Map<String, Object>)var13;
            } catch (Exception var16) {
                throw new OciException("获取流量数据失败: " + var16.getMessage());
            }
        }
    }

    private List<Map<String, Object>> queryMetric(
        MonitoringClient monitoringClient, String compartmentId, String instanceId, String metricName, Date start, Date end
    ) {
        String query = String.format("%s[1m]{resourceId = \"%s\"}.mean()", metricName, instanceId);
        SummarizeMetricsDataResponse response = monitoringClient.summarizeMetricsData(
            SummarizeMetricsDataRequest.builder()
                .compartmentId(compartmentId)
                .summarizeMetricsDataDetails(
                    SummarizeMetricsDataDetails.builder().namespace("oci_computeagent").query(query).startTime(start).endTime(end).resolution("1m").build()
                )
                .build()
        );
        List<Map<String, Object>> dataPoints = new ArrayList<>();

        for (MetricData metricData : response.getItems()) {
            for (AggregatedDatapoint dp : metricData.getAggregatedDatapoints()) {
                Map<String, Object> point = new LinkedHashMap<>();
                point.put("timestamp", dp.getTimestamp().toString());
                point.put("value", dp.getValue());
                dataPoints.add(point);
            }
        }

        return dataPoints;
    }
}
