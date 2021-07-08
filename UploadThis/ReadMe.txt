To build the application you will need the following:

JDK 11
Gradle 4+ (Gradle 7.1 was used)
Spring Framework (boot)
your personal IDE of choice (Eclipse with spring and gradle Plugin was used)

## How to compile
To compile have Gradle installed. 
Start a command shell and navigate to the Project, i.e.: c:/UploadThis

In the rootfolder of the Project type into the shell "gradle build"
This will build your jar file for the API.

You may adjust the version number in the build.gradle file.


## How to run the API
Open a shell and navigate to UploadThis/build/libs.
Here you will find the built jar file. 

Run it via java -jar command. Run the NON-Plain Jar file

This will start Spring Boot and the API will be accessible over localhost:8080

##Behaviour
The API will take 2 Parameters "file" (required) and "sha-256" hash (optional)
If no sha-256 hash was given, the File will be uploaded but only if the file does not already exist. 
If the file exists, the copy action will still run through, but delete the file afterwards (as per design document.) [Recommendation: This is a waste of resources! Change so file is not accepted in the first place]

If a sha-256 hash was given, a .sha-256 file is generated with the hash written to this file, but only if the calculated hash equals the provided hash.

The upload directory is hard coded as: c:/applicant/osmanjacob/uploadthis/uploads
This can be changed in the source code in the application.properties file (requires rebuild)


## POST Request (See example picture upload with postman application)
/upload
parameters:
"file": the file
"sha-256": String of the hash
the response will be the file with all relevant data: fileName, contentType, filesize, fileDownload link, sha256 and Id

#example 
curl:
curl --location --request POST 'http://localhost:8080/uploadFile' \
--form 'file=@"/C:/UploadThis/src/test/resources/ThisIsATest.txt"' \
--form 'sha-256="b56fb2b3adda914fbd64b56fa2fca7a3caffefeaf4724eed2c5c3b20a1f059fa"'


## GET Request (See example picture download with postman application)
/downloadFile/{fileName:.+}
filename: full path to the file
response: the file that was previously uploaded

#example
curl
curl --location --request GET 'http://localhost:8080/downloadFile/ThisIsATest.txt'

