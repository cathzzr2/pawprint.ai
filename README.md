# Virtual Pet

## Description
A virtual pet that help user to interact with user's pet by upload images, memories into 
LLMs, improve old photo qualities, and generate different genres images by upload a photo.


## Local Development
1. Clone the repository
2. Start up docker 
3. Run local development environment with docker compose
note:
- must run in the local-dev directory
- use `-d` to run in detached mode
- use `--build` to rebuild the image
- if postgres/mongodb is not working / able to connect, make sure no local postgres is installed on your computer
```bash
> cd local-dev
> docker-compose up -d
```

when you are done, you can stop the docker container by running
```bash
> docker-compose down
```
note:
- this will stop and remove the container
- user `-v` to remove the volume as well (this will remove all the data in the database)

### How to run the application
```bash
> ./gradlew bootRun
```

### How to run the test
```bash
> ./gradlew test
```

### More information about what task you can run with gradle
```bash
> ./gradlew tasks
```

## Contributing
1. Create a new branch with the following naming convention:
   ```
   {name}/{description}
   ```
   For example:
   ```
   abx/add-new-endpoint
   ```
2. Make your changes.
3. Check your changes by running the test.
4. Run style check and formatting
5. Create a pull request to the `main` branch.
6. Fill in the pull request template.
7. Ask for a review by sending the PR link to the reviewer.
To check style
```bash
./gradlew checkstyle
```
note: check style config is located in `config/checkstyle/checkstyle.xml`
To auto format
```bash
./gradlew spotlessJavaApply
```



## DB Migration
#### How to run the migration script
```bash
./gradlew flywayMigrate
```

#### How to create a new migration script
All migration scripts are stored in `src/main/resources/db/migration`. 
To create a new migration script, follow these steps:
1. Create a new file in the `src/main/resources/db/migration` directory with the following naming convention:
   ```
   V{version}__{description}.sql
   ```
   For example:
   ```
   V1__create_pet_table.sql
   ```
2. Write your SQL script in the file you created.
3. If you want to use UUID, make sure add the following line at the beginning of your script:
   ```sql
   CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
   ```
4. Make sure correct schema is used in the script. For example:
   ```sql
   CREATE TABLE virtual_pet_schema.pet (
       ...
   );
   ```
5. Run the migration script using the command above.

## How to add dependencies
1. Find the dependency in the [Maven Repository](https://mvnrepository.com/).
2. Copy the gradle(kotlin) code.
3. Add the dependency in the `build.gradle.kts` file.

## GitHub Actions
This project uses GitHub Actions for CI/CD. The workflow is defined in `.github/workflows/gradle-build.yaml`.