package org.basex.examples.server;

import org.basex.BaseXServer;
import org.basex.core.BaseXException;
import org.basex.core.cmd.Add;
import org.basex.core.cmd.XQuery;
import org.basex.examples.query.QueryCollection;
import org.basex.server.ClientSession;

/**
 * This class demonstrates database access via the client/server architecture.
 * It shows how to {@link #add(String, String, String)},
 * {@link #modify()} files.
 * For further options see {@link QueryCollection}.
 *
 * @author BaseX Team 2005-11, BSD License
 */
public final class ServerCollection {
  /** Session reference. */
  static ClientSession session;
  /** XML Document Fragment Pt. 1. */
  static final String XML_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
    + "<text version=\"draft\"><title>Chapter ";
  /** XML Document Fragment Pt. 2. */
  static final String XML_2 = "</title></text>";

  /**
   * Runs the example code.
   * @param args (ignored) command-line arguments
   * @throws Exception exception
   */
  public static void main(final String[] args) throws Exception {

    System.out.println("=== ServerCollection ===");

    // ------------------------------------------------------------------------
    // Start server
    System.out.println("\n* Start server.");
    BaseXServer server = new BaseXServer();

    // Create a client session with host name, port, user name and password
    session = new ClientSession("localhost", 1984, "admin", "admin");

    // ------------------------------------------------------------------------
    System.out.println("\n* Create a Collection.");
    session.execute("CREATE DB input");

    // ------------------------------------------------------------------------
    // Add some 50 documents
    System.out.println("\n* Adding 50 documents");

    for(int i = 0; i < 50; i++) {
      add(XML_1 + i + XML_2, "Chapter-" + i + ".xml", "/book/chapters/" + i);
    }
    // Add another Test Document in folder /book/chapters/0
    add(XML_1 + "test" + XML_2, "Chapter-test.xml", "/book/chapters/0");

    // ------------------------------------------------------------------------
    // Find some documents using the collection command
    find();

    // ------------------------------------------------------------------------
    // Modify specific document(s)
    System.out.println("\n* Modifying documents in folder /book/chapters/0:");
    modify();

    // ------------------------------------------------------------------------
    // Drop the database
    session.execute("DROP DB input");

    // ------------------------------------------------------------------------
    // Close the client session
    session.close();

    // ------------------------------------------------------------------------
    // Stop the server
    System.out.println("\n* Stop server.");
    server.stop();
  }

  /** This Methods performs a simple path based search in a collection.
   * @throws BaseXException on error.
   */
  private static void find() throws BaseXException {
    // ------------------------------------------------------------------------
    System.out.println("\n* Finding documents in folder /book/chapters/0:");
    System.out.println(session.execute(
        new XQuery(
         "for $doc in collection('input/book/chapters/0') " +
         "return $doc")));
  }

  /**
   * This method shows how to modify multiple documents at once.
   * It replaces the title of the matching documents with 1 2 3.
   * @throws BaseXException on error.
   */
  private static void modify() throws BaseXException {
    session.execute(new XQuery(
        "for $doc in collection('input/book/chapters/0/')" + " return "
            + "replace value of node $doc/text/title  "
            + "with (1 to 3)"));

    // Validate result
    System.out.println(session.execute(new XQuery(
        "for $doc in collection('input/book/chapters/0')" + " return $doc")));
  }

  /**
   * Adds a document to the collection.
   * @param xmlFragment XML Fragment
   * @param docname document name
   * @param target optional target path
   * @throws BaseXException on error.
   */
  private static void add(final String xmlFragment, final String docname,
      final String target) throws BaseXException {

    session.execute(new Add(xmlFragment, docname, target));
  }
}
