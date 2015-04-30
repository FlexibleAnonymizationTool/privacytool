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
package privacytool.gui.hierarchy;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import privacytool.framework.data.Data;

/**
 *
 * @author serafeim
 */
public class HierarchyAutogeneratePanel extends JPanel implements ActionListener{

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public enum DistinctField {
        NAME("Name"), NODES_TYPE("Nodes Type"), ATTRIBUTE("on Attribute"), 
        SORTING("Sorting"), FANOUT("Fanout"), PLUS_MINUS("+/-");

        private String title;

        private DistinctField(String title) {
           this.title = title;
        }

        public String getTitle() {
           return title;
        }
    };

    public enum RangeField{
        NAME("Name"), NODES_TYPE("Nodes Type"), ATTRIBUTE("on Attribute"), 
        START_END("Start-End"), STEP("Step"), FANOUT("Fanout"), PLUS_MINUS("+/-");

        private String title;

        private RangeField(String title) {
          this.title = title;
        }

        public String getTitle() {
           return title;
        }

    };

    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private static final Insets EAST_INSETS = new Insets(5, 5, 5, 0);
    private Map<Object, JComponent> fieldMap = new HashMap<>();
    private List<JLabel> labels = new ArrayList<>();
    private GridBagConstraints gbc;
    private int row;
    public String hierarchyType = null;
    JComboBox hierarchyTypesCombo = null;
    Data dataset = null;
    JComboBox nodesTypeCombo = null;
    JComboBox attributesCombo = null;
    JComboBox sortingTypesCombo = null;
    
    public HierarchyAutogeneratePanel(Data _dataset) {
        dataset = _dataset;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Hierarchy Parameters"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        row = 0;

        initHierarchyTypeCombo();
    }
    
    private void initPanelDistinct(){
        int position = 0;
        
        //initialize Name 
        DistinctField fieldTitle = DistinctField.values()[position++];
        gbc = createGbc(0, row);
        JLabel label = new JLabel(fieldTitle.getTitle()+ ":", JLabel.LEFT);
        labels.add(label);
        add(label, gbc);
        gbc = createGbc(1, row);
        JTextField nameText = new JTextField();
        nameText.setText("Untitled");
        add(nameText, gbc);
        fieldMap.put(fieldTitle, nameText);
        row++; 
        
        //initialize nodes type combobox
        fieldTitle = DistinctField.values()[position++];
        gbc = createGbc(0, row);
        label = new JLabel(fieldTitle.getTitle()+ ":", JLabel.LEFT);
        labels.add(label);
        add(label, gbc);
        gbc = createGbc(1, row);
        String[] nodesType = { "int", "double", "string" };
        nodesTypeCombo = new JComboBox(nodesType);
        nodesTypeCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED ) {
                    //change items in nodes type combobox
                    if(dataset!=null){
                        String[] columns = getColumnsByType((String)nodesTypeCombo.getSelectedItem());
                        attributesCombo.removeAllItems();
                        for (String column : columns) {
                            attributesCombo.addItem(column);
                        }
                    }
                    
                    //change items in sorting types combobox
                    String[] sortingTypes = getSortingTypes((String)nodesTypeCombo.getSelectedItem());
                    sortingTypesCombo.removeAllItems();
                    for (String sortingType : sortingTypes) {
                        sortingTypesCombo.addItem(sortingType);
                    }
                }            
            }
        });

        add(nodesTypeCombo, gbc); 
        fieldMap.put(fieldTitle, nodesTypeCombo);
        row++;
        
        //initialize attributes combobox
        fieldTitle = DistinctField.values()[position++];
        gbc = createGbc(0, row);
        label = new JLabel(fieldTitle.getTitle()+ ":", JLabel.LEFT);
        labels.add(label);
        add(label, gbc);
        gbc = createGbc(1, row);
        if(dataset == null){
            label = new JLabel("No dataset loaded!", JLabel.LEFT); 
            label.setForeground(Color.red);
            labels.add(label);
            add(label, gbc);
        }else{
            String[] columns = getColumnsByType((String)nodesTypeCombo.getSelectedItem());
            attributesCombo = new JComboBox(columns);
            add(attributesCombo, gbc); 
            fieldMap.put(fieldTitle, attributesCombo);
        }
        row++;
        
        //initialize sorting combobox
        fieldTitle = DistinctField.values()[position++];
        gbc = createGbc(0, row);
        label = new JLabel(fieldTitle.getTitle()+ ":", JLabel.LEFT);
        labels.add(label);
        add(label, gbc);
        gbc = createGbc(1, row);
        String[] sortingTypes = getSortingTypes((String)nodesTypeCombo.getSelectedItem());
        sortingTypesCombo =new JComboBox(sortingTypes);
        add(sortingTypesCombo, gbc); 
        fieldMap.put(fieldTitle, sortingTypesCombo);
        row++;
        
        //initialize fanout 
        fieldTitle = DistinctField.values()[position++];
        gbc = createGbc(0, row);
        label = new JLabel(fieldTitle.getTitle()+ ":", JLabel.LEFT);
        labels.add(label);
        add(label, gbc);
        gbc = createGbc(1, row);
        JTextField fanoutText = new JTextField();
        fanoutText.setText("10");
        add(fanoutText, gbc);
        fieldMap.put(fieldTitle, fanoutText);
        row++;
        
        //initialize +/- fanout
        fieldTitle = DistinctField.values()[position++];
        gbc = createGbc(0, row);
        label = new JLabel(fieldTitle.getTitle()+ " (<fanout/2):", JLabel.LEFT);
        labels.add(label);
        add(label, gbc);
        gbc = createGbc(1, row);
        JTextField plusMinusFanoutText = new JTextField();
        plusMinusFanoutText.setText("0");
        add(plusMinusFanoutText, gbc);
        fieldMap.put(fieldTitle, plusMinusFanoutText);
        
        
        /*
        //fanout group Radio Buttons
        fieldTitle = DistinctField.values()[position++];
        gbc = createGbc(1, ++row);
        JRadioButton exactBtn = new JRadioButton("exact");
        JRadioButton maxBtn = new JRadioButton("max");
        ButtonGroup group = new ButtonGroup();
        group.add(exactBtn);
        group.add(maxBtn);
        exactBtn.setSelected(true);
        add(exactBtn, gbc);
        fieldMap.put(fieldTitle, exactBtn);
        gbc = createGbc(1, ++row);
        fieldTitle = DistinctField.values()[position++];
        add(maxBtn, gbc);
        fieldMap.put(fieldTitle, maxBtn);
                */
        
    }
    
    private void initPanelRange(){
        int position = 0;
        
        //initialize Name 
        RangeField fieldTitle = RangeField.values()[position++];
        gbc = createGbc(0, row);
        JLabel label = new JLabel(fieldTitle.getTitle()+ ":", JLabel.LEFT);
        labels.add(label);
        add(label, gbc);
        gbc = createGbc(1, row);
        JTextField nameText = new JTextField();
        nameText.setText("Untitled");
        add(nameText, gbc);
        fieldMap.put(fieldTitle, nameText);
        row++; 
        
        //initialize type of Nodes
//        fieldTitle = RangeField.values()[position++];
//        gbc = createGbc(0, row);
//        label = new JLabel(fieldTitle.getTitle()+ ":", JLabel.LEFT);
//        labels.add(label);
//        add(label, gbc);
//        gbc = createGbc(1, row);
//        String[] nodesType = { "int", "double" };
//        nodesTypeCombo = new JComboBox(nodesType);
//        add(nodesTypeCombo, gbc); 
//        fieldMap.put(fieldTitle, nodesTypeCombo);
//        row++;
        
        //initialize nodes type combobox
        fieldTitle = RangeField.values()[position++];
        gbc = createGbc(0, row);
        label = new JLabel(fieldTitle.getTitle()+ ":", JLabel.LEFT);
        labels.add(label);
        add(label, gbc);
        gbc = createGbc(1, row);
        String[] nodesType = { "int", "double" };
        nodesTypeCombo = new JComboBox(nodesType);
        nodesTypeCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED ) {
                    //change items in nodes type combobox
                    if(dataset!=null){
                        String[] columns = getColumnsByType((String)nodesTypeCombo.getSelectedItem());
                        attributesCombo.removeAllItems();
                        for (String column : columns) {
                            attributesCombo.addItem(column);
                        }
                    }
                    
                    //change items in sorting types combobox
//                    String[] sortingTypes = getSortingTypes((String)nodesTypeCombo.getSelectedItem());
//                    sortingTypesCombo.removeAllItems();
//                    for (String sortingType : sortingTypes) {
//                        sortingTypesCombo.addItem(sortingType);
//                    }
                }            
            }
        });

        add(nodesTypeCombo, gbc); 
        fieldMap.put(fieldTitle, nodesTypeCombo);
        row++;
        
        //initialize attributes combobox
        fieldTitle = RangeField.values()[position++];
        gbc = createGbc(0, row);
        label = new JLabel(fieldTitle.getTitle()+ ":", JLabel.LEFT);
        labels.add(label);
        add(label, gbc);
        gbc = createGbc(1, row);
        if(dataset == null){
            label = new JLabel("No dataset loaded!", JLabel.LEFT); 
            label.setForeground(Color.red);
            labels.add(label);
            add(label, gbc);
        }else{
            String[] columns = getColumnsByType((String)nodesTypeCombo.getSelectedItem());
            attributesCombo = new JComboBox(columns);
            
            add(attributesCombo, gbc); 
            fieldMap.put(fieldTitle, attributesCombo);
        }
        row++;
        
        //Initialize start-end TextField
        fieldTitle = RangeField.values()[position++];
        gbc = createGbc(0, row);
        label = new JLabel(fieldTitle.getTitle()+ ":", JLabel.LEFT);
        labels.add(label);
        add(label, gbc);
        gbc = createGbc(1, row);
        final JTextField startEndText = new JTextField();
        if(dataset == null){
            startEndText.setText("0-100");
        } 
        else{
            startEndText.setText((String)attributesCombo.getSelectedItem());
            String selectedColumn = (String)attributesCombo.getSelectedItem();

//            System.out.println(selectedColumn);
            Pair p = findMin(selectedColumn);
            startEndText.setText(p.min + "-" + p.max);
            attributesCombo.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED ) {
                        //change items in nodes type combobox
                        if(dataset!=null){
                            String selectedColumn = (String)attributesCombo.getSelectedItem();
                            Pair p = findMin(selectedColumn);
                            startEndText.setText(p.min + "-" + p.max);
                        }
                    }            
                }
            });
        }
//        startEndText.setText("0-100");

        add(startEndText, gbc);
        fieldMap.put(fieldTitle, startEndText);
        row++;
        
        //Initialize step TextField
        fieldTitle = RangeField.values()[position++];
        gbc = createGbc(0, row);
        label = new JLabel(fieldTitle.getTitle()+ ":", JLabel.LEFT);
        labels.add(label);
        add(label, gbc);
        gbc = createGbc(1, row);
        JTextField stepText = new JTextField();
        stepText.setText("1000");
        add(stepText, gbc);
        fieldMap.put(fieldTitle, stepText);
        row++;
        
        //initialize fanout 
        fieldTitle = RangeField.values()[position++];
        gbc = createGbc(0, row);
        label = new JLabel(fieldTitle.getTitle()+ ":", JLabel.LEFT);
        labels.add(label);
        add(label, gbc);
        gbc = createGbc(1, row);
        JTextField fanoutText = new JTextField();
        fanoutText.setText("10");
        add(fanoutText, gbc);
        fieldMap.put(fieldTitle, fanoutText);
        row++;
        
        //initialize +/- fanout
        fieldTitle = RangeField.values()[position++];
        gbc = createGbc(0, row);
        label = new JLabel(fieldTitle.getTitle()+ " (<fanout/2):", JLabel.LEFT);
        labels.add(label);
        add(label, gbc);
        gbc = createGbc(1, row);
        JTextField plusMinusFanoutText = new JTextField();
        plusMinusFanoutText.setText("0");
        add(plusMinusFanoutText, gbc);
        fieldMap.put(fieldTitle, plusMinusFanoutText);
        
        /*
        //fanout group Radio Buttons
        fieldTitle = RangeField.values()[position++];
        gbc = createGbc(1, ++row);
        JRadioButton exactBtn = new JRadioButton("exact");
        JRadioButton maxBtn = new JRadioButton("max");
        ButtonGroup group = new ButtonGroup();
        group.add(exactBtn);
        group.add(maxBtn);
        exactBtn.setSelected(true);
        add(exactBtn, gbc);
        fieldMap.put(fieldTitle, exactBtn);
        gbc = createGbc(1, ++row);
        fieldTitle = RangeField.values()[position++];
        add(maxBtn, gbc);
        fieldMap.put(fieldTitle, maxBtn);
                */
    }
    
    private void initHierarchyTypeCombo(){
       gbc = createGbc(0, row);
       add(new JLabel("Type:", JLabel.LEFT), gbc);
       gbc = createGbc(1, row);
       String[] hierarchyTypes = { "ranges" , "distinct" };
       hierarchyTypesCombo = new JComboBox(hierarchyTypes);

       add(hierarchyTypesCombo, gbc);    
       hierarchyType = (String)hierarchyTypesCombo.getSelectedItem();
       hierarchyTypesCombo.setSelectedIndex(0);

       hierarchyTypesCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    hierarchyTypesComboActionPerformed();
                }            
            }
        });
        row++;
        initPanelRange();
        
    }
   
    private void hierarchyTypesComboActionPerformed() {                                           
        //System.out.println("called");
        for(Object field : fieldMap.keySet()){
            remove(fieldMap.get(field));
            row = 1;
        }
        for(JLabel l : labels){
            remove(l);
        }
        revalidate();
        repaint();
        hierarchyType = (String)hierarchyTypesCombo.getSelectedItem();
        if(hierarchyType.equals("distinct")){
            initPanelDistinct();
        }else{
            initPanelRange();
        }
        //System.out.println(hierarchyType);
    }   
    

    
    public String getHierarchyType(){
        return this.hierarchyType;
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

   public String getFieldText(Object fieldTitle) {
        String value = null;
        if(fieldMap.get(fieldTitle) instanceof JTextField)
             value = ((JTextField)fieldMap.get(fieldTitle)).getText();
        else if(fieldMap.get(fieldTitle) instanceof JComboBox){
            value = (String)((JComboBox)fieldMap.get(fieldTitle)).getSelectedItem();
        }
        else if(fieldMap.get(fieldTitle) instanceof JRadioButton){
            if(((JRadioButton)fieldMap.get(fieldTitle)).isSelected()){
                value = "selected";
            }
            else 
                value = "unselected";
        }
        return value;
   }

   private String[] getColumnsByType(String type){
       ArrayList<String> results = new ArrayList<>();

       Map<Integer, String> columnTypes = dataset.getColumnsTypes();
       for(Integer columnId : columnTypes.keySet()){
           if(columnTypes.get(columnId).equals(type)){
               results.add(dataset.getColumnsPosition().get(columnId));
           }
       }
       
       String[] res = new String[results.size()];
       for(int i=0; i<results.size(); i++){
           res[i] = results.get(i);
       }
       return res;
   }
   
    private String[] getSortingTypes(String nodesType){
        String[] numericSortingTypes = {"numeric", "alphabetical", "random"};
        String[] stringSortingTypes = {"alphabetical", "random"};

        if(nodesType.equals("int") || nodesType.equals("double")){

            return numericSortingTypes;
        }
        return stringSortingTypes;
    }
   
    private Pair findMin(String selectedColumn) {
        Integer columnIndex = dataset.getColumnByName(selectedColumn);
        double[][] data = dataset.getData();
        double min = data[0][columnIndex];
        double max = data[0][columnIndex];

        for(int i=0; i<data.length; i++){
//            System.out.println(data[i][columnIndex]);
            if(data[i][columnIndex] > max){
                max = data[i][columnIndex];
            } 
            else if(data[i][columnIndex] < min ){
                min = data[i][columnIndex];
            }
        }
//        System.out.println(min +  " " + max);
        
        return (new Pair(min, max));
    }
    
    private class Pair{
        public double min; 
        public double max; 
        
        public Pair(double _min, double _max){
            min = _min; 
            max = _max;
        }
        
    }
}