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
package com.connexience.server.workflow.blocks.processor;

import javax.swing.*;
import java.awt.*;
import org.pipeline.core.drawing.*;
import org.pipeline.core.drawing.layout.*;
import org.pipeline.core.drawing.gui.*;
import org.pipeline.core.drawing.layout.DrawingLayout;

/**
 * This class extends the default block renderer to display the version
 * @author nhgh
 */
public class DataProcessorBlockRenderer extends DefaultBlockRenderer {
    /** Latest version icon */
    private ImageIcon latestIcon = new ImageIcon(getClass().getResource("/com/connexience/server/workflow/resource/star.png"));

    /** Older version icon */
    private ImageIcon versionedIcon = new ImageIcon(getClass().getResource("/com/connexience/server/workflow/resource/clock.png"));

    /** Fetching data icon */
    private ImageIcon fetchingIcon = new ImageIcon(getClass().getResource("/com/connexience/server/workflow/resource/world_go.png"));

    public DataProcessorBlockRenderer() {
        super();
    }

    @Override
    public void render(Graphics2D display, DrawingLayout layout, BlockExecutionReport status) throws DrawingException {
        super.render(display, layout, status);
        if(getBlock() instanceof DataProcessorBlock){
            DataProcessorBlock dpb = (DataProcessorBlock)getBlock();

            boolean dynamic = dpb.isDynamicService();
            BlockModelPosition pos = layout.getLocationData(dpb);

            if(pos!=null){
                // Is the service fetching data
                if(dpb.isFetchingDefinition()){
                    display.drawImage(fetchingIcon.getImage(), pos.getLeft() + 22, pos.getTop() + 28, null);
                }

                if(dynamic){
                    // This service is dynamic
                    if(dpb.getUsesLatest()){
                        display.drawImage(latestIcon.getImage(), pos.getLeft() + 2, pos.getTop() + pos.getHeight() - 18, null);
                    } else {
                        if(dpb.getVersionId()!=null){
                            // Dynamic versioned service
                            display.drawImage(versionedIcon.getImage(), pos.getLeft() + 2, pos.getTop() + pos.getHeight() - 18, null);
                            display.drawString(Integer.toString(dpb.getVersionNumber()), pos.getLeft() + 20, pos.getTop() + pos.getHeight() - 6);

                        } else {
                            // Dynamic latest version
                            display.drawImage(latestIcon.getImage(), pos.getLeft() + 2, pos.getTop() + pos.getHeight() - 18, null);
                        }
                    }
                }
            }
        }
    }
}
