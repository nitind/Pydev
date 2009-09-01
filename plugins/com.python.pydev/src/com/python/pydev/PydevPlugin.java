package com.python.pydev;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.python.pydev.core.docutils.StringUtils;
import org.python.pydev.editor.PyEdit;


/**
 * The main plugin class to be used in the desktop.
 */
public class PydevPlugin extends AbstractUIPlugin {
    

    public static final String version = "REPLACE_VERSION";

    //The shared instance.
    private static PydevPlugin plugin;
    public static final String ANNOTATIONS_CACHE_KEY = "MarkOccurrencesJob Annotations";
    public static final String OCCURRENCE_ANNOTATION_TYPE = "com.python.pydev.occurrences";
    
    /**
     * The constructor.
     */
    public PydevPlugin() {
        plugin = this;
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        
        if(!version.equals(org.python.pydev.plugin.PydevPlugin.version)){
            final String msg = StringUtils.format("The Pydev extensions version (%s) differs from " +
            		"the Pydev version (%s) installed.", version, org.python.pydev.plugin.PydevPlugin.version);
            
            
            Display disp = Display.getDefault();
            disp.asyncExec(new Runnable() {
                public void run() {
                    IWorkbenchWindow window = getDefault().getWorkbench().getActiveWorkbenchWindow();
                    Shell shell = window == null ? null : window.getShell();
                    if (shell != null) {
                        ErrorDialog.openError(shell, "Pydev: version mismatch.", 
                                "The versions of Pydev Extensions and Pydev don't match.", 
                                new Status(IStatus.ERROR, getPluginID(), -1, msg, null));
                    }
                }
            });

        }
    }

    
    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }

    /**
     * Returns the shared instance.
     */
    public static PydevPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path.
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin("com.python.pydev", path);
    }
    
    
    /**
     * @return the list of occurrence annotations in the pyedit
     */
    @SuppressWarnings("unchecked")
    public static final List<Annotation> getOccurrenceAnnotationsInPyEdit(final PyEdit pyEdit) {
        List<Annotation> toRemove = new ArrayList<Annotation>();
        final Map<String, Object> cache = pyEdit.cache;
        
        if(cache == null){
            return toRemove;
        }
        
        List<Annotation> inEdit = (List<Annotation>) cache.get(ANNOTATIONS_CACHE_KEY);
        if(inEdit != null){
            Iterator<Annotation> annotationIterator = inEdit.iterator();
            while(annotationIterator.hasNext()){
                Annotation annotation = annotationIterator.next();
                if(annotation.getType().equals(OCCURRENCE_ANNOTATION_TYPE)){
                    toRemove.add(annotation);
                }
            }
        }
        return toRemove;
    }

    
    public static String getPluginID() {
        return getDefault().getBundle().getSymbolicName();
    }

}
