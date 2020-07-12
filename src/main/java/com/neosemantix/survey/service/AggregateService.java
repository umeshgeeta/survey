// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.neosemantix.survey.agg.SurveyResponseAggregator;
import com.neosemantix.survey.model.SurveyAggregate;
import com.neosemantix.survey.model.SurveyDef;
import com.neosemantix.survey.model.SurveyResponse;
import com.neosemantix.survey.repo.AggregateRepository;

import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.util.function.Consumer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The service implements SurveyResponseProcessor interface which - accepts
 * registration of a listener (one for the time being) and - process the
 * incoming survey response which involves dumpting the instance to a sink which
 * "back" Flux.
 * 
 * The service starts a thread which subscribes to the constructed Flus and
 * process incoming SurveyResults. Usage of Flux fundamentally utilizes the core
 * ability of reactor mechanisms to bring async process and management of back
 * filling.
 * 
 * Instead of Flux, one can have a bounded blocking queue with it's rigidity.
 */
@Service
@ComponentScan("com.neosemantix.survey.agg")	
public class AggregateService implements SurveyResponseProcessor  {
	
	@Autowired
	private final AggregateRepository aggRepository;
	private SurveyResponseListener listenerForFlux;
	private Flux<SurveyResponse> bridge;
	private SurveyDefService sdefService;
	private SurveyResponseAggregator aggregator;

	public AggregateService(AggregateRepository ar, 
			@Autowired SurveyResponseAggregator sra, 
			@Autowired SurveyDefService sds) {
		aggRepository = ar;
		sdefService = sds;
		aggregator = sra;
		
//		bridge = Flux.create(sink -> {
//			this.register(new SurveyResponseListener() {
//
//				public void onSurveyResponse(SurveyResponse chunk) {
//					sink.next(chunk);
//				}
//
//				public void processComplete() {
//					sink.complete();
//				}				
//			});
//		});
		
		//create an instance of FluxSink implementation
		FluxSinkImpl fluxSinkConsumer = new FluxSinkImpl();
		
		this.register(fluxSinkConsumer);

		//create method can accept this instance
		bridge = Flux.create(fluxSinkConsumer).delayElements(Duration.ofMillis(1)).share();
		
		Thread t = new Thread(new ProcessSurveyResponse(bridge));
		t.start();
	}
	
	public Mono<SurveyAggregate> findAggBySurvyDefId(String sfId){
		return aggRepository.findBySurveyDefId(sfId);
	}
	
	public Mono<SurveyAggregate> save(SurveyAggregate sa) {
		return aggRepository.save(sa);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neosemantix.survey.service.SurveyResponseProcessor#register(com.
	 * neosemantix.survey.service.SurveyResponseListener)
	 */
	@Override
	public void register(SurveyResponseListener listener) {
		// for now only one listener supported which will dump received responses to a sink
		listenerForFlux = listener;
	}

	public class FluxSinkImpl implements Consumer<FluxSink<SurveyResponse>>,  SurveyResponseListener {

	    private FluxSink<SurveyResponse> fluxSink;

	    @Override
	    public void accept(FluxSink<SurveyResponse> fs) {
	        this.fluxSink = fs;
	    }

	    public void onSurveyResponse(SurveyResponse event){
	        this.fluxSink.next(event);
	    }

		/* (non-Javadoc)
		 * @see com.neosemantix.survey.service.SurveyResponseListener#processComplete()
		 */
		@Override
		public void processComplete() {
			System.out.println("FluxSinkImpl complete.");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.neosemantix.survey.service.SurveyResponseProcessor#processSurveyResponse(
	 * com.neosemantix.survey.model.SurveyResponse)
	 */
	@Override
	public void processSurveyResponse(SurveyResponse msr) {
		// Should it be a list of incoming surveys?
		listenerForFlux.onSurveyResponse(msr);
	}

	private class ProcessSurveyResponse implements Runnable {

		private Flux<SurveyResponse> inResps;

		private ProcessSurveyResponse(Flux<SurveyResponse> incoming) {
			inResps = incoming;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			while (true) {
				SurveyResponse sr = inResps.next().block();
				System.out.println("Received survey response for aggregation: " + sr);
				// let us get the survey definition applicable
				SurveyDef sd = sdefService.get(sr.getSurveyId()).block();
				// next, fetch the aggregation so far; it can be null for the first
				SurveyAggregate agg = findAggBySurvyDefId(sd.getId()).block();
				// we aggregate it
				agg = aggregator.aggregateResponse(sd, agg, sr);
				// finally we save it - insert if new, else update the existing; 
				// that is the behavior expected.
				SurveyAggregate sa = save(agg).block();
				System.out.println("Saved aggregate: " + sa);
			}
		}
	}
}
