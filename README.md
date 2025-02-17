# Backend

Project settings are found in api/src/main/resources/application.properties, where the following fields are defined:

```
spring.application.name=modbus-backend
modbus.slave.ip=127.0.0.1
modbus.slave.port=502
server.port=8081
```
They are, in order:
* The name of the Springboot application.
* The IP address pyModSlave is using.
* The port pyModSlave is using.
* The port the backend should use.

# Starting The Program

1. Start up *pyModSlave* and make sure the IP address and the port in the lower left match the settings above.
2. Click connect (the cord icon) and select the data tab you want to generate data for.
3. Check the "Sim" box for your selected data and let it run for a few seconds. Once you see data has been created, uncheck the box to "freeze" it like that.
4. Go to api/src/main/java/com/Application.java and run the main method.
5. The backend will start up and connect to *pyModSlave*. It is very important that *pyModSlave* is running before you run the main method.
6. Now follow the instructions over in the frontend README in order to interact with the backend and read information.