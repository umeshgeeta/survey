// MIT License
// Author: Umesh Patil, Neosemantix, Inc.
package com.neosemantix.survey.service;

import java.util.Map;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.neosemantix.survey.model.SurveyDef;
import com.neosemantix.survey.model.SurveyQuestion;
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
		if (sd != null) {
			Map<Integer, SurveyQuestion> qs = sd.getQuestions();
			if (qs != null && !qs.isEmpty()) {
				for (Integer qk: qs.keySet()) {
					SurveyQuestion q = qs.get(qk);
					if (q.getId() == null) {
						// it is bson object id
						// if we need completely Java specific, change it to
						// java.util.UUID
						q.setId(new ObjectId().toString());
					}
				}
			}
			return this.sdRepository.save(sd);
		} else {
			return null;
		}
		
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
