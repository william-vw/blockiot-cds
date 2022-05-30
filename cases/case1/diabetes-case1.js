class Patient {
	constructor(hasPatientProfile) {
		this.hasPatientProfile = hasPatientProfile;
	}

	hasPatientProfile;
}

class PatientProfile {
	constructor(hasTreatmentPlan, hasDiagnosis) {
		this.hasTreatmentPlan = hasTreatmentPlan;
		this.hasDiagnosis = hasDiagnosis;
	}

	hasDemographic = {};
	hasTreatmentPlan;
	hasDiagnosis;
}

class PatientDemographic {
	static residence = 'residence';
	static breastFeeding = 'breastFeeding';
	static maritalStatus = 'maritalStatus';
	static weight = 'weight';
	static levelOfEducation = 'levelOfEducation';
	static overweight = 'overweight';
	static height = 'height';
	static gender = 'gender';
	static activityLevel = 'activityLevel';
	static age = 'age';
	static pregnancyState = 'pregnancyState';
	static job = 'job';

	constructor(type) {
		this.type = type;
	}

	type;
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

class TreatmentPlan {

	hasPart = {};
}

class TreatmentSubplan {
	static lifestyleSubplan = 'lifestyleSubplan';
	static educationSubplan = 'educationSubplan';
	static drugSubplan = 'drugSubplan';

	constructor(type, label) {
		this.label = label;
		this.type = type;
	}

	label;
	type;
}

class DiabetesDiagnosis {
	constructor(hasDiabetesType) {
		this.hasDiabetesType = hasDiabetesType;
	}

	hasDiabetesType;
}

class DiabetesMellitus {
	static diabetesMellitusDuringPregnancyandChildbirthAndThePuerperium = 'diabetesMellitusDuringPregnancyandChildbirthAndThePuerperium';
	static diabetesMellitusWithoutComplication = 'diabetesMellitusWithoutComplication';
	static type2DiabetesMellitus = 'type2DiabetesMellitus';

	constructor(type) {
		this.type = type;
	}

	type;
}

function execute(exam, patient) {
	if (patient.hasPatientProfile != undefined
		&& exam.type == DiabetesPhysicalExamination.bmi
		&& exam.hasQuantitativeValue >= 25) {
	
		var v0 = new PatientDemographic(PatientDemographic.overweight);
		patient.hasPatientProfile.hasDemographic[v0.type] = v0;
	}
	
	if (patient.hasPatientProfile != undefined
		&& patient.hasPatientProfile.hasTreatmentPlan != undefined
		&& patient.hasPatientProfile.hasDiagnosis != undefined
		&& patient.hasPatientProfile.hasDiagnosis.hasDiabetesType != undefined
		&& patient.hasPatientProfile.hasDiagnosis.hasDiabetesType.type == DiabetesMellitus.type2DiabetesMellitus
		&& patient.hasPatientProfile.hasDemographic[PatientDemographic.overweight] != undefined) {
	
		var v1 = new TreatmentSubplan(TreatmentSubplan.lifestyleSubplan, "diet, physical activity, and behavioral counseling");
		patient.hasPatientProfile.hasTreatmentPlan.hasPart[v1.type] = v1;
	}
}