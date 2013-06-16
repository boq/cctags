package boq.cctags.cc;

import boq.utils.serializable.SerializableField;

public class PrinterData extends WriterData implements PrinterHelper.Printer {
    @SerializableField
    public int inkLevel;

    @Override
    public void setInkLevel(int inkLevel) {
        this.inkLevel = inkLevel;
    }

    @Override
    public int getInkLevel() {
        return inkLevel;
    }
}
