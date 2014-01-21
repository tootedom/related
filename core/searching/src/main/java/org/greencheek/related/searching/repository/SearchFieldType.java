package org.greencheek.related.searching.repository;

/**
 * Represents how fields are to be searched (filtered on)
 * when performing searches.
 *
 * EXACT_PHRASE ie. (term no analysis)
 * OR ie. "WORD or WORD or WORD" (analysis is done on the WORD)
 * AND ie. "WORD and WORD" (analysis is done on the WORD)
 *
 * EXACT_OR (term no analysis)
 * EXACT_AND (term no analysis)
 *
 *
 */
public enum SearchFieldType {

    EXACT_PHRASE,
    EXACT_OR,EXACT_AND,
    OR,AND
}
