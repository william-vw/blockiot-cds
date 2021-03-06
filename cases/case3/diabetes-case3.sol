// Specifies the version of Solidity, using semantic versioning.
// Learn more: https://solidity.readthedocs.io/en/v0.5.10/layout-of-source-files.html#pragma
pragma solidity ^0.7.0;

contract DiabetesCase3 {
	string public message;
	
	constructor(string memory initMessage) {
		message = initMessage;
	}
	
	
	event RecommendDiabetesScreening(uint time);
	
	
	struct Patient {
		mapping(Recommendations => Recommendation) recommendTest;
		Ethnicity hasEthnicity;
		PatientProfile hasPatientProfile;
		mapping(PatientDemographics => PatientDemographic) hasDemographic;
		mapping(DiabetesPhysicalExaminations => DiabetesPhysicalExamination) hasPhysicalExamination;
		bool exists;
	}
	
	enum PatientDemographics{ Residence, BreastFeeding, MaritalStatus, Weight, LevelOfEducation, Overweight, Height, Gender, ActivityLevel, Age, PregnancyState, Job }
	
	struct PatientDemographic {
		PatientDemographics hasType;
		bool exists;
	}
	
	enum DiabetesPhysicalExaminations{ Bmi, FamilyHistoryOfType1DiabetesMellitus, Smoking, PersonalHistoryOfHemochromatosis, FamilyHistoryOfType2DiabetesMellitus, OralExam, EyeExam, VitalSign, ThyroidFunction, BabyDeliveredWeighingMoreThan4pt5Kg, LostFootSensation, FirstDegreeRelativeWithDiabetes, HistoryOfGestationalDiabetes, HighRiskPopulation, WaistCircumference, FamilyHistoryOfHemochromatosis, PhysicallyInactive, DrinkingAlcohol, FamilyHistoryOfGestationalDiabetesMellitus, HistoryOfPrediabetes }
	
	struct DiabetesPhysicalExamination {
		DiabetesPhysicalExaminations hasType;
		int hasQuantitativeValue;
		bool exists;
	}
	
	enum Ethnicities{ HighRiskEthnicity, PacificIslander, HighRiskEthnicity, AfricanAmerican, AsianAmerican, Latino, NativeAmerican }
	
	struct Ethnicity {
		Ethnicities hasType;
		bool exists;
	}
	
	struct PatientProfile {
		Ethnicity hasEthnicity;
		bool exists;
	}
	
	enum Recommendations{ DiabetesScreening }
	
	struct Recommendation {
		Recommendations hasType;
		bool exists;
	}
	
	
	mapping(address => Patient) patients;
	
	function execute() public {
		Patient storage patient = patients[msg.sender];
	
		if (patient.hasPhysicalExamination[DiabetesPhysicalExaminations.Bmi].exists
			&& patient.hasPhysicalExamination[DiabetesPhysicalExaminations.Bmi].hasQuantitativeValue >= 25
			&& patient.hasEthnicity.exists
			&& patient.hasEthnicity.hasType != Ethnicities.AsianAmerican) {
		
			PatientDemographic memory v13 = PatientDemographic({ hasType: PatientDemographics.Overweight, exists: true });
			patient.hasDemographic[v13.hasType] = v13;
		}
		
		if (patient.hasPhysicalExamination[DiabetesPhysicalExaminations.Bmi].exists
			&& patient.hasPhysicalExamination[DiabetesPhysicalExaminations.Bmi].hasQuantitativeValue >= 23
			&& patient.hasEthnicity.hasType == Ethnicities.AsianAmerican) {
		
			PatientDemographic memory v14 = PatientDemographic({ hasType: PatientDemographics.Overweight, exists: true });
			patient.hasDemographic[v14.hasType] = v14;
		}
		
		if (patient.hasPatientProfile.exists
			&& patient.hasPatientProfile.hasEthnicity.exists
			&& (patient.hasPatientProfile.hasEthnicity.hasType == Ethnicities.AfricanAmerican
			|| patient.hasPatientProfile.hasEthnicity.hasType == Ethnicities.Latino
			|| patient.hasPatientProfile.hasEthnicity.hasType == Ethnicities.NativeAmerican
			|| patient.hasPatientProfile.hasEthnicity.hasType == Ethnicities.AsianAmerican
			|| patient.hasPatientProfile.hasEthnicity.hasType == Ethnicities.PacificIslander)) {
		
			patient.hasPatientProfile.hasEthnicity.hasType = Ethnicities.HighRiskEthnicity;
		}
		
		if (patient.hasDemographic[PatientDemographics.Overweight].exists
			&& patient.hasEthnicity.hasType == Ethnicities.HighRiskEthnicity) {
		
			Recommendation memory v15 = Recommendation({ hasType: Recommendations.DiabetesScreening, exists: true });
			patient.recommendTest[v15.hasType] = v15;
		
			emit RecommendDiabetesScreening(block.timestamp);
		}
	}
	
	function update(string memory newMessage) public {
		message = newMessage;
	}}