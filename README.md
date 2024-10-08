
# Project description
FXLink is a little tool for handling URLs. Users can categorize and tag URLs based on topics of interest and enrich the data with further descriptions. </br>
Apart from that, FXLink provides import of data as text files, a search filter and backup of data. 

![](/docs/fxlink8.png) 

# Running FXLink
From version 0.8.1, FXLink is built on Java 11. <br/>
From version 0.9.7, FXLink is built on Java 17. <br/>
Depending on which version is used there are two ways to start up:
Either you clone the repository and then run `mvn javafx:run` from the project root directory. This requires that you have maven installed. 
Alternatively, you can do the following:

* Download the latest tar.gz file
* Unpack the file
* Download and unpack javafx-sdk [here](https://gluonhq.com/products/javafx/)
* From version 0.8.1 to 0.9.6: Run the following command: `java --module-path <path-to-javafx-sdk>  --add-modules javafx.controls,java.sql,java.desktop -jar fxlink11-<version>-jar-with-dependencies.jar`
* From version 0.9.7: `java --module-path <path-to-javafx-sdk> --add-exports=java.desktop/com.apple.eio=ALL-UNNAMED --add-modules javafx.controls,java.sql,java.desktop -jar fxlink17-0.9.7-jar-with-dependencies.jar`

Version 0.8.0 and earlier can be started as a usual jar file:
```bash 
java -jar fxlink8-<version>-jar-with-dependencies.jar
```

# Development details

* Java 17, JavaFX
* Maven
* HSQLDB
         
# Version/release history 

## 0.9.9 - 2024-08-15
* Added a follow-up date to keep track on when a link has been followed up

## 0.9.8 - 2024-03-10
* Added possibility to search in the import history

## 0.9.7 - 2024-01-31
* Added support for ranking links higher and lower
* Migrated to Java 17

## 0.9.6 - 2024-01-01
* Added a counter for categorized links in the import stage
* various minor changes in the import stage
* bug fixes to prevent NullPointerExceptions

## 0.9.5 - 2023-11-21
* Added support for ranking links directly in the import stage
* various minor changes and simplifications

## 0.9.4 - 2023-10-09
* Added a filter for follow-up data
* Bug fixes related to follow-up ranks
* Various minor code improvements

## 0.9.3 - 2023-08-30
* Keep the sorting order in the link table after editing a specific link
* Bug fixes related to the category filter

## 0.9.2 - 2023-07-31
* Added support to actively change the follow-up status
* Various UI-related bugs
* Various minor code improvements

## 0.9.1 - 2023-07-06
* Added the follow-up status to various views
* Added support for passively changing the follow-up status 
* Various minor code improvements

## 0.9.0 - 2023-05-29
* Prepared for follow-up status
* Added support for quickly setting the highest and lowest follow-up rank for links
* Made the search case-insensitive

## 0.8.9 - 2023-04-08
* Support for ranking links through a context menu
* Bug fixes

## 0.8.8 - 2023-02-23
* Bug fix regarding import of youtube channel links
* Various minor code improvements

## 0.8.7 - 2022-11-26
* Added support to rank links
* Bug fix related to XML import
* Minor code improvements

## 0.8.6 - 2022-05-15
* Changes to use Naive Bayesian Classifier
* Improved visualisations

## 0.8.5 - 2022-04-21
* added basic support for csv export
* added multiple visualizations/charts
* added support for truncating the database (for lazy developers)
* multiple smaller refactorings

## 0.8.4 - 2022-01-31
* started with experimental machine learning using Naive Bayes
* added a new stage for visualizations
* added support for generating titles for youtube links
* updated the search to allow searches within a specific category
* Minor refactoring to improve testability 

## 0.8.3 - 2021-08-23
* made suggestion generation case insensitive
* improved event handlers
* refactoring to improve testability

## 0.8.2 - 2021-05-18
* added support for generating category suggestions

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
* Added support for generating link title instead of link description
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

