
# Project description
FXLink is a little tool for handling URLs. Users can categorize and tag URLs based on topics of interest and enrich the data with further descriptions. </br>
Apart from that, FXLink provides import of data as text files, a search filter and backup of data. 

![](/docs/fxlink8.png) 

# Running FXLink
Depending on which version is used, FXLink can be started up in different ways:
From version 0.8.1, FXLink is built on Java 11. There are two ways to start up:
Either you clone the repository and then run `mvn javafx:run` from the project root directory. This requires that you have maven installed. 
Alternatively, you can do the following:

* Download the latest tar.gz file
* Unpack the file
* Download and unpack javafx-sdk [here](https://gluonhq.com/products/javafx/)
* Run the following command: `java --module-path <path-to-javafx-sdk>  --add-modules javafx.controls,java.sql,java.desktop -jar fxlink11-0.8.1-jar-with-dependencies.jar` 

Version 0.8.0 and earlier can be started as a usual jar file:
```bash 
java -jar fxlink8-<version>-jar-with-dependencies.jar
```

# Development details

* Java 11, JavaFX
* Maven
* HSQLDB
         
# Version/release history 

## 0.8.1 - 2020-09-20
* migrated to Java 11

## 0.8.0 - 2020-02-13
* Updated import history stage
* Changed so the user is asked for confirmation before the import history is deleted

## 0.7.9 - 2019-05-25
* Fix to update the list of categories after a new category has been added
* Updated the list of imports to descending order

## 0.7.8 - 2018-12-30
* Added support for an import history, i.e. keeping track of which files have been imported
* Minor bug fixes
* Minor GUI fixes

## 0.7.7 - 2018-06-17

* Added support for creating links that contain four characters in the domain suffix
* Minor bug fixes
* Minor layout fixes

## 0.7.6 - 2018-05-27

* Added category as search criterion
* Added support for generating link itle instead of link description
* Minor GUI changes

## 0.7.5 - 2018-03-31

* Changed the layout of the main stage
* Added support for generating description for links
* minor refactoring

## 0.7.4 - 2018-02-10

* added support for selecting/deselecting all links in the main stage and in the import stage
* minor changes in the user interface
* bug fixes related to populating combo boxes
* minor refactoring

## 0.7.2 - 2017-06-28

* Added a system check to ensure that the required database files exist during application startup

## 0.7.1 - 2017-06-11

* Minor changes regarding layout, update of combo boxes and column order

## 0.7.0 - 2017-05-30 

* First build published here on github
* Provides the features that I originally considered as core, such as handling of urls, categories and tags, simple search, text import, xml backup

