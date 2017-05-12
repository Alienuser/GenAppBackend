# GenAppBackend

## Overview
The GenAppBackend application is the backend for the Mainframe2020 application. Its designed for working on a CICS-Region
with WebSphere Liberty. The backend is written in Java.

## Requirements
The GenAppBackend requires the following applications running on your computer

* The newest version of ant

## Building
After cloning the repository to your computer change in the directory and install the requirements by typing:
```html
ant build
```

After that you should have a new *.war file. You can install this war file on a WebSphere Liberty Server.