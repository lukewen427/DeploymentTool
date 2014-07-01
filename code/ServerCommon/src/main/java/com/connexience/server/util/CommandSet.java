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
package com.connexience.server.util;

import java.util.ArrayList;

/**
 * This class will run all of the commands in a set proceeding to the next
 * command only if the previous one had a zero exit code/
 * @author hugo
 */
public class CommandSet {
    private ArrayList<String> commands = new ArrayList<>();
    private ArrayList<Integer> exitCodes = new ArrayList<>();
    private String failedCommand;
    
    public CommandSet() {
    }
    
    public void add(String cmd){
        commands.add(cmd);
    }
    
    public boolean execute(){
        int exitCode;
        boolean error = false;
        CommandRunner runner = new CommandRunner();
        for(String cmd : commands){
            if(!error){
                try {                    
                    exitCode = runner.run(cmd);
                    exitCodes.add(exitCode);
                    if(exitCode!=0){
                        error = true;
                        failedCommand = cmd;
                    }
                } catch (Exception e){
                    exitCodes.add(1);
                    failedCommand = cmd;
                    error = true;
                }
                
            } else {
                exitCodes.add(1);
            }
        }
        if(error){
            return false;
        } else {
            return true;
        }
    }
            
    public int getExitCode(int index){
        return exitCodes.get(index);
    }
    
    public String getFailedCommand(){
        return failedCommand;
    }
}
