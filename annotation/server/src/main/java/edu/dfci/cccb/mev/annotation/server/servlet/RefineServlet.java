/*

Copyright 2010, Google Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

 * Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above
copyright notice, this list of conditions and the following disclaimer
in the documentation and/or other materials provided with the
distribution.
 * Neither the name of Google Inc. nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,           
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY           
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */

package edu.dfci.cccb.mev.annotation.server.servlet;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.log4j.Log4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.refine.ProjectManager;
import com.google.refine.commands.Command;
import com.google.refine.importing.ImportingManager;

import edu.mit.simile.butterfly.Butterfly;
import edu.mit.simile.butterfly.ButterflyModule;

@Log4j
public class RefineServlet extends Butterfly {

  static private String ASSIGNED_VERSION = "2.6";

  static public String VERSION = "";
  static public String REVISION = "";
  static public String FULL_VERSION = "";
  static public String FULLNAME = "OpenRefine ";

  static public final String AGENT_ID = "/en/google_refine"; // TODO: Unused?
                                                             // Freebase ID

  static final long serialVersionUID = 2386057901503517403L;

  static private final String JAVAX_SERVLET_CONTEXT_TEMPDIR = "javax.servlet.context.tempdir";
  private File tempDir = null;
  private static File s_dataDir;

  static private RefineServlet s_singleton;

  public static RefineServlet getServlet () {
    return s_singleton;
  }

  static final private Map<String, Command> commands = new HashMap<String, Command> ();

  static final Logger logger = LoggerFactory.getLogger ("refine");

  static final protected long AUTOSAVE_PERIOD = 5; // 5 minutes

  static protected class AutoSaveTimerTask implements Runnable {
    @Override
    public void run () {
      try {
        ProjectManager.getSingleton ().save (false); // quick, potentially
                                                     // incomplete save
      } catch (final Throwable e) {
        // Not the best, but we REALLY want this to keep trying
      }
    }
  }

  @Override
  public void init () throws ServletException {
    super.init ();

    VERSION = getInitParameter ("refine.version");
    REVISION = getInitParameter ("refine.revision");

    if (VERSION.equals ("$VERSION")) {
      VERSION = ASSIGNED_VERSION;
    }
    if (REVISION.equals ("$REVISION")) {
      REVISION = "TRUNK";
    }

    FULL_VERSION = VERSION + " [" + REVISION + "]";
    FULLNAME += FULL_VERSION;

    logger.info ("Starting " + FULLNAME + "...");

    s_singleton = this;

    logger.trace ("> initialize");

    String data = getInitParameter ("refine.data");

    if (data == null) {
      throw new ServletException ("can't find servlet init config 'refine.data', I have to give up initializing");
    }

    // s_dataDir = new File(data);
    // FileProjectManager.initialize(s_dataDir);

    ImportingManager.initialize (this);
    /* service.scheduleWithFixedDelay(new AutoSaveTimerTask(), AUTOSAVE_PERIOD,
     * AUTOSAVE_PERIOD, TimeUnit.MINUTES); */
    logger.trace ("< initialize");
  }

  @Override
  public void destroy () {
    logger.trace ("> destroy");

    // cancel automatic periodic saving and force a complete save.
    if (_timer != null) {
      _timer.cancel ();
      _timer = null;
    }
    if (ProjectManager.getSingleton () != null) {
      ProjectManager.getSingleton ().dispose ();
      ProjectManager.setSingleton (null);
    }

    logger.trace ("< destroy");

    super.destroy ();
  }

  @Override
  public void service (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    // logger.info("DEBUG {}", session.getId());
    // logger.info("*****DEBUG getPathInfo: {}", request.getPathInfo());
    // logger.info("*****DEBUG getServletPath: {}", request.getServletPath());
    // logger.info("*****DEBUG getQueryString: {}", request.getQueryString());
    // ProjectManager pm = projectManagerFactory.getProjectManager();
    // logger.info("****PM"+pm.toString());

    if (request.getPathInfo ().startsWith ("/command/")) {
      // if (request.getServletPath().startsWith("/command/")) {
      String commandKey = getCommandKey (request);
      Command command = commands.get (commandKey);
      if (log.isDebugEnabled ())
        log.debug (request.getMethod () + " for command " + commandKey);
      if (command != null) {
        if (request.getMethod ().equals ("GET")) {
          command.doGet (request, response);
        } else if (request.getMethod ().equals ("POST")) {
          command.doPost (request, response);
        } else if (request.getMethod ().equals ("PUT")) {
          command.doPut (request, response);
        } else if (request.getMethod ().equals ("DELETE")) {
          command.doDelete (request, response);
        } else {
          response.sendError (405);
        }
      } else {
        log.debug ("ERROR!!! " + request.getMethod () + " for command " + commandKey);
        response.sendError (404);
      }
    } else {
      super.service (request, response);
    }
  }

  public ButterflyModule getModule (String name) {
    return _modulesByName.get (name);
  }

  protected String getCommandKey (HttpServletRequest request) {
    // A command path has this format: /command/module-name/command-name/...

    String path = request.getPathInfo ().substring ("/command/".length ());

    int slash1 = path.indexOf ('/');
    if (slash1 >= 0) {
      int slash2 = path.indexOf ('/', slash1 + 1);
      if (slash2 > 0) {
        path = path.substring (0, slash2);
      }
    }

    return path;
  }

  public File getTempDir () {
    if (tempDir == null) {
      tempDir = (File) _config.getServletContext ().getAttribute (JAVAX_SERVLET_CONTEXT_TEMPDIR);
      if (tempDir == null) {
        throw new RuntimeException ("This app server doesn't support temp directories");
      }
    }
    return tempDir;
  }

  public File getTempFile (String name) {
    return new File (getTempDir (), name);
  }

  public File getCacheDir (String name) {
    File dir = new File (new File (s_dataDir, "cache"), name);
    dir.mkdirs ();

    return dir;
  }

  public String getConfiguration (String name, String def) {
    return null;
  }

  /**
   * Register a single command.
   * 
   * @param module the module the command belongs to
   * @param name command verb for command
   * @param commandObject object implementing the command
   * @return true if command was loaded and registered successfully
   */
  protected boolean registerOneCommand (ButterflyModule module, String name, Command commandObject) {
    return registerOneCommand (module.getName () + "/" + name, commandObject);
  }

  /**
   * Register a single command.
   * 
   * @param path path for command
   * @param commandObject object implementing the command
   * @return true if command was loaded and registered successfully
   */
  protected boolean registerOneCommand (String path, Command commandObject) {
    if (commands.containsKey (path)) {
      return false;
    }

    commandObject.init (this);
    commands.put (path, commandObject);

    return true;
  }

  // Currently only for test purposes
  protected boolean unregisterCommand (String verb) {
    return commands.remove (verb) != null;
  }

  /**
   * Register a single command. Used by extensions.
   * 
   * @param module the module the command belongs to
   * @param name command verb for command
   * @param commandObject object implementing the command
   * 
   * @return true if command was loaded and registered successfully
   */
  static public boolean registerCommand (ButterflyModule module, String commandName, Command commandObject) {
    return s_singleton.registerOneCommand (module, commandName, commandObject);
  }

  static private class ClassMapping {
    final String from;
    final String to;

    ClassMapping (String from, String to) {
      this.from = from;
      this.to = to;
    }
  }

  static final private List<ClassMapping> classMappings = new ArrayList<ClassMapping> ();

  /**
   * Add a mapping that determines how old class names can be updated to newer
   * class names. Such updates are desirable as the Java code changes from
   * version to version. If the "from" argument ends with *, then it's
   * considered a prefix; otherwise, it's an exact string match.
   * 
   * @param from
   * @param to
   */
  static public void registerClassMapping (String from, String to) {
    classMappings.add (new ClassMapping (from, to.endsWith ("*") ? to.substring (0, to.length () - 1) : to));
  }

  static {
    registerClassMapping ("com.metaweb.*", "com.google.*");
    registerClassMapping ("com.google.gridworks.*", "com.google.refine.*");
  }

  static final private Map<String, String> classMappingsCache = new HashMap<String, String> ();
  static final private Map<String, Class<?>> classCache = new HashMap<String, Class<?>> ();

  // TODO(dfhuynh): Temporary solution until we figure out why cross butterfly
  // module class resolution
  // doesn't entirely work
  static public void cacheClass (Class<?> klass) {
    classCache.put (klass.getName (), klass);
  }

  static public Class<?> getClass (String className) throws ClassNotFoundException {
    String toClassName = classMappingsCache.get (className);
    if (toClassName == null) {
      toClassName = className;

      for (ClassMapping m : classMappings) {
        if (m.from.endsWith ("*")) {
          if (toClassName.startsWith (m.from.substring (0, m.from.length () - 1))) {
            toClassName = m.to + toClassName.substring (m.from.length () - 1);
          }
        } else {
          if (m.from.equals (toClassName)) {
            toClassName = m.to;
          }
        }
      }

      classMappingsCache.put (className, toClassName);
    }

    Class<?> klass = classCache.get (toClassName);
    if (klass == null) {
      klass = Class.forName (toClassName);
      classCache.put (toClassName, klass);
    }
    return klass;
  }

  static public void setUserAgent (URLConnection urlConnection) {
    if (urlConnection instanceof HttpURLConnection) {
      setUserAgent ((HttpURLConnection) urlConnection);
    }
  }

  static public void setUserAgent (HttpURLConnection httpConnection) {
    httpConnection.addRequestProperty ("User-Agent", getUserAgent ());
  }

  static public String getUserAgent () {
    return "OpenRefine/" + FULL_VERSION;
  }
}