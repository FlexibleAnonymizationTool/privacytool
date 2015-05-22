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
package privacytool.gui.transformationTab;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import privacytool.framework.data.Data;
import privacytool.framework.data.RDFData;
import privacytool.framework.data.SETData;
import privacytool.framework.data.TXTData;
import privacytool.framework.dictionary.Dictionary;
import privacytool.framework.hierarchy.Hierarchy;
import privacytool.gui.ErrorWindow;

/**
 *
 * @author serafeim
 */
public class InputDataPanel extends javax.swing.JPanel {
    
    /**
     * Creates new form InputData
     */
    private Data data = null;
    Map<ItemListener, Integer> listeners = new HashMap<>();
    Map<Integer, Hierarchy> quasiIdentifiers = new TreeMap<>();
    
    
    public InputDataPanel() {
        initComponents();
        jScrollPane2.setVisible(false);
    }
    
    public void initTable(File file, String del) throws IOException{
        jScrollPane2.setVisible(true);
        String inputFile = null;
        double [][]dataSet = null;
        Map <Integer,String>colNamesType = null;
        Map <Integer,String>colNamesPosition = null;
        Map <Integer,Dictionary> dictionary = null;
        Dictionary tempDict = null;
        FileInputStream fstream = null;
        DataInputStream in = null;
        BufferedReader br = null;
        String strLine = null;
        String delimeter = null;
        
        //create a table with auto-resize columns
        JTable table = new JTable(){
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.jTable2 = table;
        
        if ( del == null ){
            delimeter = ",";
        }
        else{
            delimeter = del;
        }
        
        DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(){
            
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        
        jTable2.setModel(tableModel);
        jScrollPane2.setViewportView(jTable2);
        
        inputFile = file.getAbsolutePath();
        
        fstream = new FileInputStream(inputFile);
        in = new DataInputStream(fstream);
        br = new BufferedReader(new InputStreamReader(in));
        
        //find file extension
        String extension = "";
        
        int k = inputFile.lastIndexOf('.');
        if (k > 0) {
            extension = inputFile.substring(k+1);
        }
        
        if(extension.equals("nt")){     //RDF file
            
            //find RDF classes in file
            Set<String> classes = RDFData.findClasses(inputFile);
            if(classes.isEmpty()){
                ErrorWindow.showErrorWindow("No RDF Classes found in file");
                return;
            }
            
            //ask the user to choose main class
            String mainClass = RDFData.showChooseClassDialog(classes);
            if(mainClass == null){
                return;
            }
            data = new RDFData(inputFile, mainClass);
        }
        else{
            while ((strLine = br.readLine()) != null){
                if ( strLine.contains(delimeter)){
                    data = new TXTData(inputFile,delimeter);
                }
                else{
                    if ((strLine = br.readLine()) != null){
                        if ( strLine.contains(delimeter)){
                            data = new SETData(inputFile,delimeter);
                        }
                        else{
                            data = new TXTData(inputFile,delimeter);
                        }
                    }
                }
                break;
            }
        }
        
        br.close();
        
        data.readDataset();
        
        dataSet = data.getData();
        
        colNamesType = data.getColumnsTypes();
        colNamesPosition = data.getColumnsPosition();
        dictionary = data.getDictionary();
        
        tableModel.addColumn("line#");
        for(Integer key: colNamesType.keySet()){
            tableModel.addColumn(colNamesPosition.get(key));
        }
        
        TableColumnModel model = jTable2.getColumnModel();
        
        //set custom headerRender for column headers
        for(Integer i: colNamesType.keySet()){
            TableColumn col = model.getColumn(i+1);
            
            //set item listener for every header checkbox
            ItemListener il = new ItemListener() {
                
                @Override
                public void itemStateChanged(ItemEvent e) {
//                    System.out.println("Changed " + listeners.get(this));
                    
                    Integer column = listeners.get(this);
                    if(quasiIdentifiers.containsKey(column)){
                        quasiIdentifiers.remove(column);
                    } else {
                        quasiIdentifiers.put(column, null);
                    }
                    
//                    System.out.println(quasiIdentifiers);
                }
            };
            listeners.put(il, i);
            col.setHeaderRenderer(new CustomHeaderCell(il));
        }
        
        Integer line = 0;
        //create rows of table
        int i = 0;
        int j = 0;
        
        if (!data.getClass().getSimpleName().contains("SET")){
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
        }
        else{
            for (i = 0 ; i < dataSet.length ; i++){
                Object []row = new Object[dataSet[i].length+1];
                row[0] = line++;
                boolean FLAG = false;
                for ( j = 0 ;  j < dataSet[i].length ; j ++ ){
                    if ( FLAG == false){
                        row[1] = dictionary.get(0).getIdToString((int)dataSet[i][j]);
                        FLAG = true;
                    }
                    else{
                        row[1] = row[1] + "," + dictionary.get(0).getIdToString((int)dataSet[i][j]);
                    }
                }
                tableModel.addRow(row);
                row = null;
            }
        }
        
//        adjustColumnWidths();
    }
    
    private void adjustColumnWidths(){
        jTable2.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
        for (int column = 0; column < jTable2.getColumnCount(); column++)
        {
            TableColumn tableColumn = jTable2.getColumnModel().getColumn(column);
            int preferredWidth = tableColumn.getMinWidth();
            int maxWidth = tableColumn.getMaxWidth();
            
            for (int row = 0; row < jTable2.getRowCount(); row++)
            {
                TableCellRenderer cellRenderer = jTable2.getCellRenderer(row, column);
                Component c = jTable2.prepareRenderer(cellRenderer, row, column);
                int width = c.getPreferredSize().width + jTable2.getIntercellSpacing().width;
                preferredWidth = Math.max(preferredWidth, width);
                
                //  We've exceeded the maximum width, no need to check other rows
                
                if (preferredWidth >= maxWidth)
                {
                    preferredWidth = maxWidth;
                    break;
                }
            }
            
            tableColumn.setPreferredWidth( preferredWidth );
        }
    }
    
    public Data getData(){
        return this.data;
    }
    
    public Map<Integer, Hierarchy> getQuasiIdentifiers() {
        return quasiIdentifiers;
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Input Data"));
        setPreferredSize(new java.awt.Dimension(800, 646));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 764, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
    
    class CustomHeaderCell extends JCheckBox implements TableCellRenderer, MouseListener {
        
        /**
         * the Renderer Component.
         * @see #getTableCellRendererComponent(JTable table, Object value,
         *             boolean isSelected, boolean hasFocus, int row, int column)
         */
        protected CustomHeaderCell rendererComponent;
        
        /** To which (JTable-) columns does this Checkbox belong ? */
        protected int column;
        
        /**
         * remembers, if mousePressed() was called before.
         * Workaround, because dozens of mouseevents occurs after one
         * mouseclick.
         */
        protected boolean mousePressed = false;
        
        /**
         * @param itemListener will be notified when Checkbox will be
         *
         * checked/unchecked
         */
        public CustomHeaderCell(ItemListener itemListener) {
            
            rendererComponent = this;
            rendererComponent.addItemListener(itemListener);
            
        }
        
        /** @return this */
        //pasted from javax.swing.table.TableColumn.createDefaultHeaderRenderer()
        //with some slight modifications.
        //implements TableCellRenderer
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            if (table != null) {
                JTableHeader header = table.getTableHeader();
                if (header != null) {
                    rendererComponent.setForeground(header.getForeground());
                    rendererComponent.setBackground(header.getBackground());
                    rendererComponent.setFont(header.getFont());
                    
                    // We only need one listener on the header
                    // this adds another every time this renderer
                    // is fetched to render a column header which
                    // can eventually lead to StackOverflowError.
                    header.addMouseListener(rendererComponent);
                }
            }
            
            setColumn(column);
            rendererComponent.setText((value == null) ? "" : value.toString());
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            
            return rendererComponent;
        }
        
        /** @param column to which the CheckBox belongs to */
        protected void setColumn(int column) {
            this.column = column;
        }
        
        /** @return the column to which the CheckBox belongs to */
        public int getColumn() {
            return column;
        }
        
        /**************** Implementation of MouseListener ******************/
        
        /**
         * Calls doClick(), because the CheckBox doesn't receive any
         * mouseevents itself. (because it is in a CellRendererPane).
         * The way to get the JCheckBox to work is to create and
         * install an editor that looks, ie, is configured just like
         * this renderer.
         */
        protected void handleClickEvent(MouseEvent e) {
            // Workaround: dozens of mouseevents occur for only one mouse click.
            // First MousePressedEvents, then MouseReleasedEvents, (then
            // MouseClickedEvents).
            // The boolean flag 'mousePressed' is set to make sure
            // that the action is performed only once.
            if (mousePressed) {
                mousePressed=false;
                
                JTableHeader header = (JTableHeader)(e.getSource());
                JTable tableView = header.getTable();
                TableColumnModel columnModel = tableView.getColumnModel();
                
                int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                int column = tableView.convertColumnIndexToModel(viewColumn);
                
                if (viewColumn == this.column && e.getClickCount() == 1 && column != -1) {
                    doClick();
                }
            }
        }
        
        @Override
        public void mouseClicked(MouseEvent e) {
            handleClickEvent(e);
            ((JTableHeader)e.getSource()).repaint();
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            mousePressed = true;
            
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            
        }
        
        @Override
        public void mouseEntered(MouseEvent e) {
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
        }
        
        
    }
    
    
}
