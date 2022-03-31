# store-app

The idea of this app is to centralize and properly connect all store and product related information.
<br /><br />
For this purpose there are 5 Jobs responsible for getting data from the APIs, handling all these information, and then store it properly in a Postgres database.
<br /><br />

__Technologies used__

  In this project was used the following languages, frameworks, libs, and database:
  - Kotlin
  - Spring Boot, Spring Data, Spring Retry
  - Postgres SQL
  - Open CSV, Swagger, Spring Mockk, 
  - Docker, Docker-Compose
<br/><br/>

Below are the descriptions and functions of all job process that are running in sequence:

__ClustersJob__

  Responsible to collect all clusters information.
  - Properties in the project: clusters
<br/>

__RegionsJob__

  Responsible to collect all regions information.
  - Properties in the project: regions
<br/><br/>

__StoresJob__

  Responsible to collect all store information.
  - Properties in the project: stores
<br/><br/>

__CsvProductDataJob__

  Responsible to get and extract the CSV data from the API and persist it in the Product table.
  - Properties in the project: csvProducts
<br/><br/>

__ProductsJob__

  Responsible to get the product data from the API and persist it in the Product table.
  - Properties in the project: products
<br/><br/>

__Steps to run the project__
       
  1. Clone the project and in the root folder run the docker-compose command below
      ```bash
           docker-compose -f docker-compose.yml up -d
      ```
  2. After that the app should be working and you can access the api documentation in the follow url: 
  __http://localhost:8080/storeApp/swagger-ui/#/__
  
  3. The job processes are scheduled to run every one hour. You can change the time in the properties file
  <br/><br/>
  
     
