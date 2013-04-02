/*
*   Copyright 2012-2013 The Regents of the University of Colorado
*
*   Licensed under the Apache License, Version 2.0 (the "License")
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

package com.recomdata.grails.rositaui.utils;

import java.util.HashMap;
import java.util.Map;

public class SqlTemplates {
	
	private Map<String, String> sqlMap = new HashMap<String, String>();
	
	public SqlTemplates() {
		sqlMap.put("Organization", "INSERT INTO lz.lz_src_organization(organization_id, organization_address_1, organization_address_2, organization_city, organization_county, organization_source_value, organization_state, organization_zip, place_of_service_source_value, x_data_source_type) VALUES(?,?,?,?,?,?,?,?,?,?)");
		sqlMap.put("CareSite", "INSERT INTO lz.lz_src_care_site(care_site_id, care_site_address_1, care_site_address_2, care_site_city, care_site_county, care_site_source_value, care_site_state, care_site_zip, organization_id, organization_source_value, place_of_service_source_value, x_care_site_name, x_data_source_type) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
		sqlMap.put("Provider", "INSERT INTO lz.lz_src_provider(provider_id, care_site_id, care_site_source_value, dea, npi, provider_source_value, specialty_source_value, x_data_source_type, x_organization_source_value, x_provider_first, x_provider_last, x_provider_middle) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
		sqlMap.put("Demographic", "INSERT INTO lz.lz_src_x_demographic(x_demographic_id, address_1, address_2, care_site_source_value, city, county, day_of_birth, ethnicity_source_value, first, gender_source_value, last, medicaid_id_number, middle, month_of_birth, organization_id, person_source_value, provider_source_value, race_source_value, ssn, state, x_data_source_type, x_organization_source_value, year_of_birth, zip) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		sqlMap.put("VisitOccurrence", "INSERT INTO lz.lz_src_visit_occurrence(visit_occurrence_id, care_site_source_value, x_demographic_id, person_source_value, place_of_service_source_value, visit_end_date, visit_occurrence_source_identifier, visit_start_date, x_data_source_type, x_provider_source_value) VALUES (?,?,?,?,?,?,?,?,?,?)");
		sqlMap.put("Observation", "INSERT INTO lz.lz_src_observation(observation_id, associated_provider_source_value, x_demographic_id, observation_date, observation_source_identifier, observation_source_value, observation_source_value_vocabulary, observation_time, observation_type_source_value, person_source_value, range_high, range_low, relevant_condition_source_value, unit_source_value, value_as_number, value_as_string, visit_occurrence_source_value, x_data_source_type, x_obs_comment) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		sqlMap.put("DrugExposure", "INSERT INTO lz.lz_src_drug_exposure(drug_exposure_id, days_supply, x_demographic_id, drug_exposure_end_date, drug_exposure_source_identifier, drug_exposure_start_date, drug_source_value, drug_source_value_vocabulary, drug_type_source_value, person_source_value, provider_source_value, quantity, refills, relevant_condition_source_value, sig, stop_reason, visit_occurrence_source_identifier, x_data_source_type, x_drug_name, x_drug_strength) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		sqlMap.put("DrugCost", "INSERT INTO lz.lz_src_drug_cost(drug_cost_id, average_wholesale_price, dispensing_fee, drug_cost_source_identifier, drug_exposure_id, drug_exposure_source_identifier, ingredient_cost, paid_by_coordination_of_benefits, paid_by_payer, paid_coinsurance, paid_copay, paid_toward_deductible, payer_plan_period_source_identifier, total_out_of_pocket, total_paid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		sqlMap.put("ConditionOccurrence", "INSERT INTO lz.lz_src_condition_occurrence(condition_occurrence_id, associated_provider_source_value, condition_end_date, condition_occurrence_source_identifier, condition_source_value, condition_source_value_vocabulary, condition_start_date, condition_type_source_value, x_demographic_id, person_source_value, stop_reason, visit_occurrence_source_value, x_condition_source_desc, x_condition_update_date, x_data_source_type) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		sqlMap.put("ProcedureOccurrence", "INSERT INTO lz.lz_src_procedure_occurrence(procedure_occurrence_id, x_demographic_id, person_source_value, procedure_date, procedure_occurrence_source_identifier, procedure_source_value, procedure_source_value_vocabulary, procedure_type_source_value, provider_record_source_value, relevant_condition_source_value, visit_occurrence_source_value, x_data_source_type) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
		sqlMap.put("ProcedureCost", "INSERT INTO lz.lz_src_procedure_cost(procedure_cost_id, disease_class_concept_id, disease_class_source_value, paid_by_coordination_of_benefits, paid_by_payer, paid_coinsurance, paid_copay, paid_toward_deductible, payer_plan_period_source_identifier, procedure_cost_source_identifier, procedure_occurrence_id, procedure_occurrence_source_identifier, revenue_code_concept_id, revenue_code_source_value, total_out_of_pocket, total_paid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		sqlMap.put("Cohort", "INSERT INTO lz.lz_src_cohort(cohort_id, cohort_end_date, cohort_source_identifier, cohort_source_value, cohort_start_date, x_demographic_id, stop_reason, subject_source_identifier) VALUES (?,?,?,?,?,?,?,?)");
		sqlMap.put("Death", "INSERT INTO lz.lz_src_death(death_id, cause_of_death_source_value, death_date, death_type_concept_id, death_type_source_value, x_demographic_id, person_source_value) VALUES (?,?,?,?,?,?,?)");
		sqlMap.put("PayerPlanPeriod", "INSERT INTO lz.lz_src_payer_plan_period(payer_plan_period_id, x_demographic_id, family_source_value, payer_plan_period_end_date, payer_plan_period_source_identifier, payer_plan_period_start_date, payer_source_value, person_source_value, plan_source_value) VALUES (?,?,?,?,?,?,?,?,?)");
		
		sqlMap.put("ValidationError", "INSERT INTO cz.validation_errors(error_type, line_number, message, datetime, schema, location, source_type, filename) VALUES (?,?,?,?,?,?,?,?)");
		sqlMap.put("VocabularyRow", "INSERT INTO rz.source_to_concept_map_ucd(source_code, source_vocabulary_id, source_code_description, target_concept_id, target_vocabulary_id, mapping_type, primary_map, valid_start_date, valid_end_date, invalid_reason) VALUES (?,?,?,?,?,?,?,?,?,?)");
		
		sqlMap.put("ImportDummyOmop", "SELECT * FROM lz.dummy_omop_nopk WHERE string_id > ? ORDER BY string_id LIMIT ?");
		sqlMap.put("DummyOmop", "INSERT INTO rosita.dummy_omop_target(name, age, credentials) VALUES (?,?,?)");
	}
	  
	public String get(String name) {
		return sqlMap.get(name);
	}

}
