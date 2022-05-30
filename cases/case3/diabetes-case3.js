class Patient {
	constructor(hasPatientProfile) {
		this.hasPatientProfile = hasPatientProfile;
	}

	hasPatientProfile;
}

class PatientProfile {
	constructor(hasEthnicity) {
		this.hasEthnicity = hasEthnicity;
	}

	recommendTest = {};
	hasEthnicity;
	hasPhysicalExamination = {};
	hasDemographic = {};
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

class Ethnicity {
	static highRiskEthnicity = 'highRiskEthnicity';

	static pacificIslander = 'pacificIslander';
	static highRiskEthnicity = 'highRiskEthnicity';
	static africanAmerican = 'africanAmerican';
	static asianAmerican = 'asianAmerican';
	static latino = 'latino';
	static nativeAmerican = 'nativeAmerican';

	constructor(type) {
		this.type = type;
	}

	type;
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

class Recommendation {
	static diabetesScreening = 'diabetesScreening';

	constructor(type) {
		this.type = type;
	}

	type;
}

function execute(patient) {
	if (patient.hasPatientProfile != undefined
		&& patient.hasPatientProfile.hasPhysicalExamination[DiabetesPhysicalExamination.bmi] != undefined
		&& patient.hasPatientProfile.hasPhysicalExamination[DiabetesPhysicalExamination.bmi].hasQuantitativeValue >= 25
		&& patient.hasPatientProfile.hasEthnicity != undefined
		&& patient.hasPatientProfile.hasEthnicity.type != Ethnicity.asianAmerican) {
	
		var v4 = new PatientDemographic(PatientDemographic.overweight);
		patient.hasPatientProfile.hasDemographic[v4.type] = v4;
	}
	
	if (patient.hasPatientProfile != undefined
		&& patient.hasPatientProfile.hasPhysicalExamination[DiabetesPhysicalExamination.bmi] != undefined
		&& patient.hasPatientProfile.hasPhysicalExamination[DiabetesPhysicalExamination.bmi].hasQuantitativeValue >= 23
		&& patient.hasPatientProfile.hasEthnicity.type == Ethnicity.asianAmerican) {
	
		var v5 = new PatientDemographic(PatientDemographic.overweight);
		patient.hasPatientProfile.hasDemographic[v5.type] = v5;
	}
	
	if (patient.hasPatientProfile != undefined
		&& patient.hasPatientProfile.hasEthnicity != undefined
		&& (patient.hasPatientProfile.hasEthnicity.type == Ethnicity.africanAmerican
		|| patient.hasPatientProfile.hasEthnicity.type == Ethnicity.latino
		|| patient.hasPatientProfile.hasEthnicity.type == Ethnicity.nativeAmerican
		|| patient.hasPatientProfile.hasEthnicity.type == Ethnicity.asianAmerican
		|| patient.hasPatientProfile.hasEthnicity.type == Ethnicity.pacificIslander)) {
	
		patient.hasPatientProfile.hasEthnicity.type = Ethnicity.highRiskEthnicity;
	}
	
	if (patient.hasPatientProfile != undefined
		&& patient.hasPatientProfile.hasDemographic[PatientDemographic.overweight] != undefined
		&& patient.hasPatientProfile.hasEthnicity.type == Ethnicity.highRiskEthnicity) {
	
		var v6 = new Recommendation(Recommendation.diabetesScreening);
		patient.hasPatientProfile.recommendTest[v6.type] = v6;
	}
}