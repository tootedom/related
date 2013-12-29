package org.greencheek.relatedproduct.api.searching;

/**
 * Represents a results that contains the frequency of the id occurring.
 */
public class FrequentlyRelatedSearchResult {

    private final long frequency;
    private final String relatedProductId;

    public FrequentlyRelatedSearchResult(String id, long frequency) {
        this.frequency = frequency;
        this.relatedProductId = id;
    }

    public String getRelatedProductId() {
        return relatedProductId;
    }


    public long getFrequency() {
        return frequency;
    }

    public String toString() {
        StringBuilder b = new StringBuilder(32);
        b.append(frequency).append(':').append(relatedProductId);
        return b.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FrequentlyRelatedSearchResult that = (FrequentlyRelatedSearchResult) o;
        if (frequency != that.frequency) return false;
        if (relatedProductId != null ? !relatedProductId.equals(that.relatedProductId) : that.relatedProductId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (frequency ^ (frequency >>> 32));
        result = 31 * result + (relatedProductId != null ? relatedProductId.hashCode() : 0);
        return result;
    }

}
