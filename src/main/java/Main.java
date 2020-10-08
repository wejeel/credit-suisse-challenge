import ch.qos.logback.classic.Logger;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.concurrent.ConcurrentHashMap;


public class Main {
    //Initialize logger instance
    private static final Logger logger = (Logger) LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        //initialize ConcurrentHashMap hash map to store logs
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

        try {
            //DB Connection
            Connection connection = null;

            //Prepared statement to mitigate SQL Injection attacks
            PreparedStatement insertStatement = null;

            //load JDBC Driver
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            logger.info("HSQLDB Driver loaded");

            try {
                //Get connection to HSQL file-based DB
                connection = DriverManager.getConnection("jdbc:hsqldb:file:C:/Users/PRAISE/coding.challenge/events_db", "sa", "root");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //Variable to store each line from logfile.txt
            String line;

            //Stream logfile content by line
            InputStream input = Main.class.getResourceAsStream("/logfile.txt");

            // wrap InputStreamReader in BufferedReader
            BufferedReader buff = new BufferedReader(new InputStreamReader(input));

            // InputStreamReader read = new InputStreamReader(input);
            while (buff.ready()) {

                // reads each line of file
                line = buff.readLine();

                //Initialize jsonobject and assign new line as an Object
                JSONObject jsonObject = new JSONObject(line);

                //Get Log timestamp from JSONObject
                int getTimestamp = jsonObject.getInt("timestamp");

                //Get Log ID from JSONObject
                String getID = jsonObject.getString("id");

                //Initialize Log Type and assign to empty to due to dynamic nature of log entries, can be present or not
                String getType = "";

                //Initialize Log Host and assign to empty to due to dynamic nature of log entries, can be present or not
                int getHost = 0;

                //Type flag
                boolean isTypePresent = false;

                //Host flag
                boolean isHostPresent = false;

                //HashMap value
                String mapValue = getTimestamp.toString();

                //Flagging the log, if JSONObject contains log type value
                if(jsonObject.has("type")){
                    getType = jsonObject.getString("type");
                    mapValue+="\t"+getType;
                    isTypePresent = true;
                }

                //Flagging the log, if JSONObject contains log host value
                if(jsonObject.has("host")){
                    getHost = jsonObject.getInt("host");
                    mapValue+="\t"+getHost;
                    isHostPresent = true;
                }

                //Logic for case whereby an entry already exist in HashMap
                if(map.containsKey(getID)){

                    //Insert query to store event in file-based HSQLDB
                    String INSERT_QRY = "INSERT INTO logresults (event_id, event_duration, event_type, event_host, alert) VALUES ?,?,?,?,?";

                    //Get Map value with ID as Key and split the value with tab delimeter and store as Array
                    String[] getMapVal = map.get(getID).toString().split("\t");

                    //Get previously stored timestamp present in the HashMap
                    int getPrevTime = Integer.parseInt(getMapVal[0]);

                    //Calculate duration by subtracting prev and current and convert to absolute value
                    int result = Math.abs(getTimestamp-getPrevTime);

                    //Initialize log type as null
                    String logType = null;

                    //Initialize log host as 0
                    int logHost = 0;

                    //Initialize log status, > 4 equals true and < 4 equals false
                    boolean logStatus;

                    //Assigning value to log type if value was present in JSONObject
                    if(getMapVal.length == 2){  logType = getMapVal[1];  }else if (isTypePresent){ logType = getType;}

                    //Assigning value to log host if value was present in JSONObject
                    if(getMapVal.length == 3){  logHost = Integer.parseInt(getMapVal[2]); }else if (isHostPresent){ logHost = getHost;}

                    //Assigning log status based on logic ( > 4 equals true and < 4 equals false)
                    logStatus = result > 4;

                    logger.info(getID+"\t"+result+"\t"+logType+"\t"+logHost+"\t"+logStatus, Main.class.getSimpleName());

                    if (connection == null){
                        logger.info("connection null");
                        return;
                    }

                    try {
                        //Insert event details to HSQLDB and close connection after
                        insertStatement = connection.prepareStatement(INSERT_QRY);
                        insertStatement.setString(1, getID);
                        insertStatement.setInt(2, result);
                        insertStatement.setString(3, logType);
                        insertStatement.setString(4, logHost.toString());
                        insertStatement.setBoolean(5, logStatus);
                        insertStatement.executeUpdate();
                        connection.commit();
                        connection.close();

                        logger.info("Event "+getID+" stored to HSQLDB - event_db ");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }else{
                    //ID is not a key in the HashMap, therefore put the ID as Key with the Value
                    map.put(getID, mapValue);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }
}


