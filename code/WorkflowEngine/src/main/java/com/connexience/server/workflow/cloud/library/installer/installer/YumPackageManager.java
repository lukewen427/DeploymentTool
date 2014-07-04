/**
 * e-Science Central
 * Copyright (C) 2008-2013 School of Computing Science, Newcastle University
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation at:
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, 5th Floor, Boston, MA 02110-1301, USA.
 */
package com.connexience.server.workflow.cloud.library.installer.installer;
import com.connexience.server.util.CommandRunner;
import com.connexience.server.workflow.cloud.library.LibraryPreparationReport;
import com.connexience.server.workflow.cloud.library.installer.*;


/**
 * This class provides a wrapper to the YUM package manager.
 * @author hugo
 */
public class YumPackageManager implements PackageManager {
    @Override
    public InstallResult installPackage(String packageName, String extraArguments, LibraryPreparationReport report) throws InstallerException {
        CommandRunner runner = new CommandRunner();       
        try {
            report.addMessage(LibraryPreparationReport.INFORMATION_MESSAGE, "Installing: " + packageName + " using YUM");
            int exitCode = runner.run("sudo /usr/bin/yum -q -y install " + packageName + " " + extraArguments);
            if(exitCode==0){
                // Command ran, check output
                InstallResult result = new InstallResult(InstallResult.InstallStatus.INSTALLED_OK, runner.sysOut(), runner.sysErr(), exitCode);
                report.addMessage(LibraryPreparationReport.ITEM_DOWNLOADED_OK, packageName + " installed OK using YUM");
                return result;
            } else {
                // Command failed
                InstallResult result = new InstallResult(InstallResult.InstallStatus.INSTALL_FAILED, runner.sysOut(), runner.sysErr(), exitCode);
                report.addMessage(LibraryPreparationReport.ITEM_DOWNLOAD_FAILED, packageName + " installed FAILED using YUM: \n" + runner.sysErr() + "\n");
                return result;
            }   
            
        } catch (Exception e){
            if(runner.isCommandStarted()){
                InstallResult result = new InstallResult(InstallResult.InstallStatus.INSTALL_FAILED, runner.sysOut(), runner.sysErr(), runner.getExitCode());
                report.addMessage(LibraryPreparationReport.ITEM_DOWNLOAD_FAILED, packageName + " installed FAILED using YUM: \n" + runner.sysErr() + "\n");
                throw new InstallerException(result, "Error checking package status", e);
                        
            } else {
                throw new InstallerException(null, "Error checking package status", e);
            }
        } 
    }

    @Override
    public boolean isInstalled(String packageName, LibraryPreparationReport report) throws InstallerException {
        CommandRunner runner = new CommandRunner();       
        try {
            report.addMessage(LibraryPreparationReport.INFORMATION_MESSAGE, "Checking status of package: " + packageName + " using RPM/YUM");
            int exitCode = runner.run("sudo /bin/rpm -q " + packageName);
            if(exitCode==0){
                // Command ran, check output
                report.addMessage(LibraryPreparationReport.INFORMATION_MESSAGE, packageName + " is installed");
                return true;
            } else {
                // Command failed
                report.addMessage(LibraryPreparationReport.INFORMATION_MESSAGE, packageName + " is NOT installed");
                return false;
            }
        } catch (Exception e){
            if(runner.isCommandStarted()){
                InstallResult result = new InstallResult(InstallResult.InstallStatus.INSTALL_FAILED, runner.sysOut(), runner.sysErr(), runner.getExitCode());
                report.addMessage(LibraryPreparationReport.ITEM_DOWNLOAD_FAILED, "Error checking status of " + packageName + "\n" + runner.sysErr());
                throw new InstallerException(result, "Error checking package status", e);
                        
            } else {
                report.addMessage(LibraryPreparationReport.ITEM_DOWNLOAD_FAILED, "Could not start rpm to check installation status of: " + packageName);
                throw new InstallerException(null, "Error checking package status", e);
            }
        }
    }

    @Override
    public InstallResult updateInstalledPackages(LibraryPreparationReport report) throws InstallerException {
        CommandRunner runner = new CommandRunner();
        try {
            int exitCode = runner.run("sudo /usr/bin/yum -q -y update");
            if (exitCode == 0) {
                InstallResult result = new InstallResult(InstallResult.InstallStatus.INSTALLED_OK, runner.sysOut(), runner.sysErr(), exitCode);
                return result;
            } else {
                InstallResult result = new InstallResult(InstallResult.InstallStatus.INSTALL_FAILED, runner.sysOut(), runner.sysErr(), exitCode);
                return result;
            }
        } catch (Exception e) {
            if (runner.isCommandStarted()) {
                InstallResult result = new InstallResult(InstallResult.InstallStatus.INSTALL_FAILED, runner.sysOut(), runner.sysErr(), runner.getExitCode());
                throw new InstallerException(result, "Error checking package status", e);

            } else {
                throw new InstallerException(null, "Error checking package status", e);
            }
        }
    }
        
}
