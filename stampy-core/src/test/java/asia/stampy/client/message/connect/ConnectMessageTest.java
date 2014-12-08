/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asia.stampy.client.message.connect;

import asia.stampy.common.parsing.StompMessageParser;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stephan Spindler -stephan@spindler.priv.at-
 */
public class ConnectMessageTest {
    
    public ConnectMessageTest() {
    }

    /**
     * Test of createNewHeader method, of class ConnectMessage.
     */
    //@Test
    public void testCreateNewHeader() {
        System.out.println("createNewHeader");
        ConnectMessage instance = new ConnectMessage();
        ConnectHeader expResult = null;
        ConnectHeader result = instance.createNewHeader();
        //assertEquals(expResult, result);
        //fail("The test case is a prototype.");
    }

    /**
     * Test of validate method, of class ConnectMessage.
     */
    @Test
    public void testValidate() {
        System.out.println("validate");
        ConnectMessage instance = new ConnectMessage("1.2", "localhost");
         
        String message =
                    "CONNECT\n"
                +   "accept-version:1.2\n"
                +   "host:localhost\n\n"
                +   StompMessageParser.EOM;
        
        Assert.assertEquals("Should equal", message, instance.toStompMessage(true));
        
        //instance.validate();
        //fail("The test case is a prototype.");
    }
    
}
