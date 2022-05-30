// Specifies the version of Solidity, using semantic versioning.
// Learn more: https://solidity.readthedocs.io/en/v0.5.10/layout-of-source-files.html#pragma
pragma solidity ^0.7.0;

contract DiabetesCase4 {
	string public message;
	
	constructor(string memory initMessage) {
		message = initMessage;
	}
	
	
	event NewTreatmentSubPlan(uint time);
	
	
	struct Patient {
		mapping(DrugSubplans => DrugSubplan) hasPart;
		mapping(DiabetesPhysicalExaminations => DiabetesPhysicalExamination) hasPhysicalExamination;
		mapping(PatientDemographics => PatientDemographic) hasDemographic;
		mapping(DiabetesLaboratoryTests => DiabetesLaboratoryTest) hasLabTest;
		bool exists;
	}
	
	enum DiabetesPhysicalExaminations{ Bmi, FamilyHistoryOfType1DiabetesMellitus, Smoking, PersonalHistoryOfHemochromatosis, FamilyHistoryOfType2DiabetesMellitus, OralExam, EyeExam, VitalSign, ThyroidFunction, BabyDeliveredWeighingMoreThan4pt5Kg, LostFootSensation, FirstDegreeRelativeWithDiabetes, HistoryOfGestationalDiabetes, HighRiskPopulation, WaistCircumference, FamilyHistoryOfHemochromatosis, PhysicallyInactive, DrinkingAlcohol, FamilyHistoryOfGestationalDiabetesMellitus, HistoryOfPrediabetes }
	
	struct DiabetesPhysicalExamination {
		DiabetesPhysicalExaminations hasType;
		int hasQuantitativeValue;
		bool exists;
	}
	
	enum PatientDemographics{ Residence, BreastFeeding, MaritalStatus, Weight, LevelOfEducation, Height, Gender, ActivityLevel, Age, ObeseClassI, PregnancyState, Job }
	
	struct PatientDemographic {
		PatientDemographics hasType;
		int hasQuantitativeValue;
		bool exists;
	}
	
	enum DrugSubplans{ MonotherapyPlan, DualTherapyPlan }
	
	struct DrugSubplan {
		DiabetesDrug hasDrugParticipant;
		DrugSubplans hasType;
		bool exists;
	}
	
	enum DiabetesDrugs{ DopamineAgonist, OtherDrug, CombinedDrug, Insulin, Thiazolidinedione, Sulfonylurea, Incretin, Meglitinide, AlphaGlucosidaseInhibitor, Metformin }
	
	struct DiabetesDrug {
		DiabetesDrugs hasType;
		bool exists;
	}
	
	enum DiabetesLaboratoryTests{ BloodKetone, PlasmaBicarbonate, BloodGlucoseTest, LipidProfile, SerumFetuinA, SerumOsmolality, Autoantibody, SerumAdiponectin, HematologicalProfile, InsulinMeasurement, KidneyFunctionTest, TumorMarker, UrineAnalysis, PlasmaCreatinine, LiverFunctionTest, Hba1c, Fpg }
	
	struct DiabetesLaboratoryTest {
		DiabetesLaboratoryTests hasType;
		int hasQuantitativeValue;
		bool exists;
	}
	
	
	mapping(address => Patient) patients;
	
	function execute() public {
		Patient storage patient = patients[msg.sender];
	
		if (patient.hasPhysicalExamination[DiabetesPhysicalExaminations.Bmi].exists
			&& patient.hasPhysicalExamination[DiabetesPhysicalExaminations.Bmi].hasQuantitativeValue >= 30) {
		
			PatientDemographic memory v16 = PatientDemographic({ hasType: PatientDemographics.ObeseClassI, hasQuantitativeValue: 0, exists: true });
			patient.hasDemographic[v16.hasType] = v16;
		}
		
		if (patient.hasLabTest[DiabetesLaboratoryTests.Hba1c].exists
			&& patient.hasLabTest[DiabetesLaboratoryTests.Hba1c].hasQuantitativeValue >= 6
			&& patient.hasPhysicalExamination[DiabetesPhysicalExaminations.HistoryOfPrediabetes].exists
			&& patient.hasDemographic[PatientDemographics.Age].exists
			&& patient.hasDemographic[PatientDemographics.Age].hasQuantitativeValue >= 25
			&& patient.hasDemographic[PatientDemographics.Age].hasQuantitativeValue <= 59
			&& patient.hasLabTest[DiabetesLaboratoryTests.Fpg].exists
			&& patient.hasLabTest[DiabetesLaboratoryTests.Fpg].hasQuantitativeValue >= 110
			&& patient.hasDemographic[PatientDemographics.ObeseClassI].exists) {
		
			DrugSubplan memory v17 = DrugSubplan({ hasType: DrugSubplans.MonotherapyPlan, hasDrugParticipant: DiabetesDrugs.Metformin, exists: true });
			patient.hasPart[v17.hasType] = v17;
		
			emit NewTreatmentSubPlan(block.timestamp);
		}
	}
	
	function update(string memory newMessage) public {
		message = newMessage;
	}}