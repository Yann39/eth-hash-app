<img src="logo-ethereum.svg" width="380" alt="Ethereum logo">

# Document hashes on Ethereum blockchain from Java

Storing and reading document hashes on the Ethereum blockchain from Java.

![Version](https://img.shields.io/badge/version-1.0.1-2AAB92.svg)
![Static Badge](https://img.shields.io/badge/Last%20update-05%20May%202022-blue)

![Version](https://img.shields.io/badge/JDK-17-red.svg)
![Version](https://img.shields.io/badge/Spring%20Boot-2.5.12-green.svg)
![Version](https://img.shields.io/badge/ZK-9.6.1-blue.svg)
![Version](https://img.shields.io/badge/Bootstrap-5.1.3-purple.svg)
![Version](https://img.shields.io/badge/Web3j-5.0.0-yellow.svg)

---

# Table of Contents

* [About the Project](#about-the-project)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Usage](#usage)
* [License](#license)

# About the Project

<table>
  <tr>
    <td>
        <img alt="Java logo" src="logo-java.svg" height="72"/>
    </td>
    <td>
        <img alt="Spring logo" src="logo-spring.svg" height="36"/>
    </td>
    <td>
        <img alt="ZK logo" src="logo-zk.svg" height="96"/>
    </td>
    <td>
        <img alt="H2 logo" src="logo-h2.svg" height="64"/>
    </td>
  </tr>
</table>

This project demonstrate **document hash** storing on the **Ethereum blockchain** from **Java**.

This is a **Java Spring Boot** application with a **ZK** frontend, that use an embedded **H2** database for demonstration purpose.

# Getting Started

## Prerequisites

To run this project in a local environment, you need :
- a local Ethereum blockchain running on `localhost` on port `7545`
- A `Diploma` smart contract deployed to handle document hash read and write

See the [eth-hash-chain](https://github.com/Yann39/eth-hash-chain) project for setting up a local blockchain with all the required capabilities.

## Installation

1. Clone the repository :
    ```shell script
    git clone https://github.com/Yann39/eth-hash-app
    ```
2. Replace `application.account-address` and `application.contract-address` values in _application.properties_
   to match your account and smart contract addresses according to your running blockchain
3. Build the JAR file using **Maven** :
   ```shell script
   mvn package
   ```
4. Start your local blockchain
5. Execute application from Intellij or run the JAR file, with the right profile :
   ```shell script
   java -Dspring.profiles.active=local -jar /target/eth-hash-app.jar
   ```
6. Reach <http://localhost:8080/>

# Usage

Simply reach the application home page at <http://localhost:8080/> to display the home page.

Navigate to the **Documents** menu, or direct access : <http://localhost:8080/diplomas>

Then simply add documents and try to save hashes to the Ethereum blockchain.

# License

[General Public License (GPL) v3](https://www.gnu.org/licenses/gpl-3.0.en.html)

This program is free software: you can redistribute it and/or modify it under the terms of the GNU
General Public License as published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not,
see <http://www.gnu.org/licenses/>.