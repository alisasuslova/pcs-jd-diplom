public class PageEntry implements Comparable<PageEntry> {
    private final String pdfName;
    private final int page;
    private final int count;

    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    @Override
    public int compareTo(PageEntry o) {

        int result = Integer.compare(this.count, o.count);
        if (result == 0) {
            result = o.pdfName.compareTo(this.pdfName);
        }
        if (result == 0) {
            result = Integer.compare(o.page, this.page);
        }
        return result;
    }

    @Override
    public String toString() {
        return "PageEntry{" +
                "pdfName='" + pdfName + '\'' +
                ", page=" + page +
                ", count=" + count +
                '}';
    }
}
