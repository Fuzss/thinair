package fuzs.thinair.capability;

import net.minecraft.nbt.CompoundTag;

public class AirProtectionCapabilityImpl implements AirProtectionCapability {
    public static final String TAG_IS_USING_BLADDER = "is_using_bladder";

    private boolean isUsingBladder;

    @Override
    public boolean isProtected() {
        return this.isUsingBladder;
    }

    @Override
    public void setProtected(boolean usingBladder) {
        this.isUsingBladder = usingBladder;
    }

    @Override
    public void write(CompoundTag tag) {
        tag.putBoolean(TAG_IS_USING_BLADDER, this.isUsingBladder);
    }

    @Override
    public void read(CompoundTag tag) {
        this.isUsingBladder = tag.getBoolean(TAG_IS_USING_BLADDER);
    }
}
