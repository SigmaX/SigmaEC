package SigmaEC.experiment;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

/**
 * Handles callback from an R session, logging messages accordingly.
 * 
 * @author Eric 'Siggy' Scott
 */
public class RLoggerCallbacks implements RMainLoopCallbacks
{
    final Logger logger;
    public RLoggerCallbacks(final Logger logger) {
        assert(logger != null);
        this.logger = logger;
    }
    
    public void rWriteConsole(final Rengine re, final String text, final int oType) {
        logger.log(Level.INFO, text);
    }

    public void rBusy(final Rengine re, final int which) {
        //System.out.println("rBusy("+which+")");
    }

    public String rReadConsole(final Rengine re, final String prompt, final int addToHistory) {
        System.out.print(prompt);
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            String s=br.readLine();
            return (s==null||s.length()==0)?s:s+"\n";
        } catch (Exception e) {
            logger.log(Level.WARNING, "jriReadConsole exception: " + e.getMessage());
        }
        return null;
    }

    public void rShowMessage(Rengine re, String message) {
        logger.log(Level.INFO, "rShowMessage \""+message+"\"");
    }

    public String rChooseFile(Rengine re, int newFile) {
        FileDialog fd = new FileDialog(new Frame(), (newFile==0)?"Select a file":"Select a new file", (newFile==0)?FileDialog.LOAD:FileDialog.SAVE);
        fd.show();
        String res=null;
        if (fd.getDirectory()!=null) res=fd.getDirectory();
        if (fd.getFile()!=null) res=(res==null)?fd.getFile():(res+fd.getFile());
        return res;
    }

    public void   rFlushConsole (Rengine re) {
    }

    public void   rLoadHistory  (Rengine re, String filename) {
    }

    public void   rSaveHistory  (Rengine re, String filename) {
    }
}

