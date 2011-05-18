A guide to installing and running the soap proxy web service
============================================================

1. Make sure you have Java and Ant installed
2. Install GIT version control system
3. Install Google Web Toolkit 2.0.4 (not tested with newer versions)
4. Go to the directory where you want to install the application
5. Checkout project "git clone git://github.com/karli/Automatic-soap-widget-generator.git soapproxy"
   NB! Be patient, checkout takes a lot of time because of the amount of dependent jars that need to be downloaded
6. If checkout is complete, edit build.properties file and specify the location of GWT installation (property "gwt.sdk.location")
7. Also edit start.sh and specify the preferred port for the application to run on. (currently JAVA_OPTS="-Djetty.port=8833")
8. "touch soapproxy.pid", this is the file where process ID is saved
9. Create folder "logs" to the project root directory. This is where logs will be saved.
10. Deploy application by executing "./redeploy.sh"
11. To stop the application, call "./stop.sh"

When there is a newer version in GIT that needs to be deployed, just execute "./redeploy.sh"

