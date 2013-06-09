package org.greencheek.relatedproduct.searching.disruptor.searchexecution;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearch;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchFactory;
import org.greencheek.relatedproduct.domain.api.SearchEvent;
import org.greencheek.relatedproduct.domain.searching.SearchRequestLookupKey;
import org.greencheek.relatedproduct.resultsconverter.SearchResultsConverter;
import org.greencheek.relatedproduct.searching.RelatedProductSearchExecutor;
import org.greencheek.relatedproduct.searching.SearchRequestResponseHandler;
import org.greencheek.relatedproduct.searching.disruptor.requestresponse.SearchRequestTranslator;
import org.greencheek.relatedproduct.searching.disruptor.requestresponse.SearchResultsTranslator;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.servlet.AsyncContext;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 12:14
 * To change this template use File | Settings | File Templates.
 */
@Named
public class DisruptorBasedRelatedProductSearchExecutor implements RelatedProductSearchExecutor {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedRelatedProductSearchExecutor.class);


    private final ExecutorService executorService = newSingleThreadExecutor();
    private final Disruptor<RelatedProductSearch> disruptor;

    private final Configuration configuration;
    private final RelatedProductSearchFactory searchObjectFactory;

//    private final EventHandler<RelatedProductSearch> eventHandler;


    @Inject
    public DisruptorBasedRelatedProductSearchExecutor(Configuration configuration,
                                                      RelatedProductSearchDisruptorEventHandler eventHandler
    ) {
        this.configuration = configuration;
        this.searchObjectFactory = new RelatedProductSearchFactory(configuration);
//        this.eventHandler = eventHandler;

        disruptor = new Disruptor<RelatedProductSearch>(
                new EventFactory<RelatedProductSearch>() {
                    @Override
                    public RelatedProductSearch newInstance() {
                        return searchObjectFactory.createSearchObject();
                    }
                },
                configuration.getSizeOfRelatedContentSearchRequestHandlerQueue(), executorService,
                ProducerType.SINGLE, new SleepingWaitStrategy());

        disruptor.handleEventsWith(new EventHandler[] {eventHandler});
        disruptor.start();

    }



    @PreDestroy
    public void shutdown() {

        try {
            log.info("Attempting to shut down executor thread pool in search handler processor");
            executorService.shutdown();
        } catch (Exception e) {
            log.warn("Unable to shut down executor thread pool in search handler processor",e);
        }

        log.info("Shutting down index request processor");
        try {
            log.info("Attempting to shut down disruptor in search handler processor");
            disruptor.halt();
            disruptor.shutdown();
        } catch (Exception e) {
            log.warn("Unable to shut down disruptor in search handler processor",e);
        }

    }

    @Override
    public void executeSearch(RelatedProductSearch searchRequest) {
        disruptor.publishEvent(new RelatedProductSearchTranslator(searchRequest));
    }
}
