package org.greencheek.relatedproduct.searching.disruptor.requestprocessing;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchFactory;
import org.greencheek.relatedproduct.api.searching.RelatedProductSearchType;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequest;
import org.greencheek.relatedproduct.searching.domain.RelatedProductSearchRequestFactory;
import org.greencheek.relatedproduct.searching.RelatedProductSearchRequestProcessor;
import org.greencheek.relatedproduct.searching.requestprocessing.InvalidSearchRequestException;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchRequestParameterValidator;
import org.greencheek.relatedproduct.searching.requestprocessing.SearchRequestParameterValidatorLocator;
import org.greencheek.relatedproduct.searching.requestprocessing.ValidationMessage;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.servlet.AsyncContext;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 09/06/2013
 * Time: 12:14
 * To change this template use File | Settings | File Templates.
 */
public class DisruptorBasedSearchRequestProcessor implements RelatedProductSearchRequestProcessor {
    private static final Logger log = LoggerFactory.getLogger(DisruptorBasedSearchRequestProcessor.class);

    private final RelatedContentSearchRequestProcessorHandler eventHandler;
    private final ExecutorService executorService = newSingleThreadExecutor();
    private final Disruptor<RelatedProductSearchRequest> disruptor;
    private final SearchRequestParameterValidatorLocator requestValidators;
    private final RelatedProductSearchRequestFactory relatedProductSearchRequestFactory;
    private final RelatedProductSearchFactory searchFactory;

    private final Configuration configuration;


    public DisruptorBasedSearchRequestProcessor(RelatedContentSearchRequestProcessorHandler eventHandler,
                                                RelatedProductSearchRequestFactory relatedProductSearchRequestFactory,
                                                RelatedProductSearchFactory searchFactory,
                                                Configuration configuration,
                                                SearchRequestParameterValidatorLocator searchRequestValidator) {
        this.eventHandler = eventHandler;
        this.requestValidators= searchRequestValidator;
        this.configuration = configuration;
        this.searchFactory = searchFactory;
        this.relatedProductSearchRequestFactory = relatedProductSearchRequestFactory;
        disruptor = new Disruptor<RelatedProductSearchRequest>(
                relatedProductSearchRequestFactory,
                configuration.getSizeOfRelatedContentSearchRequestQueue(), executorService,
                ProducerType.MULTI, new SleepingWaitStrategy());
        disruptor.handleExceptionsWith(new IgnoreExceptionHandler());
        disruptor.handleEventsWith(new EventHandler[] {eventHandler});
        disruptor.start();

    }



    @Override
    public void processRequest(RelatedProductSearchType requestType, Map<String,String> parameters, AsyncContext context) throws InvalidSearchRequestException {
        SearchRequestParameterValidator validator = requestValidators.getValidatorForType(requestType);
        if(validator !=null) {
            ValidationMessage isValid = validator.validateParameters(parameters);
            if(!isValid.isValid) {
                throw new InvalidSearchRequestException("Invalid parameter :"+isValid.invalidProperty + " for search request type " + requestType);
            }
        }

        log.debug("Processing requesttype {} with parameters {}",requestType,parameters);
        disruptor.publishEvent(new RelatedProductSearchRequestTranslator(configuration,searchFactory,requestType,parameters,context));

    }

    @PreDestroy
    public void shutdown() {

        eventHandler.shutdown();

        try {
            log.info("Attempting to shut down executor thread pool in search request/response processor");
            executorService.shutdownNow();
        } catch (Exception e) {
            log.warn("Unable to shut down executor thread pool in search request/response processor",e);
        }

        log.info("Shutting down index request processor");
        try {
            log.info("Attempting to shut down disruptor in search request/response processor");
            disruptor.halt();
            disruptor.shutdown();
            log.info("disruptor search request/response processor is shut down");
        } catch (Exception e) {
            log.warn("Unable to shut down disruptor in search request/response processor",e);
        }




    }

}
