/*
* Copyright (C) 2015 serafeim
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package privacytool.gui.data;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author serafeim
 */
public class ChooseMainRDFClassPanel extends JPanel implements ActionListener{
    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private static final Insets EAST_INSETS = new Insets(5, 5, 5, 0);
    
    ButtonGroup buttonGroup = null;
    private int row;
    private GridBagConstraints gbc;
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public ChooseMainRDFClassPanel(Set<String> classes){
        
        setLayout(new GridBagLayout());
        row = 0;
        initPanel(classes);
    }
    
    private void initPanel(Set<String> classes){
        
        //print header
        String header = "<html><b>Please choose main class:</b></html>";
        gbc = createGbc(0, row);
        JLabel headerLabel = new JLabel(header, JLabel.LEFT);
        add(headerLabel, gbc);
        row++;
        
        buttonGroup = new ButtonGroup();
        
        
        //show classes found
        int i = 0;
        for(String curClass : classes){
            JRadioButton radioBtn = new JRadioButton(curClass);
            
            //select first button by default
            if(i == 0){
                radioBtn.setSelected(true);
                i++;
            }
            
            buttonGroup.add(radioBtn);
            gbc = createGbc(0, row);
            add(radioBtn, gbc);
            
            row++;
        }
    }
    
    private GridBagConstraints createGbc(int x, int y) {
        gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        
        gbc.anchor = (x == 0) ? GridBagConstraints.WEST : GridBagConstraints.EAST;
        gbc.fill = (x == 0) ? GridBagConstraints.BOTH
                : GridBagConstraints.HORIZONTAL;
        
        gbc.insets = (x == 0) ? WEST_INSETS : EAST_INSETS;
        gbc.weightx = (x == 0) ? 0.1 : 1.0;
        gbc.weighty = 1.0;
        return gbc;
    }
    
    public String getSelectedClass(){
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            
            if (button.isSelected()) {
                return button.getText();
            }
        }
        
        return null;
    }
}