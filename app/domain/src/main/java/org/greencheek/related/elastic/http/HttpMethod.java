package org.greencheek.related.elastic.http;

/**
 * Created by dominictootell on 16/02/2014.
 */
public enum HttpMethod {
    GET(1),PUT(2),POST(3),DELETE(4);

    private final int id;

    HttpMethod(int value) {
        this.id = value;
    }
}
