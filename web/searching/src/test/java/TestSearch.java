
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 16:32
 * To change this template use File | Settings | File Templates.
 */
public class TestSearch {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext xml = new ClassPathXmlApplicationContext(new String[] {"classpath:applicationContext.xml"});
        RelatedProductSearchExecutor handler = (RelatedProductSearchExecutor)xml.getBean(RelatedProductSearchExecutor.class);


        RelatedProductSearch search = new RelatedProductSearch(xml.getBean(Configuration.class));
        search.setByteBuffer(ByteBuffer.allocate(search.size()),0);
        search.relatedContentId.set("123");
        search.maxResults.set(2);
        handler.executeSearch(search);

    }
}
