/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
