import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class ApplicationTest {

    @Test
    public void isJDBCDriver_Loaded() {
        boolean isExists;
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            isExists = true;
        } catch( ClassNotFoundException e ) {
            isExists = false;
        }

        Assert.assertTrue(isExists);
    }

    @Test
    public void isConnection_Active() throws Exception {
        boolean isConnection_Active;
        Connection connection = null;
        connection = DriverManager.getConnection("jdbc:hsqldb:file:C:/Users/PRAISE/coding.challenge/events_db", "sa", "root");
        isConnection_Active = connection != null;
        Assert.assertTrue(isConnection_Active);
    }

    @Test
    public void isLogfile_Present() throws Exception {
        boolean isLogfile_Present;
        InputStream input = Main.class.getResourceAsStream("/logfile.txt");
        BufferedReader buff = new BufferedReader(new InputStreamReader(input));
        isLogfile_Present = buff.ready();
        Assert.assertTrue(isLogfile_Present);
    }

    @Test
    public void isJSON_log_valid() throws Exception {
        boolean isJSON_valid = false;
        InputStream input = Main.class.getResourceAsStream("/logfile.txt");
        BufferedReader buff = new BufferedReader(new InputStreamReader(input));
        String line;
        while (buff.ready()) {
            // reads each line of file
            line = buff.readLine();
            //Initialize jsonobject and assign new line as an Object
            JSONObject jsonObject = new JSONObject(line);

            //Get Log timestamp from JSONObject
            if(jsonObject.has("id") && jsonObject.has("state") && jsonObject.has("timestamp")){
                isJSON_valid = true;
            }
            else{
                isJSON_valid = false;
                break;
            }
        }
        Assert.assertTrue(isJSON_valid);
    }

   



}
