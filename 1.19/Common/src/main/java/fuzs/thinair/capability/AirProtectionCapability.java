package fuzs.thinair.capability;

import fuzs.puzzleslib.capability.data.CapabilityComponent;

public interface AirProtectionCapability extends CapabilityComponent {

    boolean isProtected();

    void setProtected(boolean usingBladder);
}
