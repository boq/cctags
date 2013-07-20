package boq.cctags.tag.access;

import net.minecraft.item.ItemStack;
import boq.cctags.tag.TagData;

public interface IItemStackDataAccess {
    public TagData readData(ItemStack stack);

    public void writeData(ItemStack stack, TagData data);

    public boolean isValid(ItemStack stack);

    public static class MergedAccess implements IItemStackDataAccess {

        private final IItemStackDataAccess first;
        private final IItemStackDataAccess second;

        public MergedAccess(IItemStackDataAccess first, IItemStackDataAccess second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public TagData readData(ItemStack stack) {
            if (first.isValid(stack))
                return first.readData(stack);

            if (second.isValid(stack))
                return second.readData(stack);

            throw new IllegalArgumentException("Invalid item: " + stack);
        }

        @Override
        public void writeData(ItemStack stack, TagData data) {
            if (first.isValid(stack))
                first.writeData(stack, data);

            else if (second.isValid(stack))
                second.writeData(stack, data);

            else
                throw new IllegalArgumentException("Invalid item: " + stack);
        }

        @Override
        public boolean isValid(ItemStack stack) {
            return first.isValid(stack) || second.isValid(stack);
        }

    }
}
