# MIMSAccession
Accession management software for the MIMS project

## Instructions for running the project
- Make sure postgres is installed and running
- Ensure that you have created a database for use with the accessioning system
- Make sure application.properties has the following properties set:
  - **spring.datasource.url** - must be set to the url, port and database name you wish to use. Default is jdbc:postgresql://localhost:5432/accession
  - **spring.datasource.username** - the username you used to create your database on postgres
  - **spring.datasource.password** - the password you used to create your database on postgres
  - **spring.jpa.hibernate.ddl-auto** - this determines when hibernate will create/update your database structure. Be careful with this one. For testing, you can use
  create-drop, but make sure you do not deploy this to production as it will kill all the data in your database. 
    - it would be better to use update. If you want to clear your database for a clean test, use the *deletescript.sql* provided
- For other settings, the following will need to be set:
  - **logging.file** - this is where the logging file will live
  - **base.folder** - this is where your data will sit before it gets accessioned
  - **data.folder** - this is where your data will go to after it has been accessioned
  - **next.accession.number** - this is the number you'll need to provide on the first run of the app. It indicates what 
  the first number is that the accession will use. If this is a clean system, this number will be 1. Otherwise, whatever number is next in line.
  - **odp.api.key** - this is number you'll get from the CKAN API.
  - **odp.collection.key** - the collection number that the ODP uses to identify data as MIMS related
  - **odp.schema.key** - the schema key for the metadata
  - **odp.external.url** - the actual url to hit when submitting data to the ODP
  - **admin.user** - the admin user's email address. If you change this, you'll have to change it in */src/main/resources/user-data.json* too
- development and debugging settings
  - **use.odp** - set to true if you want to connect to the ODP

The next step is building and packaging the project.
- from the directory in which the pom.xml file exists, use the command *mvn clean package -DskipTests*
- once it's finished building, you can get the packaged jar from */target/accession-0.0.1-SNAPSHOT.jar*
- next, you need to run the application using *java -jar accession-0.0.1-SNAPSHOT.jar*

The application automatically populates the admin user on the first run of the app. The next step is to log in as the admin
user. Once you've done that, you will see a button on the home page called *Populate db*. Click that to populate the 
database with the **next.accession.number**. Do not click this again!!


