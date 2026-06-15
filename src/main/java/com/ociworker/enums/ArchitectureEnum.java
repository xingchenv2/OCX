/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ociworker.enums.ArchitectureEnum
 *  lombok.Generated
 */
package com.ociworker.enums;

import lombok.Generated;

/*
 * Exception performing whole class analysis ignored.
 */
public enum ArchitectureEnum {
    ARM("ARM", "VM.Standard.A1.Flex"),
    AMD("AMD", "VM.Standard.E2.1.Micro");

    private final String architecture;
    private final String shape;

    public static String getShape(String architecture) {
        for (ArchitectureEnum e : ArchitectureEnum.values()) {
            if (!e.getArchitecture().equalsIgnoreCase(architecture)) continue;
            return e.getShape();
        }
        return ARM.getShape();
    }

    @Generated
    public String getArchitecture() {
        return this.architecture;
    }

    @Generated
    public String getShape() {
        return this.shape;
    }

    @Generated
    private ArchitectureEnum(String architecture, String shape) {
        this.architecture = architecture;
        this.shape = shape;
    }
}

