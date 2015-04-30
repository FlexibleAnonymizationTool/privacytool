/* 
 * Copyright (C) 2015 "IMIS-Athena R.C.",
 * Institute for the Management of Information Systems, part of the "Athena" 
 * Research and Innovation Centre in Information, Communication and Knowledge Technologies.
 * [http://www.imis.athena-innovation.gr/]
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
package privacytool.gui.anonymizationrules;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author serafeim
 */
public class ImportRulesPanel extends JPanel implements ActionListener{
    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private static final Insets EAST_INSETS = new Insets(5, 5, 5, 0);
    
    Map<String, Map<String, String>> rulesInFile = null;
    
    private int row;
    private GridBagConstraints gbc;
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public ImportRulesPanel(Map<String, Map<String, String>> rulesInFile){
        this.rulesInFile = rulesInFile;
        
        setLayout(new GridBagLayout());
        row = 0;
        initPanel();
    }
    
    private void initPanel(){
        
        //print header
        String header = "<html><b>Found anonymization rules for:</b></html>";
        gbc = createGbc(0, row);
        JLabel headerLabel = new JLabel(header, JLabel.LEFT);
        add(headerLabel, gbc);
        row++;
        
        
        //print rules found
        for(String columnName : this.rulesInFile.keySet()){
            
            JLabel columnLabel = new JLabel(columnName, JLabel.LEFT);
            gbc = createGbc(0, row);
            add(columnLabel, gbc);

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
    
}
