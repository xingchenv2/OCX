/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.ocxworker.util.VcnIpv6Util
 *  com.oracle.bmc.core.VirtualNetwork
 *  com.oracle.bmc.core.model.Subnet
 *  com.oracle.bmc.core.model.Vcn
 *  com.oracle.bmc.core.requests.GetSubnetRequest
 *  com.oracle.bmc.core.requests.GetVcnRequest
 */
package com.ocxworker.util;

import cn.hutool.core.util.StrUtil;
import com.oracle.bmc.core.VirtualNetwork;
import com.oracle.bmc.core.model.Subnet;
import com.oracle.bmc.core.model.Vcn;
import com.oracle.bmc.core.requests.GetSubnetRequest;
import com.oracle.bmc.core.requests.GetVcnRequest;
import java.util.List;

/*
 * Exception performing whole class analysis ignored.
 */
public final class VcnIpv6Util {
    private VcnIpv6Util() {
    }

    public static boolean isEnabled(Vcn vcn) {
        if (vcn == null) {
            return false;
        }
        List blocks = vcn.getIpv6CidrBlocks();
        return blocks != null && !blocks.isEmpty();
    }

    public static boolean isEnabled(VirtualNetwork client, Subnet subnet) {
        if (subnet == null || client == null) {
            return false;
        }
        return VcnIpv6Util.isEnabled((VirtualNetwork)client, (String)subnet.getVcnId());
    }

    public static boolean isEnabled(VirtualNetwork client, String vcnId) {
        if (client == null || StrUtil.isBlank((CharSequence)vcnId)) {
            return false;
        }
        Vcn vcn = client.getVcn(GetVcnRequest.builder().vcnId(vcnId.trim()).build()).getVcn();
        return VcnIpv6Util.isEnabled((Vcn)vcn);
    }

    public static boolean isEnabledForSubnet(VirtualNetwork client, String subnetId) {
        if (client == null || StrUtil.isBlank((CharSequence)subnetId)) {
            return false;
        }
        Subnet subnet = client.getSubnet(GetSubnetRequest.builder().subnetId(subnetId.trim()).build()).getSubnet();
        return VcnIpv6Util.isEnabled((VirtualNetwork)client, (Subnet)subnet);
    }
}

