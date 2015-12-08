# tsp-ga-solver [![Build Status](https://magnum.travis-ci.com/cgddrd/tsp-ga-solver.svg?token=tCLDqXmwoDrZUXhGxxFz&branch=master)](https://magnum.travis-ci.com/cgddrd/tsp-ga-solver)

SEM/CSM6120 Assignment: Solving Travelling Salesman Problems with Genetic Algorithms

Introduction
------------

This project (TSP-GA-Solver) contains two command-line based Java applications as part of the SEM6120 assignment two submission:

1) TSP-GA-Router - Provides the main GA implementation for solving TSP problems.
2) TSP-Generator - Utility application for generating a CSV file containing a specified number of randomised XY coordinates for use as a data file within the 'TSP-GA-Router' application.

Installation / Running Instructions
-----------------------------------

With the aim of providing a simple and consistent way of running the Java applications across varying platforms, this project makes use of the Gradle build automation system. (See: http://gradle.org for further information on Gradle).

The top-level gradle project (TSP-GA-Solver) is split into two sub-projects, one for each application listed above e.g.:

```
- tsp-ga-solver
              |
              | - tsp-ga-router
              | - tsp-generator
```


Requirements
============

Please be aware, this project requires the following dependencies be installed PRIOR to continuing with the instructions within this document:

1) Java JDK 8 - http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
2) Gradle - http://gradle.org/gradle-download/


Building:
=========

To build both applications run the following commands:

1) `cd tsp-ga-solver`
2) `gradle :tsp-ga-router:assemble`
3) `gradle :tsp-generator:assemble`

After each `assemble` command, Gradle should automatically clean and build the Java projects, making sure to AUTOMATICALLY download any dependency libraries.

Running:
========

TSP-Generator:

Whilst an example TSP datafile is included inside this submission ('tspdata.csv'), should you wish to generate your own, run the following commands:

1) `cd tsp-ga-solver`
2) `gradle :tsp-generator:run -Ptspargs="['-p', '<NO_OF_POINTS>', '-f', '<CSV_FILE_NAME>', '-g', '<MAX_GRID_SIZE>']"`

e.g. `gradle :tsp-generator:run -Ptspargs="['-p', '30', '-f', 'points.csv', '-g', '200']"`

For an explanation of the available CLI arguments, please run:

`gradle :tsp-generator:run -Ptspargs="['-help']"`


TSP-GA-Router:

To run the TSP-GA-Router application, run the following commands:

1) `cd tsp-ga-solver`
2) `gradle :tsp-ga-router:run -Ptspargs="['-cf', '<PATH_TO_XML_CONFIGURATION_FILE>, '-df', '<PATH_TO_CSV_DATA_FILE>, '-e', '<PATH_TO_RESULTS_EXPORT_DIRECTORY>']"`

e.g. `gradle :tsp-ga-router:run -Ptspargs="['-cf', 'experiment-configs/tsp-ex2-config.xml', '-df', 'tsp-data/tspdata.csv', '-e', 'results']"`

PLEASE NOTE:

 - Example XML configuration files (used to run the documented experiments) are available in 'tsp-ga-solver/experiment-configs/'
 - Example TSP data files (used to run the documented experiments) are available in 'tsp-ga-solver/tsp-data/'
 - Example results output (those to run the documented experiments) is available in 'tsp-ga-solver/experiment-results/'
 - `PATH_TO_RESULTS_EXPORT_DIRECTORY` can contain folder paths that are not yet created. These will be created automatically upon exporting of result data.

For an explanation of the available CLI arguments, please run:

`gradle :tsp-ga-router:run -Ptspargs="['-help']"`
