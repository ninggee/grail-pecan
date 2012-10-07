package org.apache.jsp.jsp.num;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import num.NumberGuessBean;

public final class numguess_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.AnnotationProcessor _jsp_annotationprocessor;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_annotationprocessor = (org.apache.AnnotationProcessor) getServletConfig().getServletContext().getAttribute(org.apache.AnnotationProcessor.class.getName());
  }

  public void _jspDestroy() {
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("<!--\r\n");
      out.write(" Licensed to the Apache Software Foundation (ASF) under one or more\r\n");
      out.write("  contributor license agreements.  See the NOTICE file distributed with\r\n");
      out.write("  this work for additional information regarding copyright ownership.\r\n");
      out.write("  The ASF licenses this file to You under the Apache License, Version 2.0\r\n");
      out.write("  (the \"License\"); you may not use this file except in compliance with\r\n");
      out.write("  the License.  You may obtain a copy of the License at\r\n");
      out.write("\r\n");
      out.write("      http://www.apache.org/licenses/LICENSE-2.0\r\n");
      out.write("\r\n");
      out.write("  Unless required by applicable law or agreed to in writing, software\r\n");
      out.write("  distributed under the License is distributed on an \"AS IS\" BASIS,\r\n");
      out.write("  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\r\n");
      out.write("  See the License for the specific language governing permissions and\r\n");
      out.write("  limitations under the License.\r\n");
      out.write("\r\n");
      out.write("  Number Guess Game\r\n");
      out.write("  Written by Jason Hunter, CTO, K&A Software\r\n");
      out.write("  http://www.servlets.com\r\n");
      out.write("-->\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      num.NumberGuessBean numguess = null;
      synchronized (session) {
        numguess = (num.NumberGuessBean) _jspx_page_context.getAttribute("numguess", PageContext.SESSION_SCOPE);
        if (numguess == null){
          numguess = new num.NumberGuessBean();
          _jspx_page_context.setAttribute("numguess", numguess, PageContext.SESSION_SCOPE);
        }
      }
      out.write('\r');
      out.write('\n');
      org.apache.jasper.runtime.JspRuntimeLibrary.introspect(_jspx_page_context.findAttribute("numguess"), request);
      out.write("\r\n");
      out.write("\r\n");
      out.write("<html>\r\n");
      out.write("<head><title>Number Guess</title></head>\r\n");
      out.write("<body bgcolor=\"white\">\r\n");
      out.write("<font size=4>\r\n");
      out.write("\r\n");
 if (numguess.getSuccess()) { 
      out.write("\r\n");
      out.write("\r\n");
      out.write("  Congratulations!  You got it.\r\n");
      out.write("  And after just ");
      out.print( numguess.getNumGuesses() );
      out.write(" tries.<p>\r\n");
      out.write("\r\n");
      out.write("  ");
 numguess.reset(); 
      out.write("\r\n");
      out.write("\r\n");
      out.write("  Care to <a href=\"numguess.jsp\">try again</a>?\r\n");
      out.write("\r\n");
 } else if (numguess.getNumGuesses() == 0) { 
      out.write("\r\n");
      out.write("\r\n");
      out.write("  Welcome to the Number Guess game.<p>\r\n");
      out.write("\r\n");
      out.write("  I'm thinking of a number between 1 and 100.<p>\r\n");
      out.write("\r\n");
      out.write("  <form method=get>\r\n");
      out.write("  What's your guess? <input type=text name=guess>\r\n");
      out.write("  <input type=submit value=\"Submit\">\r\n");
      out.write("  </form>\r\n");
      out.write("\r\n");
 } else { 
      out.write("\r\n");
      out.write("\r\n");
      out.write("  Good guess, but nope.  Try <b>");
      out.print( numguess.getHint() );
      out.write("</b>.\r\n");
      out.write("\r\n");
      out.write("  You have made ");
      out.print( numguess.getNumGuesses() );
      out.write(" guesses.<p>\r\n");
      out.write("\r\n");
      out.write("  I'm thinking of a number between 1 and 100.<p>\r\n");
      out.write("\r\n");
      out.write("  <form method=get>\r\n");
      out.write("  What's your guess? <input type=text name=guess>\r\n");
      out.write("  <input type=submit value=\"Submit\">\r\n");
      out.write("  </form>\r\n");
      out.write("\r\n");
 } 
      out.write("\r\n");
      out.write("\r\n");
      out.write("</font>\r\n");
      out.write("</body>\r\n");
      out.write("</html>\r\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
