# Use Cases

This folder contains the computerization of fragments of Clinical Decision Logic from the 2022 Standards of Medical Care in Diabetes guidelines [^1]. 

For each listed case, we provide the following links:  
- **N3 editor link**: an N3 editor link that is pre-populated with the N3 code together with a test patient; when clicking 'execute', the editor will show any inferences.  

- **Etherscan link**: a link to the smart contract deployed on etherscan, allowing users to interact and make transactions directly to the contract. On the _contract_ tab, click on _Write Contract_; in the field under _1. execute_, copy the provided tuple and click _Write_.  

Each folder contains the following:
- _diabetes-case[n].n3_: the associated CDS model as an N3 rulesset  
- _diabetes-case[n].sol_: auto-generated Solidity code  
- _diabetes-case[n].js_: auto-generated JavaScript code  
- _test-js[n].html_: an HTML file with testcode for the auto-generated JS. Simply open the cloned HTML file in the browser and check the developer console for the output. 

## Cases

1. **Patient Overweight Status**. (code)  
 If BMI lies between 25 and 26.9 (kg/m2) in a patient with Type 2 Diabetes, then the recommended treatment involves diet, physical activity, and behavioral counseling.  
  ([source](https://diabetesjournals.org/clinical/article/40/1/10/139035/Standards-of-Medical-Care-in-Diabetes-2022/\#T8.1))

   - _N3 editor link_: http://ppr.cs.dal.ca:3002/n3/editor/s/prfSSCDO  
   - _etherscan link_: https://ropsten.etherscan.io/address/0x3752aa4971Fb194f95CdFdE24175115FC18a6B1c#writeContract  
   (_tuple_: [ "0", "27", "true" ])

2. **Hypertension Treatment**. (code)   
 **10.7** For patients with blood pressure >120/80 mmHg, lifestyle intervention consists of weight loss when indicated, a Dietary Approaches to Stop Hypertension (DASH)-style eating pattern [..], moderation of alcohol intake, and increased physical activity.  
 **10.8** Patients with confirmed office-based[^2] blood pressure >= 140/90 mmHg should, in addition to lifestyle therapy, have prompt initiation and titration of pharmacologic therapy to achieve blood pressure goals.  
  ([source](https://diabetesjournals.org/clinical/article/40/1/10/139035/Standards-of-Medical-Care-in-Diabetes-2022#4097830))

   - _N3 editor link_: http://ppr.cs.dal.ca:3002/n3/editor/s/vW0GONEI  
   - _etherscan link_: ...  
   (_tuple_: ...)


3. **Diabetes Screening**. (code)  
 Testing should be considered in adults with overweight or obesity (BMI >= 25 kg/m2 or >= 23 kg/m2 in Asian Americans) who have one or more of the following risk factors:  
 - High-risk race/ethnicity (e.g., African American, Latino, Native American, Asian American, Pacific Islander) [..]  
 ([source](https://diabetesjournals.org/clinical/article/40/1/10/139035/Standards-of-Medical-Care-in-Diabetes-2022#4097671))

   - _N3 editor link_: http://ppr.cs.dal.ca:3002/n3/editor/s/XiTr4aKK  
   - _etherscan link_: ...  
   (_tuple_: ...)

 4. **Metformin Therapy**. (code)  
  **3.6** Metformin therapy for prevention of type 2 diabetes should be considered in adults with prediabetes, as typified by the DPP, especially those aged 25–59 years with BMI >=35 kg/m2, higher fasting plasma glucose (e.g., >=110 mg/dL), and higher A1C (e.g., >=6.0%) [and in women with prior GDM].  
   ([source](https://diabetesjournals.org/clinical/article/40/1/10/139035/Standards-of-Medical-Care-in-Diabetes-2022#4097686))

   - _N3 editor link_: http://ppr.cs.dal.ca:3002/n3/editor/s/cRd8gOgt  
   - _etherscan link_: ...  
   (_tuple_: ...)

[^1]: https://diabetesjournals.org/clinical/article/40/1/10/139035/Standards-of-Medical-Care-in-Diabetes-2022
[^2]: For our purpose, we assume an at-home IoMT measurement.