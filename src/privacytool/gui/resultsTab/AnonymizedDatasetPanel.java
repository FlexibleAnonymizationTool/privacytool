/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package privacytool.gui.resultsTab;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import privacytool.framework.algorithms.flash.LatticeNode;
import privacytool.framework.algorithms.incognito.GeneralizationNodeElement;
import privacytool.framework.anonymizationrules.AnonymizationRules;
import privacytool.framework.data.Data;
import privacytool.framework.dictionary.Dictionary;
import privacytool.framework.hierarchy.Hierarchy;
import privacytool.gui.ErrorWindow;

/**
 *
 * @author serafeim
 */
public class AnonymizedDatasetPanel extends javax.swing.JPanel {

    /**
     * Creates new form InputData
     */
    private Data dataset = null;
    private LatticeNode solutionNode = null;
    private Map<Integer, Hierarchy> quasiIdentifiers = null;
    private int[] qids = null;
    private boolean anonymizedTableRendered = false;
    
    public AnonymizedDatasetPanel() {
        initComponents();
        jScrollPane1.setVisible(false);
        jScrollPane2.setVisible(false);
        adjustScrolling();
    }
    
    public void renderInitialTable(){
        double [][]dataSet = this.dataset.getData();
        Map <Integer,String>colNamesType = null;
        Map <Integer,String>colNamesPosition = null;
        Map <Integer,Dictionary> dictionary = null;
        
        DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(){

            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        
        initialTable.setModel(tableModel);
        jScrollPane1.setViewportView(initialTable);
        
        colNamesType = dataset.getColumnsTypes();
        colNamesPosition = dataset.getColumnsPosition();
        dictionary = dataset.getDictionary();
 
        
        tableModel.addColumn("line#");
        for(Integer key: colNamesType.keySet()){
            tableModel.addColumn(colNamesPosition.get(key));
        }
        
                
        Integer line = 0;
        //create rows of table
        int i = 0;
        int j = 0;
        for (i = 0 ; i < dataSet.length ; i++){
            Object []row = new Object[colNamesType.size()+1]; 
            row[0] = line++;
            for ( j = 0 ;  j < dataSet[i].length ; j ++ ){
                if(colNamesType.get(j).contains("int")){
                    row[j+1] = Integer.toString((int)dataSet[i][j]);
                }
                else if(colNamesType.get(j).contains("double")){
                    row[j+1] = Double.toString((double) dataSet[i][j]);
                }
                else{
                    row[j+1] = dictionary.get(j).getIdToString((int)dataSet[i][j]);
                }

            }
            tableModel.addRow(row);
            row = null;
        } 
        
        jScrollPane1.setVisible(true);
    }

    public void renderAnonymizedTable(){
        System.out.println("Solution Selected : " + this.solutionNode);
        
        double [][]dataSet = this.dataset.getData();
        Map <Integer,String> colNamesType = null;
        Map <Integer,String> colNamesPosition = null;
        Map <Integer,Dictionary> dictionaries = null;
        Object columnName = null;
        Object[] columnData = null;
        
        DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(){

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        anonymizedTable.setModel(tableModel);
        jScrollPane2.setViewportView(anonymizedTable);
        
        colNamesType = dataset.getColumnsTypes();
        colNamesPosition = dataset.getColumnsPosition();
        dictionaries = dataset.getDictionary();
 
        //compute data of first column with line numbers
        columnName = "line#";
        System.out.println(dataSet.length);
        columnData = new Object[dataSet.length];
        for(int i=0; i<dataSet.length; i++){
            columnData[i] = i;
        }
        tableModel.addColumn(columnName, columnData);
        
//        Iterator<GeneralizationNodeElement> it = Arrays.asList(solutionNode.getNodeElements()).iterator();
        int[] transformation = this.solutionNode.getTransformation();        
        int count = 0;
        
        //compute data of columns
        for(int column=0; column<dataSet[0].length; column++){
            
            columnName = colNamesPosition.get(column);
            boolean anonymizeColumn = false;
            Hierarchy hierarchy = null;
            int level = 0;
            
            if((count < qids.length) && (qids[count] == column)){
                anonymizeColumn = true;
                hierarchy = quasiIdentifiers.get(column);
                level = transformation[count];
                count++;
            }
            
            
            if(colNamesType.get(column).contains("int")){
                for(int line=0; line<dataSet.length; line++){
                    columnData[line] = dataSet[line][column];
                    if(anonymizeColumn && level > 0){
                        columnData[line] = anonymizeValue(columnData[line], hierarchy, level);
                    }
                    columnData[line] = ((Double)columnData[line]).intValue();
                }
            }
            else if(colNamesType.get(column).contains("double")){
                for(int line=0; line<dataSet.length; line++){
                    columnData[line] = (double)dataSet[line][column];
                    if(anonymizeColumn && level > 0){
                        columnData[line] = anonymizeValue(columnData[line], hierarchy, level);
                    }
                }
            }
            else{
                Dictionary dictionary = dictionaries.get(column);
                for(int line=0; line<dataSet.length; line++){
                    columnData[line] = dictionary.getIdToString((int)dataSet[line][column]);
                    if(anonymizeColumn && level > 0){
                        columnData[line] = anonymizeValue(columnData[line], hierarchy, level);
                    }
                }
            }
            
            tableModel.addColumn(columnName, columnData);
        }

        this.anonymizedTableRendered = true;
        jScrollPane2.setVisible(true);
    }
        
    public void exportAnonymizedDataset(String file){
        
        try {
            try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
                TableModel model =  anonymizedTable.getModel();
                int columnCount = model.getColumnCount();
                
                //write column names
                for(int column = 1; column < columnCount; column++){
                    writer.print(model.getColumnName(column));
                    if(column != columnCount-1){
                        writer.print(",");
                    }
                }
                writer.println();
                
                //write table data
                for (int row = 0; row < model.getRowCount(); row++){
                    for(int column = 1; column < columnCount; column++){
                        writer.print(model.getValueAt(row, column));
                        if(column != columnCount-1){
                            writer.print(",");
                        }
                    }
                    writer.println();
                }
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(AnonymizedDatasetPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Object anonymizeValue(Object value, Hierarchy h, int level){
        Object anonymizedValue = value;
        
        for(int i=0; i<level; i++){
            if(h.getHierarchyType().equals("range") && i==0){
                anonymizedValue = h.getParent((Double)anonymizedValue);
            }
            else{
                anonymizedValue = h.getParent(anonymizedValue);
            }
        }
        
        return anonymizedValue;
    }
    
    public void exportAnonymizationRules(String file){
        AnonymizationRules rules = new AnonymizationRules();
        
        try {
            try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
                writer.println("Anonymization rules:");
                for(Integer qid : this.quasiIdentifiers.keySet()){

                    //create rules for the column
                    int column = qid + 1;
                    String columnName = initialTable.getColumnName(column);

                    Map<String, String> map = rules.createRules(column, this.initialTable.getModel(), 
                            this.anonymizedTable.getModel());

                    //write rules to file
                    writer.println(columnName);
                    
                    for (Map.Entry<String, String> entry : map.entrySet()){
                        writer.print(entry.getKey() + "," + entry.getValue() + "\n");
                    }
                    writer.println();
                }
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(AnonymizedDatasetPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void anonymizeWithImportedRules(Map<String, Map<String, String>> rules) {
        
        //set quasi-ids
        for(String columnName : rules.keySet()){
            int columnNumber = this.dataset.getColumnByName(columnName);
            this.quasiIdentifiers = new HashMap<>();
            this.quasiIdentifiers.put(columnNumber, null);
        }
        
        
        TableModel model = this.initialTable.getModel();
        
        DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(){

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        //iterate columns
        for(int column=0; column<model.getColumnCount(); column++){
            String columnName = model.getColumnName(column);
            String[] columnData = new String[model.getRowCount()];
            
            //if rules are provided for that column
            if(rules.containsKey(columnName)){
                Map<String, String> rulesForColumn = rules.get(columnName);
                for(int row=0; row<model.getRowCount(); row++){
                    String initValue = this.initialTable.getValueAt(row, column).toString();
                    String anonymizedValue = rulesForColumn.get(initValue);
                    if(anonymizedValue == null){
                        ErrorWindow.showErrorWindow(initValue + " value is not present in rules loaded");
                        return;
                    }
                    columnData[row] = anonymizedValue;
                }
            }
            else{
                for(int row=0; row<model.getRowCount(); row++){
                    columnData[row] = this.initialTable.getValueAt(row, column).toString();
                }
            }
            
            tableModel.addColumn(columnName, columnData);
        }
        
        this.anonymizedTable.setModel(tableModel);
        this.anonymizedTableRendered = true;
        jScrollPane2.setVisible(true);
    }
    
    public void setDataset(Data dataset){
        this.dataset = dataset;
    }
    
    public void setSolution(LatticeNode selectedNode) {
        this.solutionNode = selectedNode;
    }
    
    private void adjustScrolling(){
        this.initialTable.setSelectionModel(anonymizedTable.getSelectionModel());
        this.jScrollPane1.getVerticalScrollBar().setModel(this.jScrollPane2.getVerticalScrollBar().getModel());
    }
    
    public void setQuasiIdentifiers(Map<Integer, Hierarchy> quasiIdentifiers) {
        this.quasiIdentifiers = quasiIdentifiers;
        
        //set columns - quasi-ids to table
        qids = new int[this.quasiIdentifiers.keySet().size()];
        int i = 0;
        for(Integer column : this.quasiIdentifiers.keySet()){
            qids[i] = column;
            i++;
        }
        System.out.println(Arrays.toString(qids));
    }

    public boolean isAnonymizedTableRendered() {
        return anonymizedTableRendered;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        initialTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        anonymizedTable = new javax.swing.JTable();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Anonymized Dataset"));
        setPreferredSize(new java.awt.Dimension(800, 646));
        setLayout(new java.awt.GridLayout());

        initialTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(initialTable);

        add(jScrollPane1);

        anonymizedTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(anonymizedTable);

        add(jScrollPane2);

        getAccessibleContext().setAccessibleName("AnonymizedDataset");
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable anonymizedTable;
    private javax.swing.JTable initialTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables




}
