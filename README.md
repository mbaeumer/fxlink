
# Project description
FXLink is a little tool for handling URLs. Users can categorize and tag URLs based on topics of interest and enrich the data with further descriptions. </br>
Apart from that, FXLink provides import of data as text files, a search filter and backup of data in XML. 

![](/docs/fxlink8.png) 

# Running FXLink
```bash 
java -jar fxlink8-<version>-jar-with-dependencies.jar
```

# Development details

* Java 8, JavaFX
* Maven
* HSQLDB
         
# Version/release history 

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

