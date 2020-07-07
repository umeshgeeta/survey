// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import com.neosemantix.survey.agg.SurveyResponseAggregator;
import com.neosemantix.survey.model.SurveyResponse;

import reactor.core.publisher.Flux;

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
public class SurveyResultService implements SurveyResponseProcessor {

	private SurveyResponseListener listenerForFlux;
	private Flux<SurveyResponse> bridge;
	private SurveyResponseAggregator respAggregator;

	public SurveyResultService(@Autowired SurveyResponseAggregator sra) {
		respAggregator = sra;
		bridge = Flux.create(sink -> {
			this.register(new SurveyResponseListener() {

				public void onSurveyResponse(SurveyResponse chunk) {
					sink.next(chunk);
				}

				public void processComplete() {
					sink.complete();
				}
			});
		});

		Thread t = new Thread(new ProcessSurveyResponse(bridge, sra));
		t.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.neosemantix.survey.service.SurveyResponseProcessor#register(com.
	 * neosemantix.survey.service.SurveyResponseListener)
	 */
	@Override
	public void register(SurveyResponseListener listener) {
		// for now only one listener supported which will dump received responses to a
		// sink
		listenerForFlux = listener;
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
		private SurveyResponseAggregator srAgg;

		private ProcessSurveyResponse(Flux<SurveyResponse> incoming, SurveyResponseAggregator agg) {
			inResps = incoming;
			srAgg = agg;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			inResps.subscribe(next -> {
				System.out.println("Process: " + next);
			});
		}

	}
}
