class Patient {
	constructor(hasPatientProfile) {
		this.hasPatientProfile = hasPatientProfile;
	}

	hasPatientProfile;
}

class PatientProfile {
	constructor(hasTreatmentPlan) {
		this.hasTreatmentPlan = hasTreatmentPlan;
	}

	hasPhysicalExamination = {};
	hasDemographic = {};
	hasTreatmentPlan;
	hasLabTest = {};
}

class DiabetesPhysicalExamination {
	static bmi = 'bmi';
	static familyHistoryOfType1DiabetesMellitus = 'familyHistoryOfType1DiabetesMellitus';
	static smoking = 'smoking';
	static personalHistoryOfHemochromatosis = 'personalHistoryOfHemochromatosis';
	static familyHistoryOfType2DiabetesMellitus = 'familyHistoryOfType2DiabetesMellitus';
	static oralExam = 'oralExam';
	static eyeExam = 'eyeExam';
	static vitalSign = 'vitalSign';
	static thyroidFunction = 'thyroidFunction';
	static babyDeliveredWeighingMoreThan4pt5Kg = 'babyDeliveredWeighingMoreThan4pt5Kg';
	static lostFootSensation = 'lostFootSensation';
	static firstDegreeRelativeWithDiabetes = 'firstDegreeRelativeWithDiabetes';
	static historyOfGestationalDiabetes = 'historyOfGestationalDiabetes';
	static highRiskPopulation = 'highRiskPopulation';
	static waistCircumference = 'waistCircumference';
	static familyHistoryOfHemochromatosis = 'familyHistoryOfHemochromatosis';
	static physicallyInactive = 'physicallyInactive';
	static drinkingAlcohol = 'drinkingAlcohol';
	static familyHistoryOfGestationalDiabetesMellitus = 'familyHistoryOfGestationalDiabetesMellitus';
	static historyOfPrediabetes = 'historyOfPrediabetes';

	constructor(type, hasQuantitativeValue) {
		this.type = type;
		this.hasQuantitativeValue = hasQuantitativeValue;
	}

	type;
	hasQuantitativeValue;
}

class PatientDemographic {
	static residence = 'residence';
	static breastFeeding = 'breastFeeding';
	static maritalStatus = 'maritalStatus';
	static weight = 'weight';
	static levelOfEducation = 'levelOfEducation';
	static height = 'height';
	static gender = 'gender';
	static activityLevel = 'activityLevel';
	static age = 'age';
	static obeseClassI = 'obeseClassI';
	static pregnancyState = 'pregnancyState';
	static job = 'job';

	constructor(type, hasQuantitativeValue) {
		this.type = type;
		this.hasQuantitativeValue = hasQuantitativeValue;
	}

	type;
	hasQuantitativeValue;
}

class TreatmentPlan {

	hasPart = {};
}

class DrugSubplan {
	static monotherapyPlan = 'monotherapyPlan';
	static dualTherapyPlan = 'dualTherapyPlan';

	constructor(type, hasDrugParticipant) {
		this.hasDrugParticipant = hasDrugParticipant;
		this.type = type;
	}

	hasDrugParticipant;
	type;
}

class DiabetesDrug {
	static dopamineAgonist = 'dopamineAgonist';
	static otherDrug = 'otherDrug';
	static combinedDrug = 'combinedDrug';
	static insulin = 'insulin';
	static thiazolidinedione = 'thiazolidinedione';
	static sulfonylurea = 'sulfonylurea';
	static incretin = 'incretin';
	static meglitinide = 'meglitinide';
	static alphaGlucosidaseInhibitor = 'alphaGlucosidaseInhibitor';

	static metformin = 'metformin';

	constructor(type) {
		this.type = type;
	}

	type;
}

class DiabetesLaboratoryTest {
	static bloodKetone = 'bloodKetone';
	static plasmaBicarbonate = 'plasmaBicarbonate';
	static bloodGlucoseTest = 'bloodGlucoseTest';
	static lipidProfile = 'lipidProfile';
	static serumFetuinA = 'serumFetuinA';
	static serumOsmolality = 'serumOsmolality';
	static autoantibody = 'autoantibody';
	static serumAdiponectin = 'serumAdiponectin';
	static hematologicalProfile = 'hematologicalProfile';
	static insulinMeasurement = 'insulinMeasurement';
	static kidneyFunctionTest = 'kidneyFunctionTest';
	static tumorMarker = 'tumorMarker';
	static urineAnalysis = 'urineAnalysis';
	static plasmaCreatinine = 'plasmaCreatinine';
	static liverFunctionTest = 'liverFunctionTest';
	static hba1c = 'hba1c';
	static fpg = 'fpg';

	constructor(type, hasQuantitativeValue) {
		this.type = type;
		this.hasQuantitativeValue = hasQuantitativeValue;
	}

	type;
	hasQuantitativeValue;
}

function execute(patient) {
	if (patient.hasPatientProfile != undefined
		&& patient.hasPatientProfile.hasPhysicalExamination[DiabetesPhysicalExamination.bmi] != undefined
		&& patient.hasPatientProfile.hasPhysicalExamination[DiabetesPhysicalExamination.bmi].hasQuantitativeValue >= 30) {
	
		var v7 = new PatientDemographic(PatientDemographic.obeseClassI, 0);
		patient.hasPatientProfile.hasDemographic[v7.type] = v7;
	}
	
	if (patient.hasPatientProfile != undefined
		&& patient.hasPatientProfile.hasTreatmentPlan != undefined
		&& patient.hasPatientProfile.hasLabTest[DiabetesLaboratoryTest.hba1c] != undefined
		&& patient.hasPatientProfile.hasLabTest[DiabetesLaboratoryTest.hba1c].hasQuantitativeValue >= 6
		&& patient.hasPatientProfile.hasPhysicalExamination[DiabetesPhysicalExamination.historyOfPrediabetes] != undefined
		&& patient.hasPatientProfile.hasDemographic[PatientDemographic.age] != undefined
		&& patient.hasPatientProfile.hasDemographic[PatientDemographic.age].hasQuantitativeValue >= 25
		&& patient.hasPatientProfile.hasDemographic[PatientDemographic.age].hasQuantitativeValue <= 59
		&& patient.hasPatientProfile.hasLabTest[DiabetesLaboratoryTest.fpg] != undefined
		&& patient.hasPatientProfile.hasLabTest[DiabetesLaboratoryTest.fpg].hasQuantitativeValue >= 110
		&& patient.hasPatientProfile.hasDemographic[PatientDemographic.obeseClassI] != undefined) {
	
		var v8 = new DrugSubplan(DrugSubplan.monotherapyPlan, DiabetesDrug.metformin);
		patient.hasPatientProfile.hasTreatmentPlan.hasPart[v8.type] = v8;
	}
}