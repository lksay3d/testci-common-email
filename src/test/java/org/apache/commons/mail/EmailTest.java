package org.apache.commons.mail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EmailTest {
    private static String[] TEST_EMAILS = {"ab@bc.com", "a.b@c.org", "testEmail@gmail.com"};
    private static String[] TEST_EMPTY_EMAILS = {};
    private static String TEST_EMAIL = "testEmail@gmail.com";
    private static String TEST_HOST_NAME = "TestHostName";
    private String[] TEST_VALID_CHARS = {" ", "a", "A", "\uc5ec", "0123456789", "01234567890123456789"};
    private Map<String, String> TEST_HEADERS = new HashMap<String, String>() {{
        put("key1", "value1");
        put("key2", "value2");
    }};

    private ConcreteEmail email;
    private Object testObject;

    //setup to initialize the email and test object before each test method
    @Before
    public void setUpEmailTests() throws Exception {
        email = new ConcreteEmail();
        testObject = new Object();
    }

    //beginning of testing for Email addBcc(String... emails) method
    //test to add blind carbon copy (Bcc)
    @Test
    public void testAddBlind() throws Exception {
        email.addBcc(TEST_EMAILS);
        assertEquals(3, email.getBccAddresses().size());
    }

    //test to catch invalid empty email within Bcc
    @Test
    public void testAddBlind_EmptyEmail() throws Exception {
        try {
            email.addBcc(TEST_EMPTY_EMAILS);
        }
        catch(Exception e) {
            assertEquals(0, email.getBccAddresses().size());
        }
    }
    //end of testing for Email addBcc(String... emails) method

    //beginning of testing for Email addCc(String email) method
    @Test
    public void testAddCarbon() throws Exception {
        email.addCc(TEST_EMAIL);
        assertEquals(1, email.getCcAddresses().size());
    }

    //beginning of testing for void addHeader(String name, String value) method
    //test to add header to email
    @Test
    public void testAddHeader() throws Exception {
        email.addHeader("validName", "validValue");
        assertEquals(1, email.getHeaders().size());
    }

    //test to catch invalid name
    @Test
    public void testAddHeader_InvalidName() throws Exception {
        try {
            email.addHeader(null, "validValue");
        }
        catch(Exception e) {
            assertEquals(0, email.getHeaders().size());
        }
    }

    //test to catch invalid value
    @Test
    public void testAddHeader_InvalidValue() throws Exception {
        try {
            email.addHeader("validName", null);
        }
        catch(Exception e) {
            assertEquals(0, email.getHeaders().size());
        }
    }
    //end of testing for void addHeader(String name, String value) method

    //beginning of testing for Email addReplyTo(String email, String name) method
    @Test
    public void testAddReplyTo() throws Exception {
        email.addReplyTo(TEST_EMAIL, "validName");
        assertEquals(1, email.replyList.size());
    }

    //beginning of tests for void buildMimeMessage() method
    //testing for setting an email up with a sender, host, and carbon copied receivers
    @Test
    public void testBuildMimeMessage() throws Exception {
        //arranging
        email.setHostName(TEST_HOST_NAME);
        email.setFrom(TEST_EMAIL);
        email.addCc(TEST_EMAILS);

        //action, building the mime message
        email.buildMimeMessage();

        //assertion, getting the mime message
        assertNotNull(email.getMimeMessage());
    }

    //making sure that multiple/duplicate calls to buildMimeMessage() method are handled correctly
    @Test
    public void testBuildMimeMessage_MessageAlreadyBuilt() throws Exception {
        //arranging
        email.setHostName(TEST_HOST_NAME);
        email.setFrom(TEST_EMAIL);
        email.addCc(TEST_EMAILS);

        //action, building the mime message
        email.buildMimeMessage();

        try {
            email.buildMimeMessage();
        }
        catch (Exception e) {
            //assertion, getting the mime message
            assertNotNull(email.getMimeMessage());
        }
    }

    //making sure that if a subject is already set then buildMimeMessage() method can still function
    @Test
    public void testBuildMimeMessage_SubjectExists() throws Exception {
        //arranging
        email.setHostName(TEST_HOST_NAME);
        email.setFrom(TEST_EMAIL);
        email.addCc(TEST_EMAILS);
        email.setSubject("TestSubject");

        //action, building the mime message
        email.buildMimeMessage();

        //assertion, getting the mime message
        assertNotNull(email.getMimeMessage());
    }

    //same as above, except for subject and charset
    @Test
    public void testBuildMimeMessage_SubjectAndCharsetExists() throws Exception {
        //arranging
        email.setHostName(TEST_HOST_NAME);
        email.setFrom(TEST_EMAIL);
        email.addCc(TEST_EMAILS);
        email.setSubject("TestSubject");
        email.setCharset("UTF-8");

        //action, building the mime message
        email.buildMimeMessage();

        //assertion, getting the mime message
        assertNotNull(email.getMimeMessage());
    }

    //testing for invalid content when buildMimeMessage() method is called
    @Test
    public void testBuildMimeMessage_InvalidContentTypeExists() throws Exception {
        //arranging
        email.setHostName(TEST_HOST_NAME);
        email.setFrom(TEST_EMAIL);
        email.addCc(TEST_EMAILS);
        email.setContent(testObject, "TestContentType");

        //action, building the mime message
        email.buildMimeMessage();

        //assertion, getting the mime message
        assertNotNull(email.getMimeMessage());
    }

    //testing for valid content when buildMimeMessage() method is called
    @Test
    public void testBuildMimeMessage_ValidContentTypeExists() throws Exception {
        //arranging
        email.setHostName(TEST_HOST_NAME);
        email.setFrom(TEST_EMAIL);
        email.addCc(TEST_EMAILS);
        email.setContent("", "text/plain");

        //action, building the mime message
        email.buildMimeMessage();

        //assertion, getting the mime message
        assertNotNull(email.getMimeMessage());
    }

    //testing for an existing body when buildMimeMessage() method is called
    @Test
    public void testBuildMimeMessage_WithEmailBody() throws Exception {
        //arranging
        email.setHostName(TEST_HOST_NAME);
        email.setFrom(TEST_EMAIL);
        email.addCc(TEST_EMAILS);
        email.emailBody = new MimeMultipart();

        //action, building the mime message
        email.buildMimeMessage();

        //assertion, getting the mime message
        assertNotNull(email.getMimeMessage());
    }

    //testing for existing body and content when buildMimeMessage() method is called
    @Test
    public void testBuildMimeMessage_WithEmailBodyAndContentType() throws Exception {
        //arranging
        email.setHostName(TEST_HOST_NAME);
        email.setFrom(TEST_EMAIL);
        email.addCc(TEST_EMAILS);
        email.emailBody = new MimeMultipart();
        email.contentType = "TestContentType";

        //action, building the mime message
        email.buildMimeMessage();

        //assertion, getting the mime message
        assertNotNull(email.getMimeMessage());
    }

    //testing for no existing from address when buildMimeMessage() method is called
    @Test
    public void testBuildMimeMessage_NoFromAddress() throws Exception {
        //arranging
        email.setHostName(TEST_HOST_NAME);
        email.addCc(TEST_EMAILS);

        try {
            //action, building the mime message
            email.buildMimeMessage();
        }
        catch (Exception e) {
            //assertion, getting the mime message
            assertNotNull(email.getMimeMessage());
        }
    }

    //testing for no recipient addresses when buildMimeMessage() method is called
    @Test
    public void testBuildMimeMessage_NoReceivers() throws Exception {
        //arranging
        email.setHostName(TEST_HOST_NAME);
        email.setFrom(TEST_EMAIL);

        try {
            //action, building the mime message
            email.buildMimeMessage();
        }
        catch (Exception e) {
            //assertion, getting the mime message
            assertNotNull(email.getMimeMessage());
        }
    }

    //testing for existing recipient list when buildMimeMessage() method is called
    @Test
    public void testBuildMimeMessage_WithToList() throws Exception {
        //arranging
        email.setHostName(TEST_HOST_NAME);
        email.setFrom(TEST_EMAIL);
        email.addTo(TEST_EMAILS);

        //action, building the mime message
        email.buildMimeMessage();

        //assertion, getting the mime message
        assertNotNull(email.getMimeMessage());
    }

    //testing for existing carbon copy recipients when buildMimeMessage() method is called
    @Test
    public void testBuildMimeMessage_WithCcList() throws Exception {
        //arranging
        email.setHostName(TEST_HOST_NAME);
        email.setFrom(TEST_EMAIL);
        email.addCc(TEST_EMAILS);

        //action, building the mime message
        email.buildMimeMessage();

        //assertion, getting the mime message
        assertNotNull(email.getMimeMessage());
    }

    //testing for existing blind carbon copy recipients when buildMimeMessage() method is called
    @Test
    public void testBuildMimeMessage_WithBccList() throws Exception {
        //arranging
        email.setHostName(TEST_HOST_NAME);
        email.setFrom(TEST_EMAIL);
        email.addBcc(TEST_EMAILS);

        //action, building the mime message
        email.buildMimeMessage();

        //assertion, getting the mime message
        assertNotNull(email.getMimeMessage());
    }

    //testing for existing reply recipients when buildMimeMessage() method is called
    @Test
    public void testBuildMimeMessage_WithReplyList() throws Exception {
        //arranging
        email.setHostName(TEST_HOST_NAME);
        email.setFrom(TEST_EMAIL);
        email.addCc(TEST_EMAILS);
        email.addReplyTo(TEST_EMAIL);

        //action, building the mime message
        email.buildMimeMessage();

        //assertion, getting the mime message
        assertNotNull(email.getMimeMessage());
    }

    //testing for existing headers when buildMimeMessage() method is called
    @Test
    public void testBuildMimeMessage_WithHeaders() throws Exception {
        //arranging
        email.setHostName(TEST_HOST_NAME);
        email.setFrom(TEST_EMAIL);
        email.addCc(TEST_EMAILS);
        email.addHeader("validName", "validValue");

        //action, building the mime message
        email.buildMimeMessage();

        //assertion, getting the mime message
        assertNotNull(email.getMimeMessage());
    }

    //testing for checking pop before Smtp is set up correctly when buildMimeMessage() method is called
    @Test
    public void testBuildMimeMessage_PopBeforeSmtp() throws Exception {
        //arranging
        email.setHostName(TEST_HOST_NAME);
        email.setFrom(TEST_EMAIL);
        email.addCc(TEST_EMAILS);

        try {
            email.setPopBeforeSmtp(true, "", "", "");

            //action, building the mime message
            email.buildMimeMessage();
        }

        catch (Exception e) {
            //assertion, getting the mime message
            assertNotNull(email.getMimeMessage());
        }
    }
    //end of testing for void buildMimeMessage() method

    //beginning of testing for String getHostName() method
    //testing for no session or host when getHostName() method is called
    @Test
    public void testGetHostName_NoSessionOrHost() throws Exception {
        assertNull(email.getHostName());
    }

    //testing for existing session when getHostName() method is called
    @Test
    public void testGetHostName_WithSession() throws Exception {
        email.setMailSession(Session.getInstance(new Properties()));
        assertNull(email.getHostName());
    }

    //testing for existing host when getHostName() method is called
    @Test
    public void testGetHostName_WithHostName() throws Exception {
        email.setHostName(TEST_HOST_NAME);
        assertNotNull(email.getHostName());
    }
    //end of testing for String getHostName() method

    //beginning of testing for Session getMailSession() method
    @Test
    public void testGetMailSession() throws Exception {
        email.setHostName(TEST_HOST_NAME);
        assertNotNull(email.getMailSession());
    }

    //testing for no host when getMailSession() method is called
    @Test
    public void testGetMailSession_NoHostName() throws Exception {
        try {
            assertNotNull(email.getMailSession());
        }
        catch (Exception e) {
            assertEquals("Cannot find valid hostname for mail session", e.getMessage());
        }
    }

    //testing for existing authenticator when getMailSession() method is called
    @Test
    public void testGetMailSession_WithAuthenticator() throws Exception {
        email.setHostName(TEST_HOST_NAME);
        email.setAuthenticator(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return super.getPasswordAuthentication();
            }
        });

        assertNotNull(email.getMailSession());
    }

    //testing for existing SSL check when getMailSession() method is called
    @Test
    public void testGetMailSession_WithSSL() throws Exception {
        email.setHostName(TEST_HOST_NAME);
        email.setSSLOnConnect(true);
        assertNotNull(email.getMailSession());
    }

    //testing for existing TLS and SSL checks when getMailSession() method is called
    @Test
    public void testGetMailSession_WithTLSAndSSLCheck() throws Exception {
        email.setHostName(TEST_HOST_NAME);
        email.setStartTLSEnabled(true);
        email.setSSLCheckServerIdentity(true);
        assertNotNull(email.getMailSession());
    }

    //testing for existing bounce address when getMailSession() method is called
    @Test
    public void testGetMailSession_WithBounceAddress() throws Exception {
        email.setHostName(TEST_HOST_NAME);
        email.setBounceAddress(TEST_EMAIL);
        assertNotNull(email.getMailSession());
    }
    //end of testing for Session getMailSession() method

    //beginning of testing for Date getSentDate() method
    @Test
    public void testGetSentDate() throws Exception {
        assertNotNull(email.getSentDate());
    }

    //testing for existing send date when getSentDate() method is called
    @Test
    public void testGetSentDate_SentDateExists() throws Exception {
        email.setSentDate(new Date());
        assertNotNull(email.getSentDate());
    }
    //end of testing for Date getSentDate() method

    //testing for int getSocketConnectionTimeout() method after 60000ms
    @Test
    public void testGetSocketConnectionTimeout() throws Exception {
        assertEquals(60000, email.getSocketConnectionTimeout());
    }

    //testing for Email setFrom(String email) method
    @Test
    public void testSetFrom() throws Exception {
        assertNotNull(email.setFrom(TEST_EMAIL));
    }

    //more testing to hit 70% *****
    //testing for timeout after 1000ms
    @Test
    public void testSetSocketConnectionTimeout() throws Exception {
        email.setSocketConnectionTimeout(1000);
        assertEquals(1000, email.getSocketConnectionTimeout());
    }

    //testing for timeout after 30000ms
    @Test
    public void testSetSocketTimeout() throws Exception {
        email.setSocketTimeout(30000);
        assertEquals(30000, email.getSocketTimeout());
    }

    //testing for existing set headers
    @Test
    public void testSetHeaders() throws Exception {
        email.setHeaders(TEST_HEADERS);
        assertEquals(2, email.getHeaders().size());
    }

    //testing for existing smtp port being set to port 3000
    @Test
    public void testSetSmtpPort() throws Exception {
        email.setSmtpPort(3000);
        assertEquals("3000", email.getSmtpPort());
    }

    //testing for successful content type update
    @Test
    public void testUpdateContentType()  throws Exception {
        email.updateContentType("; charset=TEST_CHARSET");
        assertEquals("; charset=TEST_CHARSET", email.getContentType());
    }

    //testing for required TLS protocol
    @Test
    public void testSetStartTLSRequired() throws Exception {
        assertNotNull(email.setStartTLSRequired(true));
    }

    //teardown after each test method to keep remaining testing clean
    @After
    public void tearDownEmailTest() throws Exception {
        email = null;
        testObject = null;
    }
}
