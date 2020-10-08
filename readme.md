## Solution breakdown
This Java-based solution utilizes a concurrent HashMap and parses the JSONObject
into a HashMap and stores the event ID as a key and the Log details as a tab delimited string Value.
Every first occurrence of an event ID is stored in the Concurrent HashMap and upon a
second occurrence utilizing the method **(map.containsKey(getID))** the duration between
the first and the current timestamps are calculated.
<br /><br />The solution stores the log events with details such as the event duration 
(Difference between timestamps),log type, log host, and log alert to the 
file-based HSQLDB. To test the solution with another input, simply replace the _logfile.txt_ file in the resources directory then build the project and run.
<br /><br />The processes of the applications are logged using a logger dependency for debugging and development purposes.
Unit tests are also employed for various units of the code to ensure reliability.
<br /><br /> **(Running the application)** <hr> 
Simply select the Main.java file and run via cmd. The application reads the logfile.txt file, parses it and the stores the result in the database according to the requirements.

