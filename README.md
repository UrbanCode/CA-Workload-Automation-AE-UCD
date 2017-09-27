# IBM UrbanCode Deploy - CA Workload Automation AE (Autosys) Plug-in
---
Note: This is not the plugin distributable! This is the source code. You must download the source code and build your own plug-in zip.

### License
This plug-in is protected under the [Eclipse Public 1.0 License](http://www.eclipse.org/legal/epl-v10.html)

### Compatibility
	This plug-in requires version 6.1.1 or later of IBM UrbanCode Deploy.

### Installation
	The packaged zip is located in the releases folder. No special steps are required for installation. See Installing plug-ins in UrbanCode Deploy. Download this zip file if you wish to skip the manual build step. Otherwise, download the entire Autosys-UCD project and run the `gradle` command in the top level folder. This should compile the code and create a new distributable zip within the `build/distributions` folder. Use this command if you wish to make your own changes to the plug-in.

### History
    Version 3
        - Community GitHub Release.
    Version 2
        - Support property file encryption.
    Version 1
        - Send AutoSys commands and check the status of a CA Workload Automation AE server.

### How to build the plug-in from command line:

1. Navigate to the base folder of the project through command line.
2. Make sure that there is a build.gradle file in the root directory and execute the 'gradle' command.
3. The built plug-in is located at `build/distributions/Autosys-UCD-vdev.zip`
