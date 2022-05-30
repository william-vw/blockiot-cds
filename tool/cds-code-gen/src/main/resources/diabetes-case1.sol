// Specifies the version of Solidity, using semantic versioning.
// Learn more: https://solidity.readthedocs.io/en/v0.5.10/layout-of-source-files.html#pragma
pragma solidity ^0.7.0;

contract DiabetesCase1 {
	string public message;
	
	constructor(string memory initMessage) {
		message = initMessage;
	}
	
	
	event NewTreatmentSubPlan(uint time);
	
	
	struct Patient {
		mapping(TreatmentSubplans => TreatmentSubplan) hasPart;
		mapping(PatientDemographics => PatientDemographic) hasDemographic;
		DiabetesDiagnosis hasDiagnosis;
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
	
	enum TreatmentSubplans{ LifestyleSubplan, EducationSubplan, DrugSubplan }
	
	struct TreatmentSubplan {
		string label;
		TreatmentSubplans hasType;
		bool exists;
	}
	
	struct DiabetesDiagnosis {
		DiabetesMellitus hasDiabetesType;
		bool exists;
	}
	
	enum DiabetesMellituses{ DiabetesMellitusDuringPregnancyandChildbirthAndThePuerperium, DiabetesMellitusWithoutComplication, Type2DiabetesMellitus }
	
	struct DiabetesMellitus {
		DiabetesMellituses hasType;
		bool exists;
	}
	
	
	mapping(address => Patient) patients;
	
	function execute(DiabetesPhysicalExamination memory exam) public {
		Patient storage patient = patients[msg.sender];
	
		if (exam.hasType == DiabetesPhysicalExaminations.Bmi
			&& exam.hasQuantitativeValue >= 25) {
		
			PatientDemographic memory v9 = PatientDemographic({ hasType: PatientDemographics.Overweight, exists: true });
			patient.hasDemographic[v9.hasType] = v9;
		}
		
		if (patient.hasDiagnosis.exists
			&& patient.hasDiagnosis.hasDiabetesType.exists
			&& patient.hasDiagnosis.hasDiabetesType.hasType == DiabetesMellituses.Type2DiabetesMellitus
			&& patient.hasDemographic[PatientDemographics.Overweight].exists) {
		
			TreatmentSubplan memory v10 = TreatmentSubplan({ hasType: TreatmentSubplans.LifestyleSubplan, label: "diet, physical activity, and behavioral counseling", exists: true });
			patient.hasPart[v10.hasType] = v10;
		
			emit NewTreatmentSubPlan(block.timestamp);
		}
	}
	
	function update(string memory newMessage) public {
		message = newMessage;
	}}