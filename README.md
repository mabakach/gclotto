# gclotto

## Introduction

A simple command line tool written in Java for a geocache series about LOTTO.

The geocache series "Changenlos" starting with https://coord.info/GC3JN6W is all about playing lottery. The owner has written an online lottery where you have to get 3, 4, 5 and 6 right numbers out of 45. 

If played manually it's nearly impossible to guess the right number in a reasonable time. This small application plays until it has made 3, 4, 5 and 6 out of 45 and extracts the URLs to the webpages with the final coordinates of the caches from the result pages.

## Prerequisites

To build and run gclotto you need JDK 8 oder later and MAVEN. 

## Build

run the following command inside your project folder to compile and build it:

maven clean install

## Run gclotto
run the following command inside your project folder.

java -jar ./target/gclotto-jar-with-dependencies.jar
