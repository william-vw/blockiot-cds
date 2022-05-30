// Specifies the version of Solidity, using semantic versioning.
// Learn more: https://solidity.readthedocs.io/en/v0.5.10/layout-of-source-files.html#pragma
pragma solidity ^0.7.0;

contract DiabetesCase2 {
	string public message;
	
	constructor(string memory initMessage) {
		message = initMessage;
	}
	
	
	event NewTreatmentSubPlan(uint time);
	
	
	struct Patient {
		mapping(TreatmentSubplans => TreatmentSubplan) hasPart;
		bool exists;
	}
	
	enum TreatmentSubplans{ LifestyleSubplan, EducationSubplan, DrugSubplan }
	
	struct TreatmentSubplan {
		string label;
		TreatmentSubplans hasType;
		bool exists;
	}
	
	enum BloodPressures{ DiastolicBloodPressure, SystolicBloodPressure }
	
	struct BloodPressure {
		BloodPressures hasType;
		int hasQuantitativeValue;
		bool exists;
	}
	
	
	mapping(address => Patient) patients;
	
	function execute(BloodPressure memory sys, BloodPressure memory dias) public {
		Patient storage patient = patients[msg.sender];
	
		if (sys.hasType == BloodPressures.SystolicBloodPressure
			&& sys.hasQuantitativeValue > 120
			&& dias.hasType == BloodPressures.DiastolicBloodPressure
			&& dias.hasQuantitativeValue > 80) {
		
			TreatmentSubplan memory v11 = TreatmentSubplan({ hasType: TreatmentSubplans.LifestyleSubplan, label: "weight loss if indicated, \n            a Dietary Approaches to Stop Hypertension (DASH)-style eating pattern, \n            including reducing sodium and increasing potassium intake, moderation of alcohol intake, \n            and increased physical activity.", exists: true });
			patient.hasPart[v11.hasType] = v11;
		
			emit NewTreatmentSubPlan(block.timestamp);
		}
		
		if (sys.hasType == BloodPressures.SystolicBloodPressure
			&& sys.hasQuantitativeValue > 140
			&& dias.hasType == BloodPressures.DiastolicBloodPressure
			&& dias.hasQuantitativeValue > 80) {
		
			TreatmentSubplan memory v12 = TreatmentSubplan({ hasType: TreatmentSubplans.DrugSubplan, label: "in addition to lifestyle therapy, \n        have prompt initiation and timely titration of pharmacologic therapy \n        to achieve blood pressure goals", exists: true });
			patient.hasPart[v12.hasType] = v12;
		
			emit NewTreatmentSubPlan(block.timestamp);
		}
	}
	
	function update(string memory newMessage) public {
		message = newMessage;
	}}