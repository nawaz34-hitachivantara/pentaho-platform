/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.platform.uifoundation.component;

import org.dom4j.Element;
import org.pentaho.commons.connection.IPentahoResultSet;
import org.pentaho.platform.api.engine.IActionParameter;
import org.pentaho.platform.api.engine.ILogger;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IRuntimeContext;
import org.pentaho.platform.api.engine.ISolutionEngine;
import org.pentaho.platform.engine.core.output.SimpleOutputHandler;
import org.pentaho.platform.engine.core.solution.ActionInfo;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.util.xml.dom4j.XmlDom4JHelper;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * @author Steven Barkdull
 * 
 */
public class ActionFilterDefinition extends FilterDefinition {

  public ActionFilterDefinition( final Element node, final IPentahoSession session, final ILogger logger ) {
    super( node, session, logger );
  }

  /**
   * 
   * If the <data-output> element is present in the xml file, its text specifies the name of the output parameter
   * in the runtime context containing the result set.
   * 
   * However, the <data-output> element is not required. If this element is not in the xml file, then the first
   * parameter in the runtime context's output of type "resultset" contains the result set.
   */
  @Override
  protected IPentahoResultSet getResultSet( final Map parameterProviders ) {
    // create an instance of the solution engine to execute the specified
    // action

    // TODO we need to ensure that this data is cached (not live) so that we
    // can validate selections

    String solution = XmlDom4JHelper.getNodeText( "data-solution", node ); //$NON-NLS-1$
    String actionPath = XmlDom4JHelper.getNodeText( "data-path", node ); //$NON-NLS-1$
    String actionName = XmlDom4JHelper.getNodeText( "data-action", node ); //$NON-NLS-1$
    String listSource = XmlDom4JHelper.getNodeText( "data-output", node ); //$NON-NLS-1$

    ISolutionEngine solutionEngine = PentahoSystem.get( ISolutionEngine.class, session );
    solutionEngine.setLoggingLevel( ILogger.DEBUG );
    solutionEngine.init( session );

    OutputStream outputStream = null;
    SimpleOutputHandler outputHandler = null;
    outputHandler = new SimpleOutputHandler( outputStream, false );

    ArrayList messages = new ArrayList();
    String processId = this.getClass().getName();
    String instanceId = null;

    IRuntimeContext context = null;
    try {
      String actionSeqPath = ActionInfo.buildSolutionPath( solution, actionPath, actionName );
      context =
          solutionEngine.execute( actionSeqPath, processId, false, true, instanceId, false, parameterProviders,
              outputHandler, null, null, messages );

      if ( listSource != null ) {
        if ( context.getOutputNames().contains( listSource ) ) {
          IActionParameter output = context.getOutputParameter( listSource );
          IPentahoResultSet results = output.getValueAsResultSet();
          if ( results != null ) {
            results = results.memoryCopy();
          }
          return results;
        } else {
          // this is an error
          return null;
        }
      } else {
        // return the first list that we find...
        Iterator it = context.getOutputNames().iterator();
        while ( it.hasNext() ) {
          IActionParameter output = (IActionParameter) it.next();
          if ( output.getType().equalsIgnoreCase( IActionParameter.TYPE_RESULT_SET ) ) {
            IPentahoResultSet results = output.getValueAsResultSet();
            if ( results != null ) {
              results = results.memoryCopy();
            }
            return results;
          }
        }
      }
      return null;
    } finally {
      if ( context != null ) {
        context.dispose();
      }
    }
  }

  public static void main( final String[] args ) {

  }

}
