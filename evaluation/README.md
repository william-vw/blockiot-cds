
## Installation Instructions

Create a `.env` file with the following private information:
```
API_URL = "https://eth-ropsten.alchemyapi.io/v2/your-api-key"
PRIVATE_KEY = "your metamask private key"
ETHERSCAN_API_KEY = "your etherscan key"
```

Install the dotenv package in your project directory:
`npm install dotenv --save`

Install `ethers.js`
`npm install --save-dev @nomiclabs/hardhat-ethers "ethers@^5.0.0"`

Compile the smart contract using Hardhat:
`npx hardhat compile`

Deploy the contract:
`npx hardhat run scripts/deploy.js --network ropsten`

(The deployed contract will appear in the [ropsten test network](https://ropsten.etherscan.io/).)

To interact with the smart contract: `npx hardhat run scripts/interact.js --network ropsten`

Verifying the smart contract:
`npx hardhat verify --network ropsten DEPLOYED_CONTRACT_ADDRESS ORIGINAL_STRING_PASSED_TO_CONSTRUCTOR_ON_DEPLOY`
(Contract appears at https://ropsten.etherscan.io/address/<ADDRESS>#code)


## Initial Project Instructions

If you have not installed HardHat, run:
`npm install --save-dev hardhat`
