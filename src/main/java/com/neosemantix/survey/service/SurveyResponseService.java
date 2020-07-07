// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.neosemantix.survey.model.SurveyResponse;
import com.neosemantix.survey.repo.SurveyResponseRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @ComponentScan - this helps to get reference of other service.
 * 
 * Ref.: https://www.baeldung.com/constructor-injection-in-spring
 * 		 https://github.com/eugenp/tutorials/blob/master/spring-di/src/main/java/com/baeldung/constructordi/Config.java
 */
@Service
@ComponentScan("com.neosemantix.survey.service")		
public class SurveyResponseService {

	private final SurveyResponseRepository srRepository;
	private final SurveyResultService resultService;
	
	public SurveyResponseService(@Autowired SurveyResponseRepository srr,
			@Autowired SurveyResultService rs) {
		srRepository = srr;
		resultService = rs;
	}
	
	public Mono<SurveyResponse> getById(String id) {
		return srRepository.findById(id);
	}
	
	/**
	 * @param sr
	 * @return String New id of the saved response
	 */
	public String record(SurveyResponse sr){
		Mono<SurveyResponse> savedMono = srRepository.save(sr);
		SurveyResponse savedSr = savedMono.block();
		resultService.processSurveyResponse(savedSr);
		return savedSr.getId();
	}
	
	public Flux<SurveyResponse> getAll(String surveyDefId){
		return srRepository.findAllResponses(surveyDefId);
	}
	
	public Mono<ServerResponse> delete(SurveyResponse sr){
		return srRepository.delete(sr).then(ServerResponse.ok().build());
	}

}
