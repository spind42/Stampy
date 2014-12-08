/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asia.stampy.client.message.send;

import asia.stampy.common.parsing.StompMessageParser;
import junit.framework.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stephan Spindler -stephan@spindler.priv.at-
 */
public class SendMessageTest {
    
    public SendMessageTest() {
    }

    /**
     * Test of createNewHeader method, of class SendMessage.
     */
    //@Test
    public void testCreateNewHeader() {
        System.out.println("createNewHeader");
        SendMessage instance = new SendMessage();
        SendHeader expResult = null;
        SendHeader result = instance.createNewHeader();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    /**
     * Test of validate method, of class SendMessage.
     */
    @Test
    public void testValidate() {
        System.out.println("validate");
        SendMessage send = new SendMessage("/queue/test.q", "1" );
        
        send.setBody("Hallo Welt");
        
        String message = "SEND\n"
                + "destination:/queue/test.q\n"
                + "receipt:1\n\n"
                + "Hallo Welt"
                + StompMessageParser.EOM;
        
        //System.out.println("message\n " + send.toStompMessage(true) );
        Assert.assertEquals("Should be equal", message, send.toStompMessage(true));

    }
    
}
