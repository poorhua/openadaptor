/*
 Copyright (C) 2001 - 2007 The Software Conservancy as Trustee. All rights reserved.

 Permission is hereby granted, free of charge, to any person obtaining a copy of
 this software and associated documentation files (the "Software"), to deal in the
 Software without restriction, including without limitation the rights to use, copy,
 modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 and to permit persons to whom the Software is furnished to do so, subject to the
 following conditions:

 The above copyright notice and this permission notice shall be included in all 
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 Nothing in this notice shall be deemed to grant any rights to trademarks, copyrights,
 patents, trade secrets or any other intellectual property of the licensor or any
 contributor except as expressly stated herein. No patent license is granted separate
 from the Software, for code that you delete from the Software, or for combinations
 of the Software with other software or hardware.
 */

package org.openadaptor.core.exception;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openadaptor.auxil.connector.jdbc.JDBCConnection;
import org.openadaptor.auxil.connector.jdbc.JDBCConnectionTestCase;
import org.openadaptor.auxil.connector.jdbc.reader.JDBCPollingReadConnector;
import org.openadaptor.auxil.connector.jdbc.reader.JDBCReadConnector;
import org.openadaptor.auxil.connector.jdbc.reader.orderedmap.ResultSetToOrderedMapConverter;
import org.openadaptor.core.Component;
import org.openadaptor.core.IDataProcessor;
import org.openadaptor.core.IReadConnector;
import org.openadaptor.core.IWriteConnector;
import org.openadaptor.core.adaptor.Adaptor;
import org.openadaptor.core.router.Router;
import org.openadaptor.spring.SpringAdaptor;
import org.openadaptor.util.LocalHSQLJdbcConnection;
import org.openadaptor.util.SystemTestUtil;
import org.openadaptor.util.TestComponent;
import org.openadaptor.util.TestComponent.ExceptionThrowingWriteConnector;
import org.openadaptor.util.TestComponent.TestReadConnector;
import org.openadaptor.util.TestComponent.TestWriteConnector;

/**
 * System tests for the hospital writer and reader.
 * Uses in-memory HSQL database for the hospital.
 * 
 * @author Kris Lachor
 */
public class HospitalTestCase extends JDBCConnectionTestCase {
  
  private static String SCHEMA = "CREATE MEMORY TABLE ERROR_LOG(ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL PRIMARY KEY,TIMESTAMP CHAR(32),EXCEPTION_CLASS_NAME CHAR(128),ORIGINATING_COMPONENT CHAR(128),DATA CHAR(256),FIXED BOOLEAN,REPROCESSED BOOLEAN)";
  
  private static String SELECT_ALL_ERRORS_SQL = "SELECT * FROM ERROR_LOG";
  
  private static String HOSPITAL_READER_SELECT_STMT = "SELECT  id, timestamp, data, exception_class_name, originating_component FROM error_log WHERE fixed = 'true' AND REPROCESSED = 'false' ";
  
  private static final String RESOURCE_LOCATION = "test/system/src/";
  
  private static final String CONFIG_FILE_WRITER_1 = "hospital_db_writer.xml";
  
  private static final String CONFIG_FILE_WRITER_2 = "hospital_db_writer2.xml";
  
  private static final String CONFIG_FILE_WRITER_3 = "hospital_db_writer3.xml";
  
  private static final String CONFIG_FILE_WRITER_4 = "hospital_db_writer4.xml";
  
  private static final String CONFIG_FILE_READER = "hospital_db_reader.xml";
 
  /**
   * @return hospital table definition.
   */
  public String getSchemaDefinition() {
    return SCHEMA;
  }

  /**
   * Ensures the hospital table is correctly set up and empty.
   */
  public void testHospitalIsEmpty1() throws Exception{
    PreparedStatement preparedStmt = jdbcConnection.getConnection().prepareStatement(SELECT_ALL_ERRORS_SQL);
    ResultSet rs = preparedStmt.executeQuery();
    assertFalse("Hospital not empty", rs.next());
    ResultSetMetaData rsmd = rs.getMetaData();
    int numberOfColumns = rsmd.getColumnCount();
    assertTrue("Hospital returned wrong number of columns", numberOfColumns > 5);
    preparedStmt.close();
  }
  
  /**
   * Runs adaptor (with hospital set up as exceptionProcessor) with nodes that don't throw any exceptions.
   * Ensures the hospital is empty.
   */
  public void testHospitalIsEmpty2() throws Exception{
    SystemTestUtil.runAdaptor(this, RESOURCE_LOCATION, CONFIG_FILE_WRITER_1);
    PreparedStatement preparedStmt = jdbcConnection.getConnection().prepareStatement(SELECT_ALL_ERRORS_SQL);
    ResultSet rs = preparedStmt.executeQuery();
    assertFalse("Hospital not empty", rs.next());
    preparedStmt.close();
  }

  /**
   * Runs adaptor with a writer node that throws an exception. 
   * Verifies hospital has one entry.
   */
  public void testHospitalWriterGetsOneExceptionFromWriteConnector() throws Exception{
    SystemTestUtil.runAdaptor(this, RESOURCE_LOCATION, CONFIG_FILE_WRITER_2);
    PreparedStatement preparedStmt = jdbcConnection.getConnection().prepareStatement(SELECT_ALL_ERRORS_SQL);
    ResultSet rs = preparedStmt.executeQuery();
    assertTrue("Hospital is empty", rs.next());
    assertFalse("Hospital has more than one element", rs.next());
    preparedStmt.close();
  }
  
  
// Readers are unable to do any exception handling at the moment. 
//  
//  /**
//   * Runs adaptor with a reader node that throws an exception. 
//   * Verifies hospital has one entry.
//   */
//  public void testHospitalWriterGetsOneExceptionFromReadConnector() throws Exception{
//    runAdaptor(CONFIG_FILE_WRITER_3);
//    PreparedStatement preparedStmt = jdbcConnection.getConnection().prepareStatement(SELECT_ALL_ERRORS_SQL);
//    ResultSet rs = preparedStmt.executeQuery();
//    assertTrue("Hospital is empty", rs.next());
//    assertFalse("Hospital has more than one element", rs.next());
//    preparedStmt.close();
//  }
  
  /**
   * Runs adaptor with a data processor node that throws an exception. 
   * Verifies hospital has one entry.
   */
  public void testHospitalWriterGetsOneExceptionFromDataProcessor() throws Exception{
    SystemTestUtil.runAdaptor(this, RESOURCE_LOCATION, CONFIG_FILE_WRITER_4);
    PreparedStatement preparedStmt = jdbcConnection.getConnection().prepareStatement(SELECT_ALL_ERRORS_SQL);
    ResultSet rs = preparedStmt.executeQuery();
    assertTrue("Hospital is empty", rs.next());
    assertFalse("Hospital has more than one element", rs.next());
    preparedStmt.close();
  }
  
  /**
   * Runs adaptor (two times) with a node that throws an exception. 
   * Verifies the hostpital has two exceptions, verifies corect values of its some data.
   */
  public void testHospitalWriterGetsTwoExceptions() throws Exception{
    SpringAdaptor adaptor = SystemTestUtil.runAdaptor(this, RESOURCE_LOCATION, CONFIG_FILE_WRITER_2);
    adaptor.run();
    PreparedStatement preparedStmt = jdbcConnection.getConnection().prepareStatement(SELECT_ALL_ERRORS_SQL);
    ResultSet rs = preparedStmt.executeQuery();
    assertTrue("Hospital is empty, expected two elements.", rs.next());
    assertTrue("Hospital has one elements, expected two. ", rs.next());
    String exceptionClassName = rs.getString("EXCEPTION_CLASS_NAME");
    assertTrue(exceptionClassName!=null);
    assertEquals(exceptionClassName, new RuntimeException().getClass().getName());
    String data = rs.getString("DATA");
    assertTrue(data!=null);
    assertEquals(data, "Dummy read connector test data");
    assertFalse("Hospital has more than two elements", rs.next());
    preparedStmt.close();
  }
  
  
  /**
   * Runs adaptor that creates one entry in the hospital, then 
   * adaptor that reads from the hospital. Runs the hospital reader.
   */
  public void testHospitalReader() throws Exception{
    SystemTestUtil.runAdaptor(this, RESOURCE_LOCATION, CONFIG_FILE_WRITER_2);
    SystemTestUtil.runAdaptor(this, RESOURCE_LOCATION, CONFIG_FILE_READER);
  }
  
  
  /** 
   * Runs adaptor that creates one entry in the hospital, then 
   * the hospital reader. Verifies that reader reads no elements (as they need 
   * to be 'fixed' first)
   */
  public void testHospitalReader2() throws Exception{
    SystemTestUtil.runAdaptor(this, RESOURCE_LOCATION, CONFIG_FILE_WRITER_2);
    Router router = new Router();
    Map processMap = new HashMap();  
    Adaptor adaptor = new Adaptor();
    adaptor.setMessageProcessor(router);
    JDBCReadConnector hospitalReader = assembleHostpitalReader();
    TestComponent.TestWriteConnector writer = new TestComponent.TestWriteConnector();
    processMap.put(hospitalReader, writer);
    router.setProcessMap(processMap);
    assertTrue(writer.counter == 0);
    adaptor.run();
    /* No record should be read from the hospital as the FIXED flat is still set to false. */
    assertTrue(writer.counter == 0);
  }
  
  
//  /** 
//   * Runs adaptor that creates one entry in the hospital. 'Fixes' the records in the hospital. 
//   * Runs the hospital reader, verifies that reader read one element.
//   * @todo find a way to 'fix' hospital data
//   */
//  public void testHospitalReader3() throws Exception{
//    runAdaptor(CONFIG_FILE_WRITER_2);
//    Router router = new Router();
//    Map processMap = new HashMap();  
//    Adaptor adaptor = new Adaptor();
//    adaptor.setMessageProcessor(router);
//    JDBCReadConnector hospitalReader = assembleHostpitalReader();
//    TestComponent.TestWriteConnector writer = new TestComponent.TestWriteConnector();
//    processMap.put(hospitalReader, writer);
//    router.setProcessMap(processMap);
//    assertTrue(writer.counter == 0);
//    adaptor.run();
//    
//    assertTrue(writer.counter == 1);
//  }

  
  private JDBCReadConnector assembleHostpitalReader(){
    JDBCConnection jdbcConnection = new LocalHSQLJdbcConnection();
    JDBCReadConnector hospitalReader = new JDBCReadConnector();
    ResultSetToOrderedMapConverter resultSetConverter = new ResultSetToOrderedMapConverter();
    hospitalReader.setResultSetConverter(resultSetConverter);
    hospitalReader.setJdbcConnection(jdbcConnection);
    hospitalReader.setSql(HOSPITAL_READER_SELECT_STMT);
    return hospitalReader;
  }
  
  
  
  //
  // Helper read and write connectors.
  //
  //
  
  /**
   * Simple read connector that returns one item of data then becomes dry.
   */
  public static final class TestReadConnector implements IReadConnector {
    private boolean isDry = false;
    
    public void connect() {}
    public void disconnect() {}
    public Object getReaderContext() {return null;}
   
    public boolean isDry() { 
      boolean result = isDry;
      isDry = true;
      return result;
    }
   
    public Object[] next(long timeoutMs) { 
      return new String[]{"Dummy read connector test data"}; 
    }
    
    public void validate(List exceptions) {}
  }
  
  /**
   * Simple read connector that throws an exception.
   */
  public static final class ExceptionThrowingReadConnector implements IReadConnector {
    private boolean isDry = false;
    
    public void connect() {}
    public void disconnect() {}
    public Object getReaderContext() {return null;}
   
    public boolean isDry() { 
      boolean result = isDry;
      isDry = true;
      return result;
    }
   
    public Object[] next(long timeoutMs) { 
      throw new RuntimeException("Test read connector exception");
    }
    
    public void validate(List exceptions) {}
  }
   
  /**
   * Dummy write connector - does nothing.
   */
  public static final class DummyWriteConnector extends Component implements IWriteConnector {
    public void connect() {}
    public void disconnect() {}
    public Object deliver(Object[] data) {return null;}
    public void validate(List exceptions) {}
  }
  
  /**
   * Simple write connector that throws an exception.
   */
  public static final class ExceptionThrowingWriteConnector extends Component implements IWriteConnector {
    public void connect() {}
    public void disconnect() {}
    public Object deliver(Object[] data) {
       throw new RuntimeException();
    }
    public void validate(List exceptions) {}
  }
 
  
  /**
   * A data processor connector that throws an exception.
   */
  public static final class ExceptionThrowingDataProcessor extends Component implements IDataProcessor {

    public Object[] process(Object data) {
      throw new RuntimeException("Sample exception from test data processor.");
    }

    public void reset(Object context) {}

    public void validate(List exceptions) {}
  }
}