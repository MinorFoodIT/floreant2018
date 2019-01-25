//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.floreantpos.extension;

import com.orocube.common.util.TerminalUtil;
//import com.orocube.licensemanager.InvalidLicenseException;
//import com.orocube.licensemanager.LicenseUtil;
//import com.orocube.licensemanager.OroLicense;
//import com.orocube.licensemanager.ui.InvalidPluginDialog;
//import com.orocube.licensemanager.ui.LicenseSelectionListener;
import java.awt.Component;
import java.io.File;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractFloreantPlugin implements FloreantPlugin {        //, LicenseSelectionListener {
    //private OroLicense license;

    public AbstractFloreantPlugin() {
    }

    public boolean requireLicense() {
        return true;
    }

    public void initLicense() {
        /*
        try {
            this.license = LicenseUtil.loadAndValidate(this.getProductName(), this.getProductVersion(), TerminalUtil.getSystemUID());
        } catch (InvalidLicenseException var2) {
            InvalidPluginDialog.show(this, this.getParent(), var2.getMessage(), "Plugin error!", this.getProductName(), this.getProductVersion(), TerminalUtil.getSystemUID());
        } catch (Exception var3) {
            LogFactory.getLog(this.getClass()).error(var3);
        }
        */
    }

    public abstract String getId();

    public abstract String getProductName();

    public abstract String getProductVersion();

    public abstract Component getParent();

    public void licenseFileSelected(File pluginFile) throws Exception {
        /*
        try {
            OroLicense newLicense = LicenseUtil.loadAndValidate(pluginFile, this.getProductName(), this.getProductVersion(), TerminalUtil.getSystemUID());
            LicenseUtil.copyLicenseFile(pluginFile, this.getProductName());
            this.setLicense(newLicense);
        } catch (Exception var3) {
            LogFactory.getLog(this.getClass()).error(var3);
            throw var3;
        }
        */
    }

    public boolean hasValidLicense() {
        return true; //this.license != null ? this.license.isValid() : false;
    }


    public Object getLicense() {
        return null; //this.license;
    }

    /*
    public void setLicense(OroLicense license) {
        this.license = license;
    }
    */

    public void initBackoffice() {
    }

    public void initConfigurationView(JDialog dialog) {
    }

    public List<AbstractAction> getSpecialFunctionActions() {
        return null;
    }

    public void initUI() {
    }

    public void restartPOS(boolean restart) {
    }
}
