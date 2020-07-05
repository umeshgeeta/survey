// Copyright 2019; All rights reserved with NeoSemantix, Inc.
package com.neosemantix.survey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.neosemantix.survey.model.SurveyDef;
import com.neosemantix.survey.repo.SurveyDefRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Transactional
public class SurveyDefService {

	@Autowired
	private final SurveyDefRepository sdRepository;

	public SurveyDefService(SurveyDefRepository sdr) {
		this.sdRepository = sdr;
	}

	public Flux<SurveyDef> all() {
		return this.sdRepository.findAll();
	}

	/**
	 * @param name
	 *            Survey name
	 * @param owner
	 * 			  Name of the survey owner
	 * @return
	 */
	public Flux<SurveyDef> getAllByName(String name, String owner) {
		return this.sdRepository.findByNameAndOwner(name, owner);
	}

	public Mono<SurveyDef> get(String id) {
		return this.sdRepository.findById(id);
	}

	public Mono<SurveyDef> create(SurveyDef sd) {
		return this.sdRepository.save(sd);
	}

	public Mono<ServerResponse> delete(SurveyDef sd) {
		return sdRepository.delete(sd).then(ServerResponse.ok().build());
	}

	public Mono<SurveyDef> delete(final String id) {
		return sdRepository.findById(id)
		        .flatMap(oldValue -> 
		            sdRepository.deleteById(id)
		                           .then(Mono.just(oldValue))
		        );
	}
	
	public void deleteAll() {
		sdRepository.deleteAll().then().block();
	}

}
