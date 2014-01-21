package org.greencheek.related.searching.disruptor.requestprocessing;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.related.api.searching.RelatedItemSearchType;
import org.greencheek.related.searching.RelatedItemSearchRequestProcessor;
import org.greencheek.related.searching.domain.RelatedItemSearchRequest;
import org.greencheek.related.searching.domain.RelatedItemSearchRequestFactory;
import org.greencheek.related.searching.requestprocessing.*;
import org.greencheek.related.util.concurrency.DefaultNameableThreadFactory;
import org.greencheek.related.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Main entry point for dealing with user searches.  A Search request is published onto the disruptor ring buffer.
 * The search request is translated into a {@link org.greencheek.related.searching.domain.RelatedItemSearchRequest} with is dealt with by a
 * {@link RelatedContentSearchRequestProcessorHandler}.  The handler is responsible for dealing with the user request,
 * and also returning the search result to the user.
 *
 */
public class DisruptorBasedSearchRequestProcessor implements RelatedItemSearchRequestProcessor {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedSearchRequestProcessor.class);

    private final RelatedContentSearchRequestProcessorHandler eventHandler;
    private final ExecutorService executorService;
    private final Disruptor<RelatedItemSearchRequest> disruptor;
    private final SearchRequestParameterValidatorLocator requestValidators;
    private final IncomingSearchRequestTranslator searchRequestTranslator;

    private final RingBuffer<RelatedItemSearchRequest> ringBuffer;


    public DisruptorBasedSearchRequestProcessor(IncomingSearchRequestTranslator searchRequestTranslator ,
                                                RelatedContentSearchRequestProcessorHandler eventHandler,
                                                RelatedItemSearchRequestFactory relatedItemSearchRequestFactory,
                                                Configuration configuration,
                                                SearchRequestParameterValidatorLocator searchRequestValidator) {
        this.executorService = getExecutorService();
        this.searchRequestTranslator = searchRequestTranslator;
        this.eventHandler = eventHandler;
        this.requestValidators= searchRequestValidator;
        disruptor = new Disruptor<RelatedItemSearchRequest>(
                relatedItemSearchRequestFactory,
                configuration.getSizeOfRelatedItemSearchRequestQueue(), executorService,
                ProducerType.MULTI, configuration.getWaitStrategyFactory().createWaitStrategy());
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());
        disruptor.handleEventsWith(eventHandler);
        ringBuffer = disruptor.start();

    }

    private ExecutorService getExecutorService() {
        return newSingleThreadExecutor(new DefaultNameableThreadFactory("SearchRequestProcessor"));
    }

    @Override
    public SearchRequestSubmissionStatus processRequest(RelatedItemSearchType requestType, Map<String,String> parameters, SearchResponseContext[] context) {
        SearchRequestParameterValidator validator = requestValidators.getValidatorForType(requestType);
        if(validator !=null) {
            ValidationMessage isValid = validator.validateParameters(parameters);
            if(!isValid.isValid()) {
                log.warn("Invalid parameter :{} for search request type {}",isValid.getInvalidProperty(), requestType);
                return SearchRequestSubmissionStatus.REQUEST_VALIDATION_FAILURE;
            }
        }

        log.debug("Processing requesttype {} with parameters {}",requestType,parameters);
        boolean published = ringBuffer.tryPublishEvent(searchRequestTranslator, requestType, parameters, context);
        if(published) return SearchRequestSubmissionStatus.PROCESSING;
        else return SearchRequestSubmissionStatus.PROCESSING_REJECTED_AT_MAX_CAPACITY;
    }

    @PreDestroy
    public void shutdown() {

        try {
            log.info("Shutting down search request handler");
            eventHandler.shutdown();
        } catch (Exception e) {
            log.warn("Unable to shut down search request handler",e);
        }

        try {
            log.info("Attempting to shut down executor thread pool in search request/response processor");
            executorService.shutdownNow();
        } catch (Exception e) {
            log.warn("Unable to shut down executor thread pool in search request/response processor",e);
        }

        log.info("Shutting down index request processor");
        try {
            log.info("Attempting to shut down disruptor in search request/response processor");
            disruptor.shutdown();
            log.info("disruptor search request/response processor is shut down");
        } catch (Exception e) {
            log.warn("Unable to shut down disruptor in search request/response processor",e);
        }




    }

}
