/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package privacytool.gui.data;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import privacytool.framework.data.Data;
import privacytool.framework.hierarchy.Hierarchy;

/**
 *
 * @author serafeim
 */
public class QuasiIdentifiersPanel extends JPanel implements ActionListener{
    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private static final Insets EAST_INSETS = new Insets(5, 5, 5, 0);
    
    Data dataset = null;
    Map<String, Hierarchy> hierarchies = null;
    Map<Integer, Hierarchy> quasiIdentifiers = null;
    Map<Integer, JComboBox> quasiToComboBox = new HashMap<>();
    
    private int row;
    private GridBagConstraints gbc;
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public QuasiIdentifiersPanel(Data _dataset, Map<Integer, Hierarchy> _quasiIdentifiers, Map<String, Hierarchy> _hierarchies){
        dataset = _dataset;
        quasiIdentifiers = _quasiIdentifiers;
        hierarchies = _hierarchies;
        setLayout(new GridBagLayout());
//        setBorder(BorderFactory.createCompoundBorder(
//            BorderFactory.createTitledBorder("Quasi-Identifiers"),
//            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        row = 0;
        initPanel();
    }
    
    private void initPanel(){
        
        Map<Integer, String> colNames = dataset.getColumnsPosition();
        Map<Integer, String> colTypes = dataset.getColumnsTypes();
        
        //print header
        String header = "<html><b>Attributes</b></html>";
        gbc = createGbc(0, row);
        JLabel headerLabel = new JLabel(header, JLabel.LEFT);
        add(headerLabel, gbc);
        header = "<html><b>Set Hierarchy</b></html>";
        gbc = createGbc(1, row);
        headerLabel = new JLabel(header, JLabel.LEFT);
        add(headerLabel, gbc);
        row++;
        
        
        //print quasi identifiers
        for(Integer i : quasiIdentifiers.keySet()){
            
            JLabel columnName = new JLabel(colNames.get(i), JLabel.LEFT);
            gbc = createGbc(0, row);
            add(columnName, gbc);
            
            JComboBox comboBox = new JComboBox();
            //create combobox with hierarchies of correct type to choose from
            if(colTypes.get(i).equals("double")){
                for(String hierarchyName : hierarchies.keySet()){
                    if(hierarchies.get(hierarchyName).getNodesType().equals("double")){
//                        System.out.println("\t "+ colNames.get(i) +": " + hierarchyName);
                        comboBox.addItem(hierarchyName);
                    }
                    
                }
            } else if(colTypes.get(i).equals("int")){
                for(String hierarchyName : hierarchies.keySet()){
                    if(hierarchies.get(hierarchyName).getNodesType().equals("int") || 
                            hierarchies.get(hierarchyName).getNodesType().equals("double")){
//                        System.out.println("\t "+ colNames.get(i) +": " + hierarchyName);
                        comboBox.addItem(hierarchyName);
                    }
                    
                }
            } else if(colTypes.get(i).equals("string")){
                for(String hierarchyName : hierarchies.keySet()){
                    if(hierarchies.get(hierarchyName).getNodesType().equals("string")){
//                        System.out.println("\t "+ colNames.get(i) +": " + hierarchyName);
                        comboBox.addItem(hierarchyName);
                    }
                    
                }
            }
            gbc = createGbc(1, row);
            if(comboBox.getItemCount() == 0){
               JLabel label = new JLabel("No matching hierachies of column's type found", JLabel.LEFT);
               add(label, gbc);
            }
            else{
                add(comboBox, gbc);
                quasiToComboBox.put(i, comboBox);
            }
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

    public Map<Integer, JComboBox> getQuasiToComboBox() {
        return quasiToComboBox;
    }
    
    
}
