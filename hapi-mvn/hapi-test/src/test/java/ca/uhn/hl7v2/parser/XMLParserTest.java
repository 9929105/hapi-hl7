/*
 * XMLParserTest.java
 *
 * Created on March 15, 2002, 12:53 PM
 */

package ca.uhn.hl7v2.parser;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;
import ca.uhn.hl7v2.HL7Exception;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import ca.uhn.hl7v2.model.Composite;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v25.datatype.ED;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
 
/**
 * JUnit test harness for XMLParser
 * @author Bryan Tripp
 */
public class XMLParserTest extends TestCase {

    XMLParser parser;
    
    /** Creates a new instance of XMLParserTest */
    public XMLParserTest(String arg) {
        super(arg);
    }
    
    public void setUp() throws Exception {
        parser = new DummyXMLParser();
    }
    
    public void tearDown() throws Exception {
        
    }

    /**
     * Tests a fix to bug 2164291
     * 
     * XML parsing of segments which appear twice in a message structure definition (e.g. the
     * duplicate PID segments in a swap patient message) should be handled correctly.
     */
    public void testParseDuplicateSegment() throws IOException, EncodingNotSupportedException, HL7Exception {
    	InputStream stream = XMLParserTest.class.getClassLoader().getResourceAsStream("ca/uhn/hl7v2/parser/adt_a17.xml");
    	byte[] bytes = new byte[10000];
    	StringBuffer buffer = new StringBuffer();
    	int count;
    	while ((count = stream.read(bytes)) > 0) {
    		buffer.append(new String(bytes), 0, count);
    	}
    	
    	String xmlMessage = buffer.toString();
    	Message message = new DefaultXMLParser().parse(xmlMessage);
    	
    	String er7Message = new PipeParser().encode(message).replaceAll("\\r", "\r\n");

    	System.out.println("Re-encoded:\r\n" + er7Message);
    	
    	// We should have only two reps of PID
    	int firstIndex = er7Message.indexOf("PID");
    	int secondIndex = er7Message.indexOf("PID", firstIndex + 1);
    	int thirdIndex = er7Message.indexOf("PID", secondIndex + 1);
    	assertTrue(firstIndex > 0);
    	assertTrue(secondIndex > firstIndex);
    	assertTrue("Found third PID " + firstIndex + " " + secondIndex + " " + thirdIndex + ":\r\n" + er7Message, thirdIndex == -1);
    	
    }
    
    public void testGetAckID() throws HL7Exception {
        assertEquals(parser.getAckID("<MSA.2>12</MSA.2>"), "12");
        assertEquals(parser.getAckID("<thing<foo> help >>><msa.2> *** </i forget"), "***");
        String ackID = parser.getAckID("<there is no msa.2>x</msa.2>");
        assertEquals(null, ackID);
        ackID = parser.getAckID("<msa.2>x");
        assertEquals(null, ackID);
        ackID = parser.getAckID("<MSA.2>   12   \r</MSA.2>");
        assertEquals("12", ackID);
        ackID = parser.getAckID("<?xml version=\"1.0\" standalone=\"no\"?><!DOCTYPE RSP_K22 SYSTEM \"http://localhost/DTDs/Q22Response.dtd\"><RSP_K22><MSH><MSH.1>|</MSH.1><MSH.2>^~/&amp;</MSH.2><MSH.3><HD.1>MPI</HD.1><HD.3>ISO</HD.3></MSH.3><MSH.4><HD.1>HealthLink</HD.1><HD.3>ISO</HD.3></MSH.4><MSH.5><HD.1>UHN Vista</HD.1><HD.3>ISO</HD.3></MSH.5><MSH.6><HD.1>UHN</HD.1><HD.3>ISO</HD.3></MSH.6><MSH.7>200204292049</MSH.7><MSH.9><CM_MSG_TYPE.1>RSP</CM_MSG_TYPE.1><CM_MSG_TYPE.2>K22</CM_MSG_TYPE.2><CM_MSG_TYPE.3>RSP_K22</CM_MSG_TYPE.3></MSH.9><MSH.10>200204292049100799</MSH.10><MSH.11><PT.1>P</PT.1></MSH.11><MSH.12><VID.1>2.4</VID.1></MSH.12><MSH.21>Q22</MSH.21></MSH><MSA><MSA.1>AA</MSA.1><MSA.2>876</MSA.2></MSA><QAK><QAK.2>OK</QAK.2><QAK.3><CE.1>Q22</CE.1><CE.2>");
        assertEquals("876", ackID);
    }
    
 
    public void testGetEncoding() throws Exception {
        String test1 = "<MSH>\r<MSH.1>|</MSH.1>\r<MSH.2>^~\\&amp;</MSH.2>\r</MSH>";
        String test2 = "<MSH>\r<MSH.1>|</MSH.1>\r<MSH.2>^~\\&amp;</MSH.2>\r";  //bad: no </MSH>
        assertEquals("XML", parser.getEncoding(test1));
        assertEquals(null, parser.getEncoding(test2));
    }
    
    /*public void testParse() throws Exception {
        String test1 = "<MSH>\r<MSH.1>|</MSH.1>\r<MSH.2>^~\\&amp;</MSH.2>\r</MSH>";
        Message m = parser.parse(test1);
        assertEquals(null, m);
    }*/
    
    public void testGetVersion() throws Exception {
        String message = "<?xml version=\"1.0\"?> <!DOCTYPE QBP_Q22 SYSTEM \"http://142.224.24.144/QueryServices/xmlDtds/Q22Query.dtd\"> <!--Generated by XML Authority--><QBP_Q22>  <MSH>   <MSH.1>|</MSH.1>   <MSH.2>^~/&amp;</MSH.2>   <MSH.3>    <HD.1>UHN Vista</HD.1>    <HD.3>ISO</HD.3>   </MSH.3>   <MSH.4>    <HD.1>UHN</HD.1>    <HD.3>ISO</HD.3>   </MSH.4>   <MSH.5>    <HD.1>MPI</HD.1>    <HD.3>ISO</HD.3>   </MSH.5>   <MSH.6>    <HD.1>HealthLink</HD.1>    <HD.3>ISO</HD.3>   </MSH.6>   <MSH.7>20020429132718.734-0400</MSH.7>   <MSH.9>    <CM_MSG_TYPE.1>QBP</CM_MSG_TYPE.1>    <CM_MSG_TYPE.2>Q22</CM_MSG_TYPE.2>  <CM_MSG_TYPE.3>QBP_Q21</CM_MSG_TYPE.3>   </MSH.9>   <MSH.10>855</MSH.10>   <MSH.11>    <PT.1>P</PT.1>   </MSH.11>   <MSH.12>    <VID.1>2.4</VID.1>      </MSH.12>   <MSH.21>Q22</MSH.21>  </MSH>  <QPD>   <QPD.1>    <CE.1>Q22</CE.1>    <CE.2>Find Candidates</CE.2>    <CE.3>HL7nnnn</CE.3>   </QPD.1>   <QPD.3><QIP><QIP.1>@PID.3.1</QIP.1><QIP.2>9583518684</QIP.2></QIP><QIP><QIP.1>@PID.3.4.1</QIP.1><QIP.2>CANON</QIP.2></QIP><QIP><QIP.1>@PID.5.1.1</QIP.1><QIP.2>ECG-Acharya</QIP.2></QIP><QIP><QIP.1>@PID.5.2</QIP.1><QIP.2>Nf</QIP.2></QIP><QIP><QIP.1>@PID.5.7</QIP.1><QIP.2>L</QIP.2></QIP><QIP><QIP.1>@PID.7</QIP.1><QIP.2>197104010000</QIP.2></QIP><QIP><QIP.1>@PID.8</QIP.1><QIP.2>M</QIP.2></QIP></QPD.3>   <QPD.4>100</QPD.4>   <QPD.8><CX.4><HD.1>TTH</HD.1></CX.4></QPD.8>  <QPD.9>13831</QPD.9><QPD.10>ULTIuser2</QPD.10><QPD.11>234564</QPD.11><QPD.12>R&amp;H Med</QPD.12></QPD>  <RCP>   <RCP.1>I</RCP.1>   <RCP.2><CQ.1>100</CQ.1><CQ.2>RD</CQ.2></RCP.2>   <RCP.3>   <CE.1>R</CE.1>   </RCP.3>  </RCP> </QBP_Q22>";
        String ver = parser.getVersion(message);
        assertEquals("2.4", ver);
    }
    
    public void testRemoveWhitespace() throws Exception {
        assertEquals("hello", parser.removeWhitespace("\t\r\nhello "));
        assertEquals("hello there", parser.removeWhitespace(" hello \t \rthere\r\n"));
    }
    
    /**
     * -
     * @throws HL7Exception -
     */
    public void testGetCriticalResponseData() throws HL7Exception {
        
        String message = "<ORU_R01 xmlns=\"urn:hl7-org:v2xml\">\r\n" + 
                "    <MSH>\r\n" + 
                "        <MSH.1>|</MSH.1>\r\n" + 
                "        <MSH.2>^~\\&amp;</MSH.2>\r\n" + 
                "        <MSH.3>LABMI1</MSH.3>\r\n" + 
                "        <MSH.5>DMCRES</MSH.5>\r\n" + 
                "        <MSH.7>\r\n" + 
                "            <TS.1>19951010134000</TS.1>\r\n" + 
                "        </MSH.7>\r\n" + 
                "        <MSH.9>\r\n" + 
                "            <CM_MSG.1>ORU</CM_MSG.1>\r\n" + 
                "            <CM_MSG.2>R01</CM_MSG.2>\r\n" + 
                "        </MSH.9>\r\n" + 
                "        <MSH.10>LABMI1199510101340007</MSH.10>\r\n" + 
                "        <MSH.11>D</MSH.11>\r\n" + 
                "        <MSH.12>2.2</MSH.12>\r\n" + 
                "        <MSH.15>AL</MSH.15>\r\n" + 
                "    </MSH>";
        
        Segment data = parser.getCriticalResponseData(message);
        
        Type actual = data.getField(2, 0);
        String expected = "^~\\&";
        assertEquals(expected, actual.toString()); // Encoding
        
    }
    
        
    public class DummyXMLParser extends XMLParser {
        public DummyXMLParser() throws HL7Exception { 
            super();
        }
        
        public Message parseDocument(Document XMLMessage, String version) throws HL7Exception {
            return null;
        }
        
        public Document encodeDocument(Message source) throws HL7Exception {
            return null;
        }        
        
    }


    public void testEdIssue() throws EncodingNotSupportedException, HL7Exception {
        
        String messageText = "<ORU_R01 xmlns=\"urn:hl7-org:v2xml\">\r\n" + 
                "   <MSH>\r\n" + 
                "       <MSH.1>|</MSH.1>\r\n" + 
                "       <MSH.2>^~\\&amp;</MSH.2>\r\n" + 
                "       <MSH.3>\r\n" + 
                "           <HD.1>APPNAME</HD.1>\r\n" + 
                "       </MSH.3>\r\n" + 
                "       <MSH.4>\r\n" + 
                "           <HD.1>VENDOR NAME</HD.1>\r\n" + 
                "       </MSH.4>\r\n" + 
                "       <MSH.5>\r\n" + 
                "           <HD.1>CLINIC APPLICATION</HD.1>\r\n" + 
                "       </MSH.5>\r\n" + 
                "       <MSH.6>\r\n" + 
                "           <HD.1>CLINIC ID</HD.1>\r\n" + 
                "       </MSH.6>\r\n" + 
                "       <MSH.7>\r\n" + 
                "           <TS.1>20100723170708</TS.1>\r\n" + 
                "       </MSH.7>\r\n" + 
                "       <MSH.9>\r\n" + 
                "           <MSG.1>ORU</MSG.1>\r\n" + 
                "           <MSG.2>R01</MSG.2>\r\n" + 
                "       </MSH.9>\r\n" + 
                "       <MSH.10>12345</MSH.10>\r\n" + 
                "       <MSH.11>\r\n" + 
                "           <PT.1>P</PT.1>\r\n" + 
                "       </MSH.11>\r\n" + 
                "       <MSH.12>\r\n" + 
                "           <VID.1>2.5</VID.1>\r\n" + 
                "       </MSH.12>\r\n" + 
                "   </MSH>\r\n" + 
                "   <ORU_R01.PATIENT_RESULT>\r\n" + 
                "       <ORU_R01.PATIENT>\r\n" + 
                "           <PID>\r\n" + 
                "               <PID.2>\r\n" + 
                "                   <CX.1>MODEL:xxx/SERIAL:xxx</CX.1>\r\n" + 
                "                   <CX.4>\r\n" + 
                "                       <HD.1>STJ</HD.1>\r\n" + 
                "                   </CX.4>\r\n" + 
                "                   <CX.5>U</CX.5>\r\n" + 
                "               </PID.2>\r\n" + 
                "               <PID.5>\r\n" + 
                "                   <XPN.1>\r\n" + 
                "                       <FN.1>Doe</FN.1>\r\n" + 
                "                   </XPN.1>\r\n" + 
                "                   <XPN.2>John</XPN.2>\r\n" + 
                "                   <XPN.3>Adams</XPN.3>\r\n" + 
                "               </PID.5>\r\n" + 
                "               <PID.7>\r\n" + 
                "                   <TS.1>197903110920</TS.1>\r\n" + 
                "               </PID.7>\r\n" + 
                "               <PID.8>M</PID.8>\r\n" + 
                "               <PID.11>\r\n" + 
                "                   <XAD.1>\r\n" + 
                "                       <SAD.2>Street</SAD.2>\r\n" + 
                "                   </XAD.1>\r\n" + 
                "                   <XAD.3>City</XAD.3>\r\n" + 
                "                   <XAD.5>06531</XAD.5>\r\n" + 
                "                   <XAD.6>Country</XAD.6>\r\n" + 
                "               </PID.11>\r\n" + 
                "           </PID>\r\n" + 
                "           <ORU_R01.VISIT>\r\n" + 
                "               <PV1>\r\n" + 
                "                   <PV1.1>1</PV1.1>\r\n" + 
                "                   <PV1.2>R</PV1.2>\r\n" + 
                "                   <PV1.7>\r\n" + 
                "                       <XCN.1>DoctorID</XCN.1>\r\n" + 
                "                   </PV1.7>\r\n" + 
                "                   <PV1.19>\r\n" + 
                "                       <CX.1>123456</CX.1>\r\n" + 
                "                   </PV1.19>\r\n" + 
                "               </PV1>\r\n" + 
                "           </ORU_R01.VISIT>\r\n" + 
                "       </ORU_R01.PATIENT>\r\n" + 
                "       <ORU_R01.ORDER_OBSERVATION>\r\n" + 
                "           <OBR>\r\n" + 
                "               <OBR.1>1</OBR.1>\r\n" + 
                "               <OBR.3>\r\n" + 
                "                   <EI.1>123456</EI.1>\r\n" + 
                "               </OBR.3>\r\n" + 
                "               <OBR.4>\r\n" + 
                "                   <CE.1>Remote Follow-up</CE.1>\r\n" + 
                "               </OBR.4>\r\n" + 
                "               <OBR.7>\r\n" + 
                "                   <TS.1>20040328134623</TS.1>\r\n" + 
                "               </OBR.7>\r\n" + 
                "               <OBR.8>\r\n" + 
                "                   <TS.1>20040328134623</TS.1>\r\n" + 
                "               </OBR.8>\r\n" + 
                "               <OBR.22>\r\n" + 
                "                   <TS.1>20040328134623</TS.1>\r\n" + 
                "               </OBR.22>\r\n" + 
                "               <OBR.25>F</OBR.25>\r\n" + 
                "           </OBR>\r\n" + 
                "           <NTE>\r\n" + 
                "               <NTE.1>1</NTE.1>\r\n" + 
                "               <NTE.2>L</NTE.2>\r\n" + 
                "               <NTE.3>Comment</NTE.3>\r\n" + 
                "           </NTE>\r\n" + 
                "           <ORU_R01.OBSERVATION>\r\n" + 
                "               <OBX>\r\n" + 
                "                   <OBX.1>1</OBX.1>\r\n" + 
                "                   <OBX.2>ST</OBX.2>\r\n" + 
                "                   <OBX.3>\r\n" + 
                "                       <CE.1>257</CE.1>\r\n" + 
                "                       <CE.2>MDC-IDC_SYSTEM_STATUS</CE.2>\r\n" + 
                "                       <CE.3>MDC_IDC</CE.3>\r\n" + 
                "                   </OBX.3>\r\n" + 
                "                   <OBX.4>1</OBX.4>\r\n" + 
                "                   <OBX.6>\r\n" + 
                "                       <CE.1>m</CE.1>\r\n" + 
                "                   </OBX.6>\r\n" + 
                "                   <OBX.8>L</OBX.8>\r\n" + 
                "                   <OBX.11>F</OBX.11>\r\n" + 
                "                   <OBX.14>\r\n" + 
                "                       <TS.1>20070422170125</TS.1>\r\n" + 
                "                   </OBX.14>\r\n" + 
                "                   <OBX.17>\r\n" + 
                "                       <CE.1>LastFU</CE.1>\r\n" + 
                "                       <CE.2>Since Last Follow-up Aggregate</CE.2>\r\n" + 
                "                   </OBX.17>\r\n" + 
                "               </OBX>\r\n" + 
                "           </ORU_R01.OBSERVATION>\r\n" + 
                "           <ORU_R01.OBSERVATION>\r\n" + 
                "               <OBX>\r\n" + 
                "                   <OBX.1>2</OBX.1>\r\n" + 
                "                   <OBX.2>ED</OBX.2>\r\n" + 
                "                   <OBX.3>\r\n" + 
                "                       <CE.1>18750-0</CE.1>\r\n" + 
                "                       <CE.2>Cardiac Electrophysiology Report</CE.2>\r\n" + 
                "                       <CE.3>LN</CE.3>\r\n" + 
                "                   </OBX.3>\r\n" + 
                "                   <OBX.5>\r\n" + 
                "                       <ED.2>Application</ED.2>\r\n" + 
                "                       <ED.3>PDF</ED.3>\r\n" + 
                "                       <ED.4>Base64</ED.4>\r\n" + 
                "\r\n" + 
                "\r\n" + 
                "<ED.5>JVBERi0xLjMKJeLjz9MKCjEgMCBvYmoKPDwgL1R5cGUgL0NhdGFsb2cKL091dGxpbmVzID\r\n" + 
                "IgMCBS\r\n" + 
                "\r\n" + 
                "Ci9QYWdlcyAzIDAgUgovT3BlbkFjdGlvbiA4IDAgUiA+PgplbmRvYmoKMiAwIG9iago8PCAvVHlw\r\n" + 
                "\r\n" + 
                "ZSAvT3V0bGluZXMgL0NvdW50IDAgPj4KZW5kb2JqCjMgMCBvYmoKPDwgL1R5cGUgL1BhZ2VzCi9L\r\n" + 
                "\r\n" + 
                "aWRzIFs2IDAgUgpdCi9Db3VudCAxCi9SZXNvdXJjZXMgPDwKL1Byb2NTZXQgNCAwIFIKL0ZvbnQg\r\n" + 
                "\r\n" + 
                "PDwgCi9GMSAxMSAwIFIKL0YyIDEyIDAgUgovRjMgMTcgMCBSID4+Ci9YT2JqZWN0IDw8IAovSTEg\r\n" + 
                "\r\n" + 
                "OSAwIFIgPj4KPj4KL01lZGlhQm94IFswLjAwMCAwLjAwMCA1OTUuMjgwIDg0MS44OTBdCiA+Pgpl\r\n" + 
                "\r\n" + 
                "bmRvYmoKNCAwIG9iagpbL1BERiAvVGV4dCAvSW1hZ2VDIF0KZW5kb2JqCjUgMCBvYmoKPDwKL0Ny\r\n" + 
                "\r\n" + 
                "ZWF0b3IgKERPTVBERiBDb252ZXJ0ZXIpCi9DcmVhdGlvbkRhdGUgKDIwMTAtMDItMTUpCj4+CmVu\r\n" + 
                "\r\n" + 
                "ZG9iago2IDAgb2JqCjw8IC9UeXBlIC9QYWdlCi9QYXJlbnQgMyAwIFIKL0Fubm90cyBbIDEzIDAg\r\n" + 
                "\r\n" + 
                "UiAxNSAwIFIgXQovQ29udGVudHMgNyAwIFIKPj4KZW5kb2JqCjcgMCBvYmoKPDwgL0ZpbHRlciAv\r\n" + 
                "\r\n" + 
                "RmxhdGVEZWNvZGUKL0xlbmd0aCA5MTcgPj4Kc3RyZWFtCnicnVbZbttGFH3XV9y3xoA8nn1IFQgg\r\n" + 
                "\r\n" + 
                "23HSBC1cS4Uf6jyMpZE5KReXS4wG+Zi+9i97SYuLRSl2AgMUfMR77pm7nNGEEUopDJ/53YTBA3B4\r\n" + 
                "\r\n" + 
                "DxQ+wZ/wET/XE8abb2UgiA4ZKMMIDygIqQkLQ8gdbCZ/T0LdvFX/iYDImvQxzoSMyJDCKpmc/MLg\r\n" + 
                "\r\n" + 
                "PJv8PqGP7w6emPt02YUYQxgPYLmGkwvWoLDcALy6rG5jX0RuDVkKR7D8BG+WdVwgiaFBE8cZfYzj\r\n" + 
                "\r\n" + 
                "g7hfbVHYVVQVriwLmMdfIucTl/9UwLkvnC0cXDn8zFcRnLm0dPmAWwQBCbg8LApuXrWvD0/EBucS\r\n" + 
                "\r\n" + 
                "WB2m9GGOqCzvZycniV3nK5LcRSSy+Webr4lbV4fJr94iqLBlFFvWtkuECstf51LESAYJKCWJYLRD\r\n" + 
                "\r\n" + 
                "Ylgc7IBSgij9jRbcHD132LaJIiShoi1D2BK8yxLXU+yqZzhcqg4OSIiSExCaGCU64FvaRT2C4mBi\r\n" + 
                "\r\n" + 
                "eF13fpPlVQI2XcPiev7bDJaRg8scu5+WDXpRlRWOdLaBxcoj6Dd+BdfuFs6yJKlSX3pX9PI1ryX0\r\n" + 
                "\r\n" + 
                "z0MrJLYl4UToEJQOmxXaiq8XaO+ZxiB2vCuvaLiwvbiYNVuLxIjwLcK3yI6AGDBlR8RGROy7iHYF\r\n" + 
                "\r\n" + 
                "vIxpjAw1tUw7IvcI2Keon0IdojUg3AyDAC7bafiuURjYQcurKbqaGfM+PzUDEgyTXLUkOz63sqVH\r\n" + 
                "\r\n" + 
                "mzu3pZv9OwzGEwvFm2gh5XhFOaVmnEyhL+hW8fD1s9jmf8GyOfIHn2ZF5EuLA3zTEN0ckadrUzzY\r\n" + 
                "\r\n" + 
                "dAYl1up+UKtNV6uiL8ADFmDVF4DsqaMyuJ+KjVWd5t5t4NRnPsXcybOOodCwtJY4MvV9I2QH1I6B\r\n" + 
                "\r\n" + 
                "+UJc0EAezvczBDNgWhwzvN+GOhUJ0LmVwn7TPcV+erW8danLbQzvsuIeqxhP4dqmdzBPbqvYlln+\r\n" + 
                "\r\n" + 
                "D5xZLNPjJQOLypcODGfnU8wDl9iHxKawKHPnyimcZkWZpdNh1TjHSxjNXeGCGLXHnp/KoZwxOYU/\r\n" + 
                "\r\n" + 
                "FvMn88OIxvlRDC2e8THH0sXuPspSNwPNzLHh+liEgYGvcGFXhU98PPhGMsogy7v/jWHBMBlOnanr\r\n" + 
                "\r\n" + 
                "TrF+Wo2T/QfvbVpZrAybAqdo8fM4hit/F6H++lbOP+N1/6OOK+u21z9aXui47W8dnKE6rHeyFul9\r\n" + 
                "\r\n" + 
                "q6XuHKgHeiOTQbhLtEVeSrQr4GVMY2SgqWPaEblHwB5F/wMtHGbMCmVuZHN0cmVhbQplbmRvYmoK\r\n" + 
                "\r\n" + 
                "CjggMCBvYmoKWzYgMCBSIC9GaXRdCmVuZG9iagoKOSAwIG9iago8PAovVHlwZSAvWE9iamVjdAov\r\n" + 
                "\r\n" + 
                "U3VidHlwZSAvSW1hZ2UKL1dpZHRoIDIwMAovSGVpZ2h0IDgwCi9GaWx0ZXIgL0ZsYXRlRGVjb2Rl\r\n" + 
                "\r\n" + 
                "Ci9EZWNvZGVQYXJtcyA8PCAvUHJlZGljdG9yIDE1IC9Db2xvcnMgMSAvQ29sdW1ucyAyMDAgL0Jp\r\n" + 
                "\r\n" + 
                "dHNQZXJDb21wb25lbnQgOD4+Ci9Db2xvclNwYWNlICBbIC9JbmRleGVkIC9EZXZpY2VSR0IgMzIg\r\n" + 
                "\r\n" + 
                "MTAgMCBSIF0KL0JpdHNQZXJDb21wb25lbnQgOAovTGVuZ3RoIDIwMzEgPj4Kc3RyZWFtCmje7Zlp\r\n" + 
                "\r\n" + 
                "l6soEIZNOp2OccGoGBNc+P+/cqhiK1zSduaeM33u6Ic0IhT1SBXg25H8S65oB9lBdpAdZAf5/SB1\r\n" + 
                "\r\n" + 
                "fXflZ133r1rW7xh9v48DqaPo0xSvUXQxxbsvSnk5RFF0OD/xwUmVo49M+j7e8/snPIw+YbBLFLkB\r\n" + 
                "\r\n" + 
                "anIrT9FpYrSO3FVLX1YeBDehI0sg0U2/6oP3/qw62BafxtShRkB9WfijL8rMjZp9C0KNbgWhfZZB\r\n" + 
                "\r\n" + 
                "Dr0ewYE8jTemwbmXz8vhhG3Ayu1kpwF9N2/opgxl2PJ7kMCournW+uohNmv1GuHPc3JD+yyDRGfr\r\n" + 
                "\r\n" + 
                "kwG5RMdzdLRl3bYHf22Lu/PqfMTeODkHXX3Pvg2twGhNw5MOGd4EfZZzBAz1B/ir3VTl7G6tq/4+\r\n" + 
                "\r\n" + 
                "wUggmc7Pq5nQzM1hOP4ayJ1a2Qhyf7FqgZVTdOw/lT8WJAPfTsZnSIvzrXf5crx6c2fllKK+6vJh\r\n" + 
                "\r\n" + 
                "4oyOlusSSGDUh1b/CiToswyi0vwEKW9BjtEXvuEnyeFPfN39B5SPX0+bSjcgOJLViIxPUniW7NRo\r\n" + 
                "\r\n" + 
                "TRuugwR9lkFgEYX3b0BqjeCC//l1BAvazwzXjsPNpJLGyX4MQo1uBQkdWQSB1ah3ICrSLur6MMGP\r\n" + 
                "\r\n" + 
                "Jq7KxNXc3MzirILqBA0PaHoeWi/3EWJ0Y47MHVkAueNeokGe4S5krv5I3oSO+yx456+TPXOZerAT\r\n" + 
                "\r\n" + 
                "7Yz+BGTiyAxE1tKBqFd7wuuIkdN/6Xn5gP7ZzW4ZNcTeUTfUvh3s8vu8zEGekfH/hryB0Y0gQZ9V\r\n" + 
                "\r\n" + 
                "EOlA3NoFjzKMuutT9nh+gbVaodwx6m40pp/BhniegcB2+wVmDhiw1Gi4Ia6DBH02gFx8agB777L2\r\n" + 
                "\r\n" + 
                "qLbdD3tzpcFu2F2owcxMQeAA5B4GRsMjyipI2Od7kP7oYzjTUXFGH85629Pr7w2X9czvJwivD436\r\n" + 
                "\r\n" + 
                "VDcFkf3ZnyhDo9tAJo68d4y/k8N5D+ee987m4Rn/vv3E/6LP/oW4g+wgO8gO8mtABs6HV/f2ann7\r\n" + 
                "\r\n" + 
                "J1zgXJifPw0i4li8urcXi9nWYco4HteexTE3P/8VSMWqrcOwuJAvQRhr/zuQH1wsbl+D/MEcGThj\r\n" + 
                "\r\n" + 
                "rBqs4yox9NXifVeyQtOMjWomXI7AT4PvU7gmsi1YiZ4LzmVXVjLHyIIWDQkxaNZpEJ0jQ8WYyUdn\r\n" + 
                "\r\n" + 
                "QgpVyXQ3P/bMlgPpYn21BkSYe5UHqtgk5pl8YClubI6onxwqqtZ2lx1WYDDxOO4S1QxvKqxO7OyO\r\n" + 
                "\r\n" + 
                "TPfwOfKIrW1iotGVSac4E1s7FljKh4UZSXPOVU4mI52RHFwDJsZV1wRtsYcoMNgMSJxUFYyQYndl\r\n" + 
                "\r\n" + 
                "KY/TVnD9olV1nFYSAAdVI1qWkgUg5VUae5A0LsSjjIfAxBAXnDN4oapH/hBV0sI7SZSthKw2HgSn\r\n" + 
                "\r\n" + 
                "qTWOC5cdHH8LPbBQP+lIJgNBOuwH9dikxRrFoN4JN5NnjI12HIlcTJXH3IPE8UM3oCZ0B2V5UGNV\r\n" + 
                "\r\n" + 
                "+vmgrdLkJSAPzpsODLrnKipK3x7/JNovFQSUxmYsNin0CjVCmZNMVqOnjY+FRjsrOzojSYV11IQc\r\n" + 
                "\r\n" + 
                "WpWorX6LcfnQfRO0kXvrPkcSkxMeRIVRIScgsbvEGghzTTiAeNcxi1hn90F4F+E+gj6kDxmYaP2A\r\n" + 
                "\r\n" + 
                "MHsqprCvy+ApSBInrdBhaRxX3fJxDsLMajasg6SmifDe6jlplCfJ4EBG89rdhji2kMQPakJNmEqM\r\n" + 
                "\r\n" + 
                "R6l9eJSKtcKJtmvqBETo+JQexHEEIDkGG9nZ5yBVnPrjRwCiZ5m7ERvThO7sIxgkJgyvTwe19kFk\r\n" + 
                "\r\n" + 
                "Dyv7iNArZ+NBSlzxpiCtaQeMKyAqFzCz0i4EETlYSV1Yq/WqGcfGr1pj2mJaM2rCBCeuk4zrhQD6\r\n" + 
                "\r\n" + 
                "ggND3s5XLZVoJaxyFqQJ9hEHgosmSxFnBQSGSmBzKUOQwvQc/LtD+w6kMf1aagKWiIrDIg1RFjOG\r\n" + 
                "\r\n" + 
                "diGboJiP82QHiFQ4EL4CIrnNyFUQ+UhNUgYgo+9pSKBZQXKkdf2ICSwxXLUELgLlaN3VxdlZaxDd\r\n" + 
                "\r\n" + 
                "tmNN933DQYgtPTsxrPbzRdJqFGKcF/cvxB1kB9lBdpC/DORfizu/BeRtPeH9N/DLQN5XVP4+ECvc\r\n" + 
                "\r\n" + 
                "hEoL0V9otZeOUE8dK2YbF63xR9VNNbe2sLIO0X2gv9WaBo7fbUM4GvFsC4gVbgKlxesvQTWRjvCL\r\n" + 
                "\r\n" + 
                "LMczrhGKQFFQX3Go55T0gKgVnlSrCk4YolqTsN+0wWjOs80gKNwESovXX0IBxktHMLj6lGFIp79o\r\n" + 
                "\r\n" + 
                "OgABpSc28oKeW3UaL6BTg9JFKRS+6e+0Jjibpwy+6ulozrPNIPjpGSgtTn+ZCDBeOkJHSq0Q4Rdl\r\n" + 
                "\r\n" + 
                "22JopfpbjtOPXhTvKvyIwzlOplqTy5FgNCopbQPhM6XF6S8TAcZLRzAYM2Pzac7SqtQHR2dmqoKq\r\n" + 
                "\r\n" + 
                "8KttyQm+aQWgIIOUE6XF6S9hNZGO7Md+qHQvgJCy8JrSCkgwGp8rDd+AuJkhSovRX8JqIh05ACvD\r\n" + 
                "\r\n" + 
                "rINUBKQ0xtZB/GgzJWYbyFxpAf0lqKbSkZ8Ju7CMyyAFpjY+HmPyT4ZFkMCJN0ECpcXrL7SaSkce\r\n" + 
                "\r\n" + 
                "RBkoBrXw5OMiiGqXC7V/wIqglaaxLBdACjmOoRPvghClhegvgQBDpCOSG7kJ68ciiPmnAkYYpF7O\r\n" + 
                "\r\n" + 
                "EsQJQXKzj9DR3gWhSgvRX2g1kY5okqPck4vl0DK6TooL6Vg6GTgEwWWkC0f7IQi9VkQXUl6RjoQY\r\n" + 
                "\r\n" + 
                "tupIa03dGBO5Z/+w2kF2kB1kB9lBdpD/E8g/QZSt+wplbmRzdHJlYW0KZW5kb2JqCgoxMCAwIG9i\r\n" + 
                "\r\n" + 
                "ago8PCAvRmlsdGVyIC9GbGF0ZURlY29kZQovTGVuZ3RoIDExMCA+PgpzdHJlYW0KeJwBYwCc/2MG\r\n" + 
                "\r\n" + 
                "EW0WIHclL4A1PopETZRUXJ5ka6hzerGCiJaWlrqSlp2dnaOjo8ShpaqqqrCwsLe3t86xtL6+vsTE\r\n" + 
                "\r\n" + 
                "xNjBw8rKytHR0eLQ0tfX197e3uvg4eXl5evr6/Xv8PLy8vj4+P///6g8Qs0KZW5kc3RyZWFtCmVu\r\n" + 
                "\r\n" + 
                "ZG9iagoKMTEgMCBvYmoKPDwgL1R5cGUgL0ZvbnQKL1N1YnR5cGUgL1R5cGUxCi9OYW1lIC9GMQov\r\n" + 
                "\r\n" + 
                "QmFzZUZvbnQgL0hlbHZldGljYQovRW5jb2RpbmcgL1dpbkFuc2lFbmNvZGluZwo+PgplbmRvYmoK\r\n" + 
                "\r\n" + 
                "MTIgMCBvYmoKPDwgL1R5cGUgL0ZvbnQKL1N1YnR5cGUgL1R5cGUxCi9OYW1lIC9GMgovQmFzZUZv\r\n" + 
                "\r\n" + 
                "bnQgL0hlbHZldGljYS1Cb2xkT2JsaXF1ZQovRW5jb2RpbmcgL1dpbkFuc2lFbmNvZGluZwo+Pgpl\r\n" + 
                "\r\n" + 
                "bmRvYmoKMTMgMCBvYmoKPDwgL1R5cGUgL0Fubm90Ci9TdWJ0eXBlIC9MaW5rCi9BIDE0IDAgUgov\r\n" + 
                "\r\n" + 
                "Qm9yZGVyIFswIDAgMF0KL0ggL0kKL1JlY3QgWyAzOTYuMTU2MCA3NzcuMTI4NCA1NTMuNTYwMCA3\r\n" + 
                "\r\n" + 
                "OTEuMDAwNCBdCj4+CmVuZG9iagoxNCAwIG9iago8PCAvVHlwZSAvQWN0aW9uCi9TIC9VUkkKL1VS\r\n" + 
                "\r\n" + 
                "SSAoaHR0cDovL21hZHJjLm1naC5oYXJ2YXJkLmVkdSkKPj4KZW5kb2JqCjE1IDAgb2JqCjw8IC9U\r\n" + 
                "\r\n" + 
                "eXBlIC9Bbm5vdAovU3VidHlwZSAvTGluawovQSAxNiAwIFIKL0JvcmRlciBbMCAwIDBdCi9IIC9J\r\n" + 
                "\r\n" + 
                "Ci9SZWN0IFsgMTIuMDAwMCA3MzkuOTUwMCAzNi4wMDMwIDc1MC4zNTQwIF0KPj4KZW5kb2JqCjE2\r\n" + 
                "\r\n" + 
                "IDAgb2JqCjw8IC9UeXBlIC9BY3Rpb24KL1MgL1VSSQovVVJJIChodHRwOi8vbWFkcmMubWdoLmhh\r\n" + 
                "\r\n" + 
                "cnZhcmQuZWR1LykKPj4KZW5kb2JqCjE3IDAgb2JqCjw8IC9UeXBlIC9Gb250Ci9TdWJ0eXBlIC9U\r\n" + 
                "\r\n" + 
                "eXBlMQovTmFtZSAvRjMKL0Jhc2VGb250IC9IZWx2ZXRpY2EtQm9sZAovRW5jb2RpbmcgL1dpbkFu\r\n" + 
                "\r\n" + 
                "c2lFbmNvZGluZwo+PgplbmRvYmoKeHJlZgowIDE4CjAwMDAwMDAwMDAgNjU1MzUgZiAKMDAwMDAw\r\n" + 
                "\r\n" + 
                "MDAxNSAwMDAwMCBuIAowMDAwMDAwMDk4IDAwMDAwIG4gCjAwMDAwMDAxNDQgMDAwMDAgbiAKMDAw\r\n" + 
                "\r\n" + 
                "MDAwMDM0NyAwMDAwMCBuIAowMDAwMDAwMzg0IDAwMDAwIG4gCjAwMDAwMDA0NjAgMDAwMDAgbiAK\r\n" + 
                "\r\n" + 
                "MDAwMDAwMDU0OSAwMDAwMCBuIAowMDAwMDAxNTM5IDAwMDAwIG4gCjAwMDAwMDE1NjggMDAwMDAg\r\n" + 
                "\r\n" + 
                "biAKMDAwMDAwMzg2OCAwMDAwMCBuIAowMDAwMDA0MDUyIDAwMDAwIG4gCjAwMDAwMDQxNjAgMDAw\r\n" + 
                "\r\n" + 
                "MDAgbiAKMDAwMDAwNDI4MCAwMDAwMCBuIAowMDAwMDA0NDA4IDAwMDAwIG4gCjAwMDAwMDQ0ODgg\r\n" + 
                "\r\n" + 
                "MDAwMDAgbiAKMDAwMDAwNDYxNCAwMDAwMCBuIAowMDAwMDA0Njk1IDAwMDAwIG4gCnRyYWlsZXIK\r\n" + 
                "\r\n" + 
                "PDwKL1NpemUgMTgKL1Jvb3QgMSAwIFIKL0luZm8gNSAwIFIKPj4Kc3RhcnR4cmVmCjQ4MDgKJSVF\r\n" + 
                "T0YK</ED.5>\r\n" + 
                "                   </OBX.5>\r\n" + 
                "                   <OBX.11>F</OBX.11>\r\n" + 
                "                   <OBX.14>\r\n" + 
                "                       <TS.1>20070422170125</TS.1>\r\n" + 
                "                   </OBX.14>\r\n" + 
                "               </OBX>\r\n" + 
                "           </ORU_R01.OBSERVATION>\r\n" + 
                "           <ORU_R01.OBSERVATION>\r\n" + 
                "               <OBX>\r\n" + 
                "                   <OBX.1>3</OBX.1>\r\n" + 
                "                   <OBX.2>CE</OBX.2>\r\n" + 
                "                   <OBX.3>\r\n" + 
                "                       <CE.1>257</CE.1>\r\n" + 
                "                       <CE.2>MDC-IDC_SYSTEM_STATUS</CE.2>\r\n" + 
                "                       <CE.3>MDC_IDC</CE.3>\r\n" + 
                "                   </OBX.3>\r\n" + 
                "                   <OBX.4>1</OBX.4>\r\n" + 
                "                   <OBX.5>\r\n" + 
                "                       <CE.1>T57000</CE.1>\r\n" + 
                "                       <CE.2>GALLBLADDER</CE.2>\r\n" + 
                "                       <CE.3>SNM</CE.3>\r\n" + 
                "                   </OBX.5>\r\n" + 
                "                   <OBX.6>\r\n" + 
                "                       <CE.1>m</CE.1>\r\n" + 
                "                   </OBX.6>\r\n" + 
                "                   <OBX.8>L</OBX.8>\r\n" + 
                "                   <OBX.11>F</OBX.11>\r\n" + 
                "                   <OBX.14>\r\n" + 
                "                       <TS.1>20070422170125</TS.1>\r\n" + 
                "                   </OBX.14>\r\n" + 
                "                   <OBX.17>\r\n" + 
                "                       <CE.1>LastFU</CE.1>\r\n" + 
                "                       <CE.2>Since Last Follow-up Aggregate</CE.2>\r\n" + 
                "                   </OBX.17>\r\n" + 
                "               </OBX>\r\n" + 
                "           </ORU_R01.OBSERVATION>\r\n" + 
                "       </ORU_R01.ORDER_OBSERVATION>\r\n" + 
                "   </ORU_R01.PATIENT_RESULT>\r\n" + 
                "</ORU_R01>";
        
        ORU_R01 message = (ORU_R01) new DefaultXMLParser().parse(messageText);
        ED obx5 = (ED) message.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION(1).getOBX().getObx5_ObservationValue(0).getData();
        
        assertTrue(obx5.getData().getValue().startsWith("JVBERi0xLjMKJeLjz9MKCjEgMCBvYmoKPDwgL1R5cG"));
        
    }

    
}
