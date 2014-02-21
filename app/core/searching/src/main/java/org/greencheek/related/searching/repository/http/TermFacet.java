package org.greencheek.related.searching.repository.http;

/**
 * Created by dominictootell on 20/02/2014.
 */
public class TermFacet {
    public final String name;
    public final long count;

    public TermFacet(String name,long count) {
        this.name = name;
        this.count = count;
    }
}
