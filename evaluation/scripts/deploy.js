async function main() {
    const DiabetesIot1 = await ethers.getContractFactory("DiabetesIot1");
    const DiabetesIot2 = await ethers.getContractFactory("DiabetesIot2");
    const DiabetesIot3 = await ethers.getContractFactory("DiabetesIot3");
    const DiabetesIot4 = await ethers.getContractFactory("DiabetesIot4");
 
    // Start deployment, returning a promise that resolves to a contract object
    const diabetes_iot_1 = await DiabetesIot1.deploy("DiabetesIot1");
    const diabetes_iot_2 = await DiabetesIot2.deploy("DiabetesIot2");
    const diabetes_iot_3 = await DiabetesIot3.deploy("DiabetesIot3");
    const diabetes_iot_4 = await DiabetesIot4.deploy("DiabetesIot4");
    console.log("Contract 1 deployed to address:", diabetes_iot_1.address);
    console.log("Contract 2 deployed to address:", diabetes_iot_2.address);
    console.log("Contract 3 deployed to address:", diabetes_iot_3.address);
    console.log("Contract 4 deployed to address:", diabetes_iot_4.address);
  
  }
 
 main()
   .then(() => process.exit(0))
   .catch(error => {
     console.error(error);
     process.exit(1);
   });
 