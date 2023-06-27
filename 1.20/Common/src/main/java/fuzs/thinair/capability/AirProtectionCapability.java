package fuzs.thinair.capability;

import fuzs.puzzleslib.api.capability.v2.data.CapabilityComponent;

public interface AirProtectionCapability extends CapabilityComponent {

    boolean isProtected();

    void setProtected(boolean usingBladder);
}
