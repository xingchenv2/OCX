package com.ocxworker.util;

import cn.hutool.core.util.StrUtil;
import com.oracle.bmc.core.VirtualNetwork;
import com.oracle.bmc.core.model.Subnet;
import com.oracle.bmc.core.model.Vcn;
import com.oracle.bmc.core.requests.GetSubnetRequest;
import com.oracle.bmc.core.requests.GetVcnRequest;
import java.util.List;

public final class VcnIpv6Util {
    private VcnIpv6Util() {
    }

    public static boolean isEnabled(Vcn vcn) {
        if (vcn == null) {
            return false;
        } else {
            List<String> blocks = vcn.getIpv6CidrBlocks();
            return blocks != null && !blocks.isEmpty();
        }
    }

    public static boolean isEnabled(VirtualNetwork client, Subnet subnet) {
        return subnet != null && client != null ? isEnabled(client, subnet.getVcnId()) : false;
    }

    public static boolean isEnabled(VirtualNetwork client, String vcnId) {
        if (client != null && !StrUtil.isBlank(vcnId)) {
            Vcn vcn = client.getVcn(GetVcnRequest.builder().vcnId(vcnId.trim()).build()).getVcn();
            return isEnabled(vcn);
        } else {
            return false;
        }
    }

    public static boolean isEnabledForSubnet(VirtualNetwork client, String subnetId) {
        if (client != null && !StrUtil.isBlank(subnetId)) {
            Subnet subnet = client.getSubnet(GetSubnetRequest.builder().subnetId(subnetId.trim()).build()).getSubnet();
            return isEnabled(client, subnet);
        } else {
            return false;
        }
    }
}
